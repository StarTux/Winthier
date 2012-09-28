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

import org.bukkit.configuration.ConfigurationSection;

public abstract class AbstractComponent implements Component {
        private WinthierPlugin plugin;
        private String name;

        public AbstractComponent(WinthierPlugin plugin, String name) {
                this.plugin = plugin;
                this.name = name;
        }

        @Override
        public void enable() {}

        @Override
        public void disable() {}

        @Override
        public void loadConfiguration() {}

        @Override
        public void reloadConfiguration() {
                loadConfiguration();
        }

        @Override
        public void saveConfiguration() {}

        protected WinthierPlugin getPlugin() {
                return plugin;
        }

        protected ConfigurationSection getConfig() {
                return plugin.getConfig().getConfigurationSection("components." + name);
        }

        public String getName() {
                return name;
        }
}
