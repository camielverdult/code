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

import static groove.util.Groove.DEFAULT_CONTROL_NAME;
import groove.control.ControlView;
import groove.graph.GraphShape;
import groove.trans.RuleNameLabel;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.view.AspectualGraphView;
import groove.view.AspectualRuleView;
import groove.view.DefaultGrammarView;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Staijen
 * @version $Revision $
 */
public class FileGps extends AspectualViewGps {

    /**
     * Creates a grammar (un)marshaller for .gps directories.
     * @param layouted whether or not layout iformation must be loaded.
     */
    public FileGps(boolean layouted) {
        super(layouted);
    }

    /**
     * Marshal a grammar to a certain directory, represented by the given File.
     */
    @Override
    public void marshal(DefaultGrammarView gg, File location)
        throws IOException {
        createLocation(location);
        String grammarName = GRAMMAR_FILTER.stripExtension(location.getName());
        gg.setName(grammarName);
        // iterate over rules and save them
        for (RuleNameLabel ruleName : gg.getRuleMap().keySet()) {
            // turn the rule into a rule graph
            marshalRule(gg.getRule(ruleName), location);
        }

        /** Saves all graphs, so no needs to also save the current startgraph */
        // saveStartGraph(gg.getStartGraph(), location);
        saveGraphs(gg.getGraphs(), location);

        saveProperties(gg, location);
        saveControl(gg.getControl(), location);
    }

    private void saveGraphs(Map<String,File> graphs, File location) throws IOException {
        for( String name : graphs.keySet() ) {
            File file = graphs.get(name);
            AspectGraph graph = this.unmarshalGraph(file.toURI().toURL());
            File newLocation =
                new File(location,
                    STATE_FILTER.addExtension(name));
            getGxlGraphMarshaller().marshalGraph(graph,
                newLocation);
        }

    }

    /**
     * Marshals a single rule, given in graph input format, to a given location.
     * Creates the necessary sub-directories if the rule has a structured rule
     * name.
     * @param ruleGraph the rule to be marshaled
     * @param location the location to which the rule is to be marshaled
     * @throws IOException if {@link Xml#marshalGraph(GraphShape, File)} throws
     *         an exception
     */
    @Override
    public void marshalRule(AspectualRuleView ruleGraph, File location)
        throws IOException {
        this.getGxlGraphMarshaller().marshalGraph(ruleGraph.getAspectGraph(),
            getFile(location, ruleGraph, true));
    }

    /**
     * Deletes the rule given a grammar location.
     */
    @Override
    public void deleteRule(AspectualRuleView ruleGraph, File location) {
        this.getGxlGraphMarshaller().deleteGraph(
            getFile(location, ruleGraph, false));
    }

    /**
     * Saves the properties of a graph grammar in a given location, assumed to
     * be a directory.
     * 
     */
    private void saveProperties(DefaultGrammarView grammar, File location)
        throws IOException, FileNotFoundException {
        // save properties
        SystemProperties properties = grammar.getProperties();
        File propertiesFile =
            new File(location,
                PROPERTIES_FILTER.addExtension(Groove.PROPERTY_NAME));
        properties.store(new FileOutputStream(propertiesFile),
            "Graph grammar properties for " + grammar.getName());
    }

    /**
     * Saves a graph as start graph (using the default name) in a given
     * location, assumed to be a directory.
     * @see #DEFAULT_START_GRAPH_NAME
     */
    private void saveStartGraph(AspectualGraphView startGraph, File location)
        throws IOException {
        if (startGraph != null) {
            // save start graph
            File startGraphLocation =
                new File(location,
                    STATE_FILTER.addExtension(startGraph.getName()));
            getGxlGraphMarshaller().marshalGraph(startGraph.getAspectGraph(),
                startGraphLocation);
        }
    }

    /**
     * Saves a graph as start graph (using the default name) in a given
     * location, assumed to be a directory.
     * @see #DEFAULT_START_GRAPH_NAME
     */
    private void saveControl(ControlView control, File location)
        throws IOException {
        if (control != null) {
            // save start graph
            File controlLocation =
                new File(location, STATE_FILTER.addExtension(control.getName()));
            ControlView.store(control.getProgram(), new FileOutputStream(
                controlLocation));
        }
    }

    @Override
    public DefaultGrammarView unmarshal(URL location, String startGraphName,
            String controlName) throws IOException {
        return unmarshal(toFile(location), startGraphName, controlName);
    }

    /**
     * For backwards compatibility, this creates a grammarsource first
     */
    public DefaultGrammarView unmarshal(File location) throws IOException {
        return unmarshal(location, null, null);
    }

    /**
     * Utility method for easy use.
     */
    public DefaultGrammarView unmarshal(File location, String startGraphName)
        throws IOException {
        return unmarshal(location, startGraphName, null);
    }

