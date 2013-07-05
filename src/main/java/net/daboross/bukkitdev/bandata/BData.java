package net.daboross.bukkitdev.bandata;

/**
 *
 * @author daboross
 */
public class BData {

    private Ban[] bans;

    public BData(Ban[] b) {
        this.bans = b;
    }

    public void addBan(Ban b) {
        Ban[] newBans = new Ban[bans.length + 1];
        System.arraycopy(bans, 0, newBans, 0, bans.length);
        newBans[newBans.length - 1] = b;
        bans = newBans;
    }

    public Ban[] getBans() {
        return bans;
    }
}
