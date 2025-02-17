/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.grammar.type;

import static nl.utwente.groove.graph.EdgeRole.NODE_TYPE;

import java.awt.Color;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.grammar.AnchorKind;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.util.line.Line;

/**
 * Node in a type graph.
 * As added functionality w.r.t. default nodes, a type node stores its type
 * (which is a node type label).
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class TypeNode implements Node, TypeElement {
    /**
     * Constructs a new type node, with a given number and label.
     * The label must be a node type.
     * Should only be called from {@link TypeFactory}.
     * @param nr the number of the type node
     * @param type the non-{@code null} type label
     * @param graph the type graph to which this node belongs; non-{@code null}
     */
    public TypeNode(int nr, TypeLabel type, TypeGraph graph) {
        assert graph != null;
        assert type.getRole() == NODE_TYPE : String
            .format("Can't create type node for non-type label '%s'", type);
        this.key = new TypeNodeKey(type);
        this.nr = nr;
        this.graph = graph;
    }

    /**
     * Type nodes are equal if they have the same type graph and number.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TypeNode other)) {
            return false;
        }
        if (getGraph() != other.getGraph()) {
            return false;
        }
        if (getNumber() != other.getNumber()) {
            return false;
        }
        assert label().equals(other.label());
        assert isImported() == other.isImported();
        assert isAbstract() == other.isAbstract();
        return true;
    }

    @Override
    public int hashCode() {
        return getNumber() ^ label().hashCode();
    }

    @Override
    public String toString() {
        return label().text();
    }

    @Override
    public int getNumber() {
        return this.nr;
    }

    /** The number of this node. */
    private final int nr;

    @Override
    public int compareTo(Label obj) {
        if (obj instanceof TypeNode) {
            return label().compareTo(((TypeNode) obj).label());
        } else {
            assert obj instanceof TypeEdge;
            // nodes come before edges with the node as source
            int result = compareTo(((TypeEdge) obj).source());
            if (result == 0) {
                result = -1;
            }
            return result;
        }
    }

    @Override
    public Line toLine() {
        return label().toLine();
    }

    @Override
    public String toParsableString() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String text() {
        return label().text();
    }

    @Override
    public EdgeRole getRole() {
        return EdgeRole.NODE_TYPE;
    }

    @Override
    public @NonNull TypeKey key() {
        return this.key;
    }

    private final TypeKey key;

    /** Indicates if this node type is abstract. */
    public final boolean isAbstract() {
        return this.abstracted;
    }

    /** Sets this node type to abstract. */
    public final void setAbstract() {
        this.abstracted = true;
    }

    /** Returns true if this node if of top type. */
    public final boolean isTopType() {
        return label() == TypeLabel.NODE;
    }

    /** Changes the importedness status of this node.
     * Also notifies the containing type graph of the change
     * @param imported the new value for the importedness
     */
    public final void setImported(boolean imported) {
        this.imported = imported;
        getGraph().setImported(this, imported);
    }

    /** Indicates if this node type is imported. */
    public final boolean isImported() {
        return this.imported;
    }

    /** Flag indicating if this node type is imported from another type graph. */
    private boolean imported;

    /** Indicates if this node type stands for a data type. */
    public final boolean isSort() {
        return label().isSort();
    }

    /** Returns the data type of this node type, if any. */
    public final @Nullable Sort getSort() {
        return label().getSort();
    }

    /** Returns the (possibly {@code null}) label pattern associated with this type node. */
    public final @Nullable LabelPattern getLabelPattern() {
        return this.pattern;
    }

    /** Checks if there is a label pattern associated with this type node. */
    public final boolean hasLabelPattern() {
        return this.pattern != null;
    }

    /** Sets the label pattern of this type node. */
    public final void setLabelPattern(@Nullable LabelPattern pattern) {
        this.pattern = pattern;
    }

    /** The label pattern of this node, if any. */
    private @Nullable LabelPattern pattern;

    /** Sets the declared (and derived) colour of this type node. */
    public void setDeclaredColor(Color colour) {
        this.declaredColour = this.derivedColour = colour;
    }

    /** Indicates if this type node has a declared display colour. */
    public boolean hasDeclaredColor() {
        return this.declaredColour != null;
    }

    /** Returns the declared colour of this type node, if any. */
    public @Nullable Color getDeclaredColor() {
        return this.declaredColour;
    }

    /** The declared display colour of this node, if any. */
    private @Nullable Color declaredColour;

    /** Returns the (possibly {@code null}) colour of this type node. */
    public final @Nullable Color getColor() {
        return this.derivedColour;
    }

    /** Indicates if this type node has a (declared or derived) display colour. */
    public final boolean hasColor() {
        return this.derivedColour != null;
    }

    /** Sets the derived colour of this type node. */
    void setDerivedColor(Color colour) {
        this.derivedColour = colour;
    }

    /** The derived display colour of this node, if any. */
    private @Nullable Color derivedColour;

    @Override
    public TypeGraph getGraph() {
        return this.graph;
    }

    /** The type graph with which this node is associated. */
    private final TypeGraph graph;

    @Override
    public Set<TypeNode> getSubtypes() {
        return getGraph().getSubtypes(this);
    }

    @Override
    public Set<TypeNode> getSupertypes() {
        return getGraph().getSupertypes(this);
    }

    /** Returns the set of direct supertypes of this type node. */
    public Set<TypeNode> getDirectSupertypes() {
        return getGraph().getDirectSupertypes(this);
    }

    /** Tests if another type satisfies the constraints of this one.
     * This is the case if the types are equal, or this type is a
     * supertype of the other.
     * @param other the other type node
     * @param strict if {@code true}, no subtype check is performed
     * @return {@code true} if {@code other} equals {@code this},
     * or is a subtype and {@code strict} is {@code false}
     */
    public boolean subsumes(TypeNode other, boolean strict) {
        if (this.equals(other)) {
            return true;
        } else {
            return !strict && getGraph().isSubtype(other, this);
        }
    }

    @Override
    public AnchorKind getAnchorKind() {
        return AnchorKind.LABEL;
    }

    /** Flag indicating if this node type is abstract. */
    private boolean abstracted;
}
