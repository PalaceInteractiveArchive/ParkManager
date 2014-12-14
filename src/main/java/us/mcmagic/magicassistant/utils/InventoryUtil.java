package us.mcmagic.magicassistant.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import us.mcmagic.magicassistant.FoodLocation;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.PlayerData;
import us.mcmagic.mcmagiccore.coins.Coins;
import us.mcmagic.mcmagiccore.credits.Credits;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.PlayerUtil;
import us.mcmagic.mcmagiccore.player.User;

import java.util.Arrays;
import java.util.List;

public class InventoryUtil implements Listener {
    public static MagicAssistant pl;
    public static ItemStack rna = new ItemStack(Material.MINECART);
    public static ItemStack sne = new ItemStack(Material.FIREWORK);
    public static ItemStack hnr = new ItemStack(Material.BED);
    public static ItemStack toggleon = new ItemStack(Material.WOOL, 1, (byte) 14);
    public static ItemStack toggleoff = new ItemStack(Material.WOOL, 1, (byte) 5);
    public static ItemStack shop = new ItemStack(Material.GOLD_BOOTS);
    public static ItemStack food = new ItemStack(Material.POTATO_ITEM);
    public static ItemStack hub = new ItemStack(Material.ENDER_PEARL);
    public static ItemStack map = new ItemStack(Material.NETHER_STAR);
    public static ItemStack locker = new ItemStack(Material.ENDER_CHEST);
    public static ItemStack custom = new ItemStack(Material.PAPER);
    public static ItemStack arcade = new ItemStack(Material.GLOWSTONE_DUST);
    public static ItemStack creative = new ItemStack(Material.GRASS);
    public static ItemStack seasonal = new ItemStack(Material.RED_ROSE, 1, (byte) 2);
    public static ItemStack next = new ItemStack(Material.ARROW);
    public static ItemStack last = new ItemStack(Material.ARROW);

    public InventoryUtil(MagicAssistant instance) {
        pl = instance;
    }

    public static void initialize() {
        ItemMeta rnam = rna.getItemMeta();
        ItemMeta snem = sne.getItemMeta();
        ItemMeta hnrm = hnr.getItemMeta();
        ItemMeta tom = toggleon.getItemMeta();
        ItemMeta tofm = toggleoff.getItemMeta();
        ItemMeta sm = shop.getItemMeta();
        ItemMeta fm = food.getItemMeta();
        ItemMeta hm = hub.getItemMeta();
        ItemMeta mm = map.getItemMeta();
        ItemMeta lm = locker.getItemMeta();
        ItemMeta cm = custom.getItemMeta();
        ItemMeta am = arcade.getItemMeta();
        ItemMeta crm = creative.getItemMeta();
        ItemMeta sem = seasonal.getItemMeta();
        ItemMeta nm = next.getItemMeta();
        ItemMeta lam = last.getItemMeta();
        rnam.setDisplayName(ChatColor.GREEN + "Rides and Attractions");
        snem.setDisplayName(ChatColor.GREEN + "Shows and Events");
        hnrm.setDisplayName(ChatColor.GREEN + "Hotels and Resorts");
        tom.setDisplayName(ChatColor.GREEN + "Toggle Players On");
        tofm.setDisplayName(ChatColor.RED + "Toggle Players Off");
        sm.setDisplayName(ChatColor.GREEN + "Shop");
        fm.setDisplayName(ChatColor.GREEN + "Find Food");
        hm.setDisplayName(ChatColor.GREEN + "Return to Hub");
        mm.setDisplayName(ChatColor.GREEN + "Park Map");
        lm.setDisplayName(ChatColor.GREEN + "Locker");
        cm.setDisplayName(ChatColor.GREEN + "Customize your MagicBand");
        am.setDisplayName(ChatColor.GREEN + "Arcade");
        crm.setDisplayName(ChatColor.GREEN + "Creative");
        sem.setDisplayName(ChatColor.GREEN + "Seasonal");
        nm.setDisplayName(ChatColor.GREEN + "Next Page");
        lam.setDisplayName(ChatColor.GREEN + "Last Page");
        sem.setLore(Arrays.asList(ChatColor.YELLOW + "Where Seasonal Events", ChatColor.YELLOW + "are held for the server!"));
        rna.setItemMeta(rnam);
        sne.setItemMeta(snem);
        hnr.setItemMeta(hnrm);
        toggleon.setItemMeta(tom);
        toggleoff.setItemMeta(tofm);
        shop.setItemMeta(sm);
        food.setItemMeta(fm);
        hub.setItemMeta(hm);
        map.setItemMeta(mm);
        locker.setItemMeta(lm);
        custom.setItemMeta(cm);
        arcade.setItemMeta(am);
        creative.setItemMeta(crm);
        seasonal.setItemMeta(sem);
        next.setItemMeta(nm);
        last.setItemMeta(lam);
    }

