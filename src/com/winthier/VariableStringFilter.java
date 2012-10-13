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

public class VariableStringFilter extends ConfigurableColorStringFilter {
        private final VariableReplacer variableReplacer;

        public VariableStringFilter() {
                variableReplacer = new VariableReplacer("\\{(\\w+)\\}");
                addReplacer(variableReplacer);
        }

        @Override
        public VariableMessage parseMessage(String input) {
                VariableMessage result = new VariableMessage(variableReplacer);
                result.addLine(filter(input));
                return result;
        }

        @Override
        public VariableMessage parseMessage(List<String> input) {
                VariableMessage result = new VariableMessage(variableReplacer);
                for (String line : input) result.addLine(filter(line));
                return result;
        }
}
