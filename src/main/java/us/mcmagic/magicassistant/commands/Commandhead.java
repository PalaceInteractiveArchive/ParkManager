package us.mcmagic.magicassistant.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Created by Marc on 3/10/15
 */
public class Commandhead implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can do this!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
            SkullMeta sm = (SkullMeta) head.getItemMeta();
            sm.setOwner(args[0]);
            head.setItemMeta(sm);
            player.getInventory().addItem(head);
            player.sendMessage(ChatColor.BLUE + "Here's your head of " + ChatColor.AQUA + args[0] + "!");
            return true;
        }
        player.sendMessage(ChatColor.RED + "/head [Username]");
        return true;
    }
}
