/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.control.graph;

import groove.control.Attempt;
import groove.control.Attempt.Stage;
import groove.control.Position;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.NodeSetEdgeSetGraph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Graph representation of a control automaton, used for visualisation purposes.
 * Attempts are translated to individual edges for each of the calls, as well
 * as verdict edges.
 * Verdict edges to deadlocks are left out of the graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ControlGraph extends NodeSetEdgeSetGraph<ControlNode,ControlEdge> {
    /**
     * Constructs a new graph with a given name.
     */
    private ControlGraph(String name) {
        super(name);
    }

    /** Use only {@link #addNode(ControlNode)}. */
    @Override
    public ControlNode addNode() {
        throw new UnsupportedOperationException();
    }

    /** Use only {@link #addNode(ControlNode)}. */
    @Override
    public ControlNode addNode(int nr) {
        throw new UnsupportedOperationException();
    }

    /** Use only {@link #addEdge(ControlEdge)}. */
    @Override
    public ControlEdge addEdge(ControlNode source, String label, ControlNode target) {
        throw new UnsupportedOperationException();
    }

    /** Use only {@link #addEdge(ControlEdge)}. */
    @Override
    public ControlEdge addEdge(ControlNode source, Label label, ControlNode target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GraphRole getRole() {
        return GraphRole.CTRL;
    }

    /** Constructs a control graph from a given initial position. */
    public static <P extends Position<P,A>,A extends Stage<P,A>> ControlGraph newGraph(String name,
            P init) {
        ControlGraph result = new ControlGraph(name);
        Map<P,ControlNode> nodeMap = new HashMap<P,ControlNode>();
        Queue<P> fresh = new LinkedList<P>();
        addNode(result, nodeMap, init, fresh);
        while (!fresh.isEmpty()) {
            P next = fresh.poll();
            ControlNode node = nodeMap.get(next);
            if (next.isTrial()) {
                Attempt<P,A> attempt = next.getAttempt();
                if (!attempt.onSuccess().isDead()) {
                    ControlNode target = addNode(result, nodeMap, attempt.onSuccess(), fresh);
                    node.addVerdictEdge(target, true);
                    fresh.add(attempt.onSuccess());
                }
                if (!attempt.onFailure().isDead()) {
                    ControlNode target = addNode(result, nodeMap, attempt.onFailure(), fresh);
                    node.addVerdictEdge(target, false);
                }
                for (A out : attempt) {
                    addEdge(result, nodeMap, node, out, fresh);
                }
            }
        }
        return result;
    }

    /**
     * Adds a node to the control graph under construction.
     */
    private static <P extends Position<P,A>,A extends Stage<P,A>> ControlNode addNode(
            ControlGraph graph, Map<P,ControlNode> nodeMap, P pos, Queue<P> fresh) {
        ControlNode result = nodeMap.get(pos);
        if (result == null) {
            nodeMap.put(pos, result = new ControlNode(graph, pos));
            fresh.add(pos);
        }
        return result;
    }

    /**
     * Adds a call edge to the control graph under construction.
     */
    private static <P extends Position<P,A>,A extends Stage<P,A>> void addEdge(ControlGraph result,
            Map<P,ControlNode> nodeMap, ControlNode node, Stage<P,A> out, Queue<P> fresh) {
        ControlNode target;
        target = addNode(result, nodeMap, out.onFinish(), fresh);
        node.addCallEdge(target, out.getCallStack());
    }
}
