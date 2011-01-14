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
 * $Id: AspectJGraph.java,v 1.10 2008-01-30 09:33:14 iovka Exp $
 */
package groove.gui.jgraph;

import groove.graph.GraphRole;
import groove.gui.Editor;
import groove.gui.Options;
import groove.gui.SetLayoutMenu;
import groove.gui.Simulator;
import groove.gui.layout.ForestLayouter;
import groove.gui.layout.SpringLayouter;
import groove.trans.RuleName;
import groove.trans.SystemProperties;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;

import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.AttributeMap.SerializableRectangle2D;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.PortView;

/**
 * Extension of {@link JGraph} that provides the proper popup menu.
 */
final public class AspectJGraph extends JGraph {
    /**
     * Creates a j-graph for a given simulator, with an initially empty j-model.
     */
    public AspectJGraph(Simulator simulator, GraphRole role) {
        super(simulator == null ? null : simulator.getOptions(),
            role != GraphRole.RULE);
        this.simulator = simulator;
        this.editor = null;
        assert role.inGrammar();
        this.graphRole = role;
        setExporter(simulator.getExporter());
    }

    /**
     * Creates a j-graph for a given simulator, with an initially empty j-model.
     */
    public AspectJGraph(Editor editor) {
        super(editor.getOptions(), false);
        this.simulator = null;
        this.editor = editor;
        this.graphRole = null;
        setMarqueeHandler(createMarqueeHandler());
        getGraphLayoutCache().setSelectsLocalInsertedCells(true);
        setCloneable(true);
        setConnectable(true);
        setDisconnectable(true);
        setExporter(editor.getExporter());
    }

    @Override
    public AspectJModel getModel() {
        return (AspectJModel) super.getModel();
    }

    @Override
    public AspectJModel newModel() {
        return new AspectJModel(AspectJVertex.getPrototype(this),
            AspectJEdge.getPrototype(this), this.editor);
    }

    /**
     * Indicates whether aspect prefixes should be shown for nodes and edges.
     */
    public final boolean isShowRemarks() {
        return getOptionValue(Options.SHOW_REMARKS_OPTION);
    }

    /**
     * Indicates whether aspect prefixes should be shown for nodes and edges.
     */
    public final boolean isShowAspects() {
        return getOptionValue(Options.SHOW_ASPECTS_OPTION);
    }

    /**
     * Indicates whether data nodes should be shown in the JGraph.
     * This is certainly the case if this model is editable.
     */
    public final boolean isShowValueNodes() {
        return hasActiveEditor() || getOptionValue(Options.SHOW_VALUE_NODES_OPTION);
    }

    /** Indicates that the JModel has an editor enabled. */
    public boolean hasActiveEditor() {
        return this.editor != null && !this.editor.isPreviewMode();
    }

    @Override
    public Simulator getSimulator() {
        return this.simulator;
    }

    /** 
     * Returns the editor with which this JGraph is associated.
     * May be {@code null} is the simulator is set instead.
     */
    public Editor getEditor() {
        return this.editor;
    }

    @Override
    SystemProperties getProperties() {
        SystemProperties result = super.getProperties();
        if (result == null) {
            result = getEditor().getProperties();
        }
        return result;
    }

    /** 
     * Returns the role of either the simulator or the editor,
     * whichever is set.
     */
    public GraphRole getGraphRole() {
        if (this.simulator == null) {
            return this.editor.getRole();
        } else {
            return this.graphRole;
        }
    }

    @Override
    public JMenu createPopupMenu(Point atPoint) {
        JMenu result = new JMenu("Popup");
        if (this.simulator != null) {
            switch (getGraphRole()) {
            case HOST:
                result.add(this.simulator.getApplyTransitionAction());
                result.addSeparator();
                result.add(this.simulator.getEditGraphAction());
                break;
            case RULE:
                JMenu setRuleMenu = createSetRuleMenu();
                setRuleMenu.setEnabled(getSimulator().getGrammarView() != null);
                result.add(setRuleMenu);
                result.addSeparator();
                result.add(this.simulator.getEditRuleAction());
                break;
            case TYPE:
                result.add(this.simulator.getEditTypeAction());
            }
        }
        addSubmenu(result, createEditMenu(atPoint));
        addSubmenu(result, super.createPopupMenu(atPoint));
        return result;
    }

