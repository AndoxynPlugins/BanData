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
            sender.sendMessage(ColorList.ERR + "Please specify a player to get info for");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        if (subCommandArgs.length > 2) {
            sender.sendMessage(ColorList.ERR + "Please use only one word and one number after " + ColorList.CMD + "/" + baseCommandLabel + " " + ColorList.SUBCMD + subCommandLabel);
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        String playerUserName = playerDataHandler.getFullUsername(subCommandArgs[0]);
        if (playerUserName == null) {
            sender.sendMessage(ColorList.REG + "The player " + ColorList.NAME + subCommandArgs[0] + ColorList.REG + " was not found.");
            return;
        }
        Data rawData = playerDataHandler.getCustomData(playerUserName, "bandata");
        if (rawData == null) {
            sender.sendMessage(ColorList.REG + "Found no ban data for Player " + ColorList.NAME + playerUserName + ColorList.REG + ".");
            return;
        }
        BData banData = DataParser.parseFromlist(rawData);
        int number = -1;
        if (subCommandArgs.length < 2) {
            if (banData.getBans().length < 2) {
                number = 1;
            } else {
                sender.sendMessage(InfoParser.getInstance().shortInfo(rawData));
                sender.sendMessage(ColorList.REG + "Type " + ColorList.CMD + "/" + baseCommandLabel + " " + ColorList.SUBCMD + subCommandLabel + " " + ColorList.ARGS + subCommandArgs[0] + " <1-" + (banData.getBans().length) + ">" + ColorList.REG + "for info on a ban");
                return;
            }
        }
        if (number == -1) {
            try {
                number = Integer.valueOf(subCommandArgs[1]);
            } catch (Exception e) {
                sender.sendMessage(ColorList.ERR_ARGS + subCommandArgs[1] + ColorList.ERR + " is not an integer.");
                sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
                return;
            }
        }
        sender.sendMessage(InfoParser.getInstance().banInfo(rawData, banData, number - 1));
    }
}
