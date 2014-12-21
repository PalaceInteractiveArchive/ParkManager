package us.mcmagic.magicassistant.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
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

import java.util.*;

public class InventoryUtil implements Listener {
    public static MagicAssistant pl;
    //Main Menu Items
    public static ItemStack rna = new ItemStack(Material.MINECART);
    public static ItemStack sne = new ItemStack(Material.FIREWORK);
    public static ItemStack hnr = new ItemStack(Material.BED);
    public static ItemStack toggleon = new ItemStack(Material.WOOL, 1, (byte) 14);
    public static ItemStack toggleoff = new ItemStack(Material.WOOL, 1, (byte) 5);
    public static ItemStack shop = new ItemStack(Material.GOLD_BOOTS);
    public static ItemStack food = new ItemStack(Material.POTATO_ITEM);
    public static ItemStack hub = new ItemStack(Material.ENDER_PEARL);
    public static ItemStack parks = new ItemStack(Material.NETHER_STAR);
    public static ItemStack report = new ItemStack(Material.COMPASS);
    public static ItemStack custom = new ItemStack(Material.PAPER);
    public static ItemStack arcade = new ItemStack(Material.GLOWSTONE_DUST);
    public static ItemStack creative = new ItemStack(Material.GRASS);
    public static ItemStack seasonal = new ItemStack(Material.RED_ROSE, 1, (byte) 2);
    public static ItemStack next = new ItemStack(Material.ARROW);
    public static ItemStack last = new ItemStack(Material.ARROW);
    //Park Menu Items
    public static ItemStack mk = new ItemStack(Material.DIAMOND_HOE);
    public static ItemStack epcot = new ItemStack(Material.SNOW_BALL);
    public static ItemStack hws = new ItemStack(Material.JUKEBOX);
    public static ItemStack ak = new ItemStack(Material.SAPLING);
    public static ItemStack tl = new ItemStack(Material.WATER_BUCKET);
    public static ItemStack dcl = new ItemStack(Material.BOAT);
    //Player Info
    public static ItemStack dvc = new ItemStack(Material.DIAMOND);
    public static ItemStack web = new ItemStack(Material.REDSTONE);
    public static ItemStack flist = new ItemStack(Material.BOOK);
    public static ItemStack locker = new ItemStack(Material.ENDER_CHEST);
    public static ItemStack ach = new ItemStack(Material.EMERALD);
    public static ItemStack mumble = new ItemStack(Material.COMPASS);
    public static ItemStack packs = new ItemStack(Material.NOTE_BLOCK);
    //Friend Menu
    public static ItemStack nextPage = new ItemStack(Material.ARROW);
    public static ItemStack lastPage = new ItemStack(Material.ARROW);

    public InventoryUtil(MagicAssistant instance) {
        pl = instance;
    }

