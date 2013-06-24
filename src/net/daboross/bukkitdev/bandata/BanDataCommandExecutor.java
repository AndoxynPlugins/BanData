package net.daboross.bukkitdev.bandata;

import java.util.ArrayList;
import net.daboross.bukkitdev.bandata.commandreactors.BanCommandReactor;
import net.daboross.bukkitdev.bandata.commandreactors.BanInfoCommandReactor;
import net.daboross.bukkitdev.bandata.commandreactors.BanRecordClearReactor;
import net.daboross.bukkitdev.bandata.commandreactors.UnBanCommandReactor;
import net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.SubCommandHandler;
import net.daboross.bukkitdev.playerdata.Data;
import net.daboross.bukkitdev.playerdata.PlayerData;
import net.daboross.bukkitdev.playerdata.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

/**
 *
 * @author daboross
 */
public class BanDataCommandExecutor implements SubCommandHandler {

    private final CommandExecutorBase commandExecutorBase;
    private final PlayerDataHandler playerDataHandler;
    private final BanData banDataMain;

    protected BanDataCommandExecutor(BanData bd) {
        this.banDataMain = bd;
        PlayerData playerDataMain = banDataMain.getPlayerData();
        this.playerDataHandler = playerDataMain.getHandler();
        commandExecutorBase = new CommandExecutorBase("bandata.help");
        commandExecutorBase.addSubCommand(new SubCommand("ban", true, "bandata.ban", new String[]{"Player", "Reason"}, "Bans A Player With PEX and Records Info.", new BanCommandReactor(playerDataHandler)));
        commandExecutorBase.addSubCommand(new SubCommand("baninfo", new String[]{"bi", "i"}, true, "bandata.baninfo", new String[]{"Player"}, "Views Ban Info On a Player", new BanInfoCommandReactor(playerDataHandler)));
        commandExecutorBase.addSubCommand(new SubCommand("bantp", new String[]{"tp", "tpban"}, false, "bandata.bantp", new String[]{"Player"}, "This Command Teleports You To Where Someone Was Banned.", this));
        commandExecutorBase.addSubCommand(new SubCommand("list", new String[]{"l"}, true, "bandata.listbans", new String[]{"PageNumber"}, "This Command Lists All Players Who Have Been Banned and How Many Times They have Been Banned", this));
        commandExecutorBase.addSubCommand(new SubCommand("checkBans", true, "bandata.admin", "This Command Checks For Users Who Are Banned, But Not In The DataBase", this));
        commandExecutorBase.addSubCommand(new SubCommand("unban", true, "bandata.unban", new String[]{"Player"}, "Unbans the given player", new UnBanCommandReactor(playerDataHandler)));
        commandExecutorBase.addSubCommand(new SubCommand("clearban", true, "bandata.admin.clearban", new String[]{"Player"}, "Clears the last ban off of a Player's ban record.", new BanRecordClearReactor(playerDataHandler)));
    }

    protected void registerCommand(PluginCommand command) {
        command.setExecutor(commandExecutorBase);
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
        if (subCommand.getName().equalsIgnoreCase("bantp")) {
            runBanTpCommand(sender, subCommand, baseCommandLabel, subCommandLabel, subCommandArgs);
        } else if (subCommand.getName().equalsIgnoreCase("list")) {
            runListCommand(sender, subCommand, baseCommandLabel, subCommandLabel, subCommandArgs);
        } else if (subCommand.getName().equalsIgnoreCase("checkbans")) {
            runBanCheckCommand();
        }
    }

