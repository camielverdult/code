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

import java.util.List;

/**
 * Until-do term.
 * @author Arend Rensink
 * @version $Revision $
 */
public class UntilTerm extends Term {
    /**
     * Constructs an until-do term.
     */
    UntilTerm(Term arg0, Term arg1) {
        super(Op.UNTIL, arg0, arg1);
    }

    @Override
    protected List<TermAttempt> computeAttempts() {
        List<TermAttempt> result = null;
        switch (arg0().getType()) {
        case TRIAL:
            result = arg0().getAttempts();
            break;
        case DEAD:
            if (arg1().isTrial()) {
                result = createAttempts();
                for (TermAttempt attempt : arg1().getAttempts()) {
                    result.add(attempt.newAttempt(attempt.target().seq(this)));
                }
            }
            break;
        }
        return result;
    }

    @Override
    protected Term computeSuccess() {
        Term result = null;
        switch (arg0().getType()) {
        case TRIAL:
            result = arg0().onSuccess();
            break;
        case DEAD:
            if (arg1().isTrial()) {
                result = arg1().onSuccess().seq(this);
            }
            break;
        }
        return result;
    }

    @Override
    protected Term computeFailure() {
        Term result = null;
        switch (arg0().getType()) {
        case TRIAL:
            result = arg0().onFailure();
            break;
        case DEAD:
            if (arg1().isTrial()) {
                result = arg1().onFailure().seq(this);
            }
            break;
        }
        return result;
    }

    @Override
    protected int computeDepth() {
        return 0;
    }

    @Override
    protected Type computeType() {
        switch (arg0().getType()) {
        case TRIAL:
            return Type.TRIAL;
        case DEAD:
            if (arg1().isTrial()) {
                return Type.TRIAL;
            } else {
                return Type.DEAD;
            }
        case FINAL:
            return Type.FINAL;
        default:
            assert false;
            return null;
        }
    }
}
