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
 * $Id: JEdgeView.java,v 1.10 2008-01-30 09:33:13 iovka Exp $
 */
package groove.gui.jgraph;

import static groove.gui.look.Values.ERROR_COLOR;
import groove.gui.Options;
import groove.gui.look.HTMLLineFormat;
import groove.gui.look.LineStyle;
import groove.gui.look.MultiLabel;
import groove.gui.look.Values;
import groove.gui.look.VisualKey;
import groove.gui.look.VisualMap;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.jgraph.graph.CellHandle;
import org.jgraph.graph.CellMapper;
import org.jgraph.graph.CellView;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.EdgeRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphCellEditor;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphContext;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.PortView;

/**
 * An edge view that uses the <tt>getText()</tt> of the underlying edge as a
 * label. Moreover, new views take care to bend to avoid overlap, and offer
 * functionality to add and remove points.
 * @author Arend Rensink
 * @version $Revision$
 */
public class JEdgeView extends EdgeView {

    /** The editor for all instances of <tt>JEdgeView</tt>. */
    static protected final MultiLinedEditor editor = new MultiLinedEditor();

    private final JGraph<?> jGraph;

    /**
     * Apart from constructing a new edge view, adds points to the edge if it is
     * a self-edge or if a straight parallel edge already exists. Queries the
     * underlying model for edge attributes. (using
     * <tt>@link JModel#createEdgeAttributes</tt>).
     */
    public JEdgeView(JEdge<?> jEdge, JGraph<?> jGraph) {
        super(jEdge);
        this.jGraph = jGraph;
    }

    /* Overridden to avoid inserting PortViews into the points list. */
    @Override
    public void setSource(CellView sourceView) {
        this.sourceParentView = null;
        this.source = sourceView;
        if (this.source != null) {
            setPoint(0, getCenterPoint(sourceView.getParentView()));
        }
        invalidate();
    }

    /* Overridden to avoid inserting PortViews into the points list. */
    @Override
    public void setTarget(CellView targetView) {
        this.target = targetView;
        this.targetParentView = null;
        if (this.target != null) {
            int ix = this.points.size() - 1;
            setPoint(ix, getCenterPoint(targetView.getParentView()));
        }
        invalidate();
    }

    /** Convenience method to retrieve the source vertex. */
    private JVertex<?> getSourceVertex() {
        return getCell().getSourceVertex();
    }

    /** Convenience method to retrieve the target vertex. */
    private JVertex<?> getTargetVertex() {
        return getCell().getTargetVertex();
    }

    @Override
    public String toString() {
        return this.cell.toString();
    }

    @Override
    public MyEdgeRenderer getRenderer() {
        return (MyEdgeRenderer) super.getRenderer();
    }

    /**
     * This implementation returns the (static) {@link MultiLinedEditor}.
     */
    @Override
    public GraphCellEditor getEditor() {
        return editor;
    }

    /**
     * Specialises the return type.
     */
    @Override
    public JEdge<?> getCell() {
        return (JEdge<?>) super.getCell();
    }

    /**
     * Does some routing of self-edges and overlapping edges.
     */
    @Override
    public void refresh(GraphLayoutCache cache, CellMapper mapper,
            boolean createDependentViews) {
        super.refresh(cache, mapper, createDependentViews);
        // target could be null, if we are dealing with a temporary cache that just
        // contains a mapping for the edge
        if (this.target != null && !this.jGraph.isLayouting()) {
            if (this.isSelfEdge()) {
                routeSelfEdge();
            } else if (getPointCount() <= 2) {
                //routeParallelEdge(mapper);
            }
        }
    }

    /** Returns true if this is a self edge. */
    protected boolean isSelfEdge() {
        return this.source == this.target;
    }

    /**
     * Overrides the method to return a {@link MyEdgeHandle}.
     */
    @Override
    public CellHandle getHandle(GraphContext context) {
        return new MyEdgeHandle(this, context);
    }

