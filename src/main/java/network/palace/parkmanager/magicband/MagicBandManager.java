package network.palace.parkmanager.magicband;

import com.google.common.collect.ImmutableMap;
import network.palace.core.Core;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.HeadUtil;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.food.FoodLocation;
import network.palace.parkmanager.handlers.magicband.BandType;
import network.palace.parkmanager.utils.VisibilityUtil;
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
                        new MenuButton(2, ItemUtil.create(Material.LIGHT_BLUE_BED, ChatColor.GREEN + "Hotels and Resorts",
                                Arrays.asList(ChatColor.GREEN + "Visit and rent a room", ChatColor.GREEN + "from a Resort Hotel!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.HOTELS))),
                        new MenuButton(4, HeadUtil.getPlayerHead(player.getTextureValue(), ChatColor.GREEN + "My Profile"),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.PROFILE))),
                        new MenuButton(10, ItemUtil.create(Material.POTATO, ChatColor.GREEN + "Find Food",
                                Arrays.asList(ChatColor.GREEN + "Visit a restaurant", ChatColor.GREEN + "to get some food!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.FOOD))),
                        new MenuButton(11, ItemUtil.create(Material.FIREWORK_ROCKET, ChatColor.GREEN + "Shows and Events",
                                Arrays.asList(ChatColor.GREEN + "Watch stage shows, nighttime", ChatColor.GREEN + "spectaculars, and much more!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.SHOWS))),
                        new MenuButton(12, ItemUtil.create(Material.MINECART, ChatColor.GREEN + "Attractions",
                                Arrays.asList(ChatColor.GREEN + "View all of our available", ChatColor.GREEN + "theme park attractions!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.ATTRACTIONS))),
                        new MenuButton(13, ItemUtil.create(Material.NETHER_STAR, ChatColor.GREEN + "Park Menu",
                                Arrays.asList(ChatColor.GREEN + "Travel to all of our", ChatColor.GREEN + "theme park recreations!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.PARKS))),
                        new MenuButton(14, ItemUtil.create(Material.GOLDEN_BOOTS, ChatColor.GREEN + "Shop",
                                Arrays.asList(ChatColor.GREEN + "Purchase souveniers and", ChatColor.GREEN + "all kinds of collectibles!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.SHOP))),
                        new MenuButton(15, ItemUtil.create(Material.IRON_CHESTPLATE, ChatColor.GREEN + "Wardrobe Manager",
                                Arrays.asList(ChatColor.GREEN + "Change your outfit to make you", ChatColor.GREEN + "look like your favorite characters!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.WARDROBE))),
                        new MenuButton(16, ItemUtil.create(setting.getBlock(), ChatColor.AQUA + "Guest Visibility " +
                                        ChatColor.GOLD + "➠ " + setting.getColor() + setting.getText(),
                                Arrays.asList(ChatColor.YELLOW + "Right-Click to " + (setting.equals(VisibilityUtil.Setting.ALL_HIDDEN) ? "show" : "hide") + " all players",
                                        ChatColor.YELLOW + "Left-Click for more options")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.VISIBILITY), ClickType.RIGHT, p -> {
                                    ParkManager.getVisibilityUtil().toggleVisibility(player);
                                    openInventory(p, BandInventory.MAIN);
                                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                                }))
                );

                new Menu(Core.createInventory(27, ChatColor.BLUE + "Your MagicBand"),
                        ChatColor.BLUE + "Your MagicBand", player, buttons).open();
                break;
            }
            case FOOD: {
                List<MenuButton> buttons = new ArrayList<>(Collections.singletonList(getBackButton(22, BandInventory.MAIN)));
                int i = 10;
                int size = 27;
                for (FoodLocation food : ParkManager.getFoodManager().getFoodLocations()) {
                    ItemStack item = food.getItem();
                    ItemMeta meta = item.getItemMeta();
                    meta.setLore(Arrays.asList("", ChatColor.YELLOW + "/warp " + food.getWarp()));
                    item.setItemMeta(meta);
                    buttons.add(new MenuButton(i, item, ImmutableMap.of(ClickType.LEFT, p -> p.performCommand("warp " + food.getWarp()))));
                    if (i++ >= (size - 10)) {
                        i += 2;
                        size += 9;
                    }
                    if (size > 54) {
                        size = 54;
                        break;
                    }
                }
                new Menu(Core.createInventory(size, ChatColor.BLUE + "Food Locations"),
                        ChatColor.BLUE + "Food Locations", player, buttons).open();
                break;
            }
            case SHOWS: {
                new Menu(Core.createInventory(27, ChatColor.BLUE + "Shows and Events"),
                        ChatColor.BLUE + "Shows and Events", player, Arrays.asList(
                        new MenuButton(8, ItemUtil.create(Material.BOOK, ChatColor.GREEN + "Show Timetable"),
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
            case ATTRACTIONS: {
                new Menu(Core.createInventory(27, ChatColor.BLUE + "Attractions List"),
                        ChatColor.BLUE + "Attractions List", player, Collections.singletonList(getBackButton(22, BandInventory.MAIN))).open();
                break;
            }
            case PARKS: {
                new Menu(Core.createInventory(27, ChatColor.BLUE + "Park Menu"),
                        ChatColor.BLUE + "Park Menu", player, Collections.singletonList(getBackButton(22, BandInventory.MAIN))).open();
                break;
            }
            case SHOP: {
                new Menu(Core.createInventory(27, ChatColor.BLUE + "Shop List"),
                        ChatColor.BLUE + "Shop List", player, Collections.singletonList(getBackButton(22, BandInventory.MAIN))).open();
                break;
            }
            case WARDROBE: {
                new Menu(Core.createInventory(27, ChatColor.BLUE + "Wardrobe Manager"),
                        ChatColor.BLUE + "Wardrobe Manager", player, Collections.singletonList(getBackButton(22, BandInventory.MAIN))).open();
                break;
            }
            case HOTELS: {
                new Menu(Core.createInventory(27, ChatColor.BLUE + "Hotels and Resorts"),
                        ChatColor.BLUE + "Hotels and Resorts", player, Collections.singletonList(getBackButton(22, BandInventory.MAIN))).open();
                break;
            }
            case PROFILE: {
                new Menu(Core.createInventory(27, ChatColor.BLUE + "My Profile"),
                        ChatColor.BLUE + "My Profile", player, Collections.singletonList(getBackButton(22, BandInventory.MAIN))).open();
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
                                    ParkManager.getVisibilityUtil().setSetting(p, VisibilityUtil.Setting.ALL_VISIBLE);
                                    openInventory(p, BandInventory.VISIBILITY);
                                })),
                        new MenuButton(12, staffFriends,
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    ParkManager.getVisibilityUtil().setSetting(p, VisibilityUtil.Setting.ONLY_STAFF_AND_FRIENDS);
                                    openInventory(p, BandInventory.VISIBILITY);
                                })),
                        new MenuButton(14, friends,
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    ParkManager.getVisibilityUtil().setSetting(p, VisibilityUtil.Setting.ONLY_FRIENDS);
                                    openInventory(p, BandInventory.VISIBILITY);
                                })),
                        new MenuButton(16, none,
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    ParkManager.getVisibilityUtil().setSetting(p, VisibilityUtil.Setting.ALL_HIDDEN);
                                    openInventory(p, BandInventory.VISIBILITY);
                                })),
                        getBackButton(22, BandInventory.MAIN)
                );
                new Menu(Core.createInventory(27, ChatColor.BLUE + "Visibility Settings"),
                        ChatColor.BLUE + "Visibility Settings", player, buttons).open();
                break;
            }
            case TIMETABLE: {
                List<MenuButton> buttons = ParkManager.getScheduleManager().getButtons();
                buttons.add(getBackButton(49, BandInventory.SHOWS));
                new Menu(Core.createInventory(54, ChatColor.BLUE + "Show Timetable"),
                        ChatColor.BLUE + "Show Timetable", player, buttons).open();
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
