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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class MessageComponent extends AbstractComponent implements Listener {
        private MessageNode root = new MessageNode();
        private StringFilter filter = new ColorStringFilter();

        public MessageComponent(WinthierPlugin plugin) {
                super(plugin, "message");
                root.errorMessage = "";
        }

        @Override
        public void enable() {
                getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
        }

        @Override
        public void loadConfiguration() {
                root.subNodes.clear();
                configureNode(root, getConfig());
        }

        private void configureNode(MessageNode node, ConfigurationSection conf) {
                if (node != root) {
                        List<String> message = conf.getStringList("Message");
                        if (message != null) {
                                node.message = filter.replace(message);
                        }
                }
                String errorMessage = conf.getString("ErrorMessage", null);
                if (errorMessage != null) {
                        node.errorMessage = filter.replace(errorMessage);
                }
                String permission = conf.getString("Permission", null);
                if (permission != null) {
                        node.permission = permission;
                }
                String overridePermission = conf.getString("OverridePermission", null);
                if (overridePermission != null) {
                        node.overridePermission = overridePermission;
                }
                List<String> aliases = conf.getStringList("Aliases");
                if (aliases != null) {
                        node.aliases = aliases;
                }
                for (String key : conf.getKeys(false)) {
                        if (!Character.isUpperCase(key.charAt(0))) {
                                MessageNode newNode = new MessageNode(node);
                                configureNode(newNode, conf.getConfigurationSection(key));
                                node.subNodes.put(key.toLowerCase(), newNode);
                                if (newNode.aliases != null) {
                                        for (String alias : newNode.aliases) {
                                                node.subNodes.put(alias.toLowerCase(), newNode);
                                        }
                                }
                        }
                }
        }

        private static String getErrorMessage(MessageNode node) {
                while (node.errorMessage == null) node = node.parent;
                return node.errorMessage;
        }

        /**
         * @return true if the command was recognized, false otherwise.
         */
        public boolean onCommand(CommandSender sender, String message) {
                String[] tokens = message.split(" +");
                if (tokens.length == 0) return false;
                MessageNode node = root;
                for (String token : tokens) {
                        if (node.overridePermission != null && sender.hasPermission(node.overridePermission)) return false;
                        MessageNode nextNode = node.subNodes.get(token.toLowerCase());
                        if (nextNode == null) {
                                if (node == root) return false;
                                sender.sendMessage(getErrorMessage(node));
                                return true;
                        }
                        node = nextNode;
                }
                if (node.overridePermission != null && sender.hasPermission(node.overridePermission)) return false;
                if (node.message == null) {
                        sender.sendMessage(getErrorMessage(node));
                } else if (node.permission != null && !sender.hasPermission(node.permission)) {
                        sender.sendMessage("" + ChatColor.RED + "You don't have permission");
                } else {
                        for (String line : node.message) sender.sendMessage(line);
                }
                return true;
        }

        @EventHandler(ignoreCancelled = true)
        public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
                if (onCommand(event.getPlayer(), event.getMessage().substring(1))) {
                        getPlugin().getLogger().info(event.getPlayer().getName() + " issued command: " + event.getMessage().trim());
                        event.setCancelled(true);
                }
        }

        @EventHandler
        public void onServerCommand(ServerCommandEvent event) {
                if (onCommand(event.getSender(), event.getCommand())) {
                        event.setCommand("winthier devnull");
                }
        }
}

class MessageNode {
        public Map<String, MessageNode> subNodes = new LinkedHashMap<String, MessageNode>();
        public List<String> message;
        public List<String> aliases;
        public String errorMessage;
        public MessageNode parent = null;
        public String permission = null;
        public String overridePermission = null;
        public MessageNode() {}
        public MessageNode(MessageNode parent) {
                this.parent = parent;
        }
}
