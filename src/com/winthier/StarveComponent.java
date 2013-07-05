/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright 2013 StarTux
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
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class StarveComponent extends AbstractComponent {
        private List<Player> targets = new ArrayList<Player>();

        public StarveComponent(WinthierPlugin plugin) {
                super(plugin, "starve");
        }

        public void onEnable() {
        }

        @CommandHandler(description = "Drain player's hunger", usage = "/<command> [player|*]", permission = "winthier.starve", permissionDefault = "op")
        public boolean starve(CommandSender sender, Command command, String token, String[] args) {
                Player player = null;
                if (sender instanceof Player) player = (Player)sender;
                targets.clear();
                if (args.length == 0) {
                        if (player == null) return false;
                        targets.add(player);
                } else {
                        for (String arg : args) {
                                if (arg.equals("*")) {
                                        for (Player target : getPlugin().getServer().getOnlinePlayers()) {
                                                targets.add(target);
                                        }
                                } else if (arg.startsWith("#")) {
                                        if (player == null) {
                                                sender.sendMessage("Player expected for radius parameter: " + arg);
                                                return true;
                                        }
                                        int radius = 0;
                                        try {
                                                radius = Integer.parseInt(arg.substring(1));
                                        } catch (NumberFormatException e) {
                                                sender.sendMessage("" + ChatColor.RED + "Number expected after hashtag: " + arg);
                                                return true;
                                        }
                                        final int radiusSquared = radius * radius;
                                        for (Player other : getPlugin().getServer().getOnlinePlayers()) {
                                                if (player != other && other.getWorld() == player.getWorld() && player.getLocation().distanceSquared(other.getLocation()) <= radiusSquared) {
                                                        targets.add(other);
                                                }
                                        }
                                } else {
                                        Player target = getPlugin().getServer().getPlayer(arg);
                                        if (target == null) {
                                                sender.sendMessage("" + ChatColor.RED + "Player not found: " + arg);
                                                return true;
                                        }
                                        targets.add(target);
                                }
                        }
                }
                if (targets.isEmpty()) {
                        sender.sendMessage("" + ChatColor.RED + "Nobody's hungry.");
                        return true;
                }
                for (Player target : targets) {
                        target.setFoodLevel(0);
                        sender.sendMessage("" + ChatColor.GREEN + "Drained " + target.getName() + "'s hunger bar.");
                }
                return true;
        }
}
