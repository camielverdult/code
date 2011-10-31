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
package groove.abstraction.neigh.shape;

import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.graph.TypeLabel;

/**
 * An edge signature is composed by a direction, a node (n), a label (l),
 * and an equivalence class (C) and is used as the key for the outgoing and
 * incoming edge multiplicity mappings.
 * 
 * @author Eduardo Zambon
 */
public final class EdgeSignature {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    private final EdgeMultDir direction;
    private final TypeLabel label;
    private final ShapeNode node;
    private final EquivClass<ShapeNode> equivClass;
    private final int hashCode;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Standard constructor that just fills in the object fields. */
    public EdgeSignature(EdgeMultDir direction, ShapeNode node,
            TypeLabel label, EquivClass<ShapeNode> equivClass) {
        this.direction = direction;
        this.label = label;
        this.node = node;
        this.equivClass = equivClass;
        // Fix the equivalence class to avoid problems with hashing.
        this.equivClass.setFixed();
        this.hashCode = this.computeHashCode();
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return this.direction + ":(" + this.node + ", " + this.label + ", "
            + this.equivClass + ")";
    }

    /** 
     * Two edge signatures are equal if they have the same node, same label,
     * and the same equivalence class.
     */
    @Override
    public boolean equals(Object o) {
        boolean result;
        if (this == o) {
            result = true;
        } else if (!(o instanceof EdgeSignature)) {
            result = false;
        } else {
            EdgeSignature es = (EdgeSignature) o;
            result =
                this.direction.equals(es.direction)
                    && this.node.equals(es.node) && this.label.equals(es.label)
                    && this.hasSameEquivClass(es.equivClass);
        }
        // Check for consistency between equals and hashCode.
        assert (!result || this.hashCode() == o.hashCode());
        return result;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    private int computeHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.direction.hashCode();
        result = prime * result + this.node.hashCode();
        result = prime * result + this.label.hashCode();
        result = prime * result + this.equivClass.hashCode();
        return result;
    }

    /**
     * Returns true if the edge signature contains the edge given as argument.
     */
    public boolean contains(ShapeEdge edge) {
        ShapeNode incident = edge.incident(this.direction);
        ShapeNode opposite = edge.opposite(this.direction);
        return this.node.equals(incident) && this.label.equals(edge.label())
            && this.equivClass.contains(opposite);
    }

    /** Basic getter method. */
    public EdgeMultDir getDirection() {
        return this.direction;
    }

    /** Basic getter method. */
    public ShapeNode getNode() {
        return this.node;
    }

    /** Basic getter method. */
    public TypeLabel getLabel() {
        return this.label;
    }

    /** Basic getter method. */
    public EquivClass<ShapeNode> getEquivClass() {
        return this.equivClass;
    }

    /** Returns true if this signature has the given class. */
    public boolean hasSameEquivClass(EquivClass<ShapeNode> ec) {
        return EquivClass.<ShapeNode>areEqual(this.equivClass, ec);
    }

    /** Returns true if the signature node is in the equivalence class. */
    public boolean isSelfReferencing() {
        return this.equivClass.contains(this.node);
    }

}
