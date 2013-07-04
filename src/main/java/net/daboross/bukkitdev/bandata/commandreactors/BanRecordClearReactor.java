package net.daboross.bukkitdev.bandata.commandreactors;

import net.daboross.bukkitdev.bandata.BData;
import net.daboross.bukkitdev.bandata.Ban;
import net.daboross.bukkitdev.bandata.DataParser;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.SubCommandHandler;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.daboross.bukkitdev.playerdata.api.PlayerHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class BanRecordClearReactor implements SubCommandHandler {

    private final PlayerHandler playerHandler;

    public BanRecordClearReactor(PlayerHandler playerHandler) {
        this.playerHandler = playerHandler;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length < 1) {
            sender.sendMessage(ColorList.ERR + "Please specify a player");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        if (subCommandArgs.length > 1) {
            sender.sendMessage(ColorList.ERR + "To many arguments");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        PlayerData pd = playerHandler.getPlayerDataPartial(subCommandArgs[0]);
        if (pd == null) {
            sender.sendMessage(ColorList.ERR + "Player " + ColorList.ERR_ARGS + subCommandArgs[0] + ColorList.ERR + " not found");
            return;
        }
        if (pd.isGroup("banned")) {
            sender.sendMessage(ColorList.ERR_ARGS + pd.getUsername() + ColorList.ERR + " is currently banned");
            return;
        }
        Data data = pd.getData("bandata");
        if (data == null) {
            sender.sendMessage(ColorList.ERR + "No BanData found for " + ColorList.ERR_ARGS + pd.getUsername());
            return;
        }
        BData banData = DataParser.parseFromlist(data);
        if (banData == null) {
            sender.sendMessage(ColorList.ERR + "Error parsing BanData");
            return;
        }
        Ban[] bans = banData.getBans();
        if (bans.length == 1) {
            pd.removeData("bandata");
            sender.sendMessage(ColorList.NAME + pd.getUsername() + ColorList.REG + "'s ban record has been cleared");
        } else {
            Ban[] newBans = new Ban[bans.length - 1];
            System.arraycopy(bans, 0, newBans, 0, bans.length - 1);
            BData newBanData = new BData(newBans, pd);
            Data newRawData = new Data("bandata", DataParser.parseToList(newBanData));
            pd.addData(newRawData);
            sender.sendMessage(ColorList.NAME + pd.getUsername() + ColorList.REG + "'s last ban has been cleared.");
        }
    }
}
