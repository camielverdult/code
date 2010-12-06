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
 * $Id: NodeEdgeMap.java,v 1.4 2008-01-30 09:32:58 iovka Exp $
 */
package groove.graph;

import java.util.Map;

/**
 * Specialisation of a {@link Map} for {@link Element}s with some added
 * functionality.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface GraphMap<SN extends Node,TN extends Node,SE extends Edge,TE extends Edge>
        extends GenericNodeEdgeMap<SN,TN,SE,TE> { // extends
    // Map<Element,Element>
    // {
    /**
     * Tests if a given node occurs as a key in the node map.
     * @param elem the element tested for
     * @return <code>true</code> if <code>elem</code> occurs as a key
     */
    boolean containsNodeKey(SN elem);

    /**
     * Tests if a given edge occurs as a key in the edge map.
     * @param elem the element tested for
     * @return <code>true</code> if <code>elem</code> occurs as a key
     */
    boolean containsEdgeKey(SE elem);

    /**
     * Tests if a given element occurs as a key in the node or edge map.
     * @param elem the element tested for
     * @return <code>true</code> if <code>elem</code> occurs as a key
     */
    @Deprecated
    boolean containsKey(Element elem);

    /**
     * Tests if a given element occurs as a value in the node or edge map.
     * @param elem the element tested for
     * @return <code>true</code> if <code>elem</code> occurs as a value
     */
    boolean containsValue(Element elem);

    /** Returns the image of a label under this map. */
    Label getLabel(Label label);

    /**
     * Returns the image of an edge under this map, creating the image if
     * necessary. An image is created if the map does not contain an image but
     * does contain images for the end nodes and label. The exact type of the
     * created edge depends on the map instance.
     */
    TE mapEdge(SE key);

    /**
     * A public clone method returning a {@link GraphMap}.
     * @return a copy of this object
     */
    GraphMap<SN,TN,SE,TE> clone();
}
