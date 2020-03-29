package network.palace.parkmanager.shop;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.core.economy.currency.CurrencyType;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.outfits.Outfit;
import network.palace.parkmanager.handlers.shop.Shop;
import network.palace.parkmanager.handlers.shop.ShopItem;
import network.palace.parkmanager.handlers.shop.ShopOutfit;
import network.palace.parkmanager.utils.FileUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.*;

@SuppressWarnings("unchecked")
public class ShopManager {
    private List<Shop> shops = new ArrayList<>();

    public ShopManager() {
        initialize();
    }

    public void initialize() {
        shops.clear();
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
                    String id;
                    if (object.has("id")) {
                        id = object.get("id").getAsString();
                    } else {
                        id = object.get("warp").getAsString().toLowerCase();
                    }

                    JsonArray shopItems = object.getAsJsonArray("items");
                    List<ShopItem> items = new ArrayList<>();
                    int nextId = 0;
                    for (JsonElement itemElement : shopItems) {
                        JsonObject itemObject = (JsonObject) itemElement;
                        items.add(new ShopItem(nextId++, ItemUtil.getItemFromJsonNew(itemObject.getAsJsonObject("item").toString()),
                                itemObject.get("cost").getAsInt(),
                                CurrencyType.fromString(itemObject.get("currency").getAsString())));
                    }

                    JsonArray shopOutfits = object.getAsJsonArray("outfits");
                    List<ShopOutfit> outfits = new ArrayList<>();
                    nextId = 0;
                    for (JsonElement outfitElement : shopOutfits) {
                        JsonObject outfitObject = (JsonObject) outfitElement;
                        outfits.add(new ShopOutfit(nextId++, outfitObject.get("outfit-id").getAsInt(), outfitObject.get("cost").getAsInt()));
                    }

                    shops.add(new Shop(id, ChatColor.translateAlternateColorCodes('&', object.get("name").getAsString()),
                            object.get("warp").getAsString(), ItemUtil.getItemFromJsonNew(object.getAsJsonObject("item").toString()), items, outfits));
                }
            }
            saveToFile();
            Core.logMessage("ShopManager", "Loaded " + shops.size() + " shop" + TextUtil.pluralize(shops.size()) + "!");
        } catch (IOException e) {
            Core.logMessage("ShopManager", "There was an error loading the ShopManager config!");
            e.printStackTrace();
        }
    }

    public List<Shop> getShops() {
        return new ArrayList<>(shops);
    }

    public Shop getShopById(String id) {
        for (Shop shop : getShops()) {
            if (shop.getId().equals(id)) {
                return shop;
            }
        }
        return null;
    }

    public Shop getShopByName(String s) {
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

    public boolean removeShop(String id) {
        Shop shop = getShopById(id);
        if (shop == null) return false;
        shops.remove(shop);
        saveToFile();
        return true;
    }

    public void openShopInventory(CPlayer player, Shop shop) {
        List<MenuButton> buttons = new ArrayList<>();
        List<ShopItem> shopItems = shop.getItems();
        List<ShopOutfit> shopOutfits = shop.getOutfits();

        boolean divider = !shopItems.isEmpty() && !shopOutfits.isEmpty();
        int pos = 0;

        int itemSize = 9;
        for (ShopItem shopItem : shopItems) {
            ItemStack item = shopItem.getItem();
            ItemMeta meta = item.getItemMeta();
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            meta.setLore(Arrays.asList("", ChatColor.YELLOW + "Cost: " + shopItem.getCurrencyType().getIcon() + shopItem.getCost()));
            item.setItemMeta(meta);

            if (pos != 0 && pos % 9 == 0) itemSize += 9;

            buttons.add(new MenuButton(pos++, item, ImmutableMap.of(ClickType.LEFT, p -> openConfirmItemPurchase(p, item, shopItem.getCost(), shopItem.getCurrencyType()))));
            if (itemSize > 27) {
                itemSize = 27;
                break;
            }
        }

        if (divider) {
            ItemStack dividerItem = ItemUtil.create(Material.STAINED_GLASS_PANE, ChatColor.RESET + "");
            pos = itemSize;
            for (int i = 0; i < 9; i++) {
                buttons.add(new MenuButton(pos++, dividerItem));
            }
        }

        int outfitSize = 9;
        int initialPos = pos;
        for (ShopOutfit shopOutfit : shopOutfits) {
            int outfitId = shopOutfit.getOutfitId();
            Outfit outfit = ParkManager.getWardrobeManager().getOutfit(outfitId);
            if (outfit == null) continue;

            ItemStack item = outfit.getHead().clone();
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(outfit.getName());
            meta.setLore(Arrays.asList("", ChatColor.YELLOW + "Cost: " + CurrencyType.TOKENS.getIcon() + shopOutfit.getCost()));
            item.setItemMeta(meta);

            if (pos != initialPos && pos % 9 == 0) outfitSize += 9;

            buttons.add(new MenuButton(pos++, item, ImmutableMap.of(ClickType.LEFT, p -> openConfirmOutfitPurchase(p, item, outfitId, shopOutfit.getCost()))));
            if (outfitSize > 18) {
                outfitSize = 18;
                break;
            }
        }

        int size = itemSize + outfitSize + (divider ? 9 : 0);
        new Menu(size, shop.getName(), player, buttons).open();
    }

    private void openConfirmOutfitPurchase(CPlayer player, ItemStack item, int outfitId, int cost) {
        List<Integer> currentPurchases = (List<Integer>) player.getRegistry().getEntry("outfitPurchases");
        if (currentPurchases.contains(outfitId)) {
            player.sendMessage(ChatColor.RED + "You already own this outfit!");
            return;
        }

        int tokens = player.getTokens();
        if (tokens < cost) {
            player.sendMessage(ChatColor.RED + "You cannot afford that outfit!");
            return;
        }

        List<MenuButton> buttons = Arrays.asList(
                new MenuButton(4, item),
                new MenuButton(11, ItemUtil.create(Material.CONCRETE, ChatColor.RED + "Decline Purchase", 14),
                        ImmutableMap.of(ClickType.LEFT, p -> {
                            p.closeInventory();
                            p.sendMessage(ChatColor.RED + "You cancelled the purchase");
                        })),
                new MenuButton(15, ItemUtil.create(Material.CONCRETE, 1, 13,
                        ChatColor.GREEN + "Confirm Purchase", Arrays.asList(ChatColor.GRAY + "You agree you will buy",
                                ChatColor.GRAY + "this shop outfit for " + ChatColor.AQUA + CurrencyType.TOKENS.getIcon() + cost)),
                        ImmutableMap.of(ClickType.LEFT, p -> {
                            p.closeInventory();
                            p.sendMessage(ChatColor.GREEN + "Processing your payment...");
                            Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
                                p.removeTokens(cost, "Park Store " + Core.getInstanceName());
                                p.sendMessage(ChatColor.GREEN + "Payment has been processed!");
                                List<Integer> purchases = (List<Integer>) player.getRegistry().getEntry("outfitPurchases");
                                purchases.add(outfitId);
                                Core.getMongoHandler().purchaseOutfit(p.getUniqueId(), outfitId);
                            });
                        }))
        );

        new Menu(27, ChatColor.BLUE + "Confirm Purchase", player, buttons).open();
    }

    private void openConfirmItemPurchase(CPlayer player, ItemStack item, int cost, CurrencyType currencyType) {
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
                new MenuButton(11, ItemUtil.create(Material.CONCRETE, ChatColor.RED + "Decline Purchase", 14),
                        ImmutableMap.of(ClickType.LEFT, p -> {
                            p.closeInventory();
                            p.sendMessage(ChatColor.RED + "You cancelled the purchase");
                        })),
                new MenuButton(15, ItemUtil.create(Material.CONCRETE, 1, 13,
                        ChatColor.GREEN + "Confirm Purchase", Arrays.asList(ChatColor.GRAY + "You agree you will buy",
                                ChatColor.GRAY + "this shop item for " + ChatColor.AQUA + currencyType.getIcon() + cost)),
                        ImmutableMap.of(ClickType.LEFT, p -> {
                            p.closeInventory();
                            p.sendMessage(ChatColor.GREEN + "Processing your payment...");
                            Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
                                if (currencyType.equals(CurrencyType.BALANCE)) {
                                    p.removeBalance(cost, "Park Store " + Core.getInstanceName());
                                } else {
                                    p.removeTokens(cost, "Park Store " + Core.getInstanceName());
                                }
                                p.sendMessage(ChatColor.GREEN + "Payment has been processed!");
                                Core.runTask(ParkManager.getInstance(), () -> p.getInventory().addItem(item));
                            });
                        }))
        );

        new Menu(27, ChatColor.BLUE + "Confirm Purchase", player, buttons).open();
    }

    public void saveToFile() {
        JsonArray array = new JsonArray();
        shops.sort(Comparator.comparing(o -> ChatColor.stripColor(o.getName().toLowerCase())));
        for (Shop shop : shops) {
            JsonObject object = new JsonObject();
            object.addProperty("name", shop.getName());
            object.addProperty("warp", shop.getWarp());
            object.add("item", ItemUtil.getJsonFromItemNew(shop.getItem()));

            JsonArray items = new JsonArray();
            for (ShopItem item : shop.getItems()) {
                JsonObject shopItem = new JsonObject();
                shopItem.add("item", ItemUtil.getJsonFromItemNew(item.getItem()));
                shopItem.addProperty("cost", item.getCost());
                shopItem.addProperty("currency", item.getCurrencyType().name().toLowerCase());
                items.add(shopItem);
            }
            object.add("items", items);

            JsonArray outfits = new JsonArray();
            for (ShopOutfit outfit : shop.getOutfits()) {
                JsonObject shopOutfit = new JsonObject();
                shopOutfit.addProperty("outfit-id", outfit.getOutfitId());
                shopOutfit.addProperty("cost", outfit.getCost());
                outfits.add(shopOutfit);
            }
            object.add("outfits", outfits);

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