    /**
     * unmarshals a grammar for a gps directory with a specific start graph name
     * and a specific control name.
     */
    protected DefaultGrammarView unmarshal(File location,
            String startGraphName, String controlName) throws IOException {

        if (startGraphName == null) {
            startGraphName = DEFAULT_START_GRAPH_NAME;
        }
        if (controlName == null) {
            controlName = DEFAULT_CONTROL_NAME;
        }

        if (!location.exists()) {
            throw new FileNotFoundException(LOAD_ERROR
                + ": rule rystem location \"" + location.getAbsolutePath()
                + "\" does not exist");
        }

        if (!location.isDirectory()) {
            throw new IOException(LOAD_ERROR + ": rule system location \""
                + location + "\" is not a directory");
        }
        DefaultGrammarView result =
            createGrammar(getExtensionFilter().stripExtension(
                location.getName()));

        // PROPERTIES
        File propertiesFile =
            new File(location,
                PROPERTIES_FILTER.addExtension(Groove.PROPERTY_NAME));
        // backwards compatibility: <grammar name>.properties
        if (!propertiesFile.exists()) {
            propertiesFile =
                new File(location,
                    PROPERTIES_FILTER.addExtension(result.getName()));
        }
        if (propertiesFile.exists()) {
            loadProperties(result, toURL(propertiesFile));
        }

        Map<RuleNameLabel,URL> ruleMap = new HashMap<RuleNameLabel,URL>();

        collectRuleNames(ruleMap, location, null);

        // RULES
        loadRules(result, ruleMap);

        // GRAPHS
        Map<String,File> graphMap = new HashMap<String,File>();
        collectGraphNames(graphMap, location);
        for (String graphName : graphMap.keySet()) {
            result.addGraph(graphName, graphMap.get(graphName));
        }

        // START GRAPH
        File startGraphFile;
        if (!hasRecognisedExtension(startGraphName)) {
            startGraphName = STATE_FILTER.addExtension(startGraphName);
        }
        startGraphFile = new File(location, startGraphName);
        if (startGraphFile.exists()) {
            loadStartGraph(result, toURL(startGraphFile));
        } else {
            throw new IOException(String.format("Start graph '%s' does not exist", startGraphName));
        }

        // CONTROL
        File controlFile =
            new File(location, CONTROL_FILTER.addExtension(controlName));
        // backwards compatibility: <grammar name>.gcp
        if (!controlFile.exists()) {
            controlFile =
                new File(location,
                    CONTROL_FILTER.addExtension(result.getName()));

        }
        if (controlFile.exists()) {
            loadControl(result, controlFile);
        }
        return result;
    }

    private boolean hasRecognisedExtension(String filename) {
        File file = new File(filename);
        return AUT_FILTER.accept(file) || STATE_FILTER.accept(file);
    }

    /**
     * Loads a control program for the given grammar from the given control
     * File.
     */
    public void loadControl(DefaultGrammarView grammar, File controlFile)
        throws IOException {
        String controlName =
            CONTROL_FILTER.stripExtension(controlFile.getName());
        loadControl(grammar, toURL(controlFile), controlName);

    }

    /**    mzimakova
     * Loads a control program for the given grammar from the given control
     * File.
     */
    public void loadRule(DefaultGrammarView grammar, File ruleFile)
        throws IOException {
        Map<RuleNameLabel,URL> ruleMap = new HashMap<RuleNameLabel,URL>();
        if (ruleFile == null) {
             throw new IOException(LOAD_ERROR + ": no files found");
        } else {
            // read in production rule
                String fileName = RULE_FILTER.stripExtension(ruleFile.getName());
                PriorityFileName priorityFileName =
                    new PriorityFileName(fileName);
                RuleNameLabel ruleName =
                    new RuleNameLabel(null,
                        priorityFileName.getActualName());
                
                // check for overlapping rule and directory names
                if (ruleMap.put(ruleName, ruleFile.toURI().toURL()) != null) {
                    throw new IOException(LOAD_ERROR
                        + ": duplicate rule name \"" + ruleName + "\"");
                }
        }
        loadRules(grammar, ruleMap);
    }

    /** returns the extension filter for directory grammars */
    public ExtensionFilter getExtensionFilter() {
        return GRAMMAR_FILTER;
    }

    @Override
    public boolean canWrite() {
        return true;
    }

    /**
     * Prepares a location for marshalling a graph grammar.
     */
    private void createLocation(File location) throws IOException {
        // delete existing file, if any
        if (location.exists()) {
            if (!deleteRecursive(location)) {
                throw new IOException("Existing location " + location
                    + " cannot be deleted");
            }
        }
        // create location as directory
        location.mkdirs();
    }

    /**
     * Recursively traverses all subdirectories and deletes all files and
     * directories.
     * TOM: changing this to only delete the rules recursively
     */
    private boolean deleteRecursive(File location) {
        if (location.isDirectory()) {
            for (File file : location.listFiles()) {
                if (!deleteRecursive(file)) {
                    return false;
                }
            }
            // after deleting rules, try if it is empty; if so, delete 
            if( location.listFiles().length == 0 ) {
                return location.delete();
            } else {
                return true;
            }
        }
        else {
            // this is a file. Only delete it if it is a rule or a rule layout
            
            if( RULE_FILTER.accept(location)) {
                location.delete();
                File layout = new File( location , ".gl");
                if( layout.exists() ) {
                    layout.delete();
                }
            }
            // TOM: this should probably reflect whether it succeeded to delete the rule...
            return true;        
        }
    }

