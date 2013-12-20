/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
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

import java.util.List;
import net.daboross.bukkitdev.bandata.BData;
import net.daboross.bukkitdev.bandata.BanDataPlugin;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class BanMigrateCommand extends SubCommand {

    private final BanDataPlugin plugin;

    public BanMigrateCommand(BanDataPlugin plugin) {
        super("migrate", true, "bandata.banmigrate", "Copy database to SQL");
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        List<? extends PlayerData> banned = plugin.getPlayerData().getHandler().getAllPlayerDatasWithExtraData(("bandata"));
        for (PlayerData pd : banned) {
            BData bans = plugin.getParser().parseFromlist(pd.getExtraData("bandata"));
        }
    }
}