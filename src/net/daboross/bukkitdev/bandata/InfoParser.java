package net.daboross.bukkitdev.bandata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.daboross.bukkitdev.commandexecutorbase.ArrayHelpers;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.playerdata.Data;
import net.daboross.bukkitdev.playerdata.DataDisplayParser;
import net.daboross.bukkitdev.playerdata.PData;
import net.daboross.bukkitdev.playerdata.PlayerData;

/**
 *
 * @author daboross
 */
public class InfoParser implements DataDisplayParser {

    private static InfoParser instance;
    private static final Object INSTANCE_LOCK = new Object();

    private InfoParser() {
    }

    /**
     *
     * @param rawData
     * @return
     */
    @Override
    public String[] shortInfo(Data rawData) {
        if (rawData == null) {
            return new String[0];
        }
        if (!rawData.getName().equalsIgnoreCase("bandata")) {
            return new String[]{ColorList.ERR + "Illegal BanData"};
        }
        BData banData = DataParser.parseFromlist(rawData);
        PData owner = rawData.getOwner();
        return new String[]{
            ColorList.REG + "Player" + ColorList.NAME + (owner == null ? "" : " " + owner.userName())
            + ColorList.REG + " has " + ColorList.DATA + banData.getBans().length
            + ColorList.REG + " recorded bans."};
    }

    /**
     *
     * @param rawData
     * @return
     */
    @Override
    public String[] longInfo(Data rawData) {
        if (rawData == null) {
            return new String[0];
        }
        if (!rawData.getName().equalsIgnoreCase("bandata")) {
            return new String[]{ColorList.ERR + "Illegal BanData"};
        }
        BData banData = DataParser.parseFromlist(rawData);
        PData owner = rawData.getOwner();
        String userName;
        if (owner != null) {
            userName = " " + owner.userName();
        } else {
            userName = "";
        }
        ArrayList<String> returnList = new ArrayList<String>();
        returnList.add(ColorList.REG + "BanData info from last ban of" + ColorList.NAME + userName);
        returnList.addAll(Arrays.asList(banInfo(rawData, banData, -1)));
        return returnList.toArray(new String[returnList.size()]);
    }

    /**
     *
     * @param rawData
     * @param data
     * @param banNumber
     * @return
     */
    public static String[] banInfo(Data rawData, BData data, int banNumber) {
        if (rawData == null || data == null) {
            throw new IllegalArgumentException("Can't Be Null!");
        }
        if (data.getBans().length < 1) {
            return new String[0];
        }
        if (banNumber < 0) {
            banNumber = data.getBans().length - 1;
        }
        if (banNumber >= data.getBans().length) {
            banNumber = data.getBans().length - 1;
        }
        Ban banToView = data.getBans()[banNumber];
        PData owner = rawData.getOwner();
        String userName;
        if (owner != null) {
            userName = owner.userName();
        } else {
            return new String[0];
        }
        boolean isBannerAvalible = !"Unknown".equalsIgnoreCase(banToView.getBanner());
        List<String> infoList = new ArrayList<String>(7);
        infoList.add(ColorList.TOP_SEPERATOR + " -- " + ColorList.TOP + "Ban " + ColorList.DATA + banNumber + ColorList.TOP + " for " + ColorList.NAME + userName + ColorList.TOP_SEPERATOR + " --");
        infoList.add(ColorList.REG + "Ban Occurred " + ColorList.DATA + PlayerData.getFormattedDate(System.currentTimeMillis() - banToView.getTimeStamp()) + ColorList.REG + " ago.");
        infoList.add(ColorList.REG + "Ban Reason: " + ColorList.DATA + banToView.getReason());
        infoList.add(ColorList.NAME + userName + ColorList.REG + (owner.isGroup("Banned") ? " is still banned" : "is not currently banned"));
        infoList.add(ColorList.NAME + userName + ColorList.REG + " was " + ColorList.DATA + ArrayHelpers.combinedWithSeperator(banToView.getOldGroups(), ", ") + ColorList.REG + " before they were banned.");
        if (isBannerAvalible) {
            infoList.add(ColorList.NAME + userName + ColorList.REG + " was banned by " + ColorList.NAME + banToView.getBanner());
        }
        if (!banToView.isConsoleBan()) {
            infoList.add(ColorList.REG + "To see where " + ColorList.NAME + userName + ColorList.REG + " was banned type " + ColorList.CMD + "/bd " + ColorList.SUBCMD + "tp " + ColorList.NAME + userName + " " + ColorList.ARGS + banNumber);
        }
        return infoList.toArray(new String[infoList.size()]);
    }

    public static InfoParser getInstance() {
        synchronized (INSTANCE_LOCK) {
            if (instance == null) {
                instance = new InfoParser();
            }
        }
        return instance;
    }
}
