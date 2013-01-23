package net.daboross.bukkitdev.bandata;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import net.daboross.bukkitdev.playerdata.ColorList;
import net.daboross.bukkitdev.playerdata.Data;
import net.daboross.bukkitdev.playerdata.PlayerData;
import net.daboross.bukkitdev.playerdata.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 *
 * @author daboross
 */
public class BanDataCommandExecutor implements CommandExecutor {

    private final Map<String, String> aliasMap = new HashMap<>();
    private final Map<String, Boolean> isConsoleMap = new HashMap<>();
    private final Map<String, String> helpList = new HashMap<>();
    private final Map<String, String> permMap = new HashMap<>();
    private PlayerData playerDataMain;
    private PlayerDataHandler pDataH;
    private BanData banDataMain;

    /**
     *
     */
    protected BanDataCommandExecutor(BanData bd) {
        this.playerDataMain = bd.getPlayerData();
        this.banDataMain = bd;
        this.pDataH = playerDataMain.getHandler();
        initCommand("?", new String[]{"help"}, true, "bandata.help", "This Command Views This Page");
        initCommand("ban", new String[]{}, true, "bandata.ban",
                (ColorList.ARGS + "<Player> <Reason>" + ColorList.HELP + " Bans A Player With PEX and records Info."));
        initCommand("viewban", new String[]{"vb", "i"}, true, "bandata.viewban",
                (ColorList.ARGS + "<Player>" + ColorList.HELP + " Views Ban Info On a Player"));
        initCommand("bantp", new String[]{"tp", "tpban"}, false, "bandata.bantp", ColorList.ARGS + "<Player>" + ColorList.HELP + " This Command Teleports you to where someone was banned.");
    }

    private void initCommand(String cmd, String[] aliases, boolean isConsole, String permission, String helpString) {
        aliasMap.put(cmd, cmd);
        for (String alias : aliases) {
            aliasMap.put(alias, cmd);
        }
        isConsoleMap.put(cmd, isConsole);
        permMap.put(cmd, permission);
        helpList.put(cmd, helpString);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("bd")) {
            if (args.length < 1) {
                sender.sendMessage(ColorList.MAIN + "This is a base command, Please Use a sub command after it.");
                sender.sendMessage(ColorList.MAIN + "To see all possible sub commands, type " + ColorList.CMD + "/" + cmd.getName() + ColorList.SUBCMD + " ?");
                return true;
            }
            String commandName;
            if (aliasMap.containsKey(args[0].toLowerCase())) {
                commandName = aliasMap.get(args[0].toLowerCase());
                Bukkit.getServer().getLogger().log(Level.INFO, (sender.getName() + " used " + commandName));
            } else {
                sender.sendMessage(ColorList.MAIN + "The SubCommand: " + ColorList.CMD + args[0] + ColorList.MAIN + " Does not exist.");
                sender.sendMessage(ColorList.MAIN + "To see all possible sub commands, type " + ColorList.CMD + "/" + cmd.getName() + ColorList.SUBCMD + " ?");
                return true;
            }
            if (sender instanceof Player) {
                if (!sender.hasPermission(permMap.get(commandName))) {
                    sender.sendMessage(ColorList.NOPERM + "You don't have permission to do this command!");
                    return true;
                }
            }
            boolean isConsole;
            if (isConsoleMap.containsKey(commandName)) {
                isConsole = isConsoleMap.get(commandName);
            } else {
                isConsole = false;
            }
            if (!(sender instanceof Player)) {
                if (!isConsole) {
                    sender.sendMessage(ColorList.NOPERM + "This command must be run by a player");
                    return true;
                }
            }
            if (commandName.equalsIgnoreCase("?")) {
                runHelpCommand(sender, cmd, getSubArray(args));
            } else if (commandName.equalsIgnoreCase("ban")) {
                runBanCommand(sender, cmd, args[0], getSubArray(args));
            } else if (commandName.equalsIgnoreCase("viewban")) {
                runViewBanCommand(sender, cmd, args[0], getSubArray(args));
            }
            return true;
        }
        return false;
    }

    private String[] getSubArray(String[] array) {
        if (array.length > 1) {
            return Arrays.asList(array).subList(1, array.length).toArray(new String[0]);
        } else {
            return new String[0];
        }
    }

    private void runHelpCommand(CommandSender sender, Command cmd, String[] args) {
        sender.sendMessage(ColorList.MAIN + "List Of Possible Sub Commands:");
        for (String str : aliasMap.keySet()) {
            if (sender.hasPermission(permMap.get(aliasMap.get(str)))) {
                sender.sendMessage(getHelpMessage(str, cmd.getLabel()));
            }
        }
    }

    private String getHelpMessage(String alias, String baseCommand) {
        String str = aliasMap.get(alias);
        return (ColorList.CMD + "/" + baseCommand + ColorList.SUBCMD + " " + alias + ColorList.HELP + " " + helpList.get(aliasMap.get(str)));
    }

    private void runBanCommand(CommandSender sender, Command cmd, String aliasLabel, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ColorList.ILLEGALARGUMENT + "Please Specify a Player Name and a Ban Reason!");
            sender.sendMessage(getHelpMessage(aliasLabel, cmd.getLabel()));
            return;
        }

        if (!pDataH.doesPlayerExists(args[0])) {
            sender.sendMessage(ColorList.MAIN + "No Player whoes full name matches " + ColorList.NAME + args[0] + ColorList.MAIN + " was found.");
            sender.sendMessage(ColorList.MAIN + "Do To The Nature of this command, please specify the full username of a player.");
            String fullUserName = pDataH.getFullUsername(args[0]);
            if (fullUserName != null) {
                sender.sendMessage(ColorList.MAIN + "Did you mean " + ColorList.NAME + fullUserName + ColorList.MAIN + "?");
            }
            return;
        }
        String playerToBanUserName = pDataH.getFullUsername(args[0]);
        OfflinePlayer playerToBan = Bukkit.getOfflinePlayer(playerToBanUserName);
        if (!playerToBan.hasPlayedBefore()) {
            banDataMain.getLogger().log(Level.SEVERE, "Player Username Passed By Player Data Hasn't Played Before!!!");
            sender.sendMessage(ColorList.ERROR + "Error!");
            return;
        }
        String reason = "";
        for (int i = 1; i < args.length; i++) {
            reason += args[i];
        }
        sender.sendMessage(ColorList.MAIN + "Banning " + ColorList.NAME + playerToBanUserName + ColorList.MAIN + " for " + ColorList.NUMBER + reason);
        PermissionUser permPlayer = PermissionsEx.getUser(playerToBanUserName);
        PermissionGroup[] oldGroups = permPlayer.getGroups();
        String oldGroup = null;
        for (PermissionGroup pg : oldGroups) {
            if (pg.has("basic")) {
                oldGroup = pg.getName();
            }
        }
        if (oldGroup == null) {
            oldGroup = "Basic";
        }
        permPlayer.setGroups(new String[]{"Banned"});
        Ban ban;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location loc = player.getLocation();
            ban = new Ban(reason, oldGroup, (long) loc.getX(), (long) loc.getY(), (long) loc.getZ(), loc.getWorld().getName(), System.currentTimeMillis());
        } else {
            ban = new Ban(reason, oldGroup, System.currentTimeMillis());

        }
        Data rawData = pDataH.getCustomData(playerToBanUserName, "bandata");
        BData banData;
        if (rawData == null) {
            Ban[] banList = new Ban[]{ban};
            banData = new BData(banList);
        } else {
            banData = DataParser.parseFromlist(rawData.getData());
            banData.addBan(ban);
        }
        String[] newRawBanData = DataParser.parseToList(banData);
        Data banDataToSet = new Data("bandata", newRawBanData);
        pDataH.addCustomData(playerToBanUserName, banDataToSet);

    }

    private void runViewBanCommand(CommandSender sender, Command cmd, String aliasLabel, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ColorList.ILLEGALARGUMENT + "Please Specify a Player Name to get info from!");
            sender.sendMessage(getHelpMessage(aliasLabel, cmd.getLabel()));
            return;
        }
        if (args.length > 2) {
            sender.sendMessage(ColorList.ILLEGALARGUMENT + "Please Only Use One Word and a number after " + ColorList.SUBCMD + aliasLabel);
            sender.sendMessage(getHelpMessage(aliasLabel, cmd.getLabel()));
            return;
        }
        String playerUserName = pDataH.getFullUsername(args[0]);
        if (playerUserName == null) {
            sender.sendMessage(ColorList.MAIN + "The Player " + ColorList.NAME + args[0] + ColorList.MAIN + " was not found.");
            return;
        }
        Data rawData = pDataH.getCustomData(playerUserName, "bandata");
        if (rawData == null) {
            sender.sendMessage(ColorList.MAIN + "Found no ban data for Player " + ColorList.NAME + playerUserName + ColorList.MAIN + ".");
            return;
        }
        BData banData = DataParser.parseFromlist(rawData.getData());
        int number = -1;
        if (args.length < 2) {
            if (banData.getBans().length < 2) {
                number = 0;
            } else {
                sender.sendMessage(InfoParser.getInstance().shortInfo(rawData));
                sender.sendMessage(ColorList.MAIN + "Type " + ColorList.CMD + "/" + cmd.getLabel() + " " + ColorList.SUBCMD + aliasLabel + " " + ColorList.ARGS + args[0] + " {0-" + (banData.getBans().length - 1) + "}" + ColorList.MAIN + " for more info on a ban");
                return;
            }
        }
        if (number == -1) {
            try {
                number = Integer.valueOf(args[1]);
            } catch (Exception e) {
                sender.sendMessage(ColorList.ERROR_ARGS + args[1] + ColorList.ERROR + " is not a number.");
                sender.sendMessage(getHelpMessage(aliasLabel, cmd.getLabel()));
                return;
            }
        }
        sender.sendMessage(InfoParser.getInstance().banInfo(rawData, banData, number));
    }
}
