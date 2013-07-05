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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
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
        private Map<String, Component> components = new HashMap<String, Component>();
        private Set<String> enabledComponents = new LinkedHashSet<String>();
        private Set<String> disabledComponents = new LinkedHashSet<String>();

        @Override
        public void onEnable() {
                getServer().getPluginManager().registerEvents(this, this);
                setupPermissions();
                Component tmp[] = {
                        new NoClientModsComponent(this),
                        new PasswordComponent(this),
                        new RulesComponent(this),
                        new MotdComponent(this),
                        new SignColorComponent(this),
                        new MessageComponent(this),
                        new IgnoreComponent(this),
                        new PlayerMessagesComponent(this),
                        new ChatComponent(this),
                        new NewbieWatchComponent(this),
                        new WitherComponent(this),
                        new NoVoidDeathComponent(this),
                        new ExactSpawnComponent(this),
                        new MailComponent(this),
                        new PartyComponent(this),
                        new StarveComponent(this)
                };
                for (Component component : tmp) {
                        components.put(component.getName(), component);
                }
                getConfig().options().copyDefaults(true);
                loadConfiguration();
        }

        public void onDisable() {
                for (Component component : components.values()) {
                        component.disable();
                }
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
                if (!token.equalsIgnoreCase("winthier")) {
                        sender.sendMessage("Unknown command. Type \"help\" for help.");
                        return true;
                }
                if (args.length == 1 && args[0].equals("devnull")) return true;
                if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                        reloadConfig();
                        reloadConfiguration();
                        saveConfiguration();
                        sender.sendMessage("Configuration reloaded");
                        return true;
                }
                if (args.length == 1 && args[0].equalsIgnoreCase("save")) {
                        saveConfiguration();
                        sender.sendMessage("Configuration saved");
                        return true;
                }
                return false;
        }

        private final void reloadConfiguration() {
                loadConfiguration();
                for (Component component : components.values()) {
                        if (component.isEnabled()) component.reloadConfiguration();
                }
        }

        private final void loadConfiguration() {
                enabledComponents.clear();
                disabledComponents.clear();
                enabledComponents.addAll(getConfig().getStringList("enabled"));
                disabledComponents.addAll(getConfig().getStringList("disabled"));
                // remove duplicates and zombies from enabledComponents
                for (Iterator<String> iter = enabledComponents.iterator(); iter.hasNext();) {
                        String enabled = iter.next();
                        if (disabledComponents.contains(enabled) || !components.keySet().contains(enabled)) iter.remove();
                }
                // remove zombies from disabledComponents
                for (Iterator<String> iter = disabledComponents.iterator(); iter.hasNext();) {
                        String disabled = iter.next();
                        if (!components.keySet().contains(disabled)) iter.remove();
                }
                // add components to disabledComponents by default
                for (String name : components.keySet()) {
                        if (!enabledComponents.contains(name) && !disabledComponents.contains(name)) {
                                disabledComponents.add(name);
                        }
                }
                for (String enabled : enabledComponents) {
                        Component component = components.get(enabled);
                        component.enable();
                }
                for (String disabled : disabledComponents) {
                        Component component = components.get(disabled);
                        component.disable();
                }
        }

        private final void saveConfiguration() {
                for (Component component : components.values()) {
                        if (component.isEnabled()) component.saveConfiguration();
                }
                getConfig().set("enabled", new ArrayList<String>(enabledComponents));
                getConfig().set("disabled", new ArrayList<String>(disabledComponents));
                saveConfig();
        }
}
