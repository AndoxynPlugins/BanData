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
package net.daboross.bukkitdev.bandata.commandreactors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.daboross.bukkitdev.bandata.BData;
import net.daboross.bukkitdev.bandata.Ban;
import net.daboross.bukkitdev.bandata.BanDataPlugin;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.daboross.bukkitdev.playerdata.api.PlayerDataStatic;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class UnBanCommand extends SubCommand {

    private final BanDataPlugin plugin;

    public UnBanCommand( BanDataPlugin plugin ) {
        super( "unban", true, "bandata.unban", "Unbans the given player" );
        addArgumentNames( "Player" );
        this.plugin = plugin;
    }

    @Override
    public void runCommand( CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs ) {
        if ( subCommandArgs.length < 1 ) {
            sender.sendMessage( ColorList.ERR + "Please specify a player." );
            sender.sendMessage( getHelpMessage( baseCommandLabel, subCommandLabel ) );
            return;
        }
        if ( subCommandArgs.length > 1 ) {
            sender.sendMessage( ColorList.ERR + "To many arguments" );
            sender.sendMessage( getHelpMessage( baseCommandLabel, subCommandLabel ) );
            return;
        }
        PlayerData pd = plugin.getPlayerData().getHandler().getPlayerDataPartial( subCommandArgs[0] );
        if ( pd == null ) {
            sender.sendMessage( ColorList.ERR + "Player " + ColorList.ERR_ARGS + subCommandArgs[0] + ColorList.ERR + " not found" );
            return;
        }
        Permission p = PlayerDataStatic.getPermissionHandler();
        if ( !p.playerInGroup( (String) null, pd.getUsername(), "Banned" ) ) {
            sender.sendMessage( ColorList.ERR_ARGS + pd.getUsername() + ColorList.ERR + " is not currently banned" );
            return;
        }
        String[] data = pd.getExtraData( "bandata" );
        if ( data == null ) {
            sender.sendMessage( ColorList.ERR + "No bandata found for player " + ColorList.ERR_ARGS + pd.getUsername() );
            return;
        }
        BData banData = plugin.getParser().parseFromlist( data );
        if ( banData == null ) {
            sender.sendMessage( ColorList.ERR + "Error parsing BanData" );
            return;
        }
        Ban[] bans = banData.getBans();
        String[] permissionGroupsToSet = null;
        for ( int i = bans.length - 1 ; i >= 0 ; i-- ) {
            Ban b = bans[i];
            String[] oldGroups = b.getOldGroups();
            if ( oldGroups.length == 0 ) {
                continue;
            }
            List<String> groups = new ArrayList<String>();
            for ( String str : oldGroups ) {
                if ( !( str.equalsIgnoreCase( "banned" ) ) || ( groups.contains( str ) ) ) {
                    groups.add( str );
                }
            }
            if ( !groups.isEmpty() ) {
                permissionGroupsToSet = groups.toArray( new String[ groups.size() ] );
                break;
            }
        }
        if ( permissionGroupsToSet == null ) {
            sender.sendMessage( ColorList.ERR + "Error parsing BanData. No previous groups found" );
            return;
        }
        if ( !( PlayerDataStatic.isPermissionLoaded() ) ) {
            sender.sendMessage( ColorList.ERR + "Vault permission handler not found" );
        }
        List<String> rankRecord;
        if ( pd.hasExtraData( "rankrecord" ) ) {
            rankRecord = new ArrayList<String>( Arrays.asList( pd.getExtraData( "rankrecord" ) ) );
        } else {
            rankRecord = new ArrayList<String>();
        }
        PlayerDataStatic.getPermissionHandler().playerRemoveGroup( (String) null, pd.getUsername(), "Banned" );
        for ( String group : permissionGroupsToSet ) {
            PlayerDataStatic.getPermissionHandler().playerAddGroup( (String) null, pd.getUsername(), group );
        }
        rankRecord.add( "SET " + sender.getName() + " " + Arrays.toString( permissionGroupsToSet ) + " " + System.currentTimeMillis() );
        String[] finalRankRecord = rankRecord.toArray( new String[ rankRecord.size() ] );
        pd.addExtraData( "rankrecord", finalRankRecord );
        Bukkit.getServer().broadcastMessage( String.format( ColorList.BROADCAST_NAME_FORMAT, "BanData" ) + ColorList.NAME + pd.getUsername() + ColorList.BROADCAST + " was unbanned by " + ColorList.NAME + sender.getName() );
        sender.sendMessage( ColorList.NAME + pd.getUsername() + " has been set to: " + getString( permissionGroupsToSet ) );
    }

    private String getString( String[] strings ) {
        if ( strings.length == 0 ) {
            return "None";
        }
        StringBuilder resultBuilder = new StringBuilder( strings[0] );
        for ( int i = 1 ; i < strings.length ; i++ ) {
            String string = strings[i];
            resultBuilder.append( ", " ).append( string );
        }
        return resultBuilder.toString();
    }
}
