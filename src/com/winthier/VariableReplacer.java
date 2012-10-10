/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright 2012 StarTux
 *
 * This file is part of Winthier.
 *
 * Winthier is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Winthier is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Winthier.  If not, see <http://www.gnu.org/licenses/>.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.winthier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;

public class VariableReplacer implements StringReplacer {
        private Pattern referencePattern;
        private Map<String, Object> variables = new HashMap<String, Object>();

        public VariableReplacer(String referencePattern) {
                this.referencePattern = Pattern.compile(referencePattern);
        }
        
        @Override
        public Pattern getPattern() {
                return referencePattern;
        }

        @Override
        public Object getReplacement(Matcher matcher) {
                return new VariableInstance(this, matcher.group(1));
        }

        public void setVariable(String key, Object value) {
                variables.put(key, value);
        }

        public String getVariable(String key) {
                Object v = variables.get(key);
                if (v == null) return "";
                return v.toString();
        }
}

class VariableInstance {
        protected VariableReplacer parent;
        protected String name;

        public VariableInstance(VariableReplacer parent, String name) {
                this.parent = parent;
                this.name = name;
        }

        @Override
        public String toString() {
                return parent.getVariable(name);
        }
}