    /*
     * Overridden to avoid relying on PortViews in the point list
     */
    @Override
    public Point2D getPoint(int index) {
        Point2D result = null;
        if (index == 0 && this.source != null) {
            result = getEndPoint(this.source.getParentView(), true);
        } else if (index == getPointCount() - 1 && this.target != null) {
            result = getEndPoint(this.target.getParentView(), false);
        }
        Object obj = this.points.get(index);
        if (result == null && obj instanceof Point2D) {
            result = (Point2D) obj;
        }
        return result;
    }

    /*
     * If we're doing this for the target point and the nearest point is the
     * source, take the corrected source point.
     */
    @Override
    protected Point2D getNearestPoint(boolean source) {
        Point2D result = null;
        if (getPointCount() == 2) {
            if (!source && this.source instanceof PortView) {
                JVertexView sourceCellView =
                    (JVertexView) ((PortView) this.source).getParentView();
                result =
                    sourceCellView.getPerimeterPoint(this, null,
                        getPointLocation(getPointCount() - 1));
            }
        }
        if (result == null) {
            result = super.getNearestPoint(source);
        }
        return result;
    }

    /*
     * Overridden to avoid relying on PortViews in the point list
     */
    @Override
    protected Point2D getPointLocation(int index) {
        Point2D result = null;
        if (index == 0 && this.source != null) {
            CellView vertex = this.source.getParentView();
            if (vertex != null) {
                result = getCenterPoint(vertex);
            }
        } else if (index == getPointCount() - 1 && this.target != null) {
            CellView vertex = this.target.getParentView();
            if (vertex != null) {
                result = getCenterPoint(vertex);
            }
        }
        if (result == null) {
            // this is neither source nor target vertex, so take the point itself
            result = (Point2D) this.points.get(index);
        }
        return result;
    }

    /**
     * Returns the parallel edges rank of this edge.
     * This is the rank within the set of parallel unrouted
     * edges. The rank is
     * determined by the position in the edge set of this edges's source port.
     * If this edge is routed (that is, it has explicit routing points)
     * then its parallel rank is always 0.
     * @return the computed parallel edges rank
     */
    private int getParRank() {
        if (this.source == null || this.target == null) {
            return 0;
        }
        if (getPointCount() > 2) {
            return 0;
        }
        if (getCell().isLoop()) {
            return 0;
        }
        // the total number of incoming and outgoing parallel edges
        int inCount = 0;
        int outCount = 0;
        // the rank calculated for this edge
        int rank = 0;
        // flag indicating that this edge has been encountered
        boolean found = false;
        // determine the rank within the incoming/outgoing edges
        for (JEdge<?> edge : getSourceVertex().getContext()) {
            // determine if this is a parallel edge
            if (edge.getVisuals().getPoints().size() > 2) {
                continue;
            }
            found |= edge == getCell();
            if (edge.getTargetVertex() == getTargetVertex()) {
                // edge is outgoing
                outCount++;
                if (!found) {
                    rank++;
                }
            } else if (edge.getSourceVertex() == getTargetVertex()) {
                // edge is incoming
                inCount++;
            }
        }
        // adjust so the ranks are points on an interval centered on 0 with distance 2
        return 2 * (inCount + rank) - (inCount + outCount - 1);
    }

