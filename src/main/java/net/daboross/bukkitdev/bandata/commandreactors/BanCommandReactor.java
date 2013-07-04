package net.daboross.bukkitdev.bandata.commandreactors;

import net.daboross.bukkitdev.bandata.BData;
import net.daboross.bukkitdev.bandata.Ban;
import net.daboross.bukkitdev.bandata.DataParser;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.SubCommandHandler;
import net.daboross.bukkitdev.playerdata.Data;
import net.daboross.bukkitdev.playerdata.PlayerDataImpl;
import net.daboross.bukkitdev.playerdata.PlayerDataBukkit;
import net.daboross.bukkitdev.playerdata.PlayerDataHandler;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.daboross.bukkitdev.playerdata.api.PlayerHandler;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author daboross
 */
public class BanCommandReactor implements SubCommandHandler {

    private final PlayerHandler playerHandler;

    public BanCommandReactor(PlayerHandler ph) {
        this.playerHandler = ph;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length < 2) {
            sender.sendMessage(ColorList.ERR + "Please specify a player name and a ban reason");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        if (PlayerDataBukkit.isVaultLoaded()) {
            if (playerHandler.getPlayerData(subCommandArgs[0]) == null) {
                sender.sendMessage(ColorList.REG + "No player who's full name matches " + ColorList.NAME + subCommandArgs[0] + ColorList.REG + " was found.");
                String fullUserName = playerHandler.getFullUsername(subCommandArgs[0]);
                if (fullUserName != null) {
                    sender.sendMessage(ColorList.REG + "Did you mean " + ColorList.NAME + fullUserName + ColorList.REG + "?");
                }
                return;
            }
            Permission p = PlayerDataBukkit.getPermissionHandler();
            String playerToBanUserName = playerHandler.getFullUsername(subCommandArgs[0]);
            PlayerData playerToBanPData = playerHandler.getPlayerDataPartial(playerToBanUserName);
            StringBuilder reasonBuilder = new StringBuilder(subCommandArgs[1]);
            for (int i = 2; i < subCommandArgs.length; i++) {
                reasonBuilder.append(" ").append(subCommandArgs[i]);
            }
            String reason = reasonBuilder.toString();
            sender.sendMessage(ColorList.REG + "Banning " + ColorList.NAME + playerToBanUserName + ColorList.REG + " for " + ColorList.DATA + reason);
            String[] oldGroups = p.getPlayerGroups((String)null, playerToBanUserName);
            if (oldGroups == null) {
                oldGroups = new String[]{"Basic"};
            } else if (oldGroups.length < 2) {
                if (oldGroups.length == 0 || oldGroups[0].equalsIgnoreCase("Unknown")) {
                    oldGroups = new String[]{"Basic"};
                }
            }
            for (String group : oldGroups) {
                PlayerDataBukkit.getPermissionHandler().playerRemoveGroup((String) null, playerToBanUserName, group);
            }
            PlayerDataBukkit.getPermissionHandler().playerAddGroup((String) null, playerToBanUserName, "Banned");
            Ban ban;
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Location loc = player.getLocation();
                ban = new Ban(player.getName(), reason, oldGroups, (long) loc.getX(), (long) loc.getY(), (long) loc.getZ(), loc.getWorld().getName(), System.currentTimeMillis());
            } else {
                ban = new Ban(reason, oldGroups, System.currentTimeMillis());

            }
            Data rawData = playerHandler.getCustomData(playerToBanUserName, "bandata");
            BData banData;
            if (rawData == null) {
                Ban[] banList = new Ban[]{ban};
                banData = new BData(banList, playerHandler.getPlayerDataPartial(playerToBanUserName));
            } else {
                banData = DataParser.parseFromlist(rawData);
                banData.addBan(ban);
            }
            String[] newRawBanData = DataParser.parseToList(banData);
            Data banDataToSet = new Data("bandata", newRawBanData);
            playerHandler.addCustomData(playerToBanUserName, banDataToSet);
            Bukkit.getServer().broadcastMessage(String.format(ColorList.BROADCAST_NAME_FORMAT, "BanData") + ColorList.NAME + playerToBanUserName + ColorList.BROADCAST + " was just banned for " + ColorList.DATA + reason + ColorList.BROADCAST + " by " + ColorList.NAME + sender.getName());
        } else {
            sender.sendMessage(ColorList.ERR + "Permission Handler not found");
        }
    }
}
