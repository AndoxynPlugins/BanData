package net.daboross.bukkitdev.bandata;

import java.util.logging.Level;
import net.daboross.bukkitdev.playerdata.PlayerDataBukkit;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author daboross
 */
public class BanData extends JavaPlugin {

    private PlayerDataBukkit playerData;
    private BanDataCommandExecutor bdce;
    private static BanData currentInstance;
    private BanCheckReloader banCheckReloader;
    private BanDataJoinListener bdjl;

    @Override
    public void onEnable() {
        Plugin playerDataPlugin = Bukkit.getPluginManager().getPlugin("PlayerData");
        if (playerDataPlugin == null) {
            getLogger().log(Level.SEVERE, "PlayerData Not Found!");
        } else {
            if (playerDataPlugin instanceof PlayerDataBukkit) {
                playerData = (PlayerDataBukkit) playerDataPlugin;
            } else {
                getLogger().log(Level.SEVERE, "PlayerData not instanceof PlayerData!");
            }
        }
        if (playerData == null) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        bdce = new BanDataCommandExecutor(this);
        PluginCommand bandata = getCommand("bd");
        if (bandata == null) {
            getLogger().log(Level.WARNING, "/bd command not found! Is another plugin using it?");
        } else {
            bdce.registerCommand(bandata);
        }
//        playerData.getHandler().addCustomDataParsing("bandata", InfoParser.getInstance());
        banCheckReloader = new BanCheckReloader(this);
        banCheckReloader.goThrough();
        currentInstance = this; 
        bdjl = new BanDataJoinListener(this);
        Bukkit.getPluginManager().registerEvents(bdjl, this);
        getLogger().log(Level.INFO, "BanData Fully Enabled");
    }

    @Override
    public void onDisable() {
        currentInstance = null;
    }

    protected PlayerDataBukkit getPlayerData() {
        return playerData;
    }

    protected BanCheckReloader getBanCheckReloader() {
        return banCheckReloader;
    }

    protected static BanData getCurrentInstance() {
        return currentInstance;
    }
}
