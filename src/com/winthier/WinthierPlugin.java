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

import java.util.LinkedHashSet;
import java.util.Set;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class WinthierPlugin extends JavaPlugin implements Listener {
        private Permission permission = null;
        private Set<Component> components = new LinkedHashSet<Component>();

        @Override
        public void onEnable() {
                getServer().getPluginManager().registerEvents(this, this);
                setupPermissions();
                components.add(new PasswordComponent(this));
                components.add(new RulesComponent(this));
                components.add(new MotdComponent(this));
                components.add(new SignColorComponent(this));
                for (Component component : components) {
                        component.enable();
                        component.loadConfiguration();
                }
        }

        public void onDisable() {
                for (Component component : components) {
                        component.saveConfiguration();
                        component.disable();
                }
                getConfig().options().copyDefaults(true);
                saveConfig();
        }

        private boolean setupPermissions()
        {
                RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
                if (permissionProvider != null) {
                        permission = permissionProvider.getProvider();
                }
                return (permission != null);
        }

        public Permission getPermission() {
                return permission;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String token, String[] args) {
                if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                        reloadConfig();
                        for (Component component : components) {
                                component.reloadConfiguration();
                        }
                        return true;
                }
                return false;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerJoin(PlayerJoinEvent event) {
                if (!event.getPlayer().hasPermission("winthier.joinleavemsg")) {
                        event.setJoinMessage("");
                } else {
                        event.setJoinMessage(ChatColor.DARK_GRAY.toString() + event.getPlayer().getName() + " joined");
                }
                if (!event.getPlayer().hasPermission("winthier.cheatmods")) {
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

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerQuit(PlayerQuitEvent event) {
                if (!event.getPlayer().hasPermission("winthier.joinleavemsg")) {
                        event.setQuitMessage("");
                } else {
                        event.setQuitMessage(ChatColor.DARK_GRAY.toString() + event.getPlayer().getName() + " left");
                }
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerKick(PlayerKickEvent event) {
                if (!event.getPlayer().hasPermission("winthier.joinleavemsg")) {
                        event.setLeaveMessage("");
                } else {
                        event.setLeaveMessage(ChatColor.DARK_GRAY.toString() + event.getPlayer().getName() + " left");
                }
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerDeath(PlayerDeathEvent event) {
                if (!event.getEntity().hasPermission("winthier.deathmsg")) {
                        event.setDeathMessage("");
                } else {
                        event.setDeathMessage(ChatColor.DARK_GRAY.toString() + event.getDeathMessage());
                }
        }
}
