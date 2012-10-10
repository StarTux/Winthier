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
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Display death messages only from players with the permission
 * "winthier.deathmessage.visible" and respect players ignoring
 * you.
 */
public class DeathMessageComponent extends AbstractComponent implements Listener {
        private ChatColor msgColor;

        public DeathMessageComponent(WinthierPlugin plugin) {
                super(plugin, "deathmessage");
                getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
        }

        @Override
        public void loadConfiguration() {
                msgColor = ChatColor.getByChar(getConfig().getString("Color"));
                if (msgColor == null) msgColor = ChatColor.WHITE;
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onPlayerDeath(PlayerDeathEvent event) {
                String msg = event.getDeathMessage();
                getPlugin().getLogger().info(msg);
                event.setDeathMessage("");
                if (event.getEntity().hasPermission("winthier.deathmessage.visible")) {
                        Collection<Player> recipients = new HashSet<Player>(Arrays.asList(getPlugin().getServer().getOnlinePlayers()));
                        IgnoreComponent.getInstance().filterRecipients(event.getEntity().getName(), recipients);
                        for (Player recipient : recipients) {
                                recipient.sendMessage(msgColor + msg);
                        }
                }
        }
}
