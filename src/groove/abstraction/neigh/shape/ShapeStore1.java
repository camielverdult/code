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
package groove.abstraction.neigh.shape;

import static groove.abstraction.neigh.Multiplicity.EdgeMultDir.INCOMING;
import static groove.abstraction.neigh.Multiplicity.EdgeMultDir.OUTGOING;
import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.Multiplicity.MultKind;
import groove.abstraction.neigh.MyHashMap;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.equiv.EquivRelation;
import groove.abstraction.neigh.equiv.NodeEquivClass;
import groove.graph.TypeLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
class ShapeStore1 implements ShapeStore {
    /** Private constructor. */
    private ShapeStore1() {
        // empty
    }

    @Override
    public ShapeStore flatten(ShapeCache cache) {
        ShapeStore1 result = new ShapeStore1();
        result.edges = cache.getEdgeSet().toArray(new ShapeEdge[0]);
        char[] nodeEquivArray = flattenNodeEquiv(cache);
        result.nodeEquivArray = nodeEquivArray;
        result.nodeMultArray = flattenNodeMultMap(cache);
        result.inEdgeMult = flattenEdgeMultMap(cache, INCOMING, nodeEquivArray);
        result.outEdgeMult =
            flattenEdgeMultMap(cache, OUTGOING, nodeEquivArray);
        assert nodeEquivArray != null;
        return result;
    }

    /** Computes the flattened representation of an edge multiplicity map. */
    private EdgeRecord[] flattenEdgeMultMap(ShapeCache cache, EdgeMultDir dir,
            char[] nodeEquiv) {
        Map<EdgeSignature,Multiplicity> multMap = cache.getEdgeMultMap(dir);
        EdgeRecord[] result = new EdgeRecord[multMap.size()];
        int ix = 0;
        for (Map.Entry<EdgeSignature,Multiplicity> multEntry : multMap.entrySet()) {
            result[ix] =
                new EdgeRecord(multEntry.getKey(), multEntry.getValue(),
                    cache.getFactory(), nodeEquiv);
            ix++;
        }
        return result;
    }

    /** Computes the flattened representation of the node multiplicity map. */
    private char[] flattenNodeMultMap(ShapeCache cache) {
        char[] result = new char[cache.getNodeStoreSize()];
        for (Map.Entry<ShapeNode,Multiplicity> multEntry : cache.getNodeMultMap().entrySet()) {
            result[multEntry.getKey().getNumber()] =
                multEntry.getValue().getIndex();
        }
        return result;
    }

    /** Computes the flattened representation of the node equivalence relation. */
    private char[] flattenNodeEquiv(ShapeCache cache) {
        char[] result = new char[cache.getNodeStoreSize()];
        char cellIx = 1;
        for (EquivClass<ShapeNode> cell : cache.getEquivRel()) {
            for (ShapeNode node : cell) {
                result[node.getNumber()] = cellIx;
            }
            cellIx++;
            assert cellIx != 0 : "Too many cells in the node partition";
        }
        return result;
    }

    @Override
    public void fill(ShapeCache cache) {
        assert this.edges != null;
        setNodeSet(cache);
        setEdgeSet(cache);
        setNodeEquiv(cache);
        setNodeMultMap(cache);
        setEdgeMultMaps(cache);
    }

    private void setNodeSet(ShapeCache cache) {
        ShapeFactory factory = cache.getFactory();
        Set<ShapeNode> nodeSet = cache.createElementSet();
        char[] nodeEquiv = this.nodeEquivArray;
        for (int i = 0; i < nodeEquiv.length; i++) {
            if (nodeEquiv[i] != 0) {
                nodeSet.add(factory.getNode(i));
            }
        }
        cache.setNodeSet(nodeSet);
    }

    private void setEdgeSet(ShapeCache cache) {
        Set<ShapeEdge> edgeSet = cache.createElementSet();
        ShapeEdge[] edges = this.edges;
        for (int i = 0; i < edges.length; i++) {
            edgeSet.add(edges[i]);
        }
        cache.setEdgeSet(edgeSet);
    }

