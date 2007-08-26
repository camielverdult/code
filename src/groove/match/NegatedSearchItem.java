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
 * $Id: NegatedSearchItem.java,v 1.2 2007-08-26 07:24:12 rensink Exp $
 */
package groove.match;

import static groove.match.SearchPlanStrategy.Search;

/**
 * A search item that negates another search item.
 * @author Arend Rensink
 * @version $Revision $
 */
public class NegatedSearchItem extends ConditionSearchItem {
	/** 
	 * Constructs a new search item. The item will match (precisely once)
	 * if and only the underlying item does not match.
	 * @param item the underlying, negated item
	 */
	public NegatedSearchItem(SearchItem item) {
		this.inner = item;
	}
	
    @Override
	public NegatedSearchRecord getRecord(Search search) {
		return new NegatedSearchRecord(search);
	}
	
    @Override
	public String toString() {
		return String.format("Negation of %s", inner); 
	}

	/**
	 * The inner search item, for which we test for the negation.
	 */
	protected final SearchItem inner;

    /** Record for the negated search item. */
    protected class NegatedSearchRecord extends ConditionRecord {
        /** Constructs a new record, for a given matcher. */
        protected NegatedSearchRecord(Search search) {
            super(search);
            this.innerRecord = inner.getRecord(search);
        }

        /**
         * Tests if the inner record can be satisfied; if so,
         * it is undone immediately to avoid lasting effects.
         */
        @Override
        protected boolean condition() {
            boolean result = !innerRecord.find();
            innerRecord.reset();
            return result;
        }

        @Override
        public void exit() {
            innerRecord.reset();
            super.exit();
        }
        
        /**
         * The record of the inner (negated) item.
         */
        private final SearchItem.Record innerRecord;
    }
}
