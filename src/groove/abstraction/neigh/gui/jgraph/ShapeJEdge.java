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
package groove.abstraction.neigh.gui.jgraph;

import groove.abstraction.neigh.shape.ShapeEdge;
import groove.graph.Edge;
import groove.gui.jgraph.GraphJEdge;
import groove.gui.jgraph.GraphJGraph;

/**
 * Class that connects to the JGraph library for displaying ShapeEdges.
 * @author Eduardo Zambon
 */
public class ShapeJEdge extends GraphJEdge {

    private ShapeJEdge(ShapeJGraph jGraph) {
        super(jGraph);
    }

    private ShapeJEdge(ShapeJGraph jGraph, ShapeEdge edge) {
        super(jGraph, edge);
    }

    @Override
    public ShapeJGraph getJGraph() {
        return (ShapeJGraph) super.getJGraph();
    }

    /** 
     * Factory method, in case this object is used as a prototype.
     * Returns a fresh {@link GraphJEdge} of the same type as this one. 
     */
    @Override
    public GraphJEdge newJEdge(Edge edge) {
        assert edge instanceof ShapeEdge;
        return new ShapeJEdge(getJGraph(), (ShapeEdge) edge);
    }

    /** Returns a prototype {@link GraphJEdge} for a given {@link GraphJGraph}. */
    public static ShapeJEdge getPrototype(ShapeJGraph jGraph) {
        return new ShapeJEdge(jGraph);
    }
}
