package us.mcmagic.magicassistant.magicband;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.InventoryType;
import us.mcmagic.magicassistant.handlers.PlayerData;
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.magicassistant.utils.VisibleUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.permissions.Rank;

import java.util.ArrayList;
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
            MagicAssistant.inventoryUtil.openInventory(player, InventoryType.MYPROFILE);
            return;
        }
        PlayerData pdata = MagicAssistant.getPlayerData(player.getUniqueId());
        switch (item.getType()) {
            case MINECART:
                MagicAssistant.inventoryUtil.openInventory(player, InventoryType.RIDESANDATTRACTIONS);
                return;
            case FIREWORK:
                MagicAssistant.inventoryUtil.openInventory(player, InventoryType.SHOWSANDEVENTS);
                return;
            case BED:
                if (MCMagicCore.getMCMagicConfig().serverName.equals("Resorts")) {
                    MagicAssistant.inventoryUtil.openInventory(player, InventoryType.HOTELSANDRESORTS);
                } else {
                    Inventory hotel = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Visit Hotels and Resorts?");
                    hotel.setItem(11, new ItemCreator(Material.WOOL, 1, (byte) 5, ChatColor.GREEN + "Yes", new ArrayList<String>()));
                    hotel.setItem(15, new ItemCreator(Material.WOOL, 1, (byte) 14, ChatColor.GREEN + "No", new ArrayList<String>()));
                    hotel.setItem(22, BandUtil.getBackItem());
                    player.openInventory(hotel);
                }
                return;
            case WOOL:
                byte data = item.getData().getData();
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 2);
                if (data == 5) {
                    player.sendMessage(ChatColor.GREEN + "You can no longer see players!");
                    player.closeInventory();
                    VisibleUtil.addToHideAll(player);
                    pdata.setVisibility(false);
                    MagicAssistant.bandUtil.setSetting(player.getUniqueId(), "visibility", pdata.getVisibility());
                } else {
                    player.sendMessage(ChatColor.GREEN + "You can now see players!");
                    player.closeInventory();
                    VisibleUtil.removeFromHideAll(player);
                    pdata.setVisibility(true);
                    MagicAssistant.bandUtil.setSetting(player.getUniqueId(), "visibility", pdata.getVisibility());
                }
                return;
            case WATCH:
                ItemStack time = new ItemStack(Material.WATCH);
                ItemMeta tm = time.getItemMeta();
                tm.setDisplayName(ChatColor.GREEN + "Current Time in EST");
                tm.setLore(Arrays.asList(ChatColor.YELLOW + MagicAssistant.bandUtil.currentTime()));
                time.setItemMeta(tm);
                inv.setItem(4, time);
                return;
            case GOLD_BOOTS:
                MagicAssistant.shopManager.openMenu(player);
                return;
            case POTATO_ITEM:
                MagicAssistant.inventoryUtil.openInventory(player, InventoryType.FOOD);
                return;
            case ENDER_PEARL:
                player.closeInventory();
                MagicAssistant.getInstance().sendToServer(player, "Hub");
                return;
            case NETHER_STAR:
                MagicAssistant.inventoryUtil.openInventory(player, InventoryType.PARK);
                return;
            case NOTE_BLOCK:
                MagicAssistant.packManager.openMenu(player);
                return;
            case FIREWORK_CHARGE:
                MagicAssistant.inventoryUtil.openInventory(player, InventoryType.CUSTOMIZE);
                return;
            case GLOWSTONE_DUST:
                player.closeInventory();
                MagicAssistant.getInstance().sendToServer(player, "Arcade");
                return;
            case GRASS:
                player.closeInventory();
                MagicAssistant.getInstance().sendToServer(player, "Creative");
                return;
            case CLAY_BRICK:
                if (MCMagicCore.getUser(player.getUniqueId()).getRank().getRankId() >= Rank.CASTMEMBER.getRankId()) {
                    MagicAssistant.inventoryUtil.openInventory(player, InventoryType.FASTPASS);
                }
        }
    }
}
