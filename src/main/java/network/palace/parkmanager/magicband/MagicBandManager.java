package network.palace.parkmanager.magicband;

import com.google.common.collect.ImmutableMap;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.HeadUtil;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.attractions.Attraction;
import network.palace.parkmanager.food.FoodLocation;
import network.palace.parkmanager.handlers.AttractionCategory;
import network.palace.parkmanager.handlers.magicband.BandType;
import network.palace.parkmanager.queues.Queue;
import network.palace.parkmanager.shop.Shop;
import network.palace.parkmanager.utils.VisibilityUtil;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MagicBandManager {

    public void openInventory(CPlayer player, BandInventory inventory) {
        switch (inventory) {
            case MAIN: {
                VisibilityUtil.Setting setting = ParkManager.getVisibilityUtil().getSetting(player);
                ChatColor color = setting.getColor();
                List<MenuButton> buttons = Arrays.asList(
                        new MenuButton(2, ItemUtil.create(Material.LIGHT_BLUE_BED, ChatColor.AQUA + "Hotels and Resorts",
                                Arrays.asList(ChatColor.GREEN + "Visit and rent a room", ChatColor.GREEN + "from a Resort Hotel!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.HOTELS))),
                        new MenuButton(4, HeadUtil.getPlayerHead(player.getTextureValue(), ChatColor.AQUA + "My Profile"),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.PROFILE))),
                        new MenuButton(10, ItemUtil.create(Material.POTATO, ChatColor.AQUA + "Find Food",
                                Arrays.asList(ChatColor.GREEN + "Visit a restaurant", ChatColor.GREEN + "to get some food!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.FOOD))),
                        new MenuButton(11, ItemUtil.create(Material.FIREWORK_ROCKET, ChatColor.AQUA + "Shows and Events",
                                Arrays.asList(ChatColor.GREEN + "Watch stage shows, nighttime", ChatColor.GREEN + "spectaculars, and much more!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.SHOWS))),
                        new MenuButton(12, ItemUtil.create(Material.MINECART, ChatColor.AQUA + "Attractions",
                                Arrays.asList(ChatColor.GREEN + "View all of our available", ChatColor.GREEN + "theme park attractions!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.ATTRACTION_MENU))),
                        new MenuButton(13, ItemUtil.create(Material.NETHER_STAR, ChatColor.AQUA + "Park Menu",
                                Arrays.asList(ChatColor.GREEN + "Travel to all of our", ChatColor.GREEN + "available theme parks!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.PARKS))),
                        new MenuButton(14, ItemUtil.create(Material.GOLDEN_BOOTS, ChatColor.AQUA + "Shop",
                                Arrays.asList(ChatColor.GREEN + "Purchase souveniers and", ChatColor.GREEN + "all kinds of collectibles!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.SHOP))),
                        new MenuButton(15, ItemUtil.create(Material.IRON_CHESTPLATE, ChatColor.AQUA + "Wardrobe Manager",
                                Arrays.asList(ChatColor.GREEN + "Change your outfit to make you", ChatColor.GREEN + "look like your favorite characters!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.WARDROBE))),
                        new MenuButton(16, ItemUtil.create(setting.getBlock(), ChatColor.AQUA + "Guest Visibility " +
                                        ChatColor.GOLD + "➠ " + setting.getColor() + setting.getText(),
                                Arrays.asList(ChatColor.YELLOW + "Right-Click to " + (setting.equals(VisibilityUtil.Setting.ALL_HIDDEN) ? "show" : "hide") + " all players",
                                        ChatColor.YELLOW + "Left-Click for more options")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.VISIBILITY), ClickType.RIGHT, p -> {
                                    if (ParkManager.getVisibilityUtil().toggleVisibility(player)) {
                                        openInventory(p, BandInventory.MAIN);
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                                    }
                                }))
                );

                new Menu(27, ChatColor.BLUE + "Your MagicBand", player, buttons).open();
                break;
            }
            case FOOD: {
                List<MenuButton> buttons = new ArrayList<>();
                int i = 0;
                int size = 18;
                for (FoodLocation food : ParkManager.getFoodManager().getFoodLocations()) {
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
                    buttons.add(new MenuButton(i++, item, ImmutableMap.of(ClickType.LEFT, p -> p.performCommand("warp " + food.getWarp()))));
                }
                if (buttons.isEmpty()) {
                    buttons.add(new MenuButton(4, ItemUtil.create(Material.REDSTONE_BLOCK, ChatColor.RED + "No Food Locations",
                            Arrays.asList(ChatColor.GRAY + "Sorry, it looks like there are", ChatColor.GRAY + "no food locations on this server!"))));
                }
                buttons.add(getBackButton(size - 5, BandInventory.MAIN));
                new Menu(size, ChatColor.BLUE + "Food Locations", player, buttons).open();
                break;
            }
            case SHOWS: {
                new Menu(27, ChatColor.BLUE + "Shows and Events", player, Arrays.asList(
                        new MenuButton(8, ItemUtil.create(Material.BOOK, ChatColor.AQUA + "Show Timetable"),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(player, BandInventory.TIMETABLE))),
                        new MenuButton(10, ItemUtil.create(Material.DIAMOND_SWORD, ChatColor.RED + "Symphony in the Stars"),
                                ImmutableMap.of(ClickType.LEFT, p -> p.performCommand("warp sits"))),
                        new MenuButton(11, ItemUtil.create(Material.DIAMOND_HELMET, ChatColor.BLUE + "Fantasmic!"),
                                ImmutableMap.of(ClickType.LEFT, p -> p.performCommand("warp fant"))),
                        new MenuButton(12, ItemUtil.create(Material.BLAZE_ROD, ChatColor.AQUA + "Wishes!"),
                                ImmutableMap.of(ClickType.LEFT, p -> p.performCommand("warp castle"))),
                        new MenuButton(13, ItemUtil.create(Material.SHEEP_SPAWN_EGG, ChatColor.GREEN + "Illuminations: Reflections of Earth"),
                                ImmutableMap.of(ClickType.LEFT, p -> p.performCommand("warp iroe"))),
                        new MenuButton(14, ItemUtil.create(Material.LIGHT_BLUE_DYE, ChatColor.DARK_AQUA + "Festival of Fantasy"),
                                ImmutableMap.of(ClickType.LEFT, p -> p.performCommand("warp fof"))),
                        new MenuButton(15, ItemUtil.create(Material.GLOWSTONE_DUST, ChatColor.YELLOW + "Main Street Electrical Parade"),
                                ImmutableMap.of(ClickType.LEFT, p -> p.performCommand("warp msep"))),
                        new MenuButton(16, ItemUtil.create(Material.TROPICAL_FISH, ChatColor.GOLD + "Finding Nemo: The Musical"),
                                ImmutableMap.of(ClickType.LEFT, p -> p.performCommand("warp fntm"))),
                        getBackButton(22, BandInventory.MAIN))).open();
                break;
            }
            case ATTRACTION_MENU:
                new Menu(27, ChatColor.BLUE + "Attractions Menu", player, Arrays.asList(
                        new MenuButton(11, ItemUtil.create(Material.MINECART, ChatColor.AQUA + "Attractions List",
                                Arrays.asList(ChatColor.GREEN + "View all of our available", ChatColor.GREEN + "theme park attractions")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(player, BandInventory.ATTRACTION_LIST))),
                        new MenuButton(15, ItemUtil.create(Material.CLOCK, ChatColor.AQUA + "Wait Times",
                                Arrays.asList(ChatColor.GREEN + "View the wait times for all", ChatColor.GREEN + "queues on this server")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(player, BandInventory.WAIT_TIMES))),
                        getBackButton(22, BandInventory.MAIN))).open();
                break;
            case ATTRACTION_LIST: {
                List<MenuButton> buttons = new ArrayList<>();
                int i = 0;
                int size = 18;
                for (Attraction attraction : ParkManager.getAttractionManager().getAttractions()) {
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
                        Queue queue = ParkManager.getQueueManager().getQueue(attraction.getLinkedQueue());
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
                    buttons.add(new MenuButton(i++, item, ImmutableMap.of(ClickType.LEFT, p -> p.performCommand("warp " + attraction.getWarp()))));
                }
                if (buttons.isEmpty()) {
                    buttons.add(new MenuButton(4, ItemUtil.create(Material.REDSTONE_BLOCK, ChatColor.RED + "No Attractions",
                            Arrays.asList(ChatColor.GRAY + "Sorry, it looks like there are", ChatColor.GRAY + "no attractions on this server!"))));
                }
                buttons.add(getBackButton(size - 5, BandInventory.ATTRACTION_MENU));
                new Menu(size, ChatColor.BLUE + "Attractions List", player, buttons).open();
                break;
            }
            case WAIT_TIMES: {
                List<MenuButton> buttons = new ArrayList<>();
                int i = 0;
                int size = 18;
                for (Queue queue : ParkManager.getQueueManager().getQueues()) {
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
                    buttons.add(new MenuButton(i++, item, ImmutableMap.of(ClickType.LEFT, p -> p.performCommand("warp " + queue.getWarp()))));
                }
                if (buttons.isEmpty()) {
                    buttons.add(new MenuButton(4, ItemUtil.create(Material.REDSTONE_BLOCK, ChatColor.RED + "No Queues",
                            Arrays.asList(ChatColor.GRAY + "Sorry, it looks like there are", ChatColor.GRAY + "no queues on this server!"))));
                }
                buttons.add(getBackButton(size - 5, BandInventory.ATTRACTION_MENU));
                new Menu(size, ChatColor.BLUE + "Wait Times", player, buttons).open();
                break;
            }
            case PARKS: {
                List<MenuButton> buttons = Arrays.asList(
                        new MenuButton(10, ItemUtil.create(Material.FILLED_MAP, ChatColor.AQUA + "Universal Orlando Resort", Collections.singletonList(ChatColor.GREEN + "/warp USO")),
                                ImmutableMap.of(ClickType.LEFT, p -> p.performCommand("warp uso"))),
                        new MenuButton(13, ItemUtil.create(Material.FILLED_MAP, ChatColor.AQUA + "Walt Disney World Resort", Collections.singletonList(ChatColor.GREEN + "/warp WDW")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(player, BandInventory.PARKS_WDW))),
                        new MenuButton(16, ItemUtil.create(Material.SNOWBALL, ChatColor.AQUA + "Seasonal", Collections.singletonList(ChatColor.GREEN + "/warp Seasonal")),
                                ImmutableMap.of(ClickType.LEFT, p -> p.performCommand("warp seasonal"))),
                        getBackButton(22, BandInventory.MAIN)
                );
                new Menu(27, ChatColor.BLUE + "Park Menu", player, buttons).open();
                break;
            }
            case PARKS_WDW: {
                List<MenuButton> buttons = Arrays.asList(
                        new MenuButton(10, ItemUtil.create(Material.DIAMOND_HOE, ChatColor.AQUA + "Magic Kingdom", Collections.singletonList(ChatColor.GREEN + "/warp MK")),
                                ImmutableMap.of(ClickType.LEFT, p -> p.performCommand("warp mk"))),
                        new MenuButton(11, ItemUtil.create(Material.SNOWBALL, ChatColor.AQUA + "Epcot", Collections.singletonList(ChatColor.GREEN + "/warp Epcot")),
                                ImmutableMap.of(ClickType.LEFT, p -> p.performCommand("warp epcot"))),
                        new MenuButton(12, ItemUtil.create(Material.JUKEBOX, ChatColor.AQUA + "Disney's Hollywood Studios", Collections.singletonList(ChatColor.GREEN + "/warp DHS")),
                                ImmutableMap.of(ClickType.LEFT, p -> p.performCommand("warp dhs"))),
                        new MenuButton(13, ItemUtil.create(Material.OAK_SAPLING, ChatColor.AQUA + "Disney's Animal Kingdom", Collections.singletonList(ChatColor.GREEN + "/warp AK")),
                                ImmutableMap.of(ClickType.LEFT, p -> p.performCommand("warp ak"))),
                        new MenuButton(14, ItemUtil.create(Material.LIGHT_BLUE_BED, ChatColor.AQUA + "Resorts", Collections.singletonList(ChatColor.GREEN + "/warp Resorts")),
                                ImmutableMap.of(ClickType.LEFT, p -> p.performCommand("warp resorts"))),
                        new MenuButton(15, ItemUtil.create(Material.WATER_BUCKET, ChatColor.AQUA + "Typhoon Lagoon", Collections.singletonList(ChatColor.GREEN + "/warp Typhoon")),
                                ImmutableMap.of(ClickType.LEFT, p -> p.performCommand("warp typhoon"))),
                        new MenuButton(16, ItemUtil.create(Material.OAK_BOAT, ChatColor.AQUA + "Disney Cruise Line", Collections.singletonList(ChatColor.GREEN + "/warp DCL")),
                                ImmutableMap.of(ClickType.LEFT, p -> p.performCommand("warp dcl"))),
                        getBackButton(22, BandInventory.PARKS)
                );
                new Menu(27, ChatColor.BLUE + "Park Menu - WDW", player, buttons).open();
                break;
            }
            case SHOP: {
                List<MenuButton> buttons = new ArrayList<>();
                int i = 0;
                int size = 18;
                for (Shop shop : ParkManager.getShopManager().getShops()) {
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
                    buttons.add(new MenuButton(i++, item, ImmutableMap.of(ClickType.LEFT, p -> p.performCommand("warp " + shop.getWarp()))));
                }
                if (buttons.isEmpty()) {
                    buttons.add(new MenuButton(4, ItemUtil.create(Material.REDSTONE_BLOCK, ChatColor.RED + "No Shops",
                            Arrays.asList(ChatColor.GRAY + "Sorry, it looks like there are", ChatColor.GRAY + "no shops on this server!"))));
                }
                buttons.add(getBackButton(size - 5, BandInventory.MAIN));
                new Menu(size, ChatColor.BLUE + "Shop List", player, buttons).open();
                break;
            }
            case WARDROBE: {
                new Menu(27, ChatColor.BLUE + "Wardrobe Manager", player, Arrays.asList(
                        new MenuButton(13, ItemUtil.create(Material.REDSTONE_BLOCK, ChatColor.AQUA + "Pardon Our Pixie Dust!",
                                Arrays.asList(ChatColor.GRAY + "We've temporarily disabled outfits",
                                        ChatColor.GRAY + "while we work to improve them",
                                        ChatColor.GRAY + "behind the scenes.", "",
                                        ChatColor.GRAY + "We apologize for the inconvenience,",
                                        ChatColor.GRAY + "they will be returning shortly!"))),
                        getBackButton(22, BandInventory.MAIN))
                ).open();
                break;
            }
            case HOTELS: {
                new Menu(27, ChatColor.BLUE + "Hotels and Resorts", player, Collections.singletonList(getBackButton(22, BandInventory.MAIN))).open();
                break;
            }
            case PROFILE: {
                new Menu(27, ChatColor.BLUE + "My Profile", player, Collections.singletonList(getBackButton(22, BandInventory.MAIN))).open();
                break;
            }
            case VISIBILITY: {
                VisibilityUtil.Setting setting = ParkManager.getVisibilityUtil().getSetting(player);
                ItemStack visible = ItemUtil.create(VisibilityUtil.Setting.ALL_VISIBLE.getBlock(),
                        VisibilityUtil.Setting.ALL_VISIBLE.getColor() + VisibilityUtil.Setting.ALL_VISIBLE.getText()
                                + (setting.equals(VisibilityUtil.Setting.ALL_VISIBLE) ? (ChatColor.YELLOW + " (SELECTED)") : ""),
                        Collections.singletonList(ChatColor.GREEN + "Show all players"));
                ItemStack staffFriends = ItemUtil.create(VisibilityUtil.Setting.ONLY_STAFF_AND_FRIENDS.getBlock(),
                        VisibilityUtil.Setting.ONLY_STAFF_AND_FRIENDS.getColor() + VisibilityUtil.Setting.ONLY_STAFF_AND_FRIENDS.getText()
                                + (setting.equals(VisibilityUtil.Setting.ONLY_STAFF_AND_FRIENDS) ? (ChatColor.YELLOW + " (SELECTED)") : ""),
                        Collections.singletonList(ChatColor.GREEN + "Show only staff and friends"));
                ItemStack friends = ItemUtil.create(VisibilityUtil.Setting.ONLY_FRIENDS.getBlock(),
                        VisibilityUtil.Setting.ONLY_FRIENDS.getColor() + VisibilityUtil.Setting.ONLY_FRIENDS.getText()
                                + (setting.equals(VisibilityUtil.Setting.ONLY_FRIENDS) ? (ChatColor.YELLOW + " (SELECTED)") : ""),
                        Collections.singletonList(ChatColor.GREEN + "Show only friends"));
                ItemStack none = ItemUtil.create(VisibilityUtil.Setting.ALL_HIDDEN.getBlock(),
                        VisibilityUtil.Setting.ALL_HIDDEN.getColor() + VisibilityUtil.Setting.ALL_HIDDEN.getText()
                                + (setting.equals(VisibilityUtil.Setting.ALL_HIDDEN) ? (ChatColor.YELLOW + " (SELECTED)") : ""),
                        Collections.singletonList(ChatColor.GREEN + "Hide all players"));
                switch (setting) {
                    case ALL_VISIBLE:
                        visible.addUnsafeEnchantment(Enchantment.LUCK, 1);
                        break;
                    case ONLY_STAFF_AND_FRIENDS:
                        staffFriends.addUnsafeEnchantment(Enchantment.LUCK, 1);
                        break;
                    case ONLY_FRIENDS:
                        friends.addUnsafeEnchantment(Enchantment.LUCK, 1);
                        break;
                    case ALL_HIDDEN:
                        none.addUnsafeEnchantment(Enchantment.LUCK, 1);
                        break;
                }
                List<MenuButton> buttons = Arrays.asList(
                        new MenuButton(10, visible,
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    if (ParkManager.getVisibilityUtil().setSetting(p, VisibilityUtil.Setting.ALL_VISIBLE, false)) {
                                        openInventory(p, BandInventory.VISIBILITY);
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                                    }
                                })),
                        new MenuButton(12, staffFriends,
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    if (ParkManager.getVisibilityUtil().setSetting(p, VisibilityUtil.Setting.ONLY_STAFF_AND_FRIENDS, false)) {
                                        openInventory(p, BandInventory.VISIBILITY);
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                                    }
                                })),
                        new MenuButton(14, friends,
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    if (ParkManager.getVisibilityUtil().setSetting(p, VisibilityUtil.Setting.ONLY_FRIENDS, false)) {
                                        openInventory(p, BandInventory.VISIBILITY);
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                                    }
                                })),
                        new MenuButton(16, none,
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    if (ParkManager.getVisibilityUtil().setSetting(p, VisibilityUtil.Setting.ALL_HIDDEN, false)) {
                                        openInventory(p, BandInventory.VISIBILITY);
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                                    }
                                })),
                        getBackButton(22, BandInventory.MAIN)
                );
                new Menu(27, ChatColor.BLUE + "Visibility Settings", player, buttons).open();
                break;
            }
            case TIMETABLE: {
                List<MenuButton> buttons = ParkManager.getScheduleManager().getButtons();
                buttons.add(getBackButton(49, BandInventory.SHOWS));
                new Menu(54, ChatColor.BLUE + "Show Timetable", player, buttons).open();
                break;
            }
        }
    }

    public ItemStack getMagicBandItem(String type, String color) {
        BandType bandType = BandType.fromString(type);
        ItemStack item;
        if (bandType.isColor()) {
            item = ItemUtil.create(Material.FIREWORK_STAR, getNameColor(color) + "MagicBand " + ChatColor.GRAY + "(Right-Click)");
            FireworkEffectMeta meta = (FireworkEffectMeta) item.getItemMeta();
            meta.setEffect(FireworkEffect.builder().withColor(getBandColor(bandType)).build());
            item.setItemMeta(meta);
        } else {
            item = null;
        }
        return item;
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
                return ChatColor.GREEN;
            case "darkgreen":
                return ChatColor.DARK_GREEN;
            case "blue":
                return ChatColor.BLUE;
            case "purple":
                return ChatColor.DARK_PURPLE;
            default:
                return ChatColor.GOLD;
        }
    }

    public MenuButton getBackButton(int slot, BandInventory inv) {
        return new MenuButton(slot, ItemUtil.create(Material.ARROW, ChatColor.GRAY + "Back"),
                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, inv)));
    }
}