    @SuppressWarnings("deprecation")
    public static void openInventory(final Player player, InventoryType inv) {
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        ItemStack back = new ItemStack(BandUtil.getBandMaterial(data.getBandColor()));
        ItemMeta backm = back.getItemMeta();
        backm.setDisplayName(ChatColor.GREEN + "Back");
        back.setItemMeta(backm);
        switch (inv) {
            case MAINMENU:
                final Inventory main = Bukkit.createInventory(player, 27, ChatColor.BLUE
                        + player.getName() + "'s MagicBand");
                final ItemStack playerInfo = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
                ItemMeta sm = playerInfo.getItemMeta();
                sm.setDisplayName(ChatColor.GREEN + "Player Info");
                sm.setLore(Arrays.asList(ChatColor.GRAY + "Loading..."));
                playerInfo.setItemMeta(sm);
                ItemStack time = new ItemStack(Material.WATCH);
                ItemMeta tm = time.getItemMeta();
                tm.setDisplayName(ChatColor.GREEN + "Current Time in EST");
                tm.setLore(Arrays.asList(ChatColor.YELLOW + BandUtil.currentTime()));
                time.setItemMeta(tm);
                main.setItem(0, rna);
                main.setItem(9, sne);
                main.setItem(18, hnr);
                if (VisibleUtil.hideall.contains(player)) {
                    main.setItem(2, toggleon);
                } else {
                    main.setItem(2, toggleoff);
                }
                main.setItem(11, shop);
                main.setItem(20, food);
                main.setItem(4, time);
                main.setItem(13, hub);
                main.setItem(22, map);
                main.setItem(6, locker);
                main.setItem(15, playerInfo);
                main.setItem(24, custom);
                main.setItem(8, arcade);
                main.setItem(17, creative);
                main.setItem(26, seasonal);
                player.openInventory(main);
                Bukkit.getScheduler().runTaskLaterAsynchronously(pl, new Runnable() {
                    @Override
                    public void run() {
                        User user = PlayerUtil.getUser(player.getUniqueId());
                        Rank rank = user.getRank();
                        ItemStack pinfo2 = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
                        SkullMeta pm = (SkullMeta) playerInfo.getItemMeta();
                        pm.setDisplayName(ChatColor.GREEN + "Player Info");
                        pm.setOwner(player.getName());
                        List<String> lore = Arrays.asList(ChatColor.GREEN + "Name: " + ChatColor.YELLOW + player.getName(),
                                ChatColor.GREEN + "Rank: " + rank.getNameWithBrackets(),
                                ChatColor.GREEN + "Coins: " + ChatColor.YELLOW + Coins.getSqlCoins(player),
                                ChatColor.GREEN + "Credits: " + ChatColor.YELLOW + Credits.getSqlCredits(player),
                                ChatColor.GREEN + "Online Time: " + ChatColor.YELLOW + DateUtil.formatDateDiff(BandUtil.getOnlineTime(player.getUniqueId() + "")));
                        pm.setLore(lore);
                        pinfo2.setItemMeta(pm);
                        main.setItem(15, pinfo2);
                    }
                }, 20L);
                return;
            case PARK:
                return;
            case FOOD:
                Inventory foodMenu = Bukkit.createInventory(player, 27, ChatColor.BLUE
                        + "Food Menu");
                player.closeInventory();
                List<FoodLocation> foodLocations = MagicAssistant.foodLocations;
                if (((double) foodLocations.size() / 2) != 0) {
                    int place = 13;
                    for (FoodLocation loc : foodLocations) {
                        if (place > 16) {
                            break;
                        }
                        ItemStack food = new ItemStack(loc.getType(), 1, loc.getData());
                        ItemMeta fm = food.getItemMeta();
                        fm.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                loc.getName()));
                        fm.setLore(Arrays.asList(ChatColor.GREEN + "/warp "
                                + loc.getWarp()));
                        food.setItemMeta(fm);
                        foodMenu.setItem(place, food);
                        place++;
                    }
                    foodMenu.setItem(22, back);
                    if (foodLocations.size() > 7) {
                        foodMenu.setItem(23, next);
                    }
                    player.openInventory(foodMenu);
                } else {
                }
                return;
            case PROFILE:
                return;
            case SHOWSANDEVENTS:
                return;
            case CUSTOMIZE:
                return;
            case CUSTOMNAME:
                return;
            case CUSTOMCOLOR:
                return;
            case RIDESANDATTRACTIONS:
                return;
            case RIDES:
                return;
            case ATTRACTIONS:
                return;
            case HOTELSANDRESORTS:
                return;
            case PARKMAP:
        }
    }
}