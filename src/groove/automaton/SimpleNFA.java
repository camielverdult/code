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
 * $Id: MatrixAutomaton.java,v 1.13 2008-01-30 09:32:26 iovka Exp $
 */
package groove.automaton;

import static groove.graph.Direction.INCOMING;
import static groove.graph.Direction.OUTGOING;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.rule.LabelVar;
import groove.grammar.rule.RuleLabel;
import groove.grammar.rule.Valuation;
import groove.grammar.type.TypeElement;
import groove.grammar.type.TypeFactory;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeGuard;
import groove.grammar.type.TypeLabel;
import groove.graph.Direction;
import groove.graph.Edge;
import groove.graph.ElementFactory;
import groove.graph.NodeSetEdgeSetGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of regular automata that also keeps track of the valuation
 * of the variables.
 */
public class SimpleNFA extends NodeSetEdgeSetGraph<RegNode,RegEdge> implements RegAut {
    /** Constructor for the singleton prototype object. */
    private SimpleNFA() {
        super("prototype");
        this.start = null;
        this.end = null;
        this.typeGraph = null;
        this.dfas = null;
    }

    /**
     * Creates an automaton with a given start and end node, which does not
     * accept the empty word.
     * The type graph indicates which labels to expect (which is used
     * to predict the matching of wildcards).
     */
    private SimpleNFA(RegNode start, RegNode end, TypeGraph typeGraph) {
        super("automaton");
        this.start = start;
        this.end = end;
        this.typeGraph = typeGraph;
        assert typeGraph != null;
        addNode(start);
        addNode(end);
        this.dfas = new EnumMap<Direction,Map<List<TypeLabel>,DFA>>(Direction.class);
    }

    @Override
    public RegAut newAutomaton(RegNode start, RegNode end, TypeGraph typeGraph) {
        return new SimpleNFA(start, end, typeGraph);
    }

    /** 
     * Regular automata are created to have disjoint node sets,
     * so fresh nodes should not be generated by the automaton itself.
     */
    @Override
    public RegNode addNode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RegNode getStartNode() {
        return this.start;
    }

    @Override
    public RegNode getEndNode() {
        return this.end;
    }

    @Override
    public boolean isAcceptsEmptyWord() {
        return this.acceptsEmptyWord;
    }

    @Override
    public void setAcceptsEmptyWord(boolean acceptsEmptyWord) {
        this.acceptsEmptyWord = acceptsEmptyWord;
    }

    @Override
    public void setEndNode(RegNode endNode) {
        this.end = endNode;
    }

    @Override
    public void setStartNode(RegNode startNode) {
        this.start = startNode;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer(super.toString());
        result.append("\nStart node: " + getStartNode());
        result.append("\nEnd node: " + getEndNode());
        result.append("\nAccepts empty word: " + isAcceptsEmptyWord());
        return result.toString();
    }

    @Override
    public boolean setFixed() {
        boolean result = super.setFixed();
        if (result) {
            // when the graph is fixed, we can initialise the auxiliary structures.
            for (Direction dir : Direction.values()) {
                this.dfas.put(dir, new HashMap<List<TypeLabel>,DFA>());
            }
            this.labelVars = computeLabelVars();
        }
        return result;
    }

    /** 
     * Returns the minimal DFA for a given direction and valuation.
     * @param dir the direction in which the DFA should traverse its input
     * @param valuation valuation for all label variables occurring in this NFA.
     * May be {@code null} if the NFA does not contain label variables.
     */
    public DFA getDFA(Direction dir, Valuation valuation) {
        testFixed(true);
        if (valuation == null) {
            valuation = Valuation.EMPTY;
        }
        Map<List<TypeLabel>,DFA> dfaMap = this.dfas.get(dir);
        List<TypeLabel> varImages = getVarImages(valuation);
        DFA result = dfaMap.get(varImages);
        if (result == null) {
            dfaMap.put(varImages, result = computeDFA(dir, valuation));
        }
        return result;
    }

    /** Creates a normalised automaton for exploration in a given direction. */
    private DFA computeDFA(Direction dir, Valuation valuation) {
        DFA result =
            new DFA(dir, dir == OUTGOING ? getStartNode() : getEndNode(), isAcceptsEmptyWord());
        // set of unexplored states
        Set<DFAState> unexplored = new HashSet<DFAState>();
        unexplored.add(result.getStartState());
        do {
            Iterator<DFAState> iter = unexplored.iterator();
            DFAState current = iter.next();
            iter.remove();
            // mapping from type labels to target nodes, per direction
            Map<Direction,Map<TypeLabel,Set<RegNode>>> succMaps =
                new EnumMap<Direction,Map<TypeLabel,Set<RegNode>>>(Direction.class);
            // initialise the maps
            for (Direction edgeDir : Direction.values()) {
                succMaps.put(edgeDir, new HashMap<TypeLabel,Set<RegNode>>());
            }
            // collect the transitions of all nodes contained in the state
            for (RegNode rn : current.getNodes()) {
                for (RegEdge re : dir.edges(this, rn)) {
                    RuleLabel rel = re.label();
                    Set<TypeLabel> matches = getMatchingLabels(rel);
                    Map<TypeLabel,Set<RegNode>> succMap =
                        succMaps.get(rel.isInv() ? dir.getInverse() : dir);
                    RegNode opposite = dir.opposite(re);
                    TypeGuard guard = rel.getWildcardGuard();
                    if (guard == null || !guard.isNamed()) {
                        for (TypeLabel l : matches) {
                            addToImages(succMap, l, opposite);
                        }
                    } else {
                        TypeLabel l = valuation.get(guard.getVar()).label();
                        if (matches.contains(l)) {
                            addToImages(succMap, l, opposite);
                        }
                    }
                }
            }
            for (Direction d : Direction.values()) {
                for (Map.Entry<TypeLabel,Set<RegNode>> le : succMaps.get(d).entrySet()) {
                    Set<RegNode> ns = le.getValue();
                    DFAState target = result.getState(ns);
                    if (target == null) {
                        RegNode finalNode = dir == OUTGOING ? getEndNode() : getStartNode();
                        target = result.addState(ns, ns.contains(finalNode));
                        unexplored.add(target);
                    }
                    current.addSuccessor(d, le.getKey(), target);
                }
            }
        } while (!unexplored.isEmpty());
        return result.toMinimised();
    }

