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
        ArrayList<String> returnList = new ArrayList<String>();
        for (Ban b : bd.getBans()) {
            if (b.isConsoleBan()) {
                returnList.add("CBAN:");
                returnList.add(b.getReason());
                returnList.add(groupToString(b.getOldGroups()));
                returnList.add(String.valueOf(b.getTimeStamp()));
                returnList.add(";;;");
            } else {
                returnList.add("BAN:");
                returnList.add(b.getBanner());
                returnList.add(b.getReason());
                returnList.add(groupToString(b.getOldGroups()));
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

    private static String groupToString(String[] groups) {
        String returnV = "";
        for (String str : groups) {
            returnV += "," + str;
        }
        return returnV;
    }

    private static String[] stringToGroup(String groups) {
        char[] gC = groups.toCharArray();
        ArrayList<String> groupsFound = new ArrayList<String>();
        String currentGroup = "";
        for (int i = 0; i < gC.length; i++) {
            char current = gC[i];
            if (current == ',') {
                if (currentGroup.length() == 0) {
                    continue;
                }
                groupsFound.add(currentGroup);
                currentGroup = "";
                continue;
            }
            currentGroup += current;
        }
        if (!(currentGroup.length() == 0)) {
            groupsFound.add(currentGroup);
        }
        return groupsFound.toArray(new String[0]);
    }

    protected static BData parseFromlist(Data data) {
        if (data == null) {
            throw new IllegalArgumentException("Data Can't Be Null");
        }
        String[] strl = data.getData();
        String current = "finding";
        ArrayList<String> currentBan = new ArrayList<String>();
        ArrayList<Ban> banList = new ArrayList<Ban>();
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
                    if (currentBan.size() == 8) {
                        try {
                            Ban b;
                            String banner = currentBan.get(0);
                            String reason = currentBan.get(1);
                            String[] oldGroups = stringToGroup(currentBan.get(2));
                            long timeStamp = Long.valueOf(currentBan.get(3));
                            if (reason.equalsIgnoreCase("Unknown Reason")) {
                                b = new Ban(reason, oldGroups, timeStamp);
                            } else {
                                long xPos = Long.valueOf(currentBan.get(4));
                                long yPos = Long.valueOf(currentBan.get(5));
                                long zPos = Long.valueOf(currentBan.get(6));
                                String world = currentBan.get(7);
                                b = new Ban(banner, reason, oldGroups, xPos, yPos, zPos, world, timeStamp);
                            }
                            banList.add(b);
                        } catch (Exception e) {
                            BanData.getCurrentInstance().getLogger().log(Level.SEVERE, "Error Parsing Player Data!");
                        }
                    } else if (currentBan.size() == 7) {
                        Ban b;
                        String reason = currentBan.get(0);
                        String[] oldGroups = stringToGroup(currentBan.get(1));
                        long timeStamp = Long.valueOf(currentBan.get(2));
                        if (reason.equalsIgnoreCase("Unknown Reason")) {
                            b = new Ban(reason, oldGroups, timeStamp);
                        } else {
                            long xPos = Long.valueOf(currentBan.get(3));
                            long yPos = Long.valueOf(currentBan.get(4));
                            long zPos = Long.valueOf(currentBan.get(5));
                            String world = currentBan.get(6);
                            b = new Ban("Unknown", reason, oldGroups, xPos, yPos, zPos, world, timeStamp);
                        }
                        banList.add(b);
                    } else {
                        BanData.getCurrentInstance().getLogger().log(Level.SEVERE, "Error Parsing Player Data!");
                    }
                } else if (current.equalsIgnoreCase("cban")) {
                    if (currentBan.size() == 3) {
                        try {
                            String reason = currentBan.get(0);
                            String[] oldGroups = stringToGroup(currentBan.get(1));
                            long timeStamp = Long.valueOf(currentBan.get(2));
                            Ban b = new Ban(reason, oldGroups, timeStamp);
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
