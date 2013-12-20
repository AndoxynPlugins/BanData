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
import net.daboross.bukkitdev.playerdata.api.PermissionsHelper;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class BanClearCommand extends SubCommand {

    private final BanDataPlugin plugin;

    public BanClearCommand(BanDataPlugin plugin) {
        super("clearban", true, "bandata.admin.clearban", "Clears the last ban off of a Player's ban record.");
        addArgumentNames("Player");
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length < 1) {
            sender.sendMessage(ColorList.ERR + "Please specify a player");
            sender.sendMessage(getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        if (subCommandArgs.length > 1) {
            sender.sendMessage(ColorList.ERR + "To many arguments");
            sender.sendMessage(getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        PlayerData pd = plugin.getPlayerData().getHandler().getPlayerDataPartial(subCommandArgs[0]);
        if (pd == null) {
            sender.sendMessage(ColorList.ERR + "Player " + ColorList.ERR_ARGS + subCommandArgs[0] + ColorList.ERR + " not found");
            return;
        }
        if (PermissionsHelper.userInGroup(pd.getUsername(), "Banned")) {
            sender.sendMessage(ColorList.ERR_ARGS + pd.getUsername() + ColorList.ERR + " is currently banned");
            return;
        }
        String[] data = pd.getExtraData("bandata");
        if (data == null) {
            sender.sendMessage(ColorList.ERR + "No BanData found for " + ColorList.ERR_ARGS + pd.getUsername());
            return;
        }
        BData banData = plugin.getParser().parseFromlist(data);
        if (banData == null) {
            sender.sendMessage(ColorList.ERR + "Error parsing BanData");
            return;
        }
        Ban[] bans = banData.getBans();
        if (bans.length == 1) {
            pd.removeExtraData("bandata");
            sender.sendMessage(ColorList.NAME + pd.getUsername() + ColorList.REG + "'s ban record has been cleared");
        } else {
            Ban[] newBans = new Ban[bans.length - 1];
            System.arraycopy(bans, 0, newBans, 0, bans.length - 1);
            BData newBanData = new BData(newBans);
            String[] newData = plugin.getParser().parseToList(newBanData);
            pd.addExtraData("bandata", newData);
            sender.sendMessage(ColorList.NAME + pd.getUsername() + ColorList.REG + "'s last ban has been cleared.");
        }
    }
}