    /** Returns the perimeter point where the end of this edge has to connect.
     * @param vertex the end vertex
     * @param source if {@code true}, we're computing this for the edge target
     */
    private Point2D getEndPoint(CellView vertex, boolean source) {
        JVertexView vertexView = (JVertexView) vertex;
        Point2D center = getCenterPoint(vertex);
        Point2D nextPoint = getNearestPoint(source);
        Point2D adjustedCenter;
        Point2D adjustedNextPoint;
        int parRank = source ? getParRank() : -getParRank();
        if (parRank == 0) {
            adjustedCenter = center;
            adjustedNextPoint = nextPoint;
        } else {
            // direction of the next point
            double dx = nextPoint.getX() - center.getX();
            double dy = nextPoint.getY() - center.getY();
            // direction for the offset, perpendicular to the next point
            double offDirX = dy;
            double offDirY = -dx;
            double offDist = center.distance(nextPoint);
            // calculate vertex radius in the specified direction
            double offMax = vertexView.getRadius(offDirX, offDirY);
            // calculate actual offset
            double offset =
                Math.signum(parRank)
                    * Math.min(PAR_EDGES_DISTANCE * Math.abs(parRank), offMax);
            double offX = offset * offDirX / offDist;
            double offY = offset * offDirY / offDist;
            adjustedCenter =
                new Point2D.Double(center.getX() + offX, center.getY() + offY);
            adjustedNextPoint =
                new Point2D.Double(nextPoint.getX() + offX, nextPoint.getY()
                    + offY);
        }
        return vertexView.getPerimeterPoint(this, adjustedCenter,
            adjustedNextPoint);
    }

    /**
     * The vector is from the first to the second point.
     */
    @Override
    public Point2D getLabelVector() {
        Point2D p0 = getPoint(0);
        Point2D p1 = getPoint(1);
        if (getCell().getVisuals().getLineStyle() == LineStyle.MANHATTAN
            && p1.getX() != p0.getX()) {
            p1 = new Point2D.Double(p1.getX(), p0.getY());
        }
        double dx = p1.getX() - p0.getX();
        double dy = p1.getY() - p0.getY();
        return new Point2D.Double(dx, dy);
    }

    /**
     * Adds points to the view and sets the line style so that the edge makes a
     * nice curve. The points are created perpendicular to the line between the
     * first and second point when the method is invoked, also taking the vertex
     * bound into account. All but the first and last points of the original
     * points are removed. Should only be called if
     * <code>getSource() == getTarget()</code>.
     */
    protected void routeSelfEdge() {
        VisualMap visuals = getCell().getVisuals();
        LineStyle lineStyle = visuals.getLineStyle();
        boolean isManhattan = lineStyle == LineStyle.MANHATTAN;
        if (isManhattan ? getPointCount() == 2 : getPointCount() <= 3) {
            Point2D startPoint = getPoint(0);
            Point2D endPoint = getPoint(1);
            List<Point2D> newPoints = new ArrayList<Point2D>(4);
            newPoints.add(startPoint);
            VisualMap sourceVisuals = getCell().getSourceVertex().getVisuals();
            Point2D pos = sourceVisuals.getNodePos();
            Dimension2D size = sourceVisuals.getNodeSize();
            pos.setLocation(pos.getX() - size.getWidth() / 2,
                pos.getY() - size.getHeight() / 2);
            Rectangle2D bounds = new Rectangle();
            bounds.setFrame(pos, size);
            if (bounds.contains(endPoint)) {
                endPoint.setLocation(endPoint.getX() + size.getWidth() * 2,
                    endPoint.getY());
            }
            newPoints.add(1,
                createPointPerpendicular(startPoint, endPoint, true));
            if (!isManhattan) {
                newPoints.add(1,
                    createPointPerpendicular(startPoint, endPoint, false));
                visuals.setLineStyle(LineStyle.BEZIER);
            }
            newPoints.add(startPoint);
            visuals.put(VisualKey.POINTS, newPoints);
        }
    }

    /**
     * Tests if the edge has parallel edges and, if so, adds a point to this
     * edge so it can be routed around.
     */
    protected void routeParallelEdge(CellMapper mapper) {
        // look for parallel edges; if one exists, make this one bend
        boolean parallelEdge = false;
        JVertex<?> sourceVertex = getCell().getSourceVertex();
        JVertex<?> targetVertex = getCell().getTargetVertex();
        for (JEdge<?> otherEdge : sourceVertex.getContext()) {
            EdgeView otherView = (EdgeView) mapper.getMapping(otherEdge, false);
            if (otherEdge != getCell()
                && otherView != null
                && otherEdge.getVisuals().getPoints().size() <= 2
                && (otherEdge.getSourceVertex() == targetVertex || otherEdge.getTargetVertex() == targetVertex)) {
                parallelEdge = true;
                break;
            }
        }
        if (parallelEdge) {
            Point2D startPoint = getPoint(0);
            Point2D endPoint = getPoint(1);
            Point2D midPoint = createPointBetween(startPoint, endPoint);
            VisualMap visuals = getCell().getVisuals();
            visuals.setPoints(Arrays.asList(startPoint, midPoint, endPoint));
            visuals.setLineStyle(LineStyle.BEZIER);
        }
    }