    public static void initialize() {
        //Main Menu Items
        ItemMeta rnam = rna.getItemMeta();
        ItemMeta snem = sne.getItemMeta();
        ItemMeta hnrm = hnr.getItemMeta();
        ItemMeta tom = toggleon.getItemMeta();
        ItemMeta tofm = toggleoff.getItemMeta();
        ItemMeta sm = shop.getItemMeta();
        ItemMeta fm = food.getItemMeta();
        ItemMeta hm = hub.getItemMeta();
        ItemMeta pm = parks.getItemMeta();
        ItemMeta rm = report.getItemMeta();
        ItemMeta cm = custom.getItemMeta();
        ItemMeta am = arcade.getItemMeta();
        ItemMeta crm = creative.getItemMeta();
        ItemMeta sem = seasonal.getItemMeta();
        ItemMeta nm = next.getItemMeta();
        ItemMeta lam = last.getItemMeta();
        rnam.setDisplayName(ChatColor.GREEN + "Rides and Attractions");
        snem.setDisplayName(ChatColor.GREEN + "Shows and Events");
        hnrm.setDisplayName(ChatColor.GREEN + "Hotels and Resorts");
        tom.setDisplayName(ChatColor.AQUA + "Player Visibility " + ChatColor.GOLD + "➠ " + ChatColor.RED + "Hidden");
        tofm.setDisplayName(ChatColor.AQUA + "Player Visibility " + ChatColor.GOLD + "➠ " + ChatColor.GREEN + "Visible");
        sm.setDisplayName(ChatColor.GREEN + "Shop");
        fm.setDisplayName(ChatColor.GREEN + "Find Food");
        hm.setDisplayName(ChatColor.GREEN + "Return to Hub");
        pm.setDisplayName(ChatColor.GREEN + "Park Menu");
        rm.setDisplayName(ChatColor.GREEN + "Create a Report");
        cm.setDisplayName(ChatColor.GREEN + "Customize your MagicBand");
        am.setDisplayName(ChatColor.GREEN + "Arcade");
        crm.setDisplayName(ChatColor.GREEN + "Creative");
        sem.setDisplayName(ChatColor.GREEN + "Seasonal");
        nm.setDisplayName(ChatColor.GREEN + "Next Page");
        lam.setDisplayName(ChatColor.GREEN + "Last Page");
        rnam.setLore(Arrays.asList(ChatColor.GREEN + "Ride or experience", ChatColor.GREEN + "a ride from Walt", ChatColor.GREEN + "Disney World!"));
        snem.setLore(Arrays.asList(ChatColor.GREEN + "Watch one of the", ChatColor.GREEN + "famous MCMagic Shows!"));
        hnrm.setLore(Arrays.asList(ChatColor.GREEN + "Visit and rent a", ChatColor.GREEN + "room from a Walt", ChatColor.GREEN + "Disney World Resort!"));
        sm.setLore(Arrays.asList(ChatColor.RED + "Coming Soon™"));
        fm.setLore(Arrays.asList(ChatColor.GREEN + "Visit a restaurant", ChatColor.GREEN + "to get some food!"));
        hm.setLore(Arrays.asList(ChatColor.GREEN + "Return to the", ChatColor.GREEN + "Hub Server!"));
        pm.setLore(Arrays.asList(ChatColor.GREEN + "Visit one of the", ChatColor.GREEN + "Walt Disney World Parks!"));
        rm.setLore(Arrays.asList(ChatColor.RED + "Coming Soon™"));
        cm.setLore(Arrays.asList(ChatColor.GREEN + "Make your MagicBand", ChatColor.GREEN + "perfect for you!"));
        am.setLore(Arrays.asList(ChatColor.YELLOW + "Play some unique", ChatColor.YELLOW + "MCMagic Mini-Games!"));
        crm.setLore(Arrays.asList(ChatColor.YELLOW + "Create your own", ChatColor.RED + "M" + ChatColor.GOLD + "a"
                + ChatColor.YELLOW + "g" + ChatColor.DARK_GREEN + "i" + ChatColor.BLUE + "c" + ChatColor.LIGHT_PURPLE + "!"));
        sem.setLore(Arrays.asList(ChatColor.YELLOW + "Where Seasonal Events", ChatColor.YELLOW + "are held for the server!"));
        rna.setItemMeta(rnam);
        sne.setItemMeta(snem);
        hnr.setItemMeta(hnrm);
        toggleon.setItemMeta(tom);
        toggleoff.setItemMeta(tofm);
        shop.setItemMeta(sm);
        food.setItemMeta(fm);
        hub.setItemMeta(hm);
        parks.setItemMeta(pm);
        report.setItemMeta(rm);
        custom.setItemMeta(cm);
        arcade.setItemMeta(am);
        creative.setItemMeta(crm);
        seasonal.setItemMeta(sem);
        next.setItemMeta(nm);
        last.setItemMeta(lam);
        //Park Menu Items
        ItemMeta mkm = mk.getItemMeta();
        ItemMeta em = epcot.getItemMeta();
        ItemMeta hwsm = hws.getItemMeta();
        ItemMeta akm = ak.getItemMeta();
        ItemMeta tlm = tl.getItemMeta();
        ItemMeta dclm = dcl.getItemMeta();
        mkm.setDisplayName(ChatColor.AQUA + "Magic Kingdom");
        em.setDisplayName(ChatColor.AQUA + "Epcot");
        hwsm.setDisplayName(ChatColor.AQUA + "Hollywood Studios");
        akm.setDisplayName(ChatColor.AQUA + "Animal Kingdom");
        tlm.setDisplayName(ChatColor.AQUA + "Typhoon Lagoon");
        dclm.setDisplayName(ChatColor.AQUA + "Disney Cruise Line");
        List<String> mkl = Arrays.asList(ChatColor.GREEN + "/join MK");
        List<String> el = Arrays.asList(ChatColor.GREEN + "/join Epcot");
        List<String> hwsl = Arrays.asList(ChatColor.GREEN + "/join HWS");
        List<String> akl = Arrays.asList(ChatColor.GREEN + "/join AK");
        List<String> tll = Arrays.asList(ChatColor.GREEN + "/join Typhoon");
        List<String> dcll = Arrays.asList(ChatColor.GREEN + "/join DCL");
        mkm.setLore(mkl);
        em.setLore(el);
        hwsm.setLore(hwsl);
        akm.setLore(akl);
        tlm.setLore(tll);
        dclm.setLore(dcll);
        mk.setItemMeta(mkm);
        epcot.setItemMeta(em);
        hws.setItemMeta(hwsm);
        ak.setItemMeta(akm);
        tl.setItemMeta(tlm);
        dcl.setItemMeta(dclm);
        //Player Info
        ItemMeta dvcm = dvc.getItemMeta();
        ItemMeta wm = web.getItemMeta();
        ItemMeta frm = flist.getItemMeta();
        ItemMeta lm = locker.getItemMeta();
        ItemMeta achm = ach.getItemMeta();
        ItemMeta mum = mumble.getItemMeta();
        ItemMeta pam = packs.getItemMeta();
        dvcm.setDisplayName(ChatColor.AQUA + "Become a DVC Member!");
        wm.setDisplayName(ChatColor.GREEN + "Website");
        frm.setDisplayName(ChatColor.YELLOW + "Friends List");
        lm.setDisplayName(ChatColor.GREEN + "Locker");
        achm.setDisplayName(ChatColor.GREEN + "Achievements");
        mum.setDisplayName(ChatColor.GREEN + "Mumble");
        pam.setDisplayName(ChatColor.GREEN + "Resource/Audio Packs");
        dvc.setItemMeta(dvcm);
        web.setItemMeta(wm);
        flist.setItemMeta(frm);
        locker.setItemMeta(lm);
        ach.setItemMeta(achm);
        mumble.setItemMeta(mum);
        packs.setItemMeta(pam);
        //Friend Menu
        ItemMeta lpm = lastPage.getItemMeta();
        ItemMeta npm = nextPage.getItemMeta();
        lpm.setDisplayName(ChatColor.GREEN + "Last Page");
        npm.setDisplayName(ChatColor.GREEN + "Next Page");
        lastPage.setItemMeta(lpm);
        nextPage.setItemMeta(npm);
    }

