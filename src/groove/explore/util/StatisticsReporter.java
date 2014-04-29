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
package groove.explore.util;

import static groove.explore.Verbosity.HIGH;
import static groove.explore.Verbosity.MEDIUM;
import groove.explore.Exploration;
import groove.explore.Verbosity;
import groove.grammar.Rule;
import groove.grammar.host.HostFactory;
import groove.graph.AGraph;
import groove.graph.iso.IsoChecker;
import groove.graph.iso.PartitionRefiner;
import groove.lts.AbstractGraphState;
import groove.lts.GTS;
import groove.lts.GTSAdapter;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.GraphState.Flag;
import groove.lts.GraphTransition;
import groove.lts.MatchApplier;
import groove.lts.MatchCollector;
import groove.transform.Record;
import groove.util.Groove;
import groove.util.Reporter;
import groove.util.cache.AbstractCacheHolder;
import groove.util.cache.CacheReference;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

/**
 * @author Eduardo Zambon
 */
public class StatisticsReporter extends AExplorationReporter {

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    /** Number of bytes in a kilobyte. */
    static private final int BYTES_PER_KB = 1024;

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** Time stamp of the moment at which exploration was started. */
    private long startTime;

    /** Time stamp of the moment at which exploration was ended. */
    private long endTime;

    /** Amount of memory used at the moment at which exploration was started. */
    private long startUsedMemory;

    private StringBuilder sb;
    private Formatter fm;
    /** The verbosity level with which {@link #sb} was built. */
    private Verbosity sbVerbosity;

    private final Verbosity verbosity;
    private final StatisticsListener statisticsListener = new StatisticsListener();

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs a exploration statistics object at a 
     * given verbosity level.
     */
    public StatisticsReporter(Verbosity verbosity) {
        this.verbosity = verbosity;
    }

    /**
     * Constructs a exploration statistics object with high verbosity.
     */
    public StatisticsReporter() {
        this(Verbosity.HIGH);
    }

    /** Starts up the reporter, for a given GTS. */
    @Override
    public void start(Exploration exploration, GTS gts) {
        super.start(exploration, gts);
        Runtime runTime = Runtime.getRuntime();
        runTime.gc();
        this.startUsedMemory = runTime.totalMemory() - runTime.freeMemory();
        if (!this.verbosity.isLow()) {
            gts.addLTSListener(this.statisticsListener);
        }
        this.startTime = System.currentTimeMillis();
        // clear any previous report
        this.sb = null;
        this.fm = null;
    }

    /** Reports the statistics of the GTS.
     * Should be called right after the exploration finishes. */
    @Override
    public void report() {
        this.endTime = System.currentTimeMillis();
        getGTS().removeLTSListener(this.statisticsListener);
    }

    /**
     * Returns the statistics report string at the default verbosity level
     * (set at construction time).
     */
    public String getReport() {
        return getReport(this.verbosity);
    }

    /** Returns the statistics report, at a given verbosity level. */
    public String getReport(Verbosity verbosity) {
        if (getExploration() != null) {
            // build the report if that has not yet been done
            if (this.sb == null || this.sbVerbosity != verbosity) {
                createReport(verbosity);
            }
        } else {
            return "*** No exploration performed since the current grammar was opened. ***";
        }
        return this.sb.toString();
    }

    private void createReport(Verbosity verbosity) {
        // Set the string builder before we start.
        this.sb = new StringBuilder();
        this.fm = new Formatter(this.sb);
        this.sbVerbosity = verbosity;
        reportProfiling();
        reportStatistics();
        reportTime();
        reportSpace();
        reportLTS();
    }

    private void reportProfiling() {
        emit(HIGH, "%n");
        if (this.sbVerbosity.isHigh()) {
            StringWriter sw = new StringWriter();
            Reporter.report(new PrintWriter(sw));
            this.sb.append(sw.toString());
        }
        emit(HIGH,
            "===============================================================================%n");
    }

