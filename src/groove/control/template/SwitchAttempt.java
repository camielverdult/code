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
package groove.control.template;

import groove.control.Attempt;

/**
 * Vector of switches in combination with the success and failure alternates.
 * @author Arend Rensink
 * @version $Revision $
 */
public class SwitchAttempt extends Attempt<Location,SwitchStack> {
    /** Constructs a switch attempt for a given source location. */
    public SwitchAttempt(Location source, Location onSuccess, Location onFailure) {
        this.source = source;
        setSuccess(onSuccess);
        setFailure(onFailure);
        assert source.getTemplate() == onSuccess.getTemplate();
        assert source.getTemplate().getLocations().contains(source);
    }

    /** Returns the source location of this switch attempt. */
    public Location source() {
        return this.source;
    }

    private final Location source;
}
