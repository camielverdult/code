/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.grammar;

import groove.algebra.AlgebraFamily;
import groove.explore.Exploration;
import groove.grammar.model.FormatErrorSet;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.grammar.model.RuleModel;
import groove.util.Groove;
import groove.util.Parser;
import groove.util.PropertyKey;
import groove.util.ThreeValued;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Grammar property keys. */
public enum GrammarKey implements PropertyKey<Object>, GrammarChecker {
    /** Property name for the GROOVE version. */
    GROOVE_VERSION("grooveVersion", true, "The Groove version that created this grammar"),
    /** Property name for the Grammar version. */
    GRAMMAR_VERSION("grammarVersion", true, "The version of this grammar"),
    /** One-line documentation comment on the graph production system. */
    REMARK("remark", "A one-line description of the graph production system"),

    /** Property name for the algebra to be used during simulation. */
    ALGEBRA(
        "algebraFamily",
        "<body>Algebra used for attributes"
            + "<li>- <i>default</i>: java-based values (<tt>int</tt>, <tt>boolean</tt>, <tt>String</tt>, <tt>double</tt>)"
            + "<li>- <i>big</i>: arbitrary-precision values (<tt>BigInteger</tt>, <tt>boolean</tt>, <tt>String</tt>, <tt>BigDecimal</tt>)"
            + "<li>- <i>point</i>: a single value for every type (so all values are equal)"
            + "<li>- <i>term</i>: symbolic term representations",
        new Parser.EnumParser<AlgebraFamily>(AlgebraFamily.class, AlgebraFamily.DEFAULT)),

    /**
     * Flag determining the injectivity of the rule system. If <code>true</code>,
     * all rules should be matched injectively. Default is <code>false</code>.
     */
    INJECTIVE("matchInjective",
        "<body>Flag controlling if all rules should be matched injectively. "
            + "<p>If true, overrules the local rule injectivity property", Parser.boolFalse),

    /**
     * Dangling edge check. If <code>true</code>, all
     * matches that leave dangling edges are invalid. Default is
     * <code>false</code>.
     */
    DANGLING("checkDangling",
        "Flag controlling if dangling edges should be forbidden rather than deleted",
        Parser.boolFalse),

    /**
     * Creator edge check. If <code>true</code>, creator
     * edges are implicitly treated as (individual) NACs. Default is
     * <code>false</code>.
     */
    CREATOR_EDGE("checkCreatorEdges",
        "Flag controlling if creator edges should be treated as implicit NACs", Parser.boolFalse),

    /**
     * RHS-as-NAC property. If <code>true</code>, each RHS
     * is implicitly treated as a NAC. Default is <code>false</code>.
     */
    RHS_AS_NAC("rhsIsNAC", "Flag controlling if RHSs should be treated as implicit NACs",
        Parser.boolFalse),

    /**
     * Isomorphism check. If <code>true</code>, state
     * graphs are compared up to isomorphism; otherwise, they are compared up to
     * equality. Default is <code>true</code>.
     */
    ISOMORPHISM("checkIsomorphism",
        "Flag controlling whether states are checked up to isomorphism", Parser.boolTrue),

    /**
     * Space-separated list of active start graph names.
     */
    START_GRAPH_NAMES("startGraph", "List of active start graph names", Parser.splitter,
        ResourceChecker.get(ResourceKind.HOST)),

    /**
     * Name of the active control program.
     */
    CONTROL_NAMES("controlProgram", "List of enabled control programs", Parser.splitter,
        ResourceChecker.get(ResourceKind.CONTROL)),

    /**
     * Space-separated list of active type graph names.
     */
    TYPE_NAMES("typeGraph", "List of active type graph names", Parser.splitter,
        ResourceChecker.get(ResourceKind.TYPE)),

    /**
     * Space-separated list of active prolog program names.
     */
    PROLOG_NAMES("prolog", "List of active prolog program names", Parser.splitter,
        ResourceChecker.get(ResourceKind.PROLOG)),

    /** Policy for rule application. */
    ACTION_POLICY(
        "actionPolicy",
        "<body>List of <i>key=value</i> pairs, where <i>key</i> is an action name and <i>value</i> is one of:"
            + "<li> - <i>off</i>: the action is disabled (overrules the <b>enabled</b> property)"
            + "<li> - <i>silent</i>: the constraint is checked and flagged on the state as a condition"
            + "<li> - <i>error</i>: applicability is an error"
            + "<li> - <i>remove</i>: applicability causes the state to be removed from the state space"
            + "<p>The last three are only valid for forbidden and invariant properties",
        CheckPolicy.multiParser, ActionPolicyChecker.instance),
    /** Policy for dealing with type violations. */
    TYPE_POLICY(
        "typePolicy",
        "<body>Flag controlling how dynamic type constraints (multiplicities, composites) are dealt with."
            + "<li>- <i>off</i>: dynamic type constraints are not checked"
            + "<li>- <i>error</i>: dynamic type violations are flagged as errors"
            + "<li>- <i>remove</i>: dynamic type violations cause the state to be removed from the state space",
        new Parser.EnumParser<CheckPolicy>(CheckPolicy.class, CheckPolicy.ERROR, "off", null,
            "error", "remove")),

