package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.StatePanel;
import groove.view.aspect.AspectGraph;

import java.io.IOException;

import javax.swing.SwingUtilities;

/**
 * Action for editing the current host graph or state.
 */
public class EditHostOrStateAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public EditHostOrStateAction(Simulator simulator) {
        super(simulator, Options.EDIT_STATE_ACTION_NAME, Icons.EDIT_ICON);
        putValue(ACCELERATOR_KEY, Options.EDIT_KEY);
    }

    /**
     * Checks if the enabling condition is satisfied, and if so, calls
     * {@link #setEnabled(boolean)}.
     */
    @Override
    public void refresh() {
        boolean enabled =
            getModel().getHost() != null || getModel().getState() != null;
        if (enabled != isEnabled()) {
            setEnabled(enabled);
        }
    }

    /**
     * Invokes the editor on the current state. Handles the execution of an
     * <code>EditHostOrStateAction</code>, if the current panel is the state
     * panel.
     */
    @Override
    public boolean execute() {
        AspectGraph graph = getStatePanel().getJModel().getGraph();
        // find out if we're editing a host graph or a state
        if (getModel().getHost() == null) {
            String newGraphName =
                askNewGraphName("Select graph name", graph.getName(), true);
            if (newGraphName != null) {
                final AspectGraph newGraph = graph.rename(newGraphName);
                try {
                    getModel().doAddHost(newGraph);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            getPanel().doEditGraph(newGraph);
                        }
                    });
                } catch (IOException e) {
                    showErrorDialog(e, "Can't edit state '%s'", graph.getName());
                }
            }
        } else {
            getPanel().doEditGraph(graph);
        }
        return false;
    }

    private StatePanel getStatePanel() {
        return getSimulator().getStatePanel();
    }
}