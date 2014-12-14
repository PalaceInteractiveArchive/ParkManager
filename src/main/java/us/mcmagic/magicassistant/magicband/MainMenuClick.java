package us.mcmagic.magicassistant.magicband;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.utils.InventoryType;
import us.mcmagic.magicassistant.utils.InventoryUtil;
import us.mcmagic.magicassistant.utils.VisibleUtil;

/**
 * Created by Marc on 12/13/14
 */
public class MainMenuClick {

    @SuppressWarnings("deprecation")
    public static void handle(ItemStack item, Player player) {
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
                    VisibleUtil.addToHideAll(player);
                } else {
                    VisibleUtil.removeFromHideAll(player);
                }
                return;
            case WATCH:
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
