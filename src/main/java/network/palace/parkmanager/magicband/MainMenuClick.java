package network.palace.parkmanager.magicband;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.InventoryType;
import network.palace.parkmanager.handlers.PlayerData;
import network.palace.parkmanager.handlers.Resort;
import network.palace.parkmanager.utils.BandUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Created by Marc on 12/13/14
 */
public class MainMenuClick {

    @SuppressWarnings("deprecation")
    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        CPlayer player = Core.getPlayerManager().getPlayer((Player) event.getWhoClicked());
        Inventory inv = event.getInventory();
        if (item.getType().equals(Material.SKULL_ITEM)) {
            ParkManager.inventoryUtil.openInventory(player, InventoryType.MYPROFILE);
            return;
        }
        PlayerData pdata = ParkManager.getPlayerData(player.getUniqueId());
        switch (item.getType()) {
            case MINECART:
                ParkManager.inventoryUtil.openInventory(player, InventoryType.RIDESANDATTRACTIONS);
                return;
            case FIREWORK:
                ParkManager.inventoryUtil.openInventory(player, InventoryType.SHOWSANDEVENTS);
                return;
            case BED:
                if (ParkManager.hotelServer) {
                    ParkManager.inventoryUtil.openInventory(player, InventoryType.HOTELSANDRESORTS);
                } else {
                    Inventory hotel = Bukkit.createInventory(player.getBukkitPlayer(), 27, ChatColor.BLUE + "Visit Hotels and Resorts?");
                    hotel.setItem(11, ItemUtil.create(Material.WOOL, 1, (byte) 5, ChatColor.GREEN + "Yes", new ArrayList<>()));
                    hotel.setItem(15, ItemUtil.create(Material.WOOL, 1, (byte) 14, ChatColor.GREEN + "No", new ArrayList<>()));
                    hotel.setItem(22, BandUtil.getBackItem());
                    player.openInventory(hotel);
                }
                return;
            case WOOL:
                byte data = item.getData().getData();
                if (data == 5) {
                    player.sendMessage(ChatColor.GREEN + "You can no longer see players!");
                    player.closeInventory();
                    ParkManager.visibilityUtil.addToHideAll(player);
                    pdata.setVisibility(false);
                    ParkManager.bandUtil.setSetting(player.getUniqueId(), "visibility", pdata.getVisibility());
                } else {
                    player.sendMessage(ChatColor.GREEN + "You can now see players!");
                    player.closeInventory();
                    ParkManager.visibilityUtil.removeFromHideAll(player);
                    pdata.setVisibility(true);
                    ParkManager.bandUtil.setSetting(player.getUniqueId(), "visibility", pdata.getVisibility());
                }
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                return;
            case IRON_CHESTPLATE:
                ParkManager.inventoryUtil.openWardrobeManagerPage(player.getBukkitPlayer(), 1);
                return;
            case WATCH:
                ParkManager.inventoryUtil.openInventory(player, InventoryType.PLAYERTIME);
                return;
            case GOLD_BOOTS:
                ParkManager.shopManager.openMenu(player);
                return;
            case POTATO_ITEM:
                ParkManager.inventoryUtil.openFoodMenuPage(player.getBukkitPlayer(), 1);
                return;
            case NETHER_STAR:
                if (ParkManager.isResort(Resort.WDW)) {
                    ParkManager.inventoryUtil.openInventory(player, InventoryType.PARK_WDW);
                } else {
                    ParkManager.inventoryUtil.openInventory(player, InventoryType.PARK_ALL);
                }
        }
    }
}