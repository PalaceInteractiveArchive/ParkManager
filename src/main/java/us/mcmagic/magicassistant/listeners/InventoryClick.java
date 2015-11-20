package us.mcmagic.magicassistant.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.designstation.DesignStationClick;
import us.mcmagic.magicassistant.magicband.*;
import us.mcmagic.magicassistant.watch.WatchTask;
import us.mcmagic.mcmagiccore.actionbar.ActionBarManager;

public class InventoryClick implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) {
            return;
        }
        int slot = event.getSlot();
        if (event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
            if (!BlockEdit.isInBuildMode(((PlayerInventory) event.getClickedInventory()).getHolder().getUniqueId())) {
                if (event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
                    event.setCancelled(true);
                    return;
                }
                if (slot > 3) {
                    event.setResult(Event.Result.DENY);
                    event.setCancelled(true);
                    return;
                }
            }
        } else {
            if (!BlockEdit.isInBuildMode(player.getUniqueId())) {
                if (event.getAction().equals(InventoryAction.HOTBAR_SWAP)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        String name = ChatColor.stripColor(inv.getName());
        if (name.equals(player.getName() + "'s MagicBand")) {
            MainMenuClick.handle(event);
            event.setCancelled(true);
            return;
        }
        if (name.startsWith("Wardrobe Manager Page")) {
            event.setCancelled(true);
            MagicAssistant.wardrobeManager.handle(event);
            return;
        }
        if (name.startsWith("Resource Pack Menu")) {
            event.setCancelled(true);
            MagicAssistant.packManager.handleClick(event);
            return;
        }
        if (name.startsWith("Ride List")) {
            event.setCancelled(true);
            RideListClick.handle(event);
            return;
        }
        if (name.startsWith("Attraction List")) {
            event.setCancelled(true);
            AttractionListClick.handle(event);
            return;
        }
        if (name.startsWith("Rooms in")) {
            event.setCancelled(true);
            HotelRoomMenuClick.handle(event);
            return;
        }
        if (name.startsWith("Shop - ")) {
            event.setCancelled(true);
            String shop = inv.getName().replaceFirst(ChatColor.GREEN + "Shop - ", "");
            if (shop.equals(ChatColor.RED + "Confirm")) {
                ShopConfirmClick.handle(event);
                return;
            }
            MagicAssistant.shopManager.handleClick(event, shop);
            return;
        }
        switch (name) {
            case "Ride Counter":
                event.setCancelled(true);
                RideCounterClick.handle(event);
                return;
            case "Player Settings":
                event.setCancelled(true);
                PlayerSettingsClick.handle(event);
                return;
            case "My Profile":
                event.setCancelled(true);
                MyProfileMenuClick.handle(event);
                return;
            case "Shows and Events":
                event.setCancelled(true);
                ShowEventClick.handle(event);
                return;
            case "Customize Menu":
                event.setCancelled(true);
                CustomizeMenuClick.handle(event);
                return;
            case "Wardrobe Manager":
                event.setCancelled(true);
                MagicAssistant.wardrobeManager.handle(event);
                return;
            case "Customize Band Color":
                event.setCancelled(true);
                CustomBandClick.handle(event);
                return;
            case "Customize Name Color":
                event.setCancelled(true);
                CustomNameClick.handle(event);
                return;
            case "Park Menu":
                event.setCancelled(true);
                ParkMenuClick.handle(event);
                return;
            case "Rides and Attractions":
                event.setCancelled(true);
                RideAttractionClick.handle(event);
                return;
            case "Food Menu":
                event.setCancelled(true);
                FoodMenuClick.handle(event);
                return;
            case "Special Edition MagicBands":
                event.setCancelled(true);
                SpecialEditionClick.handle(event);
                return;
            case "Hotels and Resorts":
                event.setCancelled(true);
                HotelAndResortMenuClick.handle(event);
                return;
            case "Hotels":
                event.setCancelled(true);
                HotelMenuClick.handle(event);
                return;
            case "Storage Upgrade":
                event.setCancelled(true);
                StorageUpgradeClick.handle(event);
                return;
            case "My Hotel Rooms":
                event.setCancelled(true);
                MyHotelRoomsMenuClick.handle(event);
                return;
            case "Book Room?":
                event.setCancelled(true);
                HotelRoomMenuClick.handle(event);
                return;
            case "Check Out?":
                event.setCancelled(true);
                HotelCheckoutMenuClick.handle(event);
                return;
            case "Visit Hotels and Resorts?":
                event.setCancelled(true);
                VisitHotelMenuClick.handle(event);
                return;
            case "Pick Model":
                event.setCancelled(true);
                DesignStationClick.handleModel(event);
                return;
            case "Pick Size/Color":
                event.setCancelled(true);
                DesignStationClick.handleSizeAndColor(event);
                return;
            case "Pick Engine":
                event.setCancelled(true);
                DesignStationClick.handleEngine(event);
                return;
            case "Shop":
                event.setCancelled(true);
                ShopMainMenuClick.handle(event);
                return;
            case "Wait Times":
                event.setCancelled(true);
                WaitTimeClick.handle(event);
                return;
            case "Purchase FastPass":
                event.setCancelled(true);
                FastPassMenuClick.handle(event);
                return;
            case "Show Timetable":
                event.setCancelled(true);
                ShowTimeClick.handle(event);
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (BlockEdit.isInBuildMode(player.getUniqueId())) {
            return;
        }
        if (event.getNewSlot() == 6) {
            ActionBarManager.sendMessage(player, ChatColor.YELLOW + "" + ChatColor.BOLD + "Current time in EST: " +
                    ChatColor.GREEN + MagicAssistant.bandUtil.currentTime());
            WatchTask.addToMessage(player.getUniqueId());
        } else if (event.getPreviousSlot() == 6) {
            WatchTask.removeFromMessage(player.getUniqueId());
        }
    }
}