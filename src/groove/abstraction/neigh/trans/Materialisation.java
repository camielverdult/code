/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.abstraction.neigh.trans;

import static groove.abstraction.neigh.Multiplicity.EdgeMultDir.INCOMING;
import static groove.abstraction.neigh.Multiplicity.EdgeMultDir.OUTGOING;
import static groove.abstraction.neigh.Multiplicity.MultKind.EDGE_MULT;
import static groove.abstraction.neigh.Multiplicity.MultKind.NODE_MULT;
import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.MyHashMap;
import groove.abstraction.neigh.MyHashSet;
import groove.abstraction.neigh.Parameters;
import groove.abstraction.neigh.PowerSetIterator;
import groove.abstraction.neigh.Util;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.equiv.EquivRelation;
import groove.abstraction.neigh.gui.dialog.ShapePreviewDialog;
import groove.abstraction.neigh.match.PreMatch;
import groove.abstraction.neigh.shape.EdgeSignature;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeMorphism;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.abstraction.neigh.trans.EquationSystem.EdgeBundle;
import groove.graph.TypeLabel;
import groove.trans.BasicEvent;
import groove.trans.GraphGrammar;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.Proof;
import groove.trans.Rule;
import groove.trans.RuleApplication;
import groove.trans.RuleEdge;
import groove.trans.RuleEvent;
import groove.trans.RuleNode;
import groove.trans.SystemRecord;
import groove.util.Pair;
import groove.view.FormatException;
import groove.view.GrammarModel;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class represents an attempt to materialise a certain shape, driven by
 * a certain pre-match of a rule into the shape. We assume that the reader is
 * familiar with the concepts of shape abstraction, in particular Sect. 6
 * (pg 27) of the Technical Report "Graph Abstraction and Abstract Graph
 * Transformation".
 * 
 * The constructors of this class are all private. This means that it is not
 * possible to freely create objects of this class. The only way to do so is
 * by calling the static method getMaterialisations().
 * 
 * WARNING: Beware of the code in this class. It's rather tricky.
 * 
 * @author Eduardo Zambon
 */
public final class Materialisation {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    private int stage;

    /**
     * The shape we are trying to materialise.
     * The field is final but the shape is modified by the materialisation.
     */
    private final Shape shape;
    /**
     * The original shape that started the materialisation process.
     * This is left unchanged during the materialisation.
     */
    private final Shape originalShape;
    /**
     * The morphism from the (partially) materialised shape into the original
     * shape.
     * The field is final but the morphism is modified by the materialisation.
     */
    private final ShapeMorphism morph;
    /**
     * The matched rule.
     */
    private final Rule matchedRule;
    /**
     * A copy of the original match of the rule into the original shape.
     * This is left unchanged during the materialisation.
     */
    private final RuleToShapeMap originalMatch;
    /**
     * The concrete match of the rule into the (partially) materialised shape.
     * The field is final but the match is modified by the materialisation.
     */
    private final RuleToShapeMap match;

    // ------------------------------------------------------------------------
    // Used in first stage.
    // ------------------------------------------------------------------------

    /**
     * Auxiliary set that contains all newly materialised nodes.
     */
    private Set<ShapeNode> matNodes;
    /**
     * Auxiliary set that contains all newly materialised edges.
     */
    private Set<ShapeEdge> matEdges;
    /**
     * Auxiliary set of possible new edges that should be included in the 
     * shape that is being materialised.
     */
    private Set<ShapeEdge> possibleEdges;

    private Set<EdgeBundle> bundles;

    // ------------------------------------------------------------------------
    // Used in second stage.
    // ------------------------------------------------------------------------

    /**
     * Auxiliary set that contains non-singular bundles.
     */
    private Set<EdgeBundle> nonSingBundles;
    /**
     * Map from nodes to their split copies.
     */
    private Map<ShapeNode,Set<ShapeNode>> nodeSplitMap;
    /**
     * Map from nodes to their split bundles.
     */
    private Map<ShapeNode,Set<EdgeBundle>> bundleSplitMap;

    // ------------------------------------------------------------------------
    // Used in third stage.
    // ------------------------------------------------------------------------