    /** Policy for dealing with deadlocks. */
    DEAD_POLICY("deadlockPolicy", "Flag controlling how deadlocked states are dealt with."
        + "<br>(A state is considered deadlocked if no scheduled transformer is applicable.)"
        + "<li>- <i>off</i>: deadlocks are not checked"
        + "<li>- <i>error</i>: deadlocks are flagged as errors",
        new Parser.EnumParser<CheckPolicy>(CheckPolicy.class, CheckPolicy.OFF, "off", null,
            "error", null)),

    /**
     * Exploration strategy description.
     */
    EXPLORATION("explorationStrategy", "Default exploration strategy for this grammar",
        Exploration.parser(), ExplorationChecker.instance),

    /**
     * Space-separated list of control labels of a graph grammar. The
     * control labels are those labels which should be matched first for optimal
     * performance, presumably because they occur infrequently or indicate a
     * place where rules are likely to be applicable.
     */
    CONTROL_LABELS("controlLabels", "List of rare labels, used to optimise rule matching",
        Parser.splitter),

    /**
     * Space-separated list of common labels of a graph grammar. The
     * control labels are those labels which should be matched last for optimal
     * performance, presumably because they occur frequently.
     */
    COMMON_LABELS("commonLabels", "List of frequent labels, used to optimise rule matching",
        Parser.splitter),

    /**
     * Space-separated list of abstraction node labels of a graph grammar.
     * These labels are used to define the level zero neighbourhood relation
     * between nodes.
     */
    ABSTRACTION_LABELS("abstractionLabels",
        "List of node labels, used by neighbourhood abstraction", Parser.splitter),

    /**
     * Flag that determines if transition parameters are included in the LTS
     * transition labels
     */
    TRANSITION_PARAMETERS("transitionParameters", false, "Show parameters",
        "Flag controlling if transition labels should include rule parameters",
        new Parser.EnumParser<ThreeValued>(ThreeValued.class, ThreeValued.SOME), null),

    /**
     * Flag that determines if (binary) loops can be shown as vertex labels.
     */
    LOOPS_AS_LABELS("loopsAsLabels",
        "Flag controlling if binary self-edges may be shown as vertex labels", Parser.boolTrue), ;

    /**
     * Constructor for a key with a plain string value
     * @param name name of the key; should be an identifier possibly prefixed by #SYSTEM_KEY_PREFIX
     * @param explanation short explanation of the meaning of the key
     */
    private GrammarKey(String name, String explanation) {
        this(name, false, null, explanation, null, null);
    }

    /**
     * Constructor for a key with a plain string value
     * @param name name of the key; should be an identifier possibly prefixed by #SYSTEM_KEY_PREFIX
     * @param explanation short explanation of the meaning of the key
     */
    private GrammarKey(String name, boolean system, String explanation) {
        this(name, system, null, explanation, null, null);
    }

    /**
     * Constructor for a key with values parsed by a given parser
     * @param name name of the key; should be an identifier possibly prefixed by #SYSTEM_KEY_PREFIX
     * @param explanation short explanation of the meaning of the key
     * @param parser the parser used to convert key values to string representations and back; if {@code null},
     * {@link Parser#identity} is used
     */
    private GrammarKey(String name, String explanation, Parser<?> parser) {
        this(name, false, null, explanation, parser, null);
    }

    /**
     * Constructor for a key with values parsed by a given parser
     * @param name name of the key; should be an identifier possibly prefixed by #SYSTEM_KEY_PREFIX
     * @param explanation short explanation of the meaning of the key
     * @param parser the parser used to convert key values to string representations and back; if {@code null},
     * {@link Parser#identity} is used
     * @param checker the checker used to test compatibility with a given grammar model; if {@code null},
     * {@code this} is used
     */
    private GrammarKey(String name, String explanation, Parser<?> parser, GrammarChecker checker) {
        this(name, false, null, explanation, parser, checker);
    }

    /**
     * Constructor for a key with a plain string value
     * @param name name of the key; should be an identifier possibly prefixed by #SYSTEM_KEY_PREFIX
     * @param system flag indicating this is a system key
     * @param keyPhrase user-readable version of the name; if {@code null},
     * the key phrase is constructed from {@code name}
     * @param explanation short explanation of the meaning of the key
     * @param parser the parser used to convert key values to string representations and back; if {@code null},
     * {@link Parser#identity} is used
     * @param checker the checker used to test compatibility with a given grammar model; if {@code null},
     * {@code this} is used
     */
    private GrammarKey(String name, boolean system, String keyPhrase, String explanation,
        Parser<?> parser, GrammarChecker checker) {
        this.name = name;
        this.system = system;
        this.keyPhrase = keyPhrase == null ? Groove.unCamel(name, false) : keyPhrase;
        this.explanation = explanation;
        this.parser = parser == null ? Parser.identity : parser;
        this.checker = checker;
    }

