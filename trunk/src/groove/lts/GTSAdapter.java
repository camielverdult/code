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
 * $Id: LTSAdapter.java,v 1.2 2008-01-30 09:32:20 iovka Exp $
 */
package groove.lts;


/**
 * Default implementation of an LTS listener that does nothing.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GTSAdapter implements GTSListener {
    /** Provides an empty default implementation. */
    @Override
    public void addUpdate(GTS gts, GraphState state) {
        // empty default implementation
    }

    /** Provides an empty default implementation. */
    @Override
    public void addUpdate(GTS gts, GraphTransition transition) {
        // empty default implementation
    }

    /**
     * Event that occurs when closing a state. This default implementation
     * has no behaviour.
     */
    public void closeUpdate(GTS graph, GraphState explored) {
        // this implementation does nothing
    }
}
