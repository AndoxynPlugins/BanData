package net.daboross.bukkitdev.bandata;

import java.util.ArrayList;
import java.util.logging.Level;
import net.daboross.bukkitdev.playerdata.Data;

/**
 *
 * @author daboross
 */
public class DataParser {

    protected static String[] parseToList(BData bd) {
        ArrayList<String> returnList = new ArrayList<>();
        for (Ban b : bd.getBans()) {
            if (b.isConsoleBan()) {
                returnList.add("CBAN:");
                returnList.add(b.getReason());
                returnList.add(b.getOldGroup());
                returnList.add(String.valueOf(b.getTimeStamp()));
                returnList.add(";;;");
            } else {
                returnList.add("BAN:");
                returnList.add(b.getReason());
                returnList.add(b.getOldGroup());
                returnList.add(String.valueOf(b.getTimeStamp()));
                returnList.add(String.valueOf(b.getXPos()));
                returnList.add(String.valueOf(b.getYPos()));
                returnList.add(String.valueOf(b.getZPos()));
                returnList.add(String.valueOf(b.getWorld()));
                returnList.add(";;;");
            }
        }
        return returnList.toArray(new String[0]);
    }

    protected static BData parseFromlist(Data data) {
        if (data == null) {
            throw new IllegalArgumentException("Data Can't Be Null");
        }
        String[] strl = data.getData();
        String current = "finding";
        ArrayList<String> currentBan = new ArrayList<>();
        ArrayList<Ban> banList = new ArrayList<>();
        for (int i = 0; i < strl.length; i++) {
            String currentString = strl[i];
            char[] currentCharList = currentString.toCharArray();
            if (current.equalsIgnoreCase("finding")) {
                if (currentCharList.length > 0) {
                    if (currentCharList[currentCharList.length - 1] == ':') {
                        current = currentString.substring(0, (currentString.length() - 1)).toLowerCase();
                    } else {
                        BanData.getCurrentInstance().getLogger().log(Level.SEVERE, "Error Parsing Player Data!");
                        return null;
                    }
                }
            } else if (currentString.equals(";;;")) {
                if (current.equalsIgnoreCase("ban")) {
                    if (currentBan.size() == 7) {
                        try {
                            String reason = currentBan.get(0);
                            String oldGroup = currentBan.get(1);
                            long timeStamp = Long.valueOf(currentBan.get(2));
                            long xPos = Long.valueOf(currentBan.get(3));
                            long yPos = Long.valueOf(currentBan.get(4));
                            long zPos = Long.valueOf(currentBan.get(5));
                            String world = currentBan.get(6);
                            Ban b = new Ban(reason, oldGroup, xPos, yPos, zPos, world, timeStamp);
                            banList.add(b);
                        } catch (Exception e) {
                            BanData.getCurrentInstance().getLogger().log(Level.SEVERE, "Error Parsing Player Data!");
                        }
                    } else {
                        BanData.getCurrentInstance().getLogger().log(Level.SEVERE, "Error Parsing Player Data!");
                    }
                } else if (current.equalsIgnoreCase("cban")) {
                    if (currentBan.size() == 3) {
                        try {
                            String reason = currentBan.get(0);
                            String oldGroup = currentBan.get(1);
                            long timeStamp = Long.valueOf(currentBan.get(2));
                            Ban b = new Ban(reason, oldGroup, timeStamp);
                            banList.add(b);
                        } catch (Exception e) {
                            BanData.getCurrentInstance().getLogger().log(Level.SEVERE, "Error Parsing Player Data!");
                        }
                    } else {
                        BanData.getCurrentInstance().getLogger().log(Level.SEVERE, "Error Parsing Player Data!");
                    }
                }
                currentBan.clear();
                current = "finding";
            } else {
                currentBan.add(currentString);
            }
        }
        BData bData = new BData(banList.toArray(new Ban[0]), data.getOwner());
        return bData;
    }

    protected static BData[] parseAll(Data[] data) {
        BData[] returnList = new BData[data.length];
        for (int i = 0; i < data.length; i++) {
            returnList[i] = parseFromlist(data[i]);
        }
        return returnList;
    }
}
