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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PartyComponent extends AbstractComponent implements Listener {
        private Map<String, String> parties = new HashMap<String, String>();
        private Map<Player, String> invites = new HashMap<Player, String>();
        private Set<Player> focussed = Collections.synchronizedSet(new HashSet<Player>());
        private VariableMessage messageFormat;

        public PartyComponent(WinthierPlugin plugin) {
                super(plugin, "party");
        }

        @Override
        public void onEnable() {
                load();
                getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
        }

        @Override
        public void onDisable() {
                save();
        }

        @Override
        public void loadConfiguration() {
                messageFormat = new VariableStringFilter().parseMessage(getConfig().getString("MessageFormat"));
        }

        @Override
        public void saveConfiguration() {
                save();
        }

        private static final void sendMessage(CommandSender sender, String message) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

        private List<Player> getPartyPlayers(String party) {
                List<Player> result = new ArrayList<Player>();
                for (Player player : getPlugin().getServer().getOnlinePlayers()) {
                        String pp = parties.get(player.getName());
                        if (party.equals(pp)) {
                                result.add(player);
                        }
                }
                return result;
        }

        public void joinParty(Player player, String partyName) {
                String oldParty = parties.get(player.getName());
                if (oldParty != null) {
                        for (Player other : getPartyPlayers(oldParty)) {
                                if (!other.equals(player)) {
                                        sendMessage(other, "&7" + player.getName() + " left party &b" + oldParty + "&7.");
                                }
                        }
                }
                if (partyName == null) {
                        parties.remove(player.getName());
                        focussed.remove(player);
                        sendMessage(player, "&7You left the party.");
                        return;
                }
                parties.put(player.getName(), partyName);
                sendMessage(player, "&7You joined party &b" + partyName + "&7.");
                for (Player other : getPartyPlayers(partyName)) {
                        if (other.equals(player)) continue;
                        sendMessage(other, "&7" + player.getName() + " joined party &b" + partyName + "&7.");
                }
        }

        public void focusParty(Player player, boolean focus) {
                String partyName = parties.get(player.getName());
                if (focus && partyName == null) {
                        sendMessage(player, "&cYou are not in a party to focus.");
                        focussed.remove(player);
                        return;
                }
                if (focus) {
                        focussed.add(player);
                        sendMessage(player, "&7Now focussing party " + partyName + ".");
                } else {
                        focussed.remove(player);
                        sendMessage(player, "&7No longer focussing party chat.");
                }
        }

        public void speakParty(Player player, String message) {
                String partyName = parties.get(player.getName());
                if (partyName == null) {
                        sendMessage(player, "&cYou are not in a party");
                        focussed.remove(player); // should never happen
                        return;
                }
                messageFormat.setVariable("party", partyName);
                messageFormat.setVariable("sender", player.getName());
                messageFormat.setVariable("message", message);
                String msg = messageFormat.toString();
                for (Player other : getPartyPlayers(partyName)) {
                        other.sendMessage(msg);
                }
                getPlugin().getLogger().info("[Party] [" + partyName + "] " + player.getName() + ": " + message);
        }

        @CommandHandler(description = "Join or create a party", usage = "/<command> [partyname]", permission = "winthier.party", permissionDefault = "op")
        public boolean party(CommandSender sender, Command command, String alias, String args[]) {
                if (!(sender instanceof Player)) {
                        sender.sendMessage("Player expected!");
                        return true;
                }
                if (args.length == 0) {
                        String party = parties.get(sender.getName());
                        if (party == null) {
                                sendMessage(sender, "&cYou are not in a party.");
                        } else {
                                StringBuilder sb = new StringBuilder("&7Party &b").append(party).append("&7:");
                                for (Player player : getPartyPlayers(party)) {
                                        sb.append(" ").append(player.getName());
                                }
                                sendMessage(sender, sb.toString());
                        }
                } else if (args.length == 1 && args[0].equals("focus")) {
                        Player player = (Player)sender;
                        focusParty(player, true);
                        return true;
                } else if (args.length == 1 && args[0].equals("unfocus")) {
                        Player player = (Player)sender;
                        focusParty(player, false);
                        return true;
                } else if (args.length == 1 && args[0].equals("accept")) {
                        Player player = (Player)sender;
                        String partyName = invites.get(player);
                        if (partyName == null) {
                                sendMessage(sender, "&cNo one invited you.");
                                return true;
                        }
                        joinParty(player, partyName);
                } else if (args.length == 1 && (args[0].equals("q") || args[0].equals("quit"))) {
                        if (parties.get(sender.getName()) == null) {
                                sendMessage(sender, "&cYou are not in a party.");
                                return true;
                        }
                        Player player = (Player)sender;
                        joinParty(player, null);
                } else if (args.length >= 1 && args[0].equals("invite")) {
                        if (args.length == 1 || args.length > 2) {
                                sendMessage(sender, "&fUsage: &e/party &6invite <player>");
                                return true;
                        }
                        String partyName = parties.get(sender.getName());
                        if (partyName == null) {
                                sendMessage(sender, "&cYou must be in a party to invite someone.");
                                return true;
                        }
                        Player invitee = getPlugin().getServer().getPlayer(args[1]);
                        if (invitee == null) {
                                sendMessage(sender, "&cPlayer not found: " + args[1]);
                                return true;
                        }
                        invites.put(invitee, partyName);
                        sendMessage(sender, "&7Invited " + invitee.getName() + " to party " + partyName);
                        sendMessage(invitee, "&7" + sender.getName() + " has invited you to the party " + partyName + ". To accept, type &e/party accept&7.");
                } else if (args.length == 1) {
                        Player player = (Player)sender;
                        final String partyName = args[0];
                        joinParty(player, partyName);
                } else {
                        return false;
                }
                return true;
        }

        @CommandHandler(description = "Speak in party chat", usage = "/<command> <message>", permission = "winthier.party", permissionDefault = "op")
        public boolean p(CommandSender sender, Command command, String alias, String args[]) {
                if (!(sender instanceof Player)) {
                        sender.sendMessage("Player expected!");
                        return true;
                }
                Player player = (Player)sender;
                if (args.length == 0) {
                        if (focussed.contains(player)) {
                                focusParty(player, false);
                        } else {
                                focusParty(player, true);
                        }
                        return true;
                } else {
                        StringBuilder sb = new StringBuilder(args[0]);
                        for (int i = 1; i < args.length; ++i) {
                                sb.append(" ").append(args[i]);
                        }
                        speakParty(player, sb.toString());
                        return true;
                }
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
        public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
                final Player player = event.getPlayer();
                if (!focussed.contains(player)) return;
                event.setCancelled(true);
                final String msg = event.getMessage();
                new BukkitRunnable() {
                        public void run() {
                                speakParty(player, msg);
                        }
                }.runTask(getPlugin());
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
        public void onPlayerQuit(PlayerQuitEvent event) {
                invites.remove(event.getPlayer());
                focussed.remove(event.getPlayer());
        }

        // configuration routines

        private File getSaveFile() {
                File file = getPlugin().getDataFolder();
                if (!file.exists()) file.mkdir();
                return new File(file, "parties.yml");
        }

        private void load() {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
                parties.clear();
                for (String key : config.getKeys(false)) {
                        parties.put(key, config.getString(key));
                }
        }

        private void save() {
                YamlConfiguration config = new YamlConfiguration();
                for (Map.Entry<String, String> entry : parties.entrySet()) {
                        config.set(entry.getKey(), entry.getValue());
                }
                try {
                        config.save(getSaveFile());
                } catch (IOException ioe) {
                        getPlugin().getLogger().warning("[Party] Save failed");
                }
        }
}
