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

import java.util.List;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MotdComponent extends AbstractComponent implements Listener {
        private List<String> motd;

        public MotdComponent(WinthierPlugin plugin) {
                super(plugin, "motd");
        }

        @Override
        public void loadConfiguration() {
                StringFilter filter = new ColorStringFilter();
                motd = filter.replace(getConfig().getStringList("Message"));
        }

        @Override
        public void onEnable() {
                getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onPlayerJoin(PlayerJoinEvent event) {
                for (String line : motd) {
                        event.getPlayer().sendMessage(line);
                }
        }
}
