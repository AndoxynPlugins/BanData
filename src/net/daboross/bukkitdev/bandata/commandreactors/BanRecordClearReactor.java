package net.daboross.bukkitdev.bandata.commandreactors;

import java.util.ArrayList;
import java.util.List;
import net.daboross.bukkitdev.bandata.BData;
import net.daboross.bukkitdev.bandata.Ban;
import net.daboross.bukkitdev.bandata.DataParser;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase;
import net.daboross.bukkitdev.playerdata.Data;
import net.daboross.bukkitdev.playerdata.PData;
import net.daboross.bukkitdev.playerdata.PlayerDataHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import ru.tehkode.permissions.PermissionUser;

/**
 *
 * @author daboross
 */
public class BanRecordClearReactor implements CommandExecutorBase.CommandReactor {

    private final PlayerDataHandler playerDataHandler;

    public BanRecordClearReactor(PlayerDataHandler playerDataHandler) {
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
        if (pData.isGroup("banned")) {
            sender.sendMessage(ColorList.ERROR_ARGS + pData.userName() + ColorList.ERROR + " is currently banned!");
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
        if (bans.length == 1) {
            pData.removeData("bandata");
            sender.sendMessage(ColorList.NAME + pData.userName() + ColorList.MAIN + "'s ban record has been cleared");
        } else {
            Ban[] newBans = new Ban[bans.length - 1];
            System.arraycopy(bans, 0, newBans, 0, bans.length - 1);
            BData newBanData = new BData(bans, pData);
            Data newRawData = new Data("bandata", DataParser.parseToList(newBanData));
            pData.addData(newRawData);
            sender.sendMessage(ColorList.NAME + pData.userName() + ColorList.MAIN + "'s last ban has been cleared.");
        }
    }
}