    @Override
    public SetLayoutMenu createSetLayoutMenu() {
        if (this.editor != null) {
            SetLayoutMenu result =
                new SetLayoutMenu(this, new SpringLayouter());
            result.addLayoutItem(new ForestLayouter());
            return result;
        } else {
            return super.createSetLayoutMenu();
        }
    }

    /**
     * Returns a menu containing all known editing actions.
     * @param atPoint point at which the popup menu will appear
     */
    public JMenu createEditMenu(Point atPoint) {
        JMenu result = new JMenu("Edit");
        if (hasActiveEditor()) {
            result.add(getEditLabelAction());
            result.add(getAddPointAction(atPoint));
            result.add(getRemovePointAction(atPoint));
            result.add(getResetLabelPositionAction());
            result.add(createLineStyleMenu());
        }
        return result;
    }

    /**
     * Computes and returns a menu that allows setting the display to another
     * rule.
     */
    private JMenu createSetRuleMenu() {
        // add actions to set the rule display to each production rule
        JMenu setMenu = new JMenu("Set rule to") {
            @Override
            public void menuSelectionChanged(boolean selected) {
                super.menuSelectionChanged(selected);
                if (selected) {
                    removeAll();
                    for (RuleName ruleName : getSimulator().getGrammarView().getRuleNames()) {
                        add(createSetRuleAction(ruleName));
                    }
                }
            }
        };
        return setMenu;
    }

    /** Action to change the display to a given (named) rule. */
    private Action createSetRuleAction(final RuleName ruleName) {
        return new AbstractAction(ruleName.toString()) {
            public void actionPerformed(ActionEvent evt) {
                getSimulator().setRule(ruleName);
            }
        };
    }

    @Override
    protected String getExportActionName() {
        switch (getGraphRole()) {
        case HOST:
            return Options.EXPORT_GRAPH_ACTION_NAME;
        case RULE:
            return Options.EXPORT_RULE_ACTION_NAME;
        case TYPE:
            return Options.EXPORT_TYPE_ACTION_NAME;
        }
        throw new IllegalStateException();
    }

    @Override
    public void setEditable(boolean editable) {
        setCloneable(editable);
        setConnectable(editable);
        setDisconnectable(editable);
        super.setEditable(editable);
    }

    /**
     * This implementation returns a {@link EditorMarqueeHandler}.
     * @see groove.gui.jgraph.JGraph#createMarqueeHandler()
     */
    @Override
    protected EditorMarqueeHandler createMarqueeHandler() {
        return new EditorMarqueeHandler(this);
    }

    /**
     * Adds a j-vertex to the j-graph, and positions it at a given point. The
     * point is in screen coordinates
     * @param screenPoint the intended central point for the new j-vertex
     */
    void addVertex(Point2D screenPoint) {
        stopEditing();
        Point2D atPoint = fromScreen(snap(screenPoint));
        // define the j-cell to be inserted
        AspectJVertex jVertex = getModel().computeJVertex();
        jVertex.setNodeFixed();
        // set the bounds and store them in the cell
        Dimension size = JAttr.DEFAULT_NODE_SIZE;
        Point2D corner =
            new Double(atPoint.getX() - (double) size.width / 2
                - JAttr.EXTRA_BORDER_SPACE, atPoint.getY()
                - (double) size.height / 2 - JAttr.EXTRA_BORDER_SPACE);
        GraphConstants.setBounds(
            jVertex.getAttributes(),
            new SerializableRectangle2D(corner.getX(), corner.getY(),
                size.getWidth(), size.getHeight()));
        // add the cell to the jGraph
        Object[] insert = new Object[] {jVertex};
        getModel().insert(insert, null, null, null, null);
        setSelectionCell(jVertex);
        // immediately add a label, if so indicated by startEditingNewNode
        if (this.startEditingNewNode) {
            startEditingAtCell(jVertex);
        }
    }

