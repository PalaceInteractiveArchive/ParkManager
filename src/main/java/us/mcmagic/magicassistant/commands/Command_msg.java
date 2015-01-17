package us.mcmagic.magicassistant.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.utils.PlayerUtil;

/**
 * Created by Marc on 12/16/14
 */
public class Command_msg {

    public static void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "/" + label + " [Player] [Message]");
                return;
            }
            String msg = "";
            for (int i = 1; i < args.length; i++) {
                msg += args[i] + " ";
            }
            Player player = PlayerUtil.findPlayer(args[0]);
            if (player == null) {
                return;
            }
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.translateAlternateColorCodes('&', msg));
            return;
        }
        sender.sendMessage(ChatColor.RED + "Whoa, what'd you do? Stahp it!");
    }
}
