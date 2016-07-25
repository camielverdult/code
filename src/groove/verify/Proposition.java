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
package groove.verify;

import static groove.verify.Proposition.Kind.CALL;
import static groove.verify.Proposition.Kind.ID;
import static groove.verify.Proposition.Kind.LABEL;

import java.util.ArrayList;
import java.util.List;

import groove.algebra.Constant;
import groove.util.Exceptions;
import groove.util.line.Line;
import groove.util.parse.Id;

/** Proposition, wrapped inside a formula of type {@link LogicOp#PROP}. */
public class Proposition {
    /** Creates an identifier proposition.
     * @see Kind#ID
     */
    Proposition(Id id) {
        assert id != null;
        this.kind = ID;
        this.id = id;
        this.label = null;
        this.args = null;
    }

    /** Creates call proposition.
     * @see Kind#ID
     */
    Proposition(Id id, List<Arg> args) {
        assert id != null;
        assert args != null;
        this.kind = CALL;
        this.id = id;
        this.label = null;
        this.args = args;
    }

    /** Creates a label proposition.
     * @see Kind#LABEL
     */
    Proposition(String label) {
        assert label != null;
        this.kind = LABEL;
        this.id = null;
        this.label = label;
        this.args = null;
    }

    /** Returns the identifier in this proposition if the proposition is an {@link #ID} or {@link #CALL}
     * @throws UnsupportedOperationException if this proposition is not an {@link #ID} or {@link #CALL}
     */
    public Id getId() {
        if (getKind() == LABEL) {
            throw new UnsupportedOperationException();
        }
        return this.id;
    }

    private final Id id;

    /** Returns the kind of this proposition. */
    public Kind getKind() {
        return this.kind;
    }

    private final Kind kind;

    /** Returns the constant in this proposition if the proposition is a {@link #LABEL}.
     * @throws UnsupportedOperationException if this proposition is not a {@link #LABEL}
     */
    public String getLabel() {
        if (getKind() != LABEL) {
            throw new UnsupportedOperationException();
        }
        return this.label;
    }

    private final String label;

    /** Returns the array of call arguments.
     * @throws UnsupportedOperationException if this proposition is not a {@link #CALL}
     */
    public List<Arg> getArgs() {
        if (getKind() != CALL) {
            throw new UnsupportedOperationException();
        }
        return this.args;
    }

    /** Returns the argument count of the call. */
    public int arity() {
        return getArgs().size();
    }

    private final List<Arg> args;