    /**
     * Adds an edge beteen two given points. The edge actually goes from the
     * vertices underlying the points. The end point may not be at a vertex, in
     * which case a self-edge should be drawn. The points are given in screen
     * coordinates.
     * @param screenFrom The start point of the new edge
     * @param screenTo The end point of the new edge
     */
    void addEdge(Point2D screenFrom, Point2D screenTo) {
        stopEditing();
        // translate screen coordinates to real coordinates
        PortView fromPortView =
            getPortViewAt(screenFrom.getX(), screenFrom.getY());
        PortView toPortView = getPortViewAt(screenTo.getX(), screenTo.getY());
        Point2D from = fromScreen((Point2D) screenFrom.clone());
        Point2D to = fromScreen((Point2D) screenTo.clone());
        assert fromPortView != null : "addEdge should not be called with dangling source "
            + from;
        DefaultPort fromPort = (DefaultPort) fromPortView.getCell();
        // if toPortView is null, we're drawing a self-edge
        DefaultPort toPort =
            toPortView == null ? fromPort : (DefaultPort) toPortView.getCell();
        // define the edge to be inserted
        AspectJEdge newEdge = getModel().computeJEdge();
        // to make sure there is at least one graph edge wrapped by this JEdge,
        // we add a dummy edge label to the JEdge's user object
        Object[] insert = new Object[] {newEdge};
        // define connections between edge and nodes, if any
        ConnectionSet cs = new ConnectionSet();
        cs.connect(newEdge, fromPort, true);
        cs.connect(newEdge, toPort, false);
        // if we're drawing a self-edge, provide some intermediate points
        if (toPort == fromPort) {
            AttributeMap edgeAttr = newEdge.getAttributes();
            ArrayList<Point2D> endpointList = new ArrayList<Point2D>(4);
            endpointList.add(from);
            // this middle point is there to provide a vector for
            // the direction and size of the self-loop
            endpointList.add(to);
            endpointList.add(to);
            GraphConstants.setPoints(edgeAttr, endpointList);
        }
        // add the cell to the jGraph
        getModel().insert(insert, null, cs, null, null);
        setSelectionCell(newEdge);
        // immediately add a label
        if (this.startEditingNewEdge) {
            startEditingAtCell(newEdge);
        }
    }

    /**
     * Callback method to determine whether an event concerns edge creation. To
     * be overridden by subclasses.
     * @param evt the event on the basis of which the judgement is made
     * @return <tt>true</tt> if edge creation mode is available and enabled
     */
    boolean isEdgeMode(MouseEvent evt) {
        boolean result = false;
        if (this.editor != null && this.editor.isEdgeMode()) {
            result = isVertex(getFirstCellForLocation(evt.getX(), evt.getY()));
        }
        return result;
    }

    /**
     * Callback method to determine whether an event concerns node creation. To
     * be overridden by subclasses.
     * @param evt evt the event on the basis of which the judgement is made
     * @return <tt>true</tt> if node creation mode is available and enabled
     */
    boolean isNodeMode(MouseEvent evt) {
        boolean result = false;
        if (evt.getButton() == MouseEvent.BUTTON1 && this.editor != null
            && this.editor.isNodeMode()) {
            result = getFirstCellForLocation(evt.getX(), evt.getY()) == null;
        }
        return result;
    }

    /**
     * Flag to indicate creating a node will immediately start editing the node
     * label
     */
    private final boolean startEditingNewNode = true;
    /**
     * Flag to indicate creating an edge will immediately start editing the edge
     * label
     */
    private final boolean startEditingNewEdge = true;
    /**
     * The simulator with which this j-graph is associated.
     * Either this or {@link #editor} is set.
     */
    private final Simulator simulator;
    /**
     * The editor with which this j-graph is associated.
     * Either this or {@link #simulator} is set.
     */
    private final Editor editor;

    /** The role for which this {@link JGraph} will display graphs. */
    private final GraphRole graphRole;

