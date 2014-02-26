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
package groove.lts;

import groove.control.CtrlStep;
import groove.grammar.Rule;
import groove.grammar.host.HostNode;
import groove.graph.ALabel;
import groove.graph.Label;
import groove.transform.AbstractRuleEvent;
import groove.transform.Record;
import groove.transform.RuleEvent;

import java.util.Arrays;

/** Class of labels that can appear on rule transitions. */
public class RuleTransitionLabel extends ALabel implements ActionLabel {
    /** 
     * Constructs a new label on the basis of a given match and list
     * of created nodes.
     */
    private RuleTransitionLabel(GraphState source, MatchResult match, HostNode[] addedNodes) {
        this.event = match.getEvent();
        this.step = match.getStep();
        this.addedNodes = addedNodes;
    }

    @Override
    public Rule getAction() {
        return this.event.getRule();
    }

    /** Returns the event wrapped in this label. */
    public RuleEvent getEvent() {
        return this.event;
    }

    private final RuleEvent event;

    /** Returns the control step wrapped in this label. */
    public CtrlStep getStep() {
        return this.step;
    }

    private final CtrlStep step;

    /** Returns the added nodes of the label. */
    public HostNode[] getAddedNodes() {
        return this.addedNodes;
    }

    private final HostNode[] addedNodes;

    @Override
    public String text() {
        return text(false);
    }

    /** Returns the label text, with optionally the rule parameters
     * replaced by anchor images.
     * @param anchored if {@code true}, the anchor images are used
     * instead of the rule parameters
     */
    public String text(boolean anchored) {
        StringBuilder result = new StringBuilder();
        boolean brackets = getAction().getSystemProperties().isShowTransitionBrackets();
        if (brackets) {
            result.append(BEGIN_CHAR);
        }
        if (getStep().isRecipeStep()) {
            result.append(getStep().getRecipe().getFullName());
            result.append('/');
        }
        result.append(((AbstractRuleEvent<?,?>) getEvent()).getLabelText(this.addedNodes, anchored));
        if (brackets) {
            result.append(END_CHAR);
        }
        return result.toString();
    }

    @Override
    protected int computeHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(this.addedNodes);
        result = prime * result + this.event.hashCode();
        result = prime * result + this.step.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RuleTransitionLabel other = (RuleTransitionLabel) obj;
        if (!Arrays.equals(this.addedNodes, other.addedNodes)) {
            return false;
        }
        if (!this.event.equals(other.event)) {
            return false;
        }
        if (!this.step.equals(other.step)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Label obj) {
        if (!(obj instanceof ActionLabel)) {
            throw new IllegalArgumentException(String.format("Can't compare %s and %s",
                this.getClass(), obj.getClass()));
        }
        int result = super.compareTo(obj);
        if (result != 0) {
            return result;
        }
        if (obj instanceof RecipeTransitionLabel) {
            return -1;
        }
        RuleTransitionLabel other = (RuleTransitionLabel) obj;
        result = getStep().compareTo(other.getStep());
        if (result != 0) {
            return result;
        }
        result = getEvent().compareTo(other.getEvent());
        return result;
    }

    /** 
     * Returns the label text for the rule label consisting of a given source state
     * and event. Optionally, the rule parameters are replaced by anchor images.
     */
    public static final String text(GraphState source, MatchResult match, boolean anchored) {
        return createLabel(source, match, null).text(anchored);
    }

    /** 
     * Creates a normalised rule label.
     * @see Record#normaliseLabel(RuleTransitionLabel)
     */
    public static final RuleTransitionLabel createLabel(GraphState source, MatchResult match,
            HostNode[] addedNodes) {
        RuleTransitionLabel result = new RuleTransitionLabel(source, match, addedNodes);
        if (REUSE_LABELS) {
            Record record = source.getGTS().getRecord();
            RuleTransitionLabel newResult = record.normaliseLabel(result);
            result = newResult;
        }
        return result;
    }

    /** The obligatory first character of a rule name. */
    private static final char BEGIN_CHAR = '<';
    /** The obligatory last character of a rule name. */
    private static final char END_CHAR = '>';
    /** Flag controlling whether transition labels are normalised. */
    public static boolean REUSE_LABELS = true;
}
