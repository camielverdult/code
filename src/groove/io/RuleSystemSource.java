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
 * $Id$
 */
package groove.io;

import groove.trans.RuleNameLabel;
import groove.trans.SystemProperties;

import java.util.Map;

/**
 * Interface for any source of rule system data. The data consist of a list of
 * graphs, a list of rules, a list of control programs, and a rule system
 * properties object. Depending on the implementation, the source may be
 * mutable.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface RuleSystemSource<R,G> {
    /** Immutable view on the rulename-to-rule map in the source. */
    public Map<RuleNameLabel,R> getRules();

    /** Immutable view on the name-to-graph map in the source. */
    public Map<String,G> getGraphs();

    /** Immutable view on the name-to-control-program map in the source. */
    public Map<String,String> getControls();

    /** The system properties object in the source. */
    public SystemProperties getProperties();

    /**
     * Deletes a rule from the source.
     * @param name name of the rule to be deleted (non-null)
     * @return the rule with name <code>name</code>, or <code>null</code> if
     *         there was no such rule
     * @throws UnsupportedOperationException if the source is immutable
     */
    public R deleteRule(String name) throws UnsupportedOperationException;

    /**
     * Adds or replaces a rule in the source.
     * @param rule the rule to be added (non-null)
     * @return the old rule with the name of <code>rule</code>, if any;
     *         <code>null</code> otherwise
     * @throws UnsupportedOperationException if the source is immutable
     */
    public R putRule(R rule) throws UnsupportedOperationException;

    /**
     * Renames a rule in the source.
     * @param oldName the name of the rule to be renamed (non-null)
     * @param newName the intended new name of the rule (non-null)
     * @return the renamed rule, or <code>null</code> if no rule named
     *         <code>oldName</code> existed
     * @throws UnsupportedOperationException if the source is immutable
     */
    public R renameRule(String oldName, String newName);

    /**
     * Deletes a graph from the source.
     * @param name name of the graph to be deleted
     * @return the graph with name <code>name</code>, or <code>null</code> if
     *         there was no such graph
     * @throws UnsupportedOperationException if the source is immutable
     */
    public G deleteGraph(String name) throws UnsupportedOperationException;

    /**
     * Adds or replaces a graph in the source.
     * @param graph the graph to be added
     * @return the old graph with the name of <code>graph</code>, if any;
     *         <code>null</code> otherwise
     * @throws UnsupportedOperationException if the source is immutable
     */
    public G putGraph(G graph) throws UnsupportedOperationException;

    /**
     * Renames a graph in the source.
     * @param oldName the name of the graph to be renamed (non-null)
     * @param newName the intended new name of the graph (non-null)
     * @return the renamed graph, or <code>null</code> if no graph named
     *         <code>oldName</code> existed
     * @throws UnsupportedOperationException if the source is immutable
     */
    public G renameGraph(String oldName, String newName);

    /**
     * Adds or replaces a control program in the source.
     * @param control the control program to be added
     * @return the old control program with name <code>name</code>, if any;
     *         <code>null</code> otherwise
     * @throws UnsupportedOperationException if the source is immutable
     */
    public String putControl(String name, String control)
        throws UnsupportedOperationException;

    /**
     * Replaces the system properties in the source
     * @param properties the new system properties object
     * @throws UnsupportedOperationException if the source is immutable
     */
    public void putProperties(SystemProperties properties)
        throws UnsupportedOperationException;
}