    /**
     * Tests if this proposition matches another.
     * This is the case of the two are equal, or if this is a parameterless {@link #ID}
     * and the other a call of that {@link Id}, or this is a call with wildcards and the other
     * a call that only differs in providing values for the wildcards.
     */
    public boolean matches(Proposition other) {
        boolean result;
        switch (getKind()) {
        case CALL:
            if (other.getKind() == CALL) {
                result = other.arity() == arity();
                for (int i = 0; result && i < arity(); i++) {
                    result = getArgs().get(i)
                        .matches(other.getArgs()
                            .get(i));
                }
            } else {
                result = false;
            }
            break;
        case ID:
            result = equals(other);
            // An Id proposition without arguments matches all calls of that Id
            if (!result && other.getKind() == CALL) {
                return getId().equals(other.getId());
            }
            break;
        case LABEL:
            result = equals(other);
            break;
        default:
            throw Exceptions.UNREACHABLE;
        }
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.kind.hashCode();
        switch (this.kind) {
        case CALL:
            result = prime * result + this.id.hashCode();
            result = prime * result + this.args.hashCode();
            break;
        case ID:
            result = prime * result + this.id.hashCode();
            break;
        case LABEL:
            result = prime * result + this.label.hashCode();
            break;
        default:
            throw Exceptions.UNREACHABLE;
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Proposition)) {
            return false;
        }
        Proposition other = (Proposition) obj;
        if (this.kind != other.kind) {
            return false;
        }
        switch (this.kind) {
        case CALL:
            if (!this.id.equals(other.id)) {
                return false;
            }
            if (!this.args.equals(other.args)) {
                return false;
            }
            break;
        case ID:
            if (!this.id.equals(other.id)) {
                return false;
            }
            break;
        case LABEL:
            if (!this.label.equals(other.label)) {
                return false;
            }
            break;
        default:
            throw Exceptions.UNREACHABLE;
        }
        return true;
    }

    @Override
    public String toString() {
        switch (getKind()) {
        case CALL:
            StringBuilder result = new StringBuilder(this.id.getName());
            result.append('(');
            boolean first = true;
            for (Arg arg : getArgs()) {
                if (first) {
                    first = false;
                } else {
                    result.append(',');
                }
                result.append(arg);
            }
            result.append(')');
            return result.toString();
        case ID:
            return this.id.getName();
        case LABEL:
            return this.label;
        default:
            throw Exceptions.UNREACHABLE;
        }
    }

    /** Constructs a display line for this proposition.
     * @param spaces flag indicating if spaces should be used for layout.
     */
    public Line toLine(boolean spaces) {
        Line result;
        switch (getKind()) {
        case CALL:
            List<Line> lines = new ArrayList<Line>();
            lines.add(getId().toLine());
            lines.add(Line.atom("("));
            boolean firstArg = true;
            for (Arg arg : getArgs()) {
                if (!firstArg) {
                    lines.add(Line.atom(spaces ? ", " : ","));
                } else {
                    firstArg = false;
                }
                lines.add(arg.toLine());
            }
            lines.add(Line.atom(")"));
            result = Line.composed(lines);
            break;
        case LABEL:
            return Line.atom(getLabel());
        case ID:
            return getId().toLine();
        default:
            throw Exceptions.UNREACHABLE;
        }
        return result;
    }

    /**
     * Call argument.
     * Types of call arguments are given by {@link Arg#kind}.
     */
    public static class Arg {
        /** Constructs a new {@link Kind#ID} argument. */
        public Arg(Id id) {
            assert id != null;
            this.id = id;
            this.constant = null;
            this.kind = Kind.ID;
        }

        /** Constructs a new {@link Kind#CONST} argument. */
        public Arg(Constant constant) {
            assert constant != null;
            this.id = null;
            this.constant = constant;
            this.kind = Kind.CONST;
        }

        /** Constructs a new {@link Kind#WILD} argument. */
        private Arg() {
            this.id = null;
            this.constant = null;
            this.kind = Kind.WILD;
        }

        /** Returns the identifier in this argument if the argument is a {@link Kind#ID},
         * or {@code null} otherwise.
         */
        public Id getId() {
            return this.id;
        }

        /** Returns the constant in this argument if the argument is a {@link Kind#CONST},
         * or {@code null} otherwise.
         */
        public Constant getConstant() {
            return this.constant;
        }

        /** Returns the kind of this argument. */
        public Kind getKind() {
            return this.kind;
        }

        private final Kind kind;
        private final Id id;
        private final Constant constant;

        /** Tests if this argument matches another. */
        public boolean matches(Arg other) {
            switch (getKind()) {
            case CONST:
            case ID:
                return equals(other);
            case WILD:
                return true;
            default:
                throw Exceptions.UNREACHABLE;
            }
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.kind.hashCode();
            result = prime * result + ((this.constant == null) ? 0 : this.constant.hashCode());
            result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Arg)) {
                return false;
            }
            Arg other = (Arg) obj;
            if (this.kind != other.kind) {
                return false;
            }
            switch (this.kind) {
            case CONST:
                return this.constant.equals(other.constant);
            case ID:
                return this.id.equals(other.id);
            case WILD:
                return true;
            default:
                throw Exceptions.UNREACHABLE;
            }
        }

        @Override
        public String toString() {
            switch (this.kind) {
            case ID:
                return this.id.getName();
            case CONST:
                return this.constant.toString();
            case WILD:
                return WILD_TEXT;
            default:
                throw Exceptions.UNREACHABLE;
            }
        }

        Line toLine() {
            switch (getKind()) {
            case CONST:
                return getConstant().toLine();
            case ID:
                return getId().toLine();
            case WILD:
                return Line.atom("_");
            default:
                throw Exceptions.UNREACHABLE;
            }
        }

        /** Textual representation of a wildcard argument. */
        public static final String WILD_TEXT = "_";

        /** Singleton wildcard argument. */
        public static final Arg WILD_ARG = new Arg();

        /** Call argument kind. */
        public static enum Kind {
            /** Identifier argument. */
            ID,
            /** Constant argument. */
            CONST,
            /** Wildcard argument. */
            WILD;
        }
    }

    /** Atomic proposition kind. */
    public static enum Kind {
        /** Identifier proposition. */
        ID,
        /** Constant proposition. */
        LABEL,
        /** Call proposition. */
        CALL,
    }
}