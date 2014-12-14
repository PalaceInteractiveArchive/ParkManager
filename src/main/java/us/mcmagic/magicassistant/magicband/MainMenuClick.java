package us.mcmagic.magicassistant.magicband;

import org.bukkit.ChatColor;
import org.bukkit.Material;
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
                InventoryUtil.openInventory(player, InventoryType.HOTELSANDRESORTS);
                return;
            case WOOL:
                byte data = item.getData().getData();
                if (data == 5) {
                    ItemStack off = new ItemStack(Material.WOOL, 1, (byte) 14);
                    ItemMeta om = off.getItemMeta();
                    om.setDisplayName(ChatColor.GREEN + "Toggle Players On");
                    off.setItemMeta(om);
                    inv.setItem(2, off);
                    VisibleUtil.addToHideAll(player);
                } else {
                    ItemStack on = new ItemStack(Material.WOOL, 1, (byte) 5);
                    ItemMeta om = on.getItemMeta();
                    om.setDisplayName(ChatColor.RED + "Toggle Players Off");
                    on.setItemMeta(om);
                    inv.setItem(2, on);
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
                InventoryUtil.openInventory(player, InventoryType.PARKMAP);
                return;
            case ENDER_CHEST:
                player.openInventory(player.getEnderChest());
                return;
            case PAPER:
                InventoryUtil.openInventory(player, InventoryType.CUSTOMIZE);
                return;
            case GLOWSTONE_DUST:
                player.closeInventory();
                MagicAssistant.sendToServer(player, "Arcade");
                return;
            case GRASS:
                MagicAssistant.sendToServer(player, "Creative");
                return;
            case RED_ROSE:
                MagicAssistant.sendToServer(player, "Seasonal");
        }
    }
}
