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
package groove.control.template;

import groove.control.CallStack;
import groove.grammar.Recipe;

import java.util.Stack;

/**
 * Stack of switches, corresponding to nested procedure calls.
 * @author Arend Rensink
 * @version $Revision $
 */
public class SwitchStack extends Stack<Switch> {
    /**
     * Constructs the stack consisting of a given (bottom) switch
     * and its recursively nested switches.
     * @see Switch#getNested()
     */
    public SwitchStack(Switch bottom) {
        do {
            add(bottom);
            bottom = bottom.getNested();
        } while (bottom != null);
    }

    /** Constructs a copy of a given stack. */
    public SwitchStack(SwitchStack other) {
        addAll(other);
    }

    /** Constructs an initially empty stack. */
    public SwitchStack() {
        // empty
    }

    /** Returns the call stack corresponding to this switch stack. */
    public CallStack getCallStack() {
        if (this.callStack == null) {
            this.callStack = new CallStack();
            for (Switch deriv : this) {
                this.callStack.add(deriv.getCall());
            }
        }
        return this.callStack;
    }

    private CallStack callStack;

    /** Indicates if this step is part of a recipe. */
    public boolean isRecipeStep() {
        return getCallStack().getRecipe() != null;
    }

    /** Returns the recipe of which this is a step, if any. */
    public Recipe getRecipe() {
        return getCallStack().getRecipe();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SwitchStack)) {
            return false;
        }
        SwitchStack other = (SwitchStack) obj;
        if (size() != other.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (!get(i).equals(other.get(i), false)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        for (int i = 0; i < size(); i++) {
            result = prime * result + get(i).hashCode(false);
        }
        return result;
    }

}
