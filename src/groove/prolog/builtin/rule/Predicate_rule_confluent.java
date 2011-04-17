/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.prolog.builtin.rule;

import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologException;
import groove.prolog.GrooveEnvironment;
import groove.prolog.builtin.trans.TransPrologCode;
import groove.trans.RuleName;
import groove.view.RuleView;

/**
 * Predicate rule_confluent(+RuleName)
 * @author Lesley Wevers
 */
public class Predicate_rule_confluent extends TransPrologCode {
    @Override
    public int execute(Interpreter interpreter, boolean backtrackMode,
            Term[] args) throws PrologException {

        if (!(interpreter.getEnvironment() instanceof GrooveEnvironment)) {
            GrooveEnvironment.invalidEnvironment();
        }

        try {
            RuleName name = new RuleName(((AtomTerm) args[0]).value);

            RuleView ruleView =
                ((GrooveEnvironment) interpreter.getEnvironment()).getGrooveState().getGrammarView().getRuleView(
                    name);

            if (ruleView != null && ruleView.isConfluent()) {
                return SUCCESS_LAST;
            }
        } catch (Exception e) {
            return FAIL;
        }

        return FAIL;
    }
}