// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id: MinimalAnchorFactory.java,v 1.8 2008-02-29 11:02:20 fladder Exp $
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Node;
import groove.graph.algebra.ValueNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * In this implementation, the anchors are the minimal set of nodes and edges
 * needed to reconstruct the transformation, but not necessarily the entire
 * matching: only mergers, eraser nodes and edges (the later only if they are
 * not incident to an eraser node) and the incident nodes of creator edges are
 * stored.
 * @author Arend Rensink
 * @version $Revision$
 */
public class MinimalAnchorFactory implements AnchorFactory<SPORule> {
    /** Private empty constructor to make this a singleton class. */
    private MinimalAnchorFactory() {
        // empty constructor
    }

    /**
     * This implementation assumes that the rule is an <tt>SPORule</tt>, and
     * that the rule's internal sets of <tt>lhsOnlyNodes</tt> etc. have been
     * initialised already.
     */
    public Element[] newAnchors(SPORule rule) {
        Set<Element> anchors =
            new LinkedHashSet<Element>(Arrays.asList(rule.getEraserNodes()));
        if (rule.isTop()) {
            Set<Node> hiddenPars = rule.getHiddenPars();
            if (hiddenPars != null) {
                anchors.addAll(hiddenPars);
            }
            List<Node> inPars = rule.getInPars();
            if (inPars != null) {
                anchors.addAll(rule.getInPars());
            }
        }
        // set of endpoints that we will remove again
        Set<Node> removableEnds = new HashSet<Node>();
        for (Edge lhsVarEdge : rule.getSimpleVarEdges()) {
            anchors.add(lhsVarEdge);
            // if we have the edge in the anchors, its end nodes need not be
            // there
            removableEnds.add(lhsVarEdge.source());
            removableEnds.add(lhsVarEdge.target());
        }
        for (Edge eraserEdge : rule.getEraserEdges()) {
            Node source = eraserEdge.source();
            Node target = eraserEdge.target();
            if (!(anchors.contains(source) && anchors.contains(target))) {
                anchors.add(eraserEdge);
                // if we have the edge in the anchors, its end nodes need not be
                // there
                removableEnds.add(source);
                removableEnds.add(target);
            }
        }
        anchors.addAll(rule.getModifierEnds());
        // remove all constant data nodes
        Iterator<Element> anchorIter = anchors.iterator();
        while (anchorIter.hasNext()) {
            if (anchorIter.next() instanceof ValueNode) {
                anchorIter.remove();
            }
        }
        anchors.removeAll(removableEnds);
        return anchors.toArray(new Element[anchors.size()]);
    }

    /**
     * Returns the singleton instance of this class.
     */
    static public MinimalAnchorFactory getInstance() {
        return prototype;
    }

    /** The singleton instance of this class. */
    static private MinimalAnchorFactory prototype = new MinimalAnchorFactory();
}
