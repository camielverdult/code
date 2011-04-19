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
 * $Id: ConditionSearchPlanFactory.java,v 1.23 2008-02-05 13:43:26 rensink Exp $
 */
package groove.match;

import groove.algebra.AlgebraFamily;
import groove.graph.DefaultNode;
import groove.graph.Label;
import groove.graph.LabelStore;
import groove.graph.TypeLabel;
import groove.graph.algebra.OperatorEdge;
import groove.graph.algebra.VariableNode;
import groove.rel.LabelVar;
import groove.rel.RegExpr;
import groove.rel.VarSupport;
import groove.trans.Condition;
import groove.trans.EdgeEmbargo;
import groove.trans.MergeEmbargo;
import groove.trans.NotCondition;
import groove.trans.RuleEdge;
import groove.trans.RuleGraph;
import groove.trans.RuleGraphMorphism;
import groove.trans.RuleLabel;
import groove.trans.RuleNode;
import groove.trans.SystemProperties;
import groove.util.Bag;
import groove.util.HashBag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeSet;

/**
 * Factory that adds to a graph search plan the following items the search items
 * for the simple negative conditions (edge and merge embargoes).
 * @author Arend Rensink
 * @version $Revision$
 */
public class SearchPlanEngine extends SearchEngine<SearchPlanStrategy> {
    /**
     * Private constructor. Get the instance through
     * {@link #getInstance(boolean,String)}.
     * @param injective if <code>true</code>, the factory produces injective
     *        matchers only
     * @param algebraFamily the name of the set of algebras to be used in
     * data value manipulation
     * @see AlgebraFamily#getInstance(String)
     */
    private SearchPlanEngine(boolean injective, String algebraFamily) {
        this.injective = injective;
        this.ignoreNeg = false;
        this.algebraFamily = AlgebraFamily.getInstance(algebraFamily);
    }

    @Override
    public SearchPlanStrategy createMatcher(Condition condition,
            Collection<RuleNode> anchorNodes, Collection<RuleEdge> anchorEdges,
            Collection<RuleNode> relevantNodes) {
        assert (anchorNodes == null) == (anchorEdges == null) : "Anchor nodes and edges should be null simultaneously";
        if (anchorNodes == null) {
            RuleGraphMorphism patternMap = condition.getRootMap();
            anchorNodes = patternMap.nodeMap().values();
            anchorEdges = patternMap.edgeMap().values();
        }
        GrammarPlanData planData = new GrammarPlanData(condition);
        List<AbstractSearchItem> plan =
            planData.getPlan(anchorNodes, anchorEdges);
        if (relevantNodes != null) {
            Set<RuleNode> unboundRelevantNodes =
                new HashSet<RuleNode>(relevantNodes);
            Set<LabelVar> boundVars = new HashSet<LabelVar>();
            for (AbstractSearchItem item : plan) {
                item.setRelevant(unboundRelevantNodes.removeAll(item.bindsNodes())
                    | boundVars.addAll(item.bindsVars()));
            }
        }
        SearchPlanStrategy result =
            new SearchPlanStrategy(condition.getTarget(), plan, this.injective);
        if (PRINT) {
            System.out.print(String.format(
                "%nPlan for %s, prematched nodes %s, prematched edges %s:%n    %s",
                condition.getName(), anchorNodes, anchorEdges, result));
        }
        result.setFixed();
        return result;
    }

    /** 
     * The algebra family to be used for algebraic operations.
     * If {@code null}, the default will be used.
     * @see AlgebraFamily#getInstance(String)
     */
    private final AlgebraFamily algebraFamily;
    /** Flag indicating if this factory creates injective matchings. */
    private final boolean injective;

    /**
     * Flag indicating if this factory creates matchings that ignore negations
     * in the source graph.
     */
    private final boolean ignoreNeg;

    /** Returns an instance of this factory class.
     * @param injective if <code>true</code>, the factory produces injective
     *        matchers only
     * @param algebraFamily the name of the set of algebras to be used in
     * data value manipulation
     * @see AlgebraFamily#getInstance(String)
     */
    static public SearchPlanEngine getInstance(boolean injective,
            String algebraFamily) {
        if (instance == null || instance.injective != injective
            || instance.algebraFamily.equals(algebraFamily)) {
            instance = new SearchPlanEngine(injective, algebraFamily);
        }
        return instance;
    }

