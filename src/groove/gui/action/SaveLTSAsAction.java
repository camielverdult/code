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

import groove.explore.util.LTSLabels;
import groove.explore.util.LTSReporter;
import groove.explore.util.StateReporter;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.SaveLTSAsDialog;
import groove.gui.dialog.SaveLTSAsDialog.StateExport;
import groove.lts.GTS;
import groove.lts.GraphState;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Action that takes care of saving the LTS graph under a certain name.
 * @author Arend Rensink
 * @version $Revision $
 */
public class SaveLTSAsAction extends SimulatorAction {
    /** Constructs an instance of the action, for a given simulator. */
    public SaveLTSAsAction(Simulator simulator) {
        super(simulator, Options.SAVE_LTS_ACTION_NAME, Icons.SAVE_AS_ICON);
    }

    @Override
    public void execute() {
        SaveLTSAsDialog dialog = new SaveLTSAsDialog(getSimulator());
        if (getLastGrammarFile() != null) {
            dialog.setCurrentDirectory(getLastGrammarFile().getAbsolutePath());
        }
        if (dialog.showDialog(getSimulator())) {
            doSave(dialog.getDirectory(), dialog.getLtsPattern(), dialog.getStatePattern(),
                dialog.getExportStates(), dialog.getLTSLabels());
        }
    }

    private void doSave(String dir, String ltsPattern, String statePattern,
            StateExport exportStates, LTSLabels flags) {
        GTS gts = getSimulatorModel().getGts();

        Collection<? extends GraphState> export = new HashSet<GraphState>(0);
        switch (exportStates) {
        case ALL:
            export = gts.nodeSet();
            break;
        case TOP:
            List<GraphState> states = new ArrayList<GraphState>();
            for (GraphState state : gts.nodeSet()) {
                if (!state.isRecipeStage()) {
                    states.add(state);
                }
            }
            export = states;
            break;
        case FINAL:
            export = gts.getFinalStates();
            break;
        case RESULT:
            export = gts.getResultStates();
            break;
        default:
            assert exportStates == StateExport.NONE;
        }

        try {
            LTSReporter.exportLTS(gts, new File(dir, ltsPattern).toString(), flags);
            for (GraphState state : export) {
                StateReporter.exportState(state, new File(dir, statePattern).toString());
            }
        } catch (IOException e) {
            showErrorDialog(e, "Error while saving LTS to %s", dir);
        }
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getGts() != null);
    }
}