    private void reportStatistics() {
        if (Groove.GATHER_STATISTICS) {
            reportGraphStatistics();
            reportGraphElementStatistics();
            reportTransitionStatistics();
            reportIsomorphism();
            reportCacheStatistics();
        }
    }

    /**
     * Returns a string describing the distribution of cache reconstruction
     * counts.
     */
    private String getCacheReconstructionDistribution() {
        List<Integer> sizes = new ArrayList<Integer>();
        boolean finished = false;
        for (int incarnation = 1; !finished; incarnation++) {
            int size = CacheReference.getFrequency(incarnation);
            finished = size == 0;
            if (!finished) {
                sizes.add(size);
            }
        }
        return Groove.toString(sizes.toArray());
    }

    /** Reports data on the LTS generated. */
    private void reportLTS() {
        int openRecipeStageCount = this.statisticsListener.getOpenRecipeStageCount();
        int closedRecipeStageCount = this.statisticsListener.getClosedRecipeStageCount();
        int realStateCount = getGTS().nodeCount() - openRecipeStageCount - closedRecipeStageCount;
        String formatString = "%-14s%d%n";
        emit(MEDIUM, "%n");
        emit(MEDIUM, formatString, "States:", realStateCount);
        int openRealStateCount = getGTS().openStateCount() - openRecipeStageCount;
        if (openRealStateCount > 0) {
            emit(MEDIUM, formatString, "Explored:", (realStateCount - openRealStateCount));
        }
        int recipeStepCount = this.statisticsListener.getRecipeStepCount();
        int realTransitionCount = getGTS().edgeCount() - recipeStepCount;
        emit(MEDIUM, formatString, "Transitions:", realTransitionCount);
    }

    /** Gives some statistics regarding the graphs and deltas. */
    private void reportGraphStatistics() {
        String formatString = "    %-20s";
        String intFormatString = formatString + "%d%n";
        String floatFormatString = formatString + "%.1f%n";
        emit(HIGH, "%nGraph count%n");
        emit(HIGH, intFormatString, "Modifiable:", AGraph.getModifiableGraphCount());
        emit(HIGH, intFormatString, "Frozen:", AbstractGraphState.getFrozenGraphCount());
        emit(HIGH, floatFormatString, "Bytes/state:", getGTS().getBytesPerState());
    }

    /** Gives some statistics regarding the generated transitions. */
    private void reportTransitionStatistics() {
        String format = "    %-20s";
        String intFormat = format + "%d%n";
        String ratioFormat = format + "%d/%d%n";
        emit(HIGH, "%nTransition count%n");
        emit(HIGH, intFormat, "Reused:", MatchCollector.getEventReuse());
        emit(HIGH, intFormat, "Confluent:", MatchApplier.getConfluentDiamondCount());
        emit(HIGH, intFormat, "Events:", Record.getEventCount());
        emit(HIGH, ratioFormat, "Coanchor reuse:", HostFactory.getNormaliseGain(),
            HostFactory.getNormaliseCount());
    }

    /** Reports statistics on isomorphism checking. */
    private void reportIsomorphism() {
        int predicted = IsoChecker.getTotalCheckCount();
        int falsePos2 = IsoChecker.getDistinctSimCount();
        int falsePos1 =
            falsePos2 + IsoChecker.getDistinctSizeCount() + IsoChecker.getDistinctCertsCount();
        int equalGraphCount = IsoChecker.getEqualGraphsCount();
        int equalCertsCount = IsoChecker.getEqualCertsCount();
        int equalSimCount = IsoChecker.getEqualSimCount();
        int intCertOverlap = IsoChecker.getIntCertOverlap();
        String format = "    %-20s";
        String intFormat = format + "%d%n";
        String stringFormat = format + "%s%n";
        String intIntFormat = format + "%-6d(-%d)%n";
        String percFormat = "   " + format + "%-6d(%4.1f%%)%n";
        emit(HIGH, "%nIsomorphism statistics%n");
        emit(HIGH, intIntFormat, "Predicted:", predicted, intCertOverlap);
        emit(HIGH, percFormat, "False pos 1:", falsePos1, (double) 100 * falsePos1
            / (predicted - intCertOverlap));
        emit(HIGH, percFormat, "False pos 2:", falsePos2, (double) 100 * falsePos2
            / (predicted - intCertOverlap));
        emit(HIGH, intFormat, "Equal graphs:", equalGraphCount);
        emit(HIGH, intFormat, "Equal certificates:", equalCertsCount);
        emit(HIGH, intFormat, "Equal simulation:", equalSimCount);
        emit(HIGH, stringFormat, "Iterations:", PartitionRefiner.getIterateCount());
        emit(HIGH, intFormat, "Symmetry breaking:", PartitionRefiner.getSymmetryBreakCount());
    }

