/* * GROOVE: GRaphs for Object Oriented VErification *  * Copyright 2003--2007 University of Twente *  *  *  * Licensed under the Apache License, Version 2.0 (the "License"); *  * you may not use this file except in compliance with the License. *  * You may obtain a copy of the License at *  * http://www.apache.org/licenses/LICENSE-2.0 *  *  *  * Unless required by applicable law or agreed to in writing, *  * software distributed under the License is distributed on an *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, *  * either express or implied. See the License for the specific *  * language governing permissions and limitations under the License. *  *  *  * $Id: ControlTransition.java,v 1.10 2008-01-30 11:13:57 fladder Exp $ */package groove.control;import groove.graph.DefaultLabel;import groove.graph.Edge;import groove.graph.Element;import groove.graph.Label;import groove.graph.Node;import groove.trans.Rule;import groove.trans.SPORule;import groove.util.Pair;import java.util.ArrayList;import java.util.Arrays;import java.util.HashMap;import java.util.HashSet;import java.util.List;import java.util.Map;import java.util.Set;/** *  * @author Staijen *  *         Represents a transition in a control automaton, which is unique by *         its source, target and associated Rule. *  *         This is a DefaultEdge to be able to visualize as GraphShape, and a *         LocationTransition to use for explocation. */public class ControlTransition implements Edge, Cloneable {    /**     * Creates a labelled controltransition between two controlstates     * @param source     * @param target     * @param label is the Rule associated with this transition     */    /**     * Creates a new ControLTransition with a label and a failureset     */    public ControlTransition(ControlState source, ControlState target,            String label, Map<String,ControlTransition> failures) {        this.source = source;        this.target = target;        this.label = label;        this.failures = failures;    }    /**      * creates a new ControlTransition with a label (empty failure set)     */    public ControlTransition(ControlState source, ControlState target,            String label) {        this(source, target, label, new HashMap<String,ControlTransition>());    }    /**     * creates a new ControlTransition without label and empty failure set (i.e.     * lambda-transition) *     */    public ControlTransition(ControlState source, ControlState target) {        this(source, target, null, new HashMap<String,ControlTransition>());    }    /**     * creates a new ControlTransition with no label and the given failure set *     */    public ControlTransition(ControlState source, ControlState target,            Map<String,ControlTransition> failures) {        this(source, target, null, failures);    }    @Override    public ControlTransition clone() {        ControlTransition ret =            new ControlTransition(this.source, this.target, this.label,                this.failures);        if (this.hasParameters()) {            ret.setOutputParameters(this.outputParameters);            ret.setInputParameters(this.inputParameters);        }        return ret;    }    /**     * Returns the text on the label     * @return the text on the label     */    public String getText() {        return getText(true);    }    /**     * Returns the text on the label     * @param noFailure whether this should be shown as a failure (as in a failure     * of another transition)     * @return the text on the label     */    public String getText(boolean noFailure) {        String retval = "";        if (this.hasFailures()) {            Set<String> failureStrings = new HashSet<String>();            for (ControlTransition ct : this.failures.values()) {                if (ct != null) {                    for (ControlTransition ct2 : ct.failures.values()) {                        failureStrings.add(ct2.getText(false));                    }                }            }            retval += failureStrings;        }        if (this.label != null) {            if (!retval.equals("")) {                retval += " ";            }            retval += this.label;        }        if (this.hasRelevantParameters()) {            String[] params = this.getInputParameters();            for (int i = 0; i < params.length; i++) {                if (params[i] == null) {                    params[i] = "_";                }            }            retval += Arrays.asList(params).toString();            if (noFailure) {                params = this.getOutputParameters();                for (int i = 0; i < params.length; i++) {                    if (params[i] == null) {                        params[i] = "_";                    }                }                retval += Arrays.asList(params).toString();            }        }        return retval;    }    public Label label() {        return DefaultLabel.createLabel(getText());    }    public ControlState source() {        return this.source;    }    public ControlState target() {        return this.target;    }    public Node end(int i) {        return null;    }    public int endCount() {        return 0;    }    public int endIndex(Node node) {        return 0;    }    public Node[] ends() {        return new Node[] {target()};    }    public boolean hasEnd(Node node) {        return false;    }    @Deprecated    public Node opposite() {        return target();    }    public int compareTo(Element obj) {        if (obj instanceof ControlState) {            // for states, we just need to look at the source of this transition            if (source().equals(obj)) {                return +1;            } else {                return source().compareTo(obj);            }        } else {            Edge other = (Edge) obj;            if (!source().equals(other.source())) {                return source().compareTo(other.source());            }            // for other edges, first the end count, then the label, then the            // other ends            if (endCount() != other.endCount()) {                return endCount() - other.endCount();            }            if (!label().equals(other.label())) {                return label().compareTo(other.label());            }            for (int i = 1; i < endCount(); i++) {                if (!end(i).equals(other.end(i))) {                    return end(i).compareTo(other.end(i));                }            }            return 0;        }    }    /**     * Some control transitions are not visible in the control automaton.     * @param parent the representing and visible parent element     */    public void setVisibleParent(ControlTransition parent) {        this.visibleParent = parent;    }    /**     * Some control transitions are not visible in the control automaton.     * @return the representing and visible parent element     */    public ControlTransition getVisibleParent() {        return this.visibleParent;    }    /**     * Modify the source. Meant to be used for merging states only.     */    public void setSource(ControlState source) {        this.source = source;    }    /**     * Modify the target. Ment to be used for merging states only.     */    public void setTarget(ControlState target) {        this.target = target;    }    @Override    public String toString() {        return this.source + "--- " + getText() + " --->" + this.target;    }    /**     * Setter for parent;     */    public void setParent(ControlShape parent) {        this.parent = parent;    }    /**     * Getter for parent;     * @return ControlShape     */    public ControlShape getParent() {        return this.parent;    }    /**     * Initialises the failures of this transition from the outgoing transition     * labels of the source state. It is assumed that the transition is an else     * transition.     */    public void setFailureFromInit(ControlState state) {        assert state == source();        assert this.label == null;        this.failures.putAll(state.getInit());    }    /** returns the set of string values of the failures * */    public Map<String,ControlTransition> getFailures() {        return this.failures;    }    /**     * Sets the failure-set of this transition from a given set of rules.     */    public void setFailureSet(Set<Rule> rules) {        this.failureSet = rules;    }    /**     * Sets the failure map for this ControlTransition     * @param failures a Map<String,ControlTransition> of failures     */    public void setFailures(Map<String,ControlTransition> failures) {        this.failures = failures;    }    /**     * @return the concrete failure-set of this transition (set of rules)     */    public Set<Rule> getFailureSet() {        return this.failureSet;    }    /** set rule corresponding to label * */    public void setRule(Rule rule) {        this.rule = rule;        // now that the rule is known we can populate the inputParameters and        // outputParameters lists        if (rule instanceof SPORule) {            this.inputParameters =                new String[((SPORule) rule).getNumberOfParameters(Rule.PARAMETER_INPUT)];            this.outputParameters =                new String[((SPORule) rule).getNumberOfParameters(Rule.PARAMETER_OUTPUT)];            int count = 0;            for (Pair<String,Integer> parameter : this.parameters) {                count++;                if (parameter.second() == Rule.PARAMETER_INPUT) {                    this.inputParameters[count - 1] = parameter.first();                } else if (parameter.second() == Rule.PARAMETER_OUTPUT) {                    this.outputParameters[count - 1] = parameter.first();                }            }        }    }    /** get Rule corresponding to label * */    public Rule getRule() {        return this.rule;    }    /**     * Tests if this transition is a lambda transition. This is the case if the     * label is not set and the failure set is empty.     */    public boolean isLambda() {        return this.label == null && this.failures.isEmpty();    }    /**     * Tests if this transition is an else transition. This is the case if the     * failure set is non-empty.     */    public boolean hasFailures() {        return !this.failures.isEmpty();    }    /** Tests if this transition has a label. */    public boolean hasLabel() {        return !(this.label == null);    }    /** Returns the string label of this transition. */    public String getLabel() {        return this.label;    }    /**     * Adds a parameter to this control transition     * @param name the name of the parameter     * @param type the type of the parameter (input, output, both, dont_care)     */    public void addParameter(String name, int type) {        this.parameters.add(new Pair<String,Integer>(name, type));    }    /**     * Gets the input parameters for this transition     * @return inputParameters     */    public String[] getInputParameters() {        return this.inputParameters;    }    /**     * Sets the input parameters for this transition     * @param inputParameters an array of Strings for input parameters     */    public void setInputParameters(String[] inputParameters) {        this.inputParameters = inputParameters;    }    /**     * Gets the output parameters for this transition     * @return outputParameters     */    public String[] getOutputParameters() {        return this.outputParameters;    }    /**     * Sets the output parameters for this transition     * @param outputParameters an array of Strings for input parameters     */    public void setOutputParameters(String[] outputParameters) {        this.outputParameters = outputParameters;    }    /**     * Whether this transition has parameters     * @return this.parameters != null && this.parameters.size() > 0     */    public boolean hasParameters() {        return (this.parameters != null && this.parameters.size() > 0)            || this.inputParameters != null || this.outputParameters != null;    }    /**     * Whether this transition has parameters that are not "don't care"     * @return true if this transition has any parameters that matter     */    public boolean hasRelevantParameters() {        return this.hasInputParameters() || this.hasOutputParameters();    }    /**     * Whether this ControlTransition has relevant input parameters     * @return true if any input parameters are used, false if not     */    public boolean hasInputParameters() {        boolean ret = false;        if (this.inputParameters != null) {            for (String s : this.inputParameters) {                if (s != null && !s.equals("_")) {                    ret = true;                }            }        }        return ret;    }    /**     * Whether this ControlTransition has relevant output parameters     * @return true if any output parameters are used, false if not     */    public boolean hasOutputParameters() {        boolean ret = false;        if (this.outputParameters != null) {            for (String s : this.outputParameters) {                if (s != null && !s.equals("_")) {                    ret = true;                }            }        }        return ret;    }    /** The source state of this transition. */    private ControlState source;    /** The target state of this transition. */    private ControlState target;    /** the main label of the rule; can be <code>null</code>. */    private final String label;    /**     * The failure set of the transition; can be empty. The failure set is a set     * of names; the corresponding rules are collected in {@link #failureSet} on     * finalisation of the automaton.     */    private Map<String,ControlTransition> failures;    /**     * to store the shape the transition is stored in, for removing purposes     */    private ControlShape parent;    private ControlTransition visibleParent;    /** Rules corresponding to the failure set (see {@link #failures}). */    private Set<Rule> failureSet;    /** Rule corresponding to the transition label (see {@link #label()}). */    private Rule rule;    /**     * Temporary list of parameters used in this transition, without the Rule     * being known     */    private final List<Pair<String,Integer>> parameters =        new ArrayList<Pair<String,Integer>>();    /** List of input parameters used in this transition */    private String[] inputParameters;    /**     * List of output parameters used in this transition, note that this will     * always be null if it is a failure transition     */    private String[] outputParameters;}