    private void collectRuleNames(Map<RuleNameLabel,URL> result,
            File directory, RuleNameLabel rulePath) throws IOException {
        File[] files = directory.listFiles(RULE_FILTER);
        if (files == null) {
            throw new IOException(LOAD_ERROR + ": no files found at "
                + directory);
        } else {
            // read in production rules
            for (File file : files) {
                String fileName = RULE_FILTER.stripExtension(file.getName());
                PriorityFileName priorityFileName =
                    new PriorityFileName(fileName);
                RuleNameLabel ruleName =
                    new RuleNameLabel(rulePath,
                        priorityFileName.getActualName());
                // check for overlapping rule and directory names
                if (file.isDirectory()) {
                    if (!file.getName().equals(".svn")) {
                        collectRuleNames(result, file, ruleName);
                    }
                } else if (result.put(ruleName, file.toURI().toURL()) != null) {
                    throw new IOException(LOAD_ERROR
                        + ": duplicate rule name \"" + ruleName + "\"");
                }
            }
        }
    }

    /**
     * Collects all graphs (states) found directly in the given directly (no
     * subdirectories)
     */
    private void collectGraphNames(Map<String,File> result, File directory) {
        File[] files = directory.listFiles(STATE_FILTER);
        // read in production rules
        for (File file : files) {
            String fileName = RULE_FILTER.stripExtension(file.getName());
            PriorityFileName priorityFileName = new PriorityFileName(fileName);

            // check for overlapping rule and directory names
            if (!file.isDirectory()) {
                result.put(STATE_FILTER.stripExtension(file.getName()), file);
            }
        }
    }

    @Override
    public URL createURL(File file) {
        return toURL(file);
    }

    private static File getFile(File location, AspectualRuleView ruleGraph,
            boolean create) {
        File result = null;
        // if the rule name is structured, go to the relevant sub-directory
        String remainingName = ruleGraph.getName();
        int priority = ruleGraph.getPriority();
        boolean searching = true;
        while (searching) {
            File candidate =
                new File(location, RULE_FILTER.addExtension(remainingName));
            if (!candidate.exists()) {
                PriorityFileName priorityName =
                    new PriorityFileName(remainingName, priority, true);
                candidate =
                    new File(location,
                        RULE_FILTER.addExtension(priorityName.toString()));
            }
            if (candidate.exists()) {
                result = candidate;
            }
            int separator = remainingName.indexOf(RuleNameLabel.SEPARATOR);
            searching = result == null && separator >= 0;
            if (searching) {
                // descend into sub-directory if no file is found and the name
                // specifies a further hierarchy
                String subDir = remainingName.substring(0, separator);
                remainingName = remainingName.substring(separator + 1);
                location = new File(location, subDir);
                // if the sub-directory does not exist, create it or stop the
                // search
                if (!location.exists()) {
                    if (create) {
                        location.mkdir();
                    } else {
                        searching = false;
                    }
                }
            }
        }
        // create a file if none is found and the create parameter says so
        if (result == null && create) {
            result =
                new File(location, RULE_FILTER.addExtension(remainingName));
        }
        return result;
    }

    /**
     * Convenience method for backwards compatibility. Converts a File to an
     * URL, discarding any exceptions since it is well-formed
     */
    public static URL toURL(File file) {
        URL url = null;
        try {
            url = file.toURI().toURL();
        } catch (Exception e) {
            //
        }
        return url;
    }

    /**
     * Convenience method for converting URL's to Files without %20 for spaces
     */
    public static File toFile(URL url) {
        try {
            URI uri = new URI(url.getPath());
            return new File(uri.getPath());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /** File filter for graph grammars in the GPS format. */
    static protected final ExtensionFilter GRAMMAR_FILTER =
        Groove.createRuleSystemFilter();

    /** File filter for transformation rules in the GPR format. */
    static protected final ExtensionFilter RULE_FILTER =
        Groove.createRuleFilter();

    /** File filter for state files. */
    static protected final ExtensionFilter STATE_FILTER =
        Groove.createStateFilter();

    /** File filter for state files. */
    static protected final ExtensionFilter AUT_FILTER =
        new ExtensionFilter("CADP .aut files", Groove.AUT_EXTENSION);

    /** File filter for property files. */
    static protected final ExtensionFilter PROPERTIES_FILTER =
        Groove.createPropertyFilter();

    /** File filter for control files. */
    static protected final ExtensionFilter CONTROL_FILTER =
        Groove.createControlFilter();

    /** Returns the name of the grammar located at the given URL * */
    public String grammarName(URL grammarURL) {
        return GRAMMAR_FILTER.stripExtension(toFile(grammarURL).getName());
    }

    /**
     * Returns a string that should explain where the grammar was found. For a
     * FileGps, this is the absolute path of the parent directory.
     */
    public String grammarLocation(URL grammarURL) {
        return toFile(grammarURL).getParent();
    }

}