    /**
     * Abstract class for j-cell edit actions.
     */
    private abstract class JCellEditAction extends AbstractAction implements
            GraphSelectionListener {
        /**
         * Constructs an edit action that is enabled for all j-cells.
         * @param name the name of the action
         */
        protected JCellEditAction(String name) {
            super(name);
            this.allCells = true;
            this.vertexOnly = true;
            this.jCells = new ArrayList<GraphJCell>();
            this.setEnabled(false);
            addGraphSelectionListener(this);
        }

        /**
         * Constructs an edit action that is enabled for only j-vertices or
         * j-edges.
         * @param name the name of the action
         * @param vertexOnly <tt>true</tt> if the action is for j-vertices only
         */
        protected JCellEditAction(String name, boolean vertexOnly) {
            super(name);
            this.allCells = false;
            this.vertexOnly = vertexOnly;
            this.jCells = new ArrayList<GraphJCell>();
            this.setEnabled(false);
            addGraphSelectionListener(this);
        }

        /**
         * Sets the j-cell to the first selected cell. Disables the action if
         * the type of the cell disagrees with the expected type.
         */
        public void valueChanged(GraphSelectionEvent e) {
            this.jCell = null;
            this.jCells.clear();
            for (Object cell : AspectJGraph.this.getSelectionCells()) {
                GraphJCell jCell = (GraphJCell) cell;
                if (this.allCells
                    || this.vertexOnly == (jCell instanceof GraphJVertex)) {
                    this.jCell = jCell;
                    this.jCells.add(jCell);
                }
            }
            this.setEnabled(this.jCell != null);
        }

        /**
         * Sets the location attribute of this action.
         */
        public void setLocation(Point2D location) {
            this.location = location;
        }

        /**
         * Switch indication that the action is enabled for all types of
         * j-cells.
         */
        protected final boolean allCells;
        /** Switch indication that the action is enabled for all j-vertices. */
        protected final boolean vertexOnly;
        /** The first currently selected j-cell of the right type. */
        protected GraphJCell jCell;
        /** List list of currently selected j-cells of the right type. */
        protected final List<GraphJCell> jCells;
        /** The currently set point location. */
        protected Point2D location;
    }

    /**
     * Initialises and returns an action to add a point to the currently selected j-edge.
     */
    public JCellEditAction getAddPointAction(Point atPoint) {
        if (this.addPointAction == null) {
            this.addPointAction = new AddPointAction();
            addAccelerator(this.addPointAction);
        }
        this.addPointAction.setLocation(atPoint);
        return this.addPointAction;
    }

    /** The permanent AddPointAction associated with this j-graph. */
    private AddPointAction addPointAction;

    /**
     * Action to add a point to the currently selected j-edge.
     */
    private class AddPointAction extends JCellEditAction {
        /** Constructs an instance of the action. */
        AddPointAction() {
            super(Options.ADD_POINT_ACTION, false);
            putValue(ACCELERATOR_KEY, Options.ADD_POINT_KEY);
        }

        @Override
        public boolean isEnabled() {
            return this.jCells.size() == 1;
        }

        public void actionPerformed(ActionEvent evt) {
            addPoint((GraphJEdge) this.jCell, this.location);
        }
    }

    /**
     * @return an action to edit the currently selected j-cell label.
     */
    public JCellEditAction getEditLabelAction() {
        if (this.editLabelAction == null) {
            this.editLabelAction = new EditLabelAction();
            addAccelerator(this.editLabelAction);
        }
        return this.editLabelAction;
    }

    /**
     * The permanent EditLabelAction associated with this j-graph.
     */
    private EditLabelAction editLabelAction;

    /**
     * Action to edit the label of the currently selected j-cell.
     */
    private class EditLabelAction extends JCellEditAction {
        /** Constructs an instance of the action. */
        EditLabelAction() {
            super(Options.EDIT_LABEL_ACTION);
            putValue(ACCELERATOR_KEY, Options.RENAME_KEY);
        }

        public void actionPerformed(ActionEvent evt) {
            startEditingAtCell(this.jCell);
        }
    }

    /**
     * Initialises and returns an action to remove a point from the currently selected j-edge.
     */
    public JCellEditAction getRemovePointAction(Point atPoint) {
        if (this.removePointAction == null) {
            this.removePointAction = new RemovePointAction();
            addAccelerator(this.removePointAction);
        }
        this.removePointAction.setLocation(atPoint);
        return this.removePointAction;
    }

    /**
     * The permanent RemovePointAction associated with this j-graph.
     */
    private RemovePointAction removePointAction;

