/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.util.parse;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import nl.utwente.groove.grammar.aspect.AspectElement;
import nl.utwente.groove.graph.Element;

/**
 * Set of format errors, with additional functionality for
 * adding errors and throwing an exception on the basis of the
 * errors.
 * @author Arend Rensink
 * @version $Revision $
 */
public class FormatErrorSet extends LinkedHashSet<FormatError> {
    /** Constructs a fresh, empty error set. */
    public FormatErrorSet() {
    }

    /** Constructs a copy of a given set of errors. */
    public FormatErrorSet(Collection<? extends FormatError> c) {
        super(c);
        if (c instanceof FormatErrorSet fes) {
            this.wrapper.putAll(fes.wrapper);
        }
    }

    /** Constructs a singleton error set. */
    public FormatErrorSet(String message, Object... args) {
        add(message, args);
    }

    /** Adds a format error based on a given error message and set of arguments. */
    public boolean add(String message, Object... args) {
        return add(new FormatError(message, args).wrap(this.wrapper));
    }

    /** Adds a format error based on an existing error and set of additional arguments. */
    public boolean add(FormatError error, Object... args) {
        return add(new FormatError(error, args).wrap(this.wrapper));
    }

    @Override
    public boolean add(FormatError e) {
        return super.add(e.wrap(this.wrapper));
    }

    /**
     * Throws an exception based on this error set if the error set is nonempty.
     * Does nothing otherwise.
     * @throws FormatException if this error set is nonempty.
     */
    public void throwException() throws FormatException {
        if (!isEmpty()) {
            throw new FormatException(this);
        }
    }

    /** Returns a new format error set in which the context information is transferred.
     * @param map mapping from the context of this error to the context
     * of the result error; or {@code null} if there is no mapping
     */
    public FormatErrorSet transfer(Map<?,?> map) {
        FormatErrorSet result = new FormatErrorSet();
        for (FormatError error : this) {
            result.add(error.transfer(map));
        }
        return result;
    }

    /** Returns a new format error set in which the context information is extended. */
    public FormatErrorSet extend(Object... objects) {
        FormatErrorSet result = new FormatErrorSet();
        for (FormatError error : this) {
            result.add(error.extend(objects));
        }
        return result;
    }

    /** Returns a new error set, based on the current one,
     * in which the wrapper has been extended.
     * All errors in the set are likewise wrapped.
     * @param wrapper mapping from error {@link Element}s to (contextual) {@link AspectElement}s
     */
    public FormatErrorSet wrap(Map<?,?> wrapper) {
        var result = new FormatErrorSet();
        stream().map(e -> e.wrap(wrapper)).forEach(result::add);
        result.wrapper.putAll(wrapper);
        return result;
    }

    private final Map<Object,Object> wrapper = new HashMap<>();

    @Override
    public FormatErrorSet clone() {
        return new FormatErrorSet(this);
    }
}
