package net.daboross.bukkitdev.bandata;

import java.util.logging.Level;
import net.daboross.bukkitdev.playerdata.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author daboross
 */
public class BanData extends JavaPlugin {

    private PlayerData playerData;
    private BanDataCommandExecutor bdce;
    private static BanData currentInstance;

    @Override
    public void onEnable() {
        Plugin playerDataPlugin = Bukkit.getPluginManager().getPlugin("PlayerData");
        if (playerDataPlugin == null) {
            getLogger().log(Level.SEVERE, "PlayerData Not Found!");
        } else {
            if (playerDataPlugin instanceof PlayerData) {
                playerData = (PlayerData) playerDataPlugin;
            } else {
                getLogger().log(Level.SEVERE, "PlayerData Not Instance Of Player Data!");
            }
        }
        if (playerData == null) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        bdce = new BanDataCommandExecutor(this);
        PluginCommand bd = getCommand("bd");
        if (bd != null) {
            bd.setExecutor(bdce);
        } else {
            getLogger().log(Level.SEVERE, "Ban Data Command Not Found!");
        }
        playerData.getHandler().addCustomDataParsing("bandata", InfoParser.getInstance());
        currentInstance = this;
    }

    @Override
    public void onDisable() {
        if (playerData == null) {
            return;
        }
        currentInstance = null;
    }

    protected PlayerData getPlayerData() {
        return playerData;
    }

    protected static BanData getCurrentInstance() {
        return currentInstance;
    }
}
