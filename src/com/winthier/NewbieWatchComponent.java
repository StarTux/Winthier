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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Watch first few block breaks of new players for malicious
 * activities.
 * Idea by hkenneth.
 */
public class NewbieWatchComponent extends AbstractComponent implements Listener {
        private Map<String, Integer> blockBreakCount = new HashMap<String, Integer>();
        private int maxBlockBreaks;
        private Set<Material> blockBreaks = new HashSet<Material>();
        private VariableMessage blockBreakMessage;

        public NewbieWatchComponent(WinthierPlugin plugin) {
                super(plugin, "newbiewatch");
                getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
        }

        @Override
        public void loadConfiguration() {
                maxBlockBreaks = getConfig().getInt("MaxBlockBreaks", 100);
                blockBreaks.clear();
                for (String blockName : getConfig().getStringList("BlockBreaks")) {
                        Material mat = null;
                        try {
                                int id = Integer.parseInt(blockName);
                                mat = Material.getMaterial(id);
                                continue;
                        } catch (NumberFormatException nfe) {}
                        mat = Material.getMaterial(blockName.toUpperCase().replace("-", "_"));
                        if (mat == null) {
                                getPlugin().getLogger().warning("[NewbieWatch] Unrecognized block type: " + blockName);
                        } else if (!mat.isBlock()) {
                                getPlugin().getLogger().warning("[NewbieWatch] Not a block: " + blockName);
                        } else {
                                blockBreaks.add(mat);
                        }
                }
                blockBreakMessage = new VariableStringFilter().parseMessage(getConfig().getString("BlockBreakMessage"));
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
        public void onPlayerJoin(PlayerJoinEvent event) {
                if (!event.getPlayer().hasPlayedBefore()) {
                        blockBreakCount.put(event.getPlayer().getName(), 0);
                }
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
        public void onBlockBreak(BlockBreakEvent event) {
                Integer count = blockBreakCount.get(event.getPlayer().getName());
                if (count == null) return;
                if (count + 1 > maxBlockBreaks) {
                        blockBreakCount.remove(event.getPlayer().getName());
                        return;
                }
                blockBreakCount.put(event.getPlayer().getName(), count + 1);
                if (blockBreaks.contains(event.getBlock().getType())) {
                        blockBreakMessage.setVariable("player", event.getPlayer().getName());
                        String blockName = event.getBlock().getType().name();
                        blockBreakMessage.setVariable("block", "" + event.getBlock().getType().name().toLowerCase().replace("_", " "));
                        blockBreakMessage.setVariable("position", String.format("%d,%d,%d", event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ()));
                        blockBreakMessage.setVariable("x", "" + event.getBlock().getX());
                        blockBreakMessage.setVariable("y", "" + event.getBlock().getY());
                        blockBreakMessage.setVariable("z", "" + event.getBlock().getZ());
                        getPlugin().getLogger().warning("[NewbieWatch] " + blockBreakMessage.toStringNoColor());
                        for (Player player : getPlugin().getServer().getOnlinePlayers()) {
                                if (player.hasPermission("winthier.newbiewatch.notify")) {
                                        blockBreakMessage.sendTo(player);
                                }
                        }
                }
        }
}
