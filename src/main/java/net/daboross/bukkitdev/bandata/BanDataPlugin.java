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

import java.io.IOException;
import java.util.logging.Level;
import net.daboross.bukkitdev.playerdata.api.PlayerDataPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

/**
 *
 * @author daboross
 */
public class BanDataPlugin extends JavaPlugin {

    private PlayerDataPlugin playerDataPlugin;
    private BanDataCommandExecutor bdce;
    private static BanDataPlugin currentInstance;
    private BanCheckReloader banCheckReloader;
    private BanDataJoinListener bdjl;

    @Override
    public void onEnable() {
        Plugin pluginPlayerData = Bukkit.getPluginManager().getPlugin("PlayerData");
        if (pluginPlayerData == null) {
            getLogger().log(Level.SEVERE, "PlayerData Not Found!");
        } else {
            if (pluginPlayerData instanceof PlayerDataPlugin) {
                playerDataPlugin = (PlayerDataPlugin) pluginPlayerData;
            } else {
                getLogger().log(Level.SEVERE, "PlayerData not instanceof PlayerData!");
            }
        }
        if (playerDataPlugin == null) {
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
        banCheckReloader = new BanCheckReloader(this);
        banCheckReloader.goThrough();
        currentInstance = this;
        bdjl = new BanDataJoinListener(this);
        Bukkit.getPluginManager().registerEvents(bdjl, this);
        MetricsLite metrics = null;
        try {
            metrics = new MetricsLite(this);
        } catch (IOException ex) {
            getLogger().warning("Unable to create Metrics");
        }
        if (metrics != null) {
            metrics.start();
        }
        getLogger().log(Level.INFO, "BanData Fully Enabled");
    }

    @Override
    public void onDisable() {
        currentInstance = null;
    }

    protected PlayerDataPlugin getPlayerData() {
        return playerDataPlugin;
    }

    protected BanCheckReloader getBanCheckReloader() {
        return banCheckReloader;
    }

    protected static BanDataPlugin getCurrentInstance() {
        return currentInstance;
    }
}