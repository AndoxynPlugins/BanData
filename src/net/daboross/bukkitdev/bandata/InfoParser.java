package net.daboross.bukkitdev.bandata;

import java.util.ArrayList;
import java.util.Arrays;
import net.daboross.bukkitdev.playerdata.ColorList;
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
            return new String[]{ColorList.ILLEGALARGUMENT + "Illegal Data"};
        }
        BData banData = DataParser.parseFromlist(rawData);
        PData owner = rawData.getOwner();
        String userName;
        if (owner != null) {
            userName = " " + owner.userName();
        } else {
            userName = "";
        }
        return new String[]{
            ColorList.MAIN + "Player" + ColorList.NAME + userName
            + ColorList.MAIN + " has " + ColorList.NUMBER + banData.getBans().length
            + ColorList.MAIN + " recorded bans."};
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
            return new String[]{ColorList.ILLEGALARGUMENT + "Illegal Data"};
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
        returnList.add(ColorList.MAIN + "Ban Data Info from last ban of" + ColorList.NAME + userName);
        returnList.addAll(Arrays.asList(banInfo(rawData, banData, -1)));
        return returnList.toArray(new String[0]);
    }

    /**
     *
     * @param rawData
     * @param data
     * @param banNumber
     * @return
     */
    protected String[] banInfo(Data rawData, BData data, int banNumber) {
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
            userName = " " + owner.userName();
        } else {
            return new String[0];
        }
        boolean isBannerAvalible = !banToView.getBanner().equals("Unknown");
        String[] infoLines = isBannerAvalible ? new String[6] : new String[5];
        infoLines[0] = ColorList.MAIN + "Ban Data For Ban Number " + ColorList.NUMBER + banNumber + ColorList.MAIN + " of player " + ColorList.NAME + userName + ColorList.MAIN + ":";
        infoLines[1] = ColorList.MAIN + "Ban Occurred " + ColorList.NUMBER + PlayerData.getFormattedDDate(System.currentTimeMillis() - banToView.getTimeStamp()) + ColorList.MAIN + " ago.";
        infoLines[2] = ColorList.MAIN + "Ban Reason: " + ColorList.NUMBER + banToView.getReason();
        if (owner.isGroup("Banned")) {
            infoLines[3] = ColorList.NAME + userName + ColorList.MAIN + " Is Currently Banned";
        } else {
            infoLines[3] = ColorList.NAME + userName + ColorList.MAIN + " Is Not Currently Banned";
        }
        infoLines[4] = ColorList.NAME + userName + ColorList.MAIN + " was " + ColorList.NUMBER + PlayerData.formatList(banToView.getOldGroups()) + ColorList.MAIN + " before they were banned.";
        if (isBannerAvalible) {
            infoLines[5] = ColorList.NAME + userName + ColorList.MAIN + " was banned by " + ColorList.NAME + banToView.getBanner();
        }
        return infoLines;
    }

    public static InfoParser getInstance() {
        if (instance == null) {
            instance = new InfoParser();
        }
        return instance;
    }
}
