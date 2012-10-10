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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class IgnoreComponent extends AbstractComponent implements CommandExecutor, Listener {
        private static IgnoreComponent instance;
        private Map<String, Set<String>> ignoreLists = new LinkedHashMap<String, Set<String>>();

        public IgnoreComponent(WinthierPlugin plugin) {
                super(plugin, "ignore");
                instance = this;
        }

        @Override
        public void enable() {
                getPlugin().getCommand("ignore").setExecutor(this);
                getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String token, String[] args) {
                if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.RED + "Player expected!");
                        return true;
                }
                Player ignorer = (Player)sender;
                if (args.length == 0) {
                        Set<String> ignorees = getIgnoreList(ignorer.getName());
                        if (ignorees.size() == 0) {
                                sender.sendMessage("You are not ignoring anyone.");
                        } else {
                                StringBuilder sb = new StringBuilder("Ignored players:");
                                for (String ignoree : ignorees) sb.append(" ").append(ignoree);
                                sender.sendMessage(sb.toString());
                        }
                        return true;
                }
                if (args.length == 1) {
                        String ignoree = getPlayer(args[0], ignorer.getName());
                        if (ignoree == null) {
                                sender.sendMessage("" + ChatColor.RED + "Player not found: " + args[0]);
                                return true;
                        }
                        boolean ignore = toggleIgnore(ignorer.getName(), ignoree);
                        sender.sendMessage((ignore ? "Ignoring " : "No longer ignoring ") + ignoree);
                } else {
                        for (String arg : args) {
                                String ignoree = getPlayer(arg, ignorer.getName());
                                if (ignoree == null) {
                                        sender.sendMessage("" + ChatColor.RED + "Player not found: " + arg);
                                } else {
                                        setIgnore(ignorer.getName(), ignoree, true);
                                        sender.sendMessage("Ignoring " + ignoree);
                                }
                        }
                }
                return true;
        }

        @Override
        public void loadConfiguration() {
                FileConfiguration conf = new YamlConfiguration();
                try {
                        conf.load(new File(getPlugin().getDataFolder(), "ignore.yml"));
                } catch (FileNotFoundException fnfe) {
                        // do nothing
                } catch (Exception e) {
                        e.printStackTrace();
                        return;
                }
                synchronized(this) {
                        ignoreLists.clear();
                }
                for (String ignorer : conf.getKeys(false)) {
                        for (String ignoree : conf.getStringList(ignorer)) {
                                setIgnore(ignorer, ignoree, true);
                        }
                }
        }

        @Override
        public void saveConfiguration() {
                FileConfiguration conf = new YamlConfiguration();
                synchronized(this) {
                        for (String ignorer : ignoreLists.keySet()) {
                                conf.set(ignorer, new ArrayList<String>(ignoreLists.get(ignorer)));
                        }
                }
                try {
                        conf.save(new File(getPlugin().getDataFolder(), "ignore.yml"));
                } catch (Exception e) {
                        e.printStackTrace();
                        return;
                }
        }

        private String getPlayer(String name, String forPlayer) {
                OfflinePlayer player = getPlugin().getServer().getPlayer(name);
                if (player != null) return player.getName();
                player = getPlugin().getServer().getOfflinePlayer(name);
                if (player.hasPlayedBefore()) return player.getName();
                if (forPlayer != null) {
                        synchronized(this) {
                                Set<String> ignoreList = ignoreLists.get(forPlayer);
                                if (ignoreList != null) {
                                        for (String entry : ignoreList) {
                                                if (entry.toLowerCase().startsWith(name.toLowerCase())) {
                                                        return entry;
                                                }
                                        }
                                }
                        }
                }
                return null;
        }

        private String getPlayer(String name) {
                return getPlayer(name, null);
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
        public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
                filterRecipients(event.getPlayer().getName(), event.getRecipients());
        }

        public synchronized void setIgnore(String ignorer, String ignoree, boolean ignore) {
                Set<String> ignoreList = ignoreLists.get(ignorer);
                if (ignoreList == null) {
                        if (!ignore) return;
                        ignoreList = new LinkedHashSet<String>();
                        ignoreLists.put(ignorer, ignoreList);
                }
                if (ignore) {
                        ignoreList.add(ignoree);
                } else {
                        ignoreList.remove(ignoree);
                        if (ignoreList.isEmpty()) ignoreLists.remove(ignorer);
                }
        }

        public synchronized boolean doesIgnore(String ignorer, String ignoree) {
                Set<String> ignoreList = ignoreLists.get(ignorer);
                if (ignoreList == null) return false;
                return ignoreList.contains(ignoree);
        }

        public synchronized boolean toggleIgnore(String ignorer, String ignoree) {
                boolean ignore = !doesIgnore(ignorer, ignoree);
                setIgnore(ignorer, ignoree, ignore);
                return ignore;
        }

        public synchronized void filterRecipients(String ignoree, Collection<Player> recipients) {
                for (Iterator<Player> it = recipients.iterator(); it.hasNext(); ) {
                        String ignorer = it.next().getName();
                        Set<String> ignoreList = ignoreLists.get(ignorer);
                        if (ignoreList == null) continue;
                        if (ignoreList.contains(ignoree)) {
                                it.remove();
                        }
                }
        }

        public synchronized Set<String> getIgnoreList(String ignorer) {
                Set<String> ignoreList = ignoreLists.get(ignorer);
                if (ignoreList == null) return new LinkedHashSet<String>();
                return new LinkedHashSet<String>(ignoreList);
        }

        public static IgnoreComponent getInstance() {
                return instance;
        }
}
