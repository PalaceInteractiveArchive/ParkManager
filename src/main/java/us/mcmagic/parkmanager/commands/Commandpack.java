package us.mcmagic.parkmanager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.parkmanager.ParkManager;

/**
 * Created by Marc on 3/20/15
 */
public class Commandpack implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        ParkManager.packManager.openMenu((Player) sender);
        return true;
    }
}
