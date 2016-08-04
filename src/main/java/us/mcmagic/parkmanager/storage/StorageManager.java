package us.mcmagic.parkmanager.storage;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.InventoryType;
import us.mcmagic.parkmanager.handlers.PlayerData;
import us.mcmagic.parkmanager.listeners.BlockEdit;
import us.mcmagic.parkmanager.utils.SqlUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Marc on 10/10/15
 */
public class StorageManager {
    private List<UUID> loadingPack = new ArrayList<>();
    private List<UUID> loadingLocker = new ArrayList<>();
    private HashMap<UUID, ItemStack[]> buildModeHotbars = new HashMap<>();
    public List<UUID> makeBuildMode = new ArrayList<>();

    public StorageManager() {
        Bukkit.getScheduler().runTaskTimer(ParkManager.getInstance(), () -> {
            for (UUID uuid : new ArrayList<>(loadingPack)) {
                PlayerData data = ParkManager.getPlayerData(uuid);
                if (data.getBackpack() != null) {
                    Player tp = Bukkit.getPlayer(uuid);
                    if (tp == null) {
                        continue;
                    }
                    if (tp.getOpenInventory() != null &&
                            tp.getOpenInventory().getTopInventory().getName().contains("Loading")) {
                        loadingPack.remove(uuid);
                        ParkManager.inventoryUtil.openInventory(tp, InventoryType.BACKPACK);
                    }
                }
            }
            for (UUID uuid : new ArrayList<>(loadingLocker)) {
                PlayerData data = ParkManager.getPlayerData(uuid);
                if (data.getBackpack() != null) {
                    Player tp = Bukkit.getPlayer(uuid);
                    if (tp == null) {
                        continue;
                    }
                    if (tp.getOpenInventory() != null &&
                            tp.getOpenInventory().getTopInventory().getName().contains("Loading")) {
                        loadingLocker.remove(uuid);
                        ParkManager.inventoryUtil.openInventory(tp, InventoryType.LOCKER);
                    }
                }
            }
        }, 0L, 10L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(ParkManager.getInstance(), () ->
                Bukkit.getOnlinePlayers().forEach(this::update), 0L, 6000L);
    }

    public void downloadInventory(UUID uuid) {
        final Player player = Bukkit.getPlayer(uuid);
        User user = MCMagicCore.getUser(uuid);
        if (player == null || user == null) {
            return;
        }
        ParkManager.playerJoinAndLeave.setInventory(player.getUniqueId());
        final PlayerInventory inv = player.getInventory();
        Backpack pack = ParkManager.storageManager.getBackpack(player);
        pack.getInventory().remove(Material.MINECART);
        pack.getInventory().remove(Material.SNOW_BALL);
        Locker locker = ParkManager.storageManager.getLocker(player);
        locker.getInventory().remove(Material.MINECART);
        locker.getInventory().remove(Material.SNOW_BALL);
        final ItemStack[] hotbar = ParkManager.storageManager.getHotbar(player);
        ParkManager.playerJoinAndLeave.setInventory(player, true);
        if (hotbar != null) {
            ItemStack[] cont = inv.getContents();
            for (int i = 0; i < 4; i++) {
                cont[i] = hotbar[i];
            }
            inv.setContents(Arrays.copyOfRange(cont, 0, 36));
        }
        if (user.getRank().getRankId() > Rank.EARNINGMYEARS.getRankId()) {
            if (inv.getItem(0) == null || inv.getItem(0).getType().equals(Material.AIR)) {
                inv.setItem(0, new ItemStack(Material.COMPASS));
            }
            if (makeBuildMode.remove(player.getUniqueId())) {
                Bukkit.getScheduler().runTaskLater(ParkManager.getInstance(), () -> player.performCommand("build"), 20L);
            }
        } else {
            inv.remove(Material.COMPASS);
        }
        PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
        data.setBackpack(pack);
        data.setLocker(locker);
        player.getInventory().remove(Material.MINECART);
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
        if (stacks == null) {
            return new byte[]{};
        }
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
        final PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
        final boolean build = BlockEdit.isInBuildMode(player.getUniqueId());
        Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), () -> {
            try {
                final long time = System.currentTimeMillis();
                update(player, data, build);
                if (Bukkit.getOnlinePlayers().size() > 0) {
                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(b);
                    out.writeUTF("Uploaded");
                    out.writeUTF(player.getUniqueId().toString());
                    ((Player) Bukkit.getOnlinePlayers().toArray()[0]).sendPluginMessage(ParkManager.getInstance(),
                            "BungeeCord", b.toByteArray());
                }
                removeHotbar(player.getUniqueId());
                //System.out.println("Total Processing Time: " + (System.currentTimeMillis() - time) + "ms");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void update(Player player) {
        update(player, ParkManager.getPlayerData(player.getUniqueId()));
    }


    private void update(Player player, PlayerData data) {
        update(player, data, BlockEdit.isInBuildMode(player.getUniqueId()));
    }

    private void update(Player player, PlayerData data, boolean build) {
        Backpack pack = data.getBackpack();
        Locker locker = data.getLocker();
        if (pack == null || locker == null) {
            return;
        }
        Inventory bp = pack.getInventory();
        if (build) {
            final PlayerInventory inv = player.getInventory();
            bp.clear();
            for (ItemStack i : inv.getContents()) {
                if (i == null || i.getType().equals(Material.AIR) || i.getType().equals(Material.WOOD_AXE) ||
                        i.getType().equals(Material.COMPASS)) {
                    continue;
                }
                bp.addItem(i);
            }
        }
        try (Connection connection = SqlUtil.getConnection()) {
            ItemStack[] hotbar = new ItemStack[4];
            if (build) {
                ItemStack[] h = getBuildModeHotbars().get(player.getUniqueId());
                for (int i = 0; i < 4; i++) {
                    try {
                        hotbar[i] = h[i] == null ? null : h[i];
                    } catch (Exception ignored) {
                    }
                }
            } else {
                ItemStack[] cont = player.getInventory().getContents();
                for (int i = 0; i < 4; i++) {
                    hotbar[i] = cont[i];
                }
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
        Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), () -> {
            try (Connection connection = SqlUtil.getConnection()) {
                PreparedStatement sql = connection.prepareStatement("UPDATE storage SET " + key + "=? WHERE uuid=?");
                sql.setString(1, value);
                sql.setString(2, uuid.toString());
                sql.execute();
                sql.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public HashMap<UUID, ItemStack[]> getBuildModeHotbars() {
        return new HashMap<>(buildModeHotbars);
    }

    public void addHotbar(UUID uuid, ItemStack[] stack) {
        buildModeHotbars.remove(uuid);
        buildModeHotbars.put(uuid, stack);
    }

    public ItemStack[] removeHotbar(UUID uuid) {
        return buildModeHotbars.remove(uuid);
    }
}