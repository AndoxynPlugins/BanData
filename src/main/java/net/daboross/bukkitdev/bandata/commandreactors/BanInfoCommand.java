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

import net.daboross.bukkitdev.bandata.BData;
import net.daboross.bukkitdev.bandata.BanDataPlugin;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class BanInfoCommand extends SubCommand {

    private final BanDataPlugin plugin;

    public BanInfoCommand(BanDataPlugin plugin) {
        super("baninfo", true, "bandata.baninfo", "Views ban info on a player");
        addAliases("bi", "i");
        addArgumentNames("Player");
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length < 1) {
            sender.sendMessage(ColorList.ERR + "Please specify a player to get info for");
            sender.sendMessage(getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        if (subCommandArgs.length > 2) {
            sender.sendMessage(ColorList.ERR + "Please use only one word and one number after " + ColorList.CMD + "/" + baseCommandLabel + " " + ColorList.SUBCMD + subCommandLabel);
            sender.sendMessage(getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        PlayerData playerData = plugin.getPlayerData().getHandler().getPlayerDataPartial(subCommandArgs[0]);
        if (playerData == null) {
            sender.sendMessage(ColorList.REG + "The player " + ColorList.NAME + subCommandArgs[0] + ColorList.REG + " was not found.");
            return;
        }
        String[] rawData = playerData.getExtraData("bandata");
        if (rawData == null) {
            sender.sendMessage(ColorList.REG + "Found no ban data for Player " + ColorList.NAME + playerData.getUsername() + ColorList.REG + ".");
            return;
        }
        BData banData = plugin.getParser().parseFromlist(rawData);
        int number = -1;
        if (subCommandArgs.length < 2) {
            if (banData.getBans().length < 2) {
                number = 1;
            } else {
                sender.sendMessage(plugin.getInfo().shortInfo("bandata", rawData, playerData));
                sender.sendMessage(ColorList.REG + "Type " + ColorList.CMD + "/" + baseCommandLabel + " " + ColorList.SUBCMD + subCommandLabel + " " + ColorList.ARGS + subCommandArgs[0] + " <1-" + (banData.getBans().length) + ">" + ColorList.REG + " for info on a ban");
                return;
            }
        }
        if (number == -1) {
            try {
                number = Integer.valueOf(subCommandArgs[1]);
            } catch (Exception e) {
                sender.sendMessage(ColorList.ERR_ARGS + subCommandArgs[1] + ColorList.ERR + " is not an integer.");
                sender.sendMessage(getHelpMessage(baseCommandLabel, subCommandLabel));
                return;
            }
        }
        sender.sendMessage(plugin.getInfo().banInfo(banData, playerData, number - 1));
    }
}
