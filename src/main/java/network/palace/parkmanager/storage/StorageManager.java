package network.palace.parkmanager.storage;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.PlayerStatus;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.dashboard.packets.parks.PacketInventoryContent;
import network.palace.parkmanager.handlers.InventoryType;
import network.palace.parkmanager.handlers.PlayerData;
import network.palace.parkmanager.listeners.BlockEdit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Marc on 10/10/15
 */
public class StorageManager {
    private List<UUID> loadingPack = new ArrayList<>();
    private List<UUID> loadingLocker = new ArrayList<>();
    private HashMap<UUID, PacketInventoryContent> cachedInventories = new HashMap<>();
    private HashMap<UUID, ItemStack[]> buildModeHotbars = new HashMap<>();
    public List<UUID> makeBuildMode = new ArrayList<>();

    public StorageManager() {
        Bukkit.getScheduler().runTaskTimer(ParkManager.getInstance(), () -> {
            for (UUID uuid : new ArrayList<>(loadingPack)) {
                PlayerData data = ParkManager.getPlayerData(uuid);
                Player tp = Bukkit.getPlayer(uuid);
                if (tp == null || data == null) {
                    continue;
                }
                if (data.getBackpack() != null) {
                    if (tp.getOpenInventory() != null &&
                            tp.getOpenInventory().getTopInventory().getName().contains("Loading")) {
                        loadingPack.remove(uuid);
                        ParkManager.inventoryUtil.openInventory(tp, InventoryType.BACKPACK);
                    }
                }
            }
            for (UUID uuid : new ArrayList<>(loadingLocker)) {
                PlayerData data = ParkManager.getPlayerData(uuid);
                Player tp = Bukkit.getPlayer(uuid);
                if (tp == null || data == null) {
                    continue;
                }
                if (data.getLocker() != null) {
                    if (tp.getOpenInventory() != null &&
                            tp.getOpenInventory().getTopInventory().getName().contains("Loading")) {
                        loadingLocker.remove(uuid);
                        ParkManager.inventoryUtil.openInventory(tp, InventoryType.LOCKER);

                    }
                }
            }
        }, 0L, 10L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(ParkManager.getInstance(), () ->
                Bukkit.getOnlinePlayers().forEach(this::update), 0L, 1200L);
    }
/*
    public void downloadInventory(UUID uuid, boolean force) {
        if (ParkManager.playerJoinAndLeave.isSet(uuid) && !force) {
            return;
        }
        ParkManager.playerJoinAndLeave.setInventory(uuid);
        final CPlayer player = Core.getPlayerManager().getPlayer(uuid);
        if (player == null) {
            return;
        }
        final PlayerInventory inv = player.getInventory();
        Backpack pack = ParkManager.storageManager.getBackpack(player);
        Locker locker = ParkManager.storageManager.getLocker(player);
        Inventory p = pack.getInventory();
        Inventory l = locker.getInventory();
        p.remove(Material.MINECART);
        p.remove(Material.SNOW_BALL);
        p.remove(Material.EGG);
        l.remove(Material.MINECART);
        l.remove(Material.SNOW_BALL);
        l.remove(Material.EGG);
        final ItemStack[] hotbar = ParkManager.storageManager.getHotbar(player);
        ParkManager.playerJoinAndLeave.setInventory(player, true);
        if (hotbar != null) {
            ItemStack[] cont = inv.getContents();
            System.arraycopy(hotbar, 0, cont, 0, hotbar.length >= 4 ? 4 : hotbar.length);
            inv.setContents(Arrays.copyOfRange(cont, 0, 36));
        }
        if (player.getRank().getRankId() > Rank.SQUIRE.getRankId()) {
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

    public Backpack getBackpack(CPlayer player) {
        try (Connection connection = Core.getSqlUtil().getConnection()) {
            PreparedStatement pack = connection.prepareStatement("SELECT pack,packsize FROM storage WHERE uuid=? AND resort=?");
            pack.setString(1, player.getUniqueId().toString());
            pack.setInt(2, ParkManager.resort.getId());
            ResultSet result = pack.executeQuery();
            if (!result.next()) {
                result.close();
                pack.close();
                PreparedStatement insert = connection.prepareStatement("INSERT INTO storage (uuid, pack, packsize, " +
                        "locker, lockersize, hotbar, resort) VALUES (?,?,0,?,0,?,?)");
                insert.setString(1, player.getUniqueId().toString());
                insert.setString(2, new JSONObject().toString());
                insert.setString(3, new JSONObject().toString());
                insert.setString(4, new JSONObject().toString());
                insert.setInt(5, ParkManager.resort.getId());
                insert.execute();
                insert.close();
                return new Backpack(player, StorageSize.SMALL, new ItemStack[]{});
            }
            StorageSize size = StorageSize.fromInt(result.getInt("packsize"));
            String json = result.getString("pack");
            ItemStack[] items = ItemUtil.getInventoryFromJson(json, size.getSlots());
            Backpack backpack = new Backpack(player, size, items);
            result.close();
            pack.close();
            return backpack;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Locker getLocker(CPlayer player) {
        try (Connection connection = Core.getSqlUtil().getConnection()) {
            PreparedStatement pack = connection.prepareStatement("SELECT locker,lockersize FROM storage WHERE uuid=? AND resort=?");
            pack.setString(1, player.getUniqueId().toString());
            pack.setInt(2, ParkManager.resort.getId());
            ResultSet result = pack.executeQuery();
            if (!result.next()) {
                result.close();
                pack.close();
                PreparedStatement insert = connection.prepareStatement("INSERT INTO storage (uuid, pack, packsize, " +
                        "locker, lockersize, hotbar,resort) VALUES (?,?,0,?,0,?,?)");
                insert.setString(1, player.getUniqueId().toString());
                insert.setString(2, new JSONObject().toString());
                insert.setString(3, new JSONObject().toString());
                insert.setString(4, new JSONObject().toString());
                insert.setInt(5, ParkManager.resort.getId());
                insert.execute();
                insert.close();
                return new Locker(player, StorageSize.SMALL, new ItemStack[]{});
            }
            StorageSize size = StorageSize.fromInt(result.getInt("lockersize"));
            String json = result.getString("locker");
            ItemStack[] items = ItemUtil.getInventoryFromJson(json, size.getSlots());
            Locker locker = new Locker(player, size, items);
            result.close();
            pack.close();
            return locker;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }*/

    public void logout(final Player player) {
        loadingPack.remove(player.getUniqueId());
        loadingLocker.remove(player.getUniqueId());
        final PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
        final boolean build = BlockEdit.isInBuildMode(player.getUniqueId());
        cachedInventories.remove(player.getUniqueId());
        Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), () -> {
            final long time = System.currentTimeMillis();
            update(player, data, build, true);
            removeHotbar(player.getUniqueId());
            //System.out.println("Total Processing Time: " + (System.currentTimeMillis() - time) + "ms");
        });
    }

    private void update(Player player) {
        update(player, ParkManager.getPlayerData(player.getUniqueId()));
    }


    private void update(Player player, PlayerData data) {
        update(player, data, BlockEdit.isInBuildMode(player.getUniqueId()));
    }

    private void update(Player player, PlayerData data, boolean build) {
        update(player, data, build, false);
    }

    private void update(Player player, PlayerData data, boolean build, boolean force) {
        if (data == null || data.getBackpack() == null || data.getLocker() == null) {
            return;
        }
        if (System.currentTimeMillis() - data.getLastInventoryUpdate() > (5 * 60 * 1000) && !force) {
            return;
        }
        long cur = System.currentTimeMillis();
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
                if (i == null) {
                    i = new ItemStack(Material.AIR);
                }
                bp.addItem(i);
            }
        }
        ItemStack[] hotbar = new ItemStack[4];
        if (build) {
            ItemStack[] h = getBuildModeHotbars().get(player.getUniqueId());
            for (int i = 0; i < 4; i++) {
                try {
                    hotbar[i] = h[i];
                } catch (Exception ignored) {
                }
            }
        } else {
            ItemStack[] cont = player.getInventory().getContents();
            System.arraycopy(cont, 0, hotbar, 0, 4);
        }
        String packjson = ItemUtil.getJsonFromInventory(bp).toString();
        String packhash = generateHash(packjson);
        String lockerjson = ItemUtil.getJsonFromInventory(locker.getInventory()).toString();
        String lockerhash = generateHash(lockerjson);
        String hotbarjson = ItemUtil.getJsonFromArray(hotbar).toString();
        String hotbarhash = generateHash(hotbarjson);
        if (packhash.equals(data.getBackpackHash())) {
            packjson = "";
            packhash = "";
        } else {
            data.setBackpackHash(packhash);
        }
        if (lockerhash.equals(data.getLockerHash())) {
            lockerjson = "";
            lockerhash = "";
        } else {
            data.setLockerHash(lockerhash);
        }
        if (hotbarhash.equals(data.getHotbarHash())) {
            hotbarjson = "";
            hotbarhash = "";
        } else {
            data.setHotbarHash(hotbarhash);
        }
        if (packhash.isEmpty() && lockerhash.isEmpty() && hotbarhash.isEmpty()) {
            Bukkit.getLogger().info("Skipped updating " + player.getName() + "'s inventory, no change!");
        }
        PacketInventoryContent packet = new PacketInventoryContent(player.getUniqueId(), ParkManager.resort,
                packjson, packhash, data.getBackpack().getSize().ordinal(),
                lockerjson, lockerhash, data.getLocker().getSize().ordinal(),
                hotbarjson, hotbarhash);
        Core.getDashboardConnection().send(packet);
        Bukkit.getLogger().info("Updated " + player.getName() + "'s inventory in " + (System.currentTimeMillis() - cur) + "ms");
    }

    public void setLoadingPack(Player player) {
        loadingPack.remove(player.getUniqueId());
        loadingPack.add(player.getUniqueId());
    }

    public void setLoadingLocker(Player player) {
        loadingLocker.remove(player.getUniqueId());
        loadingLocker.add(player.getUniqueId());
    }
