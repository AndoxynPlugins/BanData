package net.daboross.bukkitdev.bandata;

/**
 *
 * @author daboross
 */
public class Ban {

    private String reason;
    private long xPos;
    private long yPos;
    private long zPos;
    private long timeStamp;
    private boolean isConsoleBan;
    private String world;
    private String[] oldGroups;

    protected Ban(String reason, String[] oldGroups, long xPos, long yPos, long zPos, String world, long timeStamp) {
        this.reason = reason;
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
        this.world = world;
        this.timeStamp = timeStamp;
        this.isConsoleBan = false;
        this.oldGroups = oldGroups;
    }

    protected Ban(String reason, String oldGroups[], long timeStamp) {
        this.reason = reason;
        this.isConsoleBan = true;
        this.timeStamp = timeStamp;
        this.oldGroups = oldGroups;
    }

    protected String getReason() {
        return reason;
    }

    protected long getXPos() {
        return xPos;
    }

    protected long getYPos() {
        return yPos;
    }

    protected long getZPos() {
        return zPos;
    }

    protected long getTimeStamp() {
        return timeStamp;
    }

    protected boolean isConsoleBan() {
        return isConsoleBan;
    }

    protected String getWorld() {
        return world;
    }

    protected String[] getOldGroups() {
        return oldGroups;
    }
}
