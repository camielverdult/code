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
 * $Id: Edge.java,v 1.7 2008-01-30 09:32:52 iovka Exp $
 */
package groove.graph;

/**
 * Generically typed specialisation of the {@link Edge} interface.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface GEdge<N extends Node> extends Edge {
    /*
     * Specialises the return type.
     */
    @Override
    public N source();

    /*
     * Specialises the return type.
     */
    @Override
    public N target();
}