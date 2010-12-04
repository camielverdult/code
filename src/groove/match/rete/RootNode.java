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
package groove.match.rete;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Node;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class RootNode extends ReteNetworkNode {

    public RootNode(ReteNetwork network) {
        super(network);
    }

    @Override
    public boolean addSuccessor(ReteNetworkNode nnode) {
        boolean result =
            (nnode instanceof EdgeCheckerNode)
                || (nnode instanceof NodeCheckerNode);
        /*
         * check to see if n-node is of type g-node-checker or 
         * g-edge-checker. If it is, then if it is not already there 
         * it should be added to the successors collection.         
         * if it's already there, then it should just return true;
         * if the type is no of the above two, it should fail and
         * return false
         */
        if (result && !isAlreadySuccessor(nnode)) {
            getSuccessors().add(nnode);
            nnode.addAntecedent(this);
        }
        return result;
    }

    public void receiveElement(ReteNetworkNode source, Element elem,
            Action action) {

        if (elem instanceof Node) {
            for (ReteNetworkNode nnode : this.getSuccessors()) {
                if (nnode instanceof NodeCheckerNode) {
                    ((NodeCheckerNode) nnode).receiveNode((Node) elem, action);
                }
            }
        } else if (elem instanceof Edge) {
            for (ReteNetworkNode nnode : this.getSuccessors()) {
                if (nnode instanceof EdgeCheckerNode) {
                    if (((Edge) elem).label().text().equals(
                        ((EdgeCheckerNode) nnode).getEdge().label().text())) {
                        ((EdgeCheckerNode) nnode).receiveEdge(this,
                            (Edge) elem, action);
                    }
                }
            }
        }
    }

    @Override
    public boolean equals(ReteNetworkNode node) {
        //There should be only one Root node
        return this == node;
    }

    /**
     * For root the value of size is irrelevant. By fiat, we set its size
     * to be -1.
     * 
     * This is a construction-time method only.  
     */
    @Override
    public int size() {
        return -1;
    }

    @Override
    public Element[] getPattern() {
        // this method is not supposed to be called
        throw new NotImplementedException();
    }
}
