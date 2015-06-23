package us.mcmagic.magicassistant.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;

/**
 * Created by Marc on 12/21/14
 */
public class Commandserverparty implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "Creating Party for your server...");
            MagicAssistant.bandUtil.createParty();
            Bukkit.getScheduler().runTaskLater(MagicAssistant.getInstance(), new Runnable() {
                @Override
                public void run() {
                    MagicAssistant.bandUtil.askForParty();
                }
            }, 100L);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("refresh")) {
                player.sendMessage(ChatColor.GREEN + "Refreshing Party Data...");
                MagicAssistant.bandUtil.askForParty();
                return true;
            }
            if (args[0].equalsIgnoreCase("stop")) {
                player.sendMessage(ChatColor.RED + "Deleting Party Data...");
                MagicAssistant.bandUtil.removeParty();
            }
        }
        return true;
    }
}
