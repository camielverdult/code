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
package groove.match;

import groove.trans.Condition;
import groove.trans.RuleEdge;
import groove.trans.RuleNode;

import java.util.Collection;

/**
 * This is the common interface among factory classes that generate
 * match strategies based on a specific algorithm, such as search plan, 
 * or RETE.
 *   
 * @author Arash Jalali
 * @version $Revision $
 */
public abstract class SearchEngine {
    /**
     * Factory method returning a matcher for a graph condition, taking into
     * account that a certain set of nodes and edges has been matched already.
     * @param condition the condition for which a search plan is to be
     *        constructed
     * @param seedNodes the nodes of the condition that have been matched
     *        already; if <code>null</code>, the condition's pattern map values
     *        are used
     * @param seedEdges the edges of the condition that have been matched
     *        already; if <code>null</code>, the condition's pattern map values
     *        are used
     */
    public abstract SearchStrategy createMatcher(Condition condition,
            Collection<RuleNode> seedNodes, Collection<RuleEdge> seedEdges);

    /** 
     * Value determining what is actually checked in the search.
     * For abstraction, it is important that NACs are checked only after
     * materialisation.
     */
    public static enum SearchMode {
        /** Everything is checked. */
        NORMAL,
        /** Binary NACS and regular expressions are not checked, nor is match injectivity. */
        MINIMAL,
        /** 
         * Only regular expressions are checked; match injectivity is not checked.
         */
        REGEXPR
    }
}
