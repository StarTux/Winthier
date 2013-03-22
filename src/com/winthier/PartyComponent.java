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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PartyComponent extends AbstractComponent {
        private Map<String, String> parties = new HashMap<String, String>();
        private VariableMessage messageFormat;

        public PartyComponent(WinthierPlugin plugin) {
                super(plugin, "party");
        }

        @Override
        public void onEnable() {
                load();
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
                } else if (args.length == 1) {
                        String party = args[0];
                        String oldParty = parties.get(sender.getName());
                        if (oldParty != null) {
                                for (Player player : getPartyPlayers(oldParty)) {
                                        if (!player.equals(sender)) {
                                                sendMessage(player, "&7" + sender.getName() + " left party &b" + oldParty + "&7.");
                                        }
                                }
                        }
                        if (party.equals("q")) {
                                parties.remove(sender.getName());
                                sendMessage(sender, "&7You left the party.");
                        } else {
                                parties.put(sender.getName(), party);
                                sendMessage(sender, "&7You joined party &b" + party + "&7.");
                                for (Player player : getPartyPlayers(party)) {
                                        if (!player.equals(sender)) {
                                                sendMessage(player, "&7" + sender.getName() + " joined party &b" + party + "&7.");
                                        }
                                }
                        }
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
                if (args.length > 0) {
                        String party = parties.get(sender.getName());
                        if (party == null) {
                                sendMessage(sender, "&cYou are not in a party.");
                                return true;
                        }
                        StringBuilder sb = new StringBuilder(args[0]);
                        for (int i = 1; i < args.length; ++i) {
                                sb.append(" ").append(args[i]);
                        }
                        messageFormat.setVariable("party", party);
                        messageFormat.setVariable("sender", sender.getName());
                        messageFormat.setVariable("message", sb.toString());
                        String msg = messageFormat.toString();
                        for (Player player : getPartyPlayers(party)) {
                                player.sendMessage(msg);
                        }
                        getPlugin().getLogger().info("[Party] [" + party + "] " + sender.getName() + ": " + sb.toString());
                        return true;
                } else {
                        return false;
                }
        }

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
