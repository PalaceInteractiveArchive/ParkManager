package us.mcmagic.magicassistant.storage;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.InventoryType;
import us.mcmagic.magicassistant.handlers.PlayerData;
import us.mcmagic.magicassistant.listeners.BlockEdit;
import us.mcmagic.magicassistant.utils.SqlUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Marc on 10/10/15
 */
public class StorageManager {
    private List<UUID> loadingPack = new ArrayList<>();
    private List<UUID> loadingLocker = new ArrayList<>();

    public StorageManager() {
        Bukkit.getScheduler().runTaskTimer(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (UUID uuid : new ArrayList<>(loadingPack)) {
                    PlayerData data = MagicAssistant.getPlayerData(uuid);
                    if (data.getBackpack() != null) {
                        Player tp = Bukkit.getPlayer(uuid);
                        if (tp == null) {
                            continue;
                        }
                        if (tp.getOpenInventory() != null &&
                                tp.getOpenInventory().getTopInventory().getName().contains("Loading")) {
                            loadingPack.remove(uuid);
                            MagicAssistant.inventoryUtil.openInventory(tp, InventoryType.BACKPACK);
                        }
                    }
                }
                for (UUID uuid : new ArrayList<>(loadingLocker)) {
                    PlayerData data = MagicAssistant.getPlayerData(uuid);
                    if (data.getBackpack() != null) {
                        Player tp = Bukkit.getPlayer(uuid);
                        if (tp == null) {
                            continue;
                        }
                        if (tp.getOpenInventory() != null &&
                                tp.getOpenInventory().getTopInventory().getName().contains("Loading")) {
                            loadingLocker.remove(uuid);
                            MagicAssistant.inventoryUtil.openInventory(tp, InventoryType.LOCKER);
                        }
                    }
                }
            }
        }, 0L, 10L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    update(tp);
                }
            }
        }, 0L, 6000L);
    }

    public Backpack getBackpack(Player player) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement pack = connection.prepareStatement("SELECT pack,packsize FROM storage WHERE uuid=?");
            pack.setString(1, player.getUniqueId().toString());
            ResultSet result = pack.executeQuery();
            if (!result.next()) {
                result.close();
                pack.close();
                PreparedStatement insert = connection.prepareStatement("INSERT INTO storage (id, uuid, pack, packsize, " +
                        "locker, lockersize, hotbar) VALUES (0,?,?,'small',?,'small',?)");
                insert.setString(1, player.getUniqueId().toString());
                insert.setBytes(2, new byte[]{});
                insert.setBytes(3, new byte[]{});
                insert.setBytes(4, new byte[]{});
                insert.execute();
                insert.close();
                return new Backpack(player, StorageSize.SMALL, new ItemStack[]{});
            }
            StorageSize size = StorageSize.fromString(result.getString("packsize"));
            byte[] p = result.getBytes("pack");
            ItemStack[] deserial = deserial(p);
            ItemStack[] cont = new ItemStack[size.getRows() * 9];
            try {
                for (int i = 0; i < p.length; i++) {
                    cont[i] = deserial[i];
                }
            } catch (Exception ignored) {
            }
            Backpack backpack = new Backpack(player, size, cont);
            result.close();
            pack.close();
            return backpack;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Locker getLocker(Player player) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement pack = connection.prepareStatement("SELECT locker,lockersize FROM storage WHERE uuid=?");
            pack.setString(1, player.getUniqueId().toString());
            ResultSet result = pack.executeQuery();
            if (!result.next()) {
                result.close();
                pack.close();
                PreparedStatement insert = connection.prepareStatement("INSERT INTO storage (id, uuid, pack, packsize, " +
                        "locker, lockersize) VALUES (0,?,?,'small',?,'small')");
                insert.setString(1, player.getUniqueId().toString());
                insert.setBytes(2, new byte[]{});
                insert.setBytes(3, new byte[]{});
                insert.execute();
                insert.close();
                return new Locker(player, StorageSize.SMALL, new ItemStack[]{});
            }
            StorageSize size = StorageSize.fromString(result.getString("lockersize"));
            byte[] p = result.getBytes("locker");
            ItemStack[] deserial = deserial(p);
            ItemStack[] cont = new ItemStack[size.getRows() * 9];
            try {
                for (int i = 0; i < p.length; i++) {
                    cont[i] = deserial[i];
                }
            } catch (Exception ignored) {
            }
            Locker locker = new Locker(player, size, cont);
            result.close();
            pack.close();
            return locker;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemStack[] deserial(byte[] b) {
        return deserializeItemStacks(uncompress(b));
    }

    public static byte[] serial(ItemStack[] stacks) {
        for (int i = 0; i < stacks.length; i++) {
            ItemStack s = stacks[i];
            if (s == null) {
                continue;
            }
            if (s.getType().equals(Material.SKULL_ITEM)) {
                stacks[i] = new ItemStack(Material.AIR);
            }
        }
        return compress(serializeItemStacks(stacks));
    }

    public static byte[] serializeItemStacks(ItemStack[] inv) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                try (BukkitObjectOutputStream bos = new BukkitObjectOutputStream(os)) {
                    bos.writeObject(inv);
                    return os.toByteArray();
                }
            } finally {
                if (Collections.singletonList(os).get(0) != null)
                    os.close();
            }
        } catch (IOException ex) {
            return null;
        }
    }

    public static ItemStack[] deserializeItemStacks(byte[] b) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(b);
            try {
                try (BukkitObjectInputStream bois = new BukkitObjectInputStream(bais)) {
                    return (ItemStack[]) bois.readObject();
                }
            } finally {
                if (Collections.singletonList(bais).get(0) != null)
                    bais.close();
            }
        } catch (Exception ex) {
            return null;
        }
    }

    public static byte[] compress(byte[] uncomp) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                GZIPOutputStream compressor = new GZIPOutputStream(os);
                compressor.write(uncomp);
                compressor.close();

                return os.toByteArray();
            } finally {
                if (Collections.singletonList(os).get(0) != null)
                    os.close();

            }

        } catch (IOException ex) {
            return null;
        }
    }

    public static byte[] uncompress(byte[] comp) {
        GZIPInputStream decompressor = null;
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(comp);
            try {
                decompressor = new GZIPInputStream(is);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] b = new byte[256];
                int tmp;
                while ((tmp = decompressor.read(b)) != -1) {
                    buffer.write(b, 0, tmp);
                }
                buffer.close();
                byte[] arrayOfByte1 = buffer.toByteArray();

                if (Collections.singletonList(is).get(0) != null)
                    is.close();

                return arrayOfByte1;
            } finally {
                if (Collections.singletonList(is).get(0) != null)
                    is.close();

            }
        } catch (IOException ex) {
            return null;
        } finally {
            try {
                if (decompressor != null)
                    decompressor.close();
            } catch (IOException ignored) {
            }
        }
    }

    public void logout(final Player player) {
        loadingPack.remove(player.getUniqueId());
        loadingLocker.remove(player.getUniqueId());
        final PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        Bukkit.getScheduler().runTaskAsynchronously(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                final long time = System.currentTimeMillis();
                update(player, data);
                System.out.println("Total Processing Time: " + (System.currentTimeMillis() - time) + "ms");
            }
        });
    }

    private void update(Player player) {
        update(player, MagicAssistant.getPlayerData(player.getUniqueId()));
    }

    private void update(Player player, PlayerData data) {
        Backpack pack = data.getBackpack();
        Locker locker = data.getLocker();
        Inventory bp = Bukkit.createInventory(player, pack.getInventory().getSize());
        boolean build = BlockEdit.isInBuildMode(player.getUniqueId());
        if (build) {
            bp.setContents(player.getInventory().getContents());
        } else {
            bp.setContents(pack.getInventory().getContents());
        }
        try (Connection connection = SqlUtil.getConnection()) {
            ItemStack[] hotbar = new ItemStack[4];
            ItemStack[] cont = player.getInventory().getContents();
            for (int i = 0; i < 4; i++) {
                hotbar[i] = cont[i];
            }
            PreparedStatement sql = connection.prepareStatement("UPDATE storage SET pack=?,locker=?,hotbar=? WHERE uuid=?");
            sql.setBytes(1, serial(bp.getContents()));
            sql.setBytes(2, serial(locker.getInventory().getContents()));
            sql.setBytes(3, serial(hotbar));
            sql.setString(4, player.getUniqueId().toString());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setLoadingPack(Player player) {
        loadingPack.remove(player.getUniqueId());
        loadingPack.add(player.getUniqueId());
    }

    public void setLoadingLocker(Player player) {
        loadingLocker.remove(player.getUniqueId());
        loadingLocker.add(player.getUniqueId());
    }

    public ItemStack[] getHotbar(Player player) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT hotbar FROM storage WHERE uuid=?");
            sql.setString(1, player.getUniqueId().toString());
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                return null;
            }
            byte[] hot = result.getBytes("hotbar");
            result.close();
            sql.close();
            return deserial(hot);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setValue(final UUID uuid, final String key, final String value) {
        Bukkit.getScheduler().runTaskAsynchronously(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                try (Connection connection = SqlUtil.getConnection()) {
                    PreparedStatement sql = connection.prepareStatement("UPDATE storage SET " + key + "=? WHERE uuid=?");
                    sql.setString(1, value);
                    sql.setString(2, uuid.toString());
                    sql.execute();
                    sql.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}