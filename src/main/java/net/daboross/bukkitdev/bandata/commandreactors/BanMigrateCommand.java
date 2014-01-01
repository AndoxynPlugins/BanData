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
package net.daboross.bukkitdev.bandata.commandreactors;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import net.daboross.bukkitdev.bandata.BData;
import net.daboross.bukkitdev.bandata.Ban;
import net.daboross.bukkitdev.bandata.BanDataPlugin;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.mysqlmap.SQLConnectionInfo;
import net.daboross.bukkitdev.mysqlmap.SQLDatabaseConnection;
import net.daboross.bukkitdev.mysqlmap.api.DatabaseConnection;
import net.daboross.bukkitdev.mysqlmap.api.MapTable;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class BanMigrateCommand extends SubCommand {

    private final BanDataPlugin plugin;
    private JSONObject config;
    private static DatabaseConnection connection;
    private MapTable<String, String> table;

    public BanMigrateCommand(BanDataPlugin plugin) {
        super("migrate", true, "bandata.banmigrate", "Copy database to SQL");
        this.plugin = plugin;
        File configFile = new File(plugin.getDataFolder(), "shared-config.json");
        if (!configFile.exists()) {
            config = new JSONObject();
        } else {
            try {
                try (FileInputStream fileInputStream = new FileInputStream(configFile)) {
                    config = new JSONObject(new JSONTokener(fileInputStream));
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Unexpected IOException", e);
            }
        }
        SQLConnectionInfo info = new SQLConnectionInfo(config.getString("sql.host"), config.getInt("sql.port"), config.getString("sql.database"), config.getString("sql.username"), config.getString("sql.password"));
        try {
            connection = new SQLDatabaseConnection(plugin, info);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Unexpected SQLException", e);
        }
        table = connection.getStringToStringTable("bandata");
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if (config == null) {
            sender.sendMessage("Failed to load shared config");
            return;
        }
        List<? extends PlayerData> banned = plugin.getPlayerData().getHandler().getAllPlayerDatasWithExtraData(("bandata"));
        for (PlayerData pd : banned) {
            BData bans = plugin.getParser().parseFromlist(pd.getExtraData("bandata"));
            JSONObject bansObject = new JSONObject();
            JSONArray bansArray = new JSONArray();
            bansObject.put("bans", bansArray);
            for (Ban ban : bans.getBans()) {
                JSONObject banObject = new JSONObject();
                bansArray.put(banObject);
                banObject.put("reason", ban.getReason());
                banObject.put("timestamp", ban.getTimeStamp());
                banObject.put("isConsoleBan", ban.isConsoleBan());
                banObject.put("oldGroups", new JSONArray(ban.getOldGroups()));
                banObject.put("xPos", ban.getXPos());
                banObject.put("yPos", ban.getYPos());
                banObject.put("zPos", ban.getZPos());
                banObject.put("world", ban.getWorld());
                banObject.put("server", "main");
            }
            table.set(pd.getUsername().toLowerCase(), bansObject.toString(), null);
        }
    }

    public static DatabaseConnection getConnection() {
        return connection;
    }
}