package groove.gui.action;

import groove.gui.Options;
import groove.gui.Simulator;
import groove.lts.GTS;
import groove.lts.GraphState;

import java.util.Collection;
import java.util.Iterator;

/**
 * This action will perform random linear exploration, set the state graph to the final state 
 * and apply the layouter to this graph. This can be quite useful if you need a quick way 
 * to show an end result of a graph grammar execution.
 * @author Tim Molderez
 */
public class GotoFinalStateAction extends SimulatorAction {
    /** Constructs a new action, for a given simulator. */
    public GotoFinalStateAction(Simulator simulator, boolean animated) {
        super(simulator, Options.GOTO_FINAL_STATE_ACTION_NAME, null);
        putValue(ACCELERATOR_KEY, Options.GOTO_FINAL_STATE_KEY);
        getSimulator().addAccelerator(this);
    }

    @Override
    public void execute() {
        // Backup exploration
        Collection<GraphState> finalStates = getFinalStates();
        if (finalStates.isEmpty()) {
            getActions().getExploreAction().execute();
            finalStates = getFinalStates();
        }
        Iterator<GraphState> it = finalStates.iterator();
        GraphState state;
        if (it.hasNext()) {
            GraphState current = getSimulatorModel().getState();
            state = null;
            // look for the current final state within the iterator
            while (state != current && it.hasNext()) {
                state = it.next();
            }
            if (it.hasNext()) {
                state = it.next();
            } else {
                state = finalStates.iterator().next();
            }
        } else {
            state = getSimulatorModel().getExploration().getLastState();
            if (state == null) {
                state = getSimulatorModel().getGts().startState();
            }
        }
        getSimulatorModel().setState(state);
    }

    /**
     * Returns either the set of result states or, if that is empty,
     * the set of final states of the GTS.
     */
    private Collection<GraphState> getFinalStates() {
        GTS gts = getSimulatorModel().getGts();
        Collection<GraphState> result = gts.getFinalStates();
        if (result.isEmpty()) {
            result = gts.getResultStates();
        }
        return result;
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getGts() != null);
    }
}