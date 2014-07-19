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
package groove.explore.config;

import groove.util.parse.Parser;

/**
 * Boolean setting key.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum BooleanKey implements SettingKey, Setting<BooleanKey,NullContent> {
    /** Key for the boolean value {@code false}. */
    FALSE,
    /** Key for the boolean value {@code true}. */
    TRUE, ;

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getExplanation() {
        return "Boolean value";
    }

    @Override
    public Parser<? extends SettingContent> parser() {
        return NullContent.PARSER;
    }

    @Override
    public SettingContent getDefaultValue() {
        return null;
    }

    @Override
    public boolean isValue(Object value) {
        return value == null;
    }

    @Override
    public SettingList getDefaultSetting() {
        return SettingList.single(createSetting(getDefaultValue()));
    }

    @Override
    public Setting<?,?> createSettting() throws IllegalArgumentException {
        return this;
    }

    @Override
    public Setting<?,?> createSetting(SettingContent content) throws IllegalArgumentException {
        if (content != null) {
            throw new IllegalArgumentException();
        }
        return this;
    }

    @Override
    public SettingList wrap() {
        return SettingList.single(this);
    }

    @Override
    public BooleanKey getKind() {
        return this;
    }

    @Override
    public NullContent getContent() {
        return null;
    }

    @Override
    public Class<NullContent> getContentType() {
        return NullContent.class;
    }

    /** Returns the key for a given boolean value. */
    public static BooleanKey getKey(boolean value) {
        return value ? TRUE : FALSE;
    }
}
