/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.bandata;

import java.util.logging.Level;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.playerdata.Data;
import net.daboross.bukkitdev.playerdata.PDPlayerJoinListener;
import net.daboross.bukkitdev.playerdata.PData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author daboross
 */
public class BanDataJoinListener implements PDPlayerJoinListener {

    private final BanData main;

    public BanDataJoinListener(BanData main) {
        this.main = main;
    }

    public void playerJoinNotify(PlayerJoinEvent pje, final PData pData) {
        if (pData.isGroup("Banned")) {
            final Data data = pData.getData("bandata");
            final Player p = pje.getPlayer();

            Runnable r = new Runnable() {
                public void run() {
                    p.sendMessage(ColorList.REG + "You are currently Banned! You can not build or destroy anything while you are banned.");
                    if (data == null) {
                        pData.addData(new Data("bandata", DataParser.parseToList(new BData(new Ban[]{new Ban("Unknown Reason", new String[]{"Basic"}, System.currentTimeMillis())}, pData))));
                        main.getLogger().log(Level.INFO, "{0} has an Unrecorded Ban!", pData.userName());
                    } else {
                        BData bData = DataParser.parseFromlist(data);
                        if (bData != null) {
                            int numberOfBans = bData.getBans().length;
                            if (numberOfBans == 1) {
                                p.sendMessage(ColorList.REG + "To see the ban reason, use " + ColorList.CMD + "/bd " + ColorList.SUBCMD + "tp " + ColorList.ARGS + (p.getName().length() <= 3 ? p.getName() : p.getName().substring(0, 3)));
                            } else if (numberOfBans > 1) {
                                p.sendMessage(ColorList.REG + "To see the ban reason, use " + ColorList.CMD + "/bd " + ColorList.SUBCMD + "tp " + ColorList.ARGS + (p.getName().length() <= 3 ? p.getName() : p.getName().substring(0, 3)) + " " + numberOfBans);
                            }
                        }
                    }
                    p.sendMessage(ColorList.REG + "To request an unban, please use " + ColorList.CMD + "/mad");
                }
            };
            Bukkit.getScheduler().runTaskLater(main, r, 15);
        }
    }
}
