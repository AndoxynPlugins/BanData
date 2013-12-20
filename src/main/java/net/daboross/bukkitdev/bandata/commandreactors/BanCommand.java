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
import net.daboross.bukkitdev.playerdata.api.PlayerDataStatic;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanCommand extends SubCommand {

    private final BanDataPlugin plugin;

    public BanCommand(BanDataPlugin plugin) {
        super("ban", true, "bandata.ban", "Bans a player and records ban information.");
        addArgumentNames("Player", "Reason");
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length < 2) {
            sender.sendMessage(ColorList.ERR + "Please specify a player name and a ban reason");
            sender.sendMessage(getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        if (PlayerDataStatic.isPermissionLoaded()) {
            PlayerData playerToBan = plugin.getPlayerData().getHandler().getPlayerDataPartial(subCommandArgs[0]);
            if (playerToBan == null) {
                sender.sendMessage(ColorList.REG + "No player who's full name matches " + ColorList.NAME + subCommandArgs[0] + ColorList.REG + " was found.");
                return;
            } else if (playerToBan.getUsername().equalsIgnoreCase(subCommandArgs[0])) {
                sender.sendMessage(ColorList.REG + "No player who's full name matches " + ColorList.NAME + subCommandArgs[0] + ColorList.REG + " was found.");
                sender.sendMessage(ColorList.REG + "Did you mean " + ColorList.NAME + playerToBan.getUsername() + ColorList.REG + "?");
                return;
            }
            Permission p = PlayerDataStatic.getPermissionHandler();
            StringBuilder reasonBuilder = new StringBuilder(subCommandArgs[1]);
            for (int i = 2; i < subCommandArgs.length; i++) {
                reasonBuilder.append(" ").append(subCommandArgs[i]);
            }
            String reason = reasonBuilder.toString();
            sender.sendMessage(ColorList.REG + "Banning " + ColorList.NAME + playerToBan.getUsername() + ColorList.REG + " for " + ColorList.DATA + reason);
            String[] oldGroups = p.getPlayerGroups((String) null, playerToBan.getUsername());
            if (oldGroups == null) {
                oldGroups = new String[]{
                        "Basic"
                };
            } else if (oldGroups.length < 2) {
                if (oldGroups.length == 0 || oldGroups[0].equalsIgnoreCase("Unknown")) {
                    oldGroups = new String[]{
                            "Basic"
                    };
                }
            }
            for (String group : oldGroups) {
                PlayerDataStatic.getPermissionHandler().playerRemoveGroup((String) null, playerToBan.getUsername(), group);
            }
            PlayerDataStatic.getPermissionHandler().playerAddGroup((String) null, playerToBan.getUsername(), "Banned");
            Ban ban;
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Location loc = player.getLocation();
                ban = new Ban(player.getName(), reason, oldGroups, (long) loc.getX(), (long) loc.getY(), (long) loc.getZ(), loc.getWorld().getName(), System.currentTimeMillis());
            } else {
                ban = new Ban(reason, oldGroups, System.currentTimeMillis());
            }
            String[] rawData = playerToBan.getExtraData("bandata");
            BData banData;
            if (rawData == null) {
                Ban[] banList = new Ban[]{
                        ban
                };
                banData = new BData(banList);
            } else {
                banData = plugin.getParser().parseFromlist(rawData);
                banData.addBan(ban);
            }
            String[] newRawBanData = plugin.getParser().parseToList(banData);
            playerToBan.addExtraData("bandata", newRawBanData);
            Bukkit.getServer().broadcastMessage(String.format(ColorList.BROADCAST_NAME_FORMAT, "BanData") + ColorList.NAME + playerToBan.getUsername() + ColorList.BROADCAST + " was just banned for " + ColorList.DATA + reason + ColorList.BROADCAST + " by " + ColorList.NAME + sender.getName());
        } else {
            sender.sendMessage(ColorList.ERR + "Permission handler not found");
        }
    }
}
