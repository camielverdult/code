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
 * $Id: AnchorSearchItem.java,v 1.4 2008-01-30 09:33:29 iovka Exp $
 */
package groove.match;

import groove.graph.algebra.ArgumentEdge;
import groove.graph.algebra.OperatorEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.VariableNode;
import groove.match.SearchPlanStrategy.Search;
import groove.rel.LabelVar;
import groove.rel.VarSupport;
import groove.trans.RuleEdge;
import groove.trans.RuleNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Search item that reflects (and optionally checks) that a set of elements
 * (nodes, variables and edges) have already been matched.
 * @author Arend Rensink
 * @version $Revision $
 */
class AnchorSearchItem extends AbstractSearchItem {
    /**
     * Creates an instance with given sets of pre-matched nodes, edges and
     * variables.
     * @param nodes the set of pre-matched nodes; not <code>null</code>
     * @param edges the set of pre-matched edges; not <code>null</code>
     */
    AnchorSearchItem(Collection<RuleNode> nodes, Collection<RuleEdge> edges) {
        this.nodes = new HashSet<RuleNode>(nodes);
        this.edges = new HashSet<RuleEdge>(edges);
        this.vars = new HashSet<LabelVar>();
        for (RuleEdge edge : edges) {
            this.vars.addAll(VarSupport.getAllVars(edge));
        }
    }

    /** This implementation returns the set of pre-matched edges. */
    @Override
    public Collection<RuleEdge> bindsEdges() {
        return this.edges;
    }

    /** This implementation returns the set of pre-matched nodes. */
    @Override
    public Collection<RuleNode> bindsNodes() {
        return this.nodes;
    }

    /** This implementation returns the set of pre-matched variables. */
    @Override
    public Collection<LabelVar> bindsVars() {
        return this.vars;
    }

    /**
     * This item gets the highest rating since it should be scheduled first.
     */
    @Override
    int getRating() {
        return Integer.MAX_VALUE;
    }

    public void activate(SearchPlanStrategy strategy) {
        this.nodeIxMap = new HashMap<RuleNode,Integer>();
        for (RuleNode node : this.nodes) {
            assert !strategy.isNodeFound(node) : String.format(
                "Node %s is not fresh", node);
            if (isAnchorable(node)) {
                this.nodeIxMap.put(node, strategy.getNodeIx(node));
            }
        }
        this.edgeIxMap = new HashMap<RuleEdge,Integer>();
        for (RuleEdge edge : this.edges) {
            assert !strategy.isEdgeFound(edge) : String.format(
                "Edge %s is not fresh", edge);
            if (isAnchorable(edge)) {
                this.edgeIxMap.put(edge, strategy.getEdgeIx(edge));
            }
        }
        this.varIxMap = new HashMap<LabelVar,Integer>();
        for (LabelVar var : this.vars) {
            assert !strategy.isVarFound(var) : String.format(
                "Variable %s is not fresh", var);
            this.varIxMap.put(var, strategy.getVarIx(var));
        }
    }

    /**
     * Tests is a give node can serve proper anchor, in the sense that it is
     * matched to an actual host graph node. This fails to hold for
     * {@link ProductNode}s that are not {@link VariableNode}s.
     */
    private boolean isAnchorable(RuleNode node) {
        return !(node instanceof ProductNode) || node instanceof VariableNode;
    }

    /**
     * Tests is a give edge is a proper anchor, in the sense that it is matched
     * to an actual host graph edge. This fails to hold for {@link ArgumentEdge}s
     * and {@link OperatorEdge}s.
     */
    private boolean isAnchorable(RuleEdge edge) {
        return !(edge instanceof ArgumentEdge || edge instanceof OperatorEdge);
    }

    @Override
    public String toString() {
        List<Object> elementList = new ArrayList<Object>();
        elementList.addAll(this.nodes);
        elementList.addAll(this.edges);
        elementList.addAll(this.vars);
        return String.format("Check %s", elementList);
    }

    public Record createRecord(Search search) {
        assert allElementsMatched(search) : String.format(
            "Elements %s not pre-matched", this.unmatched);
        return new DummyRecord();
    }

    private boolean allElementsMatched(Search search) {
        if (this.unmatched == null) {
            this.unmatched = new HashSet<Object>();
            for (Map.Entry<RuleNode,Integer> nodeEntry : this.nodeIxMap.entrySet()) {
                if (search.getNode(nodeEntry.getValue()) == null) {
                    this.unmatched.add(nodeEntry.getKey());
                }
            }
            for (Map.Entry<RuleEdge,Integer> edgeEntry : this.edgeIxMap.entrySet()) {
                if (search.getEdge(edgeEntry.getValue()) == null) {
                    this.unmatched.add(edgeEntry.getKey());
                }
            }
            for (Map.Entry<LabelVar,Integer> varEntry : this.varIxMap.entrySet()) {
                if (search.getVar(varEntry.getValue()) == null) {
                    this.unmatched.add(varEntry.getKey());
                }
            }
        }
        return this.unmatched.isEmpty();
    }

    /** The set of pre-matched nodes. */
    private final Set<RuleNode> nodes;
    /** The set of pre-matched edges. */
    private final Set<RuleEdge> edges;
    /** The set of pre-matched variables. */
    private final Set<LabelVar> vars;
    /**
     * Mapping from pre-matched nodes (in {@link #nodes}) to their indices in
     * the result.
     */
    private Map<RuleNode,Integer> nodeIxMap;
    /**
     * Mapping from pre-matched edges (in {@link #edges}) to their indices in
     * the result.
     */
    private Map<RuleEdge,Integer> edgeIxMap;
    /**
     * Mapping from pre-matched variables (in {@link #vars}) to their indices
     * in the result.
     */
    private Map<LabelVar,Integer> varIxMap;
    /** The set of unmatched graph elements (that should have been pre-matched) . */
    private Set<Object> unmatched;
}
