package net.daboross.bukkitdev.bandata.commandreactors;

import net.daboross.bukkitdev.bandata.BData;
import net.daboross.bukkitdev.bandata.Ban;
import net.daboross.bukkitdev.bandata.DataParser;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase;
import net.daboross.bukkitdev.playerdata.Data;
import net.daboross.bukkitdev.playerdata.PData;
import net.daboross.bukkitdev.playerdata.PlayerData;
import net.daboross.bukkitdev.playerdata.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;

/**
 *
 * @author daboross
 */
public class BanCommandReactor implements CommandExecutorBase.CommandReactor {

    private final PlayerDataHandler playerDataHandler;

    public BanCommandReactor(PlayerDataHandler pDataH) {
        this.playerDataHandler = pDataH;
    }

    public void runCommand(CommandSender sender, Command mainCommand, String mainCommandLabel, String subCommand, String subCommandLabel,
            String[] subCommandArgs, CommandExecutorBase.CommandExecutorBridge executorBridge) {
        if (subCommandArgs.length < 2) {
            sender.sendMessage(ColorList.ILLEGALARGUMENT + "Please Specify a Player Name and a Ban Reason!");
            sender.sendMessage(executorBridge.getHelpMessage(subCommandLabel, mainCommandLabel));
            return;
        }
        if (PlayerData.isPEX()) {
            if (!playerDataHandler.doesPlayerExists(subCommandArgs[0])) {
                sender.sendMessage(ColorList.MAIN + "No Player whoes full name matches " + ColorList.NAME + subCommandArgs[0] + ColorList.MAIN + " was found.");
                sender.sendMessage(ColorList.MAIN + "Do To The Nature of this command, please specify the full username of a player.");
                String fullUserName = playerDataHandler.getFullUsername(subCommandArgs[0]);
                if (fullUserName != null) {
                    sender.sendMessage(ColorList.MAIN + "Did you mean " + ColorList.NAME + fullUserName + ColorList.MAIN + "?");
                }
                return;
            }

            String playerToBanUserName = playerDataHandler.getFullUsername(subCommandArgs[0]);
            PData playerToBanPData = playerDataHandler.getPData(playerToBanUserName);
            StringBuilder reasonBuilder = new StringBuilder(subCommandArgs[1]);
            for (int i = 1; i < subCommandArgs.length; i++) {
                reasonBuilder.append(" ").append(subCommandArgs[i]);
            }
            String reason = reasonBuilder.toString();
            sender.sendMessage(ColorList.MAIN + "Banning " + ColorList.NAME + playerToBanUserName + ColorList.MAIN + " for " + ColorList.NUMBER + reason);
            PermissionUser permPlayer = playerToBanPData.getPermUser();
            String[] oldGroups = playerToBanPData.getGroups();
            if (oldGroups == null) {
                oldGroups = new String[]{"Basic"};
            }
            if (oldGroups.length < 2) {
                if (oldGroups.length == 0) {
                    oldGroups = new String[]{"Basic"};
                } else if (oldGroups[0].equals("Unknown")) {
                    oldGroups = new String[]{"Basic"};
                }
            }
            permPlayer.setGroups(new String[]{"Banned"});
            Ban ban;
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Location loc = player.getLocation();
                ban = new Ban(player.getName(), reason, oldGroups, (long) loc.getX(), (long) loc.getY(), (long) loc.getZ(), loc.getWorld().getName(), System.currentTimeMillis());
            } else {
                ban = new Ban(reason, oldGroups, System.currentTimeMillis());

            }
            Data rawData = playerDataHandler.getCustomData(playerToBanUserName, "bandata");
            BData banData;
            if (rawData == null) {
                Ban[] banList = new Ban[]{ban};
                banData = new BData(banList, playerDataHandler.getPData(playerToBanUserName));
            } else {
                banData = DataParser.parseFromlist(rawData);
                banData.addBan(ban);
            }
            String[] newRawBanData = DataParser.parseToList(banData);
            Data banDataToSet = new Data("bandata", newRawBanData);
            playerDataHandler.addCustomData(playerToBanUserName, banDataToSet);
            Bukkit.getServer().broadcastMessage(ColorList.getBroadcastName("BanData") + " " + ColorList.NAME + playerToBanUserName + ColorList.BROADCAST + " was just banned for " + ColorList.NUMBER + reason);
        } else {
            sender.sendMessage(ColorList.ERROR_ARGS + "PermissionsEx" + ColorList.ERROR + " is not loaded");
        }
    }
}
