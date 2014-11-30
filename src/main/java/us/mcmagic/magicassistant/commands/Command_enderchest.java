package us.mcmagic.magicassistant.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.utils.PlayerUtil;

public class Command_enderchest {

    public static void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only Players can use this!");
            return;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            Player tp = PlayerUtil.findPlayer(args[0]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            player.sendMessage(ChatColor.GREEN + "Now looking in "
                    + tp.getName() + "'s Enderchest!");
            player.openInventory(tp.getEnderChest());
            return;
        }
        player.sendMessage(ChatColor.RED + "/enderchest [Username]");
    }
}