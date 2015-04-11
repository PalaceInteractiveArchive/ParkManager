package us.mcmagic.magicassistant.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

public class Commandinvcheck {

    public static void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            checkInventories();
            Bukkit.broadcast(ChatColor.GOLD
                            + "All banned items were removed by "
                            + ChatColor.DARK_GREEN + "Utilidors",
                    "magicassistant.invcheck");
        } else {
            Player player = (Player) sender;
            checkInventories();
            Bukkit.broadcast(ChatColor.GOLD
                            + "All banned items were removed by "
                            + ChatColor.DARK_GREEN + player.getName(),
                    "magicassistant.invcheck");
        }
    }

    public static void checkInventories() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerInventory pi = player.getInventory();
            Inventory pec = player.getEnderChest();
            if (!(player.hasPermission("magicassistant.invcheck.bypass"))) {
                pi.remove(Material.TNT);
                pi.remove(Material.PUMPKIN);
                pi.remove(Material.JACK_O_LANTERN);
                pi.remove(Material.DIODE);
                pi.remove(Material.IRON_INGOT);
                pi.remove(Material.REDSTONE_COMPARATOR);
                pi.remove(Material.STONE_PLATE);
                pi.remove(Material.WOOD_PLATE);
                pi.remove(Material.BOAT);
                pi.remove(Material.TORCH);
                pi.remove(Material.RAILS);
                pi.remove(Material.POWERED_RAIL);
                pi.remove(Material.DETECTOR_RAIL);
                pi.remove(Material.ACTIVATOR_RAIL);
                pi.remove(Material.MILK_BUCKET);
                pi.remove(Material.LEVER);
                pi.remove(Material.COMMAND);
                pi.remove(Material.MINECART);
                pi.remove(Material.CACTUS);
                pi.remove(Material.WORKBENCH);
                pi.remove(Material.WOOD);
                pi.remove(Material.WOOD_BUTTON);
                pi.remove(Material.STONE_BUTTON);
                pi.remove(Material.REDSTONE_TORCH_ON);
                pi.remove(Material.REDSTONE_TORCH_OFF);
                pi.remove(Material.REDSTONE);
                pi.remove(Material.ITEM_FRAME);
                pi.remove(Material.FIREWORK);
                pi.remove(Material.PAINTING);
                pi.remove(Material.LAVA_BUCKET);
                pi.remove(Material.WATER_BUCKET);
                pi.remove(Material.BUCKET);
                pi.remove(Material.GOLD_SWORD);
                pi.remove(Material.ENCHANTED_BOOK);
                pi.remove(Material.STONE);
                pi.remove(Material.GRASS);
                pi.remove(Material.ARROW);
                pi.remove(Material.BOW);
                pi.remove(Material.EXP_BOTTLE);
                pi.remove(Material.LEASH);
                pi.remove(Material.WOOD_AXE);
                pi.remove(Material.STONE_AXE);
                pi.remove(Material.IRON_AXE);
                pi.remove(Material.DIAMOND_AXE);
                pi.remove(Material.GOLD_AXE);
                pi.remove(Material.BRICK);
                pi.remove(Material.WOOD_PICKAXE);
                pi.remove(Material.STONE_PICKAXE);
                pi.remove(Material.IRON_PICKAXE);
                pi.remove(Material.DIAMOND_PICKAXE);
                pi.remove(Material.GOLD_PICKAXE);
                pi.remove(Material.FIREWORK_CHARGE);
                pec.remove(Material.TNT);
                pec.remove(Material.DIODE);
                pec.remove(Material.IRON_INGOT);
                pec.remove(Material.REDSTONE_COMPARATOR);
                pec.remove(Material.STONE_PLATE);
                pec.remove(Material.WOOD_PLATE);
                pec.remove(Material.PUMPKIN);
                pec.remove(Material.JACK_O_LANTERN);
                pec.remove(Material.BOAT);
                pec.remove(Material.TORCH);
                pec.remove(Material.RAILS);
                pec.remove(Material.POWERED_RAIL);
                pec.remove(Material.DETECTOR_RAIL);
                pec.remove(Material.ACTIVATOR_RAIL);
                pec.remove(Material.MILK_BUCKET);
                pec.remove(Material.LEVER);
                pec.remove(Material.COMMAND);
                pec.remove(Material.MINECART);
                pec.remove(Material.CACTUS);
                pec.remove(Material.WORKBENCH);
                pec.remove(Material.WOOD);
                pec.remove(Material.WOOD_BUTTON);
                pec.remove(Material.STONE_BUTTON);
                pec.remove(Material.REDSTONE_TORCH_ON);
                pec.remove(Material.REDSTONE_TORCH_OFF);
                pec.remove(Material.REDSTONE);
                pec.remove(Material.ITEM_FRAME);
                pec.remove(Material.FIREWORK);
                pec.remove(Material.PAINTING);
                pec.remove(Material.LAVA_BUCKET);
                pec.remove(Material.WATER_BUCKET);
                pec.remove(Material.BUCKET);
                pec.remove(Material.GOLD_SWORD);
                pec.remove(Material.ENCHANTED_BOOK);
                pec.remove(Material.STONE);
                pec.remove(Material.GRASS);
                pec.remove(Material.ARROW);
                pec.remove(Material.BOW);
                pec.remove(Material.EXP_BOTTLE);
                pec.remove(Material.LEASH);
                pec.remove(Material.WOOD_AXE);
                pec.remove(Material.STONE_AXE);
                pec.remove(Material.IRON_AXE);
                pec.remove(Material.DIAMOND_AXE);
                pec.remove(Material.GOLD_AXE);
                pec.remove(Material.BRICK);
                pec.remove(Material.WOOD_PICKAXE);
                pec.remove(Material.STONE_PICKAXE);
                pec.remove(Material.IRON_PICKAXE);
                pec.remove(Material.DIAMOND_PICKAXE);
                pec.remove(Material.GOLD_PICKAXE);
                pec.remove(Material.FIREWORK_CHARGE);
            }
        }
    }
}