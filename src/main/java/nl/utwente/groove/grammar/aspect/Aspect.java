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
package nl.utwente.groove.grammar.aspect;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.grammar.aspect.AspectContent.ConstContent;
import nl.utwente.groove.grammar.aspect.AspectContent.ContentKind;
import nl.utwente.groove.grammar.aspect.AspectContent.ExprContent;
import nl.utwente.groove.grammar.aspect.AspectContent.NullContent;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Parsed aspect, as used in an aspect graph to represent features
 * of the models. An aspect consists of an aspect kind and an optional
 * content field.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Aspect {
    /** Creates a prototype (i.e., empty) aspect for a given aspect kind. */
    Aspect(AspectKind kind) {
        this.aspectKind = kind;
        this.prototype = true;
        this.content = new NullContent(kind.getContentKind());
    }

    /** Creates a new aspect, wrapping either a number or a text. */
    Aspect(AspectKind kind, AspectContent content) {
        assert kind.getContentKind() == content.kind();
        this.aspectKind = kind;
        this.content = content;
        this.prototype = false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.aspectKind.hashCode();
        result = prime * result + this.content.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Aspect other = (Aspect) obj;
        if (this.aspectKind != other.aspectKind) {
            return false;
        }
        if (!this.content.equals(other.content)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getContent().toParsableString(getKind());
    }

    /**
     * Creates a new aspect, of the same kind of this one (which must be a prototype),
     * wrapping content derived from given (non-{@code null} text.
     * @param role intended graph role of the new aspect
     * @throws UnsupportedOperationException if this aspect is itself already
     * instantiated
     * @throws FormatException if the text cannot be correctly parsed as content
     * for this aspect
     */
    public Aspect newInstance(String text, GraphRole role) throws FormatException {
        if (!this.prototype) {
            throw Exceptions.unsupportedOp("New aspects can only be created from prototypes");
        }
        return new Aspect(getKind(), getContentKind().parseContent(text, role));
    }

    /**
     * Returns an aspect obtained from this one by changing all
     * occurrences of a certain label into another.
     * @param oldLabel the label to be changed
     * @param newLabel the new value for {@code oldLabel}
     * @return a clone of this object with changed labels, or this object
     *         if {@code oldLabel} did not occur
     */
    public Aspect relabel(TypeLabel oldLabel, TypeLabel newLabel) {
        Aspect result = this;
        AspectContent newContent = getContent().relabel(oldLabel, newLabel);
        if (newContent != getContent()) {
            result = new Aspect(getKind(), newContent);
        }
        return result;
    }

    /** Returns the aspect kind. */
    public AspectKind getKind() {
        return this.aspectKind;
    }

    /**
     * Indicates if this aspect has non-empty content.
     */
    public boolean hasContent() {
        return !(this.content instanceof NullContent);
    }

    /**
     * Returns the content wrapped by this aspect.
     */
    public @NonNull AspectContent getContent() {
        return this.content;
    }

    /** Convenience method to return the content kind of this aspect. */
    public ContentKind getContentKind() {
        return getKind().getContentKind();
    }

    /**
     * Returns a string description of the aspect content that can be parsed back to the content,
     * or the empty string if the aspect has no content.
     */
    public String getContentString() {
        return getContent().toParsableString();
    }

    /** Indicates that this aspect kind is allowed to appear on edges of a particular graph kind. */
    public boolean isForEdge(GraphRole role) {
        boolean result = AspectKind.allowedEdgeKinds.get(role).contains(getKind());
        if (result && getKind().hasSort()) {
            result = !(getContent() instanceof ConstContent || getContent() instanceof ExprContent);
        }
        return result;
    }

    /** Indicates that this aspect kind is allowed to appear on nodes of a particular graph kind. */
    public boolean isForNode(GraphRole role) {
        boolean result = AspectKind.allowedNodeKinds.get(role).contains(getKind());
        if (result && getKind().hasSort()) {
            result = switch (role) {
            case TYPE -> !hasContent();
            case RULE -> !hasContent() || (getContent() instanceof ExprContent);
            case HOST -> (getContent() instanceof ConstContent c) && c.get().isTerm();
            default -> throw Exceptions.UNREACHABLE;
            };
        }
        return result;
    }

    /** Flag indicating that this aspect is a prototype. */
    private final boolean prototype;
    /** Aspect kind of this aspect. */
    private final AspectKind aspectKind;
    /** Content wrapped in this aspect; {@code null} if this is a prototype. */
    private final AspectContent content;

    /** Returns the prototypical aspect for a given aspect name. */
    public static Aspect getAspect(String name) {
        return aspectNameMap.get(name);
    }

    /** Mapping from aspect names to canonical aspects (with that name). */
    private final static Map<String,Aspect> aspectNameMap = new HashMap<>();

    static {
        for (AspectKind kind : AspectKind.values()) {
            aspectNameMap.put(kind.getName(), kind.getAspect());
        }
    }
}
