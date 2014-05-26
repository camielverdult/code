package groove.gui.jgraph;

import groove.control.CtrlFrame;
import groove.graph.Node;
import groove.gui.look.Look;
import groove.gui.look.VisualKey;
import groove.io.HTMLConverter;
import groove.lts.GTS;
import groove.lts.GraphState;

/**
 * JVertex class that describes the underlying node as a graph state.
 * @author Arend Rensink
 * @version $Revision $
 */
public class LTSJVertex extends AJVertex<GTS,LTSJGraph,LTSJModel,LTSJEdge> implements LTSJCell {
    /**
     * Creates a new, uninitialised instance.
     * Call {@link #setJModel(JModel)} and {@link #setNode(Node)} to initialise.
     */
    private LTSJVertex() {
        // empty
    }

    @Override
    public GraphState getNode() {
        return (GraphState) super.getNode();
    }

    @Override
    protected void initialise() {
        super.initialise();
        this.visibleFlag = true;
        GraphState state = getNode();
        if (state != null) {
            setLook(Look.OPEN, !state.isClosed());
            setLook(Look.ABSENT, state.isAbsent());
            setLook(Look.RECIPE, state.isRecipeState());
            setLook(Look.FINAL, state.getGTS().isFinal(state));
            setLook(Look.RESULT, state.getGTS().isResult(state));
        }
    }

    @Override
    public void setVisibleFlag(boolean visible) {
        this.visibleFlag = visible;
        setStale(VisualKey.VISIBLE);
    }

    @Override
    public boolean hasVisibleFlag() {
        return this.visibleFlag;
    }

    @Override
    StringBuilder getNodeDescription() {
        StringBuilder result = new StringBuilder("State ");
        result.append(HTMLConverter.UNDERLINE_TAG.on(getNode()));
        CtrlFrame frame = getNode().getPrimeFrame();
        if (!frame.isStart()) {
            result.append(" with control state ");
            result.append(HTMLConverter.UNDERLINE_TAG.on(frame));
        }
        return result;
    }

    /**
     * @return true if the state is a result state.
     */
    public boolean isResult() {
        return getNode().getGTS().isResult(getNode());
    }

    /**
     * @return true if the state is an error state.
     */
    @Override
    public boolean hasErrors() {
        return getNode().isError();
    }

    /**
     * @return true if the state is a start state.
     */
    public boolean isStart() {
        GTS gts = getNode().getGTS();
        return gts.startState().equals(getNode());
    }

    /**
     * Returns {@code true} if the state is closed.
     */
    public boolean isClosed() {
        return getNode().isClosed();
    }

    /**
     * Returns {@code true} if the state is a sub-stage.
     */
    public boolean inRecipe() {
        return getNode().isRecipeState();
    }

    /**
     * @return true if the state is final.
     */
    public boolean isFinal() {
        GTS gts = getNode().getGTS();
        return gts.isFinal(getNode());
    }

    @Override
    public String getNodeIdString() {
        String result = super.getNodeIdString();
        CtrlFrame frame = getNode().getPrimeFrame();
        if (!frame.isStart()) {
            result += "|" + frame.toString();
        }
        return result;
    }

    /** Indicates that this edge is active. */
    final boolean isActive() {
        return getLooks().contains(Look.ACTIVE);
    }

    /** Changes the active status of this edge.
     * @return {@code true} if the active status changed as a result of this call.
     */
    @Override
    public final boolean setActive(boolean active) {
        return setLook(Look.ACTIVE, active);
    }

    @Override
    protected Look getStructuralLook() {
        if (isStart()) {
            return Look.START;
        } else {
            return Look.STATE;
        }
    }

    private boolean visibleFlag;

    /** 
     * Returns a fresh instance.
     * Call {@link #setJModel(JModel)} and {@link #setNode(Node)} to initialise.
     */
    public static LTSJVertex newInstance() {
        return new LTSJVertex();
    }
}