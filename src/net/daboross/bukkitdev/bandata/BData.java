package net.daboross.bukkitdev.bandata;

import net.daboross.bukkitdev.playerdata.PData;

/**
 *
 * @author daboross
 */
public class BData {

    private Ban[] bans;
    private PData owner;

    protected BData(Ban[] b, PData owner) {
        this.bans = b;
        this.owner = owner;
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

    protected PData getOwner() {
        return owner;
    }
}
