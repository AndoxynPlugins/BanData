package net.daboross.bukkitdev.bandata.commandreactors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import ru.tehkode.permissions.PermissionUser;

/**
 *
 * @author daboross
 */
public class UnBanCommandReactor implements CommandExecutorBase.CommandReactor {

    private final PlayerDataHandler playerDataHandler;

    public UnBanCommandReactor(PlayerDataHandler playerDataHandler) {
        this.playerDataHandler = playerDataHandler;
    }

    public void runCommand(CommandSender sender, Command mainCommand, String mainCommandLabel, String subCommand, String subCommandLabel, String[] subCommandArgs, CommandExecutorBase.CommandExecutorBridge executorBridge) {
        if (subCommandArgs.length < 1) {
            sender.sendMessage(ColorList.ILLEGALARGUMENT + "Please Specify a Player!");
            sender.sendMessage(executorBridge.getHelpMessage(subCommandLabel, mainCommandLabel));
            return;
        }
        if (subCommandArgs.length > 1) {
            sender.sendMessage(ColorList.ILLEGALARGUMENT + "To Many Arguments!");
            sender.sendMessage(executorBridge.getHelpMessage(subCommandLabel, mainCommandLabel));
            return;
        }
        PData pData = playerDataHandler.getPData(subCommandArgs[0]);
        if (pData == null) {
            sender.sendMessage(ColorList.ERROR + "Player: " + ColorList.ERROR_ARGS + subCommandArgs[0] + ColorList.ERROR + " not found!");
            return;
        }
        if (!pData.isGroup("banned")) {
            sender.sendMessage(ColorList.ERROR_ARGS + pData.userName() + ColorList.ERROR + " is not currently banned!");
            return;
        }
        Data data = pData.getData("bandata");
        if (data == null) {
            sender.sendMessage(ColorList.ERROR + "No bandata found for " + ColorList.ERROR_ARGS + pData.userName());
            return;
        }
        BData banData = DataParser.parseFromlist(data);
        if (banData == null) {
            sender.sendMessage(ColorList.ERROR + "Error parsing BanData!");
            return;
        }
        Ban[] bans = banData.getBans();
        String[] permissionGroupsToSet = null;
        for (int i = bans.length - 1; i >= 0; i--) {
            Ban b = bans[i];
            String[] oldGroups = b.getOldGroups();
            if (oldGroups.length == 0) {
                continue;
            }
            List<String> groups = new ArrayList<String>();
            for (String str : oldGroups) {
                if (!(str.equalsIgnoreCase("banned")) || (groups.contains(str))) {
                    groups.add(str);
                }
            }
            if (!groups.isEmpty()) {
                permissionGroupsToSet = groups.toArray(new String[groups.size()]);
                break;
            }
        }
        if (permissionGroupsToSet == null) {
            sender.sendMessage(ColorList.ERROR + "Error parsing BanData! No Previous Groups Found!");
            return;
        }
        PermissionUser permissionUser = pData.getPermUser();
        if (!(PlayerData.isPEX() && permissionUser != null)) {
            sender.sendMessage(ColorList.ERROR + "PEX Not Loaded!");
        }
        List<String> rawData;
        if (pData.hasData("rankrecord")) {
            rawData = new ArrayList<String>(Arrays.asList(pData.getData("rankrecord").getData()));
        } else {
            rawData = new ArrayList<String>();
        }
        permissionUser.setGroups(permissionGroupsToSet);
        rawData.add("SET " + sender.getName() + " " + permissionGroupsToSet + " " + System.currentTimeMillis());
        Data finalData = new Data("rankrecord", rawData.toArray(new String[rawData.size()]));
        pData.addData(finalData);
        Bukkit.getServer().broadcastMessage(ColorList.getBroadcastName("BanData") + " " + ColorList.NAME + pData.userName() + ColorList.BROADCAST + " was unbanned by " + ColorList.NAME + sender.getName());
        sender.sendMessage(ColorList.NAME + pData.userName() + " has been set to: " + getString(permissionGroupsToSet));
    }

    private String getString(String[] strings) {
        if (strings.length == 0) {
            return "None";
        }
        StringBuilder resultBuilder = new StringBuilder(strings[0]);
        for (int i = 1; i < strings.length; i++) {
            String string = strings[i];
            resultBuilder.append(" ,").append(string);
        }
        return resultBuilder.toString();
    }
}