    @SuppressWarnings("deprecation")
    public static void openInventory(final Player player, InventoryType inv) {
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
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
                main.setItem(22, parks);
                main.setItem(6, report);
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
                Inventory park = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Park Menu");
                park.setItem(10, mk);
                park.setItem(11, epcot);
                park.setItem(12, hws);
                park.setItem(14, ak);
                park.setItem(15, tl);
                park.setItem(16, dcl);
                park.setItem(22, BandUtil.getBackItem());
                player.openInventory(park);
                return;
            case FOOD:
                Inventory foodMenu = Bukkit.createInventory(player, 27, ChatColor.BLUE
                        + "Food Menu");
                player.closeInventory();
                List<FoodLocation> foodLocations = MagicAssistant.foodLocations;
                // If odd amount of items
                int place = 13;
                if (foodLocations.size() % 2 == 1) {
                    int amount = 1;
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
                        if (amount % 2 == 1) {
                            place -= amount;
                        } else {
                            place += amount;
                        }
                        amount++;
                    }
                    foodMenu.setItem(22, BandUtil.getBackItem());
                    if (foodLocations.size() > 7) {
                        foodMenu.setItem(23, next);
                    }
                    player.openInventory(foodMenu);
                    // If even amount of items
                } else {
                    place++;
                    int amount = 1;
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
                        if (amount % 2 == 0) {
                            place -= amount;
                        } else {
                            place += amount;
                        }
                    }
                    foodMenu.setItem(22, BandUtil.getBackItem());
                    if (foodLocations.size() > 7) {
                        foodMenu.setItem(23, next);
                    }
                    player.openInventory(foodMenu);
                }
                return;
            case PLAYERINFO:
                Inventory pmenu = Bukkit.createInventory(player, 27, ChatColor.BLUE + "My Profile");
                Rank rank = PlayerUtil.getUser(player.getUniqueId()).getRank();
                pmenu.setItem(10, web);
                pmenu.setItem(11, flist);
                pmenu.setItem(12, locker);
                if (rank.equals(Rank.GUEST)) {
                    pmenu.setItem(13, dvc);
                }
                pmenu.setItem(14, ach);
                pmenu.setItem(15, mumble);
                pmenu.setItem(16, packs);
                player.openInventory(pmenu);
                return;
            case FRIENDLIST:
                Inventory flist = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Friend List");
                HashMap<UUID, String> fl = data.getFriendList();
                if (fl.isEmpty()) {
                    ItemStack empty = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
                    SkullMeta skullMeta = (SkullMeta) empty.getItemMeta();
                    skullMeta.setOwner("Herobrine");
                    skullMeta.setDisplayName(ChatColor.RED + "Uh oh!");
                    skullMeta.setLore(Arrays.asList(ChatColor.RED + "It seems like your", ChatColor.RED + "Friends List is", ChatColor.RED + "empty! Type /friend" + ChatColor.RED + "for more info!"));
                    empty.setItemMeta(skullMeta);
                    flist.setItem(13, empty);
                    player.openInventory(flist);
                    return;
                }
                List<String> friendNames = new ArrayList<>();
                for (Map.Entry<UUID, String> entry : fl.entrySet()) {
                    friendNames.add(entry.getValue());
                }
                List<String> test = data.getPages().get(0);
                List<ItemStack> items = new ArrayList<>();
                Collections.sort(friendNames);
                for (String name : friendNames) {
                    ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
                    for (Map.Entry<UUID, String> entry : fl.entrySet()) {
                        if (entry.getValue().equals(name)) {
                            SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
                            skullMeta.setOwner(entry.getValue());
                            skullMeta.setDisplayName(ChatColor.GREEN + entry.getValue());
                            head.setItemMeta(skullMeta);
                            break;
                        }
                    }
                    if (head.getItemMeta() == null) {
                        continue;
                    }
                    items.add(head);
                }
                int pages = (int) Math.ceil(items.size() / 7);
                if (pages > 1) {
                }
                int i = 10;
                for (ItemStack item : items) {
                    if (i > 16) {
                        break;
                    }
                    flist.setItem(i, item);
                    i++;
                }
                flist.setItem(22, BandUtil.getBackItem());
                player.openInventory(flist);
                break;
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
                featureComingSoon(player);
        }
    }

    public static void openFriendListPage(Player player, int page) {
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        HashMap<UUID, String> fl = data.getFriendList();
        Inventory flist;
        if (fl.size() > 7) {
            flist = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Friend List Page " + page);
        } else {
            flist = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Friend List");
        }
        if (fl.isEmpty()) {
            ItemStack empty = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
            SkullMeta skullMeta = (SkullMeta) empty.getItemMeta();
            skullMeta.setOwner("Herobrine");
            skullMeta.setDisplayName(ChatColor.RED + "Uh oh!");
            skullMeta.setLore(Arrays.asList(ChatColor.RED + "It seems like your", ChatColor.RED + "Friends List is", ChatColor.RED + "empty! Type /friend" + ChatColor.RED + "for more info!"));
            empty.setItemMeta(skullMeta);
            flist.setItem(13, empty);
            player.openInventory(flist);
            return;
        }
        List<String> pageList = data.getPages().get(page);
        List<ItemStack> items = new ArrayList<>();
        Collections.sort(pageList);
        for (String name : pageList) {
            ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
            for (Map.Entry<UUID, String> entry : fl.entrySet()) {
                if (entry.getValue().equals(name)) {
                    SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
                    skullMeta.setOwner(entry.getValue());
                    skullMeta.setDisplayName(ChatColor.GREEN + entry.getValue());
                    head.setItemMeta(skullMeta);
                    break;
                }
            }
            if (head.getItemMeta() == null) {
                continue;
            }
            items.add(head);
        }
        int i = 10;
        for (ItemStack item : items) {
            if (i > 16) {
                break;
            }
            flist.setItem(i, item);
            i++;
        }
        if (page > 1) {
            flist.setItem(21, lastPage);
        }
        if (data.getPages().size() > page) {
            flist.setItem(23, nextPage);
        }
        flist.setItem(22, BandUtil.getBackItem());
        player.openInventory(flist);
    }

    public static void featureComingSoon(Player player) {
        player.playSound(player.getLocation(), Sound.ITEM_BREAK, 100, 0);
        player.closeInventory();
        player.sendMessage(ChatColor.RED + "This feature is coming soon!");
    }
}