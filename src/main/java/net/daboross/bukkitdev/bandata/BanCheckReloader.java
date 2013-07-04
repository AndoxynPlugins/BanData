package net.daboross.bukkitdev.bandata;

import java.util.List;
import java.util.logging.Level;
import net.daboross.bukkitdev.playerdata.PlayerDataStatic;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.milkbowl.vault.permission.Permission;

/**
 *
 * @author daboross
 */
public class BanCheckReloader {

    private BanData main;

    public BanCheckReloader(BanData bd) {
        main = bd;
    }

    /**
     * This goes through all the PDatas and records ban info on every one that
     * is banned.
     */
    public void goThrough() {
        List<? extends PlayerData> playerDatas = main.getPlayerData().getHandler().getAllPlayerDatas();
        Permission permissionHandler = PlayerDataStatic.getPermissionHandler();
        for (int i = 0; i < playerDatas.size(); i++) {
            PlayerData current = playerDatas.get(i);
            if (permissionHandler.playerInGroup((String) null, current.getUsername(), "Banned")) {
                if (!current.hasExtraData("bandata")) {
                    current.addExtraData("bandata", DataParser.parseToList(new BData(new Ban[]{new Ban("Unknown Reason", new String[]{"Basic"}, System.currentTimeMillis())})));
                    main.getLogger().log(Level.INFO, "{0} has an Unrecorded Ban!", current.getUsername());
                }
            }
        }
    }
}
