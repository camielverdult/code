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
 * $Id: ExplorationTest.java,v 1.4 2007-03-29 09:59:49 rensink Exp $
 */

package groove.test;

import groove.io.AspectualGpsGrammar;
import groove.io.LayedOutGpsGrammar;
import groove.io.XmlGrammar;
import groove.lts.ConditionalExploreStrategy;
import groove.lts.DerivedGraphRuleFactory;
import groove.lts.ExploreStrategy;
import groove.lts.GTS;
import groove.lts.explore.FullStrategy;
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.trans.StructuredRuleName;
import groove.util.Generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;

/**
 * System test class, which explores a number of graph production systems and tests if this gives
 * rise to the expected numbers of states and transitions. The test cases are listed in a separate
 * file, named in {@link #TEST_CASES_NAME}.
 * 
 * @author Arend Rensink
 * @version $Revision: 1.4 $
 */
public class ExplorationTest extends TestCase {
    static public final String INPUT_DIR = "junit/samples";
    /**
     * Name of a text file to be found in the same package as this class. 
     */
    static public final String TEST_CASES_NAME = "groove/test/testcases.txt";

    /** The test command. */
    static public final String TEST_COMMAND = "test";

    /** Position of the command on a test case line. */
    static public final int COMMAND_INDEX = 0;

    static public final int GRAMMAR_INDEX = 1;

    static public final int START_STATE_INDEX = 2;

    static public final int STRATEGY_INDEX = 3;

    static public final int NODE_COUNT_INDEX = 4;

    static public final int EDGE_COUNT_INDEX = 5;

    static public final int OPEN_COUNT_INDEX = 6;

    /**
     * Record to store the data of a single test case.
     */
    public class TestCaseRecord {
        /** Sets the fields of this record. */
        public TestCaseRecord(String grammarName, String startFileName, String strategy,
                int nodeCount, int edgeCount, int openCount) {
            super();
            this.grammarName = grammarName;
            this.startGraphName = startFileName;
            this.strategy = strategy;
            this.nodeCount = nodeCount;
            this.edgeCount = edgeCount;
            this.openCount = openCount;
        }

        /** Name of the grammar to be explored in this test case */
        public final String grammarName;

        /** Name of the start state to be explored in this test case */
        public final String startGraphName;

        /** Exploration strategy of this test case */
        public final String strategy;

        /** Expected number of nodes resulting from exploration. */
        public final int nodeCount;

        /** Expected number of edges resulting from exploration. */
        public final int edgeCount;

        /** Expected number of open states remaining after exploration. */
        public final int openCount;
    }
    
    public void testAppend() {
        testExploration("append.gps", "append-2-list-5", null, 145, 256);
        testExploration("append.gps", "append-2-list-5", "full", 145, 256);
        testExploration("append.gps", "append-2-list-5", "branching", 145, 256);
        testExploration("append.gps", "append-2-list-5", "node-bounded:20", 62, 88, 13);
        testExploration("append.gps", "append-2-list-5", "edge-bounded:append=6", 79, 108, 12);
    }

    public void testFerryman() {
        testExploration("ferryman.gps", "start", "full", 114, 198);
        testExploration("ferryman.gps", "start", "bounded:!eat", 40, 51);
    }

    public void testMergers() {
        testExploration("mergers.gps", 66, 143);
    }

    public void testRegExpr() {
        testExploration("regexpr.gps", 16, 48);
    }

    public void testLooseNodes() {
        testExploration("loose-nodes.gps", 104, 468);
        testExploration("loose-nodes.gps", "start", "linear", 10, 9);
    }

    public void testPriorities() {
        testExploration("priorities.gps", "start", "full", 13, 34);
        testExploration("priorities.gps", "start", "branching", 13, 34);
        testExploration("priorities.gps", "start", "linear", 8, 8);
    }

    public void testVariables() {
        testExploration("variables.gps", "start-smaller", "full", 61, 176);
    }

