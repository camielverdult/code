// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: AlgebraEdge.java,v 1.1.1.1 2007-03-20 10:05:37 kastenberg Exp $
 */

package groove.graph.algebra;

import groove.graph.AbstractBinaryEdge;
import groove.graph.Label;
import groove.graph.Node;

/**
 * Instances of this class are edges between {@link groove.graph.algebra.ProductNode}s
 * and {@link groove.graph.algebra.ValueNode}s.
 * 
 * @author Harmen Kastenberg
 * @version $Revision 1.0$ $Date: 2007-03-20 10:05:37 $
 */
public class AlgebraEdge extends AbstractBinaryEdge {

    public AlgebraEdge(Node source, Label label, Node target) {
        super(source, label, target);
    }

    /* (non-Javadoc)
     * @see groove.graph.AbstractBinaryEdge#newEdge(groove.graph.Node, groove.graph.Label, groove.graph.Node)
     */
    public AlgebraEdge newEdge(Node source, Label label, Node target) {
        return new AlgebraEdge(source, label, target);
    }
}
