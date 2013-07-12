/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.bukkitdev.bandata;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

/**
 *
 * @author daboross
 */
public class DataParser {

    public static String[] parseToList(final BData bd) {
        List<String> returnList = new ArrayList<String>();
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
        return returnList.toArray(new String[returnList.size()]);
    }

    private static String groupToString(final String[] groups) {
        StringBuilder returnBuilder = new StringBuilder();
        for (String str : groups) {
            returnBuilder.append(",").append(str);
        }
        return returnBuilder.toString();
    }

    private static String[] stringToGroup(final String groups) {
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
        return groupsFound.toArray(new String[groupsFound.size()]);
    }

    public static BData parseFromlist(final String[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Data Can't Be Null");
        }
        String current = "finding";
        ArrayList<String> currentBan = new ArrayList<String>();
        ArrayList<Ban> banList = new ArrayList<Ban>();
        for (int i = 0; i < data.length; i++) {
            String currentString = data[i];
            char[] currentCharList = currentString.toCharArray();
            if (current.equalsIgnoreCase("finding")) {
                if (currentCharList.length > 0) {
                    if (currentCharList[currentCharList.length - 1] == ':') {
                        current = currentString.substring(0, (currentString.length() - 1)).toLowerCase(Locale.ENGLISH);
                    } else {
                        BanData.getCurrentInstance().getLogger().log(Level.SEVERE, "Error Parsing Player Data!");
                        return null;
                    }
                }
            } else if (currentString.equals(";;;")) {
                if (current.equalsIgnoreCase("ban")) {
                    if (currentBan.size() == 8) {
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
                        String reason = currentBan.get(0);
                        String[] oldGroups = stringToGroup(currentBan.get(1));
                        long timeStamp = Long.valueOf(currentBan.get(2));
                        Ban b = new Ban(reason, oldGroups, timeStamp);
                        banList.add(b);

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
        BData bData = new BData(banList.toArray(new Ban[banList.size()]));
        return bData;
    }

    protected static BData[] parseAll(final String[][] datas) {
        BData[] returnList = new BData[datas.length];
        for (int i = 0; i < datas.length; i++) {
            returnList[i] = parseFromlist(datas[i]);
        }
        return returnList;
    }

    protected static BData[] parseAll(final List<String[]> datas) {
        BData[] returnList = new BData[datas.size()];
        for (int i = 0; i < datas.size(); i++) {
            returnList[i] = parseFromlist(datas.get(i));
        }
        return returnList;
    }
}
