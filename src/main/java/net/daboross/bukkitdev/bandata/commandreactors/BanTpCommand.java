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
import net.daboross.bukkitdev.bandata.Ban;
import net.daboross.bukkitdev.bandata.BanDataPlugin;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanTpCommand extends SubCommand {

    private final BanDataPlugin plugin;

    public BanTpCommand(BanDataPlugin plugin) {
        super("bantp", false, "bandata.bantp", "Teleports you to where you were last banned");
        addAliases("tp", "tpban");
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
            sender.sendMessage(ColorList.ERR + "Please use only one word and a number after " + ColorList.CMD + "/" + baseCommandLabel + " " + ColorList.SUBCMD + subCommandLabel + ColorList.ERR + ".");
            sender.sendMessage(getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        PlayerData playerData = plugin.getPlayerData().getHandler().getPlayerDataPartial(subCommandArgs[0]);
        if (playerData == null) {
            sender.sendMessage(ColorList.ERR + "Player " + ColorList.ERR_ARGS + subCommandArgs[0] + ColorList.ERR + " not found");
            return;
        }

        String[] rawData = playerData.getExtraData("bandata");
        if (rawData == null) {
            sender.sendMessage(ColorList.ERR + "Found no ban data for player " + ColorList.NAME + playerData.getUsername());
            return;
        }
        BData banData = plugin.getParser().parseFromlist(rawData);
        int number = -1;
        if (subCommandArgs.length < 2) {
            if (banData.getBans().length < 2) {
                number = 1;
            } else {
                sender.sendMessage(plugin.getInfo().shortInfo("bandata", rawData, playerData));
                sender.sendMessage(ColorList.REG + "Type " + ColorList.CMD + "/" + baseCommandLabel + " " + ColorList.SUBCMD + subCommandLabel + " " + ColorList.ARGS + subCommandArgs[0] + " <1-" + (banData.getBans().length) + ">" + ColorList.REG + " for information on a ban.");
                return;
            }
        }
        if (number == -1) {
            try {
                number = Integer.valueOf(subCommandArgs[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ColorList.ERR_ARGS + subCommandArgs[1] + ColorList.ERR + " is not an integer.");
                sender.sendMessage(getHelpMessage(baseCommandLabel, subCommandLabel));
                return;
            }
        }
        if (number < 1) {
            sender.sendMessage(ColorList.ERR_ARGS + subCommandArgs[1] + ColorList.ERR + " is not a non-0 positive integer.");
        }
        if (banData.getBans()[number - 1].isConsoleBan()) {
            sender.sendMessage(ColorList.ERR + "Ban number " + ColorList.DATA + number + ColorList.ERR + " for player " + ColorList.NAME + playerData.getUsername() + ColorList.ERR + " does not have a position associated with it.");
            return;
        }
        Player player = (Player) sender;
        Ban ban = banData.getBans()[number - 1];
        World world = Bukkit.getServer().getWorld(ban.getWorld());
        if (world == null) {
            sender.sendMessage(ColorList.ERR + "Could not find the world associated with ban number " + ColorList.DATA + number + ColorList.ERR + " for player " + ColorList.NAME + playerData.getUsername());
            return;
        }
        sender.sendMessage(ColorList.REG + "Teleporting you to the position associated with ban number " + ColorList.DATA + number + ColorList.REG + " for player " + ColorList.NAME + playerData.getUsername());
        Location loc = new Location(world, (double) ban.getXPos(), (double) ban.getYPos(), (double) ban.getZPos());
        player.teleport(loc);
        sender.sendMessage(plugin.getInfo().banInfo(banData, playerData, number - 1));
        sender.sendMessage(ColorList.REG + "To see where " + ColorList.NAME + playerData.getUsername() + ColorList.REG + " was banned type " + ColorList.CMD + "/bd " + ColorList.SUBCMD + "tp " + ColorList.NAME + playerData.getUsername() + " " + ColorList.ARGS + (number - 1));
    }
}