    /**
     * Creates an returns a point halfway two given points, with a random effect
     * @param p1 the first boundary point
     * @param p2 the first boundary point
     * @return new point on the perpendicular of the line between <tt>p1</tt>
     *         and <tt>p2</tt>
     */
    private Point createPointBetween(Point2D p1, Point2D p2) {
        double distance = p1.distance(p2);
        int midX = (int) (p1.getX() + p2.getX()) / 2;
        int midY = (int) (p1.getY() + p2.getY()) / 2;
        // int offset = (int) (5 + distance / 2 + 20 * Math.random());
        int x, y;
        if (distance == 0) {
            x = midX + 20;
            y = midY + 20;
        } else {
            int offset = (int) (5 + distance / 4);
            double xDelta = p1.getX() - p2.getX();
            double yDelta = p1.getY() - p2.getY();
            x = midX + (int) (offset * yDelta / distance);
            y = midY - (int) (offset * xDelta / distance);
        }
        return new Point(Math.max(x, 0), Math.max(y, 0));
    }

    /**
     * Creates and returns a point perpendicular to the line between two points,
     * at a distance to the second point that is a fraction of the length of the
     * original line. A boolean flag controls the direction to which the
     * perpendicular point sticks out from the original line.
     * @param p1 the first boundary point
     * @param p2 the first boundary point
     * @param left flag to indicate whether the new point is to stick out on the
     *        left or right hand side of the line between <tt>p1</tt> and
     *        <tt>p2</tt>.
     * @return new point on the perpendicular of the line between <tt>p1</tt>
     *         and <tt>p2</tt>
     */
    protected Point createPointPerpendicular(Point2D p1, Point2D p2,
            boolean left) {
        double distance = p1.distance(p2);
        int midX = (int) (p1.getX() + p2.getX()) / 2;
        int midY = (int) (p1.getY() + p2.getY()) / 2;
        // int offset = (int) (5 + distance / 2 + 20 * Math.random());
        int x, y;
        if (distance == 0) {
            x = midX + 20;
            y = midY + 20;
        } else {
            int offset = (int) (5 + distance / 4);
            if (left) {
                offset = -offset;
            }
            double xDelta = p1.getX() - p2.getX();
            double yDelta = p1.getY() - p2.getY();
            x = (int) (p2.getX() + offset * yDelta / distance);
            y = (int) (p2.getY() - offset * xDelta / distance);
        }
        return new Point(Math.max(x, 0), Math.max(y, 0));
    }

    /**
     * Callback method to determine the line style for edges that have points
     * added automatically.
     * @return This method always returns {@link GraphConstants#STYLE_BEZIER}.
     */
    protected LineStyle getPreferredLinestyle() {
        return LineStyle.BEZIER;
    }

    /** Distance between parallel edges. */
    private static final int PAR_EDGES_DISTANCE = 4;

    static {
        renderer = new MyEdgeRenderer();
    }

    /**
     * This class is overridden to get the same port emphasis.
     */
    static public class MyEdgeHandle extends EdgeHandle {
        /** Constructs an instance. */
        public MyEdgeHandle(EdgeView edge, GraphContext ctx) {
            super(edge, ctx);
        }

        @Override
        public void mousePressed(MouseEvent evt) {
            if (!Options.isEdgeEditEvent(evt)) {
                super.mousePressed(evt);
            }
        }

        @Override
        public void mouseReleased(MouseEvent evt) {
            if (!Options.isEdgeEditEvent(evt)) {
                super.mouseReleased(evt);
            }
        }

