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

public class DefaultStringFilter extends StringFilter {
        private static class EscapeReplacer implements StringReplacer {
                public static Pattern pattern = Pattern.compile("\\\\(.)");
                @Override
                public Pattern getPattern() {
                        return pattern;
                }
                @Override
                public Object getReplacement(Matcher matcher) {
                        return new StringFilter.Constant(matcher.group(1));
                }
        }
        public static final EscapeReplacer ESCAPE_REPLACER = new EscapeReplacer();

        public DefaultStringFilter() {
                addReplacer(ESCAPE_REPLACER);
        }
}
