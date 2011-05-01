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
package groove.view.aspect;

import static groove.view.aspect.AspectParser.ASSIGN;
import static groove.view.aspect.AspectParser.SEPARATOR;
import groove.algebra.Algebras;
import groove.algebra.Constant;
import groove.algebra.Operator;
import groove.graph.GraphRole;
import groove.io.HTMLConverter;
import groove.util.Colors;
import groove.util.Duo;
import groove.util.Pair;
import groove.view.FormatException;

import java.awt.Color;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Distinguishes the aspects that can be found in a plain graph representation
 * of a rule, host graph or type graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum AspectKind {
    /** Default aspect, if none is specified. */
    NONE("none") {
        @Override
        public String getPrefix() {
            return "";
        }
    },
    /** Used for comments/documentation. */
    REMARK("rem"),

    // rule roles
    /** Indicates an unmodified element. */
    READER("use", ContentKind.LEVEL),
    /** Indicates an element to be deleted. */
    ERASER("del", ContentKind.LEVEL),
    /** Indicates an element to be created. */
    CREATOR("new", ContentKind.LEVEL),
    /** Indicates an element to be created if not yet present. */
    ADDER("cnew", ContentKind.LEVEL),
    /** Indicates a forbidden element. */
    EMBARGO("not", ContentKind.LEVEL),
    /** Connects two embargo sub-graphs. */
    CONNECT("or", ContentKind.EMPTY),

    // data types
    /** Indicates a data value of unknown type. */
    UNTYPED("attr"),
    /** Indicates a boolean value or operator. */
    BOOL("bool", ContentKind.BOOL_LITERAL),
    /** Indicates an integer value or operator. */
    INT("int", ContentKind.INT_LITERAL),
    /** Indicates a floating-point value or operator. */
    REAL("real", ContentKind.REAL_LITERAL),
    /** Indicates a string value or operator. */
    STRING("string", ContentKind.STRING_LITERAL),

    // auxiliary attribute-related aspects
    /** Indicates an argument edge. */
    ARGUMENT("arg", ContentKind.NUMBER),
    /** Indicates a product node. */
    PRODUCT("prod"),
    /** Indicates an attribute value. */
    PRED("pred", ContentKind.PRED_VAL),
    /** Indicates an attribute operation. */
    LET("let", ContentKind.LET_EXPR),

    // rule parameters
    /** Indicates a bidirectional rule parameter. */
    PARAM_BI("par", ContentKind.PARAM),
    /** Indicates an input rule parameter. */
    PARAM_IN("parin", ContentKind.PARAM),
    /** Indicates an output rule parameter. */
    PARAM_OUT("parout", ContentKind.PARAM),

    // type-related aspects
    /** Indicates an abstract type. */
    ABSTRACT("abs"),
    /** Indicates a subtype relation. */
    SUBTYPE("sub"),
    /** Indicates an incoming multiplicity. */
    MULT_IN("in", ContentKind.MULTIPLICITY),
    /** Indicates an outgoing multiplicity. */
    MULT_OUT("out", ContentKind.MULTIPLICITY),
    /** Indicates an outgoing multiplicity. */
    COMPOSITE("part"),

    // label-related aspects
    /** Indicates that the remainder of the label is a regular expression. */
    PATH("path"),
    /** Indicates that the remainder of the label is to be taken as literal text. */
    LITERAL(""),

    // quantifier-related aspects
    /** Universal quantifier. */
    FORALL("forall", ContentKind.LEVEL),
    /** Non-vacuous universal quantifier. */
    FORALL_POS("forallx", ContentKind.LEVEL),
    /** Existential quantifier. */
    EXISTS("exists", ContentKind.LEVEL),
    /** Optional existential quantifier. */
    EXISTS_OPT("existsx", ContentKind.LEVEL),
    /** Nesting edge. */
    NESTED("nested", ContentKind.NESTED),
    /** Node identity. */
    ID("id", ContentKind.NAME),
    /** Node type colour. */
    COLOR("color", ContentKind.COLOR);

    /** Creates a new aspect kind, without content. */
    private AspectKind(String name) {
        this(name, ContentKind.NONE);
    }

    /** Creates a new aspect kind, with content of a given type. */
    private AspectKind(String name, ContentKind contentKind) {
        this.name = name;
        this.contentKind = contentKind;
    }

    @Override
    public String toString() {
        return getName();
    }

    /** Returns the name of this aspect kind. */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the prefix of this aspect kind,
     * i.e., the text (including separator) by which a plain text
     * label is recognised to have this aspect.
     */
    public String getPrefix() {
        return getName() + SEPARATOR;
    }

    /** Returns a HTML-formatted string describing the syntax of this aspect. */
    public String getSyntax() {
        StringBuilder result = new StringBuilder();
        result.append(HTMLConverter.STRONG_TAG.on(" " + getName()));
        result.append(getContentKind().preSyntax());
        result.append(HTMLConverter.STRONG_TAG.on(SEPARATOR));
        result.append(getContentKind().postSyntax());
        return result.toString();
    }

    /** 
     * Returns type of content of this aspect kind.
     * May be {@code null}, if no content kind is allowed.
     */
    public ContentKind getContentKind() {
        return this.contentKind;
    }

    /** Returns a (prototypical) aspect of this kind. */
    public Aspect getAspect() {
        if (this.aspect == null) {
            this.aspect = new Aspect(this, this.contentKind);
        }
        return this.aspect;
    }

    /** 
     * Parses a given string into an aspect of this kind, and the remainder.
     * The string is guaranteed to start with the name of this aspect, and
     * to contain a separator.
     * @param input the string to be parsed
     * @return a pair consisting of the resulting aspect and the remainder of
     * the input string, starting from the character after the first occurrence 
     * of #SEPARATOR onwards.
     * @throws FormatException if the string does not have content of the
     * correct kind
     */
    public Pair<Aspect,String> parseAspect(String input) throws FormatException {
        assert input.startsWith(getName()) && input.indexOf(SEPARATOR) >= 0;
        // give the text to the content kind to parse
        Pair<Object,String> result =
            getContentKind().parse(input, getName().length());
        return new Pair<Aspect,String>(new Aspect(this, getContentKind(),
            result.one()), result.two());
    }

    /** 
     * Indicates if this aspect is among the set of roles.
     * @see #roles 
     */
    public boolean isRole() {
        return roles.contains(this);
    }

    /** 
     * Indicates if this aspect is among the set of NAC (non-LHS) elements.
     * @see #nac 
     */
    public boolean inNAC() {
        return nac.contains(this);
    }

    /** 
     * Indicates if this aspect is among the set of LHS element.
     * @see #lhs 
     */
    public boolean inLHS() {
        return lhs.contains(this);
    }

    /** 
     * Indicates if this aspect is among the set of RHS elements.
     * @see #rhs 
     */
    public boolean inRHS() {
        return rhs.contains(this);
    }

    /** 
     * Indicates if this element is in the LHS but not the RHS. 
     * Convenience method for {@code inLHS() && !inRHS()}.
     */
    public boolean isEraser() {
        return inLHS() && !inRHS();
    }

    /** 
     * Indicates if this element is in the RHS but not the LHS. 
     * Convenience method for {@code inRHS() && !inLHS()}.
     */
    public boolean isCreator() {
        return inRHS() && !inLHS();
    }

    /** 
     * Indicates if this aspect is among the set of typed data aspects.
     * @see #data 
     */
    public boolean isTypedData() {
        return isData() && this != UNTYPED;
    }

    /** 
     * Indicates if this aspect is among the set of (typed or untyped) data aspects.
     * @see #data 
     */
    public boolean isData() {
        return data.contains(this);
    }

    /** 
     * Indicates if this aspect is among the set of meta-aspects.
     * @see #meta 
     */
    public boolean isMeta() {
        return meta.contains(this);
    }

    /** 
     * Indicates if this aspect is among the set of parameter aspects.
     * @see #params 
     */
    public boolean isParam() {
        return params.contains(this);
    }

    /** 
     * Indicates if this aspect is among the set of quantifiers.
     * @see #existsQuantifiers 
     */
    public boolean isExists() {
        return existsQuantifiers.contains(this);
    }

    /** 
     * Indicates if this aspect is among the set of quantifiers.
     * @see #forallQuantifiers 
     */
    public boolean isForall() {
        return forallQuantifiers.contains(this);
    }

    /** 
     * Indicates if this aspect is among the set of quantifiers.
     */
    public boolean isQuantifier() {
        return isExists() || isForall();
    }

    /** 
     * Indicates if this aspect is attribute related.
     * @see #attributers 
     */
    public boolean isAttrKind() {
        return attributers.contains(this);
    }

    /** Indicates that this aspect kind is always the last on a label. */
    public boolean isLast() {
        return this.contentKind != ContentKind.LEVEL;
    }

    private final ContentKind contentKind;
    private final String name;
    private Aspect aspect;

    /** 
     * Returns the aspect kind corresponding to a certain non-{@code null}
     * name, or {@code null} if there is no such aspect kind.
     */
    public static AspectKind getKind(String name) {
        return kindMap.get(name);
    }

    /** 
     * Returns the aspect kind corresponding to a certain non-{@code null}
     * name, or {@code null} if there is no such aspect kind.
     */
    public static NestedValue getNestedValue(String name) {
        return nestedValueMap.get(name);
    }

    /** Static mapping from all aspect names to aspects. */
    private static final Map<String,AspectKind> kindMap =
        new HashMap<String,AspectKind>();
    /** Static mapping from nested value texts to values. */
    private static final Map<String,NestedValue> nestedValueMap =
        new HashMap<String,NestedValue>();

    static {
        // initialise the aspect kind map
        for (AspectKind kind : EnumSet.allOf(AspectKind.class)) {
            AspectKind oldKind = kindMap.put(kind.toString(), kind);
            assert oldKind == null;
        }
        // initialise the nested value map
        for (NestedValue value : EnumSet.allOf(NestedValue.class)) {
            NestedValue oldValue = nestedValueMap.put(value.toString(), value);
            assert oldValue == null;
        }
    }

    /** Set of role aspects. */
    public static final Set<AspectKind> roles = EnumSet.of(ERASER, ADDER,
        CREATOR, READER, EMBARGO, CONNECT);
    /** Set of role aspects appearing (only) in NACs. */
    public static final Set<AspectKind> nac = EnumSet.of(EMBARGO, ADDER,
        CONNECT);
    /** Set of role aspects appearing in LHSs. */
    public static final Set<AspectKind> lhs = EnumSet.of(READER, ERASER);
    /** Set of role aspects appearing in RHSs. */
    public static final Set<AspectKind> rhs =
        EnumSet.of(READER, CREATOR, ADDER);
    /** Set of data aspects, typed or untyped. */
    public static final Set<AspectKind> data = EnumSet.of(UNTYPED, STRING,
        BOOL, INT, REAL);
    /** Set of meta-aspects, i.e., which do not reflect real graph structure. */
    public static final Set<AspectKind> meta = EnumSet.of(FORALL, FORALL_POS,
        EXISTS, EXISTS_OPT, NESTED, REMARK, CONNECT);
    /** Set of parameter aspects. */
    public static final Set<AspectKind> params = EnumSet.of(PARAM_BI, PARAM_IN,
        PARAM_OUT);
    /** Set of existential quantifier aspects, i.e., which do not reflect real graph structure. */
    public static final Set<AspectKind> existsQuantifiers = EnumSet.of(EXISTS,
        EXISTS_OPT);
    /** Set of universal quantifier aspects, i.e., which do not reflect real graph structure. */
    public static final Set<AspectKind> forallQuantifiers = EnumSet.of(FORALL,
        FORALL_POS);
    /** Set of attribute-related aspects. */
    public static final Set<AspectKind> attributers = EnumSet.of(PRODUCT,
        ARGUMENT, UNTYPED, STRING, INT, BOOL, REAL, PRED);

    /** Mapping from graph roles to the node aspects allowed therein. */
    public static final Map<GraphRole,Set<AspectKind>> allowedNodeKinds =
        new EnumMap<GraphRole,Set<AspectKind>>(GraphRole.class);
    /** Mapping from graph roles to the edge aspects allowed therein. */
    public static final Map<GraphRole,Set<AspectKind>> allowedEdgeKinds =
        new EnumMap<GraphRole,Set<AspectKind>>(GraphRole.class);

    static {
        for (GraphRole role : EnumSet.allOf(GraphRole.class)) {
            switch (role) {
            case HOST:
                allowedNodeKinds.put(role,
                    EnumSet.of(NONE, REMARK, INT, BOOL, REAL, STRING));
                allowedEdgeKinds.put(role,
                    EnumSet.of(NONE, REMARK, LITERAL, PRED));
                break;
            case RULE:
                allowedNodeKinds.put(role, EnumSet.of(REMARK, READER, ERASER,
                    CREATOR, ADDER, EMBARGO, UNTYPED, BOOL, INT, REAL, STRING,
                    PRODUCT, PARAM_BI, PARAM_IN, PARAM_OUT, FORALL, FORALL_POS,
                    EXISTS, EXISTS_OPT, ID));
                allowedEdgeKinds.put(role, EnumSet.of(REMARK, READER, ERASER,
                    CREATOR, ADDER, EMBARGO, CONNECT, BOOL, INT, REAL, STRING,
                    ARGUMENT, PATH, LITERAL, FORALL, FORALL_POS, EXISTS,
                    EXISTS_OPT, NESTED));
                break;
            case TYPE:
                allowedNodeKinds.put(role, EnumSet.of(NONE, REMARK, INT, BOOL,
                    REAL, STRING, ABSTRACT, COLOR));
                allowedEdgeKinds.put(role, EnumSet.of(NONE, REMARK, ABSTRACT,
                    SUBTYPE, MULT_IN, MULT_OUT, COMPOSITE));
                break;
            default:
                assert !role.inGrammar();
                allowedNodeKinds.put(role, EnumSet.noneOf(AspectKind.class));
                allowedEdgeKinds.put(role, EnumSet.noneOf(AspectKind.class));
            }
        }
    }

    /** Type of content that can be wrapped inside an aspect. */
    static public enum ContentKind {
        /** No content. The label text is not checked. */
        NONE("", "") {
            @Override
            Pair<Object,String> parse(String text, int pos)
                throws FormatException {
                if (text.charAt(pos) != SEPARATOR) {
                    throw new FormatException("Suffix '%s' not allowed",
                        text.substring(pos, text.indexOf(SEPARATOR)));
                }
                return new Pair<Object,String>(null, text.substring(pos + 1));
            }

            @Override
            Object parseContent(String text) {
                // there is no content, so this method should never be called
                throw new UnsupportedOperationException();
            }
        },
        /** Empty content: no text may precede or follow the separator. */
        EMPTY("", "") {
            @Override
            Pair<Object,String> parse(String text, int pos)
                throws FormatException {
                if (text.charAt(pos) != SEPARATOR) {
                    throw new FormatException("Suffix '%s' not allowed",
                        text.substring(pos, text.indexOf(SEPARATOR)));
                }
                if (pos < text.length() - 1) {
                    throw new FormatException("Label text '%s' not allowed",
                        text.substring(pos + 1));
                }
                return new Pair<Object,String>(null, text.substring(pos + 1));
            }

            @Override
            Object parseContent(String text) {
                // there is no content, so this method should never be called
                throw new UnsupportedOperationException();
            }
        },
        /** Quantifier level name. */
        LEVEL("=<i>level</i>", "") {
            @Override
            Pair<Object,String> parse(String text, int pos)
                throws FormatException {
                String content = null;
                int end = text.indexOf(SEPARATOR);
                assert end >= 0;
                if (end > pos) {
                    content = parseContent(text.substring(pos + 1, end));
                }
                return new Pair<Object,String>(content, text.substring(end + 1));
            }

            @Override
            String parseContent(String text) throws FormatException {
                for (int i = 0; i < text.length(); i++) {
                    char c = text.charAt(i);
                    if (i == 0 ? !isValidFirstChar(c) : !isValidNextChar(c)) {
                        throw new FormatException(
                            "Invalid quantification level");
                    }
                }
                return text;
            }

        },
        /** 
         * String constant, used in a typed value aspect. 
         */
        STRING_LITERAL("string"),
        /** 
         * Boolean constant, used in a typed value aspect. 
         */
        BOOL_LITERAL("bool"),
        /**
         * Integer number constant, used in a typed value aspect. 
         */
        INT_LITERAL("int"),
        /** 
         * Real number constant, used in a typed value aspect. 
         */
        REAL_LITERAL("real"),
        /**
         * Multiplicity: either a single number,
         * or of the form {@code n..m} where {@code n<m} or {@code m=*}.
         */
        MULTIPLICITY("", "<i>low</i>..<b><i>high</i></b>") {
            @Override
            Pair<Object,String> parse(String text, int pos)
                throws FormatException {
                int end = text.indexOf(SEPARATOR, pos);
                assert end >= 0;
                if (end == pos) {
                    throw new FormatException("Malformed multiplicity");
                }
                Duo<Integer> content =
                    parseContent(text.substring(pos + 1, end));
                return new Pair<Object,String>(content, text.substring(end + 1));
            }

            @Override
            Duo<Integer> parseContent(String text) throws FormatException {
                int dotdot = text.indexOf(MULT_SEPARATOR);
                int lower, upper;
                try {
                    if (dotdot < 0) {
                        // the multiplicity is a single value
                        lower = upper = Integer.parseInt(text);
                    } else {
                        lower = Integer.parseInt(text.substring(0, dotdot));
                        String upperText =
                            text.substring(dotdot + MULT_SEPARATOR.length());
                        if (upperText.equals("*")) {
                            upper = Integer.MAX_VALUE;
                        } else {
                            upper = Integer.parseInt(upperText);
                        }
                    }
                } catch (NumberFormatException e) {
                    throw new FormatException(
                        "Malformed multiplicity value %s", text);
                }
                if (lower < 0) {
                    throw new FormatException("Negative lower bound %d", lower);
                } else if (lower > upper) {
                    throw new FormatException(
                        "Lower bound %d larger than upper bound %d", lower,
                        upper);
                }
                return new Duo<Integer>(lower, upper);
            }

            @Override
            String toString(Object content) {
                String result;
                @SuppressWarnings("unchecked")
                Duo<Integer> mult = (Duo<Integer>) content;
                int lower = mult.one();
                int upper = mult.two();
                if (lower == upper) {
                    result = "" + lower;
                } else {
                    boolean unbounded = upper == Integer.MAX_VALUE;
                    result =
                        "" + lower + MULT_SEPARATOR
                            + (unbounded ? MULT_UNBOUNDED : upper);
                }
                return result;
            }
        },
        /** 
         * Parameter number, starting with a dollar sign.
         * The content is a non-negative value of type {@link Integer}. 
         */
        PARAM("", "<i>nr</i>") {
            @Override
            Pair<Object,String> parse(String text, int pos)
                throws FormatException {
                assert text.indexOf(SEPARATOR) >= 0;
                // either the prefix is of the form par=$N: or par:M
                // in the first case, the parameter number is N-1
                String nrText;
                int subtract;
                FormatException nrFormatExc =
                    new FormatException("Invalid parameter number");
                switch (text.charAt(pos)) {
                case SEPARATOR:
                    nrText = text.substring(pos + 1);
                    subtract = 0;
                    break;
                case ASSIGN:
                    if (text.charAt(pos + 1) != PARAM_START_CHAR) {
                        throw new FormatException(
                            "Parameter number should start with '%s'", ""
                                + PARAM_START_CHAR);
                    }
                    if (text.charAt(text.length() - 1) != SEPARATOR) {
                        throw new FormatException(
                            "Parameter line should end with '%s'", ""
                                + SEPARATOR);
                    }
                    nrText = text.substring(pos + 2, text.length() - 1);
                    if (nrText.length() == 0) {
                        throw nrFormatExc;
                    }
                    // the new numbering scheme starts with 0 rather than 1;
                    // the old parameter syntax is normalised to this scheme.
                    subtract = 1;
                    break;
                default:
                    throw new FormatException("Can't parse parameter");
                }
                Integer content = null;
                if (nrText.length() > 0) {
                    content = parseContent(nrText) - subtract;
                    if (content < 0) {
                        if (content + subtract == 0) {
                            // special case: par=$0: was an alternative to
                            // par: to specify a hidden parameter
                            content = null;
                        } else {
                            throw nrFormatExc;
                        }
                    }
                }
                return new Pair<Object,String>(content, "");
            }

            @Override
            Integer parseContent(String text) throws FormatException {
                try {
                    return Integer.parseInt(text);
                } catch (NumberFormatException exc) {
                    throw new FormatException("Invalid parameter number %s",
                        text);
                }
            }
        },
        /** 
         * Argument number.
         * The content is a non-negative value of type {@link Integer}. 
         */
        NUMBER("", "<b><i>nr</i></b>") {
            @Override
            Pair<Object,String> parse(String text, int pos)
                throws FormatException {
                assert text.indexOf(SEPARATOR) >= 0;
                if (text.charAt(pos) != SEPARATOR) {
                    throw new FormatException("Can't parse argument");
                }
                String nrText = text.substring(pos + 1);
                return new Pair<Object,String>(parseContent(nrText), "");
            }

            @Override
            Integer parseContent(String text) throws FormatException {
                int result;
                FormatException formatExc =
                    new FormatException("Invalid argument number %s", text);
                try {
                    result = Integer.parseInt(text);
                } catch (NumberFormatException exc) {
                    throw formatExc;
                }
                if (result < 0) {
                    throw formatExc;
                }
                return result;
            }
        },
        /** Content must be a {@link NestedValue}. */
        NESTED("", "in|at|count") {
            @Override
            Pair<Object,String> parse(String text, int pos)
                throws FormatException {
                if (text.charAt(pos) != SEPARATOR) {
                    throw new FormatException("Can't parse quantifier nesting");
                }
                return new Pair<Object,String>(
                    parseContent(text.substring(pos + 1)), "");
            }

            @Override
            NestedValue parseContent(String text) throws FormatException {
                NestedValue content = getNestedValue(text);
                if (content == null) {
                    throw new FormatException("Can't parse quantifier nesting");
                }
                return content;
            }
        },
        /** Colour name or RGB value. */
        COLOR("", "<b><i>rgb</i></b>|<b><i>name</i></b>") {
            @Override
            Pair<Object,String> parse(String text, int pos)
                throws FormatException {
                if (text.charAt(pos) != SEPARATOR) {
                    throw new FormatException("Can't parse colour value");
                }
                return new Pair<Object,String>(
                    parseContent(text.substring(pos + 1)), "");
            }

            @Override
            Color parseContent(String text) throws FormatException {
                Color result = Colors.findColor(text);
                if (result == null) {
                    throw new FormatException("Can't parse '%s' as colour",
                        text);
                }
                return result;
            }
        },
        /** Node identifier. */
        NAME("", "<b><i>name</i></b>") {
            @Override
            Pair<Object,String> parse(String text, int pos)
                throws FormatException {
                if (text.charAt(pos) != SEPARATOR) {
                    throw new FormatException("Can't parse node name");
                }
                return new Pair<Object,String>(
                    parseContent(text.substring(pos + 1)), "");
            }

            @Override
            String parseContent(String text) throws FormatException {
                boolean reserved = text.length() > 1;
                for (int i = 0; i < text.length(); i++) {
                    char c = text.charAt(i);
                    if (i == 0 ? !isValidFirstChar(c) : !isValidNextChar(c)) {
                        throw new FormatException("Invalid node name '%s'",
                            text);
                    }
                    reserved &=
                        i == 0 ? Character.isLetter(c) : Character.isDigit(c);
                }
                if (reserved) {
                    throw new FormatException(
                        "Reserved node name '%s' (letter digit+)", text);
                }
                return text;
            }
        },
        /** Predicate (attribute) value. */
        PRED_VAL("", "<b><i>constraint</i></b>") {
            @Override
            Pair<Object,String> parse(String text, int pos)
                throws FormatException {
                if (text.charAt(pos) != SEPARATOR) {
                    throw new FormatException("Can't parse attribute predicate");
                }
                return new Pair<Object,String>(
                    parseContent(text.substring(pos + 1)), "");
            }

            @Override
            Predicate parseContent(String text) throws FormatException {
                String excPrefix =
                    "Invalid attribute predicate '" + text + "', ";
                String parts[] = text.split(ASSIGN + "");
                if (parts.length != 2) {
                    throw new FormatException(excPrefix
                        + "char '%c' not present", ASSIGN);
                }
                String attrName = parts[0].trim();
                String typedVal = parts[1].trim();
                Constant constant;
                parts = typedVal.split(SEPARATOR + "");
                if (parts.length != 2) {
                    throw new FormatException(excPrefix
                        + "char '%c' not present", SEPARATOR);
                } else {
                    String signature = parts[0].trim();
                    String value = parts[1].trim();
                    if (signature.equals(BOOL_LITERAL.signature)) {
                        constant = (Constant) BOOL_LITERAL.parseContent(value);
                    } else if (signature.equals(INT_LITERAL.signature)) {
                        constant = (Constant) INT_LITERAL.parseContent(value);
                    } else if (signature.equals(REAL_LITERAL.signature)) {
                        constant = (Constant) REAL_LITERAL.parseContent(value);
                    } else if (signature.equals(STRING_LITERAL.signature)) {
                        constant =
                            (Constant) STRING_LITERAL.parseContent(value);
                    } else {
                        throw new FormatException(excPrefix
                            + "unknown data signature '%s'", signature);
                    }
                }
                return new Predicate(attrName, constant);
            }

            @Override
            String toString(Object content) {
                return ((Predicate) content).toString();
            }
        },
        /** Let expression content. */
        LET_EXPR("", "<b><i>name</i>=<i>val</i></b>") {
            @Override
            Pair<Object,String> parse(String text, int pos)
                throws FormatException {
                if (text.charAt(pos) != SEPARATOR) {
                    throw new FormatException("Can't parse let expression");
                }
                return new Pair<Object,String>(
                    parseContent(text.substring(pos + 1)), "");
            }

            @Override
            String parseContent(String text) throws FormatException {
                // EDUARDO: Implement this.
                return text;
            }
        };

        /** Default, empty constructor. */
        private ContentKind(String preSyntax, String postSyntax) {
            this.signature = null;
            this.preSyntax = preSyntax;
            this.postSyntax = postSyntax;
        }

        /** Constructor for literals of a given signature. */
        private ContentKind(String signature) {
            this.signature = signature;
            this.preSyntax = "";
            this.postSyntax = "<i>const</i>|<i>op</i>";
        }

        /** 
         * Tries to parse a given string, from a given position, as content 
         * of this kind.
         * @return a pair consisting of the resulting content value (which
         * may be {@code null} if there is, correctly, no content) and
         * the remainder of the input string
         * @throws FormatException if the input string cannot be parsed 
         */
        Pair<Object,String> parse(String text, int pos) throws FormatException {
            // this implementation tries to find a literal of the
            // correct signature, or no content if the signature is not set
            assert text.indexOf(SEPARATOR, pos) >= 0;
            if (text.charAt(pos) != SEPARATOR) {
                throw new FormatException(
                    "Prefix %s should be followed by '%s' in %s",
                    text.substring(0, pos), "" + SEPARATOR, text);
            }
            if (this.signature == null || pos == text.length() - 1) {
                return new Pair<Object,String>(null, text.substring(pos + 1));
            } else {
                // the rest of the label should be a constant or operator
                // of the signature
                String value = text.substring(pos + 1);
                return new Pair<Object,String>(parseContent(value), "");
            }
        }

        /** 
         * Tries to parse a given string as content of the correct kind. 
         * @return the resulting content value 
         */
        Object parseContent(String text) throws FormatException {
            // This implementation tries to parse the text as a constant of the 
            // given signature.
            if (this.signature == null) {
                throw new UnsupportedOperationException("No content allowed");
            }
            Object content = Algebras.getConstant(this.signature, text);
            if (content == null) {
                content = Algebras.getOperator(this.signature, text);
            }
            if (content == null) {
                throw new FormatException(
                    "Signature '%s' has no constant or operator %s",
                    this.signature, text);
            }
            return content;
        }

        /** 
         * Builds a string description of a given content object, in a form that
         * can be parsed back to the content by {@link #parseContent(String)}. 
         * @return a string description of a content object, or the empty
         * string if the object is {@code null}
         */
        String toString(Object content) {
            if (content == null) {
                return "";
            } else if (content instanceof Constant) {
                return ((Constant) content).getSymbol();
            } else if (content instanceof Operator) {
                return ((Operator) content).getName();
            } else if (content instanceof Color) {
                Color color = (Color) content;
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();
                int alpha = color.getAlpha();
                String colorString = alpha == 255 ? "%s,%s,%s" : "%s,%s,%s,%s";
                return String.format(colorString, red, green, blue, alpha);
            } else {
                return "" + content;
            }
        }

        /**
         * Builds a string description of a given aspect kind and content
         * of this {@link ContentKind}.
         */
        String toString(AspectKind aspect, Object content) {
            if (content == null) {
                return aspect.getPrefix();
            } else if (this == LEVEL) {
                return aspect.getName() + ASSIGN + toString(content)
                    + SEPARATOR;
            } else {
                return aspect.getPrefix() + toString(content);
            }
        }

        /**
         * Indicates if a given character is allowed in level names. Currently
         * allowed are: letters, digits, currency symbols, underscores and periods.
         * @param c the character to be tested
         */
        static private boolean isValidFirstChar(char c) {
            return Character.isJavaIdentifierStart(c);
        }

        /**
         * Indicates if a given character is allowed in level names. Currently
         * allowed are: letters, digits, currency symbols, underscores and periods.
         * @param c the character to be tested
         */
        static private boolean isValidNextChar(char c) {
            return Character.isJavaIdentifierPart(c);
        }

        /** Returns the description of the syntactical element for this content kind
         * that appears before the aspect colon.
         */
        String preSyntax() {
            return this.preSyntax;
        }

        /** Returns the description of the syntactical element for this content kind
         * that appears after the aspect colon.
         */
        String postSyntax() {
            return this.postSyntax;
        }

        private final String signature;
        private final String preSyntax;
        private final String postSyntax;

        /** Symbolic representation of unbounded multiplicity. */
        static public final String MULT_UNBOUNDED = "*";
        /** Separator sequence in a multiplicity value. */
        static public final String MULT_SEPARATOR = "..";
        /** Start character of parameter strings. */
        static public final char PARAM_START_CHAR = '$';
    }

    /** Correct values of the {@link #NESTED} aspect kind. */
    public static enum NestedValue {
        /** Embedding of one nesting level in another. */
        IN("in"),
        /** Assignment of a nesting level to a rule node. */
        AT("at"),
        /** Count of the number of matches of a universal quantifier. */
        COUNT("count");

        private NestedValue(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return this.text;
        }

        private final String text;
    }
}