        @Override
        public boolean isAddPointEvent(MouseEvent event) {
            return Options.isEdgeEditEvent(event)
                && super.isAddPointEvent(event);
        }

        @Override
        public boolean isRemovePointEvent(MouseEvent event) {
            return Options.isEdgeEditEvent(event)
                && super.isRemovePointEvent(event);
        }

        /**
         * Sets the target port of the view to the source port if the target
         * port is currently <tt>null</tt>, and then invokes the super
         * method.
         */
        @Override
        protected ConnectionSet createConnectionSet(EdgeView view,
                boolean verbose) {
            if (view.getTarget() == null) {
                @SuppressWarnings("unchecked")
                List<Object> points = view.getPoints();
                points.add(points.get(points.size() - 1));
                view.setTarget(view.getSource());
            }
            return super.createConnectionSet(view, verbose);
        }

        /**
         * Delegates to {@link JVertexView#paintArmed(Graphics)} if the port's
         * parent view is a {@link JVertexView}; otherwise invokes the super
         * method.
         */
        @Override
        protected void paintPort(Graphics g, CellView p) {
            if (p.getParentView() instanceof JVertexView
                && this.graph instanceof AspectJGraph) {
                ((JVertexView) p.getParentView()).paintArmed(g);
            } else {
                super.paintPort(g, p);
            }
        }
    }

