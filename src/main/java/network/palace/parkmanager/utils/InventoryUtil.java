package network.palace.parkmanager.utils;

import network.palace.core.Core;
import network.palace.core.achievements.CoreAchievement;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.HeadUtil;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.handlers.*;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.designstation.DesignStation;
import network.palace.parkmanager.queue.QueueRide;
import network.palace.parkmanager.show.handlers.ShowDay;
import network.palace.parkmanager.show.handlers.ShowType;
import network.palace.parkmanager.show.schedule.ScheduledShow;
import network.palace.parkmanager.storage.Backpack;
import network.palace.parkmanager.storage.Locker;
import network.palace.parkmanager.storage.StorageSize;

import java.util.*;
import java.util.stream.Collectors;

public class InventoryUtil {
    //Main Menu Items
    private ItemStack rna = ItemUtil.create(Material.MINECART, ChatColor.GREEN + "Rides and Meet & Greets",
            Arrays.asList(ChatColor.GREEN + "View Rides, Attractions, and", ChatColor.GREEN + "Meet & Greets you can visit!"));
    private ItemStack sne = ItemUtil.create(Material.FIREWORK, ChatColor.GREEN + "Shows and Events",
            Arrays.asList(ChatColor.GREEN + "Watch one of the", ChatColor.GREEN + "famous " + ChatColor.AQUA +
                    "MCMagic " + ChatColor.GREEN + "Shows!"));
    private ItemStack hnr = ItemUtil.create(Material.BED, ChatColor.GREEN + "Hotels and Resorts ",
            Arrays.asList(ChatColor.GREEN + "Visit and rent a room from", ChatColor.GREEN + "a Walt Disney World Resort!"));
    private ItemStack toggleon = ItemUtil.create(Material.WOOL, 1, (byte) 14, ChatColor.AQUA + "Guest Visibility " +
            ChatColor.GOLD + "➠ " + ChatColor.RED + "Hidden", Collections.singletonList(ChatColor.GREEN +
            "Click to show Guests!"));
    private ItemStack toggleoff = ItemUtil.create(Material.WOOL, 1, (byte) 5, ChatColor.AQUA + "Guest Visibility " +
            ChatColor.GOLD + "➠ " + ChatColor.GREEN + "Visible", Collections.singletonList(ChatColor.GREEN +
            "Click to hide Guests!"));
    private ItemStack shop = ItemUtil.create(Material.GOLD_BOOTS, ChatColor.GREEN + "Shop",
            Arrays.asList(ChatColor.GREEN + "Purchase Items!"));
    private ItemStack ward = ItemUtil.create(Material.IRON_CHESTPLATE, ChatColor.GREEN + "Wardrobe Manager",
            Arrays.asList(ChatColor.GREEN + "Change your outfit to clothes", ChatColor.GREEN +
                    "that you can purchase in shops!"));
    private ItemStack food = ItemUtil.create(Material.POTATO_ITEM, ChatColor.GREEN + "Find Food", Arrays.asList(
            ChatColor.GREEN + "Visit a restaurant", ChatColor.GREEN + "to get some food!"));
    private ItemStack ttc = ItemUtil.create(Material.ENDER_PEARL, ChatColor.GREEN + "Transportation and Ticket Center",
            Arrays.asList(ChatColor.GREEN + "Return to the Transportation", ChatColor.GREEN + "and Ticket Center!"));
    private ItemStack parks = ItemUtil.create(Material.NETHER_STAR, ChatColor.GREEN + "Park Menu", Arrays.asList(
            ChatColor.GREEN + "Visit one of the Walt", ChatColor.GREEN + "Disney World Parks!"));
    private ItemStack custom = ItemUtil.create(Material.FIREWORK_CHARGE, ChatColor.GREEN + "Customize your MagicBand",
            Arrays.asList(ChatColor.GREEN + "Make your MagicBand", ChatColor.GREEN + "perfect for you!"));
    private ItemStack arcade = ItemUtil.create(Material.GLOWSTONE_DUST, ChatColor.GREEN + "Arcade", Arrays.asList(
            ChatColor.YELLOW + "Play some unique", ChatColor.YELLOW + "MCMagic Mini-Games!"));
    private ItemStack creative = ItemUtil.create(Material.GRASS, ChatColor.GREEN + "Creative", Arrays.asList(
            ChatColor.YELLOW + "Create your", ChatColor.GREEN + "own " + ChatColor.RED + "M" + ChatColor.GOLD + "a"
                    + ChatColor.YELLOW + "g" + ChatColor.DARK_GREEN + "i" + ChatColor.BLUE + "c" + ChatColor.LIGHT_PURPLE + "!"));
    private ItemStack fastpass = ItemUtil.create(Material.CLAY_BRICK, 1, ChatColor.GREEN + "Purchase FastPass",
            Arrays.asList(ChatColor.YELLOW + "Use a FastPass to skip", ChatColor.YELLOW + "the line on most rides!"));
    //Park Menu Items
    private ItemStack mk = ItemUtil.create(Material.DIAMOND_HOE, ChatColor.AQUA + "Magic Kingdom",
            Collections.singletonList(ChatColor.GREEN + "/join MK"));
    private ItemStack epcot = ItemUtil.create(Material.SNOW_BALL, ChatColor.AQUA + "Epcot",
            Collections.singletonList(ChatColor.GREEN + "/join Epcot"));
    private ItemStack dhs = ItemUtil.create(Material.JUKEBOX, ChatColor.AQUA + "Hollywood Studios",
            Collections.singletonList(ChatColor.GREEN + "/join DHS"));
    private ItemStack ak = ItemUtil.create(Material.SAPLING, 1, (byte) 5, ChatColor.AQUA + "Animal Kingdom",
            Collections.singletonList(ChatColor.GREEN + "/join AK"));
    private ItemStack tl = ItemUtil.create(Material.WATER_BUCKET, ChatColor.AQUA + "Typhoon Lagoon",
            Collections.singletonList(ChatColor.GREEN + "/join Typhoon"));
    private ItemStack dcl = ItemUtil.create(Material.BOAT, ChatColor.AQUA + "Disney Cruise Line",
            Collections.singletonList(ChatColor.GREEN + "/join DCL"));
    private ItemStack seasonal = ItemUtil.create(Material.RED_ROSE, 1, (byte) 2, ChatColor.AQUA +
            "Seasonal", Arrays.asList(ChatColor.GREEN + "/join Seasonal"));
    //My Profile
    private ItemStack dvc = ItemUtil.create(Material.DIAMOND, ChatColor.AQUA + "Make a Donation!");
    private ItemStack web = ItemUtil.create(Material.REDSTONE, ChatColor.GREEN + "Website");
    private ItemStack locker = ItemUtil.create(Material.ENDER_CHEST, ChatColor.GREEN + "Locker");
    private ItemStack ach = ItemUtil.create(Material.NETHER_STAR, ChatColor.GREEN + "Achievements");
    private ItemStack rc = ItemUtil.create(Material.EMERALD, ChatColor.GREEN + "Ride Counter");
    private ItemStack mumble = ItemUtil.create(Material.COMPASS, ChatColor.GREEN + "Mumble");
    private ItemStack packs = ItemUtil.create(Material.NOTE_BLOCK, ChatColor.GREEN + "Resource/Audio Packs");
    private ItemStack prefs = ItemUtil.create(Material.DIODE, ChatColor.GREEN + "Player Settings");
    //Pages
    private ItemStack nextPage = ItemUtil.create(Material.ARROW, ChatColor.GREEN + "Next Page");
    private ItemStack lastPage = ItemUtil.create(Material.ARROW, ChatColor.GREEN + "Last Page");
    //Customize Menu
    private ItemStack nameChange = ItemUtil.create(Material.JUKEBOX, ChatColor.GREEN + "Change Name Color");
    //Customize Name
    private ItemStack redBand = ItemUtil.create(Material.FIREWORK_CHARGE, ChatColor.RED + "Red",
            new ArrayList<>());
    private ItemStack orangeBand = ItemUtil.create(Material.FIREWORK_CHARGE, ChatColor.GOLD + "Orange",
            new ArrayList<>());
    private ItemStack yellowBand = ItemUtil.create(Material.FIREWORK_CHARGE, ChatColor.YELLOW + "Yellow",
            new ArrayList<>());
    private ItemStack greenBand = ItemUtil.create(Material.FIREWORK_CHARGE, ChatColor.GREEN + "Green",
            new ArrayList<>());
    private ItemStack blueBand = ItemUtil.create(Material.FIREWORK_CHARGE, ChatColor.BLUE + "Blue",
            new ArrayList<>());
    private ItemStack purpleBand = ItemUtil.create(Material.FIREWORK_CHARGE, ChatColor.DARK_PURPLE + "Purple",
            new ArrayList<>());
    private ItemStack pinkBand = ItemUtil.create(Material.FIREWORK_CHARGE, ChatColor.LIGHT_PURPLE + "Pink",
            new ArrayList<>());
    private ItemStack s1Band;
    private ItemStack s2Band;
    private ItemStack s3Band;
    private ItemStack s4Band;
    private ItemStack s5Band;
    //Customize Color
    private ItemStack red = ItemUtil.create(Material.WOOL, 1, (byte) 14, ChatColor.RED + "Red",
            new ArrayList<>());
    private ItemStack orange = ItemUtil.create(Material.WOOL, 1, (byte) 1, ChatColor.GOLD + "Orange",
            new ArrayList<>());
    private ItemStack yellow = ItemUtil.create(Material.WOOL, 1, (byte) 4, ChatColor.YELLOW + "Yellow",
            new ArrayList<>());
    private ItemStack green = ItemUtil.create(Material.WOOL, 1, (byte) 5, ChatColor.GREEN + "Green",
            new ArrayList<>());
    private ItemStack darkGreen = ItemUtil.create(Material.WOOL, 1, (byte) 13, ChatColor.DARK_GREEN + "Dark Green",
            new ArrayList<>());
    private ItemStack blue = ItemUtil.create(Material.WOOL, 1, (byte) 11, ChatColor.BLUE + "Blue",
            new ArrayList<>());
    private ItemStack purple = ItemUtil.create(Material.WOOL, 1, (byte) 10, ChatColor.DARK_PURPLE + "Purple",
            new ArrayList<>());
    //Shows and Events
    private ItemStack fant = ItemUtil.create(Material.DIAMOND_HELMET, ChatColor.BLUE + "Fantasmic!");
    private ItemStack iroe = ItemUtil.create(Material.MONSTER_EGG, ChatColor.GREEN + "IROE");
    private ItemStack wishes = ItemUtil.create(Material.BLAZE_ROD, ChatColor.AQUA + "Wishes!");
    private ItemStack tfant = ItemUtil.create(Material.DIAMOND_HELMET, ChatColor.AQUA + "Taste of Fantasmic!");
    private ItemStack msep = ItemUtil.create(Material.GLOWSTONE_DUST, ChatColor.YELLOW +
            "Main Street Electrical Parade");
    private ItemStack fntm = ItemUtil.create(Material.RAW_FISH, 1, (byte) 2, ChatColor.GOLD +
            "Finding Nemo the Musical", new ArrayList<>());
    private ItemStack times = ItemUtil.create(Material.BOOK, ChatColor.GREEN + "Show Timetable");
    //Rides and Attractions
    private ItemStack wait = ItemUtil.create(Material.WATCH, ChatColor.GREEN + "Wait Times");
    private ItemStack ride = ItemUtil.create(Material.MINECART, ChatColor.GREEN + "Rides");
    private ItemStack mng = ItemUtil.create(Material.INK_SACK, ChatColor.GREEN + "Meet & Greets");
    private ItemStack attraction = ItemUtil.create(Material.GLOWSTONE_DUST, ChatColor.GREEN + "Attractions");
    //Hotels and Resorts
    private ItemStack viewMyRooms = ItemUtil.create(Material.BED, 1, ChatColor.GREEN + "Visit Your Hotel Room",
            Arrays.asList(ChatColor.GREEN + "View the room that you booked!"));
    private ItemStack viewHotels = ItemUtil.create(Material.EMERALD, 1, ChatColor.GREEN + "Rent a Hotel Room",
            Collections.singletonList(ChatColor.GREEN + "Book a hotel room!"));
    //Show Timetable
    private ItemStack m = new ItemStack(Material.BANNER);
    private ItemStack t = new ItemStack(Material.BANNER);
    private ItemStack w = new ItemStack(Material.BANNER);
    private ItemStack th = new ItemStack(Material.BANNER);
    private ItemStack f = new ItemStack(Material.BANNER);
    private ItemStack s = new ItemStack(Material.BANNER);
    private ItemStack su = new ItemStack(Material.BANNER);
    //Storage
    private ItemStack loadingPack = ItemUtil.create(Material.STAINED_CLAY, 1, (byte) 3, ChatColor.DARK_AQUA +
            "Loading Backpack...", new ArrayList<>());
    private ItemStack loadingLocker = ItemUtil.create(Material.STAINED_CLAY, 1, (byte) 3, ChatColor.DARK_AQUA +
            "Loading Locker...", new ArrayList<>());


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
        bm.setBaseColor(DyeColor.BLACK);
        bt.setBaseColor(DyeColor.BLACK);
        bw.setBaseColor(DyeColor.BLACK);
        bth.setBaseColor(DyeColor.BLACK);
        bf.setBaseColor(DyeColor.BLACK);
        bs.setBaseColor(DyeColor.BLACK);
        bsu.setBaseColor(DyeColor.BLACK);
        List<Pattern> m = new ArrayList<>();
        List<Pattern> t = new ArrayList<>();
        List<Pattern> w = new ArrayList<>();
        List<Pattern> f = new ArrayList<>();
        List<Pattern> s = new ArrayList<>();
        DyeColor lb = DyeColor.LIGHT_BLUE;
        DyeColor bl = DyeColor.BLACK;
        m.add(new Pattern(lb, PatternType.TRIANGLE_TOP));
        m.add(new Pattern(lb, PatternType.TRIANGLES_TOP));
        m.add(new Pattern(lb, PatternType.STRIPE_LEFT));
        m.add(new Pattern(lb, PatternType.STRIPE_RIGHT));
        t.add(new Pattern(lb, PatternType.STRIPE_CENTER));
        t.add(new Pattern(lb, PatternType.STRIPE_TOP));
        w.add(new Pattern(lb, PatternType.TRIANGLE_BOTTOM));
        w.add(new Pattern(bl, PatternType.TRIANGLES_BOTTOM));
        w.add(new Pattern(lb, PatternType.STRIPE_LEFT));
        w.add(new Pattern(lb, PatternType.STRIPE_RIGHT));
        f.add(new Pattern(lb, PatternType.STRIPE_MIDDLE));
        f.add(new Pattern(bl, PatternType.STRIPE_RIGHT));
        f.add(new Pattern(lb, PatternType.STRIPE_LEFT));
        f.add(new Pattern(lb, PatternType.STRIPE_TOP));
        s.add(new Pattern(lb, PatternType.TRIANGLE_TOP));
        s.add(new Pattern(lb, PatternType.TRIANGLE_BOTTOM));
        s.add(new Pattern(lb, PatternType.SQUARE_TOP_RIGHT));
        s.add(new Pattern(lb, PatternType.SQUARE_BOTTOM_LEFT));
        s.add(new Pattern(bl, PatternType.RHOMBUS_MIDDLE));
        s.add(new Pattern(lb, PatternType.STRIPE_DOWNRIGHT));
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
        s1Band = ItemUtil.create(ParkManager.bandUtil.getBandMaterial(BandColor.SPECIAL1),
                ChatColor.BLUE + "Holiday Band", new ArrayList<>());
        s2Band = ItemUtil.create(ParkManager.bandUtil.getBandMaterial(BandColor.SPECIAL2),
                ChatColor.RED + "Big Hero 6", new ArrayList<>());
        s3Band = ItemUtil.create(ParkManager.bandUtil.getBandMaterial(BandColor.SPECIAL3),
                ChatColor.GRAY + "Haunted Mansion", new ArrayList<>());
        s4Band = ItemUtil.create(ParkManager.bandUtil.getBandMaterial(BandColor.SPECIAL4),
                ChatColor.DARK_AQUA + "Sorcerer Mickey", new ArrayList<>());
        s5Band = ItemUtil.create(ParkManager.bandUtil.getBandMaterial(BandColor.SPECIAL5),
                ChatColor.LIGHT_PURPLE + "Princess", new ArrayList<>());
    }

    public void openInventory(CPlayer player, InventoryType inv) {
        openInventory(player.getBukkitPlayer(), inv);
    }

    public void openInventory(Player player, InventoryType inv) {
        try {
            PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
            Rank rank = Core.getPlayerManager().getPlayer(player.getUniqueId()).getRank();
            switch (inv) {
                case MAINMENU: {
                    Inventory main = Bukkit.createInventory(player, 27, ChatColor.BLUE + player.getName() + "'s MagicBand");
                    ItemStack playerInfo = HeadUtil.getPlayerHead(Core.getPlayerManager().getPlayer(player.getUniqueId())
                            .getTextureHash(), ChatColor.GREEN + "My Profile");
                    ItemMeta im = playerInfo.getItemMeta();
                    im.setLore(Collections.singletonList(ChatColor.GRAY + "Loading..."));
                    playerInfo.setItemMeta(im);
                    ItemStack ptime;
                    if (rank.getRankId() >= Rank.NOBLE.getRankId()) {
                        ptime = ItemUtil.create(Material.WATCH, ChatColor.GREEN + "Player Time",
                                Arrays.asList(ChatColor.GREEN + "Change your time in", ChatColor.GREEN +
                                        "your current Park"));
                    } else {
                        ptime = ItemUtil.create(Material.WATCH, ChatColor.GREEN + "Player Time",
                                Arrays.asList(ChatColor.RED + "You must be a " + Rank.NOBLE.getNameWithBrackets(),
                                        ChatColor.RED + "to use this feature!"));
                    }
                    main.setItem(2, hnr);
                    main.setItem(4, playerInfo);
                    main.setItem(6, ptime);
                    main.setItem(10, food);
                    main.setItem(11, sne);
                    main.setItem(12, rna);
                    main.setItem(13, parks);
                    main.setItem(14, shop);
                    main.setItem(15, ward);
                    if (ParkManager.visibilityUtil.isInHideAll(player.getUniqueId())) {
                        main.setItem(16, toggleon);
                    } else {
                        main.setItem(16, toggleoff);
                    }
                    player.openInventory(main);
                    ParkManager.bandUtil.loadPlayerData(player);
                    return;
                }
                case PARK: {
                    Inventory park = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Park Menu");
                    park.setItem(2, arcade);
                    park.setItem(4, ttc);
                    park.setItem(6, creative);
                    park.setItem(10, mk);
                    park.setItem(11, epcot);
                    park.setItem(12, dhs);
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
                    foodMenu.setItem(22, BandUtil.getBackItem());
                    List<FoodLocation> foodLocations = ParkManager.foodLocations;
                    if (foodLocations.isEmpty()) {
                        foodMenu.setItem(13, ItemUtil.create(Material.STAINED_CLAY, 1, (byte) 14, ChatColor.RED +
                                "Uh oh!", Arrays.asList(ChatColor.RED + "Sorry, but there are no",
                                ChatColor.RED + "food locations on this server!")));
                        player.openInventory(foodMenu);
                        return;
                    }
                    // If odd amount of items
                    int place = 13;
                    if (foodLocations.size() % 2 == 1) {
                        int amount = 1;
                        for (FoodLocation loc : foodLocations) {
                            if (place > 16) {
                                break;
                            }
                            @SuppressWarnings("deprecation")
                            ItemStack f = ItemUtil.create(Material.getMaterial(loc.getType()), 1, loc.getData(),
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
                        player.openInventory(foodMenu);
                        // If even amount of items
                    } else {
                        place = 14;
                        int amount = 2;
                        for (FoodLocation loc : foodLocations) {
                            if (place > 16) {
                                break;
                            }
                            @SuppressWarnings("deprecation")
                            ItemStack f = ItemUtil.create(Material.getMaterial(loc.getType()), 1, loc.getData(),
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
                        player.openInventory(foodMenu);
                    }
                    return;
                }
                case MYPROFILE: {
                    Inventory pmenu = Bukkit.createInventory(player, 27, ChatColor.BLUE + "My Profile");
                    pmenu.setItem(10, web);
                    pmenu.setItem(11, dvc);
                    pmenu.setItem(12, packs);
                    pmenu.setItem(13, ach);
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
                    shows.setItem(17, fntm);
                    shows.setItem(22, BandUtil.getBackItem());
                    player.openInventory(shows);
                    return;
                }
                case CUSTOMIZE: {
                    Inventory custom = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Customize Menu");
                    ItemStack band;
                    if (data.getSpecial()) {
                        band = ItemUtil.create(ParkManager.bandUtil.getBandMaterial(data.getBandColor()));
                        ItemMeta bm = band.getItemMeta();
                        bm.setDisplayName(ChatColor.GREEN + "Change MagicBand Color");
                        band.setItemMeta(bm);
                    } else {
                        band = ItemUtil.create(Material.FIREWORK_CHARGE);
                        FireworkEffectMeta bm = (FireworkEffectMeta) band.getItemMeta();
                        bm.setEffect(FireworkEffect.builder().withColor(ParkManager.bandUtil
                                .getBandColor(data.getBandColor())).build());
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
                    if (rank.equals(Rank.SETTLER)) {
                        special.setItem(13, ItemUtil.create(Material.REDSTONE_BLOCK, ChatColor.RED + "DVC Members only!"
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
                    Inventory rna = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Rides and Meet & Greets");
                    rna.setItem(4, wait);
                    rna.setItem(11, ride);
                    rna.setItem(13, mng);
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
                    List<HotelRoom> rooms = ParkManager.hotelManager.getHotelRooms().stream().filter(room -> room.isOccupied() && room.getCurrentOccupant().equals(player.getUniqueId())).collect(Collectors.toList());
                    int roomItemPlacement = 13 - ((rooms.size() - 1) / 2);
                    for (HotelRoom room : rooms) {
                        ItemStack roomItem = ItemUtil.create(Material.BED, 1);
                        ItemMeta rim = roomItem.getItemMeta();
                        rim.setDisplayName(ChatColor.GREEN + room.getName());
                        if (room.getCheckoutTime() <= (System.currentTimeMillis() / 1000)) {
                            ParkManager.hotelManager.checkout(room, true);
                            return;
                        }
                        String times = DateUtil.formatDateDiff(room.getCheckoutTime() * 1000);
                        List<String> himl = Arrays.asList(ChatColor.DARK_GREEN + "You have " + times, ChatColor.DARK_GREEN
                                + "left for this reservation.", ChatColor.YELLOW + "Room Type: " + (room.isSuite() ?
                                ChatColor.GOLD + "Suite" : ChatColor.YELLOW + "Standard"));
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
                    ParkManager.hotelManager.getHotelRooms().stream().filter(room -> !availableHotels
                            .contains(room.getHotelName())).forEach(room -> availableHotels.add(room.getHotelName()));
                    int hotelItemPlacement = 10;
                    for (String hotel : availableHotels) {
                        ItemStack hotelItem = ItemUtil.create(Material.BED, 1);
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
                        flash = ItemUtil.create(Material.WOOL, 1, (byte) 5, ChatColor.GREEN + "Flash Effects", enab);
                    } else {
                        flash = ItemUtil.create(Material.WOOL, 1, (byte) 14, ChatColor.RED + "Flash Effects", disab);
                    }
                    if (data.getVisibility()) {
                        visibility = ItemUtil.create(Material.WOOL, 1, (byte) 5, ChatColor.GREEN + "Player Visibility", enab);
                    } else {
                        visibility = ItemUtil.create(Material.WOOL, 1, (byte) 14, ChatColor.RED + "Player Visibility", disab);
                    }
                    if (data.getLoop()) {
                        loop = ItemUtil.create(Material.WOOL, 1, (byte) 5, ChatColor.GREEN + "Park Loop Music", enab);
                    } else {
                        loop = ItemUtil.create(Material.WOOL, 1, (byte) 14, ChatColor.RED + "Park Loop Music", disab);
                    }
                    if (data.getHotel()) {
                        hotel = ItemUtil.create(Material.WOOL, 1, (byte) 5, ChatColor.GREEN + "Friends Access Hotel Room", enab);
                    } else {
                        hotel = ItemUtil.create(Material.WOOL, 1, (byte) 14, ChatColor.RED + "Friends Access Hotel Room", disab);
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
                    ItemStack pack = ItemUtil.create(Material.CHEST, ChatColor.GREEN + "Upgrade Backpack",
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
                    ItemStack locker = ItemUtil.create(Material.CHEST, ChatColor.GREEN + "Upgrade Locker",
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
                        ParkManager.storageManager.setLoadingPack(player);
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
                        ParkManager.storageManager.setLoadingLocker(player);
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
                    List<ScheduledShow> shows = ParkManager.scheduleManager.getShows();
                    List<String> times = new ArrayList<>();
                    shows.stream().filter(show -> !times.contains(show.getTime())).forEach(show -> times.add(show.getTime()));
                    HashMap<String, Integer> timeMap = new HashMap<>();
                    int i = 9;
                    for (String st : times) {
                        if (i >= 54) {
                            break;
                        }
                        s.setItem(i, ItemUtil.create(Material.WATCH, ChatColor.GREEN + st));
                        timeMap.put(st, i / 9);
                        i += 9;
                    }
                    for (ScheduledShow show : shows) {
                        ShowType type = show.getType();
                        int place = getShowPos(show.getDay(), show.getTime(), timeMap);
                        if (type.getType().equals(Material.BANNER)) {
                            ItemStack banner = new ItemStack(Material.BANNER);
                            BannerMeta bm = (BannerMeta) banner.getItemMeta();
                            bm.setBaseColor(DyeColor.RED);
                            bm.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_SMALL));
                            bm.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_SMALL));
                            bm.addPattern(new Pattern(DyeColor.BLUE, PatternType.SQUARE_TOP_RIGHT));
                            bm.addPattern(new Pattern(DyeColor.BLUE, PatternType.SQUARE_TOP_RIGHT));
                            bm.addPattern(new Pattern(DyeColor.BLUE, PatternType.SQUARE_TOP_RIGHT));
                            bm.setDisplayName(type.getName());
                            banner.setItemMeta(bm);
                            s.setItem(place, banner);
                            continue;
                        }
                        s.setItem(place, ItemUtil.create(type.getType(), 1, type.getData(), type.getName(),
                                new ArrayList<>()));
                    }
                    s.setItem(49, BandUtil.getBackItem());
                    player.openInventory(s);
                    Core.getPlayerManager().getPlayer(player.getUniqueId()).giveAchievement(11);
                    return;
                }
                case PLAYERTIME: {
                    if (rank.getRankId() < Rank.NOBLE.getRankId()) {
                        player.closeInventory();
                        player.sendMessage(ChatColor.RED + "You must be a " + Rank.NOBLE.getNameWithBrackets() +
                                ChatColor.RED + " or above to use this!");
                        return;
                    }
                    Inventory playerTime = Bukkit.createInventory(player, 27, ChatColor.GREEN + "Player Time");
                    long time = player.getPlayerTime() % 24000;
                    List<String> current = Arrays.asList(ChatColor.YELLOW + "Currently Selected!");
                    List<String> not = Arrays.asList(ChatColor.GRAY + "Click to Select!");
                    playerTime.setItem(9, ItemUtil.create(Material.STAINED_GLASS_PANE, ChatColor.GREEN + "Reset",
                            Arrays.asList(ChatColor.GREEN + "Match Park Time")));
                    playerTime.setItem(10, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "6AM", time == 0 ? current : not));
                    playerTime.setItem(11, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "9AM", time == 3000 ? current : not));
                    playerTime.setItem(12, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "12PM", time == 6000 ? current : not));
                    playerTime.setItem(13, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "3PM", time == 9000 ? current : not));
                    playerTime.setItem(14, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "6PM", time == 12000 ? current : not));
                    playerTime.setItem(15, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "9PM", time == 15000 ? current : not));
                    playerTime.setItem(16, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "12AM", time == 18000 ? current : not));
                    playerTime.setItem(17, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "3AM", time == 21000 ? current : not));
                    playerTime.setItem(22, BandUtil.getBackItem());
                    player.openInventory(playerTime);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getNum(int i) {
        if (i == 1 || i == 21) {
            return i + "st";
        }
        if (i == 2 || i == 22) {
            return i + "nd";
        }
        if (i == 3 || i == 23) {
            return i + "rd";
        }
        return i + "th";
    }

    private int getShowPos(ShowDay day, String time, HashMap<String, Integer> timeMap) {
        int i = 10;
        i += day.ordinal();
        i += (9 * (timeMap.get(time) - 1));
        return i;
    }

    public void openWardrobeManagerPage(Player player, int page) {
        PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
        Inventory inv = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Wardrobe Manager Page " + page);
        List<Outfit> first = ParkManager.wardrobeManager.getOutfits();
        List<Outfit> outfits = first.subList((page - 1) * 6, (page * 6 > first.size() ? first.size() : page * 6));
        boolean lpage = page > 1;
        boolean npage = (page * 6) < first.size();
        int i = 0;
        List<Integer> purchs = data.getPurchases();
        PlayerData.Clothing c = data.getClothing();
        for (Outfit o : outfits) {
            boolean owns = purchs.contains(o.getId());
            ItemStack h = o.getHead().clone();
            ItemStack s = o.getShirt().clone();
            ItemStack p = o.getPants().clone();
            ItemStack b = o.getBoots().clone();
            if (!owns) {
                ItemMeta hm = h.getItemMeta();
                hm.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH +
                        ChatColor.stripColor(hm.getDisplayName()));
                h.setItemMeta(hm);
                ItemMeta sm = s.getItemMeta();
                sm.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH +
                        ChatColor.stripColor(sm.getDisplayName()));
                s.setItemMeta(sm);
                ItemMeta pm = p.getItemMeta();
                pm.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH +
                        ChatColor.stripColor(pm.getDisplayName()));
                p.setItemMeta(pm);
                ItemMeta bm = b.getItemMeta();
                bm.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH +
                        ChatColor.stripColor(bm.getDisplayName()));
                b.setItemMeta(bm);
            }
            try {
                if (c.getHead().equals(h)) {
                    h.addUnsafeEnchantment(Enchantment.LUCK, 1);
                    inv.setItem(10 + i, h);
                } else {
                    inv.setItem(10 + i, h);
                }
            } catch (Exception ignored) {
                inv.setItem(10 + i, h);
            }
            try {
                if (c.getShirt().equals(s)) {
                    s.addUnsafeEnchantment(Enchantment.LUCK, 1);
                    inv.setItem(19 + i, s);
                } else {
                    inv.setItem(19 + i, s);
                }
            } catch (Exception ignored) {
                inv.setItem(19 + i, s);
            }
            try {
                if (c.getPants().equals(p)) {
                    p.addUnsafeEnchantment(Enchantment.LUCK, 1);
                    inv.setItem(28 + i, p);
                } else {
                    inv.setItem(28 + i, p);
                }
            } catch (Exception ignored) {
                inv.setItem(28 + i, p);
            }
            try {
                if (c.getBoots().equals(b)) {
                    b.addUnsafeEnchantment(Enchantment.LUCK, 1);
                    inv.setItem(37 + i, b);
                } else {
                    inv.setItem(37 + i, b);
                }
            } catch (Exception ignored) {
                inv.setItem(37 + i, b);
            }
            i++;
        }
        if (lpage) {
            inv.setItem(48, lastPage);
        }
        if (npage) {
            inv.setItem(50, nextPage);
        }
        inv.setItem(16, ItemUtil.create(Material.GLASS, ChatColor.GREEN + "Reset Head"));
        inv.setItem(25, ItemUtil.create(Material.GLASS, ChatColor.GREEN + "Reset Shirt"));
        inv.setItem(34, ItemUtil.create(Material.GLASS, ChatColor.GREEN + "Reset Pants"));
        inv.setItem(43, ItemUtil.create(Material.GLASS, ChatColor.GREEN + "Reset Boots"));
        inv.setItem(49, BandUtil.getBackItem());
        player.openInventory(inv);
    }

    public void openHotelRoomListPage(final Player player, String hotelName) {
        Inventory viewAvailableHotelRooms = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Rooms in " + hotelName);
        List<HotelRoom> availableHotelRooms = ParkManager.hotelManager.getHotelRooms().stream().filter(room -> room.getHotelName().equalsIgnoreCase(hotelName) && !room.isOccupied()).collect(Collectors.toList());
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
            ItemStack item = ItemUtil.create(Material.BED, 1);
            ItemMeta him = item.getItemMeta();
            him.setDisplayName(ChatColor.GREEN + room.getName());
            long time = room.getStayLength() / 3600;
            List<String> himl = Arrays.asList(ChatColor.GOLD + "Cost: $" + Integer.toString(room.getCost()),
                    ChatColor.GREEN + "Click to rent this room for " + time + " hours.", ChatColor.YELLOW +
                            "Room Type: " + (room.isSuite() ? ChatColor.GOLD + "Suite" : ChatColor.YELLOW + "Standard"));
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
        ItemStack item = ItemUtil.create(Material.BED, 1);
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
            ParkManager.hotelManager.checkout(room, true);
            return;
        }
        String time = DateUtil.formatDateDiff(room.getCheckoutTime() * 1000);
        List<String> himl = Arrays.asList(ChatColor.RED + "Click to check out before the ", "" +
                ChatColor.RED + (room.getStayLength() / 3600) + "-hour period is over.", ChatColor.DARK_GREEN
                + "You have " + time, ChatColor.DARK_GREEN + "left for this reservation.");
        ItemStack item = ItemUtil.create(Material.BOOK, ChatColor.RED + "Check out of " + room.getName(), himl);
        viewAvailableHotelRooms.setItem(13, item);
        viewAvailableHotelRooms.setItem(22, BandUtil.getBackItem());
        player.openInventory(viewAvailableHotelRooms);
    }

    @SuppressWarnings("deprecation")
    public void openRideList(Player player) {
        List<Ride> rides = ParkManager.rides;
        Inventory rlist = Bukkit.createInventory(player, (rides.size() <= 7 ? 27 : (rides.size() <= 14 ? 36 :
                (rides.size() <= 21 ? 45 : 54))), ChatColor.BLUE + "Ride List");
        if (rides.isEmpty()) {
            ItemStack empty = ItemUtil.create(Material.STAINED_CLAY, 1, (byte) 14);
            ItemMeta itemMeta = empty.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RED + "Uh oh!");
            itemMeta.setLore(Arrays.asList(ChatColor.RED + "Sorry, but there", ChatColor.RED + "are no Rides",
                    ChatColor.RED + "on this server!"));
            empty.setItemMeta(itemMeta);
            rlist.setItem(13, empty);
            rlist.setItem(rlist.getSize() - 5, BandUtil.getBackItem());
            player.openInventory(rlist);
            return;
        }
        List<ItemStack> items = new ArrayList<>();
        for (Ride ride : rides) {
            if (!ride.hasItem()) {
                continue;
            }
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
            if (i > 43) {
                break;
            }
            rlist.setItem(i, item);
            if (i % 9 == 7) {
                i += 3;
            } else {
                i++;
            }
        }
        rlist.setItem(rlist.getSize() - 5, BandUtil.getBackItem());
        player.openInventory(rlist);
    }

    @SuppressWarnings("deprecation")
    public void openAttractionList(Player player) {
        List<Ride> attractions = ParkManager.attractions;
        Inventory alist = Bukkit.createInventory(player, (attractions.size() <= 7 ? 27 : (attractions.size() <= 14 ? 36 :
                (attractions.size() <= 21 ? 45 : 54))), ChatColor.BLUE + "Attraction List");
        if (attractions.isEmpty()) {
            ItemStack empty = ItemUtil.create(Material.STAINED_CLAY, 1, (byte) 14);
            ItemMeta itemMeta = empty.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RED + "Uh oh!");
            itemMeta.setLore(Arrays.asList(ChatColor.RED + "Sorry, but there", ChatColor.RED + "are no Attractions",
                    ChatColor.RED + "on this server!"));
            empty.setItemMeta(itemMeta);
            alist.setItem(13, empty);
            alist.setItem(alist.getSize() - 5, BandUtil.getBackItem());
            player.openInventory(alist);
            return;
        }
        List<ItemStack> items = new ArrayList<>();
        for (Ride attraction : attractions) {
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
            if (i > 43) {
                break;
            }
            alist.setItem(i, item);
            if (i % 9 == 7) {
                i += 3;
            } else {
                i++;
            }
        }
        alist.setItem(alist.getSize() - 5, BandUtil.getBackItem());
        player.openInventory(alist);
    }

    @SuppressWarnings("deprecation")
    public void openMeetAndGreetList(Player player) {
        List<Ride> meetandgreets = ParkManager.meetandgreets;
        Inventory mlist = Bukkit.createInventory(player, (meetandgreets.size() <= 7 ? 27 : (meetandgreets.size() <= 14 ? 36 :
                (meetandgreets.size() <= 21 ? 45 : 54))), ChatColor.BLUE + "Meet & Greet List");
        if (meetandgreets.isEmpty()) {
            ItemStack empty = ItemUtil.create(Material.STAINED_CLAY, 1, (byte) 14);
            ItemMeta itemMeta = empty.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RED + "Uh oh!");
            itemMeta.setLore(Arrays.asList(ChatColor.RED + "Sorry, but there", ChatColor.RED + "are no Meet & Greets",
                    ChatColor.RED + "on this server!"));
            empty.setItemMeta(itemMeta);
            mlist.setItem(13, empty);
            mlist.setItem(mlist.getSize() - 5, BandUtil.getBackItem());
            player.openInventory(mlist);
            return;
        }
        List<ItemStack> items = new ArrayList<>();
        for (Ride mng : meetandgreets) {
            ItemStack rideItem = new ItemStack(mng.getId(), 1, mng.getData());
            ItemMeta itemMeta = rideItem.getItemMeta();
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', mng.getDisplayName()));
            rideItem.setItemMeta(itemMeta);
            if (rideItem.getItemMeta() == null) {
                continue;
            }
            items.add(rideItem);
        }
        int i = 10;
        for (ItemStack item : items) {
            if (i > 43) {
                break;
            }
            mlist.setItem(i, item);
            if (i % 9 == 7) {
                i += 3;
            } else {
                i++;
            }
        }
        mlist.setItem(mlist.getSize() - 5, BandUtil.getBackItem());
        player.openInventory(mlist);
    }

    public void featureComingSoon(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 100, 0);
        player.closeInventory();
        player.sendMessage(ChatColor.RED + "This feature is coming soon!");
    }

    public void openWaitTimes(Player player) {
        Inventory inv = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Wait Times");
        List<ItemStack> items = new ArrayList<>();
        for (QueueRide ride : ParkManager.queueManager.getRides()) {
            List<String> lore = Arrays.asList(ChatColor.YELLOW + "Wait Time: " + ride.appxWaitTime(), ChatColor.YELLOW +
                    "Players in Queue: " + (ride.getQueueSize() <= 0 ? "None" : ride.getQueueSize()), ChatColor.YELLOW +
                    "Warp: " + ChatColor.GREEN + "/warp " + ride.getWarp());
            items.add(ItemUtil.create(Material.SIGN, ride.getName(), lore));
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
            ItemStack empty = ItemUtil.create(Material.STAINED_CLAY, 1, (byte) 14);
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

    public void openRideCounterPage(Player player, int page) {
        PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
        List<RideCount> rides = new ArrayList<>(data.getRideCounts().values());
        int size = rides.size();
        if (size < 46) {
            page = 1;
        } else if (size < (45 * (page - 1) + 1)) {
            page -= 1;
        }
        List<RideCount> list = rides.subList(page > 1 ? (45 * (page - 1)) : 0, (size - (45 * (page - 1))) > 45 ?
                (45 * page) : size);
        Inventory inv = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Ride Counter Page " + page);
        CPlayer user = Core.getPlayerManager().getPlayer(player.getUniqueId());
        int place = 0;
        for (RideCount ride : list) {
            if (place >= 45) {
                break;
            }
            inv.setItem(place, ItemUtil.create(Material.MINECART, ChatColor.GREEN + ride.getName(),
                    Arrays.asList(ChatColor.YELLOW + "Rides: " + ride.getCount(), ChatColor.YELLOW + "Park: " + ride.getServer())));
            place++;
        }
        if (page > 1) {
            inv.setItem(48, lastPage);
        }
        inv.setItem(49, BandUtil.getBackItem());
        int maxPage = 1;
        int n = size;
        while (true) {
            if (n - 45 > 0) {
                n -= 45;
                maxPage += 1;
            } else {
                break;
            }
        }
        if (size > 45 && page < maxPage) {
            inv.setItem(50, nextPage);
        }
        player.openInventory(inv);
    }

    @SuppressWarnings("deprecation")
    public void openFoodMenuPage(Player player, int page) {
        PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
        List<FoodLocation> foodLocations = ParkManager.foodLocations;
        int size = foodLocations.size();
        if (size < 8) {
            page = 1;
        } else if (size < (7 * (page - 1) + 1)) {
            page -= 1;
        }
        List<FoodLocation> list = foodLocations.subList(page > 1 ? (7 * (page - 1)) : 0, (size - (7 * (page - 1))) > 7 ?
                (7 * page) : size);
        Inventory inv = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Food Menu Page " + page);
        int place = 10;
        int i = 0;
        for (FoodLocation loc : list) {
            i++;
            if (place > 16) {
                break;
            }
            try {
                inv.setItem(place, ItemUtil.create(Material.getMaterial(loc.getType()), 1, loc.getData(),
                        ChatColor.translateAlternateColorCodes('&', loc.getName()),
                        Collections.singletonList(ChatColor.GREEN + "/warp " + loc.getWarp())));
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getLogger().severe("Error loading food item " + i);
            }
            place++;
        }
        if (page > 1) {
            inv.setItem(21, lastPage);
        }
        inv.setItem(22, BandUtil.getBackItem());
        int maxPage = 1;
        int n = size;
        while (true) {
            if (n - 7 > 0) {
                n -= 7;
                maxPage += 1;
            } else {
                break;
            }
        }
        if (size > 7 && page < maxPage) {
            inv.setItem(23, nextPage);
        }
        player.openInventory(inv);
    }

    public void openAchievementPage(Player player, int page) {
        List<CoreAchievement> achievements = Core.getAchievementManager().getAchievements();
        int size = achievements.size();
        if (size < 46) {
            page = 1;
        } else if (size < (45 * (page - 1) + 1)) {
            page -= 1;
        }
        List<CoreAchievement> list = achievements.subList(page > 1 ? (45 * (page - 1)) : 0, (size - (45 * (page - 1)))
                > 45 ? (45 * page) : size);
        Inventory inv = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Achievements Page " + page);
        CPlayer cplayer = Core.getPlayerManager().getPlayer(player.getUniqueId());
        int place = 0;
        for (CoreAchievement ach : list) {
            if (place >= 45) {
                break;
            }
            if (cplayer.hasAchievement(ach.getId())) {
                inv.setItem(place, ItemUtil.create(Material.STAINED_CLAY, 1, (byte) 5, ChatColor.GREEN +
                        ach.getDisplayName(), Arrays.asList(ChatColor.GRAY + "" + ChatColor.ITALIC + ach.getDescription())));
            } else {
                inv.setItem(place, ItemUtil.create(Material.STAINED_CLAY, 1, (byte) 4, ChatColor.RED +
                        ach.getDisplayName(), Arrays.asList(ChatColor.GRAY + "" + ChatColor.ITALIC + "?")));
            }
            place++;
        }
        if (page > 1) {
            inv.setItem(48, lastPage);
        }
        inv.setItem(49, BandUtil.getBackItem());
        int maxPage = 1;
        int n = size;
        while (true) {
            if (n - 45 > 0) {
                n -= 45;
                maxPage += 1;
            } else {
                break;
            }
        }
        if (size > 45 && page < maxPage) {
            inv.setItem(50, nextPage);
        }
        player.openInventory(inv);
    }
}