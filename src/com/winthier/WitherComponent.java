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

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class WitherComponent extends AbstractComponent implements CommandExecutor, Listener {
        private Map<String, Set<String>> regions = new LinkedHashMap<String, Set<String>>();
        private WorldGuardPlugin worldGuard;
        
        public WitherComponent(WinthierPlugin plugin) {
                super(plugin, "wither");
        }

        @Override
        public void enable() {
                getPlugin().getCommand("wither").setExecutor(this);
                getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
                worldGuard = (WorldGuardPlugin)getPlugin().getServer().getPluginManager().getPlugin("WorldGuard");
        }

        @Override
        public void loadConfiguration() {
                regions.clear();
                ConfigurationSection section = getConfig().getConfigurationSection("regions");
                if (section == null) return;
                for (String key : section.getKeys(false)) {
                        regions.put(key, new LinkedHashSet<String>(section.getStringList(key)));
                }
        }

        @Override
        public void saveConfiguration() {
                getConfig().set("regions", null);
                for (String key : regions.keySet()) {
                        getConfig().set("regions." + key, new ArrayList<String>(regions.get(key)));
                }
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String token, String args[]) {
                if (args.length == 0) {
                        sender.sendMessage("Subcommands: list, addregion, removeregion");
                        return true;
                } else if (args.length == 1 && args[0].equals("list")) {
                        sender.sendMessage("Wither region list:");
                        for (String key : regions.keySet()) {
                                StringBuilder sb = new StringBuilder(key);
                                sb.append(": ");
                                for (String regionId : regions.get(key)) sb.append(" ").append(regionId);
                                sender.sendMessage(sb.toString());
                        }
                        return true;
                } else if (args.length == 3 && args[0].equals("addregion")) {
                        String worldName = args[1];
                        String regionId = args[2];
                        if (getPlugin().getServer().getWorld(worldName) == null) {
                                sender.sendMessage("" + ChatColor.RED + "World \"" + worldName + "\" does not exist");
                                return true;
                        }
                        if (worldGuard.getGlobalRegionManager().get(getPlugin().getServer().getWorld(worldName)).getRegion(regionId) == null) {
                                sender.sendMessage("" + ChatColor.RED + "Region \"" + regionId + "\" does not exist in world \"" + worldName + "\"");
                                return true;
                        }
                        addRegion(worldName, regionId);
                        sender.sendMessage("Region \"" + regionId + "\" added to world \"" + worldName + "\"");
                        return true;
                } else if (args.length == 3 && args[0].equals("removeregion")) {
                        if (removeRegion(args[1], args[2])) {
                                sender.sendMessage("Region \"" + args[2] + "\" removed from world \"" + args[1] + "\"");
                        } else {
                                sender.sendMessage("" + ChatColor.RED + "Region \"" + args[2] + "\" did not exist in world \"" + args[1] + "\"");
                        }
                        return true;
                }
                return false;
        }
        
        public Set<String> getRegionList(String worldName) {
                return regions.get(worldName);
        }

        public void addRegion(String worldName, String regionId) {
                Set<String> regionList = regions.get(worldName);
                if (regionList == null) {
                        regionList = new LinkedHashSet<String>();
                        regions.put(worldName, regionList);
                }
                regionList.add(regionId.toLowerCase());
        }

        public boolean removeRegion(String worldName, String regionId) {
                Set<String> regionList = regions.get(worldName);
                if (regionList == null) return false;
                return regionList.remove(regionId.toLowerCase());
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
        public void onCreatureSpawn(CreatureSpawnEvent event) {
                if (event.getEntity().getType() != EntityType.WITHER) return;
                Set<String> regionList = getRegionList(event.getLocation().getWorld().getName());
                if (regionList == null) {
                        event.setCancelled(true);
                }
                for (String regionId : worldGuard.getGlobalRegionManager().get(event.getLocation().getWorld()).getApplicableRegionsIDs(new Vector(event.getLocation().getX(), event.getLocation().getY(), event.getLocation().getZ()))) {
                        if (regionList.contains(regionId.toLowerCase())) return;
                }
                event.setCancelled(true);
        }
}