    static private SearchPlanEngine instance;

    /** Flag to control search plan printing. */
    static private final boolean PRINT = false;

    /**
     * Plan data extension based on a graph condition. Additionally it takes the
     * control labels of the condition into account.
     * @author Arend Rensink
     * @version $Revision $
     */
    class GrammarPlanData extends Observable implements Comparator<SearchItem> {
        /**
         * Constructs a fresh instance of the plan data, based on a given set of
         * system properties, and sets of already matched nodes and edges.
         * @param condition the graph condition for which we develop the search
         *        plan
         */
        GrammarPlanData(Condition condition) {
            RuleGraph graph = condition.getTarget();
            // compute the set of remaining (unmatched) nodes
            this.remainingNodes = new LinkedHashSet<RuleNode>(graph.nodeSet());
            // compute the set of remaining (unmatched) edges and variables
            this.remainingEdges = new LinkedHashSet<RuleEdge>(graph.edgeSet());
            this.remainingVars =
                new LinkedHashSet<LabelVar>(VarSupport.getAllVars(graph));
            this.labelStore = condition.getLabelStore();
            this.condition = condition;
        }

        /**
         * Adds embargo and injection search items to the super result.
         * @param anchorNodes the set of pre-matched nodes
         * @param anchorEdges the set of pre-matched edges
         */
        Collection<AbstractSearchItem> computeSearchItems(
                Collection<RuleNode> anchorNodes,
                Collection<RuleEdge> anchorEdges) {
            Collection<AbstractSearchItem> result =
                new ArrayList<AbstractSearchItem>();
            Set<RuleNode> unmatchedNodes =
                new LinkedHashSet<RuleNode>(this.remainingNodes);
            Set<RuleEdge> unmatchedEdges =
                new LinkedHashSet<RuleEdge>(this.remainingEdges);
            // first a single search item for the pre-matched elements
            if (anchorNodes == null) {
                anchorNodes = Collections.emptySet();
            }
            if (anchorEdges == null) {
                anchorEdges = Collections.emptySet();
            }
            if (!anchorNodes.isEmpty() || !anchorEdges.isEmpty()) {
                AbstractSearchItem preMatchItem =
                    new AnchorSearchItem(anchorNodes, anchorEdges);
                result.add(preMatchItem);
                unmatchedNodes.removeAll(preMatchItem.bindsNodes());
                unmatchedEdges.removeAll(preMatchItem.bindsEdges());
            }
            // match all the value nodes explicitly
            Iterator<RuleNode> unmatchedNodeIter = unmatchedNodes.iterator();
            while (unmatchedNodeIter.hasNext()) {
                RuleNode node = unmatchedNodeIter.next();
                if (node instanceof VariableNode
                    && ((VariableNode) node).getConstant() != null) {
                    result.add(createNodeSearchItem(node));
                    unmatchedNodeIter.remove();
                }
            }
            // then a search item per remaining edge
            for (RuleEdge edge : unmatchedEdges) {
                AbstractSearchItem edgeItem = createEdgeSearchItem(edge);
                if (edgeItem != null) {
                    result.add(edgeItem);
                    unmatchedNodes.removeAll(edgeItem.bindsNodes());
                }
            }
            // finally a search item per remaining node
            for (RuleNode node : unmatchedNodes) {
                AbstractSearchItem nodeItem = createNodeSearchItem(node);
                if (nodeItem != null) {
                    assert !(node instanceof VariableNode)
                        || ((VariableNode) node).getConstant() != null
                        || anchorNodes.contains(node) : String.format(
                        "Variable node '%s' should be among anchors %s", node,
                        anchorNodes);
                    result.add(nodeItem);
                }
            }
            for (Condition subCondition : this.condition.getSubConditions()) {
                if (subCondition instanceof MergeEmbargo) {
                    RuleNode node1 = ((MergeEmbargo) subCondition).node1();
                    RuleNode node2 = ((MergeEmbargo) subCondition).node2();
                    result.add(createInjectionSearchItem(node1, node2));
                } else if (subCondition instanceof EdgeEmbargo) {
                    RuleEdge embargoEdge =
                        ((EdgeEmbargo) subCondition).getEmbargoEdge();
                    result.add(createNegatedSearchItem(createEdgeSearchItem(embargoEdge)));
                } else if (subCondition instanceof NotCondition) {
                    result.add(new ConditionSearchItem(subCondition));
                }
            }
            return result;
        }

