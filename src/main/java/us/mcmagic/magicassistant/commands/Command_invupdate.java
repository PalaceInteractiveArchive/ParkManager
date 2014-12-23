package us.mcmagic.magicassistant.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.utils.InventorySql;

/**
 * Created by Marc on 12/23/14
 */
public class Command_invupdate {

    public static void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only Players can use this!");
            return;
        }
        final Player player = (Player) sender;
        player.sendMessage(ChatColor.GREEN + "Updating Inventory...");
        Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("MagicAssistant"), new Runnable() {
            public void run() {
                if (!InventorySql.playerDataContainsPlayer(player)) {
                    player.performCommand("spawn");
                    InventorySql.setupData(player);
                } else {
                    player.getInventory().setContents(
                            InventorySql.invContents(player));
                    player.getInventory().setArmorContents(
                            InventorySql.armorContents(player));
                }
                if (!InventorySql.endPlayerDataContainsPlayer(player)) {
                    InventorySql.setupEndData(player);
                } else {
                    player.getEnderChest().setContents(
                            InventorySql.endInvContents(player));
                }
                player.sendMessage(net.md_5.bungee.api.ChatColor.GREEN + "Inventory Updated!");
            }
        });
    }
}
