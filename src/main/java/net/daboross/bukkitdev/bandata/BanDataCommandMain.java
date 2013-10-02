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

import net.daboross.bukkitdev.bandata.commandreactors.BanCommand;
import net.daboross.bukkitdev.bandata.commandreactors.BanInfoCommand;
import net.daboross.bukkitdev.bandata.commandreactors.BanClearCommand;
import net.daboross.bukkitdev.bandata.commandreactors.BanTpCommand;
import net.daboross.bukkitdev.bandata.commandreactors.CheckBansCommand;
import net.daboross.bukkitdev.bandata.commandreactors.ListCommand;
import net.daboross.bukkitdev.bandata.commandreactors.UnBanCommand;
import net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase;
import net.daboross.bukkitdev.playerdata.api.PlayerDataPlugin;
import net.daboross.bukkitdev.playerdata.api.PlayerHandler;
import org.bukkit.command.PluginCommand;

/**
 *
 * @author daboross
 */
public class BanDataCommandMain {

    private final CommandExecutorBase commandExecutorBase;
    private final BanDataPlugin plugin;

    protected BanDataCommandMain( BanDataPlugin plugin ) {
        this.plugin = plugin;
        commandExecutorBase = new CommandExecutorBase( "bandata.help" );
        commandExecutorBase.addSubCommand( new BanCommand( plugin ) );
        commandExecutorBase.addSubCommand( new BanInfoCommand( plugin ) );
        commandExecutorBase.addSubCommand( new BanTpCommand( plugin ) );
        commandExecutorBase.addSubCommand( new ListCommand( plugin ) );
        commandExecutorBase.addSubCommand( new CheckBansCommand( plugin ) );
        commandExecutorBase.addSubCommand( new UnBanCommand( plugin ) );
        commandExecutorBase.addSubCommand( new BanClearCommand( plugin ) );
    }

    protected void registerCommand( PluginCommand command ) {
        command.setExecutor( commandExecutorBase );
    }
}
