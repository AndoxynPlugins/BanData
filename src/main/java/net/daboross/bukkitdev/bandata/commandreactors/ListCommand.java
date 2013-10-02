/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.bandata.commandreactors;

import java.util.ArrayList;
import java.util.List;
import net.daboross.bukkitdev.bandata.BData;
import net.daboross.bukkitdev.bandata.BanDataPlugin;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.playerdata.api.PermissionsHelper;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class ListCommand extends SubCommand {

    private final BanDataPlugin plugin;

    public ListCommand( BanDataPlugin plugin ) {
        super( "list", new String[]{
            "l"
        }, true, "bandata.listbans", new String[]{
            "PageNumber"
        }, "This Command Lists All Players Who Have Been Banned and How Many Times They have Been Banned" );
        this.plugin = plugin;
    }

    @Override
    public void runCommand( CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs ) {
        if ( subCommandArgs.length > 1 ) {
            sender.sendMessage( ColorList.ERR + "Please use only one number after " + ColorList.CMD + "/" + baseCommandLabel + ColorList.SUBCMD + " " + subCommandLabel );
        }
        int pageNumber;
        if ( subCommandArgs.length == 0 ) {
            pageNumber = 1;
        } else {
            try {
                pageNumber = Integer.valueOf( subCommandArgs[0] );
            } catch ( NumberFormatException nfe ) {
                sender.sendMessage( ColorList.ERR_ARGS + subCommandArgs[0] + ColorList.ERR + " is not an integer." );
                sender.sendMessage( getHelpMessage( baseCommandLabel, subCommandLabel ) );
                return;
            }
            if ( pageNumber == 0 ) {
                sender.sendMessage( ColorList.ERR_ARGS + subCommandArgs[0] + ColorList.ERR + " is not a non-0 integer." );
                return;
            } else if ( pageNumber < 0 ) {
                sender.sendMessage( ColorList.ERR_ARGS + subCommandArgs[0] + ColorList.ERR + " is not a positive integer." );
                return;
            }
        }
        int pageNumberReal = pageNumber - 1;
        List<? extends PlayerData> banned = plugin.getPlayerData().getHandler().getAllPlayerDatasWithExtraData( ( "bandata" ) );
        ArrayList<String> messagesToSend = new ArrayList<String>();
        messagesToSend.add( ColorList.TOP_SEPERATOR + " -- " + ColorList.TOP + "Ban List " + ColorList.TOP_SEPERATOR + "--" + ColorList.TOP + " Page " + ColorList.DATA + pageNumber + ColorList.TOP + "/" + ColorList.DATA + ( ( banned.size() / 6 ) + ( banned.size() % 6 == 0 ? 0 : 1 ) ) + ColorList.TOP_SEPERATOR + " --" );
        for ( int i = ( pageNumberReal * 6 ) ; i < ( ( pageNumberReal + 1 ) * 6 ) && i < banned.size() ; i++ ) {
            PlayerData current = banned.get( i );
            BData bans = plugin.getParser().parseFromlist( current.getExtraData( "bandata" ) );
            messagesToSend.add( ColorList.NAME + current.getUsername() + ColorList.REG
                    + " has " + ColorList.DATA + bans.getBans().length + ColorList.REG + ( ( bans.getBans().length == 1 ) ? " ban" : " bans" )
                    + ", and " + ( isBanned( current ) ? "is currently banned" : "is not currently banned" ) + "." );
        }
        if ( pageNumber < ( banned.size() / 6.0 ) ) {
            messagesToSend.add( ColorList.REG + "To view the next page type " + ColorList.CMD + "/" + baseCommandLabel + ColorList.SUBCMD + " " + subCommandLabel + ColorList.ARGS + " " + ( pageNumber + 1 ) );
        }
        sender.sendMessage( messagesToSend.toArray( new String[ messagesToSend.size() ] ) );
    }

    private boolean isBanned( PlayerData playerData ) {
        return PermissionsHelper.userInGroup( playerData.getUsername(), "Banned" );
    }
}