    private void runBanTpCommand(CommandSender sender, SubCommand subCommand, String baseCommandLabel, String subCommandLabel, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ColorList.ERR + "Please specify a player to get info for");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        if (args.length > 2) {
            sender.sendMessage(ColorList.ERR + "Please use only one word and a number after " + ColorList.CMD + "/" + baseCommandLabel + " " + ColorList.SUBCMD + subCommandLabel + ColorList.ERR + ".");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        String playerUserName = playerDataHandler.getFullUsername(args[0]);
        if (playerUserName == null) {
            sender.sendMessage(ColorList.ERR + "Player " + ColorList.ERR_ARGS + args[0] + ColorList.ERR + " not found");
            return;
        }
        Data rawData = playerDataHandler.getCustomData(playerUserName, "bandata");
        if (rawData == null) {
            sender.sendMessage(ColorList.ERR + "Found no ban data for player " + ColorList.NAME + playerUserName);
            return;
        }
        BData banData = DataParser.parseFromlist(rawData);
        int number = -1;
        if (args.length < 2) {
            if (banData.getBans().length < 2) {
                number = 1;
            } else {
                sender.sendMessage(InfoParser.getInstance().shortInfo(rawData));
                sender.sendMessage(ColorList.REG + "Type " + ColorList.CMD + "/" + baseCommandLabel + " " + ColorList.SUBCMD + subCommandLabel + " " + ColorList.ARGS + args[0] + " <1-" + (banData.getBans().length) + ">" + ColorList.REG + " for information on a ban.");
                return;
            }
        }
        if (number == -1) {
            try {
                number = Integer.valueOf(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ColorList.ERR_ARGS + args[1] + ColorList.ERR + " is not an integer.");
                sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
                return;
            }
        }
        if (number < 1) {
            sender.sendMessage(ColorList.ERR_ARGS + args[1] + ColorList.ERR + " is not a non-0 positive integer.");
        }
        if (banData.getBans()[number - 1].isConsoleBan()) {
            sender.sendMessage(ColorList.ERR + "Ban number " + ColorList.DATA + number + ColorList.ERR + " for player " + ColorList.NAME + playerUserName + ColorList.ERR + " does not have a position associated with it.");
            return;
        }
        Player player = (Player) sender;
        Ban ban = banData.getBans()[number - 1];
        World world = Bukkit.getServer().getWorld(ban.getWorld());
        if (world == null) {
            sender.sendMessage(ColorList.ERR + "Could not find the world associated with ban number " + ColorList.DATA + number + ColorList.ERR + " for player " + ColorList.NAME + playerUserName);
            return;
        }
        sender.sendMessage(ColorList.REG + "Teleporting you to the position associated with ban number " + ColorList.DATA + number + ColorList.REG + " for player " + ColorList.NAME + playerUserName);
        Location loc = new Location(world, (double) ban.getXPos(), (double) ban.getYPos(), (double) ban.getZPos());
        player.teleport(loc);
        sender.sendMessage(InfoParser.getInstance().banInfo(rawData, banData, number - 1));
    }

    private void runListCommand(CommandSender sender, SubCommand subCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length > 1) {
            sender.sendMessage(ColorList.ERR + "Please use only one number after " + ColorList.CMD + "/" + baseCommandLabel + ColorList.SUBCMD + " " + subCommandLabel);
        }
        int pageNumber;
        if (subCommandArgs.length == 0) {
            pageNumber = 1;
        } else {
            try {
                pageNumber = Integer.valueOf(subCommandArgs[0]);
            } catch (NumberFormatException nfe) {
                sender.sendMessage(ColorList.ERR_ARGS + subCommandArgs[0] + ColorList.ERR + " is not an integer.");
                sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
                return;
            }
            if (pageNumber == 0) {
                sender.sendMessage(ColorList.ERR_ARGS + subCommandArgs[0] + ColorList.ERR + " is not a non-0 integer.");
                return;
            } else if (pageNumber < 0) {
                sender.sendMessage(ColorList.ERR_ARGS + subCommandArgs[0] + ColorList.ERR + " is not a positive integer.");
                return;
            }
        }
        int pageNumberReal = pageNumber - 1;
        BData[] banDataArray = DataParser.parseAll(playerDataHandler.getAllDatas("bandata"));
        ArrayList<String> messagesToSend = new ArrayList<String>();
        messagesToSend.add(ColorList.TOP_SEPERATOR + " -- " + ColorList.TOP + "Ban List " + ColorList.TOP_SEPERATOR + "--" + ColorList.TOP + " Page " + ColorList.DATA + pageNumber + ColorList.TOP + "/" + ColorList.DATA + ((banDataArray.length / 6) + (banDataArray.length % 6 == 0 ? 0 : 1)) + ColorList.TOP_SEPERATOR + " --");
        for (int i = (pageNumberReal * 6); i < ((pageNumberReal + 1) * 6) && i < banDataArray.length; i++) {
            BData currentBanData = banDataArray[i];
            messagesToSend.add(ColorList.NAME + currentBanData.getOwner().userName() + ColorList.REG
                    + " has " + ColorList.DATA + currentBanData.getBans().length + ColorList.REG + ((currentBanData.getBans().length == 1) ? " ban" : " bans")
                    + ", and " + (isBanned(currentBanData) ? "is currently banned" : "is not currently banned") + ".");
        }
        if (pageNumber < (banDataArray.length / 6.0)) {
            messagesToSend.add(ColorList.REG + "To view the next page type " + ColorList.CMD + "/" + baseCommandLabel + ColorList.SUBCMD + " " + subCommandLabel + ColorList.ARGS + " " + (pageNumber + 1));
        }
        sender.sendMessage(messagesToSend.toArray(new String[messagesToSend.size()]));
    }

    private boolean isBanned(BData bd) {
        return bd.getOwner().isGroup("banned");
    }

    private void runBanCheckCommand() {
        banDataMain.getBanCheckReloader().goThrough();
    }
}
