package groove.control; import groove.explore.util.LocationCache;import groove.lts.GraphState;import groove.lts.GraphTransition;import groove.trans.Rule;import groove.util.Pair;import java.util.HashMap;import java.util.HashSet;import java.util.Map;import java.util.Set;/** *  * Caching implementation of the "Location" part of a GraphState * when using control.  *  * @author Tom Staijen * @version $Revision $ */public class ControlLocation implements Location {	private LocationAutomatonBuilder builder;	/** the location is based on this pair of state->failureset, initialized by setStates */	private Set<Pair<ControlState, Set<Rule>>> states = new HashSet<Pair<ControlState, Set<Rule>>>();	/** stores controlstate as key, the enabling failuresets as value, initialized by setStates */	protected Map<ControlState, Set<Set<Rule>>> stateFailures = new HashMap<ControlState, Set<Set<Rule>>>();	/** initialization method called by the LocationAutomatonBuilder **/	/**	 *   The failure dependency of a state is overwritten when a failure is a subset of an already existing failure dependency.	 */	protected void setStates(Set<Pair<ControlState, Set<Rule>>> pairs) {		this.states = pairs;		for (Pair<ControlState, Set<Rule>> pair : pairs) {			Set<Set<Rule>> sf = stateFailures.get(pair.first());			if( sf == null ) {				sf = new HashSet<Set<Rule>>();				stateFailures.put(pair.first(), sf);			}			sf.add(pair.second());//			System.out.println("(()) adding state failure: " + pair.first() + " -> " + pair.second());//			this.stateFailures.put(pair.first(), pair.second());			}	}	/** 	 * Default constructor	 * Needs the automatonbuilder to build target locations, while reusing when possible 	 * @param builder	 */	public ControlLocation(LocationAutomatonBuilder builder) {		this.builder = builder;	}	/** cache for target ControlLocation of a certain rule with a certain subset of the depending rules **/	private Map<Pair<Rule, Set<Rule>>, ControlLocation> targetLocationCache = new HashMap<Pair<Rule, Set<Rule>>, ControlLocation>();	/**	 * 	 * Returns the target location of a rule applied from this location,	 * given the failing subset of the set of rules the application	 * of rule depends on.	 * 	 * @param rule	 * @param failed	 * @return target ControlLocation	 */	private ControlLocation getTargetLocation(Rule rule, Set<Rule> failed) {		Pair<Rule,Set<Rule>> p = new Pair<Rule, Set<Rule>>(rule, failed);		ControlLocation l = targetLocationCache.get(p);		if (l == null) {			Set<ControlState> states = getTargetStates(rule, failed);			l = builder.getLocation(states);			targetLocationCache.put(p, l);		}						if( l == null ) {			assert l != null: "Target Location should never be null";			}				return l;	}	/**	 * Stores the original ControlState's in this Location this rule was applicable from	 */	private Map<Rule, Set<ControlState>> ruleSourceStates = new HashMap<Rule, Set<ControlState>>();	/** 	 * Stores a the ControlState as a actual control state (encapsulated in this Location)	 * the rule was applicable from.	 * @param rule	 * @param source	 */	protected void addRuleSource(Rule rule, ControlState source) {		Set<ControlState> sources = ruleSourceStates.get(rule);		if (sources == null) {			sources = new HashSet<ControlState>();			ruleSourceStates.put(rule, sources);		}		sources.add(source);	}	/**	 * returns the set of target states given the rule and the set of failed	 * rules *	 */	protected Set<ControlState> getTargetStates(Rule rule, Set<Rule> failed) {		Set<ControlState> targets = new HashSet<ControlState>();		for (ControlState state : ruleSourceStates.get(rule)) {			boolean add = false;			for( Set<Rule> failures : stateFailures.get(state) )				if( failed.containsAll(failures) ) {					add = true;				}			if( add ) {				targets.addAll(state.targets(rule));			}		}		//		System.out.println("!!! Target states from " + this + " given rule " + rule + " and failures " + failed + ": " + targets);				return targets;	}	/** stores a set pairs of rules (second) that must fail before the rule first becomes active (for matching ordering purposes **/	private Set<Pair<Rule, Set<Rule>>> ruleActiveMap = new HashSet<Pair<Rule, Set<Rule>>>();	/** stores rules and the set of rules those rules depends on (w.r.t. applicability/failure) to define the target location **/	private Map<Rule, Set<Rule>> ruleTargetDependencyMap = new HashMap<Rule, Set<Rule>>();	/** 	 * Stores a rule and the set of rules the rule depends on (w.r.t. applicability/failure)	 * to determine the target location when applying the rule 	 * @param rule	 * @param dependencies	 */	private void addRuleTargetDependencies(Rule rule, Set<Rule> dependencies) {		Set<Rule> dep = this.ruleTargetDependencyMap.get(rule);		if (dep == null) {			dep = new HashSet<Rule>();			this.ruleTargetDependencyMap.put(rule, dep);		}		dep.addAll(dependencies);	}	/** Stores rules with no failure dependencies to become active (for matching ordering purposes). **/	private Set<Rule> alwaysActiveRules = new HashSet<Rule>();	/**	 * Called by LocationAutomatonBuilder to initialize this Location ONCE	 */	protected void initialize() {		// store the source states for every rule		for (Pair<ControlState, Set<Rule>> pair : states) {			for (Rule rule : pair.first().rules()) {				addRuleSource(rule, pair.first());			}		}		// get the rules that are enabled from the start		for (Pair<ControlState, Set<Rule>> pair : states) {			if (pair.second().isEmpty()) {				alwaysActiveRules.addAll(pair.first().rules());			}		}		// get all activation failures for rules that have one		// store target dependency for all rules		for (Pair<ControlState, Set<Rule>> pair : states) {			if (pair.second().size() > 0 && !this.alwaysActiveRules.contains(pair.first())) {				Set<Rule> failure = pair.second();				for (Rule rule : pair.first().rules()) {					addRuleTargetDependencies(rule, failure);					// if the rule is not always active then it is active as					// soon as					// this failureset is applicable					if (!this.alwaysActiveRules.contains(rule)) {						ruleActiveMap.add(new Pair<Rule, Set<Rule>>(rule,								failure));					}				}			}		}	}	public LocationCache createCache() {		return null;	}	public Location getTarget(Rule rule, LocationCache cache) {		Set<Rule> failureDependency = this.ruleTargetDependencyMap.get(rule);		Set<Rule> failed;		if (failureDependency != null) {			failed = cache.failed(failureDependency);		} else {			failed = new HashSet<Rule>();		}		return getTargetLocation(rule, failed);	}	/**	 * Returns all rules that have not been explored yet and 	 * with their failure-dependency enabled.	 */	public Set<Rule> moreRules(LocationCache cache) {		//		System.out.println("+++ Requesting more rules from: " + this.toString());//		System.out.println("--- Already matched: " + names(cache.getMatched()));//		System.out.println("--- Already failed: " + names(cache.getFailed()));		Set<Rule> testNext = allowedRules(cache.getMatched(), cache.getFailed());//		System.out.println("--- Allowing: " + names(testNext));		return testNext;	}	/**	 * Returns all rules that have not been explored yet and 	 * with their failure-dependency enabled.	 * @param explored	 * @param failed	 * @return a set of enabled rules that have not yet been explored	 */	private Set<Rule> allowedRules(Set<Rule> matched, Set<Rule> failed) {		HashSet<Rule> result = new HashSet<Rule>();		for (Rule rule : alwaysActiveRules) {			if (!matched.contains(rule) && !failed.contains(rule)) {				result.add(rule);			}		}		for (Pair<Rule, Set<Rule>> pair : ruleActiveMap) {			if (matched.contains(pair.first()) || failed.contains(pair.first())) {				// do nothing, rule is finished			} else {				boolean allFailed = true;				for (Rule rule : pair.second()) {					if (!failed.contains(rule)) {						allFailed = false;					}				}				if (allFailed)					result.add(pair.first());			}		}		return result;	}	/**	 * Returns wether the GraphState is in a success-control state, given that	 * all possible transitions have been added to the graphstate, thus any 	 * rule not found in a transition has thus failed.	 * @param state	 * 	 * FIXME: this method can be optimized if the succes-states or the	 * corresponding failures are in a seperate set	 * 	 * @return true if any of the "enabled" states is a success-state	 */	public boolean isSuccess(GraphState state) {//		assert !state.isClosed() : "isSuccess should only be called when (and before) closing the state";		Set<Rule> rulesFound = new HashSet<Rule>();		for (GraphTransition trans : state.getTransitionSet()) {			rulesFound.add(trans.getEvent().getRule());		}		// more expensive computation, but will only be done once		for (Pair<ControlState, Set<Rule>> pair : states) {			if (pair.first().isSuccess()) {				if (pair.second().size() == 0) {					return true;				}				else {					boolean failureSucceed = true;					for (Rule rule : pair.second()) {						if (rulesFound.contains(rule)) {							failureSucceed = false;						}					}					if (failureSucceed)						return true;				}			}		}		return false;	}	@Override	public String toString() {		String toString = null;		for (Pair<ControlState, Set<Rule>> pair : this.states) {			if (toString == null) {				toString = "";			} else {				toString += ",";			}			toString += failureToString(pair.second());			toString += pair.first().toString();		}				if( toString == null ) {			toString = "";		}				return new Integer(toString.hashCode()).toString();	}	/**	 * Generates a string representation of the contained states and corresponding enabling failures.	 * @param rules	 * @return string representation	 */	private String failureToString(Set<Rule> rules) {		String retval = "";		if (rules.size() != 0) {			retval = "![";			boolean first = true;			for (Rule rule : rules) {				if (!first)					retval += ",";				retval += rule.getName().text();				first = true;			}			retval += "]";		}		return retval;	}		public Set<String> names(Set<Rule> rules) {		Set<String> retval = new HashSet<String>();		for( Rule rule : rules ) {			retval.add(rule.getName().text());		}		return retval;	}}