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

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignColorComponent extends AbstractComponent implements Listener {
        public SignColorComponent(WinthierPlugin plugin) {
                super(plugin, "signcolor");
        }

        @Override
        public void enable() {
                getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
        }

        @Override
        public void disable() {}

        @Override
        public void loadConfiguration() {
                
        }

        @EventHandler
        public void onSignChange(SignChangeEvent event) {
                ConfigurableColorStringFilter filter = new ConfigurableColorStringFilter();
                for (ChatColor color : ChatColor.values()) {
                        if (event.getPlayer().hasPermission("winthier.signcolor." + color.getChar())) {
                                filter.whitelist(color);
                        } else {
                                filter.blacklist(color);
                        }
                }
                for (int i = 0; i < 4; ++i) {
                        String line = filter.replace(event.getLine(i));
                        event.setLine(i, line);
                }
        }
}
