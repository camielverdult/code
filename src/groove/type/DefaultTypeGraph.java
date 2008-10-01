/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.type;

import groove.graph.AbstractNodeEdgeMap;
import groove.graph.DefaultGraph;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Frank van Es
 * @version $Revision $
 */
public class DefaultTypeGraph extends DefaultGraph {
	
	private Map<Object, NodeEdgeMap> typings;
	
	public DefaultTypeGraph() {
		super();
		typings = new HashMap<Object,NodeEdgeMap>();
	}
	
	public void addTyping(Graph graph) {
		
		NodeEdgeMap map = new NodeEdgeHashMap();
		
		map.putAll(addNodeTypes(graph.nodeSet()));
		map.putAll(addEdgeTypes(graph.edgeSet()));
		
		typings.put(graph, map);
	}
	
	protected NodeEdgeMap addNodeTypes(Set<? extends Node> nodes) {
		
		NodeEdgeMap map = new NodeEdgeHashMap();
		
		for (Node node : nodes) {
			Node newNode = super.addNode();
			map.putNode(node, newNode);
			//addNode(newNode);
		}
		return map;
	}
	
	protected NodeEdgeMap addEdgeTypes(Set<? extends Edge> edges) {
		
		NodeEdgeMap map = new NodeEdgeHashMap();
		
		for (Edge edge : edges) {
			Edge newEdge = super.addEdge(
					edge.end(Edge.SOURCE_INDEX),
					edge.label(),
					edge.end(Edge.TARGET_INDEX)
			);
			map.putEdge(edge, newEdge);
			//addEdge(newEdge);
		}
		return map;
	}
	
	public void mergeNodeTypes(AbstractNodeEdgeMap<Node,Node,Edge,Edge> map) {
		for (Map.Entry<Node,Node> nodes : map.nodeMap().entrySet()) {
			mergeNodes(nodes.getKey(), nodes.getValue());
		}
	}
	
	public NodeEdgeMap getTyping(Object o) {
		return typings.get(o);
	}
}
