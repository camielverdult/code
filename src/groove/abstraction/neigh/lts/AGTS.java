/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.abstraction.neigh.lts;

import groove.abstraction.neigh.MyHashMap;
import groove.abstraction.neigh.MyHashSet;
import groove.abstraction.neigh.Parameters;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.iso.ShapeIsoChecker;
import groove.graph.GraphCache;
import groove.graph.TypeEdge;
import groove.graph.TypeLabel;
import groove.lts.DerivationLabel;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.GraphGrammar;
import groove.trans.HostGraph;
import groove.util.FilterIterator;
import groove.util.TreeHashSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The graph transition system for abstract exploration. All states of this
 * GTS are of type ShapeState or ShapeNextState.
 * 
 * @author Eduardo Zambon
 */
public final class AGTS extends GTS {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** Number of states marked as subsumed. */
    private int subsumedStatesCount;
    /** Number of transitions marked as subsumed. */
    private int subsumedTransitionsCount;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Constructs the GTS object for the given grammar. */
    public AGTS(GraphGrammar grammar) {
        super(grammar);
        this.subsumedStatesCount = 0;
        this.subsumedTransitionsCount = 0;
        this.getRecord().setReuseEvents(false);
        this.getRecord().setCheckIso(true);
        this.storeAbsLabels();
    }

