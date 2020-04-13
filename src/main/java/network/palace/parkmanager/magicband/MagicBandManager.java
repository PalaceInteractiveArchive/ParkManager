package network.palace.parkmanager.magicband;

import com.google.common.collect.ImmutableMap;
import network.palace.core.Core;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.HeadUtil;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.attractions.Attraction;
import network.palace.parkmanager.food.FoodLocation;
import network.palace.parkmanager.handlers.*;
import network.palace.parkmanager.handlers.magicband.BandType;
import network.palace.parkmanager.handlers.magicband.MenuType;
import network.palace.parkmanager.handlers.shop.Shop;
import network.palace.parkmanager.handlers.storage.StorageData;
import network.palace.parkmanager.handlers.storage.StorageSize;
import network.palace.parkmanager.queues.Queue;
import network.palace.parkmanager.utils.VisibilityUtil;
import org.apache.commons.lang.WordUtils;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

@SuppressWarnings("DuplicatedCode")
public class MagicBandManager {

    public void openInventory(CPlayer player, BandInventory inventory) {
        switch (inventory) {
            case MAIN: {
                VisibilityUtil.Setting setting = ParkManager.getVisibilityUtil().getSetting(player);
                ChatColor color = setting.getColor();

                ItemStack profile = HeadUtil.getPlayerHead(player.getTextureValue(), ChatColor.AQUA + "My Profile");
                ItemMeta meta = profile.getItemMeta();
                meta.setLore(Arrays.asList("", ChatColor.GREEN + "Loading...", ""));
                profile.setItemMeta(meta);

                List<MenuButton> buttons = new ArrayList<>(Arrays.asList(
                        new MenuButton(4, profile, ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.PROFILE))),
                        new MenuButton(10, ItemUtil.create(Material.POTATO_ITEM, ChatColor.AQUA + "Find Food",
                                Arrays.asList(ChatColor.GREEN + "Visit a restaurant", ChatColor.GREEN + "to get some food!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.FOOD))),
                        new MenuButton(11, ItemUtil.create(Material.FIREWORK, ChatColor.AQUA + "Shows and Events",
                                Arrays.asList(ChatColor.GREEN + "Watch stage shows, nighttime", ChatColor.GREEN + "spectaculars, and much more!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.SHOWS))),
                        new MenuButton(12, ItemUtil.create(Material.MINECART, ChatColor.AQUA + "Attractions",
                                Arrays.asList(ChatColor.GREEN + "View all of our available", ChatColor.GREEN + "theme park attractions!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.ATTRACTION_MENU))),
                        new MenuButton(13, ItemUtil.create(Material.NETHER_STAR, ChatColor.AQUA + "Park Menu",
                                Arrays.asList(ChatColor.GREEN + "Travel to another one", ChatColor.GREEN + "of our theme parks!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.PARKS))),
                        new MenuButton(14, ItemUtil.create(Material.GOLD_BOOTS, ChatColor.AQUA + "Shop",
                                Arrays.asList(ChatColor.GREEN + "Purchase souveniers and", ChatColor.GREEN + "all kinds of collectibles!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.SHOP))),
                        new MenuButton(15, ItemUtil.create(Material.IRON_CHESTPLATE, ChatColor.AQUA + "Wardrobe Manager",
                                Arrays.asList(ChatColor.GREEN + "Change your outfit to make you", ChatColor.GREEN + "look like your favorite characters!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.WARDROBE))),
                        new MenuButton(16, ItemUtil.create(setting.getBlock(), 1, setting.getData(), ChatColor.AQUA + "Guest Visibility " +
                                        ChatColor.GOLD + "➠ " + setting.getColor() + setting.getText(),
                                Arrays.asList(ChatColor.YELLOW + "Right-Click to " + (setting.equals(VisibilityUtil.Setting.ALL_HIDDEN) ? "show" : "hide") + " all players",
                                        ChatColor.YELLOW + "Left-Click for more options")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.VISIBILITY), ClickType.RIGHT, p -> {
                                    if (ParkManager.getVisibilityUtil().toggleVisibility(player)) {
                                        openInventory(p, BandInventory.MAIN);
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 2);
                                    }
                                })),
                        new MenuButton(6, ItemUtil.create(Material.CHEST, ChatColor.AQUA + "Storage Upgrade",
                                Arrays.asList(ChatColor.GREEN + "Expand the space available", ChatColor.GREEN + "in your backpack and locker")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.STORAGE_UPGRADE)))
                ));

                if (!ParkManager.getResort().equals(Resort.USO)) {
                    ItemStack band = getMagicBandItem(player);
                    meta = band.getItemMeta();
                    meta.setDisplayName(ChatColor.AQUA + "Customize your MagicBand");
                    meta.setLore(Arrays.asList("", ChatColor.GREEN + "Choose from a variety of MagicBand",
                            ChatColor.GREEN + "designs and customize the color",
                            ChatColor.GREEN + "of the name for your MagicBand!"));
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    band.setItemMeta(meta);
                    buttons.add(new MenuButton(22, band, ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.CUSTOMIZE_BAND))));
                }

                Menu menu = new Menu(27, ChatColor.BLUE + "Your " + (ParkManager.getResort().equals(Resort.USO) ? "Power Pass" : "MagicBand"), player, buttons);
                if (player.getRank().getRankId() >= Rank.NOBLE.getRankId()) {
                    menu.setButton(new MenuButton(2, ItemUtil.create(Material.WATCH, ChatColor.AQUA + "Player Time",
                            Arrays.asList(ChatColor.GREEN + "Change the time of day you see", ChatColor.GREEN + "for the park you're currently in!")),
                            ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.PLAYER_TIME))));
                } else {
                    menu.setButton(new MenuButton(2, ItemUtil.create(Material.WATCH, ChatColor.AQUA + "Player Time",
                            Arrays.asList(ChatColor.GREEN + "Purchase " + Rank.NOBLE.getFormattedName() + ChatColor.GREEN + "at",
                                    ChatColor.YELLOW + "/store" + ChatColor.GREEN + "to use this!"))));
                }
                menu.open();
                Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
                    ItemStack updatedProfile = profile.clone();
                    ItemMeta menuMeta = updatedProfile.getItemMeta();
                    menuMeta.setLore(Arrays.asList(
                            ChatColor.GREEN + "Name: " + ChatColor.YELLOW + player.getName(),
                            ChatColor.GREEN + "Rank: " + player.getRank().getFormattedName(),
                            ChatColor.GREEN + "Balance: " + ChatColor.YELLOW + "$" + player.getBalance(),
                            ChatColor.GREEN + "Tokens: " + ChatColor.YELLOW + "✪ " + player.getTokens(),
                            ChatColor.GREEN + "FastPass: " + ChatColor.YELLOW + player.getRegistry().getEntry("fastPassCount")
                    ));
                    updatedProfile.setItemMeta(menuMeta);

                    menu.setButton(new MenuButton(4, updatedProfile, ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.PROFILE))));
                });
                break;
            }
            case FOOD: {
                ParkType currentPark = currentParkOrOpenParkMenu(player);
                if (currentPark == null) return;
                List<MenuButton> buttons = new ArrayList<>();
                int i = 0;
                int size = 18;
                for (FoodLocation food : ParkManager.getFoodManager().getFoodLocations(currentPark)) {
                    ItemStack item = food.getItem();
                    ItemMeta meta = item.getItemMeta();
                    meta.setLore(Arrays.asList("", ChatColor.YELLOW + "/warp " + food.getWarp()));
                    item.setItemMeta(meta);
                    if (i != 0 && i % 9 == 0) {
                        size += 9;
                    }
                    if (size > 54) {
                        size = 54;
                        break;
                    }
                    buttons.add(new MenuButton(i++, ItemUtil.unbreakable(item), ImmutableMap.of(ClickType.LEFT, p -> {
                        p.performCommand("warp " + food.getWarp());
                        p.closeInventory();
                    })));
                }
                if (buttons.isEmpty()) {
                    buttons.add(new MenuButton(4, ItemUtil.create(Material.REDSTONE_BLOCK, ChatColor.RED + "No Food Locations",
                            Arrays.asList(ChatColor.GRAY + "Sorry, it looks like there are", ChatColor.GRAY + "no food locations in this park!"))));
                }
                buttons.add(getBackButton(size - 5, BandInventory.MAIN));
                new Menu(size, ChatColor.BLUE + "Food Locations (" + currentPark.getId() + ")", player, buttons).open();
                break;
            }
            case SHOWS: {
                new Menu(27, ChatColor.BLUE + "Shows and Events", player, Arrays.asList(
                        new MenuButton(8, ItemUtil.create(Material.BOOK, ChatColor.AQUA + "Show Timetable"),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(player, BandInventory.TIMETABLE))),
                        new MenuButton(10, ItemUtil.create(Material.DIAMOND_SWORD, ChatColor.RED + "Symphony in the Stars"),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp sits");
                                    p.closeInventory();
                                })),
                        new MenuButton(11, ItemUtil.create(Material.DIAMOND_HELMET, ChatColor.BLUE + "Fantasmic!"),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp fant");
                                    p.closeInventory();
                                })),
                        new MenuButton(12, ItemUtil.create(Material.BLAZE_ROD, ChatColor.AQUA + "Wishes!"),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp castle");
                                    p.closeInventory();
                                })),
                        new MenuButton(13, ItemUtil.create(Material.EGG, ChatColor.GREEN + "Illuminations: Reflections of Earth"),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp iroe");
                                    p.closeInventory();
                                })),
                        new MenuButton(14, ItemUtil.create(Material.INK_SACK, ChatColor.DARK_AQUA + "Festival of Fantasy", 12),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp fof");
                                    p.closeInventory();
                                })),
                        new MenuButton(15, ItemUtil.create(Material.GLOWSTONE_DUST, ChatColor.YELLOW + "Main Street Electrical Parade"),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp msep");
                                    p.closeInventory();
                                })),
                        new MenuButton(16, ItemUtil.create(Material.RAW_FISH, ChatColor.GOLD + "Finding Nemo: The Musical", 2),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp fntm");
                                    p.closeInventory();
                                })),
                        getBackButton(22, BandInventory.MAIN))).open();
                break;
            }
            case ATTRACTION_MENU:
                new Menu(27, ChatColor.BLUE + "Attractions Menu", player, Arrays.asList(
                        new MenuButton(11, ItemUtil.create(Material.MINECART, ChatColor.AQUA + "Attractions List",
                                Arrays.asList(ChatColor.GREEN + "View all of our available", ChatColor.GREEN + "theme park attractions")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(player, BandInventory.ATTRACTION_LIST))),
                        new MenuButton(15, ItemUtil.create(Material.WATCH, ChatColor.AQUA + "Wait Times",
                                Arrays.asList(ChatColor.GREEN + "View the wait times for all", ChatColor.GREEN + "queues on this server")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(player, BandInventory.WAIT_TIMES))),
                        getBackButton(22, BandInventory.MAIN))).open();
                break;
            case STORAGE_UPGRADE: {
                List<MenuButton> buttons = new ArrayList<>();
                StorageData data = (StorageData) player.getRegistry().getEntry("storageData");
                if (data.getBackpackSize().equals(StorageSize.SMALL)) {
                    buttons.add(
                            new MenuButton(11, ItemUtil.create(Material.CHEST, ChatColor.GREEN + "Expand Backpack",
                                    Arrays.asList(
                                            ChatColor.YELLOW + "3 rows ➠ 6 rows", ChatColor.GRAY + "Purchase a backpack",
                                            ChatColor.GRAY + "upgrade for " + ChatColor.GREEN + "$250"
                                    )),
                                    ImmutableMap.of(ClickType.LEFT, p -> ParkManager.getStorageManager().buyUpgrade(p, Material.CHEST)))
                    );
                } else {
                    buttons.add(
                            new MenuButton(11, ItemUtil.create(Material.CHEST, ChatColor.GREEN + "Expand Backpack",
                                    Arrays.asList(
                                            ChatColor.RED + "You already own this!",
                                            ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "3 rows ➠ 6 rows",
                                            ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "Purchase a backpack",
                                            ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "upgrade for $250"
                                    )))
                    );
                }
                if (data.getLockerSize().equals(StorageSize.SMALL)) {
                    buttons.add(
                            new MenuButton(15, ItemUtil.create(Material.ENDER_CHEST, ChatColor.GREEN + "Expand Locker",
                                    Arrays.asList(
                                            ChatColor.YELLOW + "3 rows ➠ 6 rows", ChatColor.GRAY + "Purchase a locker",
                                            ChatColor.GRAY + "upgrade for " + ChatColor.GREEN + "$250"
                                    )),
                                    ImmutableMap.of(ClickType.LEFT, p -> ParkManager.getStorageManager().buyUpgrade(p, Material.ENDER_CHEST)))
                    );
                } else {
                    buttons.add(
                            new MenuButton(15, ItemUtil.create(Material.ENDER_CHEST, ChatColor.GREEN + "Expand Locker",
                                    Arrays.asList(
                                            ChatColor.RED + "You already own this!",
                                            ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "3 rows ➠ 6 rows",
                                            ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "Purchase a locker",
                                            ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "upgrade for $250"
                                    )))
                    );
                }
                buttons.add(getBackButton(22, BandInventory.MAIN));
                new Menu(27, ChatColor.BLUE + "Storage Upgrade", player, buttons).open();
                break;
            }
            case ATTRACTION_LIST: {
                ParkType currentPark = currentParkOrOpenParkMenu(player);
                if (currentPark == null) return;
                List<MenuButton> buttons = new ArrayList<>();
                int i = 0;
                int size = 18;
                for (Attraction attraction : ParkManager.getAttractionManager().getAttractions(currentPark)) {
                    ItemStack item = attraction.getItem();
                    ItemMeta meta = item.getItemMeta();
                    List<String> lore = new ArrayList<>(Collections.singletonList(""));
                    String[] descriptionList = WordUtils.wrap(attraction.getDescription(), 30).split("\n");
                    for (String s : descriptionList) {
                        lore.add(ChatColor.DARK_AQUA + s);
                    }
                    lore.addAll(Arrays.asList("", ChatColor.GREEN + "Warp: " + ChatColor.YELLOW + "/warp " + attraction.getWarp(),
                            "", ChatColor.GREEN + "Status: " + (attraction.isOpen() ? "OPEN" : ChatColor.RED + "CLOSED"),
                            "", ChatColor.GREEN + "Categories:"));
                    if (attraction.getLinkedQueue() != null) {
                        Queue queue = ParkManager.getQueueManager().getQueue(attraction.getLinkedQueue(), currentPark);
                        if (queue != null)
                            lore.addAll(5 + descriptionList.length, Arrays.asList("", ChatColor.GREEN + "Wait: " + ChatColor.YELLOW + queue.getWaitFor(null)));
                    }
                    for (AttractionCategory category : attraction.getCategories()) {
                        lore.add(ChatColor.AQUA + "- " + ChatColor.YELLOW + category.getFormattedName());
                    }
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    if (i != 0 && i % 9 == 0) {
                        size += 9;
                    }
                    if (size > 54) {
                        size = 54;
                        break;
                    }
                    buttons.add(new MenuButton(i++, ItemUtil.unbreakable(item), ImmutableMap.of(ClickType.LEFT, p -> {
                        p.performCommand("warp " + attraction.getWarp());
                        p.closeInventory();
                    })));
                }
                if (buttons.isEmpty()) {
                    buttons.add(new MenuButton(4, ItemUtil.create(Material.REDSTONE_BLOCK, ChatColor.RED + "No Attractions",
                            Arrays.asList(ChatColor.GRAY + "Sorry, it looks like there are", ChatColor.GRAY + "no attractions on this server!"))));
                }
                buttons.add(getBackButton(size - 5, BandInventory.ATTRACTION_MENU));
                new Menu(size, ChatColor.BLUE + "Attractions List (" + currentPark.getId() + ")", player, buttons).open();
                break;
            }
            case WAIT_TIMES: {
                ParkType currentPark = currentParkOrOpenParkMenu(player);
                if (currentPark == null) return;
                List<MenuButton> buttons = new ArrayList<>();
                int i = 0;
                int size = 18;
                for (Queue queue : ParkManager.getQueueManager().getQueues(currentPark)) {
                    ItemStack item = ItemUtil.create(Material.SIGN);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(queue.getName());
                    List<String> lore = new ArrayList<>(Arrays.asList("", ChatColor.GREEN + "Wait: " + ChatColor.YELLOW + queue.getWaitFor(null),
                            "", ChatColor.GREEN + "Warp: " + ChatColor.YELLOW + "/warp " + queue.getWarp(),
                            "", ChatColor.GREEN + "Status: " + (queue.isOpen() ? "OPEN" : ChatColor.RED + "CLOSED")));
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    if (i != 0 && i % 9 == 0) {
                        size += 9;
                    }
                    if (size > 54) {
                        size = 54;
                        break;
                    }
                    buttons.add(new MenuButton(i++, item, ImmutableMap.of(ClickType.LEFT, p -> {
                        p.performCommand("warp " + queue.getWarp());
                        p.closeInventory();
                    })));
                }
                if (buttons.isEmpty()) {
                    buttons.add(new MenuButton(4, ItemUtil.create(Material.REDSTONE_BLOCK, ChatColor.RED + "No Queues",
                            Arrays.asList(ChatColor.GRAY + "Sorry, it looks like there are", ChatColor.GRAY + "no queues on this server!"))));
                }
                buttons.add(getBackButton(size - 5, BandInventory.ATTRACTION_MENU));
                new Menu(size, ChatColor.BLUE + "Wait Times (" + currentPark.getId() + ")", player, buttons).open();
                new Menu(size, ChatColor.BLUE + "Wait Times (" + currentPark.getId() + ")", player, buttons).open();
                break;
            }
            case PARKS: {
                List<MenuButton> buttons = Arrays.asList(
                        new MenuButton(0, ItemUtil.create(Material.END_CRYSTAL, ChatColor.AQUA + "Park Servers",
                                Arrays.asList(ChatColor.GREEN + "Transfer to a different park server", "",
                                        ChatColor.YELLOW + "Current Server: " + ChatColor.GREEN + Core.getInstanceName(),
                                        ChatColor.AQUA + "" + ChatColor.ITALIC + "Coming Soon - use /f and /p for now")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.SERVER_LIST))),

                        new MenuButton(4, ItemUtil.create(Material.EMPTY_MAP, ChatColor.AQUA + "Walt Disney World Resort", Collections.singletonList(ChatColor.GREEN + "/warp WDW")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp wdw");
                                    p.closeInventory();
                                })),
                        new MenuButton(11, ItemUtil.create(Material.DIAMOND_HOE, ChatColor.AQUA + "Magic Kingdom", Collections.singletonList(ChatColor.GREEN + "/warp MK")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp mk");
                                    p.closeInventory();
                                })),
                        new MenuButton(12, ItemUtil.create(Material.SNOW_BALL, ChatColor.AQUA + "Epcot", Collections.singletonList(ChatColor.GREEN + "/warp Epcot")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp epcot");
                                    p.closeInventory();
                                })),
                        new MenuButton(13, ItemUtil.create(Material.JUKEBOX, ChatColor.AQUA + "Disney's Hollywood Studios", Collections.singletonList(ChatColor.GREEN + "/warp DHS")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp dhs");
                                    p.closeInventory();
                                })),
                        new MenuButton(14, ItemUtil.create(Material.SAPLING, 1, 5, ChatColor.AQUA + "Animal Kingdom", Collections.singletonList(ChatColor.GREEN + "/warp AK")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp ak");
                                    p.closeInventory();
                                })),
                        new MenuButton(15, ItemUtil.create(Material.WATER_BUCKET, ChatColor.AQUA + "Typhoon Lagoon", Collections.singletonList(ChatColor.GREEN + "/warp Typhoon")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp typhoon");
                                    p.closeInventory();
                                })),
                        new MenuButton(28, ItemUtil.create(Material.EMPTY_MAP, ChatColor.AQUA + "Seasonal", Collections.singletonList(ChatColor.GREEN + "/warp Seasonal")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp seasonal");
                                    p.closeInventory();
                                })),
                        new MenuButton(31, ItemUtil.create(Material.EMPTY_MAP, ChatColor.AQUA + "Universal Orlando Resort", Collections.singletonList(ChatColor.GREEN + "/warp UOR")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp uso");
                                    p.closeInventory();
                                })),
                        new MenuButton(34, ItemUtil.create(Material.BOAT, ChatColor.AQUA + "Disney Cruise Line", Collections.singletonList(ChatColor.GREEN + "/warp DCL")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp dcl");
                                    p.closeInventory();
                                })),
                        new MenuButton(39, ItemUtil.create(Material.JUKEBOX, ChatColor.AQUA + "Universal Studios Florida", Collections.singletonList(ChatColor.GREEN + "/warp USF")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp usf");
                                    p.closeInventory();
                                })),
                        new MenuButton(41, ItemUtil.create(Material.JUKEBOX, ChatColor.AQUA + "Islands of Adventure", Collections.singletonList(ChatColor.GREEN + "/warp IOA")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp ioa");
                                    p.closeInventory();
                                })),
                        getBackButton(49, BandInventory.MAIN)
                );
                new Menu(54, ChatColor.BLUE + "Park Menu", player, buttons).open();
                break;
            }
            case SHOP: {
                ParkType currentPark = currentParkOrOpenParkMenu(player);
                if (currentPark == null) return;
                List<MenuButton> buttons = new ArrayList<>();
                int i = 0;
                int size = 18;
                for (Shop shop : ParkManager.getShopManager().getShops(currentPark)) {
                    ItemStack item = shop.getItem();
                    ItemMeta meta = item.getItemMeta();
                    meta.setLore(Arrays.asList("", ChatColor.YELLOW + "/warp " + shop.getWarp()));
                    item.setItemMeta(meta);
                    if (i != 0 && i % 9 == 0) {
                        size += 9;
                    }
                    if (size > 54) {
                        size = 54;
                        break;
                    }
                    buttons.add(new MenuButton(i++, ItemUtil.unbreakable(item), ImmutableMap.of(ClickType.LEFT, p -> {
                        p.performCommand("warp " + shop.getWarp());
                        p.closeInventory();
                    })));
                }
                if (buttons.isEmpty()) {
                    buttons.add(new MenuButton(4, ItemUtil.create(Material.REDSTONE_BLOCK, ChatColor.RED + "No Shops",
                            Arrays.asList(ChatColor.GRAY + "Sorry, it looks like there are", ChatColor.GRAY + "no shops on this server!"))));
                }
                buttons.add(getBackButton(size - 5, BandInventory.MAIN));
                new Menu(size, ChatColor.BLUE + "Shop List (" + currentPark.getId() + ")", player, buttons).open();
                break;
            }
            case WARDROBE: {
                ParkManager.getWardrobeManager().openWardrobePage(player, 1);
                break;
            }
            case PROFILE: {
                new Menu(27, ChatColor.BLUE + "My Profile", player, Arrays.asList(
                        new MenuButton(10, ItemUtil.create(Material.NETHER_STAR, ChatColor.AQUA + "Website",
                                Collections.singletonList(ChatColor.GREEN + "Visit our website!")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    new FormattedMessage("\n")
                                            .then("Click here to visit our website")
                                            .color(ChatColor.YELLOW).style(ChatColor.UNDERLINE)
                                            .tooltip(ChatColor.GREEN + "Click to visit " + ChatColor.YELLOW + "https://palace.network")
                                            .link("https://palace.network").then("\n").send(p);
                                    p.closeInventory();
                                })),
                        new MenuButton(11, ItemUtil.create(Material.DIAMOND, ChatColor.AQUA + "Store",
                                Collections.singletonList(ChatColor.GREEN + "Visit our store!")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    new FormattedMessage("\n")
                                            .then("Click here to visit our store")
                                            .color(ChatColor.YELLOW).style(ChatColor.UNDERLINE)
                                            .tooltip(ChatColor.GREEN + "Click to visit " + ChatColor.YELLOW + "https://store.palace.network")
                                            .link("https://store.palace.network").then("\n").send(p);
                                    p.closeInventory();
                                })),
                        new MenuButton(12, ItemUtil.create(Material.ENDER_CHEST, ChatColor.AQUA + "Locker",
                                Collections.singletonList(ChatColor.GREEN + "Click to view your Locker")),
                                ImmutableMap.of(ClickType.LEFT, p -> ParkManager.getInventoryUtil().openMenu(p, MenuType.LOCKER))),
                        new MenuButton(13, ItemUtil.create(Material.GOLD_INGOT, ChatColor.AQUA + "Ride Counters",
                                Arrays.asList(ChatColor.GREEN + "View the number of times you've",
                                        ChatColor.GREEN + "been on different theme park rides")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.RIDE_COUNTERS))),
                        new MenuButton(14, ItemUtil.create(Material.EMERALD, ChatColor.AQUA + "Achievements", Arrays.asList(ChatColor.GREEN +
                                        "You've earned " + ChatColor.YELLOW + player.getAchievementManager().getAchievements().size() + ChatColor.GREEN + " achievements!",
                                ChatColor.GREEN + "There are " + ChatColor.YELLOW + Core.getAchievementManager().getAchievements().size() + ChatColor.GREEN + " total to earn",
                                ChatColor.GRAY + "Click to view all of your achievements")),
                                ImmutableMap.of(ClickType.LEFT, p -> Core.getCraftingMenu().openAchievementPage(p, 1))),
                        new MenuButton(15, ItemUtil.create(Material.NOTE_BLOCK, ChatColor.AQUA + "Resource Packs",
                                Collections.singletonList(ChatColor.GREEN + "Manage your Resource Pack settings")),
                                ImmutableMap.of(ClickType.LEFT, p -> ParkManager.getPackManager().openMenu(p))),
                        new MenuButton(16, ItemUtil.create(Material.COMPASS, ChatColor.AQUA + "Discord",
                                Collections.singletonList(ChatColor.GREEN + "Join the conversation on our Discord!")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    new FormattedMessage("\n")
                                            .then("Click here to join our Discord")
                                            .color(ChatColor.YELLOW).style(ChatColor.UNDERLINE)
                                            .tooltip(ChatColor.GREEN + "Click to run " + ChatColor.YELLOW + "/discord")
                                            .command("/discord").then("\n").send(p);
                                    p.closeInventory();
                                })),
                        getBackButton(22, BandInventory.MAIN)
                )).open();
                break;
            }
            case RIDE_COUNTERS: {
                openRideCounterPage(player, 1);
                break;
            }
            case VISIBILITY: {
                VisibilityUtil.Setting setting = ParkManager.getVisibilityUtil().getSetting(player);
                ItemStack visible = ItemUtil.create(VisibilityUtil.Setting.ALL_VISIBLE.getBlock(), 1,
                        VisibilityUtil.Setting.ALL_VISIBLE.getData(),
                        VisibilityUtil.Setting.ALL_VISIBLE.getColor() + VisibilityUtil.Setting.ALL_VISIBLE.getText()
                                + (setting.equals(VisibilityUtil.Setting.ALL_VISIBLE) ? (ChatColor.YELLOW + " (SELECTED)") : ""),
                        Collections.singletonList(ChatColor.GREEN + "Show all players"));
                ItemStack staffFriends = ItemUtil.create(VisibilityUtil.Setting.ONLY_STAFF_AND_FRIENDS.getBlock(), 1,
                        VisibilityUtil.Setting.ONLY_STAFF_AND_FRIENDS.getData(),
                        VisibilityUtil.Setting.ONLY_STAFF_AND_FRIENDS.getColor() + VisibilityUtil.Setting.ONLY_STAFF_AND_FRIENDS.getText()
                                + (setting.equals(VisibilityUtil.Setting.ONLY_STAFF_AND_FRIENDS) ? (ChatColor.YELLOW + " (SELECTED)") : ""),
                        Collections.singletonList(ChatColor.GREEN + "Show only staff and friends"));
                ItemStack friends = ItemUtil.create(VisibilityUtil.Setting.ONLY_FRIENDS.getBlock(), 1,
                        VisibilityUtil.Setting.ONLY_FRIENDS.getData(),
                        VisibilityUtil.Setting.ONLY_FRIENDS.getColor() + VisibilityUtil.Setting.ONLY_FRIENDS.getText()
                                + (setting.equals(VisibilityUtil.Setting.ONLY_FRIENDS) ? (ChatColor.YELLOW + " (SELECTED)") : ""),
                        Collections.singletonList(ChatColor.GREEN + "Show only friends"));
                ItemStack none = ItemUtil.create(VisibilityUtil.Setting.ALL_HIDDEN.getBlock(), 1,
                        VisibilityUtil.Setting.ALL_HIDDEN.getData(),
                        VisibilityUtil.Setting.ALL_HIDDEN.getColor() + VisibilityUtil.Setting.ALL_HIDDEN.getText()
                                + (setting.equals(VisibilityUtil.Setting.ALL_HIDDEN) ? (ChatColor.YELLOW + " (SELECTED)") : ""),
                        Collections.singletonList(ChatColor.GREEN + "Hide all players"));
                ItemMeta meta;
                switch (setting) {
                    case ALL_VISIBLE:
                        visible.addUnsafeEnchantment(Enchantment.LUCK, 1);
                        meta = visible.getItemMeta();
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        visible.setItemMeta(meta);
                        break;
                    case ONLY_STAFF_AND_FRIENDS:
                        staffFriends.addUnsafeEnchantment(Enchantment.LUCK, 1);
                        meta = staffFriends.getItemMeta();
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        staffFriends.setItemMeta(meta);
                        break;
                    case ONLY_FRIENDS:
                        friends.addUnsafeEnchantment(Enchantment.LUCK, 1);
                        meta = friends.getItemMeta();
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        friends.setItemMeta(meta);
                        break;
                    case ALL_HIDDEN:
                        none.addUnsafeEnchantment(Enchantment.LUCK, 1);
                        meta = none.getItemMeta();
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        none.setItemMeta(meta);
                        break;
                }
                List<MenuButton> buttons = Arrays.asList(
                        new MenuButton(10, visible,
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    if (ParkManager.getVisibilityUtil().setSetting(p, VisibilityUtil.Setting.ALL_VISIBLE, false)) {
                                        openInventory(p, BandInventory.VISIBILITY);
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 2);
                                    }
                                })),
                        new MenuButton(12, staffFriends,
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    if (ParkManager.getVisibilityUtil().setSetting(p, VisibilityUtil.Setting.ONLY_STAFF_AND_FRIENDS, false)) {
                                        openInventory(p, BandInventory.VISIBILITY);
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 2);
                                    }
                                })),
                        new MenuButton(14, friends,
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    if (ParkManager.getVisibilityUtil().setSetting(p, VisibilityUtil.Setting.ONLY_FRIENDS, false)) {
                                        openInventory(p, BandInventory.VISIBILITY);
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 2);
                                    }
                                })),
                        new MenuButton(16, none,
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    if (ParkManager.getVisibilityUtil().setSetting(p, VisibilityUtil.Setting.ALL_HIDDEN, false)) {
                                        openInventory(p, BandInventory.VISIBILITY);
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 2);
                                    }
                                })),
                        getBackButton(22, BandInventory.MAIN)
                );
                new Menu(27, ChatColor.BLUE + "Visibility Settings", player, buttons).open();
                break;
            }
            case CUSTOMIZE_BAND: {
                List<MenuButton> buttons = Arrays.asList(
                        new MenuButton(11, ItemUtil.create(getMaterial(BandType.SORCERER_MICKEY), ChatColor.GREEN + "Customize MagicBand Type"),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.CUSTOMIZE_BAND_TYPE))),
                        new MenuButton(15, ItemUtil.create(Material.JUKEBOX, ChatColor.GREEN + "Customize MagicBand Name Color"),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.CUSTOMIZE_BAND_NAME))),
                        getBackButton(22, BandInventory.MAIN)
                );
                new Menu(27, ChatColor.BLUE + "Customize MagicBand", player, buttons).open();
                break;
            }
            case CUSTOMIZE_BAND_TYPE: {
                ItemStack red = getMagicBandItem("red", (String) player.getRegistry().getEntry("bandNameColor"));
                ItemMeta meta = red.getItemMeta();
                meta.setDisplayName(BandType.RED.getName());
                red.setItemMeta(meta);
                ItemStack orange = getMagicBandItem("orange", (String) player.getRegistry().getEntry("bandNameColor"));
                meta = orange.getItemMeta();
                meta.setDisplayName(BandType.ORANGE.getName());
                orange.setItemMeta(meta);
                ItemStack yellow = getMagicBandItem("yellow", (String) player.getRegistry().getEntry("bandNameColor"));
                meta = yellow.getItemMeta();
                meta.setDisplayName(BandType.YELLOW.getName());
                yellow.setItemMeta(meta);
                ItemStack green = getMagicBandItem("green", (String) player.getRegistry().getEntry("bandNameColor"));
                meta = green.getItemMeta();
                meta.setDisplayName(BandType.GREEN.getName());
                green.setItemMeta(meta);
                ItemStack blue = getMagicBandItem("blue", (String) player.getRegistry().getEntry("bandNameColor"));
                meta = blue.getItemMeta();
                meta.setDisplayName(BandType.BLUE.getName());
                blue.setItemMeta(meta);
                ItemStack purple = getMagicBandItem("purple", (String) player.getRegistry().getEntry("bandNameColor"));
                meta = purple.getItemMeta();
                meta.setDisplayName(BandType.PURPLE.getName());
                purple.setItemMeta(meta);
                ItemStack pink = getMagicBandItem("pink", (String) player.getRegistry().getEntry("bandNameColor"));
                meta = pink.getItemMeta();
                meta.setDisplayName(BandType.PINK.getName());
                pink.setItemMeta(meta);
                List<MenuButton> buttons = Arrays.asList(
                        new MenuButton(10, red, ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.RED.getDBName()))),
                        new MenuButton(11, orange, ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.ORANGE.getDBName()))),
                        new MenuButton(12, yellow, ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.YELLOW.getDBName()))),
                        new MenuButton(13, green, ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.GREEN.getDBName()))),
                        new MenuButton(14, blue, ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.BLUE.getDBName()))),
                        new MenuButton(15, purple, ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.PURPLE.getDBName()))),
                        new MenuButton(16, pink, ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.PINK.getDBName()))),

                        new MenuButton(20, ItemUtil.unbreakable(ItemUtil.create(getMaterial(BandType.SORCERER_MICKEY), BandType.SORCERER_MICKEY.getName())),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.SORCERER_MICKEY.getDBName()))),
                        new MenuButton(21, ItemUtil.unbreakable(ItemUtil.create(getMaterial(BandType.HAUNTED_MANSION), BandType.HAUNTED_MANSION.getName())),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.HAUNTED_MANSION.getDBName()))),
                        new MenuButton(22, ItemUtil.unbreakable(ItemUtil.create(getMaterial(BandType.PRINCESSES), BandType.PRINCESSES.getName())),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.PRINCESSES.getDBName()))),
                        new MenuButton(23, ItemUtil.unbreakable(ItemUtil.create(getMaterial(BandType.BIG_HERO_SIX), BandType.BIG_HERO_SIX.getName())),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.BIG_HERO_SIX.getDBName()))),
                        new MenuButton(24, ItemUtil.unbreakable(ItemUtil.create(getMaterial(BandType.HOLIDAY), BandType.HOLIDAY.getName())),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.HOLIDAY.getDBName()))),
                        getBackButton(31, BandInventory.CUSTOMIZE_BAND)
                );
                new Menu(36, ChatColor.BLUE + "Customize MagicBand Type", player, buttons).open();
                break;
            }
            case CUSTOMIZE_BAND_NAME: {
                List<MenuButton> buttons = Arrays.asList(
                        new MenuButton(10, ItemUtil.create(Material.CONCRETE, ChatColor.RED + "Red", 14),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandNameColor(p, "red"))),
                        new MenuButton(11, ItemUtil.create(Material.CONCRETE, ChatColor.GOLD + "Orange", 1),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandNameColor(p, "orange"))),
                        new MenuButton(12, ItemUtil.create(Material.CONCRETE, ChatColor.YELLOW + "Yellow", 4),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandNameColor(p, "yellow"))),
                        new MenuButton(13, ItemUtil.create(Material.CONCRETE, ChatColor.DARK_GREEN + "Green", 13),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandNameColor(p, "green"))),
                        new MenuButton(14, ItemUtil.create(Material.CONCRETE, ChatColor.BLUE + "Blue", 11),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandNameColor(p, "blue"))),
                        new MenuButton(15, ItemUtil.create(Material.CONCRETE, ChatColor.DARK_PURPLE + "Purple", 10),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandNameColor(p, "purple"))),
                        new MenuButton(16, ItemUtil.create(Material.CONCRETE, ChatColor.LIGHT_PURPLE + "Pink", 6),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandNameColor(p, "pink"))),
                        getBackButton(22, BandInventory.CUSTOMIZE_BAND)
                );
                new Menu(27, ChatColor.BLUE + "Customize MagicBand Name Color", player, buttons).open();
                break;
            }
            case TIMETABLE: {
                List<MenuButton> buttons = ParkManager.getScheduleManager().getButtons();
                buttons.add(getBackButton(49, BandInventory.SHOWS));
                new Menu(54, ChatColor.BLUE + "Show Timetable", player, buttons).open();
                break;
            }
            case PLAYER_TIME: {
                long time = player.getBukkitPlayer().getPlayerTime() % 24000;
                List<String> current = Collections.singletonList(ChatColor.YELLOW + "Currently Selected!");
                List<String> not = Collections.singletonList(ChatColor.GRAY + "Click to Select!");
                new Menu(27, ChatColor.BLUE + "Player Time", player, Arrays.asList(
                        new MenuButton(9, ItemUtil.create(Material.STAINED_GLASS_PANE, ChatColor.GREEN + "Reset",
                                Collections.singletonList(ChatColor.GREEN + "Match Park Time")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.sendMessage(ChatColor.GREEN + "You " + ChatColor.AQUA + "reset " + ChatColor.GREEN + "your Player Time!");
                                    p.getBukkitPlayer().resetPlayerTime();
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        new MenuButton(10, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "6AM", time == 0 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(0, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "6AM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        new MenuButton(11, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "9AM", time == 3000 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(3000, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "9AM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        new MenuButton(12, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "12PM", time == 6000 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(6000, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "12PM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        new MenuButton(13, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "3PM", time == 9000 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(9000, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "3PM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        new MenuButton(14, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "6PM", time == 12000 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(12000, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "6PM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        new MenuButton(15, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "9PM", time == 15000 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(15000, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "9PM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        new MenuButton(16, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "12AM", time == 18000 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(18000, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "12AM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        new MenuButton(17, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "3AM", time == 21000 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(21000, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "3AM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        getBackButton(22, BandInventory.MAIN)
                )).open();
                break;
            }
        }
    }

    /**
     * Get the park a player is currently in.
     * If they aren't in a park, open the park selection menu.
     *
     * @param player the player
     * @return a ParkType representing the park they're in, or null if not in a park
     * @implNote do nothing if the return is null, as this method will open a menu
     */
    private ParkType currentParkOrOpenParkMenu(CPlayer player) {
        Park p = ParkManager.getParkUtil().getPark(player.getLocation());
        if (p != null) return p.getId();
        openInventory(player, BandInventory.PARKS);
        return null;
    }

    @SuppressWarnings("unchecked")
    public void openRideCounterPage(CPlayer player, int page) {
        List<MenuButton> buttons = new ArrayList<>();
        TreeMap<String, RideCount> data = (TreeMap<String, RideCount>) player.getRegistry().getEntry("rideCounterCache");

        List<RideCount> rides = new ArrayList<>(data.values());
        rides.sort((o1, o2) -> {
            if (o1.getServer().equals(o2.getServer())) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            } else {
                return o1.getServer().toLowerCase().compareTo(o2.getServer().toLowerCase());
            }
        });
        int size = rides.size();
        if (size < 46) {
            page = 1;
        } else if (size < (45 * (page - 1) + 1)) {
            page -= 1;
        }
        List<RideCount> list = rides.subList(page > 1 ? (45 * (page - 1)) : 0, (size - (45 * (page - 1))) > 45 ? (45 * page) : size);
        int pos = 0;
        for (RideCount ride : list) {
            if (pos >= 45) break;
            buttons.add(new MenuButton(pos++, ItemUtil.create(Material.MINECART, ChatColor.GREEN + ride.getName(),
                    Arrays.asList(ChatColor.YELLOW + "Rides: " + ride.getCount(), ChatColor.YELLOW + "Park: " + ride.getServer()))));
        }
        int finalPage = page;
        if (page > 1) {
            buttons.add(new MenuButton(48, ItemUtil.create(Material.ARROW, ChatColor.GREEN + "Last Page"),
                    ImmutableMap.of(ClickType.LEFT, p -> openRideCounterPage(p, finalPage - 1))));
        }
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
            buttons.add(new MenuButton(50, ItemUtil.create(Material.ARROW, ChatColor.GREEN + "Next Page"),
                    ImmutableMap.of(ClickType.LEFT, p -> openRideCounterPage(p, finalPage + 1))));
        }
        buttons.add(getBackButton(49, BandInventory.PROFILE));
        new Menu(54, ChatColor.GREEN + "Ride Counter Page " + page, player, buttons).open();
    }

    private void setBandType(CPlayer player, String type) {
        player.getRegistry().addEntry("bandType", type.toLowerCase());
        ParkManager.getStorageManager().updateInventory(player);
        player.sendMessage(ChatColor.GREEN + "You've changed to a " + BandType.fromString(type).getName() + ChatColor.GREEN + " MagicBand!");
        player.closeInventory();
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> Core.getMongoHandler().setMagicBandData(player.getUniqueId(), "bandtype", type.toLowerCase()));
    }

    private void setBandNameColor(CPlayer player, String color) {
        player.getRegistry().addEntry("bandNameColor", color.toLowerCase());
        ParkManager.getStorageManager().updateInventory(player);
        player.sendMessage(ChatColor.GREEN + "You've set your MagicBand's name color to " + getNameColor(color) + color + "!");
        player.closeInventory();
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> Core.getMongoHandler().setMagicBandData(player.getUniqueId(), "namecolor", color.toLowerCase()));
    }

    public void handleJoin(CPlayer player, Document doc) {
        String bandtype, namecolor;
        if (!doc.containsKey("bandtype") || !doc.containsKey("namecolor")) {
            bandtype = "red";
            namecolor = "gold";
        } else {
            bandtype = doc.getString("bandtype");
            namecolor = doc.getString("namecolor");
        }
        player.getRegistry().addEntry("bandType", bandtype);
        player.getRegistry().addEntry("bandNameColor", namecolor);
        Core.runTaskAsynchronously(() -> {
            TreeMap<String, RideCount> data = new TreeMap<>();
            for (Object o : Core.getMongoHandler().getRideCounterData(player.getUniqueId())) {
                Document d = (Document) o;
                String name = d.getString("name").trim();
                String server = d.getString("server").replaceAll("[^A-Za-z ]", "");
                if (data.containsKey(name) && data.get(name).serverEquals(server)) {
                    data.get(name).addCount(1);
                } else {
                    data.put(name, new RideCount(name, server));
                }
            }
            player.getRegistry().addEntry("rideCounterCache", data);
        });
    }

    public ItemStack getMagicBandItem(CPlayer player) {
        if (!player.getRegistry().hasEntry("bandType") || !player.getRegistry().hasEntry("bandNameColor")) {
            return getMagicBandItem("red", "gold");
        }
        return getMagicBandItem((String) player.getRegistry().getEntry("bandType"), (String) player.getRegistry().getEntry("bandNameColor"));
    }

    public ItemStack getMagicBandItem(String type, String color) {
        if (ParkManager.getResort().equals(Resort.USO))
            return ItemUtil.create(Material.PAPER, getNameColor(color) + "Power Pass " + ChatColor.GRAY + "(Right-Click)");
        BandType bandType = BandType.fromString(type);
        ItemStack item;
        switch (bandType) {
            case RED:
            case ORANGE:
            case YELLOW:
            case GREEN:
            case BLUE:
            case PURPLE:
            case PINK: {
                item = ItemUtil.create(Material.FIREWORK_CHARGE, getNameColor(color) + "MagicBand " +
                        ChatColor.GRAY + "(Right-Click)");
                FireworkEffectMeta meta = (FireworkEffectMeta) item.getItemMeta();
                meta.setEffect(FireworkEffect.builder().withColor(getBandColor(bandType)).build());
                item.setItemMeta(meta);
                break;
            }
            case SORCERER_MICKEY:
            case HAUNTED_MANSION:
            case PRINCESSES:
            case BIG_HERO_SIX:
            case HOLIDAY:
                item = ItemUtil.create(getMaterial(bandType), getNameColor(color) + "MagicBand " +
                        ChatColor.GRAY + "(Right-Click)");
                break;
            default:
                return getMagicBandItem("red", "gold");
        }
        return item;
    }

    private Material getMaterial(BandType type) {
        if (ParkManager.getResort().equals(Resort.USO)) return Material.PAPER;
        switch (type) {
            case SORCERER_MICKEY:
                return Material.DIAMOND_BARDING;
            case HAUNTED_MANSION:
                return Material.GOLD_BARDING;
            case PRINCESSES:
                return Material.GHAST_TEAR;
            case BIG_HERO_SIX:
                return Material.IRON_BARDING;
            case HOLIDAY:
                return Material.PAPER;
            default:
                return Material.FIREWORK_CHARGE;
        }
    }

    private Color getBandColor(BandType type) {
        switch (type) {
            case ORANGE:
                return Color.fromRGB(247, 140, 0);
            case YELLOW:
                return Color.fromRGB(239, 247, 0);
            case GREEN:
                return Color.fromRGB(0, 192, 13);
            case BLUE:
                return Color.fromRGB(41, 106, 255);
            case PURPLE:
                return Color.fromRGB(176, 0, 220);
            case PINK:
                return Color.fromRGB(246, 120, 255);
            default:
                //Red
                return Color.fromRGB(255, 40, 40);
        }
    }

    private ChatColor getNameColor(String color) {
        switch (color.toLowerCase()) {
            case "red":
                return ChatColor.RED;
            case "yellow":
                return ChatColor.YELLOW;
            case "green":
                return ChatColor.DARK_GREEN;
            case "blue":
                return ChatColor.BLUE;
            case "purple":
                return ChatColor.DARK_PURPLE;
            case "pink":
                return ChatColor.LIGHT_PURPLE;
            default:
                //Gold
                return ChatColor.GOLD;
        }
    }

    public MenuButton getBackButton(int slot, BandInventory inv) {
        return new MenuButton(slot, ItemUtil.create(Material.ARROW, ChatColor.GRAY + "Back"),
                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, inv)));
    }
}
