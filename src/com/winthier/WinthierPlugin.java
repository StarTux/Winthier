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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class WinthierPlugin extends JavaPlugin implements Listener {
        private Permission permission = null;
        private Set<Component> components = new LinkedHashSet<Component>();

        @Override
        public void onEnable() {
                getServer().getPluginManager().registerEvents(this, this);
                setupPermissions();
                components.add(new NoClientModsComponent(this));
                components.add(new PasswordComponent(this));
                components.add(new RulesComponent(this));
                components.add(new MotdComponent(this));
                components.add(new SignColorComponent(this));
                components.add(new MessageComponent(this));
                components.add(new IgnoreComponent(this));
                components.add(new PlayerMessagesComponent(this));
                components.add(new ChatComponent(this));
                components.add(new NewbieWatchComponent(this));
                components.add(new WitherComponent(this));
                components.add(new NoVoidDeathComponent(this));
                components.add(new ExactSpawnComponent(this));
                components.add(new MailComponent(this));
                components.add(new PartyComponent(this));
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
                if (args.length == 1 && args[0].equals("devnull")) return true;
                if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                        reloadConfig();
                        for (Component component : components) {
                                component.reloadConfiguration();
                        }
                        sender.sendMessage("Configuration reloaded");
                        return true;
                }
                if (args.length == 1 && args[0].equalsIgnoreCase("save")) {
                        for (Component component : components) {
                                component.saveConfiguration();
                        }
                        getConfig().options().copyDefaults(true);
                        saveConfig();
                        sender.sendMessage("Configuration saved");
                        return true;
                }
                return false;
        }
}