    /** Private constructor used to build the reduces state space. */
    private AGTS(AGTS agts) {
        super(agts.getGrammar());
        this.subsumedStatesCount = 0;
        this.subsumedTransitionsCount = 0;
        this.getRecord().setReuseEvents(false);
        this.getRecord().setCheckIso(false);
        this.getRecord().setCollapse(false);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    /**
     * Adds the given state to the abstract GTS. 
     * The given state must be of type ShapeState or ShapeNextState.
     * While trying to add the new state, subsumption is computed in both
     * directions. If the state is fresh, this method goes over the subsumed
     * states already stored and tries to update the subsumption relation.
     */
    @Override
    public ShapeState addState(GraphState newGState) {
        assert newGState instanceof ShapeState : "Type error : " + newGState
            + " is not of type ShapeState.";
        ShapeState newState = (ShapeState) newGState;
        ShapeState result = (ShapeState) super.addState(newState);
        if (result == null) {
            // There is no state in the transition system that subsumes the
            // new state. Maybe the new state subsumes some states that are
            // already in the GTS.
            this.subsumedStatesCount += newState.markSubsumedStates();
        } else if (newState.isSubsumed()) {
            // The state will produce only a transition.
            this.subsumedTransitionsCount++;
        }
        return result;
    }

    /**
     * Delegates to super.
     * The given transition must be of type ShapeTransition or ShapeNextState.
     */
    @Override
    public void addTransition(GraphTransition transition) {
        assert (transition instanceof ShapeTransition)
            || (transition instanceof ShapeNextState) : "Type error : "
            + transition + " is not of type ShapeTransition or ShapeNextState.";
        super.addTransition(transition);
    }

    @Override
    protected TreeHashSet<GraphState> createStateSet() {
        return new ShapeStateSet(getCollapse());
    }

    @Override
    public Iterator<GraphState> getOpenStateIter() {
        return new FilterIterator<GraphState>(nodeSet().iterator()) {
            @Override
            protected boolean approves(Object obj) {
                ShapeState state = (ShapeState) obj;
                return !state.isClosed() && !state.isSubsumed();
            }
        };
    }

    /** Throws an UnsupportedOperationException. */
    @Override
    protected GraphCache<GraphState,GraphTransition> createCache() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Shape createStartGraph(HostGraph startGraph) {
        Shape result = Shape.createShape(startGraph);
        return result;
    }

    @Override
    protected ShapeState createStartState(HostGraph startGraph) {
        ShapeState result =
            new ShapeState((Shape) startGraph,
                getGrammar().getCtrlAut().getStart(), 0);
        return result;
    }

    @Override
    public boolean checkDiamonds() {
        return false;
    }

    @Override
    public ShapeState startState() {
        return (ShapeState) super.startState();
    }

    @Override
    public Set<ShapeState> nodeSet() {
        @SuppressWarnings("unchecked")
        Set<ShapeState> stateSet = (Set<ShapeState>) ((Set<?>) getStateSet());
        return Collections.unmodifiableSet(stateSet);
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Store the abstraction labels in the parameters, if any. */
    private void storeAbsLabels() {
        Set<TypeLabel> unaryLabels = new MyHashSet<TypeLabel>();
        for (TypeEdge typeEdge : getGrammar().getTypeGraph().edgeSet()) {
            if (!typeEdge.label().isBinary()) {
                unaryLabels.add(typeEdge.label());
            }
        }

        List<String> absLabelsStr =
            this.getGrammar().getProperties().getAbstractionLabels();
        Set<TypeLabel> absLabels = new MyHashSet<TypeLabel>();

        for (TypeLabel unaryLabel : unaryLabels) {
            if (absLabelsStr.contains(unaryLabel.text())) {
                absLabels.add(unaryLabel);
            }
        }

        Parameters.setAbsLabels(absLabels);
    }

    /** Returns the number of states marked as subsumed. */
    public int getSubsumedStatesCount() {
        return this.subsumedStatesCount;
    }

    /** Returns the number of transitions marked as subsumed. */
    public int getSubsumedTransitionsCount() {
        return this.subsumedTransitionsCount;
    }

    /** Basic getter method. */
    public int getStateCount() {
        return this.nodeCount();
    }

    /**
     * Constructs and returns the reduced state space with only non-subsumed
     * states.
     */
    public AGTS reduceGTS() {
        AGTS result = new AGTS(this);
        Map<ShapeState,ShapeState> closureMap =
            new MyHashMap<ShapeState,ShapeState>();
        ArrayList<ShapeState> ancestors = new ArrayList<ShapeState>();

        // Go over all states and compute the closure for the subsumption
        // relation.
        for (GraphState graphState : this.getStateSet()) {
            ShapeState state = (ShapeState) graphState;
            if (!state.isSubsumed() && !closureMap.containsKey(state)) {
                // This state will be in the reduced GTS.
                closureMap.put(state, state);
            } else { // The state is subsumed.
                // Check if we already have a closure.
                ShapeState closure = closureMap.get(state);
                if (closure == null) {
                    // No, we don't.
                    ShapeState subsumptor = state.getSubsumptor();
                    closure = closureMap.get(subsumptor);
                    ancestors.clear();
                    ancestors.add(state);
                    while (closure == null) {
                        // We need to search.
                        if (subsumptor.isSubsumed()) {
                            ancestors.add(subsumptor);
                            subsumptor = subsumptor.getSubsumptor();
                            closure = closureMap.get(subsumptor);
                        } else {
                            // The current subsumptor is the closure.
                            closure = subsumptor;
                            ancestors.add(closure);
                        }
                    }
                    // Now we have a closure, go over all ancestors that we
                    // visited and update the closure map.
                    for (ShapeState ancestor : ancestors) {
                        assert !closureMap.containsKey(ancestor);
                        closureMap.put(ancestor, closure);
                    }
                } // else closure != null: Nothing to do.
            }
        }

        // Add the shapes to the new GTS.
        List<ShapeState> toProcess = new LinkedList<ShapeState>();
        toProcess.add(this.startState());
        Map<ShapeState,ShapeState> stateMap =
            new MyHashMap<ShapeState,ShapeState>();
        while (!toProcess.isEmpty()) {
            ShapeState origSrc = toProcess.remove(0);
            ShapeState origSrcClosure = closureMap.get(origSrc);
            assert origSrcClosure != null;
            ShapeState reducedSrc = stateMap.get(origSrcClosure);
            if (reducedSrc == null) {
                // This can only happen on the first state.
                assert !(origSrc instanceof ShapeNextState);
                reducedSrc =
                    new ShapeState(origSrcClosure.getGraph(),
                        origSrcClosure.getCtrlState(), 0);
                addReducedState(result, origSrcClosure, reducedSrc);
                stateMap.put(origSrcClosure, reducedSrc);
                result.startState = reducedSrc;
            }
            // Go over all next states.
            for (GraphTransition origTrans : origSrcClosure.getTransitionSet()) {
                ShapeState origTgt = (ShapeState) origTrans.target();
                ShapeState origTgtClosure = closureMap.get(origTgt);
                assert origTgtClosure != null;
                ShapeState reducedTgt = stateMap.get(origTgtClosure);
                if (reducedTgt == null) {
                    // The target state doesn't exist yet.
                    reducedTgt =
                        new ShapeNextState(result.nodeCount(),
                            origTgtClosure.getGraph(), reducedSrc,
                            origTrans.getEvent());
                    toProcess.add(origTgtClosure);
                    addReducedState(result, origTgtClosure, reducedTgt);
                    stateMap.put(origTgtClosure, reducedTgt);
                } else {
                    // Check if we have a new transition.
                    DerivationLabel transLabel = origTrans.label();
                    // Create a new transition.
                    if (!reducedSrc.containsTransition(transLabel, reducedTgt)) {
                        ShapeTransition reducedTrans =
                            new ShapeTransition(reducedSrc,
                                origTrans.getEvent(), reducedTgt);
                        result.addTransition(reducedTrans);
                    }
                }
            }
        }

        return result;
    }

    private void addReducedState(AGTS reducedGTS, ShapeState origState,
            ShapeState reducedState) {
        reducedGTS.addStateWithoutCheck(reducedState);
        if (reducedState instanceof ShapeNextState) {
            reducedGTS.addTransition((GraphTransition) reducedState);
        }
        reducedGTS.setClosed(reducedState, true);
        if (this.isFinal(origState)) {
            reducedGTS.setFinal(reducedState);
        }
    }

    private ShapeState addStateWithoutCheck(ShapeState state) {
        return (ShapeState) super.addState(state);
    }

    // ------------------------------------------------------------------------
    // Inner classes
    // ------------------------------------------------------------------------

    /** Class to store the states of the GTS. */
    private static final class ShapeStateSet extends GTS.StateSet {

        /** Default constructor, delegates to super class. */
        private ShapeStateSet(int collapse) {
            super(collapse, ShapeIsoChecker.getInstance(true).downcast());
        }

        /**
         * Compares the given states both for (in)equality and subsumption.
         * Bear in mind that this method has side-effects. 
         */
        @Override
        protected boolean areEqual(GraphState myState, GraphState otherState) {
            assert myState instanceof ShapeState;
            assert otherState instanceof ShapeState;
            ShapeState myShapeState = (ShapeState) myState;
            ShapeState otherShapeState = (ShapeState) otherState;
            ShapeIsoChecker checker = ShapeIsoChecker.getInstance(true);
            int comparison =
                checker.compareShapes(myShapeState.getGraph(),
                    otherShapeState.getGraph()).one();
            if (checker.isDomStrictlyLargerThanCod(comparison)) {
                // New state subsumes old one.
                myShapeState.addSubsumedState(otherShapeState);
            } else if (checker.isCodSubsumesDom(comparison)) {
                // Old state subsumes new state.
                myShapeState.setSubsumptor(otherShapeState);
            }
            return checker.areEqual(comparison);
        }
    }
}