    public void testCounting() {
        testExploration("counting.gps", 10, 9);
    }

    public void testAttributes() {
        testExploration("attributed-graphs.gps", 6, 16);
    }

    /** 
     * Reads and executes the test cases specified in a given named file. 
     * The format is described in {@link #testExplorations(BufferedReader)}.
     * Calls <tt>testExploration(new BufferedReader(new FileReader(filename)))</tt>.
     */
    protected void testExplorations(String filename) {
        try {
            BufferedReader testCasesReader = new BufferedReader(new FileReader(filename));
            testExplorations(testCasesReader);
        } catch (IOException exc) {
            fail("Can't find test cases file: "+exc);
        }
    }

    /** 
     * Reads and executes the test cases in a given input stream. 
     * The format is described in {@link #testExplorations(BufferedReader)}.
     * Calls <tt>testExploration(new BufferedReader(new InputStreamReader(testCasesStream)))</tt>.
     */
    protected void testExplorations(InputStream testCasesStream) {
        BufferedReader testCasesReader = new BufferedReader(new InputStreamReader(testCasesStream));
        testExplorations(testCasesReader);
    }

    /** 
     * Reads and executes the test cases in a given reader. Each line describes a test
     * case, as while space-separated fields
     * <ul>
     * <li> The test command {@link #TEST_COMMAND}
     * <li> Name of the production system, to be found in the same package as the text file
     * <li> Name of the start state, to be found in the production system
     * <li> Exploration strategy, in the input format of {@link groove.util.Generator.ExploreOption}
     * <li> Expected number of states
     * <li> Expected number of transitions; not tested if < 0
     * <li> (Optional) expected number of open states after exploration; not tested if < 0.
     * (Used for bounded exploration strategies)
     * </ul>
     * Lines not starting with {@link #TEST_COMMAND} are ignored.
     */
    protected void testExplorations(BufferedReader testCasesReader) {
        TestCaseRecord testCase = readNextRecord(testCasesReader);
        while (testCase != null) {
            testExploration(testCase);                
            testCase = readNextRecord(testCasesReader);
        }      
    }

    /**
     * Tests exploration according to a given test case record.
     * @return the explored GTS
     */
    protected GTS testExploration(TestCaseRecord testCase) {
        return testExploration(testCase.grammarName, testCase.startGraphName, testCase.strategy, testCase.nodeCount, testCase.edgeCount);
    }

