package us.mcmagic.magicassistant.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.magicband.*;

import java.util.Arrays;

public class InventoryClick implements Listener {
    static MagicAssistant pl;

    public InventoryClick(MagicAssistant instance) {
        pl = instance;
    }

    @EventHandler
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();
        ItemStack clicked = event.getCurrentItem();
        if (clicked.equals(null)) {
            return;
        }
        /*
        if (clicked.getType().equals(BandUtil.getBandMaterial(MagicAssistant.getPlayerData(player.getUniqueId()).getBandColor()))) {
            InventoryUtil.openInventory(player, InventoryType.MAINMENU);
            return;
        }
        */
        String name = ChatColor.stripColor(inv.getName());
        if (name.equals(player.getName() + "'s MagicBand")) {
            MainMenuClick.handle(event);
            event.setCancelled(true);
            return;
        }
        if (name.startsWith("Friend List")) {
            event.setCancelled(true);
            FriendListClick.handle(event);
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
        switch (name) {
            case "My Profile":
                event.setCancelled(true);
                ProfileMenuClick.handle(event);
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
        }
    }



    /*
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();
        ItemStack clicked = event.getCurrentItem();
        if (clicked.equals(null)) {
            return;
        }
        if (inv.getName().equals(ChatColor.BLUE + "MagicBand Menu")) {
            event.setCancelled(true);
            ItemStack server = new ItemStack(Material.COMPASS);
            ItemMeta serverm = server.getItemMeta();
            serverm.setDisplayName(ChatColor.GREEN + "Change Server");
            server.setItemMeta(serverm);
            ItemStack park = new ItemStack(Material.MAP);
            ItemMeta parkm = park.getItemMeta();
            parkm.setDisplayName(ChatColor.GREEN + "Change Park");
            park.setItemMeta(parkm);
            ItemStack autograph = new ItemStack(Material.BOOK);
            ItemMeta autographm = autograph.getItemMeta();
            autographm.setDisplayName(ChatColor.GREEN + "Autographs");
            autograph.setItemMeta(autographm);
            ItemStack food = new ItemStack(Material.APPLE);
            ItemMeta foodm = food.getItemMeta();
            foodm.setDisplayName(ChatColor.GREEN + "Find Food");
            food.setItemMeta(foodm);
            ItemStack hotel = new ItemStack(Material.BED);
            ItemMeta hotelm = hotel.getItemMeta();
            hotelm.setDisplayName(ChatColor.GREEN + "Hotels and Resorts");
            hotel.setItemMeta(hotelm);
            ItemStack report = new ItemStack(Material.ENDER_CHEST);
            ItemMeta lockerm = report.getItemMeta();
            lockerm.setDisplayName(ChatColor.GREEN + "Locker");
            report.setItemMeta(lockerm);
            ItemStack tp = new ItemStack(Material.NETHER_STAR);
            ItemMeta tpm = tp.getItemMeta();
            tpm.setDisplayName(ChatColor.GREEN + "Toggle Players");
            tp.setItemMeta(tpm);
            ItemStack bal = new ItemStack(Material.GOLD_NUGGET);
            ItemMeta balm = bal.getItemMeta();
            balm.setDisplayName(ChatColor.GREEN + "Balance");
            ItemStack back = new ItemStack(Material.PAPER, 1);
            ItemMeta backm = back.getItemMeta();
            backm.setDisplayName(ChatColor.GREEN + "Back");
            back.setItemMeta(backm);
            ItemStack mk = new ItemStack(Material.DIAMOND_HOE);
            ItemStack epcot = new ItemStack(Material.SNOW_BALL);
            ItemStack hws = new ItemStack(Material.JUKEBOX);
            ItemStack ak = new ItemStack(Material.DIAMOND_BARDING);
            ItemStack typhoon = new ItemStack(Material.WATER_BUCKET);
            ItemMeta mkm = mk.getItemMeta();
            ItemMeta em = epcot.getItemMeta();
            ItemMeta hwsm = hws.getItemMeta();
            ItemMeta akm = ak.getItemMeta();
            ItemMeta tm = typhoon.getItemMeta();
            mkm.setDisplayName(ChatColor.AQUA + "Magic Kingdom");
            mkm.setLore(Arrays.asList(ChatColor.GREEN + "/join MK"));
            em.setDisplayName(ChatColor.AQUA + "Epcot");
            em.setLore(Arrays.asList(ChatColor.GREEN + "/join Epcot"));
            hwsm.setDisplayName(ChatColor.AQUA + "Hollywood Studios");
            hwsm.setLore(Arrays.asList(ChatColor.GREEN + "/join HWS"));
            akm.setDisplayName(ChatColor.AQUA + "Animal Kingdom");
            akm.setLore(Arrays.asList(ChatColor.GREEN + "/join AK"));
            tm.setDisplayName(ChatColor.AQUA + "Typhoon Lagoon");
            tm.setLore(Arrays.asList(ChatColor.GREEN + "/join Typhoon"));
            mk.setItemMeta(mkm);
            epcot.setItemMeta(em);
            hws.setItemMeta(hwsm);
            ak.setItemMeta(akm);
            typhoon.setItemMeta(tm);
            ItemStack dcl = new ItemStack(Material.BOAT);
            ItemMeta dclm = dcl.getItemMeta();
            dclm.setDisplayName(ChatColor.AQUA + "Disney Cruise Line");
            dclm.setLore(Arrays.asList(ChatColor.GREEN + "/join DCL"));
            dcl.setItemMeta(dclm);
            if (clicked.equals(server)) {
                InventoryUtil.openInventory(player, InventoryType.SERVER);
                return;
            }
            if (clicked.equals(park)) {
                InventoryUtil.openInventory(player, InventoryType.PARK);
                return;
            }
            if (clicked.equals(autograph)) {
                player.closeInventory();
                player.performCommand("signing");
                return;
            }
            if (clicked.equals(food)) {
                InventoryUtil.openInventory(player, InventoryType.FOOD);
                return;
            }
            if (clicked.equals(hotel)) {
                player.closeInventory();
                MagicAssistant.sendToServer(player, "Resorts");
                return;
            }
            if (clicked.equals(report)) {
                player.openInventory(player.getEnderChest());
                return;
            }
            if (clicked.equals(tp)) {
                player.closeInventory();
                if (VisibleUtil.hideall.contains(player)) {
                    player.sendMessage(ChatColor.GREEN
                            + "You can now see players!");
                    VisibleUtil.removeFromHideAll(player);
                    return;
                }
                player.sendMessage(ChatColor.GREEN
                        + "You can no longer see players!");
                VisibleUtil.addToHideAll(player);
                return;
            }
            if (clicked.getType().equals(Material.GOLD_NUGGET)) {
                String message = ChatColor.GREEN + "You currently have " + ChatColor.YELLOW + "" + ChatColor.BOLD + Coins.getSqlCoins(player) + ChatColor.GREEN + " Coins";
                player.closeInventory();
                player.sendMessage(message);
                return;
            }
            if (clicked.equals(back)) {
                InventoryUtil.openInventory(player, InventoryType.MAINMENU);
                return;
            }
            if (clicked.getType().equals(Material.DIAMOND_BLOCK)) {
                player.closeInventory();
                ItemMeta cm = clicked.getItemMeta();
                String name = ChatColor.stripColor(cm.getDisplayName());
                String sname = name.replaceAll("Join ", "");
                try {
                    player.sendMessage(ChatColor.LIGHT_PURPLE
                            + "Now joining the " + ChatColor.BOLD + sname
                            + ChatColor.LIGHT_PURPLE + " server!");
                    MagicAssistant.sendToServer(player, sname);
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED
                            + "There was a problem joining that server! Please try again soon!");
                }
                return;
            }
            if (clicked.getType().equals(Material.WOOL)
                    && clicked.getData().getData() == (byte) 14) {
                player.sendMessage(ChatColor.RED
                        + "Sorry, this server is for Cast Members only!");
                return;
            }
            if (clicked.equals(mk)) {
                player.closeInventory();
                MagicAssistant.sendToServer(player, "MK");
                return;
            }
            if (clicked.equals(epcot)) {
                player.closeInventory();
                MagicAssistant.sendToServer(player, "Epcot");
                return;
            }
            if (clicked.equals(hws)) {
                player.closeInventory();
                MagicAssistant.sendToServer(player, "HWS");
                return;
            }
            if (clicked.equals(ak)) {
                player.closeInventory();
                MagicAssistant.sendToServer(player, "AK");
                return;
            }
            if (clicked.equals(typhoon)) {
                player.closeInventory();
                MagicAssistant.sendToServer(player, "Typhoon");
                return;
            }
            if (clicked.equals(dcl)) {
                player.closeInventory();
                MagicAssistant.sendToServer(player, "DCL");
                return;
            }
            for (FoodLocation loc : MagicAssistant.foodLocations) {
                //event.getSlot()
                if (clicked.getTypeId() == loc.getType()
                        && clicked.getData().getData() == loc.getData()) {
                    player.closeInventory();
                    player.performCommand("warp " + loc.getWarp());
                    return;
                }
                return;
            }
            return;
        }
        if (!player.hasPermission("band.stayvisible")) {
            ItemStack mb = new ItemStack(Material.PAPER);
            ItemMeta mbm = mb.getItemMeta();
            mbm.setDisplayName(ChatColor.GOLD + "MagicBand");
            List<String> lore = Arrays.asList(ChatColor.GREEN
                    + "Click me to open", ChatColor.GREEN
                    + "the MagicBand menu!");
            mbm.setLore(lore);
            mb.setItemMeta(mbm);
            if (clicked.equals(mb)) {
                event.setCancelled(true);
            }
        }
    }
    */

    @EventHandler
    public void onInventoryCreative(InventoryCreativeEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!player.hasPermission("band.stayvisible")) {
            ItemStack clicked = event.getCurrentItem();
            ItemStack mb = new ItemStack(Material.PAPER);
            ItemMeta mbm = mb.getItemMeta();
            mbm.setDisplayName(ChatColor.GOLD + "MagicBand");
            mbm.setLore(Arrays.asList(ChatColor.GREEN + "Click me to open",
                    ChatColor.GREEN + "the MagicBand menu!"));
            mb.setItemMeta(mbm);
            if (clicked.equals(mb)) {
                event.setCancelled(true);
            }
        }
    }
}