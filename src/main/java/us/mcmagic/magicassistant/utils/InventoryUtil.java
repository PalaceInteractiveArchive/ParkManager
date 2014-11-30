package us.mcmagic.magicassistant.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.FoodLocation;
import us.mcmagic.magicassistant.MagicAssistant;

import java.util.Arrays;
import java.util.List;

public class InventoryUtil {

    @SuppressWarnings("deprecation")
    public static void openInventory(Player player, InventoryType inv) {
        Inventory mb = Bukkit.createInventory(player, 9, ChatColor.BLUE
                + "MagicBand Menu");
        ItemStack back = new ItemStack(Material.PAPER, 1);
        ItemMeta backm = back.getItemMeta();
        backm.setDisplayName(ChatColor.GREEN + "Back");
        back.setItemMeta(backm);
        if (inv == InventoryType.MAGICBAND) {
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
            ItemStack locker = new ItemStack(Material.ENDER_CHEST);
            ItemMeta lockerm = locker.getItemMeta();
            lockerm.setDisplayName(ChatColor.GREEN + "Locker");
            locker.setItemMeta(lockerm);
            ItemStack tp = new ItemStack(Material.NETHER_STAR);
            ItemMeta tpm = tp.getItemMeta();
            tpm.setDisplayName(ChatColor.GREEN + "Toggle Players");
            tp.setItemMeta(tpm);
            ItemStack bal = new ItemStack(Material.GOLD_NUGGET);
            ItemMeta balm = bal.getItemMeta();
            balm.setDisplayName(ChatColor.GREEN + "Balance");
            bal.setItemMeta(balm);
            mb.setItem(0, server);
            mb.setItem(1, park);
            mb.setItem(3, autograph);
            mb.setItem(4, food);
            mb.setItem(5, hotel);
            mb.setItem(6, locker);
            mb.setItem(7, tp);
            mb.setItem(8, bal);
            player.openInventory(mb);
            return;
        }
        if (inv == InventoryType.SERVER) {
            ItemStack hub = new ItemStack(Material.DIAMOND_BLOCK);
            ItemStack creative = new ItemStack(Material.DIAMOND_BLOCK);
            ItemStack arcade = new ItemStack(Material.DIAMOND_BLOCK);
            ItemStack build;
            if (player.hasPermission("chat.alwaystalk")) {
                build = new ItemStack(Material.DIAMOND_BLOCK);
            } else {
                build = new ItemStack(Material.STAINED_CLAY, (byte) 14);
            }
            ItemMeta hubm = hub.getItemMeta();
            ItemMeta creativem = creative.getItemMeta();
            ItemMeta arcadem = arcade.getItemMeta();
            ItemMeta buildm = build.getItemMeta();
            hubm.setDisplayName(ChatColor.GREEN + "Join Hub");
            arcadem.setDisplayName(ChatColor.GREEN + "Join Arcade");
            creativem.setDisplayName(ChatColor.GREEN + "Join Creative");
            buildm.setDisplayName(ChatColor.GREEN + "Join Build");
            hub.setItemMeta(hubm);
            creative.setItemMeta(creativem);
            arcade.setItemMeta(arcadem);
            build.setItemMeta(buildm);
            mb.setItem(0, back);
            mb.setItem(5, hub);
            mb.setItem(6, creative);
            mb.setItem(7, arcade);
            mb.setItem(8, build);
            player.openInventory(mb);
            return;
        }
        if (inv == InventoryType.PARK) {
            ItemStack mk = new ItemStack(Material.DIAMOND_HOE);
            ItemStack epcot = new ItemStack(Material.SNOW_BALL);
            ItemStack hws = new ItemStack(Material.JUKEBOX);
            ItemStack ak = new ItemStack(Material.DIAMOND_BARDING);
            ItemStack typhoon = new ItemStack(Material.WATER_BUCKET);
            ItemStack dcl = new ItemStack(Material.BOAT);
            ItemMeta mkm = mk.getItemMeta();
            ItemMeta em = epcot.getItemMeta();
            ItemMeta hwsm = hws.getItemMeta();
            ItemMeta akm = ak.getItemMeta();
            ItemMeta tm = typhoon.getItemMeta();
            ItemMeta dclm = dcl.getItemMeta();
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
            dclm.setDisplayName(ChatColor.AQUA + "Disney Cruise Line");
            dclm.setLore(Arrays.asList(ChatColor.GREEN + "/join DCL"));
            mk.setItemMeta(mkm);
            epcot.setItemMeta(em);
            hws.setItemMeta(hwsm);
            ak.setItemMeta(akm);
            typhoon.setItemMeta(tm);
            dcl.setItemMeta(dclm);
            mb.setItem(0, back);
            mb.setItem(3, mk);
            mb.setItem(4, epcot);
            mb.setItem(5, hws);
            mb.setItem(6, ak);
            mb.setItem(7, typhoon);
            mb.setItem(8, dcl);
            player.openInventory(mb);
            return;
        }
        if (inv == InventoryType.FOOD) {
            player.closeInventory();
            List<FoodLocation> foodLocations = MagicAssistant.foodLocations;
            int place = 8;
            for (FoodLocation loc : foodLocations) {
                if (place < 2) {
                    break;
                }
                ItemStack food = new ItemStack(loc.getType(), 1, loc.getData());
                ItemMeta fm = food.getItemMeta();
                fm.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                        loc.getName()));
                fm.setLore(Arrays.asList(ChatColor.GREEN + "/warp "
                        + loc.getWarp()));
                food.setItemMeta(fm);
                mb.setItem(place, food);
                place--;
            }
            mb.setItem(0, back);
            player.openInventory(mb);
        }
    }
}