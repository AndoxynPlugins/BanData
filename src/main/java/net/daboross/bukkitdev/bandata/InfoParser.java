package net.daboross.bukkitdev.bandata;

import java.util.ArrayList;
import java.util.List;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.ArrayHelpers;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.daboross.bukkitdev.playerdata.helpers.DateHelper;
import net.daboross.bukkitdev.playerdata.helpers.PermissionsHelper;

/**
 *
 * @author daboross
 */
public class InfoParser {

    public static String[] shortInfo(String dataName, String[] data, PlayerData owner) {
        if (data == null) {
            return new String[0];
        }
        if (!dataName.equalsIgnoreCase("bandata")) {
            return new String[]{ColorList.ERR + "Illegal BanData"};
        }
        BData banData = DataParser.parseFromlist(data);
        return new String[]{
            ColorList.REG + "Player" + ColorList.NAME + (owner == null ? "" : " " + owner.getUsername())
            + ColorList.REG + " has " + ColorList.DATA + banData.getBans().length
            + ColorList.REG + " recorded bans."};
    }

    /**
     *
     * @param rawData
     * @param data
     * @param banNumber
     * @return
     */
    public static String[] banInfo(BData data, PlayerData owner, int banNumber) {
        if (owner == null || data == null) {
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
        String ownerName = owner.getUsername();
        boolean isBannerAvalible = !"Unknown".equalsIgnoreCase(banToView.getBanner());
        List<String> infoList = new ArrayList<String>(7);
        infoList.add(ColorList.TOP_SEPERATOR + " -- " + ColorList.TOP + "Ban " + ColorList.DATA + (banNumber + 1) + ColorList.TOP + " of " + ColorList.NAME + ownerName + ColorList.TOP_SEPERATOR + " --");
        infoList.add(ColorList.REG + "Ban occurred " + ColorList.DATA + DateHelper.getFormattedRelativeDate(System.currentTimeMillis() - banToView.getTimeStamp()) + ColorList.REG + " ago.");
        infoList.add(ColorList.REG + "Ban was for " + ColorList.DATA + banToView.getReason());
        infoList.add(ColorList.NAME + ownerName + ColorList.REG + (PermissionsHelper.userInGroup(ownerName, "Banned") ? " is still banned" : " is not currently banned"));
        infoList.add(ColorList.NAME + ownerName + ColorList.REG + " was " + ColorList.DATA + ArrayHelpers.combinedWithSeperator(banToView.getOldGroups(), ", ") + ColorList.REG + " before they were banned.");
        if (isBannerAvalible) {
            infoList.add(ColorList.NAME + ownerName + ColorList.REG + " was banned by " + ColorList.NAME + banToView.getBanner());
        }
        if (!banToView.isConsoleBan()) {
            infoList.add(ColorList.REG + "To see where " + ColorList.NAME + ownerName + ColorList.REG + " was banned type " + ColorList.CMD + "/bd " + ColorList.SUBCMD + "tp " + ColorList.NAME + ownerName + " " + ColorList.ARGS + banNumber);
        }
        return infoList.toArray(new String[infoList.size()]);
    }
}
