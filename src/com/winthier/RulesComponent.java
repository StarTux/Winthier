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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RulesComponent extends AbstractComponent {
        private List<String> rules = new ArrayList<String>();
        private Random rnd = new Random(System.currentTimeMillis());
        private String passwordLine;

        public RulesComponent(WinthierPlugin plugin) {
                super(plugin, "rules");
        }

        @Override
        public void onEnable() {
        }

        @Override
        public void loadConfiguration() {
                rules.clear();
                StringFilter filter = new ColorStringFilter();
                for (String line : getConfig().getStringList("Rules")) {
                        rules.add(filter.replace(line));
                }
                passwordLine = getConfig().getString("PasswordLine");
        }

        public String getPasswordLine(Player player) {
                StringFilter filter = new ColorStringFilter();
                filter.addReplacer(new FixedStringReplacer("{password}", PasswordComponent.getInstance().getPassword(player.getName())));
                return filter.replace(passwordLine);
        }

        @CommandHandler(permission = "winthier.rules", permissionDefault = "true", description = "Read the rules", usage = "/<command>")
        public boolean rules(CommandSender sender, Command command, String token, String[] args) {
                if (args.length != 0) return false;
                if (sender instanceof Player) {
                        Player player = (Player)sender;
                        if (PasswordComponent.getInstance().isFromGroup(player)) {
                                int linum = rnd.nextInt(rules.size());
                                for (int i = 0; i < rules.size(); ++i) {
                                        sender.sendMessage(rules.get(i));
                                        if (i == linum) sender.sendMessage(getPasswordLine(player));
                                }
                                return true;
                        }
                }
                for (String rule : rules) {
                        sender.sendMessage(rule);
                }
                return true;
        }
}
