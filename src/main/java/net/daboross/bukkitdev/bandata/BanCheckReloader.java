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

import java.util.List;
import java.util.logging.Level;
import net.daboross.bukkitdev.playerdata.PlayerDataStatic;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.milkbowl.vault.permission.Permission;

/**
 *
 * @author daboross
 */
public class BanCheckReloader {

    private BanData main;

    public BanCheckReloader(BanData bd) {
        main = bd;
    }

    /**
     * This goes through all the PDatas and records ban info on every one that
     * is banned.
     */
    public void goThrough() {
        List<? extends PlayerData> playerDatas = main.getPlayerData().getHandler().getAllPlayerDatas();
        Permission permissionHandler = PlayerDataStatic.getPermissionHandler();
        for (int i = 0; i < playerDatas.size(); i++) {
            PlayerData current = playerDatas.get(i);
            if (permissionHandler.playerInGroup((String) null, current.getUsername(), "Banned")) {
                if (!current.hasExtraData("bandata")) {
                    current.addExtraData("bandata", DataParser.parseToList(new BData(new Ban[]{new Ban("Unknown Reason", new String[]{"Basic"}, System.currentTimeMillis())})));
                    main.getLogger().log(Level.INFO, "{0} has an Unrecorded Ban!", current.getUsername());
                }
            }
        }
    }
}
