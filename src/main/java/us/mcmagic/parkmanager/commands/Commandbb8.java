package us.mcmagic.parkmanager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.parkmanager.bb8.BB8;

/**
 * Created by Marc on 5/30/16
 */
public class Commandbb8 implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        BB8 bb8 = new BB8(player.getLocation());
        bb8.setOwner(player);
        return true;
    }
}