    /** Reports on the graph data. */
    private void reportGraphElementStatistics() {
        HostFactory factory = getGTS().getHostFactory();
        String format = "    %-20s";
        String intFormat = format + "%d%n";
        String floatFormat = format + "%5.1f%n";
        emit(HIGH, "%nGraph element count%n");
        emit(HIGH, intFormat, "Factory nodes:", factory.getNodeCount());
        emit(HIGH, intFormat, "Factory edges:", factory.getEdgeCount());
        double nodeAvg = (double) this.statisticsListener.getNodeCount() / getGTS().nodeCount();
        emit(HIGH, floatFormat, "Nodes/state (avg):", nodeAvg);
        double edgeAvg = (double) this.statisticsListener.getEdgeCount() / getGTS().edgeCount();
        emit(HIGH, floatFormat, "Edges/state (avg):", edgeAvg);
    }

    /** Reports on the cache usage. */
    private void reportCacheStatistics() {
        String format = "    %-20s";
        String intFormat = format + "%d%n";
        String stringFormat = format + "%s%n";
        emit(HIGH, "%nCache statistics%n");
        emit(HIGH, intFormat, "Created:", CacheReference.getCreateCount());
        emit(HIGH, intFormat, "Cleared:", CacheReference.getClearCount());
        emit(HIGH, intFormat, "Collected:", CacheReference.getCollectCount());
        emit(HIGH, intFormat, "Reconstructed:", CacheReference.getIncarnationCount());
        emit(HIGH, stringFormat, "Distribution:", getCacheReconstructionDistribution());
    }

    /** Reports on the time usage. */
    private void reportTime() {
        // Timing figures.
        long total = (this.endTime - this.startTime);
        long matching = Rule.getMatchingTime();
        long running = Exploration.getRunningTime();
        long overhead = total - running;
        long isoChecking = IsoChecker.getTotalTime();
        long generateTime = MatchApplier.getGenerateTime();
        long building = generateTime - isoChecking;
        long measuring = Reporter.getReportTime();

        // This calculation incorporates only transforming RuleMatches into
        // RuleApplications, bit weird maybe, but transforming is considered
        // everything besides the calculation of matches, isomorphisms, adding
        // to GTS, and reporter-duty: i.e. it's the "overhead" of the scenario.
        long transforming = running - matching - isoChecking - building - measuring;

        String format = "%-20s%d%n";
        emit(MEDIUM, "%n");
        emit(MEDIUM, format, "Time (ms):", total);

        // Time breakup only reported under high verbosity
        format = "    %-15s%7d    (%4.1f%%)%n";
        String longFormat = "    " + format;
        emit(HIGH, format, "Matching:", matching, 100 * matching / (double) total);
        emit(HIGH, format, "Transforming:", transforming, 100 * transforming / (double) total);
        emit(HIGH, format, "Iso checking:", isoChecking, 100 * isoChecking / (double) total);

        long certifying = IsoChecker.getCertifyingTime();
        long equalCheck = IsoChecker.getEqualCheckTime();
        long certCheck = IsoChecker.getCertCheckTime();
        long simCheck = IsoChecker.getSimCheckTime();
        emit(HIGH, longFormat, "Certifying:", certifying, 100 * certifying / (double) isoChecking);
        emit(HIGH, longFormat, "Equals check:", equalCheck, 100 * equalCheck / (double) isoChecking);
        emit(HIGH, longFormat, "Cert check:", certCheck, 100 * certCheck / (double) isoChecking);
        emit(HIGH, longFormat, "Sim check:", simCheck, 100 * simCheck / (double) isoChecking);

        emit(HIGH, format, "Building GTS:", building, 100 * building / (double) total);
        emit(HIGH, format, "Measuring:", measuring, 100 * measuring / (double) total);
        emit(HIGH, format, "Initialization:", overhead, 100 * overhead / (double) total);
    }

