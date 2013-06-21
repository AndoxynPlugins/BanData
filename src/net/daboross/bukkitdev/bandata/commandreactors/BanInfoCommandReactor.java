package net.daboross.bukkitdev.bandata.commandreactors;

import net.daboross.bukkitdev.bandata.BData;
import net.daboross.bukkitdev.bandata.DataParser;
import net.daboross.bukkitdev.bandata.InfoParser;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.SubCommandHandler;
import net.daboross.bukkitdev.playerdata.Data;
import net.daboross.bukkitdev.playerdata.PlayerDataHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class BanInfoCommandReactor implements SubCommandHandler {

    private final PlayerDataHandler playerDataHandler;

    public BanInfoCommandReactor(PlayerDataHandler playerDataHandler) {
        this.playerDataHandler = playerDataHandler;
    }

    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length < 1) {
            sender.sendMessage(ColorList.ILLEGALARGUMENT + "Please Specify a Player Name to get info from!");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        if (subCommandArgs.length > 2) {
            sender.sendMessage(ColorList.ILLEGALARGUMENT + "Please Only Use One Word and a number after " + ColorList.SUBCMD + subCommandLabel);
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        String playerUserName = playerDataHandler.getFullUsername(subCommandArgs[0]);
        if (playerUserName == null) {
            sender.sendMessage(ColorList.MAIN + "The Player " + ColorList.NAME + subCommandArgs[0] + ColorList.MAIN + " was not found.");
            return;
        }
        Data rawData = playerDataHandler.getCustomData(playerUserName, "bandata");
        if (rawData == null) {
            sender.sendMessage(ColorList.MAIN + "Found no ban data for Player " + ColorList.NAME + playerUserName + ColorList.MAIN + ".");
            return;
        }
        BData banData = DataParser.parseFromlist(rawData);
        int number = -1;
        if (subCommandArgs.length < 2) {
            if (banData.getBans().length < 2) {
                number = 0;
            } else {
                sender.sendMessage(InfoParser.getInstance().shortInfo(rawData));
                sender.sendMessage(ColorList.MAIN + "Type " + ColorList.CMD + "/" + baseCommandLabel + " " + ColorList.SUBCMD + subCommandLabel + " " + ColorList.ARGS + subCommandArgs[0] + " {0-" + (banData.getBans().length - 1) + "}" + ColorList.MAIN + " for more info on a ban");
                return;
            }
        }
        if (number == -1) {
            try {
                number = Integer.valueOf(subCommandArgs[1]);
            } catch (Exception e) {
                sender.sendMessage(ColorList.ERROR_ARGS + subCommandArgs[1] + ColorList.ERROR + " is not a number.");
                sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
                return;
            }
        }
        sender.sendMessage(InfoParser.getInstance().banInfo(rawData, banData, number));
    }
}
