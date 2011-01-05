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
 * $Id: LTSJGraph.java,v 1.10 2008-01-30 09:33:14 iovka Exp $
 */
package groove.gui.jgraph;

import groove.graph.Element;
import groove.gui.Exporter;
import groove.gui.ModelCheckingMenu;
import groove.gui.Options;
import groove.gui.SetLayoutMenu;
import groove.gui.Simulator;
import groove.gui.layout.Layouter;
import groove.gui.layout.SpringLayouter;
import groove.lts.GraphState;
import groove.lts.GraphTransition;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;

import org.jgraph.graph.DefaultGraphCell;

/**
 * Implementation of MyJGraph that provides the proper popup menu. To construct
 * an instance, setupPopupMenu() should be called after all global final
 * variables have been set.
 */
public class LTSJGraph extends JGraph {

    /** Constructs an instance of the j-graph for a given simulator. */
    public LTSJGraph(Simulator simulator) {
        super(null, true);
        this.simulator = simulator;
        addMouseListener(new MyMouseListener());
        getGraphLayoutCache().setSelectsAllInsertedCells(false);
        setModel(LTSJModel.EMPTY_LTS_JMODEL);
        setEnabled(false);
    }

    /** Specialises the return type to a {@link LTSJModel}. */
    @Override
    public LTSJModel getModel() {
        return (LTSJModel) this.graphModel;
    }

    /**
     * Scrolls the view to a given node or edge of the underlying graph model.
     */
    public void scrollTo(Element nodeOrEdge) {
        GraphJCell cell = getModel().getJCell(nodeOrEdge);
        assert cell != null;
        Rectangle2D bounds = getCellBounds(cell);
        if (bounds != null) {
            int extraSpace = 100;
            Rectangle scrollRect =
                new Rectangle((int) bounds.getX() - extraSpace,
                    (int) bounds.getY() - extraSpace, (int) bounds.getWidth()
                        + 2 * extraSpace, (int) bounds.getHeight() + 2
                        * extraSpace);
            scrollRectToVisible(scrollRect);
        }
    }

    /**
     * This implementation adds actions to move to different states within the
     * LTS, to apply the current transition and to explore the LTS, and
     * subsequently invokes the super implementation.
     */
    @Override
    public JMenu createPopupMenu(Point atPoint) {
        JMenu result = new JMenu("Popup");
        addSubmenu(result, createExploreMenu());
        addSubmenu(result, createGotoMenu());
        addSubmenu(result, super.createPopupMenu(atPoint));
        return result;
    }

    /** Creates a state exploration sub-menu. */
    public JMenu createExploreMenu() {
        JMenu result = new JMenu("Explore");
        result.add(this.simulator.getApplyTransitionAction());
        result.add(this.simulator.getDefaultExplorationAction());
        result.add(this.simulator.getExplorationDialogAction());
        result.add(createCheckerMenu());
        return result;
    }

    /** Creates a traversal sub-menu. */
    public JMenu createGotoMenu() {
        JMenu result = new JMenu("Go To");
        result.add(this.simulator.getGotoStartStateAction());
        result.add(getScrollToCurrentAction());
        return result;
    }

    /**
     * Overwrites the menu, so the forest layouter takes the LTS start state as
     * its root.
     */
    @Override
    protected SetLayoutMenu createSetLayoutMenu() {
        SetLayoutMenu result = new SetLayoutMenu(this, new MyForestLayouter());
        result.addLayoutItem(new SpringLayouter());
        return result;
    }

    /**
     * Lazily creates and returns the model-checking menu.
     */
    protected final JMenu createCheckerMenu() {
        return new ModelCheckingMenu(this.simulator);
    }

    @Override
    public Simulator getSimulator() {
        return this.simulator;
    }

    @Override
    protected Exporter getExporter() {
        return getSimulator().getExporter();
    }

    @Override
    protected String getExportActionName() {
        return Options.EXPORT_LTS_ACTION_NAME;
    }

    /**
     * The simulator to which this j-graph is associated.
     */
    private final Simulator simulator;

    /** Initialises and returns the action to scroll to the active state or transition. */
    private Action getScrollToCurrentAction() {
        if (getModel().getActiveTransition() == null) {
            this.scrollToCurrentAction.setState(this.simulator.getCurrentState());
        } else {
            this.scrollToCurrentAction.setTransition(this.simulator.getCurrentTransition());
        }
        return this.scrollToCurrentAction;
    }

    /**
     * Action to scroll the JGraph to the current state or derivation.
     */
    private final ScrollToCurrentAction scrollToCurrentAction =
        new ScrollToCurrentAction();

    /**
     * Action to scroll the LTS display to a (previously set) node or edge.
     * @see #scrollTo(Element)
     */
    public class ScrollToCurrentAction extends AbstractAction {
        public void actionPerformed(ActionEvent evt) {
            if (getSimulator().getCurrentState() == null) {
                scrollTo(getSimulator().getCurrentTransition());
            } else {
                scrollTo(getSimulator().getCurrentState());
            }
        }

        /**
         * Adapts the name of the action so that it reflects that the element to
         * scroll to is a given transition.
         */
        public void setTransition(GraphTransition edge) {
            putValue(Action.NAME, Options.SCROLL_TO_ACTION_NAME + " derivation");
        }

        /**
         * Adapts the name of the action so that it reflects that the element to
         * scroll to is a given state.
         */
        public void setState(GraphState node) {
            putValue(Action.NAME, Options.SCROLL_TO_ACTION_NAME + " state");
        }
    }

    /**
     * Mouse listener that activates a state or transition on a single click,
     * and switches to the state panel on a double click.
     */
    private class MyMouseListener extends MouseAdapter {
        /** Empty constructor with correct visibility. */
        MyMouseListener() {
            // empty
        }

        @Override
        public void mouseClicked(MouseEvent evt) {
            if (evt.getButton() == MouseEvent.BUTTON1) {
                // scale from screen to model
                java.awt.Point loc = evt.getPoint();
                // find cell in model coordinates
                DefaultGraphCell cell =
                    (DefaultGraphCell) getFirstCellForLocation(loc.x, loc.y);
                if (cell instanceof LTSJEdge) {
                    GraphTransition edge = ((LTSJEdge) cell).getEdge();
                    getSimulator().setTransition(edge);
                } else if (cell instanceof LTSJVertex) {
                    GraphState node = ((LTSJVertex) cell).getNode();
                    if (!getSimulator().getCurrentState().equals(node)) {
                        getSimulator().setState(node);
                    }
                    if (evt.getClickCount() == 2) {
                        getSimulator().exploreState(node);
                    }
                }
            }
        }
    }

    /**
     * A specialisation of the forest layouter that takes the LTS start graph as
     * its suggested root.
     */
    private class MyForestLayouter extends groove.gui.layout.ForestLayouter {
        /**
         * Creates a prototype layouter
         */
        public MyForestLayouter() {
            super();
        }

        /**
         * Creates a new instance, for a given {@link JGraph}.
         */
        public MyForestLayouter(String name, JGraph jgraph) {
            super(name, jgraph);
        }

        /**
         * This method returns a singleton set consisting of the LTS start
         * state.
         */
        @Override
        protected Collection<?> getSuggestedRoots() {
            LTSJModel jModel = getModel();
            return Collections.singleton(jModel.getJCellForNode(jModel.getGraph().startState()));
        }

        /**
         * This implementation returns a {@link MyForestLayouter}.
         */
        @Override
        public Layouter newInstance(JGraph jGraph) {
            return new MyForestLayouter(this.name, jGraph);
        }
    }
}