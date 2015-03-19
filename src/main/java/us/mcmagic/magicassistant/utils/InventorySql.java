package us.mcmagic.magicassistant.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import us.mcmagic.mcmagiccore.MCMagicCore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class InventorySql {
    private static HashMap<String, Integer> list = new HashMap<>();
    public static List<UUID> players = new ArrayList<>();

    public static synchronized boolean playerDataContainsPlayer(Player player) {
        try (Connection connection = MCMagicCore.getInstance().permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM `inv_data` WHERE uuid=?;");
            sql.setString(1, player.getUniqueId() + "");
            ResultSet resultset = sql.executeQuery();
            boolean containsPlayer = resultset.next();
            sql.close();
            resultset.close();
            return containsPlayer;
        } catch (Exception e) {
            return false;
        }
    }

    public static synchronized void updateInventory(Player player) {
        PlayerInventory pi = player.getInventory();
        byte[] invdata = serial(pi.getContents());
        byte[] armordata = serial(pi.getArmorContents());
        try (Connection connection = MCMagicCore.getInstance().permSqlUtil.getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("UPDATE `inv_data` SET content=? WHERE uuid=?");
            sql.setBytes(1, invdata);
            sql.setBytes(2, armordata);
            sql.setString(3, player.getUniqueId() + "");
            sql.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized static void updateEndInventory(Player player) {
        Inventory endinv = player.getEnderChest();
        byte[] invdata = serial(endinv.getContents());
        try (Connection connection = MCMagicCore.getInstance().permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE `endinv_data` SET content=? WHERE uuid=?");
            sql.setBytes(1, invdata);
            sql.setString(2, player.getUniqueId() + "");
            sql.execute();
            sql.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized boolean endPlayerDataContainsPlayer(Player player) {
        try (Connection connection = MCMagicCore.getInstance().permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM `endinv_data` WHERE UUID=?;");
            sql.setString(1, player.getUniqueId() + "");
            ResultSet resultset = sql.executeQuery();
            boolean containsPlayer = resultset.next();
            sql.close();
            resultset.close();
            return containsPlayer;
        } catch (Exception e) {
            return false;
        }
    }

    public static synchronized void setupData(Player player) {
        PlayerInventory pi = player.getInventory();
        byte[] invdata = serial(pi.getContents());
        byte[] armordata = serial(pi.getArmorContents());
        try (Connection connection = MCMagicCore.getInstance().permSqlUtil.getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("INSERT INTO `inv_data` values(0,?,?,?)");
            sql.setString(1, player.getUniqueId() + "");
            sql.setBytes(2, invdata);
            sql.setBytes(3, armordata);
            sql.execute();
            sql.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void setupEndData(Player player) {
        Inventory endinv = player.getEnderChest();
        byte[] endinvdata = serial(endinv.getContents());
        try (Connection connection = MCMagicCore.getInstance().permSqlUtil.getConnection()) {
            PreparedStatement endsql = connection
                    .prepareStatement("INSERT INTO `endinv_data` values(0,?,?)");
            endsql.setString(1, player.getUniqueId() + "");
            endsql.setBytes(2, endinvdata);
            endsql.execute();
            endsql.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized static List<ItemStack[]> invContents(UUID uuid) {
        Connection connection = SqlUtil.getConnection();
        try {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM `inv_data` WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet results = sql.executeQuery();
            if (!results.next()) {
                results.close();
                sql.close();
                return null;
            }
            byte[] inv = results.getBytes("content");
            byte[] armor = results.getBytes("armor");
            results.close();
            sql.close();
            ItemStack[] invStuff = deserial(inv);
            ItemStack[] armStuff = deserial(armor);
            return Arrays.asList(invStuff, armStuff);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized static ItemStack[] endInvContents(UUID uuid) {
        try (Connection connection = MCMagicCore.getInstance().permSqlUtil.getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("SELECT content FROM `endinv_data` WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet results = sql.executeQuery();
            results.next();
            byte[] data = results.getBytes("content");
            results.close();
            sql.close();
            return deserial(data);
        } catch (Exception e) {
            e.printStackTrace();
            return new ItemStack[]{};
        }
    }

    public static ItemStack[] deserial(byte[] b) {
        return deserializeItemStacks(uncompress(b));
    }

    public static byte[] serial(ItemStack[] stacks) {
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
            ex.printStackTrace();
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
            ex.printStackTrace();
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
            ex.printStackTrace();
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
            ex.printStackTrace();
            return null;
        } finally {
            try {
                if (decompressor != null)
                    decompressor.close();
            } catch (IOException ignored) {
            }
        }
    }
}