/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: AlgebraEdge.java,v 1.9 2008-03-13 14:43:00 rensink Exp $
 */
package groove.graph.algebra;

import groove.graph.AbstractEdge;
import groove.graph.TypeEdge;
import groove.graph.TypeElement;
import groove.graph.TypeGuard;
import groove.rel.LabelVar;
import groove.trans.RuleEdge;
import groove.trans.RuleLabel;
import groove.trans.RuleNode;

import java.util.List;
import java.util.Set;

/**
 * Instances of this class are edges between
 * {@link groove.graph.algebra.ProductNode}s and
 * {@link groove.graph.algebra.VariableNode}s.
 * @author Harmen Kastenberg
 * @version $Revision 1.0$ $Date: 2008-03-13 14:43:00 $
 */
public class ArgumentEdge extends AbstractEdge<RuleNode,RuleLabel> implements
        RuleEdge {
    /** Constructs a fresh edge.
      * @param label the rule label; must satisfy {@link RuleLabel#isArgument()}
     */
    public ArgumentEdge(ProductNode source, RuleLabel label, VariableNode target) {
        super(source, label, target);
        assert label.isArgument();
        this.number = label.getArgument();
        source.setArgument(this.number, target);
    }

    @Override
    public VariableNode target() {
        return (VariableNode) super.target();
    }

    @Override
    public ProductNode source() {
        return (ProductNode) super.source();
    }

    /** Returns the argument number of this edge. */
    public int getNumber() {
        return this.number;
    }

    @Override
    public TypeEdge getType() {
        return null;
    }

    @Override
    public Set<LabelVar> getVars() {
        return EMPTY_VAR_SET;
    }

    @Override
    public List<TypeGuard> getTypeGuards() {
        return EMPTY_GUARD_LIST;
    }

    @Override
    public Set<? extends TypeElement> getMatchingTypes() {
        return EMPTY_MATCH_SET;
    }

    /** The number of this algebra edge. */
    private final int number;
}
