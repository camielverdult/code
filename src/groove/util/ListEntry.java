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
 * $Id: ListEntry.java,v 1.2 2008-01-30 09:32:13 iovka Exp $
 */
package groove.util;

/**
 * Node in a linked list. Used for space optimizations instead of arrays.
 * This saves out the array object itself (20 bytes).
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
public interface ListEntry {
    /**
     * Retrieves the next entry in the list.
     */
    public ListEntry getNext();

    /**
     * Sets the next entry in the list.
     */
    public void setNext(ListEntry next);
}
