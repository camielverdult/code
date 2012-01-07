// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id: RuleEvent.java,v 1.21 2008-03-03 21:27:40 rensink Exp $
 */
package groove.trans;

import groove.lts.GraphTransitionStub;
import groove.lts.MatchResult;

/**
 * Interface to encode a rule instantiation that provides images to the rule
 * anchors. Together with the host graph, the event uniquely defines a
 * transformation. The event does not store information specific to the host
 * graph. To apply it to a given host graph, it has to be further instantiated
 * to a rule application.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-03-03 21:27:40 $
 */
public interface RuleEvent extends Comparable<RuleEvent>, GraphTransitionStub,
        MatchResult {
    /**
     * Returns the rule for which this is an application.
     */
    public Rule getRule();

    /**
     * Returns a string representation of the anchor image.
     */
    public String getAnchorImageString();

    /** 
     * Returns the anchor map of the event.
     * The anchor map maps the rule anchor nodes and edges to 
     * host elements.
     * This always refers to the top level existential event. 
     */
    public RuleToHostMap getAnchorMap();

    /** 
     * Returns the anchor image at a given position.
     * This always refers to the anchor of the top level existential event. 
     */
    public AnchorValue getAnchorImage(int i);

    /**
     * Returns a match of this event's rule, based on the anchor map in this
     * event. Returns <code>null</code> if no match exists.
     */
    public Proof getMatch(HostGraph source);

    /**
     * Constructs and records the application of this event to
     * a given host graph.
     */
    public RuleEffect getEffect(HostGraph host);

    /**
     * Records the application of this event, by storing the relevant
     * information into the record object passed in as a parameter.
     */
    void recordEffect(RuleEffect record);

    /**
     * Tests if this event conflicts with another, in the sense that if the
     * events occur in either order it is not guaranteed that the result is the
     * same. This is the case if one event creates a simple edge (i.e., not
     * between creator nodes) that the other erases.
     */
    public boolean conflicts(RuleEvent other);

    /**
     * Factory method to create a rule application on a given source graph.
     */
    public RuleApplication newApplication(HostGraph source);

    /**
     * Convenience method for {@link System#identityHashCode(Object)}, included
     * here for efficiency.
     */
    public int identityHashCode();
}