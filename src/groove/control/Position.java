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
package groove.control;

import groove.control.template.Location;
import groove.control.template.Template;

/**
 * Supertype for {@link Location} (which is a node of a {@link Template}) 
 * and Deadlock (which is not). This is so as to be able to keep the {@link Template}
 * graph "clean" by not having to include verdicts to deadlock locations (which
 * would otherwise be all over the place).
 * @author Arend Rensink
 * @version $Revision $
 */
public interface Position<P extends Position<P>> {
    /** Returns the position type. */
    public abstract Type getType();

    /**
     * Indicates if this is a deadlock position.
     * A deadlock position has no outgoing calls or verdicts.
     * Convenience method for {@code getType() == DEAD}.
     * @see Type#DEAD
     */
    public abstract boolean isDead();

    /**
     * Indicates if this is a final position.
     * A deadlock position has no outgoing calls or verdicts.
     * Convenience method for {@code getType() == FINAL}.
     * @see Type#FINAL
     */
    public abstract boolean isFinal();

    /**
     * Indicates if this is a trial position.
     * A trial position has at least one outgoing call, as well as verdicts.
     * Convenience method for {@code getType() == TRIAL}.
     * @see Type#TRIAL
     */
    public abstract boolean isTrial();

    /** Returns the transient depth of this position. */
    public abstract int getDepth();

    /**
     * Returns the attempt of this position.
     * Should only be invoked after the position is fixed, and is a trial position.
     */
    public abstract Attempt<P> getAttempt();

    /**
     * Returns the next position to be tried after the attempt has failed.
     * Should only be called after the position is fixed, and is a trial position.
     * @return the next position after failure; may be {@code null}
     */
    public abstract Position<P> onFailure();

    /**
     * Returns the next position to be tried after the attempt
     * has succeeded.
     * Should only be called after the position is fixed, and is a trial position.
     * @return the next position after success; may be {@code null}
     */
    public abstract Position<P> onSuccess();

    /** Position type. */
    public static enum Type {
        /** Final position: terminating, no attempt or verdicts. */
        FINAL,
        /** Deadlock position: non-terminating, no attempt or verdicts. */
        DEAD,
        /** Trial position: has an attempt, and corresponding success and failure verdicts. */
        TRIAL, ;
    }
}