        /**
         * Creates the comparators for the search plan. Adds a comparator based
         * on the control labels available in the grammar, if any.
         * @return a list of comparators determining the order in which edges
         *         should be matched
         */
        Collection<Comparator<SearchItem>> computeComparators() {
            Collection<Comparator<SearchItem>> result =
                new TreeSet<Comparator<SearchItem>>(
                    new ItemComparatorComparator());
            result.add(new NeededPartsComparator(this.remainingNodes,
                this.remainingVars));
            result.add(new ItemTypeComparator());
            result.add(new ConnectedPartsComparator(this.remainingNodes,
                this.remainingVars));
            result.add(new IndegreeComparator(this.remainingEdges));
            SystemProperties properties = this.condition.getSystemProperties();
            if (properties != null) {
                List<String> controlLabels = properties.getControlLabels();
                List<String> commonLabels = properties.getCommonLabels();
                result.add(new FrequencyComparator(controlLabels, commonLabels));
            }
            return result;
        }

        /**
         * Creates and returns a search plan on the basis of the given data.
         * @param anchorNodes the set of pre-matched nodes; may be
         *        <code>null</code> for an empty set
         * @param anchorEdges the set of pre-matched edges; may be
         *        <code>null</code> for an empty set
         */
        public List<AbstractSearchItem> getPlan(
                Collection<RuleNode> anchorNodes,
                Collection<RuleEdge> anchorEdges) {
            if (this.used) {
                throw new IllegalStateException(
                    "Method getPlan() was already called");
            } else {
                this.used = true;
            }
            List<AbstractSearchItem> result =
                new ArrayList<AbstractSearchItem>();
            Collection<AbstractSearchItem> items =
                computeSearchItems(anchorNodes, anchorEdges);
            while (!items.isEmpty()) {
                AbstractSearchItem bestItem = Collections.max(items, this);
                result.add(bestItem);
                items.remove(bestItem);
                this.remainingEdges.removeAll(bestItem.bindsEdges());
                this.remainingNodes.removeAll(bestItem.bindsNodes());
                this.remainingVars.removeAll(bestItem.bindsVars());
                // notify the observing comparators of the change
                setChanged();
                notifyObservers(bestItem);
            }
            return result;
        }

        /**
         * Orders search items according to the lexicographic order of the
         * available item comparators.
         */
        final public int compare(SearchItem o1, SearchItem o2) {
            int result = 0;
            Iterator<Comparator<SearchItem>> comparatorIter =
                getComparators().iterator();
            while (result == 0 && comparatorIter.hasNext()) {
                Comparator<SearchItem> next = comparatorIter.next();
                result = next.compare(o1, o2);
            }
            if (result == 0) {
                result = o1.compareTo(o2);
            }
            return result;
        }

        /**
         * Lazily creates and returns the set of search item comparators that
         * determines their priority in the search plan.
         */
        final Collection<Comparator<SearchItem>> getComparators() {
            if (this.comparators == null) {
                this.comparators = computeComparators();
                // add those comparators as listeners that implement the
                // observer interface
                for (Comparator<SearchItem> comparator : this.comparators) {
                    if (comparator instanceof Observer) {
                        addObserver((Observer) comparator);
                    }
                }
            }
            return this.comparators;
        }

