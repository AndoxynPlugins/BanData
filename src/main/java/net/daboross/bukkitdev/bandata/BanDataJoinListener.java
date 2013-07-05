/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.bandata;

import java.util.logging.Level;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.playerdata.api.events.PlayerDataPlayerJoinEvent;
import net.daboross.bukkitdev.playerdata.helpers.PermissionsHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 *
 * @author daboross
 */
public class BanDataJoinListener implements Listener {

    private final BanData main;

    public BanDataJoinListener(BanData main) {
        this.main = main;
    }

    public void onPlayerDataPlayerJoin(PlayerDataPlayerJoinEvent pdpje) {
        if (PermissionsHelper.userInGroup(pdpje.getPlayerData().getUsername(), "Banned")) {
            final String[] data = pdpje.getPlayerData().getExtraData("bandata");
            final Player p = pdpje.getPlayer();
            Runnable r;
            if (data == null) {
                String[] newData = DataParser.parseToList(new BData(new Ban[]{new Ban("Unknown Reason", new String[]{"Basic"}, System.currentTimeMillis())}));
                pdpje.getPlayerData().addExtraData("bandata", newData);
                main.getLogger().log(Level.INFO, "{0} has an Unrecorded Ban!", p.getName());
                r = new Runnable() {
                    @Override
                    public void run() {
                        p.sendMessage(ColorList.REG + "You are currently Banned! You can not build or destroy anything while you are banned.");
                        p.sendMessage(ColorList.REG + "To request an unban, please use " + ColorList.CMD + "/mad");
                    }
                };
            } else {
                final BData bd = DataParser.parseFromlist(data);
                r = new Runnable() {
                    @Override
                    public void run() {
                        p.sendMessage(ColorList.REG + "You are currently Banned! You can not build or destroy anything while you are banned.");
                        if (bd != null) {
                            int numberOfBans = bd.getBans().length;
                            if (numberOfBans == 1) {
                                p.sendMessage(ColorList.REG + "To see the ban reason, use " + ColorList.CMD + "/bd " + ColorList.SUBCMD + "tp " + ColorList.ARGS + (p.getName().length() <= 3 ? p.getName() : p.getName().substring(0, 3)));
                            } else if (numberOfBans > 1) {
                                p.sendMessage(ColorList.REG + "To see the ban reason, use " + ColorList.CMD + "/bd " + ColorList.SUBCMD + "tp " + ColorList.ARGS + (p.getName().length() <= 3 ? p.getName() : p.getName().substring(0, 3)) + " " + numberOfBans);
                            }
                        }
                        p.sendMessage(ColorList.REG + "To request an unban, please use " + ColorList.CMD + "/mad");
                    }
                };
            }
            Bukkit.getScheduler().runTaskLater(main, r, 15);
        }
    }
}