    private void setNodeEquiv(ShapeCache cache) {
        ShapeFactory factory = cache.getFactory();
        EquivRelation<ShapeNode> equivRel = cache.createNodeEquiv();
        char[] nodeEquiv = this.nodeEquivArray;
        List<EquivClass<ShapeNode>> cells =
            new ArrayList<EquivClass<ShapeNode>>(nodeEquiv.length);
        for (ShapeNode node : cache.getNodeSet()) {
            int cellIx = nodeEquiv[node.getNumber()];
            while (cellIx > cells.size()) {
                cells.add(new NodeEquivClass<ShapeNode>(factory));
            }
            cells.get(cellIx - 1).add(node);
        }
        equivRel.addAll(cells);
        cache.setEquivRel(equivRel);
    }

    private void setNodeMultMap(ShapeCache cache) {
        MyHashMap<ShapeNode,Multiplicity> nodeMultMap =
            cache.createNodeMultMap();
        char[] nodeMultArray = this.nodeMultArray;
        for (ShapeNode node : cache.getNodeSet()) {
            Multiplicity mult =
                Multiplicity.getMultiplicity(nodeMultArray[node.getNumber()],
                    MultKind.NODE_MULT);
            nodeMultMap.put(node, mult);
        }
        cache.setNodeMultMap(nodeMultMap);
    }

    private void setEdgeMultMaps(ShapeCache cache) {
        ShapeFactory factory = cache.getFactory();
        Map<EdgeMultDir,MyHashMap<EdgeSignature,Multiplicity>> edgeMultMaps =
            cache.createEdgeMultMaps();
        for (EdgeMultDir dir : EdgeMultDir.values()) {
            MyHashMap<EdgeSignature,Multiplicity> edgeMultMap =
                edgeMultMaps.get(dir);
            EdgeRecord[] records =
                dir == EdgeMultDir.INCOMING ? this.inEdgeMult
                        : this.outEdgeMult;
            for (int i = 0; i < records.length; i++) {
                EdgeRecord record = records[i];
                edgeMultMap.put(
                    record.getSig(dir, factory, this.nodeEquivArray),
                    record.getMult());
            }
        }
        cache.setEdgeMultMaps(edgeMultMaps);
    }

    /** Flattened set of edges, filled when the shape is fixed. */
    private ShapeEdge[] edges;
    /** Flattened node equivalence relation, filled when the shape is fixed. */
    private char[] nodeEquivArray;
    /** Flattened node multiplicity map, filled when the shape is fixed. */
    private char[] nodeMultArray;
    /** Flattened incoming edge multiplicity map, filled when the shape is fixed. */
    private EdgeRecord[] inEdgeMult;
    /** Flattened outgoing edge multiplicity map, filled when the shape is fixed. */
    private EdgeRecord[] outEdgeMult;

    /** Prototype instance of this store implementation. */
    public static final ShapeStore PROTOTYPE = new ShapeStore1();

    /** Data structure holding the essentials of a single edge signature multiplicity. */
    private static class EdgeRecord {
        public EdgeRecord(EdgeSignature sig, Multiplicity mult,
                ShapeFactory factory, char[] nodeEquiv) {
            this.source = sig.getNode().getNumber();
            this.label = sig.getLabel();
            this.targetEc =
                nodeEquiv[sig.getEquivClass().iterator().next().getNumber()];
            this.multIndex = mult.getIndex();
        }

        public EdgeSignature getSig(EdgeMultDir dir, ShapeFactory factory,
                char[] nodeEquiv) {
            EquivClass<ShapeNode> cell = new NodeEquivClass<ShapeNode>(factory);
            for (int i = 0; i < nodeEquiv.length; i++) {
                if (nodeEquiv[i] == this.targetEc) {
                    cell.add(factory.getNode(i));
                }
            }
            return new EdgeSignature(dir, factory.getNode(this.source),
                this.label, cell);
        }

        public Multiplicity getMult() {
            return Multiplicity.getMultiplicity(this.multIndex,
                MultKind.EDGE_MULT);
        }

        final int source;
        final TypeLabel label;
        final char targetEc;
        final char multIndex;
    }
}
