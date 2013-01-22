package net.daboross.bukkitdev.bandata;

import java.util.ArrayList;
import java.util.Arrays;
import net.daboross.bukkitdev.playerdata.ColorL;
import net.daboross.bukkitdev.playerdata.Data;
import net.daboross.bukkitdev.playerdata.DataDisplayParser;
import net.daboross.bukkitdev.playerdata.PlayerData;
import net.daboross.bukkitdev.playerdata.PData;

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
            return new String[]{ColorL.ILLEGALARGUMENT + "Illegal Data"};
        }
        BData banData = DataParser.parseFromlist(rawData.getData());
        PData owner = rawData.getOwner();
        String userName;
        if (owner != null) {
            userName = " " + owner.userName();
        } else {
            userName = "";
        }
        return new String[]{
                    ColorL.MAIN + "Player" + ColorL.NAME + userName
                    + ColorL.MAIN + " has " + ColorL.NUMBER + banData.getBans().length
                    + ColorL.MAIN + " recorded bans."};
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
            return new String[]{ColorL.ILLEGALARGUMENT + "Illegal Data"};
        }
        BData banData = DataParser.parseFromlist(rawData.getData());
        PData owner = rawData.getOwner();
        String userName;
        if (owner != null) {
            userName = " " + owner.userName();
        } else {
            userName = "";
        }
        ArrayList<String> returnList = new ArrayList<>();
        returnList.add(ColorL.MAIN + "Ban Data Info from last ban of" + ColorL.NAME + userName);
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
            userName = "";
        }
        String[] infoLines = new String[]{
            ColorL.MAIN + "Ban Data For Ban Number " + ColorL.NUMBER + banNumber
            + ColorL.MAIN + " of player " + ColorL.NAME + userName + ColorL.MAIN + ":",
            ColorL.MAIN + "Ban Occurred " + ColorL.NUMBER + PlayerData.getFormattedDDate(System.currentTimeMillis() - banToView.getTimeStamp()) + ColorL.MAIN + " ago.",
            ColorL.MAIN + "Ban Reason: " + ColorL.NUMBER + banToView.getReason()};
        return infoLines;
    }

    public static InfoParser getInstance() {
        if (instance == null) {
            instance = new InfoParser();
        }
        return instance;
    }
}