    /** Renderer subclass to enable our special line style. */
    static public class MyEdgeRenderer extends EdgeRenderer {
        MyEdgeRenderer() {
            this.jLabel = new JLabel();
            this.jLabel.setBorder(null);
            this.jLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getRendererComponent(org.jgraph.JGraph jGraph,
                CellView v, boolean sel, boolean focus, boolean preview) {

            assert v instanceof JEdgeView : String.format(
                "This renderer is only meant for %s", JEdgeView.class);

            JEdgeView view = this.jView = (JEdgeView) v;
            this.cell = this.jView.getCell();
            VisualMap visuals = this.visuals = this.cell.getVisuals();
            this.line2color = visuals.getInnerLine();
            this.twoLines = this.line2color != null;
            this.error = visuals.isError();
            if (this.error) {
                Rectangle b = getLabelBounds(jGraph, view).getBounds();
                b.setRect(b.x - 1, b.y - 1, b.width, b.height + 1);
                this.errorBounds = b;
            }
            super.getRendererComponent(jGraph, v, sel, focus, preview);
            // treat selection as emphasis
            float lineWidth = this.visuals.getLineWidth();
            if (visuals.isEmphasised()) {
                lineWidth += JAttr.EMPH_INCREMENT;
            }
            this.lineWidth = lineWidth;
            // if the specified background is null, use the graph background
            this.defaultBackground = jGraph.getBackground();
            return this;
        }

        /* Always set the default background, regardless of what anyone tells you. */
        @Override
        public void setBackground(Color background) {
            super.setBackground(this.defaultBackground);
        }

        @Override
        public void paint(Graphics g) {
            try {
                super.paint(g);
            } catch (InternalError e) {
                // this catches a nasty bug introduced in JRE 6.2x
            }

            Graphics2D g2 = (Graphics2D) g;
            if (this.twoLines) {
                // draw the second line
                g2.setColor(this.line2color);
                g2.setStroke(JAttr.createStroke(1, Values.NO_DASH));
                g2.draw(this.view.lineShape);
                if (this.view.endShape != null) {
                    g2.fill(this.view.endShape);
                    g2.draw(this.view.endShape);
                }
                // write text again
                g2.setStroke(new BasicStroke(1));
                paintLabels(g);
            }
            if (this.error) {
                // overlay with error colour
                int s = JAttr.EXTRA_BORDER_SPACE;
                g.setColor(ERROR_COLOR);
                g2.setStroke(JAttr.createStroke(this.lineWidth + s, null));
                g2.draw(this.view.lineShape);
                if (this.view.endShape != null) {
                    g2.fill(this.view.endShape);
                    g2.draw(this.view.endShape);
                }
                paintLabels(g);
                g.setColor(ERROR_COLOR);
                g2.fill(this.errorBounds);
            }
        }

        @Override
        protected void paintSelection(Graphics g) {
            if (this.selected) {
                float oldLineWidth = this.lineWidth;
                this.lineWidth = 1;
                super.paintSelection(g);
                this.lineWidth = oldLineWidth;
            }
        }

        /**
         * Overrides the method to take {@link LineStyle#MANHATTAN} into
         * account.
         */
        @Override
        protected Shape createShape() {
            if (this.lineStyle == Values.STYLE_MANHATTAN
                && this.view.getPointCount() > 2) {
                return createManhattanShape();
            } else {
                return super.createShape();
            }
        }

        /** Creates a shape for the {@link LineStyle#MANHATTAN} line style. */
        protected Shape createManhattanShape() {
            int n = this.view.getPointCount();
            if (n > 1) {
                // Following block may modify static vars as side effect
                // (Flyweight Design)
                JEdgeView tmp = (JEdgeView) this.view;
                Point2D[] p = null;
                p = new Point2D[n];
                for (int i = 0; i < n; i++) {
                    Point2D pt = tmp.getPoint(i);
                    if (pt == null) {
                        return null; // exit
                    }
                    p[i] = new Point2D.Double(pt.getX(), pt.getY());
                }

                // End of Side-Effect Block
                // Undo Possible MT-Side Effects
                if (this.view != tmp) {
                    this.view = tmp;
                    installAttributes(this.view);
                }
                // End of Undo
                if (this.view.sharedPath == null) {
                    this.view.sharedPath =
                        new GeneralPath(Path2D.WIND_NON_ZERO, n);
                } else {
                    this.view.sharedPath.reset();
                }
                this.view.beginShape =
                    this.view.lineShape = this.view.endShape = null;
                // first point
                Point2D p0 = p[0];
                // last point
                Point2D pe = p0;
                // second point
                Point2D p1 = null;
                // last point but one
                Point2D p2 = null;
                this.view.sharedPath.moveTo((float) p0.getX(),
                    (float) p0.getY());
                for (int i = 1; i < n; i++) {
                    // first move horizontally,
                    float x = (float) p[i].getX();
                    float y = (float) p[i - 1].getY();
                    this.view.sharedPath.lineTo(x, y);
                    p2 = pe;
                    pe = new Point2D.Float(x, y);
                    if (p1 == null) {
                        p1 = pe;
                    }
                    // then move vertically, if needed
                    if (p[i].getY() != y) {
                        y = (float) p[i].getY();
                        this.view.sharedPath.lineTo(x, y);
                        p2 = pe;
                        pe = new Point2D.Float(x, y);
                    }
                }
                if (this.beginDeco != GraphConstants.ARROW_NONE) {
                    this.view.beginShape =
                        createLineEnd(this.beginSize, this.beginDeco, p1, p0);
                }
                if (this.endDeco != GraphConstants.ARROW_NONE) {
                    this.view.endShape =
                        createLineEnd(this.endSize, this.endDeco, p2, pe);
                }
                if (this.view.endShape == null && this.view.beginShape == null) {
                    // With no end decorations the line shape is the same as the
                    // shared path and memory
                    this.view.lineShape = this.view.sharedPath;
                } else {
                    this.view.lineShape =
                        (GeneralPath) this.view.sharedPath.clone();
                    if (this.view.endShape != null) {
                        this.view.sharedPath.append(this.view.endShape, true);
                    }
                    if (this.view.beginShape != null) {
                        this.view.sharedPath.append(this.view.beginShape, true);
                    }
                }
                return this.view.sharedPath;
            }
            return null;
        }

        /*
         * Overwritten to capture drawing the main label in a JLabel, allowing the use
         * of HTML! 
         */
        @Override
        protected void paintLabel(Graphics g, String label, Point2D p,
                boolean mainLabel) {
            if (!mainLabel) {
                super.paintLabel(g, label, p, mainLabel);
            } else if (this.labelsEnabled && p != null) {
                paintMainLabel(g, p);
            }
        }

        /** Paints the main label in a JLabel, providing HTML formatting. */
        private void paintMainLabel(Graphics g, Point2D p) {
            Dimension size = setTextInJLabel(this.jView);
            if (size != null && (size.getWidth() != 0 || size.getHeight() != 0)) {
                this.jLabel.setSize(size);
                int sw = (int) size.getWidth();
                int sh = (int) size.getHeight();
                Graphics2D g2 = (Graphics2D) g;
                int dx = -sw / 2;
                int offset =
                    this.isMoveBelowZero ? 0 : Math.min(0,
                        (int) (dx + p.getX()));
                g2.translate(p.getX() - offset, p.getY());
                if (isOpaque()) {
                    g.setColor(getBackground());
                    g.fillRect(-sw / 2 - 1, -sh / 2 - 1, sw + 2, sh + 2);
                }
                int dy = -sh / 2;
                g.setColor(this.fontColor);
                g.translate(dx, dy);
                //the fontMetrics stringWidth and height can be replaced by
                //getLabel().getPreferredSize() if needed
                this.jLabel.paint(g);
                g.translate(-dx, -dy);
                g2.translate(-p.getX() + offset, -p.getY());
            }
        }

        /**
         * Sets a given string, wrapped in colour, font and HTML tags,
         * into the JLabel component in charge of rendering, and
         * returns the resulting size.
         */
        private Dimension setTextInJLabel(JEdgeView view) {
            Dimension result = this.jLabelSize;
            Color foreground = getForeground();
            // see if we can use the previously stored value
            MultiLabel lines = view.getCell().getVisuals().getLabel();
            if (lines.isEmpty()) {
                result = this.jLabelSize = new Dimension();
            } else if (lines != this.jLabelLines
                || foreground != this.jLabelColor) {
                // no, the text or colour have changed; reload the jLabel component
                StringBuilder text;
                if (view.getCell().getJGraph().isShowArrowsOnLabels()) {
                    Point2D start = view.getPoint(0);
                    Point2D end = view.getPoint(view.getPointCount() - 1);
                    text =
                        lines.toString(HTMLLineFormat.instance(), start, end);
                } else {
                    text = lines.toString(HTMLLineFormat.instance());
                }
                this.jLabel.setText(HTMLLineFormat.toHtml(text, foreground));
                this.jLabelColor = foreground;
                result = this.jLabelSize = this.jLabel.getPreferredSize();
            }
            return result;
        }

        /* Overwritten so the bounds get computed correctly even
         * before {@link #getRendererComponent}
         * has been called for the first time.
         */
        @Override
        public Rectangle2D getLabelBounds(org.jgraph.JGraph paintingContext,
                EdgeView view) {
            Rectangle2D result = null;
            Point2D p = getLabelPosition(view);
            Dimension d = getLabelSize(view, null);
            result = getLabelBounds(p, d, null);
            return result;
        }

        /* This implementation does not use the label parameter,
         * but constructs the label from the visual map of the view's cell
         */
        @Override
        public Dimension getLabelSize(EdgeView view, String label) {
            Dimension result = null;
            if (label != null) {
                result = super.getLabelSize(view, label);
            } else {
                result = setTextInJLabel((JEdgeView) view);
            }
            return result;
        }

        private JEdgeView jView;
        private JEdge<?> cell;
        private VisualMap visuals;
        // properties for drawing a second line
        private boolean twoLines = false;
        private Color line2color;
        /** Flag indicating that the underlying edge has an error. */
        private boolean error;
        private Rectangle2D errorBounds;

        /** Component used for rendering HTML text. */
        private final JLabel jLabel;
        /** Last inner text set in the jLabel component. */
        private MultiLabel jLabelLines;
        /** Last colour set in the jLabel component. */
        private Color jLabelColor;
        /** Last computed preferred size of the jLabel component. */
        private Dimension jLabelSize;
    }
}
