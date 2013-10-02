/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.bandata.commandreactors;

import net.daboross.bukkitdev.bandata.BanDataPlugin;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class CheckBansCommand extends SubCommand {

    private final BanDataPlugin plugin;

    public CheckBansCommand( BanDataPlugin plugin ) {
        super( "checkBans", true, "bandata.admin", "This Command Checks For Users Who Are Banned, But Not In The DataBase" );
        this.plugin = plugin;
    }

    @Override
    public void runCommand( CommandSender cs, Command cmnd, String string, String string1, String[] strings ) {
        plugin.getBanCheckReloader().goThrough();
    }
}
