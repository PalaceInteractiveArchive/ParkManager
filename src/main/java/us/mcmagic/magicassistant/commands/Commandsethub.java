package us.mcmagic.magicassistant.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;

/**
 * Created by Marc on 3/10/15
 */
public class Commandsethub implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this!");
            return true;
        }
        Player player = (Player) sender;
        if (player.isOp()) {
            Location loc = player.getLocation();
            MagicAssistant.getInstance().setHub(loc);
            player.sendMessage(ChatColor.DARK_AQUA + "The hub location has been set!");
        } else {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
        }
        return true;
    }
}