    /**
     * Reports on the space usage.
     */
    private void reportSpace() {
        final Runtime runTime = Runtime.getRuntime();
        // Clear all caches to see all available memory.
        for (GraphState state : getGTS().nodeSet()) {
            if (state instanceof AbstractCacheHolder<?>) {
                ((AbstractCacheHolder<?>) state).clearCache();
            }
            if (state instanceof GraphNextState) {
                ((AbstractCacheHolder<?>) ((GraphNextState) state).getEvent()).clearCache();
            }
        }
        // The following is to make sure that the graph reference queue gets
        // flushed.
        System.runFinalization();
        System.gc();
        long usedMemory = runTime.totalMemory() - runTime.freeMemory();
        String format = "%-20s%d%n";
        emit(HIGH, "%n");
        emit(MEDIUM, format, "Space (kB):", (usedMemory - this.startUsedMemory) / BYTES_PER_KB);
    }

    /** 
     * Prints a formatted string to the output stream,
     * at a given minimum verbosity. 
     */
    private void emit(Verbosity at, String text, Object... args) {
        if (at.compareTo(this.sbVerbosity) <= 0) {
            this.fm.format(text, args);
        }
    }

    /** Listener to an LTS that counts the nodes and edges of the states. */
    private static class StatisticsListener extends GTSAdapter {
        /** Empty constructor with the correct visibility. */
        StatisticsListener() {
            // Empty.
        }

        @Override
        public void addUpdate(GTS gts, GraphState state) {
            this.nodeCount += state.getGraph().nodeCount();
            this.edgeCount += state.getGraph().edgeCount();
            if (state.isRecipeStage()) {
                if (state.isClosed()) {
                    this.closedRecipeStageCount++;
                } else {
                    this.openRecipeStageCount++;
                }
            }
        }

        @Override
        public void addUpdate(GTS gts, GraphTransition transition) {
            if (transition.isPartial()) {
                this.recipeStepCount++;
            }
        }

        @Override
        public void statusUpdate(GTS graph, GraphState explored, Flag flag) {
            if (flag == Flag.CLOSED) {
                if (explored.getPrimeFrame().isTransient()) {
                    this.openRecipeStageCount--;
                }
                if (explored.isRecipeStage()) {
                    this.closedRecipeStageCount++;
                }
            }
        }

        /** Returns the number of nodes in the added states. */
        public int getNodeCount() {
            return this.nodeCount;
        }

        /** Returns the number of edges in the added states. */
        public int getEdgeCount() {
            return this.edgeCount;
        }

        /** Returns the number of closed recipe stages in the GTS. */
        public int getClosedRecipeStageCount() {
            return this.closedRecipeStageCount;
        }

        /** Returns the number of open recipe stages in the GTS. */
        public int getOpenRecipeStageCount() {
            return this.openRecipeStageCount;
        }

        /** Returns the number of recipe steps in the GTS. */
        public int getRecipeStepCount() {
            return this.recipeStepCount;
        }

        private int nodeCount;
        private int edgeCount;
        private int closedRecipeStageCount;
        private int openRecipeStageCount;
        private int recipeStepCount;
    }
}
