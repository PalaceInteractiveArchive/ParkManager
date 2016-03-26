package us.mcmagic.parkmanager.shop;

import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.MojangsonParseException;
import net.minecraft.server.v1_8_R3.MojangsonParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.InventoryType;
import us.mcmagic.parkmanager.handlers.Outfit;
import us.mcmagic.parkmanager.handlers.PlayerData;
import us.mcmagic.parkmanager.storage.StorageManager;
import us.mcmagic.parkmanager.utils.BandUtil;
import us.mcmagic.parkmanager.utils.SqlUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public void initialize() {
        StorageManager sm = ParkManager.storageManager;
        outfits.clear();
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM outfits");
            ResultSet result = sql.executeQuery();
            while (result.next()) {
                Integer id = result.getInt("id");
                int hid = result.getInt("hid");
                int hdata = result.getInt("hdata");
                String ht = result.getString("head");
                int cid = result.getInt("cid");
                int cdata = result.getInt("cdata");
                String st = result.getString("chestplate");
                int lid = result.getInt("lid");
                int ldata = result.getInt("ldata");
                String pt = result.getString("leggings");
                int bid = result.getInt("bid");
                int bdata = result.getInt("bdata");
                String bt = result.getString("boots");
                net.minecraft.server.v1_8_R3.ItemStack h = new net.minecraft.server.v1_8_R3.ItemStack(Item.getById(hid), 1);
                h.setData(hdata);
                if (!ht.equals("")) {
                    h.setTag(MojangsonParser.parse(ht));
                }
                net.minecraft.server.v1_8_R3.ItemStack s = new net.minecraft.server.v1_8_R3.ItemStack(Item.getById(cid), 1);
                s.setData(cdata);
                if (!st.equals("")) {
                    s.setTag(MojangsonParser.parse(st));
                }
                net.minecraft.server.v1_8_R3.ItemStack l = new net.minecraft.server.v1_8_R3.ItemStack(Item.getById(lid), 1);
                l.setData(ldata);
                if (!pt.equals("")) {
                    l.setTag(MojangsonParser.parse(pt));
                }
                net.minecraft.server.v1_8_R3.ItemStack b = new net.minecraft.server.v1_8_R3.ItemStack(Item.getById(bid), 1);
                b.setData(bdata);
                if (!bt.equals("")) {
                    b.setTag(MojangsonParser.parse(bt));
                }
                String name = result.getString("name");
                String cname = ChatColor.translateAlternateColorCodes('&', result.getString("name"));
                ItemStack head = CraftItemStack.asBukkitCopy(h);
                ItemMeta hm = head.getItemMeta();
                hm.setDisplayName(cname + " Head");
                head.setItemMeta(hm);
                ItemStack shirt = CraftItemStack.asBukkitCopy(s);
                ItemMeta shm = shirt.getItemMeta();
                shm.setDisplayName(cname + " Shirt");
                shirt.setItemMeta(shm);
                ItemStack pants = CraftItemStack.asBukkitCopy(l);
                ItemMeta pm = pants.getItemMeta();
                pm.setDisplayName(cname + " Pants");
                pants.setItemMeta(pm);
                ItemStack boots = CraftItemStack.asBukkitCopy(b);
                ItemMeta bm = boots.getItemMeta();
                bm.setDisplayName(cname + " Boots");
                boots.setItemMeta(bm);
                outfits.put(id, new Outfit(id, ChatColor.translateAlternateColorCodes('&', name),
                        head, shirt, pants, boots));
            }
            result.close();
            sql.close();
        } catch (SQLException | MojangsonParseException e) {
            e.printStackTrace();
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
        final Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            ParkManager.inventoryUtil.openInventory(player, InventoryType.MAINMENU);
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
                ParkManager.inventoryUtil.openWardrobeManagerPage(player, page + 1);
            } else if (name.contains("Last")) {
                ParkManager.inventoryUtil.openWardrobeManagerPage(player, page - 1);
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
                final PlayerData.Clothing c = ParkManager.getPlayerData(player.getUniqueId()).getClothing();
                c.setHead(null);
                c.setShirt(null);
                c.setPants(null);
                c.setBoots(null);
                c.setHeadID(0);
                c.setShirtID(0);
                c.setPantsID(0);
                c.setBootsID(0);
                Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        setOutfitCode(player, c.getHeadID() + "," + c.getShirtID() + "," + c.getPantsID() + "," +
                                c.getBootsID());
                    }
                });
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 2);
                ParkManager.inventoryUtil.openWardrobeManagerPage(player, page);
                return;
            }
            switch (type) {
                case HEAD: {
                    inv.setHelmet(air);
                    final PlayerData.Clothing c = ParkManager.getPlayerData(player.getUniqueId()).getClothing();
                    c.setHead(null);
                    c.setHeadID(0);
                    Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            setOutfitCode(player, c.getHeadID() + "," + c.getShirtID() + "," + c.getPantsID() + "," +
                                    c.getBootsID());
                        }
                    });
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 2);
                    ParkManager.inventoryUtil.openWardrobeManagerPage(player, page);
                    break;
                }
                case SHIRT: {
                    inv.setChestplate(air);
                    final PlayerData.Clothing c = ParkManager.getPlayerData(player.getUniqueId()).getClothing();
                    c.setShirt(null);
                    c.setShirtID(0);
                    Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            setOutfitCode(player, c.getHeadID() + "," + c.getShirtID() + "," + c.getPantsID() + "," +
                                    c.getBootsID());
                        }
                    });
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 2);
                    ParkManager.inventoryUtil.openWardrobeManagerPage(player, page);
                    break;
                }
                case PANTS: {
                    inv.setLeggings(air);
                    final PlayerData.Clothing c = ParkManager.getPlayerData(player.getUniqueId()).getClothing();
                    c.setPants(null);
                    c.setPantsID(0);
                    Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            setOutfitCode(player, c.getHeadID() + "," + c.getShirtID() + "," + c.getPantsID() + "," +
                                    c.getBootsID());
                        }
                    });
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 2);
                    ParkManager.inventoryUtil.openWardrobeManagerPage(player, page);
                    break;
                }
                case BOOTS: {
                    inv.setBoots(air);
                    final PlayerData.Clothing c = ParkManager.getPlayerData(player.getUniqueId()).getClothing();
                    c.setBoots(null);
                    c.setBootsID(0);
                    Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            setOutfitCode(player, c.getHeadID() + "," + c.getShirtID() + "," + c.getPantsID() + "," +
                                    c.getBootsID());
                        }
                    });
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 2);
                    ParkManager.inventoryUtil.openWardrobeManagerPage(player, page);
                    break;
                }
            }
            return;
        }
        if (meta.getDisplayName().contains(ChatColor.STRIKETHROUGH.toString())) {
            player.sendMessage(ChatColor.RED + "You don't own that Outfit!");
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 25, 1);
            return;
        }
        final PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
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
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 2);
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
            ParkManager.inventoryUtil.openWardrobeManagerPage(player, page);
            String code = c.getHeadID() + "," + c.getShirtID() + "," + c.getPantsID() + "," + c.getBootsID();
            data.setClothing(c);
            data.setOutfitCode(code);
            Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), new Runnable() {
                @Override
                public void run() {
                    setOutfitCode(player, data.getOutfitCode());
                }
            });
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
        ParkManager.inventoryUtil.openWardrobeManagerPage(player, page);
        String code = c.getHeadID() + "," + c.getShirtID() + "," + c.getPantsID() + "," + c.getBootsID();
        data.setClothing(c);
        data.setOutfitCode(code);
        Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), new Runnable() {
            @Override
            public void run() {
                setOutfitCode(player, data.getOutfitCode());
            }
        });
    }

    private boolean equals(ItemStack head, ItemStack item) {
        return head.getType().equals(item.getType()) &&
                item.getItemMeta().getDisplayName().contains(head.getItemMeta().getDisplayName());
    }

    private void setOutfitCode(Player player, String code) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET outfit=? WHERE uuid=?");
            sql.setString(1, code);
            sql.setString(2, player.getUniqueId().toString());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void reset(Player player, ClothingType type) {
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
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("INSERT INTO purchases (id,uuid,item,time,outfit) VALUES" +
                    " (0,?,?,?,1)");
            sql.setString(1, player.getUniqueId().toString());
            sql.setInt(2, id);
            sql.setLong(3, System.currentTimeMillis() / 1000);
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeOutfit(Integer id) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("DELETE FROM outfits WHERE id=?");
            sql.setInt(1, id);
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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