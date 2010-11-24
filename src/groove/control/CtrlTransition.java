/* * GROOVE: GRaphs for Object Oriented VErification *  * Copyright 2003--2007 University of Twente *  *  *  * Licensed under the Apache License, Version 2.0 (the "License"); *  * you may not use this file except in compliance with the License. *  * You may obtain a copy of the License at *  * http://www.apache.org/licenses/LICENSE-2.0 *  *  *  * Unless required by applicable law or agreed to in writing, *  * software distributed under the License is distributed on an *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, *  * either express or implied. See the License for the specific *  * language governing permissions and limitations under the License. *  *  *  * $Id: ControlTransition.java,v 1.10 2008-01-30 11:13:57 fladder Exp $ */package groove.control;import groove.graph.AbstractEdge;import java.util.HashMap;import java.util.List;import java.util.Map;import java.util.Set;/** * Represents a transition in a control automaton. * Control transitions have pairs of guards and rule calls as labels. * A rule call is a rule with a sequence of input and output parameters. * A guard is a failure set, i.e., a set of rules that cannot be performed. * A transition is <i>virtual</i> if the rule names in the call and guard * are only given as strings, and <i>actual</i> if they are instantiated rules.  * @author Arend Rensink */public class CtrlTransition extends AbstractEdge<CtrlState,CtrlLabel,CtrlState> {    /**     * Creates a new control transition between two control states.     */    public CtrlTransition(CtrlState source, CtrlLabel label, CtrlState target) {        super(source, label, target);        this.inVars = new HashMap<CtrlVar,Integer>();        this.outVars = new HashMap<CtrlVar,Integer>();        List<CtrlPar> args = label.getCall().getArgs();        if (args != null) {            for (int i = 0; i < args.size(); i++) {                CtrlPar arg = args.get(i);                if (arg instanceof CtrlPar.Var) {                    CtrlVar var = ((CtrlPar.Var) arg).getVar();                    if (arg.isInOnly()) {                        this.inVars.put(var, i);                    } else {                        this.outVars.put(var, i);                    }                }            }        }    }    /**      * Returns a list of indices corresponding to the bound variables in the target state.     * Indices smaller than the number of bound variables in the source state refer to     * source state variables, higher indices refer to transition out-parameters.      */    public int[] getTargetVarBinding() {        if (this.targetVarBinding == null) {            this.targetVarBinding = computeTargetVarBinding();        }        return this.targetVarBinding;    }    /** Computes the binding of bound target variables to bound source     * variables and transition parameters.     * @see #getTargetVarBinding()     */    private int[] computeTargetVarBinding() {        List<CtrlVar> targetVars = target().getBoundVars();        List<CtrlVar> sourceVars = source().getBoundVars();        int sourceVarCount = sourceVars.size();        int[] result = new int[targetVars.size()];        for (int i = 0; i < targetVars.size(); i++) {            CtrlVar targetVar = targetVars.get(i);            int index = sourceVars.indexOf(targetVar);            if (index < 0) {                index = this.outVars.get(targetVar) + sourceVarCount;            }            result[i] = index;        }        return result;    }    /** Binding of bound target variables to bound source variables and transition parameters. */    private int[] targetVarBinding;    /**      * Returns a list of indices corresponding to the transition in-parameters.     * Indices refer to bound source variables.     */    public int[] getInVarBinding() {        if (this.inVarBinding == null) {            this.inVarBinding = computeInVarBinding();        }        return this.inVarBinding;    }    /** Computes the binding of transition in-parameters to bound source variables.     * @see #getTargetVarBinding()     */    private int[] computeInVarBinding() {        List<CtrlVar> sourceVars = source().getBoundVars();        int[] result = new int[this.inVars.size()];        for (Map.Entry<CtrlVar,Integer> inVarEntry : this.inVars.entrySet()) {            int index = sourceVars.indexOf(inVarEntry.getKey());            assert index >= 0;            result[inVarEntry.getValue()] = index;        }        return result;    }    /** Binding of transition in-parameters to bound source variables. */    private int[] inVarBinding;    /** Returns the set of variables used as input parameters in this transition. */    public Set<CtrlVar> getInVars() {        return this.inVars.keySet();    }    /** Returns the set of variables used as output parameters in this transition. */    public Set<CtrlVar> getOutVars() {        return this.outVars.keySet();    }    /** Set of variables used as input parameters. */    private final Map<CtrlVar,Integer> inVars;    /** Set of variables used as output parameters. */    private final Map<CtrlVar,Integer> outVars;}