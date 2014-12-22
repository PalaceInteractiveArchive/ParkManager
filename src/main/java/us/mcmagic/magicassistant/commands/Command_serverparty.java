package us.mcmagic.magicassistant.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.utils.BandUtil;

/**
 * Created by Marc on 12/21/14
 */
public class Command_serverparty {

    public static void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this!");
            return;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "Creating Party for your server...");
            BandUtil.createParty();
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("MagicAssistant"), new Runnable() {
                @Override
                public void run() {
                    BandUtil.askForParty();
                }
            }, 100L);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("refresh")) {
                player.sendMessage(ChatColor.GREEN + "Refreshing Party Data...");
                BandUtil.askForParty();
                return;
            }
            if (args[0].equalsIgnoreCase("stop")) {
                player.sendMessage(ChatColor.RED + "Deleting Party Data...");
                BandUtil.removeParty();
            }
        }
    }
}
