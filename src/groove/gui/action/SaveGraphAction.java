package groove.gui.action;

import static groove.graph.GraphRole.RULE;
import groove.graph.GraphRole;
import groove.gui.GraphEditorTab;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.io.ExtensionFilter;
import groove.io.FileType;
import groove.io.xml.AspectGxl;
import groove.trans.ResourceKind;
import groove.view.RuleModel;
import groove.view.TypeModel;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.IOException;

import javax.swing.Action;

/**
 * Action to save the graph (host graph, rule graph of type graph) in
 * an editor panel.
 * @author Arend Rensink
 * @version $Revision $
 */
public final class SaveGraphAction extends SimulatorAction {
    /** Creates an instance of the action for a given editor panel. */
    public SaveGraphAction(GraphEditorTab editor) {
        this(editor.getSimulator(), editor, false);
    }

    /** 
     * Creates an instance of the action for a simulator.
     * @param role role of the graph that this action is supposed to save:
     * one of {@link GraphRole#HOST}, {@link GraphRole#RULE} or {@link GraphRole#TYPE}.
     * @param saveAs flag indicating that the action attempts to save to
     * a file outside the grammar.
     */
    public SaveGraphAction(Simulator simulator, GraphRole role, boolean saveAs) {
        this(simulator, (GraphEditorTab) null, saveAs);
        this.role = role;
    }

    /** 
     * Internal constructor.
     */
    private SaveGraphAction(Simulator simulator, GraphEditorTab editor,
            boolean saveAs) {
        super(simulator, saveAs ? Options.SAVE_AS_ACTION_NAME
                : Options.SAVE_ACTION_NAME, saveAs ? Icons.SAVE_AS_ICON
                : Icons.SAVE_ICON);
        this.saveAs = saveAs;
        this.editor = editor;
        putValue(Action.ACCELERATOR_KEY, Options.SAVE_KEY);
    }

    @Override
    public boolean execute() {
        boolean result = false;
        if (confirmBehaviourOption(Options.STOP_SIMULATION_OPTION)) {
            if (this.saveAs) {
                result = doSaveAs(getGraph());
            } else {
                result = doSave(getGraph());
            }
        }
        return result;
    }

    /**
     * Stores the graph within the grammar.
     * @return {@code true} if the simulation was invalidated as a 
     * consequence of this save action
     */
    public boolean doSave(AspectGraph graph) {
        boolean result = false;
        ResourceKind resource = ResourceKind.toResource(graph.getRole());
        if (isForState()) {
            // we're saving a state
            String newName = askNewName(resource, graph.getName(), true);
            graph = newName == null ? null : graph.rename(newName);
        }
        try {
            if (graph != null) {
                result = getSimulatorModel().doAddGraph(resource, graph);
                if (this.editor != null) {
                    this.editor.setClean();
                }
            }
        } catch (IOException exc) {
            showErrorDialog(exc, "Error while saving edited graph '%s'",
                graph.getName());
        }
        return result;
    }

    /** Attempts to write the graph to an external file. */
    public boolean doSaveAs(AspectGraph graph) {
        boolean result = false;
        File selectedFile = askSaveGraph(graph);
        // now save, if so required
        if (selectedFile != null) {
            try {
                ExtensionFilter filter = FileType.getFilter(graph.getRole());
                // find out if this is within the grammar directory
                String selectedPath =
                    filter.stripExtension(selectedFile.getCanonicalPath());
                String nameInGrammar =
                    getNameInGrammar(selectedPath, graph.getRole() == RULE);
                if (nameInGrammar == null) {
                    // save in external file
                    String newName =
                        filter.stripExtension(selectedFile.getName());
                    AspectGxl.getInstance().marshalGraph(graph.rename(newName),
                        selectedFile);
                    result = true;
                } else {
                    // save within the grammar
                    result = doSave(graph.rename(nameInGrammar));
                }
            } catch (IOException exc) {
                showErrorDialog(exc, String.format(
                    "Error while writing graph to '%s'", selectedFile));
            }
        }
        return result;
    }

    @Override
    public void refresh() {
        boolean enabled;
        ResourceKind resource = ResourceKind.toResource(getRole());
        if (this.editor == null) {
            if (isForState()) {
                enabled = getSimulatorModel().hasState();
            } else {
                enabled = getSimulatorModel().isSelected(resource);
            }
        } else {
            enabled = this.editor.isDirty();
        }
        setEnabled(enabled);
        if (enabled) {
            String name =
                isForState() ? Options.getSaveStateActionName(this.saveAs)
                        : Options.getSaveActionName(resource, this.saveAs);
            if (this.saveAs) {
                putValue(NAME, name);
            }
            putValue(SHORT_DESCRIPTION, name);
        }
    }

    /**
     * Returns the graph to be saved.
     * Depending on whether this action is connected to an editor panel,
     * this is either the edited graph of the graph shown in the state panel.
     */
    private AspectGraph getGraph() {
        if (this.editor == null) {
            switch (getRole()) {
            case HOST:
                if (getSimulatorModel().hasHost()) {
                    return getSimulatorModel().getHost().getSource();
                } else {
                    return getStateDisplay().getStatePanel().getGraph();
                }
            case RULE:
                RuleModel rule = getSimulatorModel().getRule();
                return rule == null ? null : rule.getSource();
            case TYPE:
                TypeModel type = getSimulatorModel().getType();
                return type == null ? null : type.getSource();
            default:
                assert false;
                return null;
            }
        } else {
            return this.editor.getGraph();
        }
    }

    /** Returns the role of the graphs that this action saves.
     * This is either initialised in the constructor, or derived from
     * the underlying editor.
     */
    private GraphRole getRole() {
        if (this.role == null) {
            assert this.editor != null;
            this.role = this.editor.getGraph().getRole();
        }
        return this.role;
    }

    private boolean isForState() {
        return getRole() == GraphRole.HOST
            && (this.editor == null || !getSimulatorModel().hasHost());
    }

    private final GraphEditorTab editor;
    private GraphRole role;
    private final boolean saveAs;
}