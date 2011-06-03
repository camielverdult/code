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
package groove.trans;

import groove.graph.GraphRole;
import groove.io.ExtensionFilter;
import groove.io.FileType;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * Abstract type of the resources that make up a grammar.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum ResourceKind {
    /** Host graph resources; in other words, potential start graphs of the grammar. */
    HOST("Graph", GraphRole.HOST, FileType.STATE_FILTER),
    /** Transformation rule resources. */
    RULE("Rule", GraphRole.RULE, FileType.RULE_FILTER),
    /** Type graph resources. */
    TYPE("Type", GraphRole.TYPE, FileType.TYPE_FILTER),
    /**
     * Control program resources.
     */
    CONTROL("Control", FileType.CONTROL_FILTER),
    /** Prolog program resources. */
    PROLOG("Prolog", FileType.PROLOG_FILTER),
    /** Grammar properties resource. */
    PROPERTIES("Properties", FileType.PROPERTIES_FILTER);

    /** Constructs a value with no corresponding graph role. */
    private ResourceKind(String name, ExtensionFilter filter) {
        this(name, GraphRole.NONE, filter);
    }

    /** Constructs a value with a given graph role. */
    private ResourceKind(String name, GraphRole graphRole,
            ExtensionFilter filter) {
        this.graphRole = graphRole;
        this.name = name;
        this.filter = filter;
    }

    /** Returns the graph role associated with this resource kind, 
     * or {@link GraphRole#NONE} if there is no corresponding graph role.
     */
    public GraphRole getGraphRole() {
        return this.graphRole;
    }

    /** 
     * Indicates if this resource is graph-based.
     * This holds if and only if the resource kind has a proper graph role.
     * @see #getGraphRole() 
     */
    public boolean isGraphBased() {
        return getGraphRole() != GraphRole.NONE;
    }

    /** 
     * Indicates if this resource is text-based.
     * This holds if and only if it is not equal to {@link #PROPERTIES},
     * and is not graph-based.
     * @see #isGraphBased()
     */
    public boolean isTextBased() {
        return this != PROPERTIES && !isGraphBased();
    }

    /** Returns the name of this kind of resource. */
    public String getName() {
        return this.name;
    }

    /** Returns the file filter for this kind of resource. */
    public ExtensionFilter getFilter() {
        return this.filter;
    }

    /** The graph role associated with this resource kind, or {@link GraphRole#NONE}
     * if there is no corresponding graph role.
     */
    private final GraphRole graphRole;
    /** Name of this resource kind. */
    private final String name;
    /** File filter for this resource kind. */
    private final ExtensionFilter filter;

    /** 
     * Returns the resource kind of a given graph role or {@code null}
     *  if the graph role does not correspond to a resource kind.
     */
    public static ResourceKind toResource(GraphRole graphRole) {
        return roleKindMap.get(graphRole);
    }

    private static Map<GraphRole,ResourceKind> roleKindMap =
        new EnumMap<GraphRole,ResourceKind>(GraphRole.class);
    static {
        for (ResourceKind kind : EnumSet.allOf(ResourceKind.class)) {
            if (kind.isGraphBased()) {
                roleKindMap.put(kind.getGraphRole(), kind);
            }
        }
    }
}