    /**
     * Action to remove a point from the currently selected j-edge.
     */
    private class RemovePointAction extends JCellEditAction {
        /** Constructs an instance of the action. */
        RemovePointAction() {
            super(Options.REMOVE_POINT_ACTION, false);
            putValue(ACCELERATOR_KEY, Options.REMOVE_POINT_KEY);
        }

        @Override
        public boolean isEnabled() {
            return this.jCells.size() == 1;
        }

        public void actionPerformed(ActionEvent evt) {
            removePoint((GraphJEdge) this.jCell, this.location);
        }
    }

    /**
     * @return an action to reset the label position of the currently selected
     *         j-edge.
     */
    public JCellEditAction getResetLabelPositionAction() {
        if (this.resetLabelPositionAction == null) {
            this.resetLabelPositionAction = new ResetLabelPositionAction();
        }
        return this.resetLabelPositionAction;
    }

    /**
     * The permanent ResetLabelPositionAction associated with this j-graph.
     */
    private ResetLabelPositionAction resetLabelPositionAction;

    /**
     * Action set the label of the currently selected j-cell to its default
     * position.
     */
    private class ResetLabelPositionAction extends JCellEditAction {
        /** Constructs an instance of the action. */
        ResetLabelPositionAction() {
            super(Options.RESET_LABEL_POSITION_ACTION, false);
        }

        public void actionPerformed(ActionEvent evt) {
            for (GraphJCell jCell : this.jCells) {
                resetLabelPosition((GraphJEdge) jCell);
            }
        }
    }

    /**
     * @param lineStyle the lineStyle for which to get the set-action
     * @return an action to set the line style of the currently selected j-edge.
     */
    public JCellEditAction getSetLineStyleAction(int lineStyle) {
        JCellEditAction result =
            this.setLineStyleActionMap.get(Options.getLineStyleName(lineStyle));
        if (result == null) {
            this.setLineStyleActionMap.put(Options.getLineStyleName(lineStyle),
                result = new SetLineStyleAction(lineStyle));
            addAccelerator(result);
        }
        return result;
    }

    /** Map from line style names to corresponding actions. */
    private final Map<String,JCellEditAction> setLineStyleActionMap =
        new HashMap<String,JCellEditAction>();

    /**
     * Action to set the line style of the currently selected j-edge.
     */
    private class SetLineStyleAction extends JCellEditAction {
        /** Constructs an instance of the action, for a given line style. */
        SetLineStyleAction(int lineStyle) {
            super(Options.getLineStyleName(lineStyle), false);
            putValue(ACCELERATOR_KEY, Options.getLineStyleKey(lineStyle));
            this.lineStyle = lineStyle;
        }

        public void actionPerformed(ActionEvent evt) {
            for (GraphJCell jCell : this.jCells) {
                GraphJEdge jEdge = (GraphJEdge) jCell;
                setLineStyle(jEdge, this.lineStyle);
                List<?> points =
                    GraphConstants.getPoints(jCell.getAttributes());
                if (points == null || points.size() == 2) {
                    addPoint(jEdge, this.location);
                }
            }
        }

        /** The line style set by this action instance. */
        protected final int lineStyle;
    }

    /**
     * Creates and returns a fresh line style menu for this j-graph.
     */
    public JMenu createLineStyleMenu() {
        JMenu result = new SetLineStyleMenu();
        return result;
    }

    /**
     * Menu offering a choice of line style setting actions.
     */
    private class SetLineStyleMenu extends JMenu implements
            GraphSelectionListener {
        /** Constructs an instance of the action. */
        SetLineStyleMenu() {
            super(Options.SET_LINE_STYLE_MENU);
            valueChanged(null);
            addGraphSelectionListener(this);
            // initialize the line style menu
            add(getSetLineStyleAction(GraphConstants.STYLE_ORTHOGONAL));
            add(getSetLineStyleAction(GraphConstants.STYLE_SPLINE));
            add(getSetLineStyleAction(GraphConstants.STYLE_BEZIER));
            add(getSetLineStyleAction(JAttr.STYLE_MANHATTAN));
        }

        public void valueChanged(GraphSelectionEvent e) {
            this.setEnabled(getSelectionCell() instanceof GraphJEdge);
        }
    }

}