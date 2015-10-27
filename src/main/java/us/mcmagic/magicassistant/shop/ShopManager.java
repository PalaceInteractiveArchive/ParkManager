package us.mcmagic.magicassistant.shop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.InventoryType;
import us.mcmagic.magicassistant.handlers.Warp;
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.magicassistant.utils.FileUtil;
import us.mcmagic.magicassistant.utils.WarpUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;

import java.util.*;

/**
 * Created by Marc on 5/29/15
 */
public class ShopManager {
    private List<Shop> shops = new ArrayList<>();
    private HashMap<UUID, ShopItem> confirmations = new HashMap<>();

    public ShopManager() {
        initialize();
    }

    @SuppressWarnings("deprecation")
    public void initialize() {
        YamlConfiguration config = FileUtil.shopsYaml();
        shops.clear();
        for (String s : config.getStringList("shops")) {
            List<String> lore = new ArrayList<>();
            for (String ss : config.getStringList("items." + s + ".lore")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', ss));
            }
            List<ShopItem> items = new ArrayList<>();
            for (String i : config.getStringList("shop." + s + ".items")) {
                int id = 0;
                byte data = 0;
                String[] list = config.getString("items." + i + ".id").split(":");
                if (list.length == 1) {
                    id = Integer.parseInt(list[0]);
                } else {
                    id = Integer.parseInt(list[0]);
                    data = Byte.parseByte(list[1]);
                }
                ShopItem item = new ShopItem(ChatColor.translateAlternateColorCodes('&', ChatColor.RESET +
                        config.getString("items." + i + ".name")), ShopCategory.fromString(config.getString("items." +
                        i + ".category")), id, data, lore,
                        config.getInt("items." + i + ".cost"));
                items.add(item);
            }
            Warp warp = WarpUtil.findWarp(config.getString("shop." + s + ".warp"));
            if (warp == null) {
                continue;
            }
            String display = ChatColor.translateAlternateColorCodes('&', config.getString("shop." + s + ".display"));
            int id = 0;
            byte data = 0;
            String[] list = config.getString("shop." + s + ".id").split(":");
            if (list.length == 1) {
                id = Integer.parseInt(list[0]);
            } else {
                id = Integer.parseInt(list[0]);
                data = Byte.parseByte(list[1]);
            }
            Location loc = new Location(Bukkit.getWorlds().get(0), config.getDouble("shop." + s + ".x"),
                    config.getDouble("shop." + s + ".y"), config.getDouble("shop." + s + ".z"));
            Shop shop = new Shop(display, loc, items, new ItemCreator(Material.getMaterial(id), 1, data, display,
                    Arrays.asList(" ", ChatColor.GREEN + "/warp " + warp.getName())), warp.getName(),
                    config.getDouble("shop." + s + ".radius"));
            shops.add(shop);
        }
    }

    public boolean isNearShop(Player player, Shop shop, int distance) {
        return shop.getLocation().distance(player.getLocation()) <= distance;
    }

    @SuppressWarnings("deprecation")
    public Inventory getShopMenu(Player player, Shop shop) {
        List<ShopItem> items = shop.getItems();
        Inventory menu = Bukkit.createInventory(player, 27, ChatColor.GREEN + "Shop - " + shop.getName());
        menu.setItem(22, BandUtil.getBackItem());
        if (items.isEmpty()) {
            menu.setItem(13, new ItemCreator(Material.REDSTONE_BLOCK, ChatColor.RED + "No items are available in this shop!"));
            return menu;
        }
        int balance = MCMagicCore.economy.getBalance(player.getUniqueId());
        int size = items.size();
        int place = 13;
        int amount = 1;
        //If even, increase place by 1
        if (size % 2 == 0) {
            place++;
            amount++;
        }
        for (ShopItem item : items) {
            List<String> lore = new ArrayList<>(item.getLore());
            int cost = item.getCost();
            lore.add(" ");
            lore.add(ChatColor.YELLOW + "Price: " + (balance >= cost ? ChatColor.GREEN : ChatColor.RED) + "$" + cost);
            ItemStack i = new ItemCreator(Material.getMaterial(item.getId()), 1, item.getData(), item.getDisplayName(),
                    lore);
            menu.setItem(place, i);
            if (amount % 2 == 0) {
                place -= amount;
            } else {
                place += amount;
            }
            amount++;
        }
        return menu;
    }

