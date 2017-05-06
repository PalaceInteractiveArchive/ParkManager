package network.palace.parkmanager.storage;

import com.google.gson.JsonArray;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.dashboard.packets.parks.PacketInventoryStatus;
import network.palace.parkmanager.handlers.InventoryType;
import network.palace.parkmanager.handlers.PlayerData;
import network.palace.parkmanager.listeners.BlockEdit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
                Bukkit.getOnlinePlayers().forEach(this::update), 0L, 1200L);
    }

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
            PreparedStatement pack = connection.prepareStatement("SELECT pack,packsize FROM storage2 WHERE uuid=? AND resort=?");
            pack.setString(1, player.getUniqueId().toString());
            pack.setInt(2, ParkManager.resort.getId());
            ResultSet result = pack.executeQuery();
            if (!result.next()) {
                result.close();
                pack.close();
                PreparedStatement insert = connection.prepareStatement("INSERT INTO storage2 (uuid, pack, packsize, " +
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
            ItemStack[] items = ItemUtil.getInventoryFromJson(json);
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
            PreparedStatement pack = connection.prepareStatement("SELECT locker,lockersize FROM storage2 WHERE uuid=? AND resort=?");
            pack.setString(1, player.getUniqueId().toString());
            pack.setInt(2, ParkManager.resort.getId());
            ResultSet result = pack.executeQuery();
            if (!result.next()) {
                result.close();
                pack.close();
                PreparedStatement insert = connection.prepareStatement("INSERT INTO storage2 (uuid, pack, packsize, " +
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
            ItemStack[] items = ItemUtil.getInventoryFromJson(json);
            Locker locker = new Locker(player, size, items);
            result.close();
            pack.close();
            return locker;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void logout(final Player player) {
        loadingPack.remove(player.getUniqueId());
        loadingLocker.remove(player.getUniqueId());
        final PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
        final boolean build = BlockEdit.isInBuildMode(player.getUniqueId());
        Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), () -> {
            final long time = System.currentTimeMillis();
            update(player, data, build);
            PacketInventoryStatus packet = new PacketInventoryStatus(player.getUniqueId(), 0);
            Core.getDashboardConnection().send(packet);
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
        try (Connection connection = Core.getSqlUtil().getConnection()) {
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
            PreparedStatement sql = connection.prepareStatement("UPDATE storage2 SET pack=?,locker=?,hotbar=? WHERE uuid=? AND resort=?");
            JsonArray parr = ItemUtil.getJsonFromArray(bp.getContents());
            JsonArray larr = ItemUtil.getJsonFromArray(locker.getInventory().getContents());
            JsonArray harr = ItemUtil.getJsonFromArray(hotbar);
            sql.setString(1, parr.toString());
            sql.setString(2, larr.toString());
            sql.setString(3, harr.toString());
            sql.setString(4, player.getUniqueId().toString());
            sql.setInt(5, ParkManager.resort.getId());
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

    public ItemStack[] getHotbar(CPlayer player) {
        try (Connection connection = Core.getSqlUtil().getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT hotbar FROM storage2 WHERE uuid=? AND resort=?");
            sql.setString(1, player.getUniqueId().toString());
            sql.setInt(2, ParkManager.resort.getId());
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                return null;
            }
            String json = result.getString("hotbar");
            result.close();
            sql.close();
            return ItemUtil.getInventoryFromJson(json);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setValue(final UUID uuid, final String key, final String value) {
        Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), () -> {
            try (Connection connection = Core.getSqlUtil().getConnection()) {
                PreparedStatement sql = connection.prepareStatement("UPDATE storage2 SET " + key + "=? WHERE uuid=? AND resort=?");
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
}