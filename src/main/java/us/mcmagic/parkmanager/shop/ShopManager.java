package us.mcmagic.parkmanager.shop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.InventoryType;
import us.mcmagic.parkmanager.handlers.Outfit;
import us.mcmagic.parkmanager.handlers.PlayerData;
import us.mcmagic.parkmanager.handlers.Warp;
import us.mcmagic.parkmanager.utils.BandUtil;
import us.mcmagic.parkmanager.utils.FileUtil;
import us.mcmagic.parkmanager.utils.WarpUtil;

import java.util.*;

/**
 * Created by Marc on 5/29/15
 */
public class ShopManager {
    private List<Shop> shops = new ArrayList<>();
    private HashMap<UUID, ShopItem> confirmations = new HashMap<>();
    private HashMap<UUID, OutfitItem> outfitConfirm = new HashMap<>();

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
            List<OutfitItem> outfits = new ArrayList<>();
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
                        config.getInt("items." + i + ".cost"), CurrencyType.fromString(config.getString("items." + i +
                        ".currency")));
                items.add(item);
            }
            for (Integer i : config.getIntegerList("shop." + s + ".outfits")) {
                outfits.add(new OutfitItem(i, config.getInt("outfits." + i + ".cost")));
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
            Shop shop = new Shop(display, loc, items, outfits, new ItemCreator(Material.getMaterial(id), 1, data,
                    display, Arrays.asList(" ", ChatColor.GREEN + "/warp " + warp.getName())), warp.getName(),
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
            lore.add(ChatColor.YELLOW + "Price: " + (balance >= cost ? ChatColor.GREEN : ChatColor.RED) +
                    item.getCurrencyType().getIcon() + cost);
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
        ItemStack storage = new ItemCreator(Material.CHEST, ChatColor.GREEN + "Storage Shop",
                Arrays.asList(ChatColor.YELLOW + "Purchase up to 3 extra rows in your ", ChatColor.AQUA + "Backpack " +
                        ChatColor.YELLOW + "and " + ChatColor.AQUA + "Locker " + ChatColor.YELLOW + "in this Shop!"));
        ItemStack custom = new ItemCreator(Material.FIREWORK_CHARGE, ChatColor.GREEN + "Custom MagicBands",
                Arrays.asList(ChatColor.YELLOW + "Select different MagicBands and ", ChatColor.YELLOW +
                        "find the one that is right for you!"));
        main.setItem(2, storage);
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
        if (category.equals(ShopCategory.WARDROBE)) {
            openWardrobe(player, shop);
            return;
        }
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
            lore.add(ChatColor.YELLOW + "Price: " + (balance >= cost ? ChatColor.GREEN : ChatColor.RED) +
                    item.getCurrencyType().getIcon() + cost);
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

    private void openWardrobe(Player player, Shop shop) {
        String title = ChatColor.GREEN + "Shop - " + shop.getName() + ChatColor.GREEN + " - " + "Wardrobe";
        if (title.length() > 32) {
            title = title.substring(0, 32);
        }
        Inventory inv = Bukkit.createInventory(player, 27, title);
        List<OutfitItem> outfits = shop.getOutfits();
        if (outfits.isEmpty()) {
            inv.setItem(13, new ItemCreator(Material.REDSTONE_BLOCK, ChatColor.RED +
                    "No items are currently available in this category!"));
            inv.setItem(22, BandUtil.getBackItem());
            player.openInventory(inv);
            return;
        }
        int balance = MCMagicCore.economy.getBalance(player.getUniqueId());
        int size = outfits.size();
        int place = 13;
        int amount = 1;
        //If even, increase place by 1
        if (size % 2 == 0) {
            place++;
            amount++;
        }
        for (OutfitItem item : outfits) {
            List<String> lore = new ArrayList<>();
            int cost = item.getCost();
            lore.add(ChatColor.YELLOW + "Price: " + (balance >= cost ? ChatColor.GREEN : ChatColor.RED) +
                    CurrencyType.TOKEN.getIcon() + cost);
            Outfit o = ParkManager.wardrobeManager.getOutfit(item.getOutfitId());
            ItemStack it = o.getHead() == null ? (o.getShirt() == null ? (o.getPants() == null ? o.getBoots() :
                    o.getPants()) : o.getShirt()) : o.getHead();
            ItemStack i = it.clone();
            ItemMeta m = i.getItemMeta();
            m.setDisplayName(o.getName());
            m.setLore(lore);
            i.setItemMeta(m);
            inv.setItem(place, i);
            if (amount % 2 == 0) {
                place -= amount;
            } else {
                place += amount;
            }
            amount++;
        }
        inv.setItem(22, BandUtil.getBackItem());
        player.openInventory(inv);
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
            String[] list = ChatColor.stripColor(sname).split(" - ");
            Shop shop = getShop(list[0]);
            if (stack.equals(BandUtil.getBackItem())) {
                openMenu(player, shop);
                return;
            }
            ShopCategory category = ShopCategory.fromString(list[1]);
            if (category.equals(ShopCategory.WARDROBE)) {
                purchaseOutfit(player, shop, name, event);
                return;
            }
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
                ParkManager.inventoryUtil.openInventory(player, InventoryType.MAINMENU);
                return;
            }
            Shop shop = getShop(sname);
            openCategory(player, shop, ShopCategory.fromString(name));
        }
    }

    private void purchaseOutfit(Player player, Shop shop, String name, InventoryClickEvent event) {
        List<OutfitItem> items = new ArrayList<>();
        for (OutfitItem item : shop.getOutfits()) {
            items.add(item);
        }
        Outfit ou = null;
        OutfitItem item = null;
        for (Outfit o : ParkManager.wardrobeManager.getOutfits()) {
            if (ChatColor.stripColor(o.getName()).equalsIgnoreCase(name)) {
                ou = o;
                break;
            }
        }
        if (ou == null) {
            return;
        }
        for (OutfitItem i : items) {
            if (i.getOutfitId() == ou.getId()) {
                item = i;
            }
        }
        if (item == null) {
            return;
        }
        openConfirm(player, item);
    }

    public void cancelPurchase(Player player) {
        confirmations.remove(player.getUniqueId());
    }

    public void confirmPurchase(final Player player) {
        PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
        if (confirmations.containsKey(player.getUniqueId())) {
            ShopItem item = confirmations.remove(player.getUniqueId());
            int balance = MCMagicCore.economy.getBalance(player.getUniqueId());
            if (balance < item.getCost()) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You cannot afford that item!");
                player.closeInventory();
                return;
            }
            PlayerInventory inv = player.getInventory();
            boolean full = true;
            ItemStack[] cont = inv.getContents();
            for (int i = 0; i < 5; i++) {
                if (cont[i] == null || cont[i].getType().equals(Material.AIR)) {
                    full = false;
                    break;
                }
            }
            if (full) {
                ItemStack[] pack = data.getBackpack().getInventory().getContents();
                for (ItemStack i : pack) {
                    if (i == null || i.getType().equals(Material.AIR)) {
                        full = false;
                        break;
                    }
                }
                if (!full) {
                    data.getBackpack().getInventory().addItem(item.getItem());
                } else {
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "You have no available Inventory Space! Make some space before buying this.");
                    return;
                }
            } else {
                player.getInventory().addItem(item.getItem());
            }
            switch (item.getCurrencyType()) {
                case MONEY:
                    MCMagicCore.economy.addBalance(player.getUniqueId(), -item.getCost(),
                            MCMagicCore.getMCMagicConfig().serverName + " Store");
                    break;
                case TOKEN:
                    MCMagicCore.economy.addTokens(player.getUniqueId(), -item.getCost(),
                            MCMagicCore.getMCMagicConfig().serverName + " Store");
                    break;
            }
            player.sendMessage(ChatColor.GREEN + "You have successfully purchased " + item.getDisplayName() +
                    ChatColor.GREEN + "!");
            player.closeInventory();
        } else if (outfitConfirm.containsKey(player.getUniqueId())) {
            OutfitItem item = outfitConfirm.remove(player.getUniqueId());
            final Outfit o = ParkManager.wardrobeManager.getOutfit(item.getOutfitId());
            int tkn = MCMagicCore.economy.getTokens(player.getUniqueId());
            if (tkn < item.getCost()) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You cannot afford that item!");
                player.closeInventory();
                return;
            }
            if (data.getPurchases().contains(o.getId())) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "You already own that Outfit!");
                return;
            }
            MCMagicCore.economy.addTokens(player.getUniqueId(), -item.getCost(),
                    MCMagicCore.getMCMagicConfig().serverName + " Store");
            player.sendMessage(ChatColor.GREEN + "You have successfully purchased the " + o.getName() + " Outfit" +
                    ChatColor.GREEN + "!");
            player.closeInventory();
            data.addPurchase(o.getId());
            Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), new Runnable() {
                @Override
                public void run() {
                    ParkManager.wardrobeManager.purchaseOutfit(player, o.getId());
                }
            });
        }
    }

    public void openConfirm(Player player, Object item) {
        if (item instanceof ShopItem) {
            ShopItem itm = (ShopItem) item;
            Inventory inv = Bukkit.createInventory(player, 27, ChatColor.GREEN + "Shop - " + ChatColor.RED + "Confirm");
            ItemStack name = new ItemCreator(Material.WOOL, 1, (byte) 9, ChatColor.GREEN + "Please confirm your Purchase.",
                    Arrays.asList(ChatColor.GREEN + "You are about to purchase", itm.getDisplayName(), ChatColor.GREEN +
                            "for " + ChatColor.YELLOW + itm.getCurrencyType().getIcon() + itm.getCost() + ChatColor.GREEN + "."));
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
            confirmations.put(player.getUniqueId(), itm);
            player.openInventory(inv);
        } else if (item instanceof OutfitItem) {
            OutfitItem itm = (OutfitItem) item;
            Outfit o = ParkManager.wardrobeManager.getOutfit(itm.getOutfitId());
            Inventory inv = Bukkit.createInventory(player, 27, ChatColor.GREEN + "Shop - " + ChatColor.RED + "Confirm");
            ItemStack name = new ItemCreator(Material.WOOL, 1, (byte) 9, ChatColor.GREEN + "Please confirm your Purchase.",
                    Arrays.asList(ChatColor.GREEN + "You are about to purchase the ", o.getName() + " Outfit", ChatColor.GREEN +
                            "for " + ChatColor.YELLOW + CurrencyType.TOKEN.getIcon() + itm.getCost() + ChatColor.GREEN + "."));
            ItemStack yes = new ItemCreator(Material.WOOL, 1, (byte) 13, ChatColor.GREEN + "Confirm Purchase",
                    Collections.singletonList(ChatColor.GRAY + "This cannot be undone!"));
            ItemStack no = new ItemCreator(Material.WOOL, 1, (byte) 14, ChatColor.RED + "Cancel Purchase",
                    new ArrayList<String>());
            inv.setItem(4, name);
            inv.setItem(11, no);
            inv.setItem(15, yes);
            if (outfitConfirm.containsKey(player.getUniqueId())) {
                outfitConfirm.remove(player.getUniqueId());
            }
            outfitConfirm.put(player.getUniqueId(), itm);
            player.openInventory(inv);
        }
    }
}