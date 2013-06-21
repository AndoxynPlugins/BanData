package net.daboross.bukkitdev.bandata;

import java.util.List;
import java.util.logging.Level;
import net.daboross.bukkitdev.playerdata.Data;
import net.daboross.bukkitdev.playerdata.PData;

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
        List<PData> pDatas = main.getPlayerData().getHandler().getAllPDatas();
        for (int i = 0; i < pDatas.size(); i++) {
            PData current = pDatas.get(i);
            if (current.isGroup("Banned")) {
                if (!current.hasData("bandata")) {
                    current.addData(new Data("bandata", DataParser.parseToList(new BData(new Ban[]{new Ban("Unknown Reason", new String[]{"Basic"}, System.currentTimeMillis())}, current))));
                    main.getLogger().log(Level.INFO, "{0} has an Unrecorded Ban!", current.userName());
                }
            }
        }
    }
}
