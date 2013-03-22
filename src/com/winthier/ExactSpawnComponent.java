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

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

/**
 * This component makes sure that a player who joins for the first
 * time will spawn at the exact spawn location, not some random
 * spot around it.
 */
public class ExactSpawnComponent extends AbstractComponent implements Listener {
        public ExactSpawnComponent(WinthierPlugin plugin) {
                super(plugin, "exactspawn");
        }

        @Override
        public void onEnable() {
                getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
        public void onPlayerJoin(PlayerJoinEvent event) {
                if (!event.getPlayer().hasPlayedBefore()) {
                        event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation(), TeleportCause.PLUGIN);
                }
        }
}