    /** Extracts the type labels from the type elements matching a given rule label. */
    private Set<TypeLabel> getMatchingLabels(RuleLabel label) {
        Set<TypeLabel> result = new HashSet<TypeLabel>();
        for (TypeElement type : this.typeGraph.getMatches(label)) {
            result.add(type.label());
        }
        return result;
    }

    private <K> void addToImages(Map<K,Set<RegNode>> map, K key, RegNode node) {
        Set<RegNode> images = map.get(key);
        if (images == null) {
            map.put(key, images = new HashSet<RegNode>());
        }
        images.add(node);
    }

    /** Extracts the images of the label variables used in this automaton. */
    private List<TypeLabel> getVarImages(Valuation valuation) {
        List<TypeLabel> result;
        List<LabelVar> labelVars = getLabelVars();
        if (labelVars.isEmpty()) {
            result = EMPTY_LABEL_LIST;
        } else {
            result = new ArrayList<TypeLabel>(labelVars.size());
            for (LabelVar v : labelVars) {
                TypeElement e = valuation.get(v);
                assert e != null;
                result.add(e.label());
            }
        }
        return result;
    }

    /** Returns the list of label variables occurring in this automaton. */
    private List<LabelVar> getLabelVars() {
        return this.labelVars;
    }

    /** Computes the list of label variables occurring in this automaton. */
    private List<LabelVar> computeLabelVars() {
        List<LabelVar> result = new ArrayList<LabelVar>();
        for (RegEdge e : edgeSet()) {
            RuleLabel label = e.label();
            if (label.isWildcard() && label.getWildcardGuard().isNamed()) {
                result.add(label.getWildcardGuard().getVar());
            }
        }
        return result;
    }

    @Override
    public ElementFactory<RegNode,RegEdge> getFactory() {
        return RegFactory.instance();
    }

    @Override
    public boolean accepts(List<String> word) {
        assert isFixed();
        DFA dfa = getDFA(OUTGOING, null);
        DFAState dfaState = dfa.getStartState();
        String invOp = "" + RegExpr.INV_OPERATOR;
        TypeFactory typeFactory = getTypeGraph().getFactory();
        for (String letter : word) {
            boolean inverse = letter.startsWith(invOp);
            if (inverse) {
                letter = letter.substring(invOp.length());
            }
            TypeLabel label = typeFactory.createLabel(letter);
            dfaState = dfaState.getLabelMap().get(inverse ? INCOMING : OUTGOING).get(label);
            if (dfaState == null) {
                break;
            }
        }
        return dfaState != null && dfaState.isFinal();
    }

    @Override
    public Set<Result> getMatches(HostGraph graph, HostNode startImage, HostNode endImage,
            Valuation valuation) {
        assert isFixed();
        if (valuation == null) {
            valuation = Valuation.EMPTY;
        }
        HostNode fromNode, toNode;
        Direction dir;
        if (endImage == null || startImage != null) {
            dir = OUTGOING;
            fromNode = startImage;
            toNode = endImage;
        } else {
            dir = INCOMING;
            fromNode = endImage;
            toNode = startImage;
        }
        DFA normalAut = getDFA(dir, valuation);
        return normalAut.getRecogniser(graph).getMatches(fromNode, toNode);
    }

    @Override
    public Set<Result> getMatches(HostGraph graph, HostNode startImage, HostNode endImage) {
        return getMatches(graph, startImage, endImage, null);
    }

    @Override
    public Set<TypeElement> getAlphabet() {
        assert isFixed();
        Set<TypeElement> result = new HashSet<TypeElement>();
        for (RegEdge edge : edgeSet()) {
            result.addAll(this.typeGraph.getMatches(edge.label()));
        }
        return result;
    }

    @Override
    protected boolean isTypeCorrect(Edge edge) {
        boolean result = edge instanceof RegEdge;
        if (result) {
            RuleLabel edgeLabel = ((RegEdge) edge).label();
            if (edgeLabel.isInv()) {
                edgeLabel = edgeLabel.getInvLabel();
            }
            result = edgeLabel.isWildcard() || edgeLabel.isSharp() || edgeLabel.isAtom();
        }
        return result;
    }

    /**
     * The start node of the automaton.
     */
    private RegNode start;
    /**
     * The end node of the automaton.
     */
    private RegNode end;
    /**
     * Flag to indicate that the automaton is to accept the empty word.
     */
    private boolean acceptsEmptyWord;
    /** Map from exploration directions to the initial state of the normalised automaton. */
    private final Map<Direction,Map<List<TypeLabel>,DFA>> dfas;
    /** List of label variables occurring in this automaton. */
    private List<LabelVar> labelVars;

    @Override
    public final TypeGraph getTypeGraph() {
        return this.typeGraph;
    }

    /** Type graph to be matched against. */
    private final TypeGraph typeGraph;

    private static final List<TypeLabel> EMPTY_LABEL_LIST = Collections.emptyList();

    /** Prototype object for simple automata. */
    public static final SimpleNFA PROTOTYPE = new SimpleNFA();
}
