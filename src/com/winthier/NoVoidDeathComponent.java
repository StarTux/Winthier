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

import org.bukkit.util.Vector;
import org.bukkit.World.Environment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class NoVoidDeathComponent extends AbstractComponent implements Listener {
        public NoVoidDeathComponent(WinthierPlugin plugin) {
                super(plugin, "novoiddeath");
        }

        public void enable() {
                getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
        public void onPlayerVoidDamage(EntityDamageEvent event) {
                if (event.getCause() != EntityDamageEvent.DamageCause.VOID) return;
                if (event.getEntity().getType() != EntityType.PLAYER) return;
                if (event.getEntity().getWorld().getEnvironment() == Environment.THE_END) return;
                Player player = (Player)event.getEntity();
                getPlugin().getLogger().info(String.format("[NoVoidDeath] saved %s in %s at (%d,%d,%d)", player.getName(), player.getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()));
                player.sendMessage("You fell into the void. Please report this incident.");
                player.setVelocity(new Vector(0, 0, 0));
                player.setFallDistance(0f);
                player.teleport(player.getWorld().getSpawnLocation(), TeleportCause.PLUGIN);
                event.setCancelled(true);
        }
}
