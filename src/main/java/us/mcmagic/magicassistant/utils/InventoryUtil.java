package us.mcmagic.magicassistant.utils;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import us.mcmagic.magicassistant.FoodLocation;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.PlayerData;
import us.mcmagic.magicassistant.magicband.Attraction;
import us.mcmagic.magicassistant.magicband.BandColor;
import us.mcmagic.magicassistant.magicband.Ride;
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
    public static ItemStack custom = new ItemStack(Material.FIREWORK_CHARGE);
    public static ItemStack arcade = new ItemStack(Material.GLOWSTONE_DUST);
    public static ItemStack creative = new ItemStack(Material.GRASS);
    public static ItemStack seasonal = new ItemStack(Material.RED_ROSE, 1, (byte) 2);
    //Park Menu Items
    public static ItemStack mk = new ItemStack(Material.DIAMOND_HOE);
    public static ItemStack epcot = new ItemStack(Material.SNOW_BALL);
    public static ItemStack hws = new ItemStack(Material.JUKEBOX);
    public static ItemStack ak = new ItemStack(Material.SAPLING, 1, (byte) 5);
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
    //Customize Menu
    public static ItemStack nameChange = new ItemStack(Material.JUKEBOX);
    //Customize Name
    public static ItemStack redBand = new ItemStack(Material.FIREWORK_CHARGE);
    public static ItemStack orangeBand = new ItemStack(Material.FIREWORK_CHARGE);
    public static ItemStack yellowBand = new ItemStack(Material.FIREWORK_CHARGE);
    public static ItemStack greenBand = new ItemStack(Material.FIREWORK_CHARGE);
    public static ItemStack blueBand = new ItemStack(Material.FIREWORK_CHARGE);
    public static ItemStack purpleBand = new ItemStack(Material.FIREWORK_CHARGE);
    public static ItemStack pinkBand = new ItemStack(Material.FIREWORK_CHARGE);
    public static ItemStack s1Band = new ItemStack(BandUtil.getBandMaterial(BandColor.SPECIAL1));
    public static ItemStack s2Band = new ItemStack(BandUtil.getBandMaterial(BandColor.SPECIAL2));
    public static ItemStack s3Band = new ItemStack(BandUtil.getBandMaterial(BandColor.SPECIAL3));
    public static ItemStack s4Band = new ItemStack(BandUtil.getBandMaterial(BandColor.SPECIAL4));
    public static ItemStack s5Band = new ItemStack(BandUtil.getBandMaterial(BandColor.SPECIAL5));
    //Customize Color
    public static ItemStack red = new ItemStack(Material.WOOL, 1, (byte) 14);
    public static ItemStack orange = new ItemStack(Material.WOOL, 1, (byte) 1);
    public static ItemStack yellow = new ItemStack(Material.WOOL, 1, (byte) 4);
    public static ItemStack green = new ItemStack(Material.WOOL, 1, (byte) 5);
    public static ItemStack darkGreen = new ItemStack(Material.WOOL, 1, (byte) 13);
    public static ItemStack blue = new ItemStack(Material.WOOL, 1, (byte) 11);
    public static ItemStack purple = new ItemStack(Material.WOOL, 1, (byte) 10);
    //Shows and Events
    public static ItemStack fant = new ItemStack(Material.DIAMOND_HELMET);
    public static ItemStack iroe = new ItemStack(Material.MONSTER_EGG);
    public static ItemStack wishes = new ItemStack(Material.BLAZE_ROD);
    public static ItemStack msep = new ItemStack(Material.GLOWSTONE_DUST);
    public static ItemStack fofp = new ItemStack(Material.INK_SACK, 1, (byte) 12);
    public static ItemStack party = new ItemStack(Material.WOOL, 1, (byte) 5);
    public static ItemStack noparty = new ItemStack(Material.WOOL, 1, (byte) 14);
    //Rides and Attractions
    public static ItemStack ride = new ItemStack(Material.MINECART);
    public static ItemStack attraction = new ItemStack(Material.GLOWSTONE_DUST);

    public InventoryUtil(MagicAssistant instance) {
        pl = instance;
    }

    public static void initialize() {
        //Shows and Events
        ItemMeta fantm = fant.getItemMeta();
        ItemMeta im = iroe.getItemMeta();
        ItemMeta wim = wishes.getItemMeta();
        ItemMeta msepm = msep.getItemMeta();
        ItemMeta fofm = fofp.getItemMeta();
        ItemMeta partym = party.getItemMeta();
        ItemMeta nopartym = noparty.getItemMeta();
        fantm.setDisplayName(ChatColor.BLUE + "Fantasmic!");
        im.setDisplayName(ChatColor.GREEN + "IROE");
        wim.setDisplayName(ChatColor.AQUA + "Wishes");
        msepm.setDisplayName(ChatColor.YELLOW + "Main Street Electrical Parade");
        fofm.setDisplayName(ChatColor.DARK_AQUA + "Festival of Fantasy Parade");
        partym.setDisplayName(ChatColor.GREEN + "Click to join the Party!");
        nopartym.setDisplayName(ChatColor.RED + "There is no Party right now!");
        fant.setItemMeta(fantm);
        iroe.setItemMeta(im);
        wishes.setItemMeta(wim);
        msep.setItemMeta(msepm);
        fofp.setItemMeta(fofm);
        party.setItemMeta(partym);
        noparty.setItemMeta(nopartym);
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
        rnam.setLore(Arrays.asList(ChatColor.GREEN + "Ride or experience", ChatColor.GREEN + "an attraction from", ChatColor.GREEN + "Walt Disney World!"));
        snem.setLore(Arrays.asList(ChatColor.GREEN + "Watch one of the", ChatColor.GREEN + "famous MCMagic Shows!"));
        hnrm.setLore(Arrays.asList(ChatColor.GREEN + "Visit and rent a room from", ChatColor.GREEN + "a Walt Disney World Resort!"));
        sm.setLore(Arrays.asList(ChatColor.RED + "Coming Soon™"));
        fm.setLore(Arrays.asList(ChatColor.GREEN + "Visit a restaurant", ChatColor.GREEN + "to get some food!"));
        hm.setLore(Arrays.asList(ChatColor.GREEN + "Return to the", ChatColor.GREEN + "Hub Server!"));
        pm.setLore(Arrays.asList(ChatColor.GREEN + "Visit one of the Walt", ChatColor.GREEN + "Disney World Parks!"));
        rm.setLore(Arrays.asList(ChatColor.RED + "Coming Soon™"));
        cm.setLore(Arrays.asList(ChatColor.GREEN + "Make your MagicBand", ChatColor.GREEN + "perfect for you!"));
        am.setLore(Arrays.asList(ChatColor.YELLOW + "Play some unique", ChatColor.YELLOW + "MCMagic Mini-Games!"));
        crm.setLore(Arrays.asList(ChatColor.YELLOW + "Create your", ChatColor.GREEN + "own " + ChatColor.RED + "M" + ChatColor.GOLD + "a"
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
        //Customize Menu
        ItemMeta nc = nameChange.getItemMeta();
        nc.setDisplayName(ChatColor.GREEN + "Change Name Color");
        nameChange.setItemMeta(nc);
        //Customize Name
        ItemMeta rnm = red.getItemMeta();
        ItemMeta onm = orange.getItemMeta();
        ItemMeta ynm = yellow.getItemMeta();
        ItemMeta gnm = green.getItemMeta();
        ItemMeta dgnm = darkGreen.getItemMeta();
        ItemMeta bnm = blue.getItemMeta();
        ItemMeta pnm = purple.getItemMeta();
        rnm.setDisplayName(ChatColor.RED + "Red");
        onm.setDisplayName(ChatColor.GOLD + "Orange");
        ynm.setDisplayName(ChatColor.YELLOW + "Yellow");
        gnm.setDisplayName(ChatColor.GREEN + "Green");
        dgnm.setDisplayName(ChatColor.DARK_GREEN + "Dark Green");
        bnm.setDisplayName(ChatColor.BLUE + "Blue");
        pnm.setDisplayName(ChatColor.DARK_PURPLE + "Purple");
        red.setItemMeta(rnm);
        orange.setItemMeta(onm);
        yellow.setItemMeta(ynm);
        green.setItemMeta(gnm);
        darkGreen.setItemMeta(dgnm);
        blue.setItemMeta(bnm);
        purple.setItemMeta(pnm);
        //Customize Color
        FireworkEffectMeta rbm = (FireworkEffectMeta) redBand.getItemMeta();
        FireworkEffectMeta obm = (FireworkEffectMeta) orangeBand.getItemMeta();
        FireworkEffectMeta ybm = (FireworkEffectMeta) yellowBand.getItemMeta();
        FireworkEffectMeta gbm = (FireworkEffectMeta) greenBand.getItemMeta();
        FireworkEffectMeta bbm = (FireworkEffectMeta) blueBand.getItemMeta();
        FireworkEffectMeta pbm = (FireworkEffectMeta) purpleBand.getItemMeta();
        FireworkEffectMeta pibm = (FireworkEffectMeta) pinkBand.getItemMeta();
        ItemMeta s1m = s1Band.getItemMeta();
        ItemMeta s2m = s2Band.getItemMeta();
        ItemMeta s3m = s3Band.getItemMeta();
        ItemMeta s4m = s4Band.getItemMeta();
        ItemMeta s5m = s5Band.getItemMeta();
        rbm.setEffect(FireworkEffect.builder().withColor(Color.fromRGB(255, 40, 40)).build());
        obm.setEffect(FireworkEffect.builder().withColor(Color.fromRGB(247, 140, 0)).build());
        ybm.setEffect(FireworkEffect.builder().withColor(Color.fromRGB(239, 247, 0)).build());
        gbm.setEffect(FireworkEffect.builder().withColor(Color.fromRGB(0, 192, 13)).build());
        bbm.setEffect(FireworkEffect.builder().withColor(Color.fromRGB(41, 106, 255)).build());
        pbm.setEffect(FireworkEffect.builder().withColor(Color.fromRGB(176, 0, 220)).build());
        pibm.setEffect(FireworkEffect.builder().withColor(Color.fromRGB(246, 120, 255)).build());
        List<String> lore = Arrays.asList(ChatColor.GREEN + "Cost: 500 Coins");
        s1m.setLore(lore);
        s2m.setLore(lore);
        s3m.setLore(lore);
        s4m.setLore(lore);
        s5m.setLore(lore);
        rbm.setDisplayName(ChatColor.RED + "Red");
        obm.setDisplayName(ChatColor.GOLD + "Orange");
        ybm.setDisplayName(ChatColor.YELLOW + "Yellow");
        gbm.setDisplayName(ChatColor.DARK_GREEN + "Green");
        bbm.setDisplayName(ChatColor.BLUE + "Blue (Original)");
        pbm.setDisplayName(ChatColor.DARK_PURPLE + "Purple");
        pibm.setDisplayName(ChatColor.LIGHT_PURPLE + "Pink");
        s1m.setDisplayName(ChatColor.BLUE + "Holiday Band");
        s2m.setDisplayName(ChatColor.RED + "Big Hero 6");
        s3m.setDisplayName(ChatColor.GRAY + "Haunted Mansion");
        s4m.setDisplayName(ChatColor.DARK_AQUA + "Sorcerer Mickey");
        s5m.setDisplayName(ChatColor.LIGHT_PURPLE + "Princesses");
        redBand.setItemMeta(rbm);
        orangeBand.setItemMeta(obm);
        yellowBand.setItemMeta(ybm);
        greenBand.setItemMeta(gbm);
        blueBand.setItemMeta(bbm);
        purpleBand.setItemMeta(pbm);
        pinkBand.setItemMeta(pibm);
        s1Band.setItemMeta(s1m);
        s2Band.setItemMeta(s2m);
        s3Band.setItemMeta(s3m);
        s4Band.setItemMeta(s4m);
        s5Band.setItemMeta(s5m);
        //Rides and Attractions
        ItemMeta ridem = ride.getItemMeta();
        ItemMeta attm = attraction.getItemMeta();
        ridem.setDisplayName(ChatColor.GREEN + "Rides");
        attm.setDisplayName(ChatColor.GREEN + "Attractions");
        ride.setItemMeta(ridem);
        attraction.setItemMeta(attm);
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
                pmenu.setItem(22, BandUtil.getBackItem());
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
                Inventory shows = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Shows and Events");
                if (MagicAssistant.party) {
                    shows.setItem(4, party);
                } else {
                    shows.setItem(4, noparty);
                }
                shows.setItem(9, fant);
                shows.setItem(11, iroe);
                shows.setItem(13, wishes);
                shows.setItem(15, msep);
                shows.setItem(17, fofp);
                shows.setItem(22, BandUtil.getBackItem());
                player.openInventory(shows);
                return;
            case CUSTOMIZE:
                Inventory custom = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Customize Menu");
                ItemStack band;
                if (data.getSpecial()) {
                    band = new ItemStack(BandUtil.getBandMaterial(data.getBandColor()));
                    ItemMeta bm = band.getItemMeta();
                    bm.setDisplayName(ChatColor.GREEN + "Change MagicBand Color");
                    band.setItemMeta(bm);
                } else {
                    band = new ItemStack(Material.FIREWORK_CHARGE);
                    FireworkEffectMeta bm = (FireworkEffectMeta) band.getItemMeta();
                    bm.setEffect(FireworkEffect.builder().withColor(BandUtil.getBandColor(data.getBandColor())).build());
                    bm.setDisplayName(ChatColor.GREEN + "Change MagicBand Color");
                    band.setItemMeta(bm);
                }
                custom.setItem(11, band);
                custom.setItem(15, nameChange);
                custom.setItem(22, BandUtil.getBackItem());
                player.openInventory(custom);
                return;
            case CUSTOMNAME:
                Inventory cname = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Customize Name Color");
                cname.setItem(10, red);
                cname.setItem(11, orange);
                cname.setItem(12, yellow);
                cname.setItem(13, green);
                cname.setItem(14, darkGreen);
                cname.setItem(15, blue);
                cname.setItem(16, purple);
                cname.setItem(22, BandUtil.getBackItem());
                player.openInventory(cname);
                return;
            case CUSTOMCOLOR:
                Inventory ccolor = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Customize Band Color");
                ccolor.setItem(10, redBand);
                ccolor.setItem(11, orangeBand);
                ccolor.setItem(12, yellowBand);
                ccolor.setItem(13, greenBand);
                ccolor.setItem(14, blueBand);
                ccolor.setItem(15, purpleBand);
                ccolor.setItem(16, pinkBand);
                ccolor.setItem(22, BandUtil.getBackItem());
                ccolor.setItem(23, nextPage);
                player.openInventory(ccolor);
                return;
            case SPECIALCOLOR:
                Inventory special = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Special Edition MagicBands");
                special.setItem(11, s1Band);
                special.setItem(12, s2Band);
                special.setItem(13, s3Band);
                special.setItem(14, s4Band);
                special.setItem(15, s5Band);
                special.setItem(21, lastPage);
                special.setItem(22, BandUtil.getBackItem());
                player.openInventory(special);
                return;
            case RIDESANDATTRACTIONS:
                Inventory rna = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Rides and Attractions");
                rna.setItem(20, ride);
                rna.setItem(24, attraction);
                rna.setItem(49, BandUtil.getBackItem());
                player.openInventory(rna);
                return;
            case HOTELSANDRESORTS:
                featureComingSoon(player);
        }
    }

    @SuppressWarnings("deprecation")
    public static void openAttractionListPage(Player player, int page) {
        HashMap<Integer, List<Attraction>> al = MagicAssistant.attPages;
        Inventory alist;
        if (al.size() > 1) {
            alist = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Attraction List Page " + page);
        } else {
            alist = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Attraction List");
        }
        if (al.isEmpty() || al.get(1).isEmpty()) {
            ItemStack empty = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
            ItemMeta itemMeta = empty.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RED + "Uh oh!");
            itemMeta.setLore(Arrays.asList(ChatColor.RED + "Sorry, but there", ChatColor.RED + "are no attraction setup", ChatColor.RED + "on this server!"));
            empty.setItemMeta(itemMeta);
            alist.setItem(22, empty);
            alist.setItem(49, BandUtil.getBackItem());
            player.openInventory(alist);
            return;
        }
        List<Attraction> pageList = al.get(page);
        List<ItemStack> items = new ArrayList<>();
        for (Attraction attraction : pageList) {
            ItemStack rideItem = new ItemStack(attraction.getId(), 1, attraction.getData());
            ItemMeta itemMeta = rideItem.getItemMeta();
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', attraction.getDisplayName()));
            rideItem.setItemMeta(itemMeta);
            if (rideItem.getItemMeta() == null) {
                continue;
            }
            items.add(rideItem);
        }
        int i = 10;
        for (ItemStack item : items) {
            if (i > 34) {
                break;
            }
            alist.setItem(i, item);
            if (i == 16 || i == 25) {
                i += 3;
            } else {
                i++;
            }
        }
        if (page > 1) {
            alist.setItem(48, lastPage);
        }
        if (al.size() > page) {
            alist.setItem(50, nextPage);
        }
        alist.setItem(49, BandUtil.getBackItem());
        player.openInventory(alist);
    }

    @SuppressWarnings("deprecation")
    public static void openRideListPage(Player player, int page) {
        HashMap<Integer, List<Ride>> rl = MagicAssistant.ridePages;
        Inventory rlist;
        if (rl.size() > 1) {
            rlist = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Ride List Page " + page);
        } else {
            rlist = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Ride List");
        }
        if (rl.isEmpty() || rl.get(1).isEmpty()) {
            ItemStack empty = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
            ItemMeta itemMeta = empty.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RED + "Uh oh!");
            itemMeta.setLore(Arrays.asList(ChatColor.RED + "Sorry, but there", ChatColor.RED + "are no rides setup", ChatColor.RED + "on this server!"));
            empty.setItemMeta(itemMeta);
            rlist.setItem(22, empty);
            rlist.setItem(49, BandUtil.getBackItem());
            player.openInventory(rlist);
            return;
        }
        List<Ride> pageList = rl.get(page);
        List<ItemStack> items = new ArrayList<>();
        for (Ride ride : pageList) {
            ItemStack rideItem = new ItemStack(ride.getId(), 1, ride.getData());
            ItemMeta itemMeta = rideItem.getItemMeta();
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ride.getDisplayName()));
            rideItem.setItemMeta(itemMeta);
            if (rideItem.getItemMeta() == null) {
                continue;
            }
            items.add(rideItem);
        }
        int i = 10;
        for (ItemStack item : items) {
            if (i > 34) {
                break;
            }
            rlist.setItem(i, item);
            if (i == 16 || i == 25) {
                i += 3;
            } else {
                i++;
            }
        }
        if (page > 1) {
            rlist.setItem(48, lastPage);
        }
        if (rl.size() > page) {
            rlist.setItem(50, nextPage);
        }
        rlist.setItem(49, BandUtil.getBackItem());
        player.openInventory(rlist);
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