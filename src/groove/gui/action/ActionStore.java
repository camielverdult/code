/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: Simulator.java,v 1.92 2008/03/18 15:34:40 iovka Exp $
 */
package groove.gui.action;

import groove.gui.Refreshable;
import groove.gui.Simulator;
import groove.gui.SimulatorListener;
import groove.gui.SimulatorModel;
import groove.gui.SimulatorModel.Change;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.Action;

/**
 * Program that applies a production system to an initial graph.
 * @author Arend Rensink
 * @version $Revision: 3382 $
 */
public class ActionStore implements SimulatorListener {
    /**
     * Constructs a simulator with an empty graph grammar.
     */
    public ActionStore(Simulator simulator) {
        this.simulator = simulator;
        simulator.getModel().addListener(this);
    }

    private final Simulator simulator;

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        refreshActions();
    }

    /**
     * Adds an element to the set of refreshables. Also calls
     * {@link Refreshable#refresh()} on the element.
     */
    public void addRefreshable(Refreshable element) {
        this.refreshables.add(element);
    }

    /**
     * Is called after a change to current state, rule or derivation or to the
     * currently selected view panel to allow registered refreshable elements to
     * refresh themselves.
     */
    public void refreshActions() {
        for (Refreshable action : this.refreshables) {
            action.refresh();
        }
    }

    private final List<Refreshable> refreshables = new ArrayList<Refreshable>();

    /**
     * Returns the transition application action permanently associated with
     * this simulator.
     */
    public ApplyTransitionAction getApplyTransitionAction() {
        if (this.applyTransitionAction == null) {
            this.applyTransitionAction =
                new ApplyTransitionAction(this.simulator);
        }
        return this.applyTransitionAction;
    }

    /**
     * The transition application action permanently associated with this
     * simulator.
     */
    private ApplyTransitionAction applyTransitionAction;

    /**
     * Returns the back simulation action permanently associated with this
     * simulator.
     */
    public Action getBackAction() {
        if (this.backAction == null) {
            this.backAction =
                this.simulator.getSimulationHistory().getBackAction();
        }
        return this.backAction;
    }

    /** The back simulation action permanently associated with this simulator. */
    private Action backAction;

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link CancelEditControlAction}.
     */
    public CancelEditControlAction getCancelEditControlAction() {
        if (this.cancelEditControlAction == null) {
            this.cancelEditControlAction =
                new CancelEditControlAction(this.simulator);
        }
        return this.cancelEditControlAction;
    }

    /** Singular instance of the CancelAction. */
    private CancelEditControlAction cancelEditControlAction;

    /**
     * Returns the CTL formula providing action permanently associated with this
     * simulator.
     * @param full if {@code true}, the action first generates the full state
     * space.
     */
    public Action getCheckCTLAction(boolean full) {
        CheckCTLAction result =
            full ? this.checkCTLFreshAction : this.checkCTLAsIsAction;
        if (result == null) {
            result = new CheckCTLAction(this.simulator, full);
            if (full) {
                this.checkCTLFreshAction = result;
            } else {
                this.checkCTLAsIsAction = result;
            }
        }
        return result;
    }

    /**
     * Action to check a CTL property on a fully explored state space.
     */
    private CheckCTLAction checkCTLFreshAction;

    /**
     * Action to check a CTL property on the current state space.
     */
    private CheckCTLAction checkCTLAsIsAction;

    /**
     * Lazily creates and returns the singleton instance of the CopyAction.
     */
    public CopyControlAction getCopyControlAction() {
        if (this.copyControlAction == null) {
            this.copyControlAction = new CopyControlAction(this.simulator);
        }
        return this.copyControlAction;
    }

    /** Singular instance of the CopyAction. */
    private CopyControlAction copyControlAction;

    /**
     * Returns the graph copying action permanently associated with this
     * simulator.
     */
    public CopyHostAction getCopyGraphAction() {
        // lazily create the action
        if (this.copyGraphAction == null) {
            this.copyGraphAction = new CopyHostAction(this.simulator);
        }
        return this.copyGraphAction;
    }

    /**
     * The graph copying action permanently associated with this simulator.
     */
    private CopyHostAction copyGraphAction;

    /**
     * Returns the rule copying action permanently associated with this
     * simulator.
     */
    public CopyRuleAction getCopyRuleAction() {
        // lazily create the action
        if (this.copyRuleAction == null) {
            this.copyRuleAction = new CopyRuleAction(this.simulator);
        }
        return this.copyRuleAction;
    }

    /**
     * The rule copying action permanently associated with this simulator.
     */
    private CopyRuleAction copyRuleAction;

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link CopyTypeAction}.
     */
    public CopyTypeAction getCopyTypeAction() {
        if (this.copyTypeAction == null) {
            this.copyTypeAction = new CopyTypeAction(this.simulator);
        }
        return this.copyTypeAction;
    }

    /** Singular instance of the CopyTypeAction. */
    private CopyTypeAction copyTypeAction;

    /**
     * Lazily creates and returns the singleton instance of the DeleteAction.
     */
    public DeleteControlAction getDeleteControlAction() {
        if (this.deleteControlAction == null) {
            this.deleteControlAction = new DeleteControlAction(this.simulator);
        }
        return this.deleteControlAction;
    }

    /** Singular instance of the DeleteAction. */
    private DeleteControlAction deleteControlAction;

    /**
     * Returns the graph deletion action permanently associated with this
     * simulator.
     */
    public DeleteHostAction getDeleteHostAction() {
        // lazily create the action
        if (this.deleteHostAction == null) {
            this.deleteHostAction = new DeleteHostAction(this.simulator);
        }
        return this.deleteHostAction;
    }

    /**
     * The graph deletion action permanently associated with this simulator.
     */
    private DeleteHostAction deleteHostAction;

    /**
     * Returns the rule deletion action permanently associated with this
     * simulator.
     */
    public DeleteRuleAction getDeleteRuleAction() {
        // lazily create the action
        if (this.deleteRuleAction == null) {
            this.deleteRuleAction = new DeleteRuleAction(this.simulator);
        }
        return this.deleteRuleAction;
    }

    /**
     * The rule deletion action permanently associated with this simulator.
     */
    private DeleteRuleAction deleteRuleAction;

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link DeleteTypeAction}.
     */
    public DeleteTypeAction getDeleteTypeAction() {
        if (this.deleteTypeAction == null) {
            this.deleteTypeAction = new DeleteTypeAction(this.simulator);
        }
        return this.deleteTypeAction;
    }

    /** Singular instance of the DeleteTypeAction. */
    private DeleteTypeAction deleteTypeAction;

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link NewControlAction}.
     */
    public DisableControlAction getDisableControlAction() {
        if (this.disableControlAction == null) {
            this.disableControlAction =
                new DisableControlAction(this.simulator);
        }
        return this.disableControlAction;
    }

    /** Singular instance of the EnableAction. */
    private DisableControlAction disableControlAction;

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link EnableTypesAction}.
     */
    public EnableTypesAction getDisableTypesAction() {
        if (this.disableTypesAction == null) {
            this.disableTypesAction =
                new EnableTypesAction(this.simulator, false);
        }
        return this.disableTypesAction;
    }

    /** Singular instance of the EnableTypesAction. */
    private EnableTypesAction disableTypesAction;

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link NewControlAction}.
     */
    public EditControlAction getEditAction() {
        if (this.editControlAction == null) {
            this.editControlAction = new EditControlAction(this.simulator);
        }
        return this.editControlAction;
    }

    /** Singular instance of the EditAction. */
    private EditControlAction editControlAction;

    /**
     * Lazily creates and returns the state edit action permanently associated
     * with this simulator.
     */
    public EditHostOrStateAction getEditHostOrStateAction() {
        // lazily create the action
        if (this.editHostOrStateAction == null) {
            this.editHostOrStateAction =
                new EditHostOrStateAction(this.simulator);
        }
        return this.editHostOrStateAction;
    }

    /**
     * The state edit action permanently associated with this simulator.
     */
    private EditHostOrStateAction editHostOrStateAction;

    /**
     * Returns the properties edit action permanently associated with this
     * simulator.
     */
    public EditRulePropertiesAction getEditRulePropertiesAction() {
        // lazily create the action
        if (this.editRulePropertiesAction == null) {
            this.editRulePropertiesAction =
                new EditRulePropertiesAction(this.simulator);
        }
        return this.editRulePropertiesAction;
    }

    /**
     * The rule properties edit action permanently associated with this
     * simulator.
     */
    private EditRulePropertiesAction editRulePropertiesAction;

    /**
     * Lazily creates and returns the rule edit action permanently associated
     * with this simulator.
     */
    public EditRuleAction getEditRuleAction() {
        // lazily create the action
        if (this.editRuleAction == null) {
            this.editRuleAction = new EditRuleAction(this.simulator);
        }
        return this.editRuleAction;
    }

    /**
     * The rule edit action permanently associated with this simulator.
     */
    private EditRuleAction editRuleAction;

    /** Returns the action to show the system properties of the current grammar. */
    public Action getEditSystemPropertiesAction() {
        // lazily create the action
        if (this.editSystemPropertiesAction == null) {
            this.editSystemPropertiesAction =
                new EditSystemPropertiesAction(this.simulator);
        }
        return this.editSystemPropertiesAction;
    }

    /**
     * The action to show the system properties of the currently selected
     * grammar.
     */
    private EditSystemPropertiesAction editSystemPropertiesAction;

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link EditTypeAction}.
     */
    public EditTypeAction getEditTypeAction() {
        if (this.editTypeAction == null) {
            this.editTypeAction = new EditTypeAction(this.simulator);
        }
        return this.editTypeAction;
    }

    /** Singular instance of the EditTypeAction. */
    private EditTypeAction editTypeAction;

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link NewControlAction}.
     */
    public EnableControlAction getEnableControlAction() {
        if (this.enableControlAction == null) {
            this.enableControlAction = new EnableControlAction(this.simulator);
        }
        return this.enableControlAction;
    }

    /** Singular instance of the EnableAction. */
    private EnableControlAction enableControlAction;

    /**
     * Lazily creates and returns the appropriate instance of the
     * {@link EnableTypesAction}.
     */
    public EnableTypesAction getEnableTypesAction() {
        if (this.enableTypesAction == null) {
            this.enableTypesAction =
                new EnableTypesAction(this.simulator, true);
        }
        return this.enableTypesAction;
    }

    /** Singular instance of the CheckAllAction. */
    private EnableTypesAction enableTypesAction;

    /**
     * Returns the rule enabling action permanently associated with this
     * simulator.
     */
    public EnableRuleAction getEnableRuleAction() {
        // lazily create the action
        if (this.enableRuleAction == null) {
            this.enableRuleAction = new EnableRuleAction(this.simulator);
        }
        return this.enableRuleAction;
    }

    /**
     * The rule enabling action permanently associated with this simulator.
     */
    private EnableRuleAction enableRuleAction;

    /**
     * Returns the 'default exploration' action that is associated with the
     * simulator.
     */
    public ExploreAction getExploreAction() {
        // lazily create the action
        if (this.exploreAction == null) {
            this.exploreAction = new ExploreAction(this.simulator);
        }

        return this.exploreAction;
    }

    /**
     * The 'default exploration' action (variable).
     */
    private ExploreAction exploreAction;

    /**
     * Returns the exploration dialog action permanently associated with this
     * simulator.
     */
    public ExplorationDialogAction getExplorationDialogAction() {
        // lazily create the action
        if (this.explorationDialogAction == null) {
            this.explorationDialogAction =
                new ExplorationDialogAction(this.simulator);
        }
        return this.explorationDialogAction;
    }

    /**
     * The exploration dialog action permanently associated with this simulator.
     */
    private ExplorationDialogAction explorationDialogAction;

    /**
     * Returns the exploration statistics dialog action permanently associated
     * with this simulator.
     */
    public ExplorationStatsDialogAction getExplorationStatsDialogAction() {
        // lazily create the action
        if (this.explorationStatsDialogAction == null) {
            this.explorationStatsDialogAction =
                new ExplorationStatsDialogAction(this.simulator);
        }
        return this.explorationStatsDialogAction;
    }

    /**
     * The exploration statistics dialog action permanently associated with
     * this simulator.
     */
    private ExplorationStatsDialogAction explorationStatsDialogAction;

    /**
     * Returns the forward (= repeat) simulation action permanently associated
     * with this simulator.
     */
    public Action getForwardAction() {
        if (this.forwardAction == null) {
            this.forwardAction =
                this.simulator.getSimulationHistory().getForwardAction();
        }
        return this.forwardAction;
    }

    /**
     * The forward simulation action permanently associated with this simulator.
     */
    private Action forwardAction;

    /**
     * Returns the go-to start state action permanently associated with this
     * simulator.
     */
    public GotoStartStateAction getGotoStartStateAction() {
        // lazily create the action
        if (this.gotoStartStateAction == null) {
            this.gotoStartStateAction =
                new GotoStartStateAction(this.simulator);
        }
        return this.gotoStartStateAction;
    }

    /**
     * The go-to start state action permanently associated with this simulator.
     */
    private GotoStartStateAction gotoStartStateAction;

    /** Returns the import action permanently associated with this simulator. */
    public ImportAction getImportAction() {
        // lazily create the action
        if (this.importAction == null) {
            this.importAction = new ImportAction(this.simulator);
        }
        return this.importAction;
    }

    /** The import action permanently associated with this simulator. */
    private ImportAction importAction;

    /**
     * Returns the grammar load action permanently associated with this
     * simulator.
     */
    public LoadGrammarAction getLoadGrammarAction() {
        // lazily create the action
        if (this.loadGrammarAction == null) {
            this.loadGrammarAction = new LoadGrammarAction(this.simulator);
        }
        return this.loadGrammarAction;
    }

    /** The grammar load action permanently associated with this simulator. */
    private LoadGrammarAction loadGrammarAction;

    /**
     * Returns the grammar load action permanently associated with this
     * simulator.
     */
    public Action getLoadGrammarFromURLAction() {
        // lazily create the action
        if (this.loadGrammarFromURLAction == null) {
            this.loadGrammarFromURLAction =
                new LoadGrammarFromURLAction(this.simulator);
        }
        return this.loadGrammarFromURLAction;
    }

    /** The grammar load action permanently associated with this simulator. */
    private LoadGrammarFromURLAction loadGrammarFromURLAction;

    /**
     * Returns the start graph load action permanently associated with this
     * simulator.
     */
    public LoadStartGraphAction getLoadStartGraphAction() {
        // lazily create the action
        if (this.loadStartGraphAction == null) {
            this.loadStartGraphAction =
                new LoadStartGraphAction(this.simulator);
        }
        return this.loadStartGraphAction;
    }

    /** The start state load action permanently associated with this simulator. */
    private LoadStartGraphAction loadStartGraphAction;

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link NewControlAction}.
     */
    public NewControlAction getNewControlAction() {
        if (this.newControlAction == null) {
            this.newControlAction = new NewControlAction(this.simulator);
        }
        return this.newControlAction;
    }

    /** Singular instance of the NewAction. */
    private NewControlAction newControlAction;

    /**
     * Returns the rule system creation action permanently associated with this
     * simulator.
     */
    public NewGrammarAction getNewGrammarAction() {
        // lazily create the action
        if (this.newGrammarAction == null) {
            this.newGrammarAction = new NewGrammarAction(this.simulator);
        }
        return this.newGrammarAction;
    }

    /**
     * The rule system creation action permanently associated with this
     * simulator.
     */
    private NewGrammarAction newGrammarAction;

    /**
     * Returns the graph creation action permanently associated with this
     * simulator.
     */
    public NewHostAction getNewHostAction() {
        // lazily create the action
        if (this.newHostAction == null) {
            this.newHostAction = new NewHostAction(this.simulator);
        }
        return this.newHostAction;
    }

    /**
     * The graph creation action permanently associated with this simulator.
     */
    private NewHostAction newHostAction;

    /**
     * Returns the rule creation action permanently associated with this
     * simulator.
     */
    public NewRuleAction getNewRuleAction() {
        // lazily create the action
        if (this.newRuleAction == null) {
            this.newRuleAction = new NewRuleAction(this.simulator);
        }
        return this.newRuleAction;
    }

    /**
     * The rule creation action permanently associated with this simulator.
     */
    private NewRuleAction newRuleAction;

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link NewTypeAction}.
     */
    public NewTypeAction getNewTypeAction() {
        if (this.newTypeAction == null) {
            this.newTypeAction = new NewTypeAction(this.simulator);
        }
        return this.newTypeAction;
    }

    /** Singular instance of the NewTypeAction. */
    private NewTypeAction newTypeAction;

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link NewControlAction}.
     */
    public PreviewControlAction getPreviewControlAction() {
        if (this.previewControlAction == null) {
            this.previewControlAction =
                new PreviewControlAction(this.simulator);
        }
        return this.previewControlAction;
    }

    /** Singular instance of the CtrlPreviewAction. */
    private PreviewControlAction previewControlAction;

    /** Returns the quit action permanently associated with this simulator. */
    public SimulatorAction getQuitAction() {
        // lazily create the action
        if (this.quitAction == null) {
            this.quitAction = new QuitAction(this.simulator);
        }
        return this.quitAction;
    }

    /**
     * The quit action permanently associated with this simulator.
     */
    private QuitAction quitAction;

    /**
     * Returns the redo action permanently associated with this simulator.
     */
    public RedoSimulatorAction getRedoAction() {
        if (this.redoAction == null) {
            this.redoAction = new RedoSimulatorAction(this.simulator);
        }
        return this.redoAction;
    }

    /**
     * The redo permanently associated with this simulator.
     */
    private RedoSimulatorAction redoAction;

    /**
     * Returns the grammar refresh action permanently associated with this
     * simulator.
     */
    public RefreshGrammarAction getRefreshGrammarAction() {
        // lazily create the action
        if (this.refreshGrammarAction == null) {
            this.refreshGrammarAction =
                new RefreshGrammarAction(this.simulator);
        }
        return this.refreshGrammarAction;
    }

    /** The grammar refresh action permanently associated with this simulator. */
    private RefreshGrammarAction refreshGrammarAction;

    /**
     * Returns the rule renaming action permanently associated with this
     * simulator.
     */
    public RelabelGrammarAction getRelabelAction() {
        // lazily create the action
        if (this.relabelAction == null) {
            this.relabelAction = new RelabelGrammarAction(this.simulator);
        }
        return this.relabelAction;
    }

    /**
     * The graph renaming action permanently associated with this simulator.
     */
    private RelabelGrammarAction relabelAction;

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link RenameControlAction}.
     */
    public RenameControlAction getRenameControlAction() {
        if (this.renameControlAction == null) {
            this.renameControlAction = new RenameControlAction(this.simulator);
        }
        return this.renameControlAction;
    }

    /** Singular instance of the RenameAction. */
    private RenameControlAction renameControlAction;

    /**
     * Returns the rule renaming action permanently associated with this
     * simulator.
     */
    public RenameHostAction getRenameGraphAction() {
        // lazily create the action
        if (this.renameGraphAction == null) {
            this.renameGraphAction = new RenameHostAction(this.simulator);
        }
        return this.renameGraphAction;
    }

    /**
     * The graph renaming action permanently associated with this simulator.
     */
    private RenameHostAction renameGraphAction;

    /**
     * Returns the rule renaming action permanently associated with this
     * simulator.
     */
    public RenameRuleAction getRenameRuleAction() {
        // lazily create the action
        if (this.renameRuleAction == null) {
            this.renameRuleAction = new RenameRuleAction(this.simulator);
        }
        return this.renameRuleAction;
    }

    /**
     * The rule renaming action permanently associated with this simulator.
     */
    private RenameRuleAction renameRuleAction;

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link RenameTypeAction}.
     */
    public RenameTypeAction getRenameTypeAction() {
        if (this.renameTypeAction == null) {
            this.renameTypeAction = new RenameTypeAction(this.simulator);
        }
        return this.renameTypeAction;
    }

    /** Singular instance of the RenameTypeAction. */
    private RenameTypeAction renameTypeAction;

    /**
     * Returns the renumbering action permanently associated with this
     * simulator.
     */
    public RenumberGrammarAction getRenumberAction() {
        // lazily create the action
        if (this.renumberAction == null) {
            this.renumberAction = new RenumberGrammarAction(this.simulator);
        }
        return this.renumberAction;
    }

    /**
     * The renumbering action permanently associated with this simulator.
     */
    private RenumberGrammarAction renumberAction;

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link NewControlAction}.
     */
    public SaveControlAction getSaveControlAction() {
        if (this.saveControlAction == null) {
            this.saveControlAction = new SaveControlAction(this.simulator);
        }
        return this.saveControlAction;
    }

    /** Singular instance of the SaveAction. */
    private SaveControlAction saveControlAction;

    /**
     * Returns the graph save action permanently associated with this simulator.
     */
    public SaveGrammarAction getSaveGrammarAction() {
        // lazily create the action
        if (this.saveGrammarAction == null) {
            this.saveGrammarAction = new SaveGrammarAction(this.simulator);
        }
        return this.saveGrammarAction;
    }

    /**
     * The grammar save action permanently associated with this simulator.
     */
    private SaveGrammarAction saveGrammarAction;

    /**
     * Returns the graph save action permanently associated with this simulator.
     */
    public SaveSimulatorAction getSaveGraphAction() {
        // lazily create the action
        if (this.saveGraphAction == null) {
            this.saveGraphAction = new SaveSimulatorAction(this.simulator);
        }
        return this.saveGraphAction;
    }

    /**
     * The state save action permanently associated with this simulator.
     */
    private SaveSimulatorAction saveGraphAction;

    /**
     * Returns the host graph save action permanently associated with this simulator.
     */
    public SaveHostAction getSaveHostGraphAction() {
        // lazily create the action
        if (this.saveHostGraphAction == null) {
            this.saveHostGraphAction = new SaveHostAction(this.simulator);
        }
        return this.saveHostGraphAction;
    }

    /**
     * The host graph save action permanently associated with this simulator.
     */
    private SaveHostAction saveHostGraphAction;

    /**
     * Returns the Save LTS As action permanently associated with this simulator.
     */
    public SaveLTSAsAction getSaveLTSAsAction() {
        // lazily create the action
        if (this.saveLtsAsAction == null) {
            this.saveLtsAsAction = new SaveLTSAsAction(this.simulator);
        }
        return this.saveLtsAsAction;
    }

    /** The LTS Save As action permanently associated with this simulator. */
    private SaveLTSAsAction saveLtsAsAction;

    /**
     * Returns the undo action permanently associated with this simulator.
     */
    public Action getSelectColorAction() {
        if (this.selectColorAction == null) {
            this.selectColorAction = new SelectColorAction(this.simulator);
        }
        return this.selectColorAction;
    }

    /**
     * The undo action permanently associated with this simulator.
     */
    private SelectColorAction selectColorAction;

    /**
     * Lazily creates and returns an instance of SetStartGraphAction.
     */
    public Action getSetStartGraphAction() {
        // lazily create the action
        if (this.setStartGraphAction == null) {
            this.setStartGraphAction = new SetStartGraphAction(this.simulator);
        }
        return this.setStartGraphAction;
    }

    /** Singleton instance of {@link SetStartGraphAction}. */
    private SetStartGraphAction setStartGraphAction;

    /**
     * Lazily creates and returns an instance of
     * {@link StartSimulationAction}.
     */
    public SimulatorAction getStartSimulationAction() {
        // lazily create the action
        if (this.startSimulationAction == null) {
            this.startSimulationAction =
                new StartSimulationAction(this.simulator);
        }
        return this.startSimulationAction;
    }

    /** The action to start a new simulation. */
    private StartSimulationAction startSimulationAction;

    /**
     * Lazily creates and returns an instance of
     * {@link ToggleExplorationStateAction}.
     */
    public Action getToggleExplorationStateAction() {
        if (this.toggleExplorationStateAction == null) {
            this.toggleExplorationStateAction =
                new ToggleExplorationStateAction(this.simulator);
        }
        return this.toggleExplorationStateAction;
    }

    /** The action to toggle between concrete and abstract exploration. */
    private ToggleExplorationStateAction toggleExplorationStateAction;

    /**
     * Returns the undo action permanently associated with this simulator.
     */
    public UndoSimulatorAction getUndoAction() {
        if (this.undoAction == null) {
            this.undoAction = new UndoSimulatorAction(this.simulator);
        }
        return this.undoAction;
    }

    /**
     * The undo action permanently associated with this simulator.
     */
    private UndoSimulatorAction undoAction;
}