        /**
         * Callback factory method for creating an edge search item.
         */
        protected AbstractSearchItem createEdgeSearchItem(RuleEdge edge) {
            AbstractSearchItem result = null;
            RuleLabel label = edge.label();
            RuleNode target = edge.target();
            RuleNode source = edge.source();
            RegExpr negOperand = label.getNegOperand();
            if (negOperand instanceof RegExpr.Empty) {
                if (!SearchPlanEngine.this.ignoreNeg) {
                    result = createInjectionSearchItem(source, target);
                }
            } else if (negOperand != null) {
                if (!SearchPlanEngine.this.ignoreNeg) {
                    RuleEdge negatedEdge =
                        new RuleEdge(source, negOperand.toLabel(), target);
                    result =
                        createNegatedSearchItem(createEdgeSearchItem(negatedEdge));
                }
            } else if (label.getWildcardId() != null) {
                result = new VarEdgeSearchItem(edge);
            } else if (label.isWildcard()) {
                result = new WildcardEdgeSearchItem(edge);
            } else if (label.isSharp()) {
                result = new Edge2SearchItem(edge);
            } else if (label.isNodeType()) {
                result = new NodeTypeSearchItem(edge, this.labelStore);
            } else if (label.isAtom()) {
                result = new Edge2SearchItem(edge);
            } else if (label.isOperator()) {
                result =
                    new OperatorEdgeSearchItem((OperatorEdge) edge,
                        SearchPlanEngine.this.algebraFamily);
            } else if (!label.isArgument()) {
                result = new RegExprEdgeSearchItem(edge, this.labelStore);
            }
            return result;
        }

        /**
         * Callback factory method for creating a node search item.
         */
        protected AbstractSearchItem createNodeSearchItem(RuleNode node) {
            AbstractSearchItem result = null;
            if (node instanceof VariableNode) {
                if (((VariableNode) node).getSymbol() != null) {
                    result =
                        new ValueNodeSearchItem((VariableNode) node,
                            SearchPlanEngine.this.algebraFamily);
                }
                // otherwise, the node must be among the count nodes of
                // the subconditions
            } else if (node instanceof DefaultNode) {
                result = new NodeSearchItem(node);
            }
            return result;
        }

        /**
         * Callback factory method for a negated search item.
         * @param inner the internal search item which this one negates
         * @return an instance of {@link NegatedSearchItem}
         */
        protected NegatedSearchItem createNegatedSearchItem(SearchItem inner) {
            return new NegatedSearchItem(inner);
        }

        /**
         * Callback factory method for an injection search item.
         * @param injection the set of nodes to be matched injectively
         * @return an instance of {@link InjectionSearchItem}
         */
        protected InjectionSearchItem createInjectionSearchItem(
                Collection<RuleNode> injection) {
            return new InjectionSearchItem(injection);
        }

        /**
         * Callback factory method for an injection search item.
         * @param node1 the first node to be matched injectively
         * @param node2 the second node to be matched injectively
         * @return an instance of {@link InjectionSearchItem}
         */
        protected InjectionSearchItem createInjectionSearchItem(RuleNode node1,
                RuleNode node2) {
            return new InjectionSearchItem(node1, node2);
        }

        /**
         * The set of nodes to be matched.
         */
        private final Set<RuleNode> remainingNodes;
        /**
         * The set of edges to be matched.
         */
        private final Set<RuleEdge> remainingEdges;
        /**
         * The set of variables to be matched.
         */
        private final Set<LabelVar> remainingVars;
        /** The label store containing the subtype relation. */
        private final LabelStore labelStore;
        /**
         * The comparators used to determine the order in which the edges should
         * be matched.
         */
        private Collection<Comparator<SearchItem>> comparators;
        /**
         * Flag determining if {@link #getPlan(Collection, Collection)} was
         * already called.
         */
        private boolean used;

        /** The graph condition for which we develop the plan. */
        private final Condition condition;
    }

    /**
     * Edge comparator based on the number of incoming edges of the source and
     * target nodes. An edge is better if it has lower source indegree, or
     * failing that, higher target indegree. The idea is that the "roots" of a
     * graph (those starting in nodes with small indegree) are likely to give a
     * better immediate reduction of the number of possible matches. For the
     * outdegree the reasoning is that the more constraints a matching causes,
     * the better. The class is an observer in order to be able to maintain the
     * indegrees.
     * @author Arend Rensink
     * @version $Revision $
     */
    static class IndegreeComparator implements Comparator<SearchItem>, Observer {
        /**
         * Constructs a comparator on the basis of a given set of unmatched
         * edges.
         */
        IndegreeComparator(Set<? extends RuleEdge> remainingEdges) {
            // compute indegrees
            Bag<RuleNode> indegrees = new HashBag<RuleNode>();
            for (RuleEdge edge : remainingEdges) {
                if (!edge.target().equals(edge.source())) {
                    indegrees.add(edge.target());
                }
            }
            this.indegrees = indegrees;
        }