    @Override
    public String getName() {
        return this.name;
    }

    private final String name;

    @Override
    public String getExplanation() {
        return this.explanation;
    }

    private final String explanation;

    @Override
    public boolean isSystem() {
        return this.system;
    }

    private final boolean system;

    @Override
    public String getKeyPhrase() {
        return this.keyPhrase;
    }

    private final String keyPhrase;

    @Override
    public Parser<?> parser() {
        return this.parser;
    }

    private final Parser<?> parser;

    @Override
    public Object getDefaultValue() {
        return parser().getDefaultValue();
    }

    @Override
    public boolean isValue(Object value) {
        return parser().isValue(value);
    }

    @Override
    public FormatErrorSet check(GrammarModel grammar, Object value) {
        return this.checker == null ? new FormatErrorSet() : this.checker.check(grammar, value);
    }

    private final GrammarChecker checker;

    /** Returns the key with a given name, if any. */
    public static GrammarKey getKey(String name) {
        return keyMap.get(name);
    }

    /**
     * List of system-defined keys, in the order in which they are to appear in
     * a properties editor.
     */
    static private final Map<String,GrammarKey> keyMap;

    static {
        Map<String,GrammarKey> defaultKeys = new LinkedHashMap<String,GrammarKey>();
        for (GrammarKey key : GrammarKey.values()) {
            defaultKeys.put(key.getName(), key);
        }
        keyMap = Collections.unmodifiableMap(defaultKeys);
    }

    /** Checks whether a value is a list of names of a given resource kind. */
    private static class ResourceChecker implements GrammarChecker {
        ResourceChecker(ResourceKind kind) {
            this.kind = kind;
        }

        /** Returns the resource kind being checked. */
        public ResourceKind getKind() {
            return this.kind;
        }

        private final ResourceKind kind;

        @Override
        public FormatErrorSet check(GrammarModel grammar, Object value) {
            List<Object> unknowns = new ArrayList<Object>((Collection<?>) value);
            FormatErrorSet result = new FormatErrorSet();
            unknowns.removeAll(grammar.getResourceMap(getKind()).keySet());
            if (!unknowns.isEmpty()) {
                result.add("Unknown %s name%s %s", Groove.convertCase(getKind().getName(), false),
                    unknowns.size() == 1 ? "" : "s",
                    Groove.toString(unknowns.toArray(), "'", "'", "', '", "' and '"));
            }
            return result;
        }

        /** Returns the singleton checker for a given resource kind. */
        public static ResourceChecker get(ResourceKind kind) {
            return resourceMap.get(kind);
        }

        private static final Map<ResourceKind,ResourceChecker> resourceMap;

        static {
            Map<ResourceKind,ResourceChecker> map =
                new EnumMap<ResourceKind,GrammarKey.ResourceChecker>(ResourceKind.class);
            for (ResourceKind kind : ResourceKind.values()) {
                switch (kind) {
                case CONTROL:
                case HOST:
                case PROLOG:
                case TYPE:
                    map.put(kind, new ResourceChecker(kind));
                }
            }
            resourceMap = map;
        }

    }

    /** Checks the value for the {@link GrammarKey#ACTION_POLICY} key. */
    private static class ActionPolicyChecker extends ResourceChecker {
        public ActionPolicyChecker() {
            super(ResourceKind.RULE);
        }

        @Override
        public FormatErrorSet check(GrammarModel grammar, Object value) {
            FormatErrorSet result = new FormatErrorSet();
            List<String> unknowns = new ArrayList<String>();
            CheckPolicy.PolicyMap map = (CheckPolicy.PolicyMap) value;
            if (map == null) {
                result.add("Invalid entry");
            } else {
                for (Map.Entry<String,CheckPolicy> entry : map.entrySet()) {
                    String name = entry.getKey();
                    RuleModel rule = grammar.getRuleModel(name);
                    if (rule == null) {
                        unknowns.add(name);
                    } else {
                        CheckPolicy policy = entry.getValue();
                        if (!policy.isFor(rule.getRole())) {
                            result.add("Policy '%s' is unsuitable for %s '%s'", policy.getName(),
                                rule.getRole(), rule.getFullName());
                        }
                    }
                }
                if (!unknowns.isEmpty()) {
                    result.add("Unknown %s name%s %s", Groove.convertCase(getKind().getName(),
                        false), unknowns.size() == 1 ? "" : "s", Groove.toString(
                        unknowns.toArray(), "'", "'", "', '", "' and '"));
                }
            }
            return result;
        }

        public static ActionPolicyChecker instance = new ActionPolicyChecker();
    }

    /** Checks the value for the {@link GrammarKey#EXPLORATION} key. */
    private static class ExplorationChecker implements GrammarChecker {
        @Override
        public FormatErrorSet check(GrammarModel grammar, Object value) {
            FormatErrorSet result = new FormatErrorSet();
            return result;
        }

        public static ExplorationChecker instance = new ExplorationChecker();
    }
}