package network.palace.parkmanager.hotels;

import network.palace.core.Core;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.HotelRoom;
import network.palace.parkwarp.handlers.Warp;
import network.palace.parkmanager.listeners.PlayerInteract;
import network.palace.parkmanager.utils.PlayerUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Greenlock28 on 1/23/2015.
 */
public class HotelManager {
    private List<HotelRoom> hotelRooms = new ArrayList<>();
    private List<HotelRoom> closeDoors = new ArrayList<>();

    public HotelManager() {
        initialize();
    }

    private void initialize() {
        ParkManager parkManager = ParkManager.getInstance();
        Bukkit.getScheduler().runTaskTimerAsynchronously(parkManager, () -> hotelRooms.stream()
                .filter(room -> room.getCheckoutNotificationRecipient() != null).forEach(room -> {
                    UUID uuid = room.getCheckoutNotificationRecipient();
                    Player tp = Bukkit.getPlayer(uuid);
                    if (tp != null && tp.isOnline()) {
                        checkout(room, true);
                    }
                }), 10L, 6000L);
        if (!parkManager.isHotelServer()) {
            return;
        }
        refreshRooms();
        Bukkit.getScheduler().runTaskTimerAsynchronously(parkManager, () -> hotelRooms.stream()
                .filter(room -> room.isOccupied() && room.getCheckoutTime() <= (System.currentTimeMillis() / 1000))
                .forEach(room -> checkout(room, true)), 0L, 6000L);
        final List<HotelRoom> closeDoorsList = new ArrayList<>(closeDoors);
        Bukkit.getScheduler().runTaskTimer(parkManager, () -> closeDoorsList.forEach(room -> {
            closeDoor(room);
            closeDoors.remove(room);
        }), 0L, 5L);
    }

    public List<HotelRoom> getHotelRooms() {
        return new ArrayList<>(hotelRooms);
    }

