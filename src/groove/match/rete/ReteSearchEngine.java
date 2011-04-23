/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.match.rete;

import groove.match.SearchEngine;
import groove.match.rete.ReteNetworkNode.Action;
import groove.trans.Condition;
import groove.trans.DeltaStore;
import groove.trans.GraphGrammar;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.RuleEdge;
import groove.trans.RuleNode;
import groove.util.Reporter;
import groove.view.StoredGrammarView;

import java.util.Collection;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class ReteSearchEngine extends SearchEngine<ReteStrategy> {

    private static ReteSearchEngine instances[] = new ReteSearchEngine[2];
    /**
     * A locked engine will respond to all requests through the 
     * specific ReteSearchEngine specified by the lockedInstance
     * member.
     */
    private static boolean locked = false;
    private static ReteSearchEngine lockedInstance = null;

    private boolean injective = false;
    /**
     * The reporter object.
     */
    static public final Reporter reporter =
        Reporter.register(ReteSearchEngine.class);

    /**
     * The reporter for the transitionOccurred method
     */
    static public final Reporter transitionOccurredReporter =
        reporter.register("transitionOccurred()");

    static {
        for (int injective = 0; injective <= 1; injective++) {
            instances[injective] = new ReteSearchEngine(injective == 1);
        }
    }

    /**
     * Instructs the RETE engine to lock down all factory responses to a specific instance.
     * 
     * This method will not lock to the given instance if it is already locked to another one.
     * @param engineInstance The engine instance that this search engine should be locked to.
     * @return <code>true</code> if the engine has not already been locked,
     * <code>false</code> otherwise.
     */
    public static boolean lockToInstance(ReteSearchEngine engineInstance) {
        boolean result = false;
        if (!ReteSearchEngine.locked) {
            ReteSearchEngine.lockedInstance = engineInstance;
            ReteSearchEngine.locked = true;
            result = true;
        }
        return result;
    }

    /**
     * Instructs the engine to come out of the locked mode.
     */
    public static void unlock() {
        ReteSearchEngine.locked = false;
        ReteSearchEngine.lockedInstance = null;
    }

    private ReteSearchEngine(boolean injective) {
        this.injective = injective;
    }

    /**
     * Gets the singleton instance of the engine with the given
     * injectivity property.
     * 
     * We don't support ignoreNeg for the moment. If the Engine has been locked,
     * then the locked instance will only be used if it has the same
     * injectivity property as requested through the <code>injective</code>
     * parameter.
     * 
     * @param injective  Determines if the desired engine instance should perform
     *                   injective matching.
     * @param ignoreNeg  this parameter is ignored at the moment.
     */
    public static synchronized ReteSearchEngine getInstance(boolean injective,
            boolean ignoreNeg) {
        ReteSearchEngine result = instances[injective ? 1 : 0];
        if ((ReteSearchEngine.locked)
            && (ReteSearchEngine.lockedInstance.isInjective() == result.isInjective())) {
            result = ReteSearchEngine.lockedInstance;
        }
        return result;
    }

    /**
     * Creates a fresh instance of the engine for anyone who wants to 
     * make sure their engine is not being updated by other threads.
     * @param injective Determines if the desired engine instance should perform
     *                  injective matching.
     * @param ignoreNeg Look at the documentation for the parameter with the same name
     *                  in the {@link ReteSearchEngine} constructor.
     * @return a fresh instance of the engine.
     */
    public static ReteSearchEngine createFreshInstance(boolean injective,
            boolean ignoreNeg) {
        return new ReteSearchEngine(injective);
    }

    private ReteNetwork network;

    /**
     * @return The network object used by this engine.
     */
    public ReteNetwork getNetwork() {
        return this.network;
    }

    /**
     * Sets up the RETE engine given a <code>GraphGrammar</code> 
     * @param g the grammar to build the RETE network with
     */
    public synchronized void setUp(StoredGrammarView g) {
        HostGraph oldGraph = null;
        if (this.network != null) {
            oldGraph = this.network.getState().getHostGraph();
        }
        this.network = new ReteNetwork(g, this.isInjective());
        if (oldGraph != null) {
            this.network.processGraph(oldGraph);
        }
    }

    /**
     * Tells the engine to set up its RETE network using the given grammar.
     * 
     * All prior matching state of the engine (if any) will be lost after calling this method.
     * @param g The given grammar.
     */
    public synchronized void setUp(GraphGrammar g) {
        this.network = new ReteNetwork(g, this.isInjective());
    }

    /**
     * Populates the RETE network by processing the initial host graph state
     * @param host the host graph to start with
     */
    public synchronized void initializeState(HostGraph host) {
        if (this.network != null) {
            this.network.processGraph(host);
        } else {
            throw new RuntimeException(
                "Must set up the RETE engine before initializing the state.");
        }
    }

    /**
     * Tells the engine to update the RETE runtime state.
     *  
     * @param destGraph The state/host graph that has resulted from the given update.
     *                  This host graph is given to the method so that it could
     *                  decide if re-initializing the RETE network is less costly
     *                  than applying the updates in the <code>deltaStore</code>.
     * @param deltaStore Represents the actual update (node/edge creations/removals) 
     *                   to the host graph which could be the sum of the effects 
     *                   of a series of rule applications/transitions.
     */
    public synchronized void transitionOccurred(HostGraph destGraph,
            DeltaStore deltaStore) {
        transitionOccurredReporter.start();

        if (deltaStore.size() > destGraph.size()) {
            this.network.processGraph(destGraph);
            transitionOccurredReporter.stop();
            return;
        }

        for (HostNode n : deltaStore.getRemovedNodeSet()) {
            this.network.update(n, Action.REMOVE);
        }

        for (HostEdge e : deltaStore.getRemovedEdgeSet()) {
            this.network.update(e, Action.REMOVE);
        }

        for (HostNode n : deltaStore.getAddedNodeSet()) {
            this.network.update(n, Action.ADD);
        }

        for (HostEdge e : deltaStore.getAddedEdgeSet()) {
            this.network.update(e, Action.ADD);
        }

        this.network.getState().setHostGraph(destGraph);
        transitionOccurredReporter.stop();
    }

    @Override
    public synchronized ReteStrategy createMatcher(Condition condition,
            Collection<RuleNode> seedNodes, Collection<RuleEdge> seedEdges,
            Collection<RuleNode> relevantNodes) {
        //this will get more complicated when we have complex conditions        
        return new ReteStrategy(this, condition);
    }

    /** Indicates if the matchers this factory produces are injective. */
    public boolean isInjective() {
        return this.injective;
    }

}
