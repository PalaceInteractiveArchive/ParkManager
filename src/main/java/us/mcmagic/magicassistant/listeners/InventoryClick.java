package us.mcmagic.magicassistant.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.designstation.DesignStationClick;
import us.mcmagic.magicassistant.magicband.*;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

public class InventoryClick implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) {
            return;
        }
        String name = ChatColor.stripColor(inv.getName());
        if (name.equals(player.getName() + "'s MagicBand")) {
            MainMenuClick.handle(event);
            event.setCancelled(true);
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
        /*
        if (name.startsWith("Trade with ")) {
            event.setCancelled(true);
            MagicAssistant.tradeManager.handle(event);
            return;
        }
        */
        switch (name) {
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
            case "Hotels and Resorts":
                event.setCancelled(true);
                HotelAndResortMenuClick.handle(event);
                return;
            case "Hotels":
                event.setCancelled(true);
                HotelMenuClick.handle(event);
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
                ShopMainMenuClick.handle(event);
                event.setCancelled(true);
                return;
            case "Wait Times":
                WaitTimeClick.handle(event);
                event.setCancelled(true);
                return;
            case "Purchase FastPass":
                FastPassMenuClick.handle(event);
                event.setCancelled(true);
                return;
            case "Show Timetable":
                ShowTimeClick.handle(event);
                event.setCancelled(true);
                return;
        }
        if (clicked.getItemMeta() != null && clicked.getItemMeta().getDisplayName() != null) {
            if (clicked.getItemMeta().getDisplayName().toLowerCase().endsWith("mickey ears")) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
                return;
            }
            User user = MCMagicCore.getUser(player.getUniqueId());
            if (user.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
                if (event.getSlot() == 7 || event.getSlot() == 8) {
                    event.setCancelled(true);
                    event.setResult(Event.Result.DENY);
                    return;
                }
                if (clicked.getItemMeta().getDisplayName().startsWith("&")) {
                    if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', clicked.getItemMeta().getDisplayName())).startsWith("MagicBand")) {
                        event.setCancelled(true);
                        event.setResult(Event.Result.DENY);
                    }
                } else {
                    if (ChatColor.stripColor(clicked.getItemMeta().getDisplayName()).startsWith("MagicBand")) {
                        event.setCancelled(true);
                        event.setResult(Event.Result.DENY);
                    }
                }
            }
        }
    }
}