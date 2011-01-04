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
 * $Id: GraphJEdge.java,v 1.16 2008-01-09 16:16:06 rensink Exp $
 */
package groove.gui.jgraph;

import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.util.Converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Extends DefaultEdge to store a collection of graph Edges. The graph edges are
 * stored as a Set in the user object. In the latter case, toString() the user
 * object is the empty string.
 */
public class GraphJEdge<N extends Node,E extends Edge<N>> extends JEdge
        implements GraphJCell<N,E> {
    /**
     * Constructs an uninitialised model edge.
     */
    GraphJEdge(GraphJModel<N,E> jModel) {
        super(jModel);
    }

    /**
     * Constructs a model edge based on a graph edge.
     * @param edge the underlying graph edge of this model edge.
     */
    GraphJEdge(GraphJModel<N,E> jModel, E edge) {
        super(jModel);
        this.source = edge.source();
        this.target = edge.target();
        this.edges.add(edge);
    }

    /** Returns the {@link JModel} associated with this {@link JEdge}. */
    @SuppressWarnings("unchecked")
    @Override
    public GraphJModel<N,E> getJModel() {
        return (GraphJModel<N,E>) super.getJModel();
    }

    /**
     * Returns <code>true</code> if the super method does so, and the edge has
     * at least one non-filtered list label, and all end nodes are visible.
     */
    @Override
    public boolean isVisible() {
        boolean result =
            super.isVisible() && !isSourceLabel() && !getLines().isEmpty();
        if (result && !getJModel().isShowUnfilteredEdges()) {
            result =
                getSourceVertex().isVisible()
                    && (isSourceLabel() || getTargetVertex().isVisible());
        }
        return result;
    }

    /**
     * Indicates if this edge is a self-edge that can be shown as a label on its
     * source vertex. This is the case if {@link GraphJModel#isShowVertexLabels()} and
     * {@link GraphJModel#isPotentialUnaryEdge(Edge)} hold for this edge.
     */
    public boolean isSourceLabel() {
        return getJModel().isShowVertexLabels()
            && getJModel().isPotentialUnaryEdge(getEdge());
    }

    /**
     * Returns the common source of the underlying graph edges.
     */
    public N getSourceNode() {
        if (this.source == null) {
            this.source = getSourceVertex().getNode();
        }
        return this.source;
    }

    /**
     * Returns the common target of the underlying graph edges.
     */
    public N getTargetNode() {
        if (this.target == null) {
            this.target = getTargetVertex().getNode();
        }
        return this.target;
    }

    /**
     * Specialises the return type.
     */
    @SuppressWarnings("unchecked")
    @Override
    public GraphJVertex<N,E> getSourceVertex() {
        return (GraphJVertex<N,E>) super.getSourceVertex();
    }

    /**
     * Specialises the return type.
     */
    @SuppressWarnings("unchecked")
    @Override
    public GraphJVertex<N,E> getTargetVertex() {
        return (GraphJVertex<N,E>) super.getTargetVertex();
    }

    /** 
     * Clears the set of graph edges wrapped in this JEdge,
     * and sets the source and target node from the source and target JVertex. 
     */
    void reset() {
        this.edges.clear();
        this.source = null;
        this.target = null;
    }

    /**
     * Returns an unmodifiable view upon the set of underlying graph edges.
     */
    public Set<E> getEdges() {
        return Collections.unmodifiableSet(this.edges);
    }

    /**
     * Returns the first edge from the set of underlying edges.
     */
    public E getEdge() {
        return this.edges.iterator().next();
    }

    /**
     * This implementation calls {@link #getLine(Edge)} on all edges in
     * {@link #getEdges()} that are not being filtered by the model
     * according to {@link JModel#isFiltering(Label)}.
     */
    public List<StringBuilder> getLines() {
        List<StringBuilder> result = new ArrayList<StringBuilder>();
        for (E edge : getEdges()) {
            // only add edges that have an unfiltered label
            boolean visible = false;
            for (Label label : getListLabels(edge)) {
                if (!getJModel().isFiltering(label)) {
                    visible = true;
                    break;
                }
            }
            if (visible) {
                result.add(getLine(edge));
            }
        }
        return result;
    }

    /**
     * This implementation returns the text from {@link #getLabel(Edge)} wrapped
     * in a StringBuilder.
     */
    public StringBuilder getLine(E edge) {
        return new StringBuilder(getLabel(edge).text());
    }

    /**
     * This implementation calls {@link #getListLabels(Edge)} on all edges in
     * {@link #getEdges()}.
     */
    public Collection<? extends Label> getListLabels() {
        List<Label> result = new ArrayList<Label>();
        for (E edge : getEdges()) {
            result.addAll(getListLabels(edge));
        }
        return result;
    }

    /** Returns the listable labels on a given edge. */
    public Set<? extends Label> getListLabels(E edge) {
        return Collections.singleton(getLabel(edge));
    }

    /** This implementation delegates to {@link Edge#label()}. */
    public Label getLabel(E edge) {
        return edge.label();
    }

    /**
     * Adds an edge to the underlying set of edges, if the edge is appropriate.
     * Indicates in its return value if the edge has indeed been added.
     * @param edge the edge to be added
     * @return <tt>true</tt> if the edge has been added; <tt>false</tt> if
     *         <tt>edge</tt> is not compatible with this j-edge and cannot be
     *         added. This implementation returns <tt>true</tt> always.
     * @require <tt>edge.source() == getSourceNode</tt> and
     *          <tt>edge.target() == getTargetNode()</tt>
     * @ensure if <tt>result</tt> then <tt>getEdgeSet().contains(edge)</tt>
     */
    public boolean addEdge(E edge) {
        assert edge.source().equals(getSourceNode());
        assert edge.target().equals(getTargetNode());
        return this.edges.add(edge);
    }

    @Override
    StringBuilder getEdgeDescription() {
        StringBuilder result = super.getEdgeDescription();
        String sourceIdentity = getSourceVertex().getNodeIdentity();
        if (sourceIdentity != null) {
            result.append(" from ");
            result.append(Converter.ITALIC_TAG.on(sourceIdentity));
        }
        String targetIdentity = getTargetVertex().getNodeIdentity();
        if (targetIdentity != null) {
            result.append(" to ");
            result.append(Converter.ITALIC_TAG.on(targetIdentity));
        }
        return result;
    }

    /** Source node of the underlying graph edges. */
    private N source;
    /** Target node of the underlying graph edges. */
    private N target;
    /** Set of graph edges mapped to this JEdge. */
    private final Set<E> edges = new TreeSet<E>();
}