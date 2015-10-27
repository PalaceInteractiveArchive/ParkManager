package us.mcmagic.magicassistant.utils;

import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.designstation.DesignStation;
import us.mcmagic.magicassistant.handlers.*;
import us.mcmagic.magicassistant.queue.QueueRide;
import us.mcmagic.magicassistant.storage.Backpack;
import us.mcmagic.magicassistant.storage.Locker;
import us.mcmagic.magicassistant.storage.StorageSize;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.permissions.Rank;

import java.util.*;

public class InventoryUtil {
    //Main Menu Items
    private ItemStack rna = new ItemCreator(Material.MINECART, ChatColor.GREEN + "Rides and Attractions",
            Arrays.asList(ChatColor.GREEN + "Ride or experience", ChatColor.GREEN + "an Attraction from",
                    ChatColor.GREEN + "Walt Disney World!"));
    private ItemStack sne = new ItemCreator(Material.FIREWORK, ChatColor.GREEN + "Shows and Events",
            Arrays.asList(ChatColor.GREEN + "Watch one of the", ChatColor.GREEN + "famous " + ChatColor.AQUA +
                    "MCMagic " + ChatColor.GREEN + "Shows!"));
    private ItemStack hnr = new ItemCreator(Material.BED, ChatColor.GREEN + "Hotels and Resorts ",
            Arrays.asList(ChatColor.GREEN + "Visit and rent a room from", ChatColor.GREEN + "a Walt Disney World Resort!"));
    private ItemStack toggleon = new ItemCreator(Material.WOOL, 1, (byte) 14, ChatColor.AQUA + "Guest Visibility " +
            ChatColor.GOLD + "➠ " + ChatColor.RED + "Hidden", Collections.singletonList(ChatColor.GREEN +
            "Click to show Guests!"));
    private ItemStack toggleoff = new ItemCreator(Material.WOOL, 1, (byte) 5, ChatColor.AQUA + "Guest Visibility " +
            ChatColor.GOLD + "➠ " + ChatColor.GREEN + "Visible", Collections.singletonList(ChatColor.GREEN +
            "Click to hide Guests!"));
    private ItemStack shop = new ItemCreator(Material.GOLD_BOOTS, ChatColor.GREEN + "Shop ",
            Arrays.asList(ChatColor.GREEN + "Purchase Items!"));
    private ItemStack food = new ItemCreator(Material.POTATO_ITEM, ChatColor.GREEN + "Find Food", Arrays.asList(
            ChatColor.GREEN + "Visit a restaurant", ChatColor.GREEN + "to get some food!"));
    private ItemStack hub = new ItemCreator(Material.ENDER_PEARL, ChatColor.GREEN + "Return to Hub", Arrays.asList(
            ChatColor.GREEN + "Return to the", ChatColor.GREEN + "Hub Server!"));
    private ItemStack parks = new ItemCreator(Material.NETHER_STAR, ChatColor.GREEN + "Park Menu", Arrays.asList(
            ChatColor.GREEN + "Visit one of the Walt", ChatColor.GREEN + "Disney World Parks!"));
    private ItemStack custom = new ItemCreator(Material.FIREWORK_CHARGE, ChatColor.GREEN + "Customize your MagicBand",
            Arrays.asList(ChatColor.GREEN + "Make your MagicBand", ChatColor.GREEN + "perfect for you!"));
    private ItemStack arcade = new ItemCreator(Material.GLOWSTONE_DUST, ChatColor.GREEN + "Arcade", Arrays.asList(
            ChatColor.YELLOW + "Play some unique", ChatColor.YELLOW + "MCMagic Mini-Games!"));
    private ItemStack creative = new ItemCreator(Material.GRASS, ChatColor.GREEN + "Creative", Arrays.asList(
            ChatColor.YELLOW + "Create your", ChatColor.GREEN + "own " + ChatColor.RED + "M" + ChatColor.GOLD + "a"
                    + ChatColor.YELLOW + "g" + ChatColor.DARK_GREEN + "i" + ChatColor.BLUE + "c" + ChatColor.LIGHT_PURPLE + "!"));
    private ItemStack fastpass = new ItemCreator(Material.CLAY_BRICK, 1, ChatColor.GREEN + "Purchase FastPass",
            Arrays.asList(ChatColor.YELLOW + "Use a FastPass to skip", ChatColor.YELLOW + "the line on most rides!"));
    //Park Menu Items
    private ItemStack mk = new ItemCreator(Material.DIAMOND_HOE, ChatColor.AQUA + "Magic Kingdom",
            Collections.singletonList(ChatColor.GREEN + "/join MK"));
    private ItemStack epcot = new ItemCreator(Material.SNOW_BALL, ChatColor.AQUA + "Epcot",
            Collections.singletonList(ChatColor.GREEN + "/join Epcot"));
    private ItemStack hws = new ItemCreator(Material.JUKEBOX, ChatColor.AQUA + "Hollywood Studios",
            Collections.singletonList(ChatColor.GREEN + "/join HWS"));
    private ItemStack ak = new ItemCreator(Material.SAPLING, 1, (byte) 5, ChatColor.AQUA + "Animal Kingdom",
            Collections.singletonList(ChatColor.GREEN + "/join AK"));
    private ItemStack tl = new ItemCreator(Material.WATER_BUCKET, ChatColor.AQUA + "Typhoon Lagoon",
            Collections.singletonList(ChatColor.GREEN + "/join Typhoon"));
    private ItemStack dcl = new ItemCreator(Material.BOAT, ChatColor.AQUA + "Disney Cruise Line",
            Collections.singletonList(ChatColor.GREEN + "/join DCL"));
    private ItemStack seasonal = new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 12, ChatColor.GREEN +
            "Seasonal", Arrays.asList(ChatColor.GREEN + "/join Seasonal"));
    //My Profile
    private ItemStack dvc = new ItemCreator(Material.DIAMOND, ChatColor.AQUA + "Make a Donation!");
    private ItemStack web = new ItemCreator(Material.REDSTONE, ChatColor.GREEN + "Website");
    private ItemStack locker = new ItemCreator(Material.ENDER_CHEST, ChatColor.GREEN + "Locker");
    private ItemStack rc = new ItemCreator(Material.EMERALD, ChatColor.GREEN + "Ride Counter");
    private ItemStack mumble = new ItemCreator(Material.COMPASS, ChatColor.GREEN + "Mumble");
    private ItemStack packs = new ItemCreator(Material.NOTE_BLOCK, ChatColor.GREEN + "Resource/Audio Packs");
    private ItemStack prefs = new ItemCreator(Material.DIODE, ChatColor.GREEN + "Player Settings");
    //Pages
    private ItemStack nextPage = new ItemCreator(Material.ARROW, ChatColor.GREEN + "Next Page");
    private ItemStack lastPage = new ItemCreator(Material.ARROW, ChatColor.GREEN + "Last Page");
    //Customize Menu
    private ItemStack nameChange = new ItemCreator(Material.JUKEBOX, ChatColor.GREEN + "Change Name Color");
    //Customize Name
    private ItemStack redBand = new ItemCreator(Material.FIREWORK_CHARGE, ChatColor.RED + "Red",
            new ArrayList<String>());
    private ItemStack orangeBand = new ItemCreator(Material.FIREWORK_CHARGE, ChatColor.GOLD + "Orange",
            new ArrayList<String>());
    private ItemStack yellowBand = new ItemCreator(Material.FIREWORK_CHARGE, ChatColor.YELLOW + "Yellow",
            new ArrayList<String>());
    private ItemStack greenBand = new ItemCreator(Material.FIREWORK_CHARGE, ChatColor.GREEN + "Green",
            new ArrayList<String>());
    private ItemStack blueBand = new ItemCreator(Material.FIREWORK_CHARGE, ChatColor.BLUE + "Blue",
            new ArrayList<String>());
    private ItemStack purpleBand = new ItemCreator(Material.FIREWORK_CHARGE, ChatColor.DARK_PURPLE + "Purple",
            new ArrayList<String>());
    private ItemStack pinkBand = new ItemCreator(Material.FIREWORK_CHARGE, ChatColor.LIGHT_PURPLE + "Pink",
            new ArrayList<String>());
    private ItemStack s1Band;
    private ItemStack s2Band;
    private ItemStack s3Band;
    private ItemStack s4Band;
    private ItemStack s5Band;
    //Customize Color
    private ItemStack red = new ItemCreator(Material.WOOL, 1, (byte) 14, ChatColor.RED + "Red",
            new ArrayList<String>());
    private ItemStack orange = new ItemCreator(Material.WOOL, 1, (byte) 1, ChatColor.GOLD + "Orange",
            new ArrayList<String>());
    private ItemStack yellow = new ItemCreator(Material.WOOL, 1, (byte) 4, ChatColor.YELLOW + "Yellow",
            new ArrayList<String>());
    private ItemStack green = new ItemCreator(Material.WOOL, 1, (byte) 5, ChatColor.GREEN + "Green",
            new ArrayList<String>());
    private ItemStack darkGreen = new ItemCreator(Material.WOOL, 1, (byte) 13, ChatColor.DARK_GREEN + "Dark Green",
            new ArrayList<String>());
    private ItemStack blue = new ItemCreator(Material.WOOL, 1, (byte) 11, ChatColor.BLUE + "Blue",
            new ArrayList<String>());
    private ItemStack purple = new ItemCreator(Material.WOOL, 1, (byte) 10, ChatColor.DARK_PURPLE + "Purple",
            new ArrayList<String>());
    //Shows and Events
    private ItemStack fant = new ItemCreator(Material.DIAMOND_HELMET, ChatColor.BLUE + "Fantasmic!");
    private ItemStack iroe = new ItemCreator(Material.MONSTER_EGG, ChatColor.GREEN + "IROE");
    private ItemStack wishes = new ItemCreator(Material.BLAZE_ROD, ChatColor.AQUA + "Wishes!");
    private ItemStack tfant = new ItemCreator(Material.DIAMOND_HELMET, ChatColor.AQUA + "Taste of Fantasmic!");
    private ItemStack msep = new ItemCreator(Material.GLOWSTONE_DUST, ChatColor.YELLOW +
            "Main Street Electrical Parade");
    private ItemStack fofp = new ItemCreator(Material.INK_SACK, 1, (byte) 12, ChatColor.DARK_AQUA +
            "Festival of Fantasy Parade", Collections.singletonList(""));
    private ItemStack times = new ItemCreator(Material.BOOK, ChatColor.GREEN + "Show Timetable");
    //Rides and Attractions
    private ItemStack ride = new ItemCreator(Material.MINECART, ChatColor.GREEN + "Rides");
    private ItemStack wait = new ItemCreator(Material.WATCH, ChatColor.GREEN + "Wait Times");
    private ItemStack attraction = new ItemCreator(Material.GLOWSTONE_DUST, ChatColor.GREEN + "Attractions");
    //Hotels and Resorts
    private ItemStack viewMyRooms = new ItemCreator(Material.BED, 1, ChatColor.GREEN + "Visit Your Hotel Room",
            Arrays.asList(ChatColor.GREEN + "View the room that you booked!"));
    private ItemStack viewHotels = new ItemCreator(Material.EMERALD, 1, ChatColor.GREEN + "Rent a Hotel Room",
            Collections.singletonList(ChatColor.GREEN + "Book a hotel room!"));
    //Show Timetable
    private ItemStack dark49 = new ItemCreator(Material.WOOL, 1, (byte) 11, "4pm & 9pm EST", new ArrayList<String>());
    private ItemStack dark11 = new ItemCreator(Material.WOOL, 1, (byte) 11, "11am EST", new ArrayList<String>());
    private ItemStack light49 = new ItemCreator(Material.WOOL, 1, (byte) 3, "4pm & 9pm EST", new ArrayList<String>());
    private ItemStack light11 = new ItemCreator(Material.WOOL, 1, (byte) 3, "11am EST", new ArrayList<String>());
    private ItemStack assistance = new ItemCreator(Material.REDSTONE_BLOCK, 1, "Ask a Staff Member for assistance",
            new ArrayList<String>());
    private ItemStack na = new ItemCreator(Material.BARRIER, 1, "N/A", new ArrayList<String>());
    private ItemStack m = new ItemStack(Material.BANNER);
    private ItemStack t = new ItemStack(Material.BANNER);
    private ItemStack w = new ItemStack(Material.BANNER);
    private ItemStack th = new ItemStack(Material.BANNER);
    private ItemStack f = new ItemStack(Material.BANNER);
    private ItemStack s = new ItemStack(Material.BANNER);
    private ItemStack su = new ItemStack(Material.BANNER);
    //Storage
    private ItemStack loadingPack = new ItemCreator(Material.STAINED_CLAY, 1, (byte) 3, ChatColor.DARK_AQUA +
            "Loading Backpack...", new ArrayList<String>());
    private ItemStack loadingLocker = new ItemCreator(Material.STAINED_CLAY, 1, (byte) 3, ChatColor.DARK_AQUA +
            "Loading Locker...", new ArrayList<String>());


    public InventoryUtil() {
        FireworkEffectMeta rbm = (FireworkEffectMeta) redBand.getItemMeta();
        FireworkEffectMeta obm = (FireworkEffectMeta) orangeBand.getItemMeta();
        FireworkEffectMeta ybm = (FireworkEffectMeta) yellowBand.getItemMeta();
        FireworkEffectMeta gbm = (FireworkEffectMeta) greenBand.getItemMeta();
        FireworkEffectMeta bbm = (FireworkEffectMeta) blueBand.getItemMeta();
        FireworkEffectMeta pbm = (FireworkEffectMeta) purpleBand.getItemMeta();
        FireworkEffectMeta pibm = (FireworkEffectMeta) pinkBand.getItemMeta();
        rbm.setEffect(FireworkEffect.builder().withColor(Color.fromRGB(255, 40, 40)).build());
        obm.setEffect(FireworkEffect.builder().withColor(Color.fromRGB(247, 140, 0)).build());
        ybm.setEffect(FireworkEffect.builder().withColor(Color.fromRGB(239, 247, 0)).build());
        gbm.setEffect(FireworkEffect.builder().withColor(Color.fromRGB(0, 192, 13)).build());
        bbm.setEffect(FireworkEffect.builder().withColor(Color.fromRGB(41, 106, 255)).build());
        pbm.setEffect(FireworkEffect.builder().withColor(Color.fromRGB(176, 0, 220)).build());
        pibm.setEffect(FireworkEffect.builder().withColor(Color.fromRGB(246, 120, 255)).build());
        redBand.setItemMeta(rbm);
        orangeBand.setItemMeta(obm);
        yellowBand.setItemMeta(ybm);
        greenBand.setItemMeta(gbm);
        blueBand.setItemMeta(bbm);
        purpleBand.setItemMeta(pbm);
        pinkBand.setItemMeta(pibm);
        BannerMeta bm = (BannerMeta) m.getItemMeta();
        BannerMeta bt = (BannerMeta) t.getItemMeta();
        BannerMeta bw = (BannerMeta) w.getItemMeta();
        BannerMeta bth = (BannerMeta) th.getItemMeta();
        BannerMeta bf = (BannerMeta) f.getItemMeta();
        BannerMeta bs = (BannerMeta) s.getItemMeta();
        BannerMeta bsu = (BannerMeta) su.getItemMeta();
        bm.setBaseColor(DyeColor.LIGHT_BLUE);
        bt.setBaseColor(DyeColor.LIGHT_BLUE);
        bw.setBaseColor(DyeColor.LIGHT_BLUE);
        bth.setBaseColor(DyeColor.LIGHT_BLUE);
        bf.setBaseColor(DyeColor.LIGHT_BLUE);
        bs.setBaseColor(DyeColor.LIGHT_BLUE);
        bsu.setBaseColor(DyeColor.LIGHT_BLUE);
        List<Pattern> m = new ArrayList<>();
        List<Pattern> t = new ArrayList<>();
        List<Pattern> w = new ArrayList<>();
        List<Pattern> f = new ArrayList<>();
        List<Pattern> s = new ArrayList<>();
        DyeColor bl = DyeColor.BLACK;
        DyeColor blu = DyeColor.LIGHT_BLUE;
        m.add(new Pattern(bl, PatternType.TRIANGLE_TOP));
        m.add(new Pattern(bl, PatternType.TRIANGLES_TOP));
        m.add(new Pattern(bl, PatternType.STRIPE_LEFT));
        m.add(new Pattern(bl, PatternType.STRIPE_RIGHT));
        t.add(new Pattern(bl, PatternType.STRIPE_CENTER));
        t.add(new Pattern(bl, PatternType.STRIPE_TOP));
        w.add(new Pattern(bl, PatternType.TRIANGLE_BOTTOM));
        w.add(new Pattern(blu, PatternType.TRIANGLES_BOTTOM));
        w.add(new Pattern(bl, PatternType.STRIPE_LEFT));
        w.add(new Pattern(bl, PatternType.STRIPE_RIGHT));
        f.add(new Pattern(bl, PatternType.STRIPE_MIDDLE));
        f.add(new Pattern(blu, PatternType.STRIPE_RIGHT));
        f.add(new Pattern(bl, PatternType.STRIPE_LEFT));
        f.add(new Pattern(bl, PatternType.STRIPE_TOP));
        s.add(new Pattern(bl, PatternType.TRIANGLE_TOP));
        s.add(new Pattern(bl, PatternType.TRIANGLE_BOTTOM));
        s.add(new Pattern(bl, PatternType.SQUARE_TOP_RIGHT));
        s.add(new Pattern(bl, PatternType.SQUARE_BOTTOM_LEFT));
        s.add(new Pattern(blu, PatternType.RHOMBUS_MIDDLE));
        s.add(new Pattern(bl, PatternType.STRIPE_DOWNRIGHT));
        bm.setPatterns(m);
        bt.setPatterns(t);
        bw.setPatterns(w);
        bth.setPatterns(t);
        bf.setPatterns(f);
        bs.setPatterns(s);
        bsu.setPatterns(s);
        bm.setDisplayName(ChatColor.GREEN + "Monday");
        bt.setDisplayName(ChatColor.GREEN + "Tuesday");
        bw.setDisplayName(ChatColor.GREEN + "Wednesday");
        bth.setDisplayName(ChatColor.GREEN + "Thursday");
        bf.setDisplayName(ChatColor.GREEN + "Friday");
        bs.setDisplayName(ChatColor.GREEN + "Saturday");
        bsu.setDisplayName(ChatColor.GREEN + "Sunday");
        this.m.setItemMeta(bm);
        this.t.setItemMeta(bt);
        this.w.setItemMeta(bw);
        this.th.setItemMeta(bth);
        this.f.setItemMeta(bf);
        this.s.setItemMeta(bs);
        this.su.setItemMeta(bsu);
        //MagicBands
        s1Band = new ItemCreator(MagicAssistant.bandUtil.getBandMaterial(BandColor.SPECIAL1),
                ChatColor.BLUE + "Holiday Band", new ArrayList<String>());
        s2Band = new ItemCreator(MagicAssistant.bandUtil.getBandMaterial(BandColor.SPECIAL2),
                ChatColor.RED + "Big Hero 6", new ArrayList<String>());
        s3Band = new ItemCreator(MagicAssistant.bandUtil.getBandMaterial(BandColor.SPECIAL3),
                ChatColor.GRAY + "Haunted Mansion", new ArrayList<String>());
        s4Band = new ItemCreator(MagicAssistant.bandUtil.getBandMaterial(BandColor.SPECIAL4),
                ChatColor.DARK_AQUA + "Sorcerer Mickey", new ArrayList<String>());
        s5Band = new ItemCreator(MagicAssistant.bandUtil.getBandMaterial(BandColor.SPECIAL5),
                ChatColor.LIGHT_PURPLE + "Princess", new ArrayList<String>());
    }

    public void openInventory(final Player player, InventoryType inv) {
        try {
            PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
            Rank rank = MCMagicCore.getUser(player.getUniqueId()).getRank();
            switch (inv) {
                case MAINMENU: {
                    Inventory main = Bukkit.createInventory(player, 27, ChatColor.BLUE
                            + player.getName() + "'s MagicBand");
                    ItemStack playerInfo = HeadUtil.getPlayerHead(MCMagicCore.getUser(player.getUniqueId())
                            .getTextureHash(), ChatColor.GREEN + "My Profile");
                    ItemMeta im = playerInfo.getItemMeta();
                    im.setLore(Collections.singletonList(ChatColor.GRAY + "Loading..."));
                    playerInfo.setItemMeta(im);
                    main.setItem(10, hnr);
                    main.setItem(11, sne);
                    main.setItem(12, rna);
                    main.setItem(13, parks);
                    main.setItem(14, shop);
                    main.setItem(15, playerInfo);
                    if (MagicAssistant.vanishUtil.isInHideAll(player.getUniqueId())) {
                        main.setItem(16, toggleon);
                    } else {
                        main.setItem(16, toggleoff);
                    }
                    player.openInventory(main);
                    MagicAssistant.bandUtil.loadPlayerData(player);
                    /*
                    ItemStack time = new ItemCreator(Material.WATCH);
                    ItemMeta tm = time.getItemMeta();
                    tm.setDisplayName(ChatColor.GREEN + "Current Time in EST");
                    tm.setLore(Collections.singletonList(ChatColor.YELLOW + MagicAssistant.bandUtil.currentTime()));
                    time.setItemMeta(tm);
                    main.setItem(0, rna);
                    main.setItem(9, sne);
                    main.setItem(18, hnr);
                    if (MagicAssistant.vanishUtil.isInHideAll(player.getUniqueId())) {
                        main.setItem(2, toggleon);
                    } else {
                        main.setItem(2, toggleoff);
                    }
                    main.setItem(11, shop);
                    main.setItem(20, food);
                    main.setItem(4, time);
                    main.setItem(13, hub);
                    main.setItem(22, parks);
                    main.setItem(6, packs);
                    main.setItem(15, playerInfo);
                    main.setItem(24, custom);
                    main.setItem(8, arcade);
                    main.setItem(17, creative);
                    main.setItem(26, fastpass);
                    MagicAssistant.bandUtil.loadPlayerData(player);
                    */
                    return;
                }
                case PARK: {
                    Inventory park = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Park Menu");
                    park.setItem(10, mk);
                    park.setItem(11, epcot);
                    park.setItem(12, hws);
                    park.setItem(13, ak);
                    park.setItem(14, tl);
                    park.setItem(15, dcl);
                    park.setItem(16, seasonal);
                    park.setItem(22, BandUtil.getBackItem());
                    player.openInventory(park);
                    return;
                }
                case FOOD: {
                    Inventory foodMenu = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Food Menu");
                    List<FoodLocation> foodLocations = MagicAssistant.foodLocations;
                    // If odd amount of items
                    int place = 13;
                    if (foodLocations.size() % 2 == 1) {
                        int amount = 1;
                        for (FoodLocation loc : foodLocations) {
                            if (place > 16) {
                                break;
                            }
                            @SuppressWarnings("deprecation")
                            ItemStack f = new ItemCreator(Material.getMaterial(loc.getType()), 1, loc.getData(),
                                    ChatColor.translateAlternateColorCodes('&', loc.getName()),
                                    Collections.singletonList(ChatColor.GREEN + "/warp " + loc.getWarp()));
                            foodMenu.setItem(place, f);
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
                            @SuppressWarnings("deprecation")
                            ItemStack f = new ItemCreator(Material.getMaterial(loc.getType()), 1, loc.getData(),
                                    ChatColor.translateAlternateColorCodes('&', loc.getName()),
                                    Collections.singletonList(ChatColor.GREEN + "/warp " + loc.getWarp()));
                            foodMenu.setItem(place, f);
                            if (amount % 2 == 0) {
                                place -= amount;
                            } else {
                                place += amount;
                            }
                            amount++;
                        }
                        foodMenu.setItem(22, BandUtil.getBackItem());
                        player.openInventory(foodMenu);
                    }
                    return;
                }
                case MYPROFILE: {
                    Inventory pmenu = Bukkit.createInventory(player, 27, ChatColor.BLUE + "My Profile");
                    pmenu.setItem(10, web);
                    pmenu.setItem(11, dvc);
                    pmenu.setItem(12, locker);
                    pmenu.setItem(14, rc);
                    pmenu.setItem(15, prefs);
                    pmenu.setItem(16, mumble);
                    pmenu.setItem(22, BandUtil.getBackItem());
                    player.openInventory(pmenu);
                    return;
                }
                case SHOWSANDEVENTS: {
                    Inventory shows = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Shows and Events");
                    shows.setItem(8, times);
                    shows.setItem(9, fant);
                    shows.setItem(11, iroe);
                    shows.setItem(13, wishes);
                    shows.setItem(15, msep);
                    shows.setItem(17, fofp);
                    shows.setItem(22, BandUtil.getBackItem());
                    player.openInventory(shows);
                    return;
                }
                case CUSTOMIZE: {
                    Inventory custom = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Customize Menu");
                    ItemStack band;
                    if (data.getSpecial()) {
                        band = new ItemCreator(MagicAssistant.bandUtil.getBandMaterial(data.getBandColor()));
                        ItemMeta bm = band.getItemMeta();
                        bm.setDisplayName(ChatColor.GREEN + "Change MagicBand Color");
                        band.setItemMeta(bm);
                    } else {
                        band = new ItemCreator(Material.FIREWORK_CHARGE);
                        FireworkEffectMeta bm = (FireworkEffectMeta) band.getItemMeta();
                        bm.setEffect(FireworkEffect.builder().withColor(MagicAssistant.bandUtil.getBandColor(data.getBandColor())).build());
                        bm.setDisplayName(ChatColor.GREEN + "Change MagicBand Color");
                        band.setItemMeta(bm);
                    }
                    custom.setItem(11, band);
                    custom.setItem(15, nameChange);
                    custom.setItem(22, BandUtil.getBackItem());
                    player.openInventory(custom);
                    return;
                }
                case CUSTOMNAME: {
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
                }
                case CUSTOMCOLOR: {
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
                }
                case SPECIALCOLOR: {
                    Inventory special = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Special Edition MagicBands");
                    if (rank.equals(Rank.GUEST)) {
                        special.setItem(13, new ItemCreator(Material.REDSTONE_BLOCK, ChatColor.RED + "DVC Members only!"
                                , Arrays.asList(ChatColor.RED + "Sorry, but you have", ChatColor.RED + "to be a DVC Member",
                                ChatColor.RED + "to use Special Edition", ChatColor.RED + "MagicBand Designs!")));
                    } else {
                        special.setItem(11, s1Band);
                        special.setItem(12, s2Band);
                        special.setItem(13, s3Band);
                        special.setItem(14, s4Band);
                        special.setItem(15, s5Band);
                    }
                    special.setItem(21, lastPage);
                    special.setItem(22, BandUtil.getBackItem());
                    player.openInventory(special);
                    return;
                }
                case RIDESANDATTRACTIONS: {
                    Inventory rna = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Rides and Attractions");
                    rna.setItem(11, ride);
                    rna.setItem(13, wait);
                    rna.setItem(15, attraction);
                    rna.setItem(22, BandUtil.getBackItem());
                    player.openInventory(rna);
                    return;
                }
                case HOTELSANDRESORTS: {
                    Inventory hotelsAndResorts = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Hotels and Resorts");
                    hotelsAndResorts.setItem(11, viewMyRooms);
                    hotelsAndResorts.setItem(15, viewHotels);
                    hotelsAndResorts.setItem(22, BandUtil.getBackItem());
                    player.openInventory(hotelsAndResorts);
                    return;
                }
                case MYHOTELROOMS: {
                    Inventory viewMyHotelRooms = Bukkit.createInventory(player, 27, ChatColor.BLUE + "My Hotel Rooms");
                    List<HotelRoom> rooms = new ArrayList<>();
                    for (HotelRoom room : MagicAssistant.hotelManager.getHotelRooms()) {
                        if (room.isOccupied() && room.getCurrentOccupant().equals(player.getUniqueId())) {
                            rooms.add(room);
                        }
                    }
                    int roomItemPlacement = 13 - ((rooms.size() - 1) / 2);
                    for (HotelRoom room : rooms) {
                        ItemStack roomItem = new ItemCreator(Material.BED, 1);
                        ItemMeta rim = roomItem.getItemMeta();
                        rim.setDisplayName(ChatColor.GREEN + room.getName());
                        if (room.getCheckoutTime() <= (System.currentTimeMillis() / 1000)) {
                            MagicAssistant.hotelManager.checkout(room, true);
                            return;
                        }
                        String times = DateUtil.formatDateDiff(room.getCheckoutTime() * 1000);
                        List<String> himl = Arrays.asList(ChatColor.DARK_GREEN + "You have " + times, ChatColor.DARK_GREEN
                                + "left for this reservation.");
                        rim.setLore(himl);
                        roomItem.setItemMeta(rim);
                        viewMyHotelRooms.setItem(roomItemPlacement, roomItem);
                        if (roomItemPlacement == 17) {
                            break;
                        } else {
                            roomItemPlacement++;
                        }
                    }
                    viewMyHotelRooms.setItem(22, BandUtil.getBackItem());
                    player.openInventory(viewMyHotelRooms);
                    return;
                }
                case HOTELS: {
                    Inventory viewAvailableHotels = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Hotels");
                    List<String> availableHotels = new ArrayList<>();
                    for (HotelRoom room : MagicAssistant.hotelManager.getHotelRooms()) {
                        if (!availableHotels.contains(room.getHotelName())) {
                            availableHotels.add(room.getHotelName());
                        }
                    }
                    int hotelItemPlacement = 10;
                    for (String hotel : availableHotels) {
                        ItemStack hotelItem = new ItemCreator(Material.BED, 1);
                        ItemMeta him = hotelItem.getItemMeta();
                        him.setDisplayName(ChatColor.GREEN + hotel);
                        List<String> himl = Collections.singletonList(ChatColor.GREEN + "Click to view rooms in this hotel.");
                        him.setLore(himl);
                        hotelItem.setItemMeta(him);
                        viewAvailableHotels.setItem(hotelItemPlacement, hotelItem);
                        if (hotelItemPlacement == 17) {
                            break;
                        } else {
                            hotelItemPlacement++;
                        }
                    }
                    viewAvailableHotels.setItem(22, BandUtil.getBackItem());
                    player.openInventory(viewAvailableHotels);
                    return;
                }
                case PLAYERSETTINGS: {
                    Inventory settings = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Player Settings");
                    List<String> enab = Collections.singletonList(ChatColor.GREEN + "Enabled");
                    List<String> disab = Collections.singletonList(ChatColor.RED + "Disabled");
                    ItemStack flash;
                    ItemStack visibility;
                    ItemStack loop;
                    ItemStack hotel;
                    if (data.getFlash()) {
                        flash = new ItemCreator(Material.WOOL, 1, (byte) 5, ChatColor.GREEN + "Flash Effects", enab);
                    } else {
                        flash = new ItemCreator(Material.WOOL, 1, (byte) 14, ChatColor.RED + "Flash Effects", disab);
                    }
                    if (data.getVisibility()) {
                        visibility = new ItemCreator(Material.WOOL, 1, (byte) 5, ChatColor.GREEN + "Player Visibility", enab);
                    } else {
                        visibility = new ItemCreator(Material.WOOL, 1, (byte) 14, ChatColor.RED + "Player Visibility", disab);
                    }
                    if (data.getFountain()) {
                        loop = new ItemCreator(Material.WOOL, 1, (byte) 5, ChatColor.GREEN + "Fountains", enab);
                    } else {
                        loop = new ItemCreator(Material.WOOL, 1, (byte) 14, ChatColor.RED + "Fountains", disab);
                    }
                    if (data.getHotel()) {
                        hotel = new ItemCreator(Material.WOOL, 1, (byte) 5, ChatColor.GREEN + "Friends Access Hotel Room", enab);
                    } else {
                        hotel = new ItemCreator(Material.WOOL, 1, (byte) 14, ChatColor.RED + "Friends Access Hotel Room", disab);
                    }
                    settings.setItem(10, flash);
                    settings.setItem(12, visibility);
                    settings.setItem(14, loop);
                    settings.setItem(16, hotel);
                    settings.setItem(22, BandUtil.getBackItem());
                    player.openInventory(settings);
                    return;
                }
                case DESIGNSTATION: {
                    DesignStation.openPickModelInventory(player);
                    return;
                }
                case FASTPASS: {
                    Inventory fp = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Purchase FastPass");
                    fp.setItem(22, BandUtil.getBackItem());
                    int current = data.getFastpass();
                    if (current >= 3) {
                        ItemStack max = new ItemCreator(Material.REDSTONE_BLOCK, ChatColor.RED +
                                "You can only have up to 3 FastPasses at one time!");
                        fp.setItem(13, max);
                        player.openInventory(fp);
                        return;
                    }
                    int daily = data.getDailyfp();
                    int day = data.getDay();
                    long timestamp = new Date().getTime();
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(timestamp);
                    int curday = cal.get(Calendar.DAY_OF_YEAR);
                    if (curday > day) {
                        data.setDailyfp(0);
                        daily = 0;
                    }
                    if (daily >= 3) {
                        ItemStack max = new ItemCreator(Material.REDSTONE_BLOCK, ChatColor.RED +
                                "You can purchase 3 FastPasses each day!");
                        fp.setItem(13, max);
                        player.openInventory(fp);
                        return;
                    }
                    ItemStack info = new ItemCreator(Material.WOOL, 1, (byte) 9, ChatColor.GREEN + "You currently have " +
                            ChatColor.AQUA + current + ChatColor.GREEN + " Fastpass" + (current == 1 ? "" : "es"),
                            new ArrayList<String>());
                    ItemStack yes = new ItemCreator(Material.WOOL, 1, (byte) 5, ChatColor.GREEN + "Yes",
                            Arrays.asList(ChatColor.DARK_AQUA + "Click to purchase a", ChatColor.DARK_AQUA +
                                            "FastPass for " + ChatColor.GREEN + "$50.",
                                    ChatColor.DARK_AQUA + "This cannot be undone!"));
                    ItemStack no = new ItemCreator(Material.WOOL, 1, (byte) 14, ChatColor.GREEN + "No",
                            Collections.singletonList(ChatColor.DARK_AQUA + "Click to return to the Shop Menu"));
                    fp.setItem(4, info);
                    fp.setItem(11, yes);
                    fp.setItem(15, no);
                    player.openInventory(fp);
                    return;
                }
                case STORAGE: {
                    Inventory storage = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Storage Upgrade");
                    StorageSize packSize = data.getBackpack().getSize();
                    StorageSize lockerSize = data.getLocker().getSize();
                    List<String> packLore = new ArrayList<>(Arrays.asList(ChatColor.YELLOW + "Current Size: " +
                            packSize.getSize()));
                    switch (packSize) {
                        case SMALL:
                            packLore.add(ChatColor.YELLOW + "Click to upgrade to the Large size");
                            packLore.add(ChatColor.GREEN + "Cost: $500");
                            break;
                        case LARGE:
                            packLore.add(ChatColor.RED + "Cannot be upgraded anymore!");
                            break;
                    }
                    ItemStack pack = new ItemCreator(Material.CHEST, ChatColor.GREEN + "Upgrade Backpack",
                            packLore);
                    List<String> lockerLore = new ArrayList<>(Arrays.asList(ChatColor.YELLOW + "Current Size: " +
                            lockerSize.getSize()));
                    switch (lockerSize) {
                        case SMALL:
                            lockerLore.add(ChatColor.YELLOW + "Click to upgrade to the Large size");
                            lockerLore.add(ChatColor.GREEN + "Cost: $500");
                            break;
                        case LARGE:
                            lockerLore.add(ChatColor.RED + "Cannot be upgraded anymore!");
                            break;
                    }
                    ItemStack locker = new ItemCreator(Material.CHEST, ChatColor.GREEN + "Upgrade Locker",
                            lockerLore);
                    storage.setItem(11, pack);
                    storage.setItem(15, locker);
                    storage.setItem(22, BandUtil.getBackItem());
                    player.openInventory(storage);
                    return;
                }
                case BACKPACK: {
                    Backpack pack = data.getBackpack();
                    if (pack == null) {
                        Inventory load = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Loading Backpack...");
                        load.setItem(13, loadingPack);
                        player.openInventory(load);
                        MagicAssistant.storageManager.setLoadingPack(player);
                        return;
                    } else {
                        player.openInventory(pack.getInventory());
                        return;
                    }
                }
                case LOCKER: {
                    Locker locker = data.getLocker();
                    if (locker == null) {
                        Inventory load = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Loading Locker...");
                        load.setItem(13, loadingLocker);
                        player.openInventory(load);
                        MagicAssistant.storageManager.setLoadingLocker(player);
                        return;
                    } else {
                        player.openInventory(locker.getInventory());
                        return;
                    }
                }
                case SHOWTIMES: {
                    Inventory s = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Show Timetable");
                    s.setItem(1, m);
                    s.setItem(2, t);
                    s.setItem(3, w);
                    s.setItem(4, th);
                    s.setItem(5, f);
                    s.setItem(6, this.s);
                    s.setItem(7, su);
                    s.setItem(9, wishes);
                    s.setItem(10, dark49);
                    s.setItem(11, dark11);
                    s.setItem(12, dark49);
                    s.setItem(13, dark11);
                    s.setItem(14, dark49);
                    s.setItem(15, assistance);
                    s.setItem(16, assistance);
                    s.setItem(18, iroe);
                    s.setItem(19, light11);
                    s.setItem(20, light49);
                    s.setItem(21, light11);
                    s.setItem(22, light49);
                    s.setItem(23, light11);
                    s.setItem(24, assistance);
                    s.setItem(25, assistance);
                    s.setItem(27, tfant);
                    s.setItem(28, na);
                    s.setItem(29, na);
                    s.setItem(30, na);
                    s.setItem(31, na);
                    s.setItem(32, na);
                    s.setItem(33, assistance);
                    s.setItem(34, assistance);
                    s.setItem(49, BandUtil.getBackItem());
                    player.openInventory(s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openHotelRoomListPage(final Player player, String hotelName) {
        Inventory viewAvailableHotelRooms = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Rooms in " + hotelName);
        List<HotelRoom> availableHotelRooms = new ArrayList<>();
        for (HotelRoom room : MagicAssistant.hotelManager.getHotelRooms()) {
            if (room.getHotelName().equalsIgnoreCase(hotelName) && !room.isOccupied()) {
                availableHotelRooms.add(room);
            }
        }
        List<HotelRoom> sortedHotelRooms = new ArrayList<>();
        while (availableHotelRooms.size() > 0) {
            HotelRoom smallest = availableHotelRooms.get(0);
            for (HotelRoom room : availableHotelRooms) {
                if (room.getRoomNumber() < smallest.getRoomNumber()) {
                    smallest = room;
                }
            }
            sortedHotelRooms.add(smallest);
            availableHotelRooms.remove(smallest);
        }
        int placement = 0;
        for (HotelRoom room : sortedHotelRooms) {
            ItemStack item = new ItemCreator(Material.BED, 1);
            ItemMeta him = item.getItemMeta();
            him.setDisplayName(ChatColor.GREEN + room.getName());
            long time = room.getStayLength() / 3600;
            List<String> himl = Arrays.asList(ChatColor.GOLD + "Cost: $" + Integer.toString(room.getCost()),
                    ChatColor.GREEN + "Click to rent this room for " + time + " hours.");
            him.setLore(himl);
            item.setItemMeta(him);
            viewAvailableHotelRooms.setItem(placement, item);
            if (placement == 17) {
                break;
            } else {
                placement++;
            }
        }
        viewAvailableHotelRooms.setItem(22, BandUtil.getBackItem());
        player.openInventory(viewAvailableHotelRooms);
    }

    public void openSpecificHotelRoomPage(final Player player, HotelRoom room) {
        Inventory viewAvailableHotelRooms = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Book Room?");
        ItemStack item = new ItemCreator(Material.BED, 1);
        ItemMeta him = item.getItemMeta();
        him.setDisplayName(ChatColor.GREEN + room.getName());
        long time = room.getStayLength() / 3600;
        List<String> himl = Arrays.asList(ChatColor.GOLD + "Cost: $" + Integer.toString(room.getCost()),
                ChatColor.GREEN + "Click to rent this room for " + time + " hours.");
        him.setLore(himl);
        item.setItemMeta(him);
        viewAvailableHotelRooms.setItem(13, item);
        viewAvailableHotelRooms.setItem(22, BandUtil.getBackItem());
        player.openInventory(viewAvailableHotelRooms);
    }

    public void openSpecificHotelRoomCheckoutPage(final Player player, HotelRoom room) {
        Inventory viewAvailableHotelRooms = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Check Out?");
        if (room.getCheckoutTime() <= (System.currentTimeMillis() / 1000)) {
            MagicAssistant.hotelManager.checkout(room, true);
            return;
        }
        String time = DateUtil.formatDateDiff(room.getCheckoutTime() * 1000);
        List<String> himl = Arrays.asList(ChatColor.RED + "Click to check out before the ", "" +
                ChatColor.RED + (room.getStayLength() / 3600) + "-hour period is over.", ChatColor.DARK_GREEN
                + "You have " + time, ChatColor.DARK_GREEN + "left for this reservation.");
        ItemStack item = new ItemCreator(Material.BOOK, ChatColor.RED + "Check out of " + room.getName(), himl);
        viewAvailableHotelRooms.setItem(13, item);
        viewAvailableHotelRooms.setItem(22, BandUtil.getBackItem());
        player.openInventory(viewAvailableHotelRooms);
    }

    @SuppressWarnings("deprecation")
    public void openAttractionListPage(Player player, int page) {
        HashMap<Integer, List<Attraction>> al = MagicAssistant.attPages;
        Inventory alist;
        if (al.size() > 1) {
            alist = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Attraction List Page " + page);
        } else {
            alist = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Attraction List");
        }
        if (al.isEmpty() || al.get(1).isEmpty()) {
            ItemStack empty = new ItemCreator(Material.STAINED_CLAY, 1, (byte) 14);
            ItemMeta itemMeta = empty.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RED + "Uh oh!");
            itemMeta.setLore(Arrays.asList(ChatColor.RED + "Sorry, but there", ChatColor.RED + "are no attraction setup",
                    ChatColor.RED + "on this server!"));
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
    public void openRideListPage(Player player, int page) {
        HashMap<Integer, List<Ride>> rl = MagicAssistant.ridePages;
        Inventory rlist;
        if (rl.size() > 1) {
            rlist = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Ride List Page " + page);
        } else {
            rlist = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Ride List");
        }
        if (rl.isEmpty() || rl.get(1).isEmpty()) {
            ItemStack empty = new ItemCreator(Material.STAINED_CLAY, 1, (byte) 14);
            ItemMeta itemMeta = empty.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RED + "Uh oh!");
            itemMeta.setLore(Arrays.asList(ChatColor.RED + "Sorry, but there", ChatColor.RED + "are no rides setup",
                    ChatColor.RED + "on this server!"));
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

    public void featureComingSoon(Player player) {
        player.playSound(player.getLocation(), Sound.ITEM_BREAK, 100, 0);
        player.closeInventory();
        player.sendMessage(ChatColor.RED + "This feature is coming soon!");
    }

    public void openWaitTimes(Player player) {
        Inventory inv = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Wait Times");
        List<ItemStack> items = new ArrayList<>();
        for (QueueRide ride : MagicAssistant.queueManager.getRides()) {
            List<String> lore = Arrays.asList(ChatColor.YELLOW + "Wait Time: " + ride.appxWaitTime(), ChatColor.YELLOW +
                    "Players in Queue: " + (ride.getQueueSize() <= 0 ? "None" : ride.getQueueSize()), ChatColor.YELLOW +
                    "Warp: " + ChatColor.GREEN + "/warp " + ride.getWarp());
            items.add(new ItemCreator(Material.SIGN, 1, ride.getName(), lore));
        }
        int i = 10;
        for (ItemStack item : items) {
            if (i > 34) {
                break;
            }
            inv.setItem(i, item);
            if (i == 16 || i == 25) {
                i += 3;
            } else {
                i++;
            }
        }
        if (items.isEmpty()) {
            ItemStack empty = new ItemCreator(Material.STAINED_CLAY, 1, (byte) 14);
            ItemMeta itemMeta = empty.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RED + "Uh oh!");
            itemMeta.setLore(Arrays.asList(ChatColor.RED + "Sorry, but there", ChatColor.RED + "are no rides setup",
                    ChatColor.RED + "on this server!"));
            empty.setItemMeta(itemMeta);
            inv.setItem(22, empty);
            inv.setItem(49, BandUtil.getBackItem());
            player.openInventory(inv);
        }
        inv.setItem(49, BandUtil.getBackItem());
        player.openInventory(inv);
    }

    public void openRideCounter(Player player) {
        Inventory inv = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Ride Counter");
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        int i = 10;
        HashMap<String, Integer> counts = data.getRideCounts();
        if (counts.isEmpty()) {
            ItemStack empty = new ItemCreator(Material.WOOL, 1, (byte) 4, ChatColor.RED + "Uh oh!",
                    Arrays.asList(ChatColor.RED + "Looks like you haven't", ChatColor.RED + "gone on any rides recently."));
            inv.setItem(22, empty);
            inv.setItem(49, BandUtil.getBackItem());
            player.openInventory(inv);
            return;
        }
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            ItemStack stack = new ItemCreator(Material.MINECART, ChatColor.GREEN + entry.getKey(),
                    Arrays.asList(ChatColor.YELLOW + "Rides: " + entry.getValue(), ChatColor.YELLOW + "Park: " +
                            MCMagicCore.getMCMagicConfig().serverName));
            inv.setItem(i, stack);
            if (i == 16 || i == 25 || i == 34 || i == 43) {
                i += 3;
            } else {
                i++;
            }
        }
        inv.setItem(49, BandUtil.getBackItem());
        player.openInventory(inv);
    }
}