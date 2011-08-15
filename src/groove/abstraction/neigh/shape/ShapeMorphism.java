/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.abstraction.neigh.shape;

import gnu.trove.THashSet;
import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.trans.HostEdge;
import groove.trans.HostGraphMorphism;
import groove.trans.HostNode;

import java.util.Map;
import java.util.Set;

/**
 * Morphism between shapes.
 * @author Arend Rensink
 */
public class ShapeMorphism extends HostGraphMorphism {

    /**
     * Creates a shape morphism with a given element factory.
     */
    public ShapeMorphism(ShapeFactory factory) {
        super(factory);
    }

    @Override
    public ShapeMorphism clone() {
        return (ShapeMorphism) super.clone();
    }

    @Override
    public ShapeMorphism newMap() {
        return new ShapeMorphism(getFactory());
    }

    @Override
    public ShapeMorphism then(Morphism<HostNode,HostEdge> other) {
        return (ShapeMorphism) super.then(other);
    }

    @Override
    public ShapeMorphism inverseThen(Morphism<HostNode,HostEdge> other) {
        return (ShapeMorphism) super.inverseThen(other);
    }

    @Override
    public ShapeNode getNode(Node key) {
        return (ShapeNode) super.getNode(key);
    }

    @Override
    public ShapeEdge getEdge(HostEdge key) {
        return (ShapeEdge) super.getEdge(key);
    }

    @Override
    public ShapeNode putNode(HostNode key, HostNode layout) {
        return (ShapeNode) super.putNode(key, layout);
    }

    @Override
    public ShapeEdge putEdge(HostEdge key, HostEdge layout) {
        return (ShapeEdge) super.putEdge(key, layout);
    }

    @Override
    public ShapeNode removeNode(HostNode key) {
        return (ShapeNode) super.removeNode(key);
    }

    @Override
    public ShapeEdge removeEdge(HostEdge key) {
        return (ShapeEdge) super.removeEdge(key);
    }

    @Override
    public ShapeFactory getFactory() {
        return (ShapeFactory) super.getFactory();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<ShapeNode> getPreImages(HostNode node) {
        assert node instanceof ShapeNode;
        return (Set<ShapeNode>) super.getPreImages(node);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<ShapeEdge> getPreImages(HostEdge edge) {
        assert edge instanceof ShapeEdge;
        return (Set<ShapeEdge>) super.getPreImages(edge);
    }

    /**
     * Creates and returns an identity shape morphism between the two given
     * shapes. Used during the materialisation phase.
     * Fails on an assertion if the given shapes are not identical.
     */
    public static ShapeMorphism createIdentityMorphism(Shape from, Shape to) {
        ShapeMorphism result = from.getFactory().createMorphism();
        for (ShapeNode node : from.nodeSet()) {
            assert to.nodeSet().contains(node);
            result.putNode(node, node);
        }
        for (ShapeEdge edge : from.edgeSet()) {
            assert to.edgeSet().contains(edge);
            result.putEdge(edge, edge);
        }
        return result;
    }

    /**
     * Remove edges from the morphism that are not part of the given shape.
     * This happens in the materialisation phase when the morphism contains
     * mappings of possible new edges that were not included in the final shape.
     */
    public void removeInvalidEdgeKeys(Shape from) {
        Set<HostEdge> invalidKeys = new THashSet<HostEdge>();
        Map<HostEdge,HostEdge> edgeMap = this.edgeMap();
        for (HostEdge key : edgeMap.keySet()) {
            if (!from.edgeSet().contains(key)) {
                invalidKeys.add(key);
            }
        }
        for (HostEdge invalidKey : invalidKeys) {
            edgeMap.remove(invalidKey);
        }
    }

    /**
     * Implements the conditions of a shape morphism given on Def. 11, page 14.
     */
    public boolean isConsistent(Shape from, Shape to) {
        // As in the paper, let shape 'from' be S and shape 'to be T.

        // Check for item 1.
        boolean complyToEquivClass = true;
        ecLoop: for (EquivClass<ShapeNode> ecS : from.getEquivRelation()) {
            if (ecS.size() > 1) {
                EquivClass<ShapeNode> ecT = null;
                for (ShapeNode nodeS : ecS) {
                    EquivClass<ShapeNode> otherEcT =
                        to.getEquivClassOf(this.getNode(nodeS));
                    if (ecT == null) {
                        ecT = otherEcT;
                    }
                    if (!ecT.equals(otherEcT)) {
                        complyToEquivClass = false;
                        break ecLoop;
                    }
                }
            }
        }

        // Check for item 2.
        boolean complyToNodeMult = true;
        if (complyToEquivClass) {
            for (ShapeNode nodeT : to.nodeSet()) {
                Multiplicity nodeTMult = to.getNodeMult(nodeT);
                Set<ShapeNode> nodesS = this.getPreImages(nodeT);
                Multiplicity sum = from.getNodeSetMultSum(nodesS);
                if (!nodeTMult.equals(sum)) {
                    complyToNodeMult = false;
                    break;
                }
            }
        }

        // Check for item 3.
        boolean complyToEdgeMult = true;
        if (complyToEquivClass && complyToNodeMult) {
            // EDUARDO: Finish this...
        }

        return complyToEquivClass && complyToNodeMult && complyToEdgeMult;
    }
}
