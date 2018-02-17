package network.palace.parkmanager.shop;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.io.NbtTextSerializer;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.InventoryType;
import network.palace.parkmanager.handlers.Outfit;
import network.palace.parkmanager.handlers.PlayerData;
import network.palace.parkmanager.handlers.Resort;
import network.palace.parkmanager.storage.StorageManager;
import network.palace.parkmanager.utils.BandUtil;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Marc on 11/9/15
 */
public class WardrobeManager {
    private HashMap<Integer, Outfit> outfits = new HashMap<>();

    public WardrobeManager() {
        initialize();
    }

    @SuppressWarnings("deprecation")
    public void initialize() {
        StorageManager sm = ParkManager.getInstance().getStorageManager();
        outfits.clear();
        Resort resort = ParkManager.getInstance().getResort();
        for (Document d : Core.getMongoHandler().getOutfits(resort.getId())) {
            try {
                int id = d.getInteger("id");
                int hid = d.getInteger("hid");
                int hdata = d.getInteger("hdata");
                String ht = d.getString("head");
                int cid = d.getInteger("cid");
                int cdata = d.getInteger("cdata");
                String st = d.getString("chestplate");
                int lid = d.getInteger("lid");
                int ldata = d.getInteger("ldata");
                String pt = d.getString("leggings");
                int bid = d.getInteger("bid");
                int bdata = d.getInteger("bdata");
                String bt = d.getString("boots");
                ItemStack h = MinecraftReflection.getBukkitItemStack(new ItemStack(Material.getMaterial(hid), 1, (short) hdata));
                if (!ht.equals("")) {
                    NbtFactory.setItemTag(h, new NbtTextSerializer().deserializeCompound(ht));
                }
                ItemStack s = MinecraftReflection.getBukkitItemStack(new ItemStack(Material.getMaterial(cid), 1, (short) cdata));
                if (!st.equals("")) {
                    NbtFactory.setItemTag(s, new NbtTextSerializer().deserializeCompound(st));
                }
                ItemStack l = MinecraftReflection.getBukkitItemStack(new ItemStack(Material.getMaterial(lid), 1, (short) ldata));
                if (!pt.equals("")) {
                    NbtFactory.setItemTag(l, new NbtTextSerializer().deserializeCompound(pt));
                }
                ItemStack b = MinecraftReflection.getBukkitItemStack(new ItemStack(Material.getMaterial(bid), 1, (short) bdata));
                if (!bt.equals("")) {
                    NbtFactory.setItemTag(b, new NbtTextSerializer().deserializeCompound(bt));
                }
                String name = d.getString("name");
                String cname = ChatColor.translateAlternateColorCodes('&', name);
                ItemMeta hm = h.getItemMeta();
                hm.setDisplayName(cname + " Head");
                h.setItemMeta(hm);
                ItemMeta shm = s.getItemMeta();
                shm.setDisplayName(cname + " Shirt");
                s.setItemMeta(shm);
                ItemMeta pm = l.getItemMeta();
                pm.setDisplayName(cname + " Pants");
                l.setItemMeta(pm);
                ItemMeta bm = b.getItemMeta();
                bm.setDisplayName(cname + " Boots");
                b.setItemMeta(bm);
                outfits.put(id, new Outfit(id, ChatColor.translateAlternateColorCodes('&', name), h, s, l, b));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Outfit> getOutfits() {
        return new ArrayList<>(outfits.values());
    }

    public Outfit getOutfit(Integer id) {
        for (Map.Entry<Integer, Outfit> entry : outfits.entrySet()) {
            if (entry.getKey().equals(id)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getItemMeta() == null) {
            return;
        }
        final CPlayer player = Core.getPlayerManager().getPlayer((Player) event.getWhoClicked());
        ParkManager parkManager = ParkManager.getInstance();
        if (item.equals(BandUtil.getBackItem())) {
            parkManager.getInventoryUtil().openInventory(player, InventoryType.MAINMENU);
            return;
        }
        Material itype = item.getType();
        if (itype == null || itype.equals(Material.AIR)) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        int page = Integer.parseInt(ChatColor.stripColor(event.getClickedInventory().getTitle()).replace("Wardrobe Manager Page ", ""));
        String name = ChatColor.stripColor(meta.getDisplayName());
        if (itype.equals(Material.ARROW)) {
            if (name.contains("Next")) {
                parkManager.getInventoryUtil().openWardrobeManagerPage(player, page + 1);
            } else if (name.contains("Last")) {
                parkManager.getInventoryUtil().openWardrobeManagerPage(player, page - 1);
            }
            return;
        }
        if (!item.getEnchantments().isEmpty()) {
            player.sendMessage(ChatColor.RED + "You are already wearing that!");
            return;
        }
        boolean right = event.isRightClick();
        PlayerInventory inv = player.getInventory();
        int slot = event.getRawSlot();
        ClothingType type = null;
        if (slot > 9 && slot < 18) {
            type = ClothingType.HEAD;
        } else if (slot > 18 && slot < 27) {
            type = ClothingType.SHIRT;
        } else if (slot > 27 && slot < 36) {
            type = ClothingType.PANTS;
        } else if (slot > 36 && slot < 45) {
            type = ClothingType.BOOTS;
        }
        if (type == null) {
            return;
        }
        if (itype.equals(Material.GLASS)) {
            ItemStack air = new ItemStack(Material.AIR);
            if (right) {
                inv.setHelmet(air);
                inv.setChestplate(air);
                inv.setLeggings(air);
                inv.setBoots(air);
                final PlayerData.Clothing c = parkManager.getPlayerData(player.getUniqueId()).getClothing();
                c.setHead(null);
                c.setShirt(null);
                c.setPants(null);
                c.setBoots(null);
                c.setHeadID(0);
                c.setShirtID(0);
                c.setPantsID(0);
                c.setBootsID(0);
                Bukkit.getScheduler().runTaskAsynchronously(parkManager, () -> setOutfitCode(player, c.getHeadID() +
                        "," + c.getShirtID() + "," + c.getPantsID() + "," + c.getBootsID()));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                parkManager.getInventoryUtil().openWardrobeManagerPage(player, page);
                return;
            }
            switch (type) {
                case HEAD: {
                    inv.setHelmet(air);
                    final PlayerData.Clothing c = parkManager.getPlayerData(player.getUniqueId()).getClothing();
                    c.setHead(null);
                    c.setHeadID(0);
                    Bukkit.getScheduler().runTaskAsynchronously(parkManager, () -> setOutfitCode(player, c.getHeadID() +
                            "," + c.getShirtID() + "," + c.getPantsID() + "," + c.getBootsID()));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                    parkManager.getInventoryUtil().openWardrobeManagerPage(player, page);
                    break;
                }
                case SHIRT: {
                    inv.setChestplate(air);
                    final PlayerData.Clothing c = parkManager.getPlayerData(player.getUniqueId()).getClothing();
                    c.setShirt(null);
                    c.setShirtID(0);
                    Bukkit.getScheduler().runTaskAsynchronously(parkManager, () -> setOutfitCode(player, c.getHeadID() +
                            "," + c.getShirtID() + "," + c.getPantsID() + "," + c.getBootsID()));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                    parkManager.getInventoryUtil().openWardrobeManagerPage(player, page);
                    break;
                }
                case PANTS: {
                    inv.setLeggings(air);
                    final PlayerData.Clothing c = parkManager.getPlayerData(player.getUniqueId()).getClothing();
                    c.setPants(null);
                    c.setPantsID(0);
                    Bukkit.getScheduler().runTaskAsynchronously(parkManager, () -> setOutfitCode(player, c.getHeadID() +
                            "," + c.getShirtID() + "," + c.getPantsID() + "," + c.getBootsID()));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                    parkManager.getInventoryUtil().openWardrobeManagerPage(player, page);
                    break;
                }
                case BOOTS: {
                    inv.setBoots(air);
                    final PlayerData.Clothing c = parkManager.getPlayerData(player.getUniqueId()).getClothing();
                    c.setBoots(null);
                    c.setBootsID(0);
                    Bukkit.getScheduler().runTaskAsynchronously(parkManager, () -> setOutfitCode(player, c.getHeadID() +
                            "," + c.getShirtID() + "," + c.getPantsID() + "," + c.getBootsID()));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
                    parkManager.getInventoryUtil().openWardrobeManagerPage(player, page);
                    break;
                }
            }
            return;
        }
        if (meta.getDisplayName().contains(ChatColor.STRIKETHROUGH.toString())) {
            player.sendMessage(ChatColor.RED + "You don't own that Outfit!");
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 25, 1);
            return;
        }
        final PlayerData data = parkManager.getPlayerData(player.getUniqueId());
        List<Outfit> first = getOutfits();
        List<Outfit> outfits = first.subList((page - 1) * 6, (page * 6 > first.size() ? first.size() : page * 6));
        int id = 0;
        for (Outfit o : getOutfits()) {
            switch (type) {
                case HEAD:
                    if (equals(o.getHead(), item)) {
                        id = o.getId();
                        break;
                    }
                    break;
                case SHIRT:
                    if (equals(o.getShirt(), item)) {
                        id = o.getId();
                        break;
                    }
                    break;
                case PANTS:
                    if (equals(o.getPants(), item)) {
                        id = o.getId();
                        break;
                    }
                    break;
                case BOOTS:
                    if (equals(o.getBoots(), item)) {
                        id = o.getId();
                        break;
                    }
                    break;
            }
            if (id != 0) {
                break;
            }
        }
        Outfit o = outfits.get((slot % 9) - 1);
        if (o == null) {
            return;
        }
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
        PlayerData.Clothing c = data.getClothing();
        if (right) {
            c.setHead(o.getHead());
            c.setHeadID(id);
            c.setShirt(o.getShirt());
            c.setShirtID(id);
            c.setPants(o.getPants());
            c.setPantsID(id);
            c.setBoots(o.getBoots());
            c.setBootsID(id);
            try {
                if (!c.getHead().equals(inv.getHelmet())) {
                    inv.setHelmet(c.getHead());
                }
            } catch (Exception ignored) {
            }
            try {
                if (!c.getShirt().equals(inv.getChestplate())) {
                    inv.setChestplate(c.getShirt());
                }
            } catch (Exception ignored) {
            }
            try {
                if (!c.getPants().equals(inv.getLeggings())) {
                    inv.setLeggings(c.getPants());
                }
            } catch (Exception ignored) {
            }
            try {
                if (!c.getBoots().equals(inv.getBoots())) {
                    inv.setBoots(c.getBoots());
                }
            } catch (Exception ignored) {
            }
            parkManager.getInventoryUtil().openWardrobeManagerPage(player, page);
            String code = c.getHeadID() + "," + c.getShirtID() + "," + c.getPantsID() + "," + c.getBootsID();
            data.setClothing(c);
            data.setOutfitCode(code);
            Bukkit.getScheduler().runTaskAsynchronously(parkManager, () -> setOutfitCode(player, data.getOutfitCode()));
            return;
        }
        switch (type) {
            case HEAD:
                c.setHead(o.getHead());
                c.setHeadID(id);
                break;
            case SHIRT:
                c.setShirt(o.getShirt());
                c.setShirtID(id);
                break;
            case PANTS:
                c.setPants(o.getPants());
                c.setPantsID(id);
                break;
            case BOOTS:
                c.setBoots(o.getBoots());
                c.setBootsID(id);
                break;
        }
        try {
            if (!c.getHead().equals(inv.getHelmet())) {
                inv.setHelmet(c.getHead());
            }
        } catch (Exception ignored) {
        }
        try {
            if (!c.getShirt().equals(inv.getChestplate())) {
                inv.setChestplate(c.getShirt());
            }
        } catch (Exception ignored) {
        }
        try {
            if (!c.getPants().equals(inv.getLeggings())) {
                inv.setLeggings(c.getPants());
            }
        } catch (Exception ignored) {
        }
        try {
            if (!c.getBoots().equals(inv.getBoots())) {
                inv.setBoots(c.getBoots());
            }
        } catch (Exception ignored) {
        }
        parkManager.getInventoryUtil().openWardrobeManagerPage(player, page);
        String code = c.getHeadID() + "," + c.getShirtID() + "," + c.getPantsID() + "," + c.getBootsID();
        data.setClothing(c);
        data.setOutfitCode(code);
        Core.runTaskAsynchronously(() -> setOutfitCode(player, data.getOutfitCode()));
    }

    private boolean equals(ItemStack head, ItemStack item) {
        return head.getType().equals(item.getType()) &&
                item.getItemMeta().getDisplayName().contains(head.getItemMeta().getDisplayName());
    }

    private void setOutfitCode(CPlayer player, String code) {
        Core.getMongoHandler().setOutfitCode(player.getUniqueId(), code);
    }

    private void reset(CPlayer player, ClothingType type) {
        PlayerInventory inv = player.getInventory();
        ItemStack air = new ItemStack(Material.AIR);
        switch (type) {
            case HEAD:
                inv.setHelmet(air);
                break;
            case SHIRT:
                inv.setChestplate(air);
                break;
            case PANTS:
                inv.setLeggings(air);
                break;
            case BOOTS:
                inv.setBoots(air);
                break;
        }

    }

    protected void purchaseOutfit(Player player, Integer id) {
        Core.getMongoHandler().purchaseOutfit(player.getUniqueId(), id);
    }

    public void removeOutfit(Integer id) {
        Core.getMongoHandler().deleteOutfit(id);
        initialize();
    }

    private enum ClothingType {
        HEAD, SHIRT, PANTS, BOOTS;

        public static ClothingType fromString(String s) {
            switch (s.toLowerCase()) {
                case "head":
                    return HEAD;
                case "shirt":
                    return SHIRT;
                case "pants":
                    return PANTS;
                case "boots":
                    return PANTS;
            }
            return null;
        }
    }
}