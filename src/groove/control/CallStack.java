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

import java.util.Stack;

/**
 * Stack of calls.
 * The bottom element is the original call; the top element is the eventual
 * rule call.
 * All but the top element are procedure calls; all but the bottom element
 * are initial calls of the bodies of the procedure of the next level down.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CallStack extends Stack<Call> {
    /**
     * Constructs an initially empty stack.
     */
    public CallStack() {
        // empty
    }

    @Override
    public String toString() {
        return toString(false);
    }

    /** Returns the concatenated names of all calls in the stack,
     * separated by '/'.
     * @param allPars if {@code true}, parentheses are always inserted;
     * otherwise, they are only inserted for parameterised calls.
     */
    public String toString(boolean allPars) {
        StringBuilder result = new StringBuilder();
        for (Call call : this) {
            if (result.length() > 0) {
                result.append('/');
            }
            if (allPars || !call.getArgs().isEmpty()) {
                result.append(call.toString());
            } else {
                result.append(call.getUnit().getFullName());
            }
        }
        return result.toString();
    }
}