    /**
     * Auxiliary set that contains nodes that must be garbage collected at
     * the end of materialisation.
     */
    private Set<ShapeNode> garbageNodes;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs the initial materialisation object given a shape and a
     * pre-match of a rule into the shape. The pre-match given must be valid.
     */
    private Materialisation(Shape shape, Proof preMatch) {
        this.stage = 1;
        this.originalShape = shape;
        this.shape = this.originalShape.clone();
        this.morph =
            ShapeMorphism.createIdentityMorphism(this.shape, this.originalShape);
        this.matchedRule = preMatch.getRule();
        this.originalMatch = (RuleToShapeMap) preMatch.getPatternMap();
        // Fix the original match to prevent modification.
        this.originalMatch.setFixed();
        this.match = this.originalMatch.clone();
        // Create new auxiliary structures.
        this.matNodes = new MyHashSet<ShapeNode>();
        this.matEdges = new MyHashSet<ShapeEdge>();
        this.possibleEdges = new MyHashSet<ShapeEdge>();
        this.bundles = new MyHashSet<EdgeBundle>();
    }

    /**
     * Copying constructor. Clones the structures of the given materialisation
     * object that can be modified. 
     */
    private Materialisation(Materialisation mat) {
        this.stage = mat.stage;
        // No need to clone the original objects since they are fixed.
        this.originalShape = mat.originalShape;
        this.originalMatch = mat.originalMatch;
        this.matchedRule = mat.matchedRule;
        // The match should also be fixed.
        assert mat.match.isFixed();
        this.match = mat.match;
        // Clone the shape.
        this.shape = mat.shape.clone();
        // Since the shape can still be modified, we also need to clone the
        // morphism into the original shape.
        this.morph = mat.morph.clone();
        if (mat.stage == 1) {
            this.bundles = new MyHashSet<EdgeBundle>();
            for (EdgeBundle matBundle : mat.bundles) {
                this.bundles.add(matBundle.clone());
            }
        } else if (mat.stage == 2) {
            // We can just use the same references at this stage.
            this.nodeSplitMap = mat.nodeSplitMap;
            this.bundleSplitMap = mat.bundleSplitMap;
        } else if (mat.stage == 3) {
            // We can just use the same references at this stage.
            this.garbageNodes = mat.garbageNodes;
        }
    }

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /**
     * Constructs and returns the set of all possible materialisations of the
     * given shape and pre-match. This method resolves all non-determinism
     * in the materialisation phase, so the shapes in the returned
     * materialisations are ready to be transformed by conventional rule
     * application.
     */
    public static Set<Materialisation> getMaterialisations(Shape shape,
            Proof preMatch) {
        Materialisation initialMat = new Materialisation(shape, preMatch);
        return initialMat.compute();
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Materialisation:\nShape:\n" + this.shape + "Match: "
            + this.match + "\n";
    }