    public Inventory getMenu(Player player) {
        Inventory main = Bukkit.createInventory(player, 27, ChatColor.GREEN + "Shop");
        main.setItem(22, BandUtil.getBackItem());
        ItemStack fp = new ItemCreator(Material.CLAY_BRICK, ChatColor.GREEN + "FastPass Shop",
                Arrays.asList(ChatColor.YELLOW + "Purchase FastPasses and use them", ChatColor.YELLOW +
                        "on rides to join a shorter line!", ChatColor.RED + "Limit: 3 FP purchases per day"));
        ItemStack storage = new ItemCreator(Material.CHEST, ChatColor.GREEN + "Storage Shop",
                Arrays.asList(ChatColor.YELLOW + "Purchase up to 3 extra rows in your ", ChatColor.AQUA + "Backpack " +
                        ChatColor.YELLOW + "and " + ChatColor.AQUA + "Locker " + ChatColor.YELLOW + "in this Shop!"));
        ItemStack custom = new ItemCreator(Material.FIREWORK_CHARGE, ChatColor.GREEN + "Custom MagicBands",
                Arrays.asList(ChatColor.YELLOW + "Select different MagicBands and ", ChatColor.YELLOW +
                        "find the one that is right for you!"));
        main.setItem(2, fp);
        main.setItem(4, storage);
        main.setItem(6, custom);
        if (shops.isEmpty()) {
            main.setItem(13, new ItemCreator(Material.REDSTONE_BLOCK, ChatColor.RED + "No shops are available!"));
            return main;
        }
        int size = shops.size();
        int place = 13;
        int amount = 1;
        //If even, increase place by 1
        if (size % 2 == 0) {
            place++;
            amount++;
        }
        for (Shop s : shops) {
            ItemStack item = new ItemStack(s.getIdentifier());
            main.setItem(place, item);
            if (amount % 2 == 0) {
                place -= amount;
            } else {
                place += amount;
            }
            amount++;
        }
        return main;
    }

