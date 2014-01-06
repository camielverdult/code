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
package groove.control;

import groove.control.CtrlEdge.Kind;
import groove.control.CtrlPar.Var;

import java.util.List;

/**
 * Unit of functionality that can be called from a control program.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface Callable {
    /**
     * Returns the kind of callable.
     * @return the kind of callable; cannot be {@link Kind#CHOICE}
     */
    Kind getKind();

    /** Returns the full (qualified) name of this unit. */
    String getLastName();

    /** 
     * Returns the last part of the full name this unit. 
     * @see #getLastName()
     */
    String getFullName();

    /**
     * Returns the priority of the action.
     */
    public int getPriority();

    /** Returns the signature of the action. */
    public List<Var> getSignature();
}