    @Override
    public Materialisation clone() {
        return new Materialisation(this);
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Common methods to all stages.
    // ------------------------------------------------------------------------

    /** Basic getter method. */
    public int getStage() {
        return this.stage;
    }

    /** Basic getter method. */
    public Shape getShape() {
        return this.shape;
    }

    /** Basic getter method. */
    public Shape getOriginalShape() {
        return this.originalShape;
    }

    /** Basic getter method. */
    public RuleToShapeMap getMatch() {
        return this.match;
    }

    /** Basic getter method. */
    public RuleToShapeMap getOriginalMatch() {
        return this.originalMatch;
    }

    /** Basic getter method. */
    ShapeMorphism getShapeMorphism() {
        return this.morph;
    }

    /** Returns true if this edge is in the co-domain of the match. */
    boolean isFixed(ShapeEdge edge) {
        return !this.match.getPreImages(edge).isEmpty();
    }

    /**
     * Removes elements from the morphism that are no longer present in the
     * shape.
     */
    void updateShapeMorphism() {
        Set<HostNode> nodesToRemove = new MyHashSet<HostNode>();
        for (HostNode key : this.morph.nodeMap().keySet()) {
            if (!this.shape.containsNode(key)) {
                nodesToRemove.add(key);
            }
        }
        for (HostNode nodeToRemove : nodesToRemove) {
            this.morph.removeNode(nodeToRemove);
        }
        Set<HostEdge> edgesToRemove = new MyHashSet<HostEdge>();
        for (HostEdge key : this.morph.edgeMap().keySet()) {
            if (!this.shape.containsEdge(key)) {
                edgesToRemove.add(key);
            }
        }
        for (HostEdge edgeToRemove : edgesToRemove) {
            this.morph.removeEdge(edgeToRemove);
        }
    }

    /**
     * Returns true if the morphism from the materialised shape to the original
     * one is consistent.
     */
    boolean isShapeMorphConsistent() {
        return this.morph.isValid(this.shape, this.originalShape)
            && this.morph.isConsistent(this.shape, this.originalShape);
    }

    /**
     * Applies the rule match defined by this materialisation and returns the
     * transformed shape, which is not yet normalised.
     */
    public Pair<Shape,RuleEvent> applyMatch(SystemRecord record) {
        assert this.hasConcreteMatch();
        RuleEvent event = new BasicEvent(this.matchedRule, this.match, false);
        RuleApplication app = new RuleApplication(event, this.shape);
        Shape result = (Shape) app.getTarget();
        return new Pair<Shape,RuleEvent>(result, event);
    }

    /**
     * Checks if the match in the materialisation is concrete. See items 3, 4,
     * and 5 of Def. 35 in pg. 21 of the technical report. 
     * @return true if all three items are satisfied; false, otherwise.
     */
    private boolean hasConcreteMatch() {
        assert this.match.isConsistent();
        // Check for items 3 and 4.
        boolean complyToNodeMult = true;
        boolean complyToEquivClass = true;
        Multiplicity one = Multiplicity.getMultiplicity(1, 1, NODE_MULT);
        // For all nodes in the image of the LHS.
        for (ShapeNode nodeS : this.match.nodeMapValueSet()) {
            // Item 3: check that all nodes in the image of the LHS have
            // multiplicity one.
            if (!this.shape.getNodeMult(nodeS).equals(one)) {
                complyToNodeMult = false;
                break;
            }
            // Item 4: check that for all nodes in the image of the LHS, their
            // equivalence class is a singleton set.
            if (!this.shape.getEquivClassOf(nodeS).isSingleton()) {
                complyToEquivClass = false;
                break;
            }
        }

        // Item 5: check that for any two nodes in the image of the LHS, their
        // outgoing and incoming multiplicities are equal and correspond to
        // the number of edges in the underlying graph structure of the shape.
        boolean complyToEdgeMult = true;
        if (complyToNodeMult && complyToEquivClass) {
            // For all binary labels.
            for (TypeLabel label : Util.getBinaryLabels(this.shape)) {
                // For all nodes v in the image of the LHS.
                for (ShapeNode v : this.match.nodeMapValueSet()) {
                    // For all nodes w in the image of the LHS.
                    for (ShapeNode w : this.match.nodeMapValueSet()) {
                        EquivClass<ShapeNode> ecW =
                            this.shape.getEquivClassOf(w);
                        EdgeSignature outEs =
                            this.shape.getEdgeSignature(EdgeMultDir.OUTGOING,
                                v, label, ecW);
                        Multiplicity outMult = this.shape.getEdgeSigMult(outEs);
                        EdgeSignature inEs =
                            this.shape.getEdgeSignature(EdgeMultDir.INCOMING,
                                v, label, ecW);
                        Multiplicity inMult = this.shape.getEdgeSigMult(inEs);
                        Set<HostEdge> vInterW =
                            Util.getIntersectEdges(this.shape, v, w, label);
                        Multiplicity vInterWMult =
                            Multiplicity.getEdgeSetMult(vInterW);
                        Set<HostEdge> wInterV =
                            Util.getIntersectEdges(this.shape, w, v, label);
                        Multiplicity wInterVMult =
                            Multiplicity.getEdgeSetMult(wInterV);
                        if (!outMult.equals(vInterWMult)
                            || !inMult.equals(wInterVMult)) {
                            complyToEdgeMult = false;
                            break;
                        }
                    }
                }
            }
        }

        return complyToNodeMult && complyToEquivClass && complyToEdgeMult;
    }

    // ------------------------------------------------------------------------
    // Methods for first stage.
    // ------------------------------------------------------------------------

    /** Returns the set of bundles for the first stage.*/
    Set<EdgeBundle> getBundles() {
        assert this.stage == 1;
        return this.bundles;
    }

    /** Returns the set of edges involved in the materialisation. */
    Set<ShapeEdge> getAffectedEdges() {
        assert this.stage == 1;
        Set<ShapeEdge> result = new MyHashSet<ShapeEdge>();
        result.addAll(this.matEdges);
        result.addAll(this.possibleEdges);
        return result;
    }

    /** Records the materialisation of the new node from the collector node. */
    public void addMatNode(ShapeNode newNode, ShapeNode collectorNode,
            RuleNode nodeR) {
        assert this.stage == 1;
        assert this.shape.containsNode(newNode);
        this.match.putNode(nodeR, newNode);
        this.morph.putNode(newNode, collectorNode);
        this.matNodes.add(newNode);
    }

    /** Records the materialisation of the new edge from the inconsistent edge. */
    public void addMatEdge(ShapeEdge newEdge, ShapeEdge inconsistentEdge,
            RuleEdge edgeR) {
        assert this.stage == 1;
        assert this.shape.containsEdge(newEdge);
        this.match.putEdge(edgeR, newEdge);
        this.morph.putEdge(newEdge, inconsistentEdge);
        this.matEdges.add(newEdge);
    }

    /** Updates the proper sets with the given collector node. */
    public void handleCollectorNode(ShapeNode collectorNode) {
        assert this.stage == 1;
        assert this.shape.containsNode(collectorNode);
        this.matNodes.add(collectorNode);
    }

    /** Updates the proper sets with the given inconsistent edge. */
    public void handleInconsistentEdge(ShapeEdge inconsistentEdge) {
        assert this.stage == 1;
        if (!this.shape.containsEdge(inconsistentEdge)) {
            // The edge was removed from the shape and we have a
            // dangling reference in the shape morphism.
            this.morph.removeEdge(inconsistentEdge);
        } else {
            this.matEdges.add(inconsistentEdge);
        }
    }

    /**
     * Adds the given edge to the list of possible edges and updates the
     * morphism to the original edge.
     */
    public void addPossibleEdge(ShapeEdge possibleEdge, ShapeEdge origEdge) {
        assert this.stage == 1 || this.stage == 2;
        assert !this.shape.containsEdge(possibleEdge);
        this.possibleEdges.add(possibleEdge);
        this.morph.putEdge(possibleEdge, origEdge);
    }

    private Set<Materialisation> compute() {
        assert this.stage == 1;
        // Search for nodes in the original match image that have to be
        // materialised. 
        for (ShapeNode nodeS : this.originalMatch.nodeMapValueSet()) {
            if (this.originalShape.getNodeMult(nodeS).isCollector()) {
                // We have a rule node that was matched to a collector
                // node. We need to materialise this collector node.
                this.shape.materialiseNode(this, nodeS);
            }
        }

        // Search for edges in the match image that have to be materialised. 
        for (ShapeEdge edgeS : this.match.getInconsistentEdges()) {
            // This edge was affected by a node materialisation.
            // We have to materialise the edge as well.
            this.shape.materialiseEdge(this, edgeS);
        }

        assert this.match.isConsistent();
        this.match.setFixed();

        // Create the set of possible edges that can occur on the final shape.
        this.createPossibleEdges(this.morph.clone(), this.shape,
            this.originalShape, this.matNodes);

        // Make sure that all shape nodes in the match image are in a
        // singleton equivalence class.
        for (ShapeNode nodeS : this.match.nodeMapValueSet()) {
            if (!this.shape.getEquivClassOf(nodeS).isSingleton()) {
                // We have a rule node that was mapped to a shape node that is
                // not in its own equivalence class. We need to singularise the
                // shape node.
                this.shape.singulariseNode(this, nodeS);
            }
        }

        // The deterministic steps of the materialisation are done.
        // Create a new equation system for this materialisation and return
        // the resulting materialisations from the solution.
        ResultSet result = new ResultSet();
        EquationSystem.newEqSys(this).solve(result);

        return result;
    }

    /**
     * Duplicates all incoming and outgoing edges for the materialised nodes.
     * These edges are not added to the shape since they may not be present
     * in the final configuration. For now they are put in a set of possible
     * edges and later used by the equation system to decide on the final
     * configuration of the shape.
     */
    private void createPossibleEdges(ShapeMorphism morph, Shape from, Shape to,
            Set<ShapeNode> nodes) {
        assert this.stage == 1 || this.stage == 2;
        if (nodes.isEmpty()) {
            return;
        }
        // Fix the morphism because we need pre-images.
        morph.setFixed();
        // For all nodes involved in the materialisation.
        for (ShapeNode node : nodes) {
            // Get the original node from the shape morphism.
            ShapeNode origNode = morph.getNode(node);
            for (EdgeMultDir direction : EdgeMultDir.values()) {
                // Look for all edges connected to the original node in the
                // original shape.     
                for (ShapeEdge origEdge : to.binaryEdgeSet(origNode, direction)) {
                    ShapeNode origOppNode = origEdge.opposite(direction);
                    for (ShapeNode oppNode : morph.getPreImages(origOppNode)) {
                        ShapeEdge possibleEdge =
                            from.createEdge(node, oppNode, origEdge.label(),
                                direction);
                        if (!from.containsEdge(possibleEdge)) {
                            this.addPossibleEdge(possibleEdge, origEdge);
                        }
                    }
                }
            }
        }
    }

    /** Basic setter method. */
    void setNonSingBundles(MyHashSet<EdgeBundle> nonSingBundles) {
        assert this.stage == 1;
        assert this.nonSingBundles == null;
        this.nonSingBundles = nonSingBundles;
    }

    void removeUnconnectedNode(ShapeNode nodeToRemove) {
        assert this.stage == 1;
        this.morph.removeNode(nodeToRemove);
        this.shape.removeNode(nodeToRemove);
        for (EdgeBundle bundle : this.nonSingBundles) {
            Iterator<ShapeEdge> iter = bundle.possibleEdges.iterator();
            while (iter.hasNext()) {
                ShapeEdge edge = iter.next();
                if (edge.source().equals(nodeToRemove)
                    || edge.target().equals(nodeToRemove)) {
                    iter.remove();
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // Methods for second stage.
    // ------------------------------------------------------------------------

    void moveToSecondStage() {
        assert this.stage == 1;
        assert this.nonSingBundles != null;

        this.stage++;
        this.matEdges = null;
        this.matNodes = new MyHashSet<ShapeNode>();
        this.possibleEdges = new MyHashSet<ShapeEdge>();
        // Map from original nodes to their split nodes.
        this.nodeSplitMap = new MyHashMap<ShapeNode,Set<ShapeNode>>();
        // Map from split nodes to their split bundles.
        this.bundleSplitMap = new MyHashMap<ShapeNode,Set<EdgeBundle>>();

        // Compute the set of nodes that need splitting.
        for (EdgeBundle nonSingBundle : this.nonSingBundles) {
            ShapeNode node = nonSingBundle.node;
            Set<EdgeBundle> bundleSet = this.bundleSplitMap.get(node);
            if (bundleSet == null) {
                bundleSet = new MyHashSet<EdgeBundle>();
                this.bundleSplitMap.put(node, bundleSet);
            }
            bundleSet.add(nonSingBundle);
        }

        Shape shape = this.getShape();
        Shape to = shape.clone();
        ShapeMorphism auxMorph =
            ShapeMorphism.createIdentityMorphism(shape, to);

        // Now split the nodes.
        for (ShapeNode origNode : this.bundleSplitMap.keySet()) {
            int copies = 1;
            for (EdgeBundle bundle : this.bundleSplitMap.get(origNode)) {
                copies =
                    (int) (copies * Math.pow(2, bundle.possibleEdges.size()));
            }
            shape.splitNode(this, origNode, copies);
            for (ShapeNode splitNode : this.nodeSplitMap.get(origNode)) {
                auxMorph.putNode(splitNode, origNode);
            }
        }

        // Create all edge permutations.
        Set<ShapeNode> toRemove = new MyHashSet<ShapeNode>();
        Set<ShapeEdge> flexEdges = new MyHashSet<ShapeEdge>();
        for (ShapeNode origNode : this.nodeSplitMap.keySet()) {
            Set<EdgeBundle> origBundles = this.bundleSplitMap.get(origNode);
            for (EdgeBundle origBundle : origBundles) {
                flexEdges.addAll(origBundle.possibleEdges);
            }
            Iterator<Set<ShapeEdge>> iter =
                new PowerSetIterator<ShapeEdge>(flexEdges, true);
            for (ShapeNode splitNode : this.nodeSplitMap.get(origNode)) {
                // Add the result of the iterator to the split node.
                if (!this.addEdges(splitNode, origNode, iter.next(),
                    origBundles)) {
                    toRemove.add(splitNode);
                }
            }
            assert !iter.hasNext();
            flexEdges.clear();
        }

        // Remove the nodes that could not be connected.
        toRemoveLoop: for (ShapeNode nodeToRemove : toRemove) {
            assert shape.isUnconnected(nodeToRemove);
            assert shape.getNodeMult(nodeToRemove).isZero();
            auxMorph.removeNode(nodeToRemove);
            this.matNodes.remove(nodeToRemove);
            shape.removeNode(nodeToRemove);
            for (Set<ShapeNode> splitSet : this.nodeSplitMap.values()) {
                if (splitSet.contains(nodeToRemove)) {
                    splitSet.remove(nodeToRemove);
                    continue toRemoveLoop;
                }
            }
        }
        this.updateBundles(toRemove);

        // Add the edges from the new bundles.
        for (ShapeNode origNode : this.nodeSplitMap.keySet()) {
            for (ShapeNode splitNode : this.nodeSplitMap.get(origNode)) {
                for (EdgeBundle bundle : this.bundleSplitMap.get(splitNode)) {
                    this.addEdges(bundle);
                }
            }
        }

        // Now add the remaining edges created by the node split that still
        // give rise to admissible configurations.
        this.createPossibleEdges(auxMorph, shape, to, this.matNodes);
        Set<ShapeEdge> edgesToAdd = new MyHashSet<ShapeEdge>();
        Set<ShapeEdge> vetoedEdges = new MyHashSet<ShapeEdge>();
        for (ShapeNode origNode : this.nodeSplitMap.keySet()) {
            for (ShapeNode splitNode : this.nodeSplitMap.get(origNode)) {
                if (!toRemove.contains(splitNode)) {
                    this.collectEdgesToAdd(splitNode, origNode, edgesToAdd,
                        vetoedEdges);
                }
            }
        }
        // Finally, add the edges we had left.
        edgesToAdd.removeAll(vetoedEdges);
        for (ShapeEdge edgeToAdd : edgesToAdd) {
            assert !shape.containsEdge(edgeToAdd);
            shape.addEdgeWithoutCheck(edgeToAdd);
            // We have to adjust the multiplicities here, in case the edge
            // signature is not used in the second stage of the equation system.
            ShapeEdge origEdge = this.morph.getEdge(edgeToAdd);
            for (EdgeMultDir direction : EdgeMultDir.values()) {
                Multiplicity origMult = to.getEdgeMult(origEdge, direction);
                shape.setEdgeMult(edgeToAdd, direction, origMult);
            }
        }
    }

    /** Records the split of the new node from the collector node. */
    public void addSplitNode(ShapeNode newNode, ShapeNode origNode) {
        assert this.stage == 2;
        assert this.shape.containsNode(origNode);
        assert this.shape.containsNode(newNode);
        this.morph.putNode(newNode, origNode);
        Set<ShapeNode> copiesSet = this.nodeSplitMap.get(origNode);
        if (copiesSet == null) {
            copiesSet = new MyHashSet<ShapeNode>();
            this.nodeSplitMap.put(origNode, copiesSet);
        }
        copiesSet.add(newNode);
        // Add the new node to the set of affected nodes.
        this.matNodes.add(newNode);
    }

    /** Returns the set of edges involved in the materialisation. */
    Set<ShapeNode> getAffectedNodes() {
        assert this.stage == 2;
        return this.bundleSplitMap.keySet();
    }

    /** Returns the split bundles from the given node. */
    Set<EdgeBundle> getSplitBundles(ShapeNode node) {
        assert this.stage == 2 || this.stage == 3;
        Set<EdgeBundle> result = this.bundleSplitMap.get(node);
        if (result == null) {
            result = Collections.emptySet();
        }
        return result;
    }

    private Set<ShapeEdge> getIncidentEdges(ShapeNode splitNode) {
        assert this.stage == 2;
        Set<ShapeEdge> result = new MyHashSet<ShapeEdge>();
        for (ShapeEdge edge : this.possibleEdges) {
            if (edge.source().equals(splitNode)
                || edge.target().equals(splitNode)) {
                result.add(edge);
            }
        }
        return result;
    }

    private void collectEdgesToAdd(ShapeNode newNode, ShapeNode origNode,
            Set<ShapeEdge> edgesToAdd, Set<ShapeEdge> vetoedEdges) {
        Set<ShapeEdge> allIncidentEdges = this.getIncidentEdges(newNode);

        Set<ShapeEdge> fixEdges = new MyHashSet<ShapeEdge>();
        Set<ShapeEdge> allBundleEdges = new MyHashSet<ShapeEdge>();
        Set<ShapeEdge> usedEdges = new MyHashSet<ShapeEdge>();
        EquivRelation<ShapeNode> er = new EquivRelation<ShapeNode>();
        for (EdgeBundle origBundle : this.bundleSplitMap.get(origNode)) {
            EdgeMultDir direction = origBundle.direction;
            // Collect the fixed edges.
            for (ShapeEdge edge : allIncidentEdges) {
                if (edge.incident(direction).equals(newNode)
                    && origBundle.getEdges().contains(this.morph.getEdge(edge))) {
                    fixEdges.add(edge);
                }
            }
            EdgeBundle newBundle = this.getNewBundle(origBundle, newNode);
            // Check if we can add the fixed edges.
            boolean addFixedEdges = true;
            Multiplicity mult = newBundle.origEsMult;
            if (!mult.isUnbounded()) {
                allBundleEdges.addAll(fixEdges);
                allBundleEdges.addAll(newBundle.allEdges);
                // Count the number of different opposite equivalence classes.
                for (ShapeEdge bundleEdge : allBundleEdges) {
                    er.add(this.shape.getEquivClassOf(bundleEdge.opposite(direction)));
                }
                int ecCount = er.size();
                Multiplicity oppMult =
                    Multiplicity.approx(ecCount, ecCount, EDGE_MULT);
                if (!oppMult.le(mult)) {
                    addFixedEdges = false;
                }
            }
            if (addFixedEdges) {
                edgesToAdd.addAll(fixEdges);
            } else {
                vetoedEdges.addAll(fixEdges);
            }
            // Adjust the sets for the next iteration of the loop.
            usedEdges.addAll(fixEdges);
            fixEdges.clear();
            allBundleEdges.clear();
            er.clear();
        }
        // Maybe we have some edges left...
        allIncidentEdges.removeAll(usedEdges);
        edgesToAdd.addAll(allIncidentEdges);
    }

    private boolean addEdges(ShapeNode newNode, ShapeNode origNode,
            Set<ShapeEdge> oldFlexEdges, Set<EdgeBundle> origBundles) {
        assert this.stage == 2;

        Map<ShapeEdge,Set<ShapeEdge>> flexEdgesMap =
            this.routeNewEdges(newNode, origNode, oldFlexEdges);
        Set<EdgeBundle> newBundles = this.bundleSplitMap.get(newNode);
        if (newBundles == null) {
            newBundles = new MyHashSet<EdgeBundle>();
            this.bundleSplitMap.put(newNode, newBundles);
        }

        for (EdgeBundle origBundle : origBundles) {
            // Create a new bundle for the new node.
            EdgeBundle newBundle =
                new EdgeBundle(origBundle.origEs, origBundle.origEsMult,
                    newNode);
            // Collect all edges associated with the new bundle.
            innerLoop: for (ShapeEdge oldFlexEdge : origBundle.possibleEdges) {
                Set<ShapeEdge> newFlexEdges = flexEdgesMap.get(oldFlexEdge);
                if (newFlexEdges == null) {
                    continue innerLoop;
                }
                for (ShapeEdge newFlexEdge : newFlexEdges) {
                    newBundle.addEdge(this.shape, newFlexEdge,
                        origBundle.direction);
                }
            }
            // Check the multiplicity.
            if (!newBundle.complyToOriginalMult()) {
                // We can't have this many incident edges on this node.
                // Nothing to do. The new node will remain unconnected and
                // will be garbage collected later...
                return false;
            }
            newBundles.add(newBundle);
        }
        // If we reached this point, we can add the bundle edges.
        /*for (EdgeBundle newBundle : newBundles) {
            this.addEdges(newBundle);
        }*/
        return true;
    }

    private Map<ShapeEdge,Set<ShapeEdge>> routeNewEdges(ShapeNode newNode,
            ShapeNode origNode, Set<ShapeEdge> oldEdges) {
        assert this.stage == 2;
        Map<ShapeEdge,Set<ShapeEdge>> result =
            new MyHashMap<ShapeEdge,Set<ShapeEdge>>();
        Set<ShapeNode> opposites = new MyHashSet<ShapeNode>();
        for (ShapeEdge oldEdge : oldEdges) {
            Set<ShapeEdge> newEdges = new MyHashSet<ShapeEdge>();
            ShapeEdge origEdge = this.morph.getEdge(oldEdge);
            assert origEdge != null;
            TypeLabel label = oldEdge.label();
            ShapeNode incident = newNode;
            EdgeMultDir direction;
            Set<ShapeNode> splitNodes;
            if (oldEdge.source().equals(origNode)) {
                direction = OUTGOING;
                opposites.add(oldEdge.target());
                splitNodes = this.nodeSplitMap.get(oldEdge.target());
            } else {
                assert oldEdge.target().equals(origNode);
                direction = INCOMING;
                opposites.add(oldEdge.source());
                splitNodes = this.nodeSplitMap.get(oldEdge.source());
            }
            if (splitNodes != null) {
                opposites.addAll(splitNodes);
            }
            for (ShapeNode newOpposite : opposites) {
                ShapeEdge newEdge =
                    this.shape.createEdge(incident, newOpposite, label,
                        direction);
                newEdges.add(newEdge);
                this.morph.putEdge(newEdge, origEdge);
            }
            opposites.clear();
            result.put(oldEdge, newEdges);
        }
        return result;
    }

    private void addEdges(EdgeBundle newBundle) {
        assert this.stage == 2;
        for (ShapeEdge newEdge : newBundle.allEdges) {
            if (!this.shape.containsEdge(newEdge)) {
                this.shape.addEdgeWithoutCheck(newEdge);
            }
            this.addEdgeSigsToBundles(newEdge);
        }
    }

    private EdgeBundle getNewBundle(EdgeBundle origBundle, ShapeNode newNode) {
        EdgeBundle result = null;
        for (EdgeBundle newBundle : this.bundleSplitMap.get(newNode)) {
            if (newBundle.isEqual(newNode, origBundle.origEs)) {
                result = newBundle;
                break;
            }
        }
        return result;
    }

    private void addEdgeSigsToBundles(ShapeEdge newEdge) {
        assert this.stage == 2;

        Shape shape = this.getShape();
        Shape origShape = this.getOriginalShape();

        for (EdgeMultDir direction : EdgeMultDir.values()) {
            ShapeNode node = newEdge.incident(direction);
            Set<EdgeBundle> splitBundles = this.bundleSplitMap.get(node);
            if (splitBundles == null) {
                splitBundles = new MyHashSet<EdgeBundle>();
                this.bundleSplitMap.put(node, splitBundles);
            }
            EdgeBundle newBundle = null;
            EdgeSignature origEs =
                this.getShapeMorphism().getEdgeSignature(origShape,
                    shape.getEdgeSignature(newEdge, direction));
            bundleLoop: for (EdgeBundle splitBundle : splitBundles) {
                if (splitBundle.isEqual(node, origEs)) {
                    newBundle = splitBundle;
                    break bundleLoop;
                }
            }
            if (newBundle == null) {
                Multiplicity origEsMult = origShape.getEdgeSigMult(origEs);
                newBundle = new EdgeBundle(origEs, origEsMult, node);
                splitBundles.add(newBundle);
            }
            newBundle.addEdge(shape, newEdge, direction);
        }
    }

    private void updateBundles(Set<ShapeNode> toRemove) {
        for (ShapeNode nodeToRemove : toRemove) {
            this.bundleSplitMap.remove(nodeToRemove);
        }
        for (ShapeNode splitNode : this.bundleSplitMap.keySet()) {
            Set<EdgeBundle> splitBundles = this.bundleSplitMap.get(splitNode);
            for (EdgeBundle splitBundle : splitBundles) {
                splitBundle.removeNodeReferences(toRemove);
            }
        }
    }

    // ------------------------------------------------------------------------
    // Methods for third stage.
    // ------------------------------------------------------------------------

    /** Moves the materialisation object to the third and last stage. */
    void moveToThirdStage() {
        assert this.stage == 2;
        this.stage++;
        this.nonSingBundles = null;
        this.garbageNodes = new MyHashSet<ShapeNode>();
        this.markGarbageNodes();
    }

    /** Basic getter method. */
    Map<ShapeNode,Set<ShapeNode>> getNodeSplitMap() {
        assert this.stage == 3;
        return this.nodeSplitMap;
    }

    /** Mark nodes that cannot exist. */
    void markGarbageNodes() {
        assert this.stage == 3;
        Shape shape = this.shape;
        Shape origShape = this.originalShape;
        this.updateShapeMorphism();
        ShapeMorphism morph = this.morph.clone();
        // We need to check for nodes that got disconnected...
        for (ShapeNode origNode : origShape.nodeSet()) {
            for (ShapeNode node : morph.getPreImages(origNode)) {
                for (EdgeSignature origEs : origShape.getEdgeSignatures(origNode)) {
                    // We know that the original signature has edges.
                    // Check if the pre-images also do.
                    if (morph.getPreImages(shape, node, origEs, false).isEmpty()) {
                        // The node got disconnected and therefore cannot exist.
                        this.garbageNodes.add(node);
                    }
                }
            }
        }
    }

    /** Remove the nodes from the shape that were marked as garbage. */
    void garbageCollectNodes() {
        if (this.stage == 3) {
            for (ShapeNode garbageNode : this.garbageNodes) {
                this.shape.removeNode(garbageNode);
            }
        }
    }

    /** Returns true if the given node is marked as garbage. */
    boolean isGarbage(ShapeNode node) {
        assert this.stage == 3;
        return this.garbageNodes.contains(node);
    }

    // ------------------------------------------------------------------------
    // Inner Classes
    // ------------------------------------------------------------------------

    // ---------
    // ResultSet
    // ---------

    private static class ResultSet extends MyHashSet<Materialisation> {
        @Override
        public boolean add(Materialisation mat) {
            mat.garbageCollectNodes();
            mat.updateShapeMorphism();
            assert mat.isShapeMorphConsistent();
            assert mat.getShape().isInvariantOK();
            return super.add(mat);
        }
    }

    /** Used for tests. */
    public static void main(String args[]) {
        String DIRECTORY = "junit/samples/abs-test.gps/";
        Parameters.setNodeMultBound(1);
        Parameters.setEdgeMultBound(1);
        Multiplicity.initMultStore();
        File file = new File(DIRECTORY);
        try {
            String number = "9";
            GrammarModel view = GrammarModel.newInstance(file, false);
            HostGraph graph =
                view.getHostModel("materialisation-test-" + number).toResource();
            Shape shape = Shape.createShape(graph);
            GraphGrammar grammar = view.toGrammar();
            Rule rule = grammar.getRule("test-mat-" + number);
            Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
            for (Proof preMatch : preMatches) {
                Set<Materialisation> mats =
                    Materialisation.getMaterialisations(shape, preMatch);
                for (Materialisation mat : mats) {
                    ShapePreviewDialog.showShape(mat.shape);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }
}
