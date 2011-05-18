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

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.view.GraphView;

import java.io.IOException;

/**
 * Action that takes care of copying a host graph in a grammar view.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CopyHostAction extends SimulatorAction {
    /** Constructs a new action for a given simulator. */
    public CopyHostAction(Simulator simulator) {
        super(simulator, Options.COPY_GRAPH_ACTION_NAME, Icons.COPY_ICON);
        putValue(ACCELERATOR_KEY, Options.COPY_KEY);
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getStore() != null
            && getModel().getStore().isModifiable()
            && !getModel().getHostSet().isEmpty());

        if (getSimulator().getGraphPanel() == getSimulator().getStatePanel()) {
            getSimulator().getCopyMenuItem().setAction(this);
        }
    }

    @Override
    public boolean execute() {
        boolean result = false;
        for (GraphView oldHostView : getModel().getHostSet()) {
            String oldName = oldHostView.getName();
            String newName =
                askNewGraphName("Select new graph name", oldName, true);
            if (newName != null) {
                try {
                    result |=
                        getModel().doAddHost(
                            oldHostView.getAspectGraph().rename(newName));
                } catch (IOException exc) {
                    showErrorDialog(exc, String.format(
                            "Error while copying host graph '%s' to '%s'", oldName,
                            newName));
                }
            }
        }
        return result;
    }
}