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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;

public class StringFilter {
        /**
         * Utility class; nothing but a constant String.
         */
        public static class Constant {
                public String string;
                public Constant(String string) {
                        this.string = string;
                }
                @Override
                public String toString() {
                        return string;
                }
        }
        private List<StringReplacer> replacers = new ArrayList<StringReplacer>();

        public void addReplacer(StringReplacer replacer) {
                replacers.add(replacer);
        }

        public List<Object> filter(String input) {
                List<Object> tokens = new LinkedList<Object>();
                tokens.add(input);
                for (StringReplacer replacer : replacers) {
                        ListIterator<Object> iter = tokens.listIterator();
                        while (iter.hasNext()) {
                                Object o = iter.next();
                                if (!(o instanceof String)) continue;
                                iter.remove();
                                String string = (String)o;
                                Pattern pattern = replacer.getPattern();
                                Matcher matcher = pattern.matcher(string);
                                int lastIndex = 0;
                                while (matcher.find()) {
                                        String remainder = string.substring(lastIndex, matcher.start());
                                        if (remainder.length() > 0) {
                                                iter.add(remainder);
                                        }
                                        iter.add(replacer.getReplacement(matcher));
                                        lastIndex = matcher.end();
                                }
                                iter.add(string.substring(lastIndex, string.length()));
                        }
                }
                return tokens;
        }

        public Message parseMessage(String input) {
                Message result = new Message();
                result.addLine(filter(input));
                return result;
        }

        public Message parseMessage(List<String> input) {
                Message result = new Message();
                for (String line : input) result.addLine(filter(line));
                return result;
        }

        public static String build(List<Object> input) {
                StringBuilder builder = new StringBuilder();
                for (Object o : input) {
                        builder.append(o.toString());
                }
                return builder.toString();
        }

        public static String buildNoColor(List<Object> input) {
                StringBuilder builder = new StringBuilder();
                for (Object o : input) {
                        if (!(o instanceof ChatColor)) builder.append(o.toString());
                }
                return builder.toString();
        }

        public String replace(String input) {
                return build(filter(input));
        }

        public List<String> replace(List<String> input) {
                List<String> result = new ArrayList<String>(input.size());
                for (String line : input) {
                        result.add(replace(line));
                }
                return result;
        }
}
