package groove.gui;

import groove.gui.SimulatorModel.Change;
import groove.gui.action.LoadGrammarFromHistoryAction;
import groove.io.store.SystemStore;
import groove.io.store.SystemStoreFactory;
import groove.trans.SystemProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.JMenu;

/** Class wrapping a menu of recently opened files. */
class SimulatorHistory implements SimulatorListener {
    /** Constructs a fresh history instance. */
    public SimulatorHistory(Simulator simulator) {
        this.simulator = simulator;
        simulator.getModel().addListener(this);
        String[] savedLocations =
            Options.userPrefs.get(SystemProperties.HISTORY_KEY, "").split(
                ",");
        for (String location : savedLocations) {
            try {
                this.history.add(createLoadAction(location, null));
            } catch (IOException exc) {
                // if we can't load from a location, just
                // omit it from the history
            }
        }

        this.menu.setText(Options.OPEN_RECENT_MENU_NAME);
        this.menu.setMnemonic(Options.OPEN_RECENT_MENU_MNEMONIC);

        synchMenu();
    }

    /**
     * Returns a JMenu that will reflect the current history. The menu is
     * updated when a grammar is loaded.
     */
    public JMenu getOpenRecentMenu() {
        return this.menu;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        if (changes.contains(Change.GRAMMAR)) {
            try {
                Object location = source.getStore().getLocation();
                String startGraphName =
                    source.getGrammar().getStartGraphName();
                LoadGrammarFromHistoryAction newAction =
                    createLoadAction(location.toString(), startGraphName);
                this.history.remove(newAction);
                this.history.add(0, newAction);
                // trimming list to 10 elements
                while (this.history.size() > 10) {
                    this.history.remove(10);
                }
                synch();
            } catch (IOException exc) {
                // if we can't load from a location, just
                // omit it from the history
            }
        }
    }

    private LoadGrammarFromHistoryAction createLoadAction(String location,
            String startGraphName) throws IOException {
        SystemStore store = SystemStoreFactory.newStore(location);
        return new LoadGrammarFromHistoryAction(this.simulator, location,
            startGraphName, store);
    }

    private void synch() {
        synchPrefs();
        synchMenu();
    }

    private void synchPrefs() {
        String newStr = makeHistoryString();
        Options.userPrefs.put(SystemProperties.HISTORY_KEY, newStr);
    }

    private void synchMenu() {
        this.menu.removeAll();
        for (LoadGrammarFromHistoryAction action : this.history) {
            this.menu.add(action);
        }
    }

    private String makeHistoryString() {
        StringBuilder result = new StringBuilder();
        for (LoadGrammarFromHistoryAction action : this.history) {
            if (result.length() > 0) {
                result.append(",");
            }
            result.append(action.getLocation());
        }
        return result.toString();
    }

    private final Simulator simulator;
    /** Menu of history items. */
    private final JMenu menu = new JMenu();
    /** List of load actions corresponding to the history items. */
    private final ArrayList<LoadGrammarFromHistoryAction> history =
        new ArrayList<LoadGrammarFromHistoryAction>();
}