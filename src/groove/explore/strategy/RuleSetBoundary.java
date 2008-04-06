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
 * $Id: RuleSetBoundary.java,v 1.6 2008/03/21 12:36:04 kastenberg Exp $
 */
package groove.explore.strategy;

import groove.lts.ProductTransition;
import groove.trans.Rule;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of interface {@link Boundary} that
 * bases the boundary on a set of rules for which application
 * are said to cross the boundary.
 *
 * @author Harmen Kastenberg
 * @version $Revision: 1.6 $
 */
public abstract class RuleSetBoundary extends AbstractBoundary {

	/**
	 * {@link RuleSetBoundary} constructor.
	 * @param ruleSetBoundary the set of rules that constitute the boundary
	 */
	public RuleSetBoundary(Set<Rule> ruleSetBoundary) {
		this.ruleSetBoundary.addAll(ruleSetBoundary);
	}

	/**
	 * Add a rule to the set of boundary rules.
	 * @param rule the rule to be added
	 * @return see {@link java.util.Set#add(Object)}
	 */
	public boolean addRule(Rule rule) {
		return ruleSetBoundary.add(rule);
	}

	public boolean crossingBoundary(ProductTransition transition) {
		Rule rule = transition.graphTransition().getEvent().getRule();
		if (!ruleSetBoundary.contains(rule)) {
			return false;
		} else {
			return true;
		}
//		// if the maximal allowed depth is not yet reached
//		// the transition may be taken anyway
//		Rule rule = transition.graphTransition().getEvent().getRule();
//		boolean forbiddenRule = ruleSetBoundary.contains(rule);
//
//		boolean depth = currentDepth() <= ModelChecking.CURRENT_ITERATION;
//			
//		if (crossingBoundary) {
//			if (allowed.get(rule)) {
//				if (ALLOW_ALL_APPLICATIONS == ALLOW_SINGLE_APPLICATION) {
//					setAllowMap();
//				} else {
//					allowed.put(rule, false);
//				}
//				return false;
//			}
//			return true;
//		}
//		return false;
	}

	public void increase() {
		// do nothing
	}

	/** the set of rules that are initially forbidden to apply */
	private Set<Rule> ruleSetBoundary = new HashSet<Rule>();
}
