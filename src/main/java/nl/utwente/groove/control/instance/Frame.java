/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package nl.utwente.groove.control.instance;

import static nl.utwente.groove.util.LazyFactory.lazyFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.Call;
import nl.utwente.groove.control.CtrlVar;
import nl.utwente.groove.control.NestedCall;
import nl.utwente.groove.control.Position;
import nl.utwente.groove.control.template.Location;
import nl.utwente.groove.control.template.NestedSwitch;
import nl.utwente.groove.control.template.Switch;
import nl.utwente.groove.control.template.SwitchAttempt;
import nl.utwente.groove.grammar.Callable.Kind;
import nl.utwente.groove.grammar.CheckPolicy;
import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.util.DefaultFixable;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Fixable;
import nl.utwente.groove.util.LazyFactory;

/**
 * Run-time composed control location.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class Frame implements Position<Frame,Step>, Fixable {
    /** Constructs a new frame.
     * @param ctrl the control automaton being built
     * @param loc top template location of the frame
     * @param swt stack of switches forming the context of this frame
     * @param pred predecessor in a verdict transition; if {@code null}, this is
     * a prime frame
     */
    Frame(Automaton ctrl, Location loc, NestedSwitch swt, @Nullable Frame pred) {
        this.aut = ctrl;
        this.nr = ctrl.getFrames().size();
        List<CallStackChange> pops = new ArrayList<>();
        // avoid sharing
        this.pred = pred;
        if (pred == null) {
            this.prime = this;
        } else {
            this.prime = pred.getPrime();
            pops.addAll(pred.getPops());
        }
        var context = new NestedSwitch(swt);
        // look for an outer recipe with output variables
        var recipe = context
            .stream()
            .filter(s -> s.getKind() == Kind.RECIPE)
            .filter(s -> s.getCall().hasOutVars())
            .findFirst();
        // pop the call stack until we have a non-final location or empty stack
        // add pop actions if we are not a prime frame
        boolean addPops = pred != null;
        while (loc.isFinal() && !context.isEmpty()) {
            Switch done = context.pop();
            // also start adding pop actions once we pass the outer recipe call,
            // if the recipe call has out-parameters that we cannot retrieve otherwise
            addPops |= recipe.filter(s -> s == done).isPresent();
            if (addPops) {
                pops.add(done.assignFinal2Target(loc).toPop());
            }
            loc = done.onFinish();
        }
        this.pops = pops;
        this.context = context;
        this.location = loc;
    }

    /** Returns the containing control automaton. */
    public Automaton getAut() {
        return this.aut;
    }

    private final Automaton aut;

    /**
     * Returns the number of this frame.
     * After a frame has been added to the automaton,
     * the frame number uniquely identifies the frame.
     */
    public int getNumber() {
        return this.nr;
    }

    private final int nr;

    @Override
    public boolean isStart() {
        return getAut().getStart() == this;
    }

    /** Returns the contextual nested switch of this frame. */
    public NestedSwitch getContext() {
        return this.context;
    }

    private final NestedSwitch context;

    /**
     * Returns the top (non-{@code null}) control location instantiated by this frame.
     */
    public Location getLocation() {
        return this.location;
    }

    private final Location location;

    /** Indicates whether this is an absence frame. */
    public boolean isRemoved() {
        return getLocation().isRemoved();
    }

    /** Indicates whether this is an error frame. */
    public boolean isError() {
        return getLocation().isError();
    }

    /** Indicates if a given frame is on the path between the prime frame and this one.*/
    public boolean isPredecessor(Frame frame) {
        if (frame == this) {
            return true;
        }
        var pred = getPred();
        if (pred == null) {
            return false;
        }
        return pred.isPredecessor(frame);
    }

    /**
     * Returns the predecessor frame in the chain between the
     * prime frame and this, or {@code null} if this is a prime frame.
     */
    private @Nullable Frame getPred() {
        return this.pred;
    }

    /** The predecessor frame, or {@code null} if this is a prime frame. */
    private final @Nullable Frame pred;

    /**
     * Returns the prime frame of this frame.
     * The prime frame is the initial frame from which this one was
     * reached after a sequence of verdicts.
     */
    public Frame getPrime() {
        return this.prime;
    }

    /** Indicates if this frame is its own prime. */
    public boolean isPrime() {
        return getPrime() == this;
    }

    private final Frame prime;

    /**
     * Returns the set of called actions that have been tried
     * between the prime frame and this one (inclusive).
     */
    public Set<NestedCall> getPastAttempts() {
        return this.pastAttempts.get();
    }

    private Supplier<Set<NestedCall>> pastAttempts = lazyFactory(() -> {
        var result = new HashSet<NestedCall>();
        if (!isPrime()) {
            var pred = getPred();
            assert pred != null;
            result.addAll(pred.getPastAttempts());
        }
        if (isTrial()) {
            for (Step step : getAttempt()) {
                result.add(step.getCall());
            }
        }
        return result;
    });

    /** Returns the set of rule calls that have been tried since the prime frame. */
    public Set<Call> getPastCalls() {
        return this.pastCalls.get();
    }

    private Supplier<Set<Call>> pastCalls = lazyFactory(() -> {
        var result = new HashSet<Call>();
        getPastAttempts().stream().map(NestedCall::getInner).forEach(result::add);
        return result;
    });

    /**
     * Returns the list of frame pop actions corresponding to procedure exits
     * up to (but not including) the outer recipe call, if there is one.
     */
    public List<CallStackChange> getPops() {
        return this.pops;
    }

    private final List<CallStackChange> pops;

    /** Returns an optional assignment to the output parameters of a recipe call
     * exited between the prime and this frame. The assignment should be
     * applied to the prime call stack of any state of which this is the actual frame.
     */
    public @Nullable List<CallStackChange> getRecipeOutAssign() {
        return this.recipeOutAssign.get();
    }

    private Supplier<@Nullable List<CallStackChange>> recipeOutAssign
        = LazyFactory.instance(this::computeRecipeOutAssign);

    private @Nullable List<CallStackChange> computeRecipeOutAssign() {
        List<CallStackChange> result = null;
        if (getPrime().isInternal() && !isInternal()) {
            result = new ArrayList<>();
            var exit = getLocation();
            for (var caller : getPrime().getContext().outIterable()) {
                assert exit.isFinal();
                result.add(CallStackChange.exit(exit, caller));
                if (caller.getKind() == Kind.RECIPE) {
                    break;
                } else {
                    exit = caller.onFinish();
                }
            }
        }
        return result;
    }

    /**
     * Returns the total nesting depth of the frame,
     * being the sum of the nested switch depth and number of pop actions.
     */
    public int getNestingDepth() {
        return getContext().size() + getPops().size();
    }

    @Override
    public Type getType() {
        var result = this.type;
        if (result == null) {
            this.type = result = getLocation().getType();
        }
        return result;
    }

    /** The type of this frame. */
    private @Nullable Type type;

    @Override
    public boolean isDead() {
        return getType() == Type.DEAD;
    }

    @Override
    public boolean isFinal() {
        return getType() == Type.FINAL;
    }

    @Override
    public boolean isTrial() {
        return getType() == Type.TRIAL;
    }

    @Override
    public StepAttempt getAttempt() {
        assert isFixed();
        return this.attempt.get();
    }

    private final LazyFactory<StepAttempt> attempt = LazyFactory.instance(this::computeAttempt);

    /** Computes the attempt of this frame. */
    private StepAttempt computeAttempt() {
        SwitchAttempt locAttempt = getLocation().getAttempt();
        // divide the switches of the control location
        // into constraints and "proper" calls
        List<NestedSwitch> constraintCalls = new ArrayList<>();
        List<NestedSwitch> properCalls = new ArrayList<>();
        for (NestedSwitch sw : locAttempt) {
            if (sw.getInnerCall().getRule().getRole().isConstraint()) {
                constraintCalls.add(sw);
            } else {
                properCalls.add(sw);
            }
        }
        StepAttempt result;
        if (properCalls.isEmpty()) {
            // we only have a constraint attempt
            Frame onVerdict = newFrame(locAttempt.onSuccess());
            assert onVerdict.getLocation() == locAttempt.onFailure();
            result = new StepAttempt(onVerdict);
            for (NestedSwitch sw : constraintCalls) {
                result.add(createStep(sw));
            }
        } else if (constraintCalls.isEmpty()) {
            // we only have a proper attempt
            Frame onSuccess = newFrame(locAttempt.onSuccess());
            Frame onFailure = newFrame(locAttempt.onFailure());
            result = new StepAttempt(onSuccess, onFailure);
            for (NestedSwitch sw : properCalls) {
                result.add(createStep(sw));
            }
        } else {
            // the initial attempt tests for constraints only;
            // the verdict leads to an intermediate frame
            Frame inter = newFrame(getLocation());
            result = new StepAttempt(inter);
            for (NestedSwitch sw : constraintCalls) {
                result.add(createStep(sw));
            }
            // this is followed by an attempt for the proper steps
            // which is set as the attempt of the intermediate frame
            Frame onVerdict = inter.newFrame(locAttempt.onSuccess());
            // we had a location with property switches; this guarantees
            // that the success and failure locations coincide
            assert onVerdict.getLocation() == locAttempt.onFailure();
            StepAttempt interAttempt = new StepAttempt(onVerdict, onVerdict);
            for (NestedSwitch sw : properCalls) {
                result.add(inter.createStep(sw));
            }
            inter.attempt.set(interAttempt);
        }
        return result;
    }

    /** Constructs a step from this frame, based on a given nested switch. */
    private Step createStep(NestedSwitch sw) {
        NestedSwitch targetSwitch = new NestedSwitch(getContext());
        sw.forEach(targetSwitch::push);
        Switch callSwitch = targetSwitch.pop();
        Frame onFinish = new Frame(getAut(), callSwitch.onFinish(), targetSwitch, null).normalise();
        return new Step(this, sw, onFinish);
    }

    /** Returns the successor frame, depending on a given policy value. */
    public Frame onPolicy(CheckPolicy policy) {
        Frame result = null;
        switch (policy) {
        case ERROR:
            result = onError();
            break;
        case REMOVE:
            result = onRemove();
            break;
        case SILENT:
            result = this;
            break;
        default:
            throw Exceptions.illegalArg("Policy value '%s' is illegal here", policy);
        }
        return result;
    }

    /** Returns the error frame from this frame. */
    public Frame onError() {
        var result = this.onError;
        if (result == null) {
            if (isError() || isRemoved()) {
                result = this;
            } else {
                result = newFrame(Location
                    .getSpecial(CheckPolicy.ERROR, getLocation().getTransience()));
            }
            this.onError = result;
        }
        return result;
    }

    private @Nullable Frame onError;

    /** Returns the absence frame from this frame. */
    public Frame onRemove() {
        var result = this.onRemove;
        if (result == null) {
            if (isRemoved()) {
                result = this;
            } else {
                result = newFrame(Location
                    .getSpecial(CheckPolicy.REMOVE, getLocation().getTransience()));
            }
            this.onRemove = result;
        }
        return result;
    }

    private @Nullable Frame onRemove;

    /**
     * Indicates if this frame is inside a recipe.
     * This is the case if and only if the recipe has started
     * and not yet terminated.
     * A frame can only be inside a recipe if it is transient.
     * @see #getRecipe()
     * @see #isTransient()
     */
    public boolean isInternal() {
        return getContext().inRecipe();
    }

    /**
     * Returns the outer recipe to which this frame belongs, if any.
     * @return the recipe to this this frame belongs, or {@code null}
     * if it is not inside a recipe
     * @see #isInternal()
     */
    public Optional<Recipe> getRecipe() {
        return getContext().getRecipe();
    }

    /**
     * Indicates if this frame is inside an atomic block.
     * Convenience method for <code>getTransience() > 0</code>
     */
    public boolean isTransient() {
        return getTransience() > 0;
    }

    @Override
    public int getTransience() {
        return getContext().getTransience() + getLocation().getTransience();
    }

    @Override
    public boolean hasVars() {
        return getLocation().hasVars();
    }

    @Override
    public List<CtrlVar> getVars() {
        return getLocation().getVars();
    }

    /**
     * Constructs a frame for a given control location,
     * with the same prime frame and context switch as this frame.
     */
    private Frame newFrame(Location loc) {
        Frame result = new Frame(getAut(), loc, getContext(), this);
        return result.normalise();
    }

    /** Fixes this frame and returns its canonical representative. */
    public Frame normalise() {
        setFixed();
        return getAut().addFrame(this);
    }

    @Override
    public int hashCode() {
        assert isFixed();
        final int prime = 31;
        // use identity of prime frame as it has already been normalised
        int result = (isPrime()
            ? 1237
            : System.identityHashCode(this.prime));
        result = prime * result + System.identityHashCode(this.pred);
        result = prime * result + this.location.hashCode();
        result = prime * result + this.pops.hashCode();
        result = prime * result + this.context.hashCode();
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        assert isFixed();
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Frame other)) {
            return false;
        }
        if (isPrime()
            ? !other.isPrime()
            : this.prime != other.prime) {
            return false;
        }
        if (this.pred != other.pred) {
            return false;
        }
        if (!this.pops.equals(other.pops)) {
            return false;
        }
        if (!this.location.equals(other.location)) {
            return false;
        }
        if (!this.context.equals(other.context)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String result = getIdString();
        if (RICH_LABELS) {
            if (getTransience() > 0) {
                result += ", d" + getTransience();
            }
            if (isFinal()) {
                result += ", final";
            } else if (isDead()) {
                result += ", dead";
            }
            if (isPrime()) {
                result += "\nPrime";
            } else {
                result += "\nPrime: " + getPrime().getIdString();
                if (VERY_RICH_LABELS) {
                    result += "\nTried:";
                    for (NestedCall tried : getPastAttempts()) {
                        result += " " + tried.toString();
                    }
                }
            }
            result += "\nLocation: " + getLocation();
            result += "\nCall stack: " + getContext();
        }
        return result;
    }

    /** Returns the concatenation of the call stack locations. */
    public String getIdString() {
        StringBuilder result = new StringBuilder();
        //        String callerName = null;
        //        for (Switch swit : getSwitchStack()) {
        //            if (callerName == null) {
        //                result.append("c");
        //            } else {
        //                result.append('/');
        //                result.append(callerName);
        //                result.append('.');
        //            }
        //            result.append(swit.onFinish().getNumber());
        //            callerName = swit.getCall().getUnit().getLastName();
        //        }
        //        if (callerName == null) {
        //            result.append("c");
        //        } else {
        //            result.append('/');
        //            result.append(callerName);
        //            result.append('.');
        //        }
        result.append("c");
        result.append(getNumber());
        return result.toString();
    }

    @Override
    public boolean setFixed() {
        return this.fixable.setFixed();
    }

    @Override
    public boolean isFixed() {
        return this.fixable.isFixed();
    }

    private final DefaultFixable fixable = new DefaultFixable();

    private final static boolean RICH_LABELS = true;
    private final static boolean VERY_RICH_LABELS = false;
}
