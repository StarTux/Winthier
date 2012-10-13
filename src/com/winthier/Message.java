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

import java.util.LinkedList;
import java.util.List;
import org.bukkit.command.CommandSender;

public class Message {
        private List<List<Object>> lines = new LinkedList<List<Object>>();

        public Message() {}

        public void addLine(List<Object> line) {
                lines.add(line);
        }

        public void sendTo(CommandSender recipient) {
                for (List<Object> line : lines) {
                        recipient.sendMessage(StringFilter.build(line));
                }
        }

        public String toString() {
                StringBuilder sb = new StringBuilder();
                for (List<Object> line : lines) sb.append(StringFilter.build(line));
                return sb.toString();
        }
}
