/*
 * Copyright (C) 2013-2014 Dabo Ross <http://www.daboross.net/>
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

public class Ban {

    private final String reason;
    private final long xPos;
    private final long yPos;
    private final long zPos;
    private final long timeStamp;
    private final boolean isConsoleBan;
    private final String world;
    private final String[] oldGroups;
    private final String banner;

    public Ban(String banner, String reason, String[] oldGroups, long xPos, long yPos, long zPos, String world, long timeStamp) {
        this.reason = reason;
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
        this.world = world;
        this.timeStamp = timeStamp;
        this.isConsoleBan = false;
        this.oldGroups = oldGroups;
        this.banner = banner;
    }

    public Ban(String reason, String oldGroups[], long timeStamp) {
        this.reason = reason;
        this.xPos = 0;
        this.yPos = 0;
        this.zPos = 0;
        this.world = "";
        this.isConsoleBan = true;
        this.timeStamp = timeStamp;
        this.oldGroups = oldGroups;
        this.banner = "Console";
    }

    public String getReason() {
        return reason;
    }

    public long getXPos() {
        return xPos;
    }

    public long getYPos() {
        return yPos;
    }

    public long getZPos() {
        return zPos;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public boolean isConsoleBan() {
        return isConsoleBan;
    }

    public String getWorld() {
        return world;
    }

    public String[] getOldGroups() {
        return oldGroups;
    }

    public String getBanner() {
        return banner;
    }
}
