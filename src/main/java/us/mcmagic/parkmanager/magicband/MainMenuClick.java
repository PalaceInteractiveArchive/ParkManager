package us.mcmagic.parkmanager.magicband;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.InventoryType;
import us.mcmagic.parkmanager.handlers.PlayerData;
import us.mcmagic.parkmanager.utils.BandUtil;

import java.util.ArrayList;

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
                    Inventory hotel = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Visit Hotels and Resorts?");
                    hotel.setItem(11, new ItemCreator(Material.WOOL, 1, (byte) 5, ChatColor.GREEN + "Yes", new ArrayList<>()));
                    hotel.setItem(15, new ItemCreator(Material.WOOL, 1, (byte) 14, ChatColor.GREEN + "No", new ArrayList<>()));
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
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 2);
                return;
            case IRON_CHESTPLATE:
                ParkManager.inventoryUtil.openWardrobeManagerPage(player, 1);
                return;
            case WATCH:
                ParkManager.inventoryUtil.openInventory(player, InventoryType.PLAYERTIME);
                return;
            case GOLD_BOOTS:
                ParkManager.shopManager.openMenu(player);
                return;
            case POTATO_ITEM:
                ParkManager.inventoryUtil.openFoodMenuPage(player, 1);
                return;
            case NETHER_STAR:
                ParkManager.inventoryUtil.openInventory(player, InventoryType.PARK);
        }
    }
}