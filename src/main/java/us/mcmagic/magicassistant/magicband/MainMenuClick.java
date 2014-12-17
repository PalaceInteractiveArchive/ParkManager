package us.mcmagic.magicassistant.magicband;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.magicassistant.utils.InventoryType;
import us.mcmagic.magicassistant.utils.InventoryUtil;
import us.mcmagic.magicassistant.utils.VisibleUtil;

import java.util.Arrays;

/**
 * Created by Marc on 12/13/14
 */
public class MainMenuClick {

    @SuppressWarnings("deprecation")
    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();
        if (item.getType().equals(Material.SKULL_ITEM)) {
            return;
        }
        switch (item.getType()) {
            case MINECART:
                InventoryUtil.openInventory(player, InventoryType.RIDESANDATTRACTIONS);
                return;
            case FIREWORK:
                InventoryUtil.openInventory(player, InventoryType.SHOWSANDEVENTS);
                return;
            case BED:
                if (MagicAssistant.resortsServer) {
                    InventoryUtil.openInventory(player, InventoryType.HOTELSANDRESORTS);
                } else {
                    MagicAssistant.sendToServer(player, "Resorts");
                }
                return;
            case WOOL:
                byte data = item.getData().getData();
                if (data == 5) {
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 5);
                    player.sendMessage(ChatColor.GREEN + "You can now see players!");
                    player.closeInventory();
                    VisibleUtil.addToHideAll(player);
                } else {
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 5);
                    player.sendMessage(ChatColor.GREEN + "You can no longer see players!");
                    player.closeInventory();
                    VisibleUtil.removeFromHideAll(player);
                }
                return;
            case WATCH:
                ItemStack time = new ItemStack(Material.WATCH);
                ItemMeta tm = time.getItemMeta();
                tm.setDisplayName(ChatColor.GREEN + "Current Time in EST");
                tm.setLore(Arrays.asList(ChatColor.YELLOW + BandUtil.currentTime()));
                time.setItemMeta(tm);
                inv.setItem(4, time);
                return;
            case GOLD_BOOTS:
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "This feature is coming soon!");
                return;
            case POTATO_ITEM:
                InventoryUtil.openInventory(player, InventoryType.FOOD);
                return;
            case ENDER_PEARL:
                player.closeInventory();
                MagicAssistant.sendToServer(player, "Hub");
                return;
            case NETHER_STAR:
                InventoryUtil.openInventory(player, InventoryType.PARK);
                return;
            case COMPASS:
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "This feature is coming soon!");
                return;
            case PAPER:
                InventoryUtil.openInventory(player, InventoryType.CUSTOMIZE);
                return;
            case GLOWSTONE_DUST:
                player.closeInventory();
                MagicAssistant.sendToServer(player, "Arcade");
                return;
            case GRASS:
                player.closeInventory();
                MagicAssistant.sendToServer(player, "Creative");
                return;
            case RED_ROSE:
                player.closeInventory();
                MagicAssistant.sendToServer(player, "Seasonal");
        }
    }
}
