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

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Display death messages only from players with the permission
 * "winthier.deathmessage.visible" and respect players ignoring
 * you.
 */
public class PlayerMessagesComponent extends AbstractComponent implements Listener {
        private ChatColor deathMsgColor;
        private List<Object> joinMsg;
        private List<Object> leaveMsg;
        private VariableReplacer variables = new VariableReplacer("\\$(\\w+)\\b");

        public PlayerMessagesComponent(WinthierPlugin plugin) {
                super(plugin, "playermessages");
                getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
        }

        @Override
        public void loadConfiguration() {
                deathMsgColor = ChatColor.getByChar(getConfig().getString("DeathMessageColor", "f"));
                StringFilter filter = new ColorStringFilter();
                filter.addReplacer(variables);
                joinMsg = filter.filter(getConfig().getString("JoinMessage"));
                leaveMsg = filter.filter(getConfig().getString("LeaveMessage"));
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerDeath(PlayerDeathEvent event) {
                String msg = event.getDeathMessage();
                getPlugin().getLogger().info(msg);
                event.setDeathMessage("");
                if (event.getEntity().hasPermission("winthier.playermessages.death")) {
                        IgnoreComponent.getInstance().broadcast(event.getEntity(), deathMsgColor + msg);
                }
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerJoin(PlayerJoinEvent event) {
                event.setJoinMessage("");
                if (event.getPlayer().hasPermission("winthier.playermessages.join")) {
                        variables.setVariable("player", event.getPlayer().getName());
                        IgnoreComponent.getInstance().broadcast(event.getPlayer(), ColorStringFilter.build(joinMsg));
                }
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerQuit(PlayerQuitEvent event) {
                        event.setQuitMessage("");
                if (event.getPlayer().hasPermission("winthier.playermessages.leave")) {
                        variables.setVariable("player", event.getPlayer().getName());
                        IgnoreComponent.getInstance().broadcast(event.getPlayer(), ColorStringFilter.build(leaveMsg));
                }
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
        public void onPlayerKick(PlayerKickEvent event) {
                event.setLeaveMessage("");
                if (event.getPlayer().hasPermission("winthier.playermessages.leave")) {
                        variables.setVariable("player", event.getPlayer().getName());
                        IgnoreComponent.getInstance().broadcast(event.getPlayer(), ColorStringFilter.build(leaveMsg));
                }
        }
}
