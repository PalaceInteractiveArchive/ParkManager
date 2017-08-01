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
        ParkManager parkManager = ParkManager.getInstance();
        CPlayer player = Core.getPlayerManager().getPlayer((Player) event.getWhoClicked());
        Inventory inv = event.getInventory();
        if (item.getType().equals(Material.SKULL_ITEM)) {
            parkManager.getInventoryUtil().openInventory(player, InventoryType.MYPROFILE);
            return;
        }
        PlayerData pdata = parkManager.getPlayerData(player.getUniqueId());
        switch (item.getType()) {
            case MINECART:
                parkManager.getInventoryUtil().openInventory(player, InventoryType.RIDESANDATTRACTIONS);
                return;
            case FIREWORK:
                parkManager.getInventoryUtil().openInventory(player, InventoryType.SHOWSANDEVENTS);
                return;
            case BED:
                if (parkManager.isHotelServer()) {
                    parkManager.getInventoryUtil().openInventory(player, InventoryType.HOTELSANDRESORTS);
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
                    parkManager.getVisibilityUtil().addToHideAll(player);
                    pdata.setVisibility(false);
                    parkManager.getBandUtil().setSetting(player, "visibility", pdata.isVisibility());
                } else {
                    player.sendMessage(ChatColor.GREEN + "You can now see players!");
                    player.closeInventory();
                    parkManager.getVisibilityUtil().removeFromHideAll(player);
                    pdata.setVisibility(true);
                    parkManager.getBandUtil().setSetting(player, "visibility", pdata.isVisibility());
                }
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                return;
            case IRON_CHESTPLATE:
                parkManager.getInventoryUtil().openWardrobeManagerPage(player, 1);
                return;
            case WATCH:
                parkManager.getInventoryUtil().openInventory(player, InventoryType.PLAYERTIME);
                return;
            case GOLD_BOOTS:
                parkManager.getShopManager().openMenu(player);
                return;
            case POTATO_ITEM:
                parkManager.getInventoryUtil().openFoodMenuPage(player.getBukkitPlayer(), 1);
                return;
            case NETHER_STAR:
                if (parkManager.isResort(Resort.WDW)) {
                    parkManager.getInventoryUtil().openInventory(player, InventoryType.PARK_WDW);
                } else {
                    parkManager.getInventoryUtil().openInventory(player, InventoryType.PARK_ALL);
                }
        }
    }
}