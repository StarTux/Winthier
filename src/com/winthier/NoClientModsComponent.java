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

public class NoClientModsComponent extends AbstractComponent implements Listener {
        private boolean enabled;

        public NoClientModsComponent(WinthierPlugin plugin) {
                super(plugin, "noclientmods");
        }

        @Override
        public void onEnable() {
                getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
        }

        @Override
        public void loadConfiguration() {
                enabled = getConfig().getBoolean("enabled", true);
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
        public void onPlayerJoin(PlayerJoinEvent event) {
                if (!enabled) return;
                if (event.getPlayer().hasPermission("winthier.noclientmods.exempt")) return;
                String message = "";
                // Disable Zombe's fly mod.
                message += "§f §f §1 §0 §2 §4";
                // Disable Zombe's noclip.
                message += "§f §f §4 §0 §9 §6";
                // Disable Zombe's cheat.
                message += "§f §f §2 §0 §4 §8";
                // Disable CJB's fly mod.
                message += "§3 §9 §2 §0 §0 §1";
                // Disable CJB's xray.
                message += "§3 §9 §2 §0 §0 §2";
                // Disable CJB's radar.
                message += "§3 §9 §2 §0 §0 §3";
                // Disable Minecraft AutoMap's ores.
                message += "§0§0§1§f§e";
                // Disable Minecraft AutoMap's cave mode.
                message += "§0§0§2§f§e";
                // Disable Minecraft AutoMap's radar.
                message += "§0§0§3§4§5§6§7§8§f§e";
                // Disable Smart Moving's climbing.
                message += "§0§1§0§1§2§f§f";
                // Disable Smart Moving's climbing.
                message += "§0§1§3§4§f§f";
                // Disable Smart Moving's climbing.
                message += "§0§1§5§f§f";
                // Disable Smart Moving's climbing.
                message += "§0§1§6§f§f";
                // Disable Smart Moving's climbing.
                message += "§0§1§8§9§a§b§f§f";
                // Disable Smart Moving's climbing.
                message += "§0§1§7§f§f";
                event.getPlayer().sendMessage(message);
        }
}
