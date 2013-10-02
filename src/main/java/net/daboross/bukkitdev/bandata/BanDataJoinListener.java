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

import java.util.logging.Level;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.playerdata.api.PermissionsHelper;
import net.daboross.bukkitdev.playerdata.api.events.PlayerDataPlayerJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 *
 * @author daboross
 */
public class BanDataJoinListener implements Listener {

    private final BanDataPlugin plugin;

    public BanDataJoinListener( BanDataPlugin main ) {
        this.plugin = main;
    }

    public void onPlayerDataPlayerJoin( PlayerDataPlayerJoinEvent pdpje ) {
        if ( PermissionsHelper.userInGroup( pdpje.getPlayerData().getUsername(), "Banned" ) ) {
            final String[] data = pdpje.getPlayerData().getExtraData( "bandata" );
            final Player p = pdpje.getPlayer();
            Runnable r;
            if ( data == null ) {
                String[] newData = plugin.getParser().parseToList( new BData( new Ban[]{
                    new Ban( "Unknown Reason", new String[]{
                        "Basic"
                    }, System.currentTimeMillis() )
                } ) );
                pdpje.getPlayerData().addExtraData( "bandata", newData );
                plugin.getLogger().log( Level.INFO, "{0} has an Unrecorded Ban!", p.getName() );
                r = new Runnable() {
                    @Override
                    public void run() {
                        p.sendMessage( ColorList.REG + "You are currently Banned! You can not build or destroy anything while you are banned." );
                        p.sendMessage( ColorList.REG + "To request an unban, please use " + ColorList.CMD + "/mad" );
                    }
                };
            } else {
                final BData bd = plugin.getParser().parseFromlist( data );
                r = new Runnable() {
                    @Override
                    public void run() {
                        p.sendMessage( ColorList.REG + "You are currently Banned! You can not build or destroy anything while you are banned." );
                        if ( bd != null ) {
                            int numberOfBans = bd.getBans().length;
                            if ( numberOfBans == 1 ) {
                                p.sendMessage( ColorList.REG + "To see the ban reason, use " + ColorList.CMD + "/bd " + ColorList.SUBCMD + "tp " + ColorList.ARGS + ( p.getName().length() <= 3 ? p.getName() : p.getName().substring( 0, 3 ) ) );
                            } else if ( numberOfBans > 1 ) {
                                p.sendMessage( ColorList.REG + "To see the ban reason, use " + ColorList.CMD + "/bd " + ColorList.SUBCMD + "tp " + ColorList.ARGS + ( p.getName().length() <= 3 ? p.getName() : p.getName().substring( 0, 3 ) ) + " " + numberOfBans );
                            }
                        }
                        p.sendMessage( ColorList.REG + "To request an unban, please use " + ColorList.CMD + "/mad" );
                    }
                };
            }
            Bukkit.getScheduler().runTaskLater( plugin, r, 15 );
        }
    }
}
