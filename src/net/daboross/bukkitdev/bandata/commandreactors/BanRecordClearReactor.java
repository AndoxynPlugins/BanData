package net.daboross.bukkitdev.bandata.commandreactors;

import net.daboross.bukkitdev.bandata.BData;
import net.daboross.bukkitdev.bandata.Ban;
import net.daboross.bukkitdev.bandata.DataParser;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.SubCommandHandler;
import net.daboross.bukkitdev.playerdata.Data;
import net.daboross.bukkitdev.playerdata.PData;
import net.daboross.bukkitdev.playerdata.PlayerDataHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class BanRecordClearReactor implements SubCommandHandler {

    private final PlayerDataHandler playerDataHandler;

    public BanRecordClearReactor(PlayerDataHandler playerDataHandler) {
        this.playerDataHandler = playerDataHandler;
    }

    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length < 1) {
            sender.sendMessage(ColorList.ERR+ "Please specify a player");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        if (subCommandArgs.length > 1) {
            sender.sendMessage(ColorList.ERR + "To many arguments");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        PData pData = playerDataHandler.getPData(subCommandArgs[0]);
        if (pData == null) {
            sender.sendMessage(ColorList.ERR + "Player " + ColorList.ERR_ARGS + subCommandArgs[0] + ColorList.ERR + " not found");
            return;
        }
        if (pData.isGroup("banned")) {
            sender.sendMessage(ColorList.ERR_ARGS + pData.userName() + ColorList.ERR + " is currently banned");
            return;
        }
        Data data = pData.getData("bandata");
        if (data == null) {
            sender.sendMessage(ColorList.ERR + "No BanData found for " + ColorList.ERR_ARGS + pData.userName());
            return;
        }
        BData banData = DataParser.parseFromlist(data);
        if (banData == null) {
            sender.sendMessage(ColorList.ERR + "Error parsing BanData");
            return;
        }
        Ban[] bans = banData.getBans();
        if (bans.length == 1) {
            pData.removeData("bandata");
            sender.sendMessage(ColorList.NAME + pData.userName() + ColorList.REG+ "'s ban record has been cleared");
        } else {
            Ban[] newBans = new Ban[bans.length - 1];
            System.arraycopy(bans, 0, newBans, 0, bans.length - 1);
            BData newBanData = new BData(newBans, pData);
            Data newRawData = new Data("bandata", DataParser.parseToList(newBanData));
            pData.addData(newRawData);
            sender.sendMessage(ColorList.NAME + pData.userName() + ColorList.REG+ "'s last ban has been cleared.");
        }
    }
}
