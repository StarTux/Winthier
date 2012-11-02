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

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class WitherComponent extends AbstractComponent implements CommandExecutor, Listener {
        private boolean witherEnabled;
        
        public WitherComponent(WinthierPlugin plugin) {
                super(plugin, "wither");
        }

        @Override
        public void enable() {
                getPlugin().getCommand("wither").setExecutor(this);
                getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String token, String args[]) {
                if (args.length == 0) {
                        sender.sendMessage("The Wither is " + (witherEnabled ? "enabled" : "disabled") + ".");
                        sender.sendMessage("Enable/disable it with /wither on|off");
                } else if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("on")) {
                                witherEnabled = true;
                                sender.sendMessage("Wither is now enabled.");
                        } else if (args[0].equalsIgnoreCase("off")) {
                                witherEnabled = false;
                                sender.sendMessage("Wither is now disabled.");
                        }
                } else {
                        return false;
                }
                return true;
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
        public void onCreatureSpawn(CreatureSpawnEvent event) {
                if (witherEnabled) return;
                if (event.getEntity().getType() == EntityType.WITHER) {
                        event.setCancelled(true);
                }
        }
}
