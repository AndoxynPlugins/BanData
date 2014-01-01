/*
 * Copyright (C) 2013-2014 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.bukkitdev.bandata.commandreactors;

import net.daboross.bukkitdev.bandata.BanDataPlugin;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CheckBansCommand extends SubCommand {

    private final BanDataPlugin plugin;

    public CheckBansCommand(BanDataPlugin plugin) {
        super("checkBans", true, "bandata.admin", "Checks for players who have been banned but are not in the databases");
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender cs, Command cmnd, String string, String string1, String[] strings) {
        plugin.getBanCheckReloader().goThrough();
    }
}