    public List<HotelRoom> getRooms() {
        List<HotelRoom> rooms = new ArrayList<>();
        try (Connection connection = Core.getSqlUtil().getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM hotelrooms");
            ResultSet result = sql.executeQuery();
            while (result.next()) {
                HotelRoom room = new HotelRoom(result.getString("hotelName"), result.getInt("roomNumber"),
                        result.getString("currentOccupant").isEmpty() ? null :
                                UUID.fromString(result.getString("currentOccupant")), result.getString("occupantName"),
                        result.getLong("checkoutTime"), Warp.fromDatabaseString(!result.getString("roomWarp").isEmpty() ?
                        result.getString("roomWarp") : null), result.getInt("cost"),
                        !result.getString("checkoutNotificationRecipient").isEmpty() ?
                                UUID.fromString(result.getString("checkoutNotificationRecipient")) : null,
                        result.getLong("stayLength"), result.getInt("x"), result.getInt("y"), result.getInt("z"),
                        result.getInt("suite") == 1);
                rooms.add(room);
            }
            result.close();
            sql.close();
            return rooms;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @SuppressWarnings("deprecation")
    public void closeDoor(HotelRoom room) {
        if (room == null)
            return;
        Block s = new Location(Bukkit.getWorlds().get(0), room.getX(), room.getY(), room.getZ()).getBlock();
        Sign sign;
        Material type = s.getType();
        if (type.equals(Material.WALL_SIGN)) {
            sign = (Sign) s.getState();
        } else {
            return;
        }
        Block door = getDoorFromSign(s);
        Chunk c = door.getChunk();
        if (!c.isLoaded()) {
            c.load();
        }
        byte originalData;
        BlockFace face = getFace(sign.getRawData());
        switch (face) {
            case NORTH:
                originalData = 1;
                break;
            case SOUTH:
                originalData = 3;
                break;
            case EAST:
                originalData = 2;
                break;
            case WEST:
                originalData = 0;
                break;
            default:
                return;
        }
        door.setData(originalData);
    }

    @SuppressWarnings("deprecation")
    public Location locFromSign(Sign sign) {
        BlockFace facing = getFace(sign.getRawData());
        if (facing == null) {
            return null;
        }
        Location loc = null;
        switch (facing) {
            case NORTH:
                loc = new Location(sign.getWorld(), sign.getX() + 0.5, sign.getY() - 1, sign.getZ() + 0.5, 0, 0);
                break;
            case EAST:
                loc = new Location(sign.getWorld(), sign.getX() + 0.5, sign.getY() - 1, sign.getZ() + 0.5, 90, 0);
                break;
            case SOUTH:
                loc = new Location(sign.getWorld(), sign.getX() + 0.5, sign.getY() - 1, sign.getZ() + 0.5, 180, 0);
                break;
            case WEST:
                loc = new Location(sign.getWorld(), sign.getX() + 0.5, sign.getY() - 1, sign.getZ() + 0.5, -90, 0);
                break;
        }
        return loc;
    }

    public BlockFace getFace(byte data) {
        switch (data) {
            case 0:
                return BlockFace.NORTH;
            case 1:
                return BlockFace.NORTH;
            case 2:
                return BlockFace.NORTH;
            case 3:
                return BlockFace.SOUTH;
            case 4:
                return BlockFace.WEST;
            case 5:
                return BlockFace.EAST;
        }
        return BlockFace.SELF;
    }

    public HotelRoom getRoom(String name) {
        for (HotelRoom room : hotelRooms) {
            if (room.getName().equalsIgnoreCase(name)) {
                return room;
            }
        }
        return null;
    }

    public void addRoom(HotelRoom room) {
        if (getRoom(room.getName()) != null) {
            updateHotelRoom(room);
            return;
        }
        try (Connection connection = Core.getSqlUtil().getConnection()) {
            PreparedStatement sql = connection.prepareStatement("INSERT INTO hotelrooms (hotelName,roomNumber,currentOccupant,occupantName,checkoutTime,roomWarp,cost,checkoutNotificationRecipient,name,stayLength,x,z,y) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
            sql.setString(1, room.getHotelName());
            sql.setInt(2, room.getRoomNumber());
            sql.setString(3, room.getCurrentOccupant() != null ? room.getCurrentOccupant().toString() : "");
            sql.setString(4, room.getOccupantName() != null ? room.getOccupantName() : "");
            sql.setLong(5, room.getCheckoutTime());
            sql.setString(6, room.getWarp() != null ? room.getWarp().toDatabaseString() : "");
            sql.setInt(7, room.getCost());
            sql.setString(8, room.getCheckoutNotificationRecipient() != null ? room.getCheckoutNotificationRecipient().toString() : "");
            sql.setString(9, room.getName());
            sql.setLong(10, room.getStayLength());
            sql.setInt(11, room.getX());
            sql.setInt(12, room.getY());
            sql.setInt(13, room.getZ());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        hotelRooms.add(room);
    }

    public void updateHotelRoom(HotelRoom room) {
        try (Connection connection = Core.getSqlUtil().getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE hotelrooms SET currentOccupant=?, occupantName=?, checkoutTime=?, checkoutNotificationRecipient=?, roomWarp=? WHERE name=?");
            sql.setString(1, room.getCurrentOccupant() == null ? "" : room.getCurrentOccupant().toString());
            sql.setString(2, room.getOccupantName() == null ? "" : room.getOccupantName());
            sql.setLong(3, room.getCheckoutTime());
            sql.setString(4, room.getCheckoutNotificationRecipient() == null ? "" : room.getCheckoutNotificationRecipient().toString());
            sql.setString(5, room.getWarp() != null ? room.getWarp().toDatabaseString() : "");
            sql.setString(6, room.getName());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeRoom(HotelRoom room) {
        try (Connection connection = Core.getSqlUtil().getConnection()) {
            PreparedStatement sql = connection.prepareStatement("DELETE FROM hotelrooms WHERE name=?");
            sql.setString(1, room.getName());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRooms() {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("UpdateHotelRooms");
            out.writeUTF(Core.getInstanceName());
            PlayerUtil.randomPlayer().sendPluginMessage(ParkManager.getInstance(), "BungeeCord",
                    b.toByteArray());
        } catch (Exception e) {
            Bukkit.getServer()
                    .getLogger()
                    .severe("There was an error contacting the Bungee server to update Hotel Rooms!");
        }
    }

    public void refreshRooms() {
        hotelRooms.clear();
        for (HotelRoom room : getRooms()) {
            hotelRooms.add(room);
            Block b = new Location(Bukkit.getWorlds().get(0), room.getX(), room.getY(), room.getZ()).getBlock();
            if (!ParkManager.getInstance().isSign(b.getLocation())) {
                continue;
            }
            Sign s = (Sign) b.getState();
            s.setLine(0, room.isSuite() ? PlayerInteract.suite : PlayerInteract.hotel);
            s.setLine(1, "" + ChatColor.GOLD + room.getRoomNumber());
            s.setLine(2, ChatColor.DARK_GREEN + room.getHotelName());
            if (room.isOccupied()) {
                s.setLine(3, room.getOccupantName());
            } else {
                s.setLine(3, ChatColor.GREEN + "$" + Integer.toString(room.getCost()));
            }
            s.update();
        }
    }

    public void serverStop() {
        if (!ParkManager.getInstance().isHotelServer()) {
            return;
        }
        hotelRooms.stream().filter(room -> room.getCheckoutTime() <= (System.currentTimeMillis() / 1000) &&
                room.getCheckoutTime() != 0).forEach(room -> {
            room.setCheckoutNotificationRecipient(room.getCurrentOccupant());
            room.setCurrentOccupant(null);
            room.setCheckoutTime(0);
            updateHotelRoom(room);
        });
    }

    public Block getDoorFromSign(Block b) {
        for (int ix = b.getLocation().getBlockX() - 1; ix < b.getLocation().getBlockX() + 2; ix++) {
            for (int iz = b.getLocation().getBlockZ() - 1; iz < b.getLocation().getBlockZ() + 2; iz++) {
                for (int iy = b.getLocation().getBlockY() - 1; iy < b.getLocation().getBlockY() + 2; iy++) {
                    Block targetBlock = b.getWorld().getBlockAt(ix, iy, iz);
                    Material type = targetBlock.getType();
                    if (type.name().toLowerCase().contains("door")) {
                        return targetBlock;
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public HotelRoom getRoomFromDoor(Block b, Player p) {
        Location bloc = b.getLocation().clone();
        for (int ix = bloc.getBlockX() - 1; ix < bloc.getBlockX() + 2; ix++) {
            for (int iz = bloc.getBlockZ() - 1; iz < bloc.getBlockZ() + 2; iz++) {
                for (int iy = bloc.getBlockY() - 1; iy < bloc.getBlockY() + 2; iy++) {
                    Block targetBlock = b.getWorld().getBlockAt(ix, iy, iz);
                    Material type = targetBlock.getType();
                    if (type == Material.SIGN_POST || type == Material.WALL_SIGN) {
                        Sign s = (Sign) targetBlock.getState();
                        BlockFace facing = getFace(s.getRawData());
                        if (facing == null) {
                            return null;
                        }
                        boolean isSign = false;
                        switch (facing) {
                            case NORTH: {
                                Location temp = s.getLocation().clone().add(0, 0, 1);
                                if (bloc.getBlockZ() == temp.getBlockZ()) {
                                    isSign = true;
                                }
                                break;
                            }
                            case EAST: {
                                Location temp = s.getLocation().clone().add(-1, 0, 0);
                                if (bloc.getBlockX() == temp.getBlockX()) {
                                    isSign = true;
                                }
                                break;
                            }
                            case SOUTH: {
                                Location temp = s.getLocation().clone().add(0, 0, -1);
                                if (bloc.getBlockZ() == temp.getBlockZ()) {
                                    isSign = true;
                                }
                                break;
                            }
                            case WEST: {
                                Location temp = s.getLocation().clone().add(1, 0, 0);
                                if (bloc.getBlockX() == temp.getBlockX()) {
                                    isSign = true;
                                }
                                break;
                            }
                        }
                        if (!isSign) {
                            continue;
                        }
                        HotelRoom room = getRoomFromSign(s);
                        if (room != null) {
                            return room;
                        }
                    }
                }
            }
        }
        return null;
    }

    public HotelRoom getRoomFromSign(Sign s) {
        if (!ChatColor.stripColor(s.getLine(0)).equalsIgnoreCase("[hotel]") || s.getLine(1).equals("") ||
                s.getLine(2).equals("")) {
            return null;
        }
        return getRoom(ChatColor.stripColor(s.getLine(2)) + " #" + ChatColor.stripColor(s.getLine(1)));
    }

    public void rentRoom(HotelRoom room, Player player) {
        room.setCurrentOccupant(player.getUniqueId());
        room.setOccupantName(player.getName());
        room.setCheckoutTime((System.currentTimeMillis() / 1000) + room.getStayLength());
        updateHotelRoom(room);
        updateRooms();
        Block b = player.getWorld().getBlockAt(room.getX(), room.getY(), room.getZ());
        Material type = b.getType();
        if (type.equals(Material.SIGN) || type.equals(Material.SIGN_POST) || type.equals(Material.WALL_SIGN)) {
            Sign s = (Sign) b.getState();
            s.setLine(3, player.getName());
            s.update();
        }
        player.closeInventory();
        player.sendMessage(ChatColor.GREEN + "You have booked the " + room.getName() + " room for $" + room.getCost() +
                "!");
        player.sendMessage(ChatColor.GREEN + "You can travel to your room using the My Hotel Rooms menu on your MagicBand.");
        ParkManager.getInstance().logActivity(player, "Reserve Hotel Room", room.getHotelName() + " #" + room.getRoomNumber());
        Core.getPlayerManager().getPlayer(player.getUniqueId()).giveAchievement(10);
    }

    public void checkout(HotelRoom room, boolean lapsed) {
        Player tp = Bukkit.getPlayer(room.getCurrentOccupant());
        boolean hotelServer = ParkManager.getInstance().isHotelServer();
        if (room == null || room.getWarp() == null || room.getWarp().getWorld() == null) return;
        try {
            if (hotelServer && room.getWarp().getWorld().equals(Bukkit.getWorlds().get(0))) {
                closeDoors.add(room);
            }
        } catch (NullPointerException ignored) {
            return;
        }
        room.setCheckoutTime(0);
        if (tp != null) {
            if (room.getWarp() != null && hotelServer &&
                    room.getWarp().getWorld().getUID().equals(tp.getWorld().getUID())) {
                Warp w = room.getWarp();
                if (tp.getLocation().distance(w.getLocation()) < 25) {
                    tp.teleport(w.getLocation());
                }
            }
            if (lapsed) {
                tp.sendMessage(ChatColor.GREEN + "Your reservation of the " + room.getName() +
                        " room has lapsed and you have been checked out. Please come stay with us again soon!");
                tp.sendTitle(ChatColor.RED + "Your Hotel Room Expired", "", 40, 60, 40);
                tp.playSound(tp.getLocation(), Sound.ENTITY_BLAZE_DEATH, 10f, 1f);
                ParkManager.getInstance().logActivity(tp, "Hotel Room Checkout", "Reservation Expired");
            } else {
                tp.sendMessage(ChatColor.GREEN + "You have checked out of your room. Have a wonderful rest of your visit!");
                ParkManager.getInstance().logActivity(tp, "Hotel Room Checkout", "Manual Checkout");
            }
        } else {
            room.setCheckoutNotificationRecipient(room.getCurrentOccupant());
        }
        if (!lapsed && hotelServer && room.getWarp().getWorld().getUID().equals(tp.getWorld().getUID())) {
            Block b = tp.getWorld().getBlockAt(room.getX(), room.getY(), room.getZ());
            Material type = b.getType();
            if (type.equals(Material.SIGN) || type.equals(Material.SIGN_POST) || type.equals(Material.WALL_SIGN)) {
                Sign s = (Sign) b.getState();
                s.setLine(3, ChatColor.GREEN + "$" + room.getCost());
                s.update();
            }
        }
        room.setCurrentOccupant(null);
        room.setOccupantName(null);
        updateHotelRoom(room);
        updateRooms();
    }

    public void closeDoor(Player player) {
        for (HotelRoom room : getHotelRooms()) {
            if (room.isOccupied() && room.getCurrentOccupant().equals(player.getUniqueId())) {
                closeDoors.add(room);
                break;
            }
        }
    }
}