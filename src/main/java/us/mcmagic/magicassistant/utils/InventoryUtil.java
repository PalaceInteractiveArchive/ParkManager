package us.mcmagic.magicassistant.utils;

import net.minecraft.server.v1_8_R2.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.*;
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
    private ItemStack hnr = new ItemCreator(Material.BED, ChatColor.GREEN + "Hotels and Resorts",
            Arrays.asList(ChatColor.GREEN + "Visit and rent a room from", ChatColor.GREEN + "a Walt Disney World Resort!"));
    private ItemStack toggleon = new ItemCreator(Material.WOOL, 1, (byte) 14, ChatColor.AQUA + "Player Visibility " +
            ChatColor.GOLD + "➠ " + ChatColor.RED + "Hidden", Arrays.asList(""));
    private ItemStack toggleoff = new ItemCreator(Material.WOOL, 1, (byte) 5, ChatColor.AQUA + "Player Visibility " +
            ChatColor.GOLD + "➠ " + ChatColor.GREEN + "Visible", Arrays.asList(""));
    private ItemStack shop = new ItemCreator(Material.GOLD_BOOTS, ChatColor.GREEN + "Shop",
            Arrays.asList(ChatColor.RED + "Coming Soon"));
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
    private ItemStack seasonal = new ItemCreator(Material.RED_ROSE, 1, (byte) 2, ChatColor.GREEN + "Seasonal",
            Arrays.asList(ChatColor.YELLOW + "Where Seasonal Events", ChatColor.YELLOW + "are held for the server!"));
    //Park Menu Items
    private ItemStack mk = new ItemCreator(Material.DIAMOND_HOE, ChatColor.AQUA + "Magic Kingdom",
            Arrays.asList(ChatColor.GREEN + "/join MK"));
    private ItemStack epcot = new ItemCreator(Material.SNOW_BALL, ChatColor.AQUA + "Epcot",
            Arrays.asList(ChatColor.GREEN + "/join Epcot"));
    private ItemStack hws = new ItemCreator(Material.JUKEBOX, ChatColor.AQUA + "HWS",
            Arrays.asList(ChatColor.GREEN + "/join HWS"));
    private ItemStack ak = new ItemCreator(Material.SAPLING, 1, (byte) 5, ChatColor.AQUA + "Animal Kingdom",
            Arrays.asList(ChatColor.GREEN + "/join AK"));
    private ItemStack tl = new ItemCreator(Material.WATER_BUCKET, ChatColor.GREEN + "Typhoon Lagoon",
            Arrays.asList(ChatColor.GREEN + "/join Typhoon"));
    private ItemStack dcl = new ItemCreator(Material.BOAT, ChatColor.GREEN + "Disney Cruise Line",
            Arrays.asList(ChatColor.GREEN + "/join DCL"));
    //My Profile
    private ItemStack dvc = new ItemCreator(Material.DIAMOND, ChatColor.AQUA + "Make a Donation!");
    private ItemStack web = new ItemCreator(Material.REDSTONE, ChatColor.GREEN + "Website");
    private ItemStack locker = new ItemCreator(Material.ENDER_CHEST, ChatColor.GREEN + "Locker");
    private ItemStack ach = new ItemCreator(Material.EMERALD, ChatColor.GREEN + "Achievements");
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
    private ItemStack s1Band = new ItemCreator(MagicAssistant.bandUtil.getBandMaterial(BandColor.SPECIAL1),
            ChatColor.BLUE + "Holiday Band", new ArrayList<String>());
    private ItemStack s2Band = new ItemCreator(MagicAssistant.bandUtil.getBandMaterial(BandColor.SPECIAL2),
            ChatColor.RED + "Big Hero 6", new ArrayList<String>());
    private ItemStack s3Band = new ItemCreator(MagicAssistant.bandUtil.getBandMaterial(BandColor.SPECIAL3),
            ChatColor.GRAY + "Haunted Mansion", new ArrayList<String>());
    private ItemStack s4Band = new ItemCreator(MagicAssistant.bandUtil.getBandMaterial(BandColor.SPECIAL4),
            ChatColor.DARK_AQUA + "Sorcerer Mickey", new ArrayList<String>());
    private ItemStack s5Band = new ItemCreator(MagicAssistant.bandUtil.getBandMaterial(BandColor.SPECIAL5),
            ChatColor.LIGHT_PURPLE + "Princess", new ArrayList<String>());
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
    private ItemStack msep = new ItemCreator(Material.GLOWSTONE_DUST, ChatColor.YELLOW +
            "Main Street Electrical Parade");
    private ItemStack fofp = new ItemCreator(Material.INK_SACK, 1, (byte) 12, ChatColor.DARK_AQUA +
            "Festival of Fantasy Parade", Arrays.asList(""));
    private ItemStack party = new ItemCreator(Material.WOOL, 1, (byte) 5, ChatColor.GREEN +
            "Click to join the Party!", Arrays.asList(""));
    private ItemStack noparty = new ItemCreator(Material.WOOL, 1, (byte) 14, ChatColor.RED +
            "There is no Party right now!", Arrays.asList(""));
    //Rides and Attractions
    private ItemStack ride = new ItemCreator(Material.MINECART, ChatColor.GREEN + "Rides");
    private ItemStack attraction = new ItemCreator(Material.GLOWSTONE_DUST, ChatColor.GREEN + "Attractions");
    //Hotels and Resorts
    private ItemStack joinHotelsAndResorts = new ItemCreator(Material.GLOWSTONE_DUST, 1, ChatColor.GREEN +
            "Visit Hotels and Resorts", Arrays.asList(ChatColor.GREEN + "Teleport yourself to the hotels",
            ChatColor.GREEN + "and resorts world!"));
    private ItemStack viewMyRooms = new ItemCreator(Material.BOOK, 1, ChatColor.GREEN + "View your Hotel Rooms",
            Arrays.asList(ChatColor.GREEN + "View the rooms that you've", ChatColor.GREEN + "currently booked!"));
    private ItemStack viewHotels = new ItemCreator(Material.BED, 1, ChatColor.GREEN + "Rent a New Hotel Room",
            Collections.singletonList(ChatColor.GREEN + "Book a hotel room!"));

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
    }

    public void openInventory(final Player player, InventoryType inv) {
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        Rank rank = MCMagicCore.getUser(player.getUniqueId()).getRank();
        switch (inv) {
            case MAINMENU:
                final Inventory main = Bukkit.createInventory(player, 27, ChatColor.BLUE
                        + player.getName() + "'s MagicBand");
                ItemStack playerInfo = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                SkullMeta sm = (SkullMeta) playerInfo.getItemMeta();
                sm.setOwner(player.getName());
                sm.setDisplayName(ChatColor.GREEN + "My Profile");
                sm.setLore(Arrays.asList(ChatColor.GRAY + "Loading..."));
                playerInfo.setItemMeta(sm);
                net.minecraft.server.v1_8_R2.ItemStack i = CraftItemStack.asNMSCopy(playerInfo);
                NBTTagCompound tag = i.getTag();
                NBTTagCompound skull = tag.getCompound("SkullOwner");
                skull.setString("Name", player.getName());
                skull.setString("Id", player.getUniqueId().toString());
                //skull.set("Properties", prop);
                tag.set("SkullOwner", skull);
                i.setTag(tag);
                org.bukkit.inventory.ItemStack done = CraftItemStack.asBukkitCopy(i);
                ItemStack time = new ItemCreator(Material.WATCH);
                ItemMeta tm = time.getItemMeta();
                tm.setDisplayName(ChatColor.GREEN + "Current Time in EST");
                tm.setLore(Collections.singletonList(ChatColor.YELLOW + MagicAssistant.bandUtil.currentTime()));
                time.setItemMeta(tm);
                main.setItem(0, rna);
                main.setItem(9, sne);
                main.setItem(18, hnr);
                if (VisibleUtil.isInHideAll(player.getUniqueId())) {
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
                main.setItem(26, seasonal);
                player.openInventory(main);
                MagicAssistant.bandUtil.loadPlayerData(player, main);
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
            case MYPROFILE:
                Inventory pmenu = Bukkit.createInventory(player, 27, ChatColor.BLUE + "My Profile");
                pmenu.setItem(10, web);
                pmenu.setItem(11, dvc);
                pmenu.setItem(12, locker);
                pmenu.setItem(14, ach);
                pmenu.setItem(15, prefs);
                pmenu.setItem(16, mumble);
                pmenu.setItem(22, BandUtil.getBackItem());
                player.openInventory(pmenu);
                return;
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
            case RIDESANDATTRACTIONS:
                Inventory rna = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Rides and Attractions");
                rna.setItem(20, ride);
                rna.setItem(24, attraction);
                rna.setItem(49, BandUtil.getBackItem());
                player.openInventory(rna);
                return;
            case HOTELSANDRESORTS:
                Inventory hotelsAndResorts = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Hotels and Resorts");
                hotelsAndResorts.setItem(11, joinHotelsAndResorts);
                hotelsAndResorts.setItem(13, viewMyRooms);
                hotelsAndResorts.setItem(15, viewHotels);
                hotelsAndResorts.setItem(22, BandUtil.getBackItem());
                player.openInventory(hotelsAndResorts);
                return;
            case MYHOTELROOMS:
                Inventory viewMyHotelRooms = Bukkit.createInventory(player, 27, ChatColor.BLUE + "My Hotel Rooms");
                List<HotelRoom> rooms = new ArrayList<>();
                for (HotelRoom room : MagicAssistant.hotelRooms) {
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
                        HotelUtil.checkout(room, true);
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
            case HOTELS:
                Inventory viewAvailableHotels = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Hotels");
                List<String> availableHotels = new ArrayList<>();
                for (HotelRoom room : MagicAssistant.hotelRooms) {
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
            case PLAYERSETTINGS:
                Inventory settings = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Player Settings");
                List<String> enab = Arrays.asList(ChatColor.GREEN + "Enabled");
                List<String> disab = Arrays.asList(ChatColor.RED + "Disabled");
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
                if (data.getLoop()) {
                    loop = new ItemCreator(Material.WOOL, 1, (byte) 5, ChatColor.GREEN + "Park Entrance Loops", enab);
                } else {
                    loop = new ItemCreator(Material.WOOL, 1, (byte) 14, ChatColor.RED + "Park Entrance Loops", disab);
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
        }
    }

    public void openHotelRoomListPage(final Player player, String hotelName) {
        Inventory viewAvailableHotelRooms = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Rooms in " + hotelName);
        List<HotelRoom> availableHotelRooms = new ArrayList<>();
        for (HotelRoom room : MagicAssistant.hotelRooms) {
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
            List<String> himl = Arrays.asList(ChatColor.GOLD + "Cost: " + Integer.toString(room.getCost()) + " Coins",
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
        List<String> himl = Arrays.asList(ChatColor.GOLD + "Cost: " + Integer.toString(room.getCost()) + " Coins",
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
            HotelUtil.checkout(room, true);
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
        HashMap<Integer, List<PlayerData.Attraction>> al = MagicAssistant.attPages;
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
            itemMeta.setLore(Arrays.asList(ChatColor.RED + "Sorry, but there", ChatColor.RED + "are no attraction setup", ChatColor.RED + "on this server!"));
            empty.setItemMeta(itemMeta);
            alist.setItem(22, empty);
            alist.setItem(49, BandUtil.getBackItem());
            player.openInventory(alist);
            return;
        }
        List<PlayerData.Attraction> pageList = al.get(page);
        List<ItemStack> items = new ArrayList<>();
        for (PlayerData.Attraction attraction : pageList) {
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

    public void featureComingSoon(Player player) {
        player.playSound(player.getLocation(), Sound.ITEM_BREAK, 100, 0);
        player.closeInventory();
        player.sendMessage(ChatColor.RED + "This feature is coming soon!");
    }
}