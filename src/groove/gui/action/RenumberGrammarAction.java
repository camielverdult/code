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
package groove.gui.action;

import groove.gui.Options;
import groove.gui.Simulator;

import java.io.IOException;

/**
 * Action for renumbering all node numbers in host graphs and rules
 * to start with {@code 0}.
 */
public class RenumberGrammarAction extends SimulatorAction {
    /** Constructs an instance of the action, for a given simulator. */
    public RenumberGrammarAction(Simulator simulator) {
        super(simulator, Options.RENUMBER_ACTION_NAME, null);
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getGrammar() != null
            && getModel().getStore().isModifiable());
    }

    @Override
    protected boolean doAction() {
        boolean result = false;
        try {
            result = getModel().doRenumber();
        } catch (IOException exc) {
            showErrorDialog("Error while renumbering", exc);
        }
        return result;
    }
}