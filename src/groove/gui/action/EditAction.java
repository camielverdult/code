package groove.gui.action;

import static groove.trans.ResourceKind.HOST;
import groove.gui.EditType;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.trans.ResourceKind;

/** Action to start editing the currently displayed resource. */
public class EditAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public EditAction(Simulator simulator, ResourceKind resource) {
        super(simulator, EditType.MODIFY, resource);
        putValue(ACCELERATOR_KEY, Options.EDIT_KEY);
        this.editStateAction = simulator.getActions().getEditStateAction();
    }

    @Override
    public void execute() {
        if (isForState()) {
            this.editStateAction.execute();
        } else {
            for (String name : getSimulatorModel().getSelectSet(
                getResourceKind())) {
                getDisplay().startEditResource(name);
            }
        }
    }

    @Override
    public void refresh() {
        boolean enabled =
            getSimulatorModel().isSelected(getResourceKind()) || isForState();
        setEnabled(enabled);
        if (getResourceKind() == HOST) {
            String name =
                isForState() ? (String) this.editStateAction.getValue(NAME)
                        : getEditActionName();
            putValue(NAME, name);
            putValue(SHORT_DESCRIPTION, name);
        }
    }

    private boolean isForState() {
        return getResourceKind() == HOST && getSimulatorModel().hasState()
            && !getSimulatorModel().isSelected(HOST);
    }

    private final EditStateAction editStateAction;
}