        /**
         * Favours the edge with the lowest source indegree, or, failing that,
         * the highest target indegree.
         */
        public int compare(SearchItem item1, SearchItem item2) {
            int result = 0;
            if (item1 instanceof Edge2SearchItem
                && item2 instanceof Edge2SearchItem) {
                RuleEdge first = ((Edge2SearchItem) item1).getEdge();
                RuleEdge second = ((Edge2SearchItem) item2).getEdge();
                // first test for the indegree of the source (lower = better)
                result = indegree(second.source()) - indegree(first.source());
                if (result == 0) {
                    // now test for the indegree of the target (higher = better)
                    result =
                        indegree(first.target()) - indegree(second.target());
                }
            }
            return result;
        }

        /**
         * This method is called when a new edge is scheduled. It decreases the
         * indegree of all the edge target.
         */
        public void update(Observable o, Object arg) {
            if (arg instanceof Edge2SearchItem) {
                RuleEdge selected = ((Edge2SearchItem) arg).getEdge();
                this.indegrees.remove(selected.target());
            }
        }

        /**
         * Returns the indegree of a given node.
         */
        private int indegree(RuleNode node) {
            return this.indegrees.multiplicity(node);
        }

        /**
         * The indegrees.
         */
        private final Bag<RuleNode> indegrees;
    }

    /**
     * Search item comparator that gives least priority to items of which some
     * needed nodes or variables have not yet been matched. Among those of which
     * all needed parts have been matched, the comparator prefers those of which
     * the most bound parts have also been matched.
     * @author Arend Rensink
     * @version $Revision$
     */
    static class NeededPartsComparator implements Comparator<SearchItem> {
        NeededPartsComparator(Set<RuleNode> remainingNodes,
                Set<LabelVar> remainingVars) {
            this.remainingNodes = remainingNodes;
            this.remainingVars = remainingVars;
        }

        /**
         * First compares the need count (higher is better), then the bind count
         * (lower is better).
         */
        public int compare(SearchItem o1, SearchItem o2) {
            return getNeedCount(o1) - getNeedCount(o2);
        }

        /**
         * Returns 0 if the item needs a node or variable that has not yet been
         * matched, 1 if all needed parts have been matched.
         */
        private int getNeedCount(SearchItem item) {
            boolean missing = false;
            Iterator<RuleNode> neededNodeIter = item.needsNodes().iterator();
            while (!missing && neededNodeIter.hasNext()) {
                missing = this.remainingNodes.contains(neededNodeIter.next());
            }
            Iterator<LabelVar> neededVarIter = item.needsVars().iterator();
            while (!missing && neededVarIter.hasNext()) {
                missing = this.remainingVars.contains(neededVarIter.next());
            }
            return missing ? 0 : 1;
        }

        /** The set of (as yet) unscheduled nodes. */
        private final Set<RuleNode> remainingNodes;
        /** The set of (as yet) unscheduled variables. */
        private final Set<LabelVar> remainingVars;
    }

    /**
     * Search item comparator that gives higher priority to items of which more
     * parts have been matched.
     * @author Arend Rensink
     * @version $Revision$
     */
    static class ConnectedPartsComparator implements Comparator<SearchItem> {
        ConnectedPartsComparator(Set<RuleNode> remainingNodes,
                Set<LabelVar> remainingVars) {
            this.remainingNodes = remainingNodes;
            this.remainingVars = remainingVars;
        }

        /**
         * Compares the connect count (higher is better).
         */
        public int compare(SearchItem o1, SearchItem o2) {
            return getConnectCount(o1) - getConnectCount(o2);
        }

        /**
         * Returns the number of nodes and variables bound by the item that have
         * not yet been matched. More unmatched parts means more
         * non-determinism, so the lower the better.
         */
        private int getConnectCount(SearchItem item) {
            int result = 0;
            for (RuleNode node : item.bindsNodes()) {
                if (!this.remainingNodes.contains(node)) {
                    result++;
                }
            }
            for (LabelVar var : item.bindsVars()) {
                if (!this.remainingVars.contains(var)) {
                    result++;
                }
            }
            return result;
        }

        /** The set of (as yet) unscheduled nodes. */
        private final Set<RuleNode> remainingNodes;
        /** The set of (as yet) unscheduled variables. */
        private final Set<LabelVar> remainingVars;
    }

    /**
     * Search item comparator that gives higher priority to items with more
     * unmatched parts.
     * @author Arend Rensink
     * @version $Revision$
     */
    static class BoundPartsComparator implements Comparator<SearchItem> {
        BoundPartsComparator(Set<RuleNode> remainingNodes,
                Set<LabelVar> remainingVars) {
            this.remainingNodes = remainingNodes;
            this.remainingVars = remainingVars;
        }

        /**
         * Compares the connect count (higher is better).
         */
        public int compare(SearchItem o1, SearchItem o2) {
            return getBoundCount(o1) - getBoundCount(o2);
        }

        /**
         * Returns the number of nodes and variables bound by the item that have
         * not yet been matched. More unmatched parts means more
         * non-determinism, so the lower the better.
         */
        private int getBoundCount(SearchItem item) {
            int result = 0;
            for (RuleNode node : item.bindsNodes()) {
                if (this.remainingNodes.contains(node)) {
                    result++;
                }
            }
            for (LabelVar var : item.bindsVars()) {
                if (this.remainingVars.contains(var)) {
                    result++;
                }
            }
            return result;
        }

        /** The set of (as yet) unscheduled nodes. */
        private final Set<RuleNode> remainingNodes;
        /** The set of (as yet) unscheduled variables. */
        private final Set<LabelVar> remainingVars;
    }

    /**
     * Edge comparator for regular expression edges. An edge is better if it is
     * not regular, or if the automaton is not reflexive.
     * @author Arend Rensink
     * @version $Revision $
     */
    static class ItemTypeComparator implements Comparator<SearchItem> {
        /**
         * Compares two regular expression-based items, with the purpose of
         * determining which one should be scheduled first. In order from worst
         * to best:
         * <ul>
         * <li> {@link NodeSearchItem}s of a non-specialised type
         * <li> {@link ConditionSearchItem}s
         * <li> {@link RegExprEdgeSearchItem}s
         * <li> {@link VarEdgeSearchItem}s
         * <li> {@link WildcardEdgeSearchItem}s
         * <li> {@link Edge2SearchItem}s
         * <li> {@link InjectionSearchItem}s
         * <li> {@link NegatedSearchItem}s
         * <li> {@link OperatorEdgeSearchItem}s
         * <li> {@link ValueNodeSearchItem}s
         * <li> {@link AnchorSearchItem}s
         * </ul>
         */
        public int compare(SearchItem o1, SearchItem o2) {
            return getRating(o1) - getRating(o2);
        }

        /**
         * Computes a rating for a search item from its type. A higher rating is
         * better.
         */
        int getRating(SearchItem item) {
            int result = 0;
            Class<?> itemClass = item.getClass();
            if (itemClass == NodeSearchItem.class) {
                return result;
            }
            result++;
            if (itemClass == NodeTypeSearchItem.class) {
                return result;
            }
            result++;
            if (itemClass == ConditionSearchItem.class) {
                return result;
            }
            result++;
            if (itemClass == RegExprEdgeSearchItem.class) {
                return result;
            }
            result++;
            if (itemClass == VarEdgeSearchItem.class) {
                return result;
            }
            result++;
            if (itemClass == WildcardEdgeSearchItem.class) {
                return result;
            }
            result++;
            if (itemClass == Edge2SearchItem.class) {
                return result;
            }
            result++;
            if (itemClass == InjectionSearchItem.class) {
                return result;
            }
            result++;
            if (itemClass == NegatedSearchItem.class) {
                return result;
            }
            result++;
            if (itemClass == OperatorEdgeSearchItem.class) {
                return result;
            }
            result++;
            if (itemClass == ValueNodeSearchItem.class) {
                return result;
            }
            result++;
            //            if (itemClass == VariableNodeSearchItem.class) {
            //                return result;
            //            }
            //            result++;
            if (itemClass == AnchorSearchItem.class) {
                return result;
            }
            throw new IllegalArgumentException(String.format(
                "Unrecognised search item %s", item));
        }
    }

    /**
     * Edge comparator on the basis of lists of high- and low-priority labels.
     * Preference is given to labels occurring early in this list.
     * @author Arend Rensink
     * @version $Revision $
     */
    static class FrequencyComparator implements Comparator<SearchItem> {
        /**
         * Constructs a comparator on the basis of two lists of labels. The
         * first list contains high-priority labels, in the order of decreasing
         * priority; the second list low-priority labels, in order of increasing
         * priority. Labels not in either list have intermediate priority and
         * are ordered alphabetically.
         * @param rare high-priority labels, in order of decreasing priority;
         *        may be <code>null</code>
         * @param common low-priority labels, in order of increasing priority;
         *        may be <code>null</code>
         */
        FrequencyComparator(List<String> rare, List<String> common) {
            this.priorities = new HashMap<Label,Integer>();
            if (rare != null) {
                for (int i = 0; i < rare.size(); i++) {
                    Label label = TypeLabel.createLabel(rare.get(i));
                    this.priorities.put(label, rare.size() - i);
                }
            }
            if (common != null) {
                for (int i = 0; i < common.size(); i++) {
                    Label label = TypeLabel.createLabel(common.get(i));
                    this.priorities.put(label, i - common.size());
                }
            }
        }

        /**
         * Favours the edge occurring earliest in the high-priority labels, or
         * latest in the low-priority labels. In case of equal priority,
         * alphabetical ordering is used.
         */
        public int compare(SearchItem first, SearchItem second) {
            if (first instanceof Edge2SearchItem
                && second instanceof Edge2SearchItem) {
                Label firstLabel = ((Edge2SearchItem) first).getEdge().label();
                Label secondLabel =
                    ((Edge2SearchItem) second).getEdge().label();
                // compare edge priorities
                return getEdgePriority(firstLabel)
                    - getEdgePriority(secondLabel);
            } else {
                return 0;
            }
        }

        /**
         * Returns the priority of an edge, judged by its label.
         */
        private int getEdgePriority(Label edgeLabel) {
            Integer result = this.priorities.get(edgeLabel);
            if (result == null) {
                return 0;
            } else {
                return result;
            }
        }

        /**
         * The priorities assigned to labels, on the basis of the list of labels
         * passed in at construction time.
         */
        private final Map<Label,Integer> priorities;
    }

    /**
     * Comparator determining the ordering in which the search item comparators
     * should be applied. Comparators will be applied in increating order, so
     * the comparators should be ordered in decreasing priority.
     * @author Arend Rensink
     * @version $Revision$
     */
    static class ItemComparatorComparator implements
            Comparator<Comparator<SearchItem>> {
        /** Empty constructor with the correct visibility. */
        ItemComparatorComparator() {
            // empty
        }

        /**
         * Returns the difference in ratings between the two comparators. This
         * means lower-rated comparators are ordered first.
         */
        public int compare(Comparator<SearchItem> o1, Comparator<SearchItem> o2) {
            return getRating(o1) - getRating(o2);
        }

        /**
         * Comparators are rated as follows, in increasing order:
         * <ul>
         * <li> {@link NeededPartsComparator}
         * <li> {@link ItemTypeComparator}
         * <li> {@link ConnectedPartsComparator}
         * <li> {@link FrequencyComparator}
         * <li> {@link IndegreeComparator}
         * </ul>
         */
        private int getRating(Comparator<SearchItem> comparator) {
            int result = 0;
            Class<?> compClass = comparator.getClass();
            if (compClass == NeededPartsComparator.class) {
                return result;
            }
            result++;
            if (compClass == ItemTypeComparator.class) {
                return result;
            }
            result++;
            if (compClass == ConnectedPartsComparator.class) {
                return result;
            }
            result++;
            if (compClass == FrequencyComparator.class) {
                return result;
            }
            result++;
            if (compClass == IndegreeComparator.class) {
                return result;
            }
            throw new IllegalArgumentException(String.format(
                "Unknown comparator class %s", compClass));
        }
    }
}