    /**
     * Tests exploration of a given grammar, starting at a given start graph,
     * and using a given exploration strategy.
     * @param grammarName name of the rule system to be tested
     * @param startGraphName name of the start graph
     * @param strategyDescr description of the exploration strategy to be used, in the format of {@link Generator.ExploreOption} 
     * @param nodeCount expected number of nodes; disregarded if < 0
     * @param edgeCount expected number of edges; disregarded if < 0
     * @param openCount expected number of open states; disregarded if < 0
     * @return the explored GTS
     */
    protected GTS testExploration(String grammarName, String startGraphName, String strategyDescr, int nodeCount,
            int edgeCount, int openCount) {
        try {
            GraphGrammar gg = loader.unmarshalGrammar(new File(INPUT_DIR + "\\"+ grammarName), startGraphName);
            GTS lts = gg.gts();
            ExploreStrategy strategy;
            if (strategyDescr != null) {
            	parser.parse(strategyDescr);
                strategy = parser.getStrategy();
                if (strategy instanceof ConditionalExploreStrategy) {
                    Rule conditionRule = gg.getRule(new StructuredRuleName(parser
                            .getCondition()));
                    assertNotNull(conditionRule);
                    ((ConditionalExploreStrategy) strategy).setCondition(conditionRule);
                    ((ConditionalExploreStrategy) strategy).setNegated(parser.isNegated());
                }
            } else {
            	strategy = new FullStrategy();
            }
            strategy.setLTS(lts);
            try {
            	strategy.explore();
            } catch (InterruptedException exc) { // proceed
            }
            if (nodeCount >= 0) {
                assertEquals(nodeCount, lts.nodeCount());
            }
            if (edgeCount >= 0) {
                assertEquals(edgeCount, lts.edgeCount());
            }
            if (openCount >= 0) {
                assertEquals(openCount, lts.openStateCount());
            }
            return lts;
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    /**
     * Tests exploration of a given grammar, starting at a given start graph,
     * and using a given exploration strategy.
     * @param grammarName name of the rule system to be tested
     * @param startGraphName name of the start graph
     * @param strategyDescr description of the exploration strategy to be used, in the format of {@link Generator.ExploreOption} 
     * @param nodeCount expected number of nodes; disregarded if < 0
     * @param edgeCount expected number of edges; disregarded if < 0
     * @return the explored GTS
     */
    protected GTS testExploration(String grammarName, String startGraphName, String strategyDescr, int nodeCount,
            int edgeCount) {
        return testExploration(grammarName, startGraphName, strategyDescr, nodeCount, edgeCount, -1);
    }

    /**
     * Tests exploration of a given grammar, starting at a given start graph.
     * The exploration strategy is the default one.
     * @param grammarName name of the rule system to be tested
     * @param startGraphName name of the start graph
     * @param nodeCount expected number of nodes; disregarded if < 0
     * @param edgeCount expected number of edges; disregarded if < 0
     * @return the explored GTS
     */
    protected GTS testExploration(String grammarName, String startGraphName, int nodeCount,
            int edgeCount) {
        return testExploration(grammarName, startGraphName, null, nodeCount, edgeCount);
    }

    /**
     * Tests exploration of a given grammar, starting at the default start graph.
     * The exploration strategy is the default one.
     * @param grammarName name of the rule system to be tested
     * @param nodeCount expected number of nodes
     * @param edgeCount expected number of edges
     * @return the explored GTS
     */
    protected GTS testExploration(String grammarName, int nodeCount, int edgeCount) {
        return testExploration(grammarName, XmlGrammar.DEFAULT_START_GRAPH_NAME, nodeCount, edgeCount);
    }
    
    /**
     * Returns the test case record corresponding to the test command line on the input. Returns
     * <tt>null</tt> if no more test command lines are found.
     * @param testCaseFile the input file
     * @return test record for the next test, or <tt>null</tt> if no more test is found.
     */
    private TestCaseRecord readNextRecord(BufferedReader testCaseFile) {
        TestCaseRecord result = null;
        try {
            String nextLine = testCaseFile.readLine();
            while (nextLine != null && result == null) {
                String[] args = nextLine.split("\\s+");
                if (args[COMMAND_INDEX].equals(TEST_COMMAND)) {
                    int nodeCount = Integer.parseInt(args[NODE_COUNT_INDEX]);
                    int edgeCount = Integer.parseInt(args[EDGE_COUNT_INDEX]);
                    int openCount = -1;
                    if (args.length >= OPEN_COUNT_INDEX) {
                        openCount = Integer.parseInt(args[OPEN_COUNT_INDEX]);
                    }
                    result = new TestCaseRecord(args[GRAMMAR_INDEX], args[START_STATE_INDEX],
                            args[STRATEGY_INDEX], nodeCount, edgeCount, openCount);
                }
            }
        } catch (IOException exc) {
            fail("Error reading test case file: " + exc);
        } catch (NumberFormatException exc) {
            fail("Number format in parameter: " + exc);
        }
        return result;
    }

    /**
     * Parser for the exploreation strategies.
     */
    private Generator.ExploreStrategyParser parser = new Generator.ExploreStrategyParser();

    /**
     * Grammar loader used in this test case.
     */
    protected XmlGrammar loader = new AspectualGpsGrammar(DerivedGraphRuleFactory.getInstance());
}