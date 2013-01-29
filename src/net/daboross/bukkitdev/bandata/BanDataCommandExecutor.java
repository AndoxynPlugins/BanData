package net.daboross.bukkitdev.bandata;

import java.util.ArrayList;
import net.daboross.bukkitdev.playerdata.ColorList;
import net.daboross.bukkitdev.playerdata.Data;
import net.daboross.bukkitdev.playerdata.PData;
import net.daboross.bukkitdev.playerdata.PlayerData;
import net.daboross.bukkitdev.playerdata.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;

/**
 *
 * @author daboross
 */
public class BanDataCommandExecutor extends CommandExecutorBase {

    private PlayerData playerDataMain;
    private PlayerDataHandler pDataH;
    private BanData banDataMain;

    /**
     *
     */
    protected BanDataCommandExecutor(BanData bd) {
        this.banDataMain = bd;
        this.playerDataMain = banDataMain.getPlayerData();
        this.pDataH = playerDataMain.getHandler();
        initCommand("help", new String[]{"?"}, true, "bandata.help", "This Command Views This Page");
        initCommand("ban", new String[]{}, true, "bandata.ban", (ColorList.ARGS + "<Player> <Reason>" + ColorList.HELP + " Bans A Player With PEX and Records Info."));
        initCommand("viewban", new String[]{"vb", "i"}, true, "bandata.viewban", (ColorList.ARGS + "<Player>" + ColorList.HELP + " Views Ban Info On a Player"));
        initCommand("bantp", new String[]{"tp", "tpban"}, false, "bandata.bantp", ColorList.ARGS + "<Player>" + ColorList.HELP + " This Command Teleports You To Where Someone Was Banned.");
        initCommand("listbans", new String[]{"list", "bl", "lb"}, true, "bandata.listbans", "This Command Lists All Players Who Have Been Banned and How Many Times They have Been Banned");
        initCommand("checkBans", new String[]{}, true, "bandata.admin", "This Command Checks For Users Who Are Banned, But Not In The DataBase");
        //initCommand("redo", new String[]{}, true, "bandata.admin", ColorList.ARGS + "<Player> <Reason>" + ColorList.HELP + "This Command Adds a Reason to an Unknown Reason Ban.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("bd")) {
            String commandName = isCommandValid(sender, cmd, label, args);
            if (commandName == null) {
                return true;
            }
            if (commandName.equalsIgnoreCase("ban")) {
                runBanCommand(sender, cmd, args[0], getSubArray(args));
            } else if (commandName.equalsIgnoreCase("viewban")) {
                runViewBanCommand(sender, cmd, args[0], getSubArray(args));
            } else if (commandName.equalsIgnoreCase("bantp")) {
                runBanTpCommand(sender, cmd, args[0], getSubArray(args));
            } else if (commandName.equalsIgnoreCase("listbans")) {
                runListBansCommand(sender, cmd, args[0], getSubArray(args));
            } else if (commandName.equalsIgnoreCase("checkbans")) {
                runBanCheckCommand(sender, cmd, getSubArray(args));
            }
            return true;
        }
        return false;
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
        PData playerToBanPData = pDataH.getPData(playerToBanUserName);
        String reason = "";
        for (int i = 1; i < args.length; i++) {
            reason += " " + args[i];
        }
        sender.sendMessage(ColorList.MAIN + "Banning " + ColorList.NAME + playerToBanUserName + ColorList.MAIN + " for " + ColorList.NUMBER + reason);
        PermissionUser permPlayer = playerToBanPData.getPermUser();
        String oldGroup = playerToBanPData.getGroup();
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
            banData = new BData(banList, pDataH.getPData(playerToBanUserName));
        } else {
            banData = DataParser.parseFromlist(rawData);
            banData.addBan(ban);
        }
        String[] newRawBanData = DataParser.parseToList(banData);
        Data banDataToSet = new Data("bandata", newRawBanData);
        pDataH.addCustomData(playerToBanUserName, banDataToSet);
        Bukkit.getServer().broadcastMessage(ColorList.getBroadcastName("BanData") + " " + ColorList.NAME + playerToBanUserName + ColorList.BROADCAST + " was just banned for " + ColorList.NUMBER + reason);
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
        BData banData = DataParser.parseFromlist(rawData);
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

    private void runBanTpCommand(CommandSender sender, Command cmd, String aliasLabel, String[] args) {
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
        BData banData = DataParser.parseFromlist(rawData);
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
        if (banData.getBans()[number].isConsoleBan()) {
            sender.sendMessage(ColorList.MAIN + "Ban Number " + ColorList.NUMBER + number + ColorList.MAIN + " for Player " + ColorList.NAME + playerUserName + ColorList.MAIN + " Does Not Have a Position Associated With It.");
            return;
        }
        Player player = (Player) sender;
        Ban ban = banData.getBans()[number];
        World world = Bukkit.getServer().getWorld(ban.getWorld());
        if (world == null) {
            sender.sendMessage(ColorList.ERROR + "Could Not Find The World Associated With Ban Number " + ColorList.NUMBER + number + ColorList.MAIN + " for Player " + ColorList.NAME + playerUserName);
            return;
        }
        sender.sendMessage(ColorList.MAIN + "Teleporting You to Position Associated With Ban Number " + ColorList.NUMBER + number + ColorList.MAIN + " for " + ColorList.NAME + playerUserName);
        Location loc = new Location(world, (double) ban.getXPos(), (double) ban.getYPos(), (double) ban.getZPos());
        player.teleport(loc);
        sender.sendMessage(InfoParser.getInstance().banInfo(rawData, banData, number));
    }

    private void runListBansCommand(CommandSender sender, Command cmd, String aliasLabel, String[] args) {
        if (args.length > 1) {
            sender.sendMessage(ColorList.MAIN + "Please Use Only 1 Number After " + ColorList.CMD + "/" + cmd.getName() + ColorList.SUBCMD + " " + aliasLabel);
        }
        int pageNumber;
        if (args.length == 0) {
            pageNumber = 1;
        } else {
            try {
                pageNumber = Integer.valueOf(args[0]);
            } catch (Exception e) {
                sender.sendMessage(ColorList.ERROR_ARGS + args[0] + ColorList.ERROR + " is not a number.");
                sender.sendMessage(getHelpMessage(aliasLabel, cmd.getLabel()));
                return;
            }
            if (pageNumber < 1) {
                sender.sendMessage(ColorList.ERROR_ARGS + args[0] + ColorList.ERROR + " is not a non-0 positive number.");
                return;
            }
        }
        BData[] banDataList = DataParser.parseAll(pDataH.getAllDatas("bandata"));
        ArrayList<String> messagesToSend = new ArrayList<String>();
        messagesToSend.add("");
        messagesToSend.add(ColorList.MAIN_DARK + "Ban List, Page " + ColorList.NUMBER + pageNumber + ColorList.MAIN_DARK + ":");
        for (int i = ((pageNumber - 1) * 6); i < ((pageNumber - 1) * 6) + 6 & i < banDataList.length; i++) {
            BData currentBanData = banDataList[i];
            String last;
            if (currentBanData.getBans().length == 1) {
                last = " ban recorded";
            } else {
                last = " bans recorded";
            }
            messagesToSend.add(ColorList.NAME + currentBanData.getOwner().userName() + ColorList.MAIN + " has " + ColorList.NUMBER + currentBanData.getBans().length + ColorList.MAIN + last);
        }
        if (pageNumber < (banDataList.length / 6.0)) {
            messagesToSend.add(ColorList.MAIN_DARK + "To View The Next Page, Type: " + ColorList.CMD + "/" + cmd.getName() + ColorList.SUBCMD + " " + aliasLabel + ColorList.ARGS + " " + (pageNumber + 1));
        }
        sender.sendMessage(messagesToSend.toArray(new String[0]));
    }

    private void runBanCheckCommand(CommandSender sender, Command cmd, String[] args) {
        banDataMain.getBanCheckReloader().goThrough();
    }
}
