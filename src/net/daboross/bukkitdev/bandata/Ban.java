package net.daboross.bukkitdev.bandata;

/**
 *
 * @author daboross
 */
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