/*
    public ItemStack[] getHotbar(CPlayer player) {
        try (Connection connection = Core.getSqlUtil().getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT hotbar FROM storage WHERE uuid=? AND resort=?");
            sql.setString(1, player.getUniqueId().toString());
            sql.setInt(2, ParkManager.resort.getId());
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                return null;
            }
            String json = result.getString("hotbar");
            result.close();
            sql.close();
            return ItemUtil.getInventoryFromJson(json, 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }*/

    public void setValue(final UUID uuid, final String key, final String value) {
        Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), () -> {
            try (Connection connection = Core.getSqlUtil().getConnection()) {
                PreparedStatement sql = connection.prepareStatement("UPDATE storage SET " + key + "=? WHERE uuid=? AND resort=?");
                sql.setString(1, value);
                sql.setString(2, uuid.toString());
                sql.setInt(3, ParkManager.resort.getId());
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

    public void updateInventory(PacketInventoryContent packet) {
        UUID uuid = packet.getUuid();
        if (cachedInventories.containsKey(uuid)) {
            return;
        }
        ItemStack[] packItems = ItemUtil.getInventoryFromJson(packet.getBackpackJson());
        ItemStack[] lockerItems = ItemUtil.getInventoryFromJson(packet.getLockerJson());
        ItemStack[] hotbar = ItemUtil.getInventoryFromJson(packet.getHotbarJson());
        packet.setBackpack(packItems);
        packet.setLocker(lockerItems);
        packet.setHotbar(hotbar);
        cachedInventories.put(uuid, packet);
        CPlayer player = Core.getPlayerManager().getPlayer(uuid);
        if (player == null || !player.getStatus().equals(PlayerStatus.JOINED)) {
            return;
        }
        setInventory(uuid);
    }

    public void setInventory(UUID uuid) {
        CPlayer player = Core.getPlayerManager().getPlayer(uuid);
        final PlayerInventory inv = player.getInventory();
        PlayerData data = ParkManager.getPlayerData(uuid);
        PacketInventoryContent packet = cachedInventories.remove(player.getUniqueId());

        StorageSize bsize = StorageSize.fromInt(packet.getLockerSize());
        StorageSize lsize = StorageSize.fromInt(packet.getLockerSize());

        ItemStack[] packItems = ItemUtil.getInventoryFromJson(packet.getBackpackJson());
        ItemStack[] lockerItems = ItemUtil.getInventoryFromJson(packet.getLockerJson());
        final ItemStack[] hotbar = ItemUtil.getInventoryFromJson(packet.getHotbarJson());

        Backpack pack = new Backpack(player, bsize, packItems);
        Locker locker = new Locker(player, lsize, lockerItems);

        Inventory p = pack.getInventory();
        Inventory l = locker.getInventory();

        p.remove(Material.MINECART);
        p.remove(Material.SNOW_BALL);
        p.remove(Material.EGG);
        l.remove(Material.MINECART);
        l.remove(Material.SNOW_BALL);
        l.remove(Material.EGG);

        data.setBackpack(pack);
        data.setLocker(locker);

        ParkManager.playerJoinAndLeave.setInventory(player, true);
        if (hotbar != null) {
            ItemStack[] cont = inv.getContents();
            System.arraycopy(hotbar, 0, cont, 0, hotbar.length >= 4 ? 4 : hotbar.length);
            inv.setContents(Arrays.copyOfRange(cont, 0, 36));
        }
        if (player.getRank().getRankId() > Rank.SQUIRE.getRankId()) {
            if (inv.getItem(0) == null || inv.getItem(0).getType().equals(Material.AIR)) {
                inv.setItem(0, new ItemStack(Material.COMPASS));
            }
            if (makeBuildMode.remove(player.getUniqueId())) {
                Bukkit.getScheduler().runTaskLater(ParkManager.getInstance(), () -> player.performCommand("build"), 20L);
            }
        } else {
            inv.remove(Material.COMPASS);
        }
        player.getInventory().remove(Material.MINECART);
    }

    public void join(UUID uuid, boolean force) {
        if (ParkManager.playerJoinAndLeave.isSet(uuid) && !force) {
            return;
        }
        if (!cachedInventories.containsKey(uuid)) {
            return;
        }
        setInventory(uuid);
    }

    /**
     * Generate hash for inventory JSON
     *
     * @param inventory the JSON
     * @return MD5 hash of inventory
     */
    private String generateHash(String inventory) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(inventory.getBytes());
            return DatatypeConverter.printHexBinary(digest.digest()).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("NO MD5?");
            return "null";
        }
    }
}
