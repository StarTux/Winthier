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

import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;

public class ConfigurableColorStringFilter extends DefaultStringFilter {
        public static class ConfigurableColorReplacer extends ColorStringFilter.ColorReplacer {
                protected EnumSet<ChatColor> whitelist = EnumSet.allOf(ChatColor.class);
                @Override
                public Object getReplacement(Matcher matcher) {
                        ChatColor color = ChatColor.getByChar(matcher.group(1));
                        if (!isAllowed(color)) return new StringFilter.Constant(matcher.group(0));
                        return color;
                }

                public void setAllowed(ChatColor color, boolean allow) {
                        if (allow) {
                                whitelist.add(color);
                        } else {
                                whitelist.remove(color);
                        }
                }

                public boolean isAllowed(ChatColor color) {
                        return whitelist.contains(color);
                }
        }
        protected ConfigurableColorReplacer replacer = new ConfigurableColorReplacer();

        public ConfigurableColorStringFilter() {
                addReplacer(replacer);
        }

        public void whitelist(ChatColor color) {
                replacer.setAllowed(color, true);
        }

        public void blacklist(ChatColor color) {
                replacer.setAllowed(color, false);
        }

        public boolean isAllowed(ChatColor color) {
                return replacer.isAllowed(color);
        }
}
