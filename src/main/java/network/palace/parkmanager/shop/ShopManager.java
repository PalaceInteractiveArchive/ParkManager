package network.palace.parkmanager.shop;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.core.economy.CurrencyType;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.utils.FileUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ShopManager {
    private int nextId = 0;
    private List<Shop> shops = new ArrayList<>();

    public ShopManager() {
        initialize();
    }

    public void initialize() {
        shops.clear();
        nextId = 0;
        FileUtil.FileSubsystem subsystem;
        if (ParkManager.getFileUtil().isSubsystemRegistered("shop")) {
            subsystem = ParkManager.getFileUtil().getSubsystem("shop");
        } else {
            subsystem = ParkManager.getFileUtil().registerSubsystem("shop");
        }
        try {
            JsonElement element = subsystem.getFileContents("shops");
            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (JsonElement entry : array) {
                    JsonObject object = entry.getAsJsonObject();

                    JsonArray shopItems = object.getAsJsonArray("items");
                    List<ShopItem> items = new ArrayList<>();
                    for (JsonElement shopElement : shopItems) {
                        JsonObject shopObject = (JsonObject) shopElement;
                        items.add(new ShopItem(ItemUtil.getItemFromJson(shopObject.getAsJsonObject("item").toString()),
                                shopObject.get("cost").getAsInt(),
                                CurrencyType.fromString(shopObject.get("currency").getAsString())));
                    }

                    shops.add(new Shop(nextId++, ChatColor.translateAlternateColorCodes('&', object.get("name").getAsString()),
                            object.get("warp").getAsString(), ItemUtil.getItemFromJson(object.getAsJsonObject("item").toString()), items));
                }
            } else {
                saveToFile();
            }
            Core.logMessage("ShopManager", "Loaded " + shops.size() + " shop" + TextUtil.pluralize(shops.size()) + "!");
        } catch (IOException e) {
            Core.logMessage("ShopManager", "There was an error loading the ShopManager config!");
            e.printStackTrace();
        }
    }

    public List<Shop> getShops() {
        return new ArrayList<>(shops);
    }

    public int getNextId() {
        return nextId++;
    }

    public Shop getShop(int id) {
        for (Shop shop : getShops()) {
            if (shop.getId() == id) {
                return shop;
            }
        }
        return null;
    }

    public Shop getShop(String s) {
        for (Shop shop : getShops()) {
            if (shop.getName().contains(s)) {
                return shop;
            }
        }
        return null;
    }

    public void addShop(Shop shop) {
        shops.add(shop);
        saveToFile();
    }

    public boolean removeShop(int id) {
        Shop shop = getShop(id);
        if (shop == null) return false;
        shops.remove(shop);
        saveToFile();
        return true;
    }

    public void openShopInventory(CPlayer player, Shop shop) {
        List<MenuButton> buttons = new ArrayList<>();
        int i = 0;
        int size = 18;
        for (ShopItem shopItem : shop.getItems()) {
            ItemStack item = shopItem.getItem();
            ItemMeta meta = item.getItemMeta();
            meta.setLore(Arrays.asList("", ChatColor.YELLOW + "Cost: " + shopItem.getCurrencyType().getIcon() + shopItem.getCost()));
            item.setItemMeta(meta);
            if (i != 0 && i % 9 == 0) {
                size += 9;
            }
            if (size > 54) {
                size = 54;
                break;
            }
            buttons.add(new MenuButton(i++, item, ImmutableMap.of(ClickType.LEFT, p -> openConfirmPurchase(p, item, shopItem.getCost(), shopItem.getCurrencyType()))));
        }
        new Menu(size, shop.getName(), player, buttons).open();
    }

    private void openConfirmPurchase(CPlayer player, ItemStack item, int cost, CurrencyType currencyType) {
        boolean openSlot = false;
        PlayerInventory inv = player.getInventory();
        for (int i = 0; i < 5; i++) {
            ItemStack itemStack = inv.getItem(i);
            if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
                openSlot = true;
                break;
            }
        }

        if (!openSlot) {
            player.sendMessage(ChatColor.RED + "You don't have an open inventory slot for this item! Clear up some space in your hotbar before you buy it.");
            return;
        }

        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        meta.setLore(Collections.emptyList());
        item.setItemMeta(meta);

        if (currencyType.equals(CurrencyType.BALANCE)) {
            int balance = player.getBalance();
            if (balance < cost) {
                player.sendMessage(ChatColor.RED + "You cannot afford that item!");
                return;
            }
        } else {
            int tokens = player.getTokens();
            if (tokens < cost) {
                player.sendMessage(ChatColor.RED + "You cannot afford that item!");
                return;
            }
        }

        List<MenuButton> buttons = Arrays.asList(
                new MenuButton(4, item),
                new MenuButton(11, ItemUtil.create(Material.RED_CONCRETE, ChatColor.RED + "Decline Purchase"),
                        ImmutableMap.of(ClickType.LEFT, p -> {
                            p.closeInventory();
                            p.sendMessage(ChatColor.RED + "You cancelled the purchase");
                        })),
                new MenuButton(15, ItemUtil.create(Material.GREEN_CONCRETE, ChatColor.GREEN + "Confirm Purchase",
                        Arrays.asList(ChatColor.GRAY + "You agree you will buy",
                                ChatColor.GRAY + "this shop item for " + ChatColor.AQUA + currencyType.getIcon() + cost)),
                        ImmutableMap.of(ClickType.LEFT, p -> {
                            p.closeInventory();
                            p.sendMessage(ChatColor.GREEN + "Processing your payment...");
                            Core.runTaskAsynchronously(() -> {
                                if (currencyType.equals(CurrencyType.BALANCE)) {
                                    p.removeBalance(cost, "Park Store");
                                } else {
                                    p.removeTokens(cost, "Park Store");
                                }
                                p.sendMessage(ChatColor.GREEN + "Payment has been processed!");
                                p.getInventory().addItem(item);
                            });
                        }))
        );

        new Menu(27, ChatColor.BLUE + "Purchase Confirm", player, buttons).open();
    }

    public void saveToFile() {
        JsonArray array = new JsonArray();
        for (Shop shop : shops) {
            JsonObject object = new JsonObject();
            object.addProperty("name", shop.getName());
            object.addProperty("warp", shop.getWarp());
            object.add("item", ItemUtil.getJsonFromItem(shop.getItem()));

            JsonArray items = new JsonArray();
            for (ShopItem item : shop.getItems()) {
                JsonObject shopItem = new JsonObject();
                shopItem.add("item", ItemUtil.getJsonFromItem(item.getItem()));
                shopItem.addProperty("cost", item.getCost());
                shopItem.addProperty("currency", item.getCurrencyType().name().toLowerCase());
                items.add(shopItem);
            }
            object.add("items", items);

            array.add(object);
        }
        try {
            ParkManager.getFileUtil().getSubsystem("shop").writeFileContents("shops", array);
        } catch (IOException e) {
            Core.logMessage("ShopManager", "There was an error writing to the ShopManager config!");
            e.printStackTrace();
        }
    }
}
