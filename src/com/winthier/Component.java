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

public interface Component {
        /**
         * Called when the Component is enabled.
         */
        public void enable();

        /**
         * Called when the Component is disabled.
         */
        public void disable();

        /**
         * Called when the configuration is to be loaded.
         */
        public void loadConfiguration();

        /**
         * Called when the configuration is to be reloaded.
         */
        public void reloadConfiguration();

        /**
         * Called when the configuration is to be saved; usually
         * before the config.yml is saved to disk.
         */
        public void saveConfiguration();
}