    public Shop getShop(String name) {
        for (Shop s : shops) {
            if (s.getName().equalsIgnoreCase(name)) {
                return s;
            } else if (ChatColor.stripColor(s.getName()).equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public void openCategory(Player player, Shop shop, ShopCategory category) {
        List<ShopItem> items = new ArrayList<>();
        for (ShopItem item : shop.getItems()) {
            if (item.getCategory().equals(category)) {
                items.add(item);
            }
        }
        String title = ChatColor.GREEN + "Shop - " + shop.getName() + ChatColor.GREEN + " - " + category.getName();
        if (title.length() > 32) {
            title = title.substring(0, 32);
        }
        Inventory menu = Bukkit.createInventory(player, 27, title);
        if (items.isEmpty()) {
            menu.setItem(13, new ItemCreator(Material.REDSTONE_BLOCK, ChatColor.RED +
                    "No items are currently available in this category!"));
            menu.setItem(22, BandUtil.getBackItem());
            player.openInventory(menu);
            return;
        }
        int balance = MCMagicCore.economy.getBalance(player.getUniqueId());
        int size = items.size();
        int place = 13;
        int amount = 1;
        //If even, increase place by 1
        if (size % 2 == 0) {
            place++;
            amount++;
        }
        for (ShopItem item : items) {
            List<String> lore = new ArrayList<>(item.getLore());
            int cost = item.getCost();
            lore.add(" ");
            lore.add(ChatColor.YELLOW + "Price: " + (balance >= cost ? ChatColor.GREEN : ChatColor.RED) + "$" + cost);
            ItemStack i = new ItemCreator(Material.getMaterial(item.getId()), 1, item.getData(), item.getDisplayName(),
                    lore);
            menu.setItem(place, i);
            if (amount % 2 == 0) {
                place -= amount;
            } else {
                place += amount;
            }
            amount++;
        }
        menu.setItem(22, BandUtil.getBackItem());
        player.openInventory(menu);
    }

    public void openMenu(Player player) {
        Shop shop = null;
        for (Shop s : shops) {
            if (isNearShop(player, s, 20)) {
                shop = s;
                break;
            }
        }
        if (shop == null) {
            Inventory main = getMenu(player);
            player.openInventory(main);
            return;
        }
        openMenu(player, shop);
    }

    public void openMenu(Player player, Shop shop) {
        Inventory inv = Bukkit.createInventory(player, 27, ChatColor.GREEN + "Shop - " + shop.getName());
        inv.setItem(11, ShopCategory.WARDROBE.getStack());
        inv.setItem(13, ShopCategory.TOYS.getStack());
        inv.setItem(15, ShopCategory.DOLLS.getStack());
        inv.setItem(22, BandUtil.getBackItem());
        player.openInventory(inv);
    }

    public void openMenu(Player player, String shopName) {
        Shop shop = getShop(shopName);
        if (shop == null) {
            player.sendMessage(ChatColor.RED + "There was an error opening that Shop menu!");
            return;
        }
        openMenu(player, shop);
    }

    public void handleClick(InventoryClickEvent event, String sname) {
        ItemStack stack = event.getCurrentItem();
        if (stack == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        if (sname.contains("-")) {
            if (stack.equals(BandUtil.getBackItem())) {
                openMenu(player);
                return;
            }
            String[] list = ChatColor.stripColor(sname).split(" - ");
            Shop shop = getShop(list[0]);
            ShopCategory category = ShopCategory.fromString(list[1]);
            List<ShopItem> items = new ArrayList<>();
            for (ShopItem item : shop.getItems()) {
                if (item.getCategory().equals(category)) {
                    items.add(item);
                }
            }
            ShopItem item = null;
            for (ShopItem i : items) {
                if (ChatColor.stripColor(i.getDisplayName()).equalsIgnoreCase(name)) {
                    item = i;
                    break;
                }
            }
            if (item == null) {
                return;
            }
            openConfirm(player, item);
        } else {
            if (stack.equals(BandUtil.getBackItem())) {
                MagicAssistant.inventoryUtil.openInventory(player, InventoryType.MAINMENU);
                return;
            }
            Shop shop = getShop(sname);
            openCategory(player, shop, ShopCategory.fromString(name));
        }
    }

    public void cancelPurchase(Player player) {
        confirmations.remove(player.getUniqueId());
    }

    public void confirmPurchase(Player player) {
        if (confirmations.containsKey(player.getUniqueId())) {
            ShopItem item = confirmations.remove(player.getUniqueId());
            int balance = MCMagicCore.economy.getBalance(player.getUniqueId());
            if (balance < item.getCost()) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You cannot afford that item!");
                player.closeInventory();
                return;
            }
            MCMagicCore.economy.addBalance(player.getUniqueId(), -item.getCost());
            player.sendMessage(ChatColor.GREEN + "You have successfully purchased " + item.getDisplayName() +
                    ChatColor.GREEN + "!");
            player.closeInventory();
            player.getInventory().addItem(item.getItem());
        }
    }

    public void openConfirm(Player player, ShopItem item) {
        Inventory inv = Bukkit.createInventory(player, 27, ChatColor.GREEN + "Shop - " + ChatColor.RED + "Confirm");
        ItemStack name = new ItemCreator(Material.WOOL, 1, (byte) 9, ChatColor.GREEN + "Please confirm your Purchase.",
                Arrays.asList(ChatColor.GREEN + "You are about to purchase", item.getDisplayName(), ChatColor.GREEN +
                        "for " + ChatColor.YELLOW + "$" + item.getCost() + ChatColor.GREEN + "."));
        ItemStack yes = new ItemCreator(Material.WOOL, 1, (byte) 13, ChatColor.GREEN + "Confirm Purchase",
                Collections.singletonList(ChatColor.GRAY + "This cannot be undone!"));
        ItemStack no = new ItemCreator(Material.WOOL, 1, (byte) 14, ChatColor.RED + "Cancel Purchase",
                new ArrayList<String>());
        inv.setItem(4, name);
        inv.setItem(11, no);
        inv.setItem(15, yes);
        if (confirmations.containsKey(player.getUniqueId())) {
            confirmations.remove(player.getUniqueId());
        }
        confirmations.put(player.getUniqueId(), item);
        player.openInventory(inv);
    }
}