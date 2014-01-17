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
package groove.control.symbolic;

import java.util.ArrayList;
import java.util.List;

/**
 * Atomic block.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AtomTerm extends Term {
    /**
     * Constructs an atomic block.
     */
    public AtomTerm(Term arg0) {
        super(Op.ATOM, arg0);
        assert arg0.isTopLevel();
    }

    @Override
    protected List<TermAttempt> computeAttempts() {
        List<TermAttempt> result = null;
        if (arg0().isTrial()) {
            result = new ArrayList<TermAttempt>();
            for (TermAttempt attempt : arg0().getAttempts()) {
                result.add(attempt.newAttempt(attempt.target().transit()));
            }
            return result;
        }
        return result;
    }

    @Override
    protected Term computeSuccess() {
        Term result = null;
        if (arg0().isTrial()) {
            result = arg0().onSuccess().atom();
        }
        return result;
    }

    @Override
    protected Term computeFailure() {
        Term result = null;
        if (arg0().isTrial()) {
            result = arg0().onFailure().atom();
        }
        return result;
    }

    @Override
    protected int computeDepth() {
        return 0;
    }

    @Override
    protected Type computeType() {
        return arg0().getType();
    }

    @Override
    public Term atom() {
        return this;
    }

}
