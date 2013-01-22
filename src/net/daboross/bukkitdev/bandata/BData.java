package net.daboross.bukkitdev.bandata;

/**
 *
 * @author daboross
 */
public class BData {

    private Ban[] bans;

    protected BData(Ban[] b) {
        this.bans = b;
    }

    protected void addBan(Ban b) {
        Ban[] newBans = new Ban[bans.length + 1];
        System.arraycopy(bans, 0, newBans, 0, bans.length);
        newBans[newBans.length - 1] = b;
        bans = newBans;
    }

    protected Ban[] getBans() {
        return bans;
    }
}
