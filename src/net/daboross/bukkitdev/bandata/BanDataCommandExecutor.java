package net.daboross.bukkitdev.bandata;

import java.util.ArrayList;
import net.daboross.bukkitdev.bandata.commandreactors.BanCommandReactor;
import net.daboross.bukkitdev.bandata.commandreactors.BanInfoCommandReactor;
import net.daboross.bukkitdev.bandata.commandreactors.BanRecordClearReactor;
import net.daboross.bukkitdev.bandata.commandreactors.UnBanCommandReactor;
import net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.playerdata.Data;
import net.daboross.bukkitdev.playerdata.PlayerData;
import net.daboross.bukkitdev.playerdata.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author daboross
 */
public class BanDataCommandExecutor extends CommandExecutorBase implements CommandExecutorBase.CommandReactor {

    private final PlayerDataHandler playerDataHandler;
    private final BanData banDataMain;

    protected BanDataCommandExecutor(BanData bd) {
        this.banDataMain = bd;
        PlayerData playerDataMain = banDataMain.getPlayerData();
        this.playerDataHandler = playerDataMain.getHandler();
        initCommand("ban", true, "bandata.ban", new String[]{"Player", "Reason"}, "Bans A Player With PEX and Records Info.", new BanCommandReactor(playerDataHandler));
        initCommand("baninfo", new String[]{"bi", "i"}, true, "bandata.baninfo", new String[]{"Player"}, "Views Ban Info On a Player", new BanInfoCommandReactor(playerDataHandler));
        initCommand("bantp", new String[]{"tp", "tpban"}, false, "bandata.bantp", new String[]{"Player"}, "This Command Teleports You To Where Someone Was Banned.", this);
        initCommand("listbans", new String[]{"list", "bl", "lb"}, true, "bandata.listbans", new String[]{"PageNumber"}, "This Command Lists All Players Who Have Been Banned and How Many Times They have Been Banned", this);
        initCommand("checkBans", true, "bandata.admin", "This Command Checks For Users Who Are Banned, But Not In The DataBase", this);
        initCommand("unban", true, "bandata.unban", new String[]{"Player"}, "Unbans the given player", new UnBanCommandReactor(playerDataHandler));
        initCommand("clearban", true, "bandata.admin.clearban", new String[]{"Player"}, "Clears the last ban off of a Player's ban record.", new BanRecordClearReactor(playerDataHandler));
    }

    @Override
    public void runCommand(CommandSender sender, Command mainCommand, String mainCommandLabel, String subCommand, String subCommandLabel,
            String[] subCommandArgs, CommandExecutorBridge executorBridge) {
        if (subCommand.equalsIgnoreCase("bantp")) {
            runBanTpCommand(sender, mainCommand, subCommandLabel, subCommandArgs);
        } else if (subCommand.equalsIgnoreCase("listbans")) {
            runListBansCommand(sender, mainCommand, subCommandLabel, subCommandArgs);
        } else if (subCommand.equalsIgnoreCase("checkbans")) {
            runBanCheckCommand();
        }
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
        String playerUserName = playerDataHandler.getFullUsername(args[0]);
        if (playerUserName == null) {
            sender.sendMessage(ColorList.MAIN + "The Player " + ColorList.NAME + args[0] + ColorList.MAIN + " was not found.");
            return;
        }
        Data rawData = playerDataHandler.getCustomData(playerUserName, "bandata");
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
            } catch (NumberFormatException e) {
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
            } catch (NumberFormatException nfe) {
                sender.sendMessage(ColorList.ERROR_ARGS + args[0] + ColorList.ERROR + " is not a number.");
                sender.sendMessage(getHelpMessage(aliasLabel, cmd.getLabel()));
                return;
            }
            if (pageNumber < 1) {
                sender.sendMessage(ColorList.ERROR_ARGS + args[0] + ColorList.ERROR + " is not a non-0 positive number.");
                return;
            }
        }
        BData[] banDataArray = DataParser.parseAll(playerDataHandler.getAllDatas("bandata"));
        ArrayList<String> messagesToSend = new ArrayList<String>();
        messagesToSend.add(ColorList.TOP_OF_LIST_SEPERATOR + "--" + ColorList.TOP_OF_LIST + " Ban List" + ColorList.TOP_OF_LIST_SEPERATOR + " -- " + ColorList.NUMBER + pageNumber + ColorList.TOP_OF_LIST + " / " + ColorList.NUMBER + (banDataArray.length / 6) + (banDataArray.length % 6 == 0 ? 0 : 1) + ColorList.TOP_OF_LIST_SEPERATOR + " --");
        for (int i = ((pageNumber - 1) * 6); i < banDataArray.length && i < ((pageNumber - 1) * 6) + 6; i++) {
            BData currentBanData = banDataArray[i];
            messagesToSend.add(ColorList.NAME + currentBanData.getOwner().userName() + ColorList.MAIN
                    + " has " + ColorList.NUMBER + currentBanData.getBans().length + ColorList.MAIN + ((currentBanData.getBans().length == 1) ? " ban recorded" : " bans recorded")
                    + ", and " + (isBanned(currentBanData) ? "is currently banned" : "is not currently banned") + ".");
        }
        if (pageNumber < (banDataArray.length / 6.0)) {
            messagesToSend.add(ColorList.MAIN + "To View The Next Page, Type: " + ColorList.CMD + "/" + cmd.getName() + ColorList.SUBCMD + " " + aliasLabel + ColorList.ARGS + " " + (pageNumber + 1));
        }
        sender.sendMessage(messagesToSend.toArray(new String[messagesToSend.size()]));
    }

    private boolean isBanned(BData bd) {
        return bd.getOwner().isGroup("banned");
    }

    private void runBanCheckCommand() {
        banDataMain.getBanCheckReloader().goThrough();
    }

    @Override
    public String getCommandName() {
        return "bd";
    }

    @Override
    protected String getMainCmdPermission() {
        return "bandata.help";
    }
}
