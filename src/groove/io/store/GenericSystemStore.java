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
package groove.io.store;

import groove.graph.TypeLabel;
import groove.trans.SystemProperties;
import groove.view.StoredGrammarView;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.swing.event.UndoableEditListener;

/**
 * Interface for any source of rule system data. The data consist of a list of
 * graphs, a list of rules, a list of control programs, and a rule system
 * properties object. Depending on the implementation, the store may be
 * immutable.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface GenericSystemStore<R,G,T,C> {
    /**
     * Returns the name of this store.
     * @return the name of this store; cannot be <code>null</code> or empty.
     */
    public String getName();

    /**
     * Returns the location of this store. The location uniquely identifies the
     * place from where the store was obtained.
     * @return the location of this store; cannot be <code>null</code> or empty.
     */
    public Object getLocation();

    /** Immutable view on the rulename-to-rule map in the store. */
    public Map<String,R> getRules();

    /**
     * Adds or replaces a rule in the store.
     * @param rule the rule to be added (non-null)
     * @return the old rule with the name of <code>rule</code>, if any;
     *         <code>null</code> otherwise
     * @throws IOException if an error occurred while storing the rule
     */
    public R putRule(R rule) throws IOException;

    /**
     * Deletes a rule from the store.
     * @param name name of the rule to be deleted (non-null)
     * @return the rule with name <code>name</code>, or <code>null</code> if
     *         there was no such rule
     * @throws IOException if the store is immutable
     */
    public R deleteRule(String name) throws IOException;

    /**
     * Renames a rule in the store.
     * @param oldName the name of the rule to be renamed (non-null)
     * @param newName the intended new name of the rule (non-null)
     * @return the renamed rule, or <code>null</code> if no rule named
     *         <code>oldName</code> existed
     * @throws IOException if an error occurred while storing the renamed rule
     */
    public R renameRule(String oldName, String newName) throws IOException;

    /** Immutable view on the name-to-graph map in the store. */
    public Map<String,G> getGraphs();

    /**
     * Adds or replaces a graph in the store.
     * @param graph the graph to be added
     * @return the old graph with the name of <code>graph</code>, if any;
     *         <code>null</code> otherwise
     * @throws IOException if an error occurred while storing the graph
     */
    public G putGraph(G graph) throws IOException;

    /**
     * Deletes a graph from the store.
     * @param name name of the graph to be deleted
     * @return the graph with name <code>name</code>, or <code>null</code> if
     *         there was no such graph
     * @throws IOException if the store is immutable
     */
    public G deleteGraph(String name) throws IOException;

    /**
     * Renames a graph in the store.
     * @param oldName the name of the graph to be renamed (non-null)
     * @param newName the intended new name of the graph (non-null)
     * @return the renamed graph, or <code>null</code> if no graph named
     *         <code>oldName</code> existed
     * @throws IOException if an error occurred while storing the renamed graph
     */
    public G renameGraph(String oldName, String newName) throws IOException;

    /** Immutable view on the name-to-control-program map in the store. */
    public Map<String,C> getControls();

    /**
     * Adds or replaces a control program in the store.
     * @param control the control program to be added
     * @return the old control program with name <code>name</code>, if any;
     *         <code>null</code> otherwise
     * @throws IOException if an error occurred while storing the control
     *         program
     */
    public String putControl(String name, C control) throws IOException;

    /**
     * Deletes a control program from the store. Also resets the control program
     * in the system properties and disables control if the deleted program was
     * the currently set control program.
     * @param name name of the control program to be deleted
     * @return the program with name <code>name</code>, or <code>null</code> if
     *         there was no such program
     * @throws IOException if the store is immutable
     */
    public String deleteControl(String name) throws IOException;

    /** Immutable view on the name-to-prolog-program map in the store. */
    public Map<String,C> getProlog();

    /**
     * Adds or replaces a prolog program in the store.
     * @param prolog the prolog program to be added
     * @return the old prolog program with name <code>name</code>, if any;
     *         <code>null</code> otherwise
     * @throws IOException if an error occurred while storing the prolog
     *         program
     */
    public String putProlog(String name, C prolog) throws IOException;

    /**
     * Deletes a prolog program from the store.
     * @param name name of the prolog program to be deleted
     * @return the program with name <code>name</code>, or <code>null</code> if
     *         there was no such program
     * @throws IOException if the store is immutable
     */
    public String deleteProlog(String name) throws IOException;

    /** Immutable view on the name-to-type map in the store. */
    public Map<String,T> getTypes();

    /**
     * Adds or replaces a type graph in the store.
     * @param type the type graph to be added
     * @return the old type graph with the name of <code>graph</code>, if any;
     *         <code>null</code> otherwise
     * @throws IOException if an error occurred while storing the type graph
     */
    public T putType(T type) throws IOException;

    /**
     * Renames a type graph in the store.
     * @param oldName the name of the type graph to be renamed (non-null)
     * @param newName the intended new name of the type graph (non-null)
     * @return the renamed type graph, or <code>null</code> if no graph named
     *         <code>oldName</code> existed
     * @throws IOException if an error occurred while storing the renamed graph
     */
    public T renameType(String oldName, String newName) throws IOException;

    /**
     * Deletes a type graph from the store. Also resets the type graph name in
     * the system properties, if it was set to the deleted type graph.
     * @param name name of the type graph to be deleted
     * @return the type graph with name <code>name</code>, or <code>null</code>
     *         if there was no such type
     * @throws IOException if the store is immutable
     */
    public T deleteType(String name) throws IOException;

    /** The system properties object in the store (non-null). */
    public SystemProperties getProperties();

    /**
     * Replaces the system properties in the store
     * @param properties the new system properties object
     * @throws IOException if an error occurred while storing the properties
     */
    public void putProperties(SystemProperties properties) throws IOException;

    /**
     * Changes a label into another in all relevant elements of the store.
     * @throws IOException if an error occurred while storing the properties
     */
    public void relabel(TypeLabel oldLabel, TypeLabel newLabel)
        throws IOException;

    /**
     * Reloads all data from the persistent storage into this store. Should be
     * called once immediately after construction of the store.
     */
    public void reload() throws IOException;

    /**
     * Saves the content of this grammar store to a given file, and returns the
     * saved store.
     * @throws IOException if the file does not have a known extension, or
     *         already exists, or if something goes wrong during saving. If an
     *         exception is thrown, any partial results are deleted.
     */
    public SystemStore save(File file, boolean clearDir) throws IOException;

    /** Returns a stored grammar view backed up by this store. */
    public StoredGrammarView toGrammarView();

    /**
     * Indicates if this store can be modified. If the store cannot be modified,
     * all the operations that attempt to modify it will throw
     * {@link IOException}s.
     * @return <code>true</code> if the store is modifiable
     */
    public boolean isModifiable();

    /** Adds a listener to this store. */
    public void addUndoableEditListener(UndoableEditListener listener);

    /** Removes a listener from this store. */
    public void removeUndoableEditListener(UndoableEditListener listener);
}
