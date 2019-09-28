package network.palace.parkmanager.wardrobe;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.io.NbtTextSerializer;
import com.google.common.collect.ImmutableMap;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import network.palace.core.Core;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.outfits.Clothing;
import network.palace.parkmanager.handlers.outfits.Outfit;
import network.palace.parkmanager.handlers.outfits.OutfitSlot;
import network.palace.parkmanager.magicband.BandInventory;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class WardrobeManager {
    private HashMap<Integer, Outfit> outfits = new HashMap<>();
    private boolean initialStarted = false;

    public WardrobeManager() {
        initialize();
    }

    public void initialize() {
        outfits.clear();
        boolean convert = false;
        for (Document doc : Core.getMongoHandler().getOutfits(ParkManager.getResort().getId())) {
            if (!doc.containsKey("shirtJSON")) {
                Core.logMessage("WardrobeManager", "Converting outfits!");
                convert = true;
                break;
            }
            outfits.put(doc.getInteger("id"), new Outfit(doc.getInteger("id"),
                    ChatColor.translateAlternateColorCodes('&', doc.getString("name")),
                    ItemUtil.getItemFromJsonNew(doc.getString("headJSON")),
                    ItemUtil.getItemFromJsonNew(doc.getString("shirtJSON")),
                    ItemUtil.getItemFromJsonNew(doc.getString("pantsJSON")),
                    ItemUtil.getItemFromJsonNew(doc.getString("bootsJSON"))));
        }
        if (convert) {
            outfits.clear();
            for (Document doc : Core.getMongoHandler().getOutfits(ParkManager.getResort().getId())) {
                if (doc.containsKey("seq")) continue;
                Outfit outfit = getLegacyOutfit(doc);
                outfits.put(outfit.getId(), outfit);
            }
            MongoCollection<Document> outfitsCollection = Core.getMongoHandler().getDatabase().getCollection("outfits");
            for (Outfit outfit : outfits.values()) {
                outfitsCollection.findOneAndUpdate(Filters.eq("id", outfit.getId()),
                        new Document("$set", new Document("headJSON", ItemUtil.getJsonFromItemNew(outfit.getHead()).toString())
                                .append("shirtJSON", ItemUtil.getJsonFromItemNew(outfit.getShirt()).toString())
                                .append("pantsJSON", ItemUtil.getJsonFromItemNew(outfit.getPants()).toString())
                                .append("bootsJSON", ItemUtil.getJsonFromItemNew(outfit.getBoots()).toString())
                        )
                );
            }
        } else {
            Core.logMessage("WardrobeManager", "Loaded " + outfits.size() + " outfits!");
        }
        if (!initialStarted) {
            initialStarted = true;
            Core.runTaskTimerAsynchronously(ParkManager.getInstance(), () -> {
                for (CPlayer player : Core.getPlayerManager().getOnlinePlayers()) {
                    if (!player.getRegistry().hasEntry("updateOutfitSelection")) continue;
                    player.getRegistry().removeEntry("updateOutfitSelection");
                    Clothing c = (Clothing) player.getRegistry().getEntry("clothing");
                    Core.getMongoHandler().setOutfitCode(player.getUniqueId(), c.getHeadID() +
                            "," + c.getShirtID() + "," + c.getPantsID() + "," + c.getBootsID());
                }
            }, 0L, 100L);
        }
    }

    public Outfit getOutfit(int id) {
        return outfits.get(id);
    }

    public List<Outfit> getOutfits() {
        return new ArrayList<>(outfits.values());
    }

    public void handleJoin(CPlayer player, String outfitCode, ArrayList arrayList) {
        List<Integer> purchases = new ArrayList<>();
        for (Object o : arrayList) {
            Document doc = (Document) o;
            int id = doc.getInteger("id");
            purchases.add(id);
        }
        player.getRegistry().addEntry("outfitPurchases", purchases);

        Clothing c = new Clothing();
        String[] list = outfitCode.split(",");
        int in = 0;
        for (String s : list) {
            try {
                int i = Integer.parseInt(s);
                if (!outfits.containsKey(i)) continue;
                Outfit o = getOutfit(i);
                switch (in) {
                    case 0:
                        c.setHead(o.getHead());
                        c.setHeadID(i);
                        break;
                    case 1:
                        c.setShirt(o.getShirt());
                        c.setShirtID(i);
                        break;
                    case 2:
                        c.setPants(o.getPants());
                        c.setPantsID(i);
                        break;
                    case 3:
                        c.setBoots(o.getBoots());
                        c.setBootsID(i);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            in++;
        }
        player.getRegistry().addEntry("clothing", c);
    }

    public void setOutfitItems(CPlayer player) {
        Clothing c = (Clothing) player.getRegistry().getEntry("clothing");
        PlayerInventory inv = player.getInventory();
        inv.setHelmet(c.getHead());
        inv.setChestplate(c.getShirt());
        inv.setLeggings(c.getPants());
        inv.setBoots(c.getBoots());
    }

    private void setSlot(CPlayer player, int outfitId, OutfitSlot slot, int page, boolean owns) {
        if (!owns) {
            player.sendMessage(ChatColor.RED + "You don't own that!");
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 25, 1);
            return;
        }
        Clothing c = (Clothing) player.getRegistry().getEntry("clothing");
        PlayerInventory inv = player.getInventory();
        Outfit outfit = getOutfit(outfitId);
        boolean selected = false;
        switch (slot) {
            case HEAD:
                if (c.getHeadID() != outfitId) {
                    c.setHeadID(outfitId);
                    c.setHead(outfit == null ? null : outfit.getHead());
                    inv.setHelmet(c.getHead());
                    selected = true;
                } else if (outfitId != 0) {
                    player.sendMessage(ChatColor.RED + "You are already wearing that!");
                }
                break;
            case SHIRT:
                if (c.getShirtID() != outfitId) {
                    c.setShirtID(outfitId);
                    c.setShirt(outfit == null ? null : outfit.getShirt());
                    inv.setChestplate(c.getShirt());
                    selected = true;
                } else if (outfitId != 0) {
                    player.sendMessage(ChatColor.RED + "You are already wearing that!");
                }
                break;
            case PANTS:
                if (c.getPantsID() != outfitId) {
                    c.setPantsID(outfitId);
                    c.setPants(outfit == null ? null : outfit.getPants());
                    inv.setLeggings(c.getPants());
                    selected = true;
                } else if (outfitId != 0) {
                    player.sendMessage(ChatColor.RED + "You are already wearing that!");
                }
                break;
            case BOOTS:
                if (c.getBootsID() != outfitId) {
                    c.setBootsID(outfitId);
                    c.setBoots(outfit == null ? null : outfit.getBoots());
                    inv.setBoots(c.getBoots());
                    selected = true;
                } else if (outfitId != 0) {
                    player.sendMessage(ChatColor.RED + "You are already wearing that!");
                }
                break;
        }
        if (selected) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
            player.getRegistry().addEntry("updateOutfitSelection", true);
            openWardrobePage(player, page);
        }
    }

    private void setOutfit(CPlayer player, int outfitId, int page, boolean owns) {
        if (!owns) {
            player.sendMessage(ChatColor.RED + "You don't own that!");
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 25, 1);
            return;
        }
        Clothing c = (Clothing) player.getRegistry().getEntry("clothing");
        PlayerInventory inv = player.getInventory();
        Outfit outfit = getOutfit(outfitId);
        boolean selected = false;
        if (outfitId != 0 &&
                c.getHeadID() == outfitId &&
                c.getShirtID() == outfitId &&
                c.getPantsID() == outfitId &&
                c.getBootsID() == outfitId) {
            player.sendMessage(ChatColor.RED + "You are already wearing that!");
            return;
        }
        c.setHeadID(outfitId);
        c.setShirtID(outfitId);
        c.setPantsID(outfitId);
        c.setBootsID(outfitId);

        c.setHead(outfit == null ? null : outfit.getHead());
        c.setShirt(outfit == null ? null : outfit.getShirt());
        c.setPants(outfit == null ? null : outfit.getPants());
        c.setBoots(outfit == null ? null : outfit.getBoots());

        inv.setHelmet(c.getHead());
        inv.setChestplate(c.getShirt());
        inv.setLeggings(c.getPants());
        inv.setBoots(c.getBoots());
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
        player.getRegistry().addEntry("updateOutfitSelection", true);
        openWardrobePage(player, page);
    }

    @SuppressWarnings("unchecked")
    public void openWardrobePage(CPlayer player, int page) {
        List<MenuButton> buttons = new ArrayList<>(Arrays.asList(
                new MenuButton(16, ItemUtil.create(Material.GLASS, ChatColor.GREEN + "Reset Head"),
                        ImmutableMap.of(ClickType.LEFT, p -> setSlot(p, 0, OutfitSlot.HEAD, page, true),
                                ClickType.RIGHT, p -> setOutfit(p, 0, page, true))),
                new MenuButton(25, ItemUtil.create(Material.GLASS, ChatColor.GREEN + "Reset Shirt"),
                        ImmutableMap.of(ClickType.LEFT, p -> setSlot(p, 0, OutfitSlot.SHIRT, page, true),
                                ClickType.RIGHT, p -> setOutfit(p, 0, page, true))),
                new MenuButton(34, ItemUtil.create(Material.GLASS, ChatColor.GREEN + "Reset Pants"),
                        ImmutableMap.of(ClickType.LEFT, p -> setSlot(p, 0, OutfitSlot.PANTS, page, true),
                                ClickType.RIGHT, p -> setOutfit(p, 0, page, true))),
                new MenuButton(43, ItemUtil.create(Material.GLASS, ChatColor.GREEN + "Reset Boots"),
                        ImmutableMap.of(ClickType.LEFT, p -> setSlot(p, 0, OutfitSlot.BOOTS, page, true),
                                ClickType.RIGHT, p -> setOutfit(p, 0, page, true))),
                ParkManager.getMagicBandManager().getBackButton(49, BandInventory.MAIN)
        ));

        List<Outfit> fullList = new ArrayList<>(outfits.values());
        List<Outfit> sublist = fullList.subList((page - 1) * 6, Math.min(page * 6, fullList.size()));

        List<Integer> purchases = (List<Integer>) player.getRegistry().getEntry("outfitPurchases");
        Clothing c = (Clothing) player.getRegistry().getEntry("clothing");

        int i = 0;
        for (Outfit outfit : sublist) {
            boolean owns = purchases.contains(outfit.getId());
            ItemStack head = outfit.getHead().clone();
            ItemStack shirt = outfit.getShirt().clone();
            ItemStack pants = outfit.getPants().clone();
            ItemStack boots = outfit.getBoots().clone();
            if (!owns) {
                ItemMeta hm = head.getItemMeta();
                hm.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH +
                        ChatColor.stripColor(hm.getDisplayName()));
                head.setItemMeta(hm);
                ItemMeta sm = shirt.getItemMeta();
                sm.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH +
                        ChatColor.stripColor(sm.getDisplayName()));
                shirt.setItemMeta(sm);
                ItemMeta pm = pants.getItemMeta();
                pm.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH +
                        ChatColor.stripColor(pm.getDisplayName()));
                pants.setItemMeta(pm);
                ItemMeta bm = boots.getItemMeta();
                bm.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH +
                        ChatColor.stripColor(bm.getDisplayName()));
                boots.setItemMeta(bm);
            }
            if (c.getHeadID() == outfit.getId()) head.addUnsafeEnchantment(Enchantment.LUCK, 1);
            buttons.add(new MenuButton(10 + i, head,
                    ImmutableMap.of(ClickType.LEFT, p -> setSlot(p, outfit.getId(), OutfitSlot.HEAD, page, owns),
                            ClickType.RIGHT, p -> setOutfit(p, outfit.getId(), page, owns)))
            );
            if (c.getShirtID() == outfit.getId()) shirt.addUnsafeEnchantment(Enchantment.LUCK, 1);
            buttons.add(new MenuButton(19 + i, shirt,
                    ImmutableMap.of(ClickType.LEFT, p -> setSlot(p, outfit.getId(), OutfitSlot.SHIRT, page, owns),
                            ClickType.RIGHT, p -> setOutfit(p, outfit.getId(), page, owns)))
            );
            if (c.getPantsID() == outfit.getId()) pants.addUnsafeEnchantment(Enchantment.LUCK, 1);
            buttons.add(new MenuButton(28 + i, pants,
                    ImmutableMap.of(ClickType.LEFT, p -> setSlot(p, outfit.getId(), OutfitSlot.PANTS, page, owns),
                            ClickType.RIGHT, p -> setOutfit(p, outfit.getId(), page, owns)))
            );
            if (c.getBootsID() == outfit.getId()) boots.addUnsafeEnchantment(Enchantment.LUCK, 1);
            buttons.add(new MenuButton(37 + i, boots,
                    ImmutableMap.of(ClickType.LEFT, p -> setSlot(p, outfit.getId(), OutfitSlot.BOOTS, page, owns),
                            ClickType.RIGHT, p -> setOutfit(p, outfit.getId(), page, owns)))
            );
            i++;
        }
        if (page > 1)
            buttons.add(new MenuButton(48, ItemUtil.create(Material.ARROW, ChatColor.GREEN + "Last Page"),
                    ImmutableMap.of(ClickType.LEFT, p -> openWardrobePage(p, page - 1))));
        if ((page * 6) < fullList.size())
            buttons.add(new MenuButton(50, ItemUtil.create(Material.ARROW, ChatColor.GREEN + "Next Page"),
                    ImmutableMap.of(ClickType.LEFT, p -> openWardrobePage(p, page + 1))));
        new Menu(54, ChatColor.BLUE + "Wardrobe Manager Page " + page, player, buttons).open();
    }

    @SuppressWarnings("deprecation")
    private Outfit getLegacyOutfit(Document doc) {
        try {
            int id = doc.getInteger("id");
            String ht = doc.getString("head");
            String ct = doc.getString("chest");
            String lt = doc.getString("leggings");
            String bt = doc.getString("boots");
            ItemStack h = MinecraftReflection.getBukkitItemStack(
                    new ItemStack(Material.getMaterial(doc.getInteger("headID")),
                            1, (short) doc.getInteger("headData", 0))
            );
            if (!ht.equals("")) {
                NbtFactory.setItemTag(h, new NbtTextSerializer().deserializeCompound(ht));
            }
            ItemStack s = MinecraftReflection.getBukkitItemStack(
                    new ItemStack(Material.getMaterial(doc.getInteger("chestID")),
                            1, (short) doc.getInteger("chestData", 0))
            );
            if (!ct.equals("")) {
                NbtFactory.setItemTag(s, new NbtTextSerializer().deserializeCompound(ct));
            }
            ItemStack l = MinecraftReflection.getBukkitItemStack(
                    new ItemStack(Material.getMaterial(doc.getInteger("leggingsID")),
                            1, (short) doc.getInteger("leggingsData", 0))
            );
            if (!lt.equals("")) {
                NbtFactory.setItemTag(l, new NbtTextSerializer().deserializeCompound(lt));
            }
            ItemStack b = MinecraftReflection.getBukkitItemStack(
                    new ItemStack(Material.getMaterial(doc.getInteger("bootsID")),
                            1, (short) doc.getInteger("bootsData", 0))
            );
            if (!bt.equals("")) {
                NbtFactory.setItemTag(b, new NbtTextSerializer().deserializeCompound(bt));
            }
            String name = ChatColor.translateAlternateColorCodes('&', doc.getString("name"));
            ItemMeta hm = h.getItemMeta();
            hm.setDisplayName(name + " Head");
            h.setItemMeta(hm);
            ItemMeta shm = s.getItemMeta();
            shm.setDisplayName(name + " Shirt");
            s.setItemMeta(shm);
            ItemMeta pm = l.getItemMeta();
            pm.setDisplayName(name + " Pants");
            l.setItemMeta(pm);
            ItemMeta bm = b.getItemMeta();
            bm.setDisplayName(name + " Boots");
            b.setItemMeta(bm);
            return new Outfit(id, name, h, s, l, b);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
