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

import java.util.Arrays;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PasswordComponent extends AbstractComponent implements CommandExecutor {
        private String fromGroup, toGroup;
        private int passwordLength;
        private static PasswordComponent instance;
        private int salt = new Random(System.currentTimeMillis()).nextInt();
        private VariableMessage congratulationsMessage;
        private VariableMessage announcementMessage;

        public PasswordComponent(WinthierPlugin plugin) {
                super(plugin, "password");
                instance = this;
        }

        public static PasswordComponent getInstance() {
                return instance;
        }

        @Override
        public void enable() {
                getPlugin().getCommand("pw").setExecutor(this);
        }

        @Override
        public void loadConfiguration() {
                fromGroup = getConfig().getString("FromGroup");
                toGroup = getConfig().getString("ToGroup");
                passwordLength = getConfig().getInt("PasswordLength");
                congratulationsMessage = new VariableStringFilter().parseMessage(getConfig().getStringList("messages.Congratulations"));
                announcementMessage = new VariableStringFilter().parseMessage(getConfig().getString("messages.Announcement"));
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String token, String[] args) {
                if (args.length != 1) return false;
                if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.RED + "Player expected!");
                        return true;
                }
                Player player = (Player)sender;
                if (!isFromGroup(player)) {
                        sender.sendMessage(ChatColor.RED + "You are no longer a " + fromGroup + ", silly!");
                        return true;
                }
                String pw = args[0];
                if (!pw.equals(getPassword(player.getName()))) {
                        sender.sendMessage(ChatColor.RED + "Bad password!");
                        return true;
                }
                getPlugin().getPermission().playerRemoveGroup(player, fromGroup);
                getPlugin().getPermission().playerAddGroup(player, toGroup);
                congratulationsMessage.setVariable("player", player.getName());
                congratulationsMessage.setVariable("fromgroup", fromGroup);
                congratulationsMessage.setVariable("togroup", toGroup);
                congratulationsMessage.sendTo(player);
                announcementMessage.setVariable("player", player.getName());
                announcementMessage.setVariable("fromgroup", fromGroup);
                announcementMessage.setVariable("togroup", toGroup);
                getPlugin().getLogger().info("[Password] " + announcementMessage.toStringNoColor());
                String announcement = announcementMessage.toString();
                for (Player recipient : getPlugin().getServer().getOnlinePlayers()) {
                        if (!recipient.hasPermission("winthier.password.notify")) continue;
                        if (recipient.equals(player)) continue;
                        recipient.sendMessage(announcement);
                }
                
                return true;
        }

        public String getPassword(String name) {
                int seed = name.hashCode() * salt;
                Random rnd = new Random((long)seed);
                String pw = "";
                for (int i = 0; i < passwordLength; ++i) {
                        int n = rnd.nextInt(10 + 26);
                        int c = (int)'-';
                        if (n < 10) {
                                c = '0' + n;
                        } else {
                                c = 'a' + (n - 10);
                        }
                        pw += (char)c;
                }
                return pw;
        }

        public boolean isFromGroup(Player player) {
                return Arrays.asList(getPlugin().getPermission().getPlayerGroups(player)).contains(fromGroup);
        }
}
