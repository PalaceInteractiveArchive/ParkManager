package us.mcmagic.magicassistant.utils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.HotelRoom;
import us.mcmagic.magicassistant.handlers.Warp;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.title.TitleObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
public class HotelUtil {
    public static TitleObject expire = new TitleObject(ChatColor.RED + "Your Hotel Room Expired",
            TitleObject.TitleType.SUBTITLE).setFadeIn(40).setStay(60).setFadeOut(40);

    public static List<HotelRoom> getRooms() {
        List<HotelRoom> rooms = new ArrayList<>();
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM `hotelrooms`");
            ResultSet result = sql.executeQuery();
            while (result.next()) {
                rooms.add(new HotelRoom(result.getString("hotelName"),
                        result.getInt("roomNumber"),
                        result.getString("currentOccupant") == "" ? null : UUID.fromString(result.getString("currentOccupant")),
                        result.getString("occupantName"),
                        result.getLong("checkoutTime"),
                        Warp.fromDatabaseString(result.getString("roomWarp") != "" ? result.getString("roomWarp") : null),
                        result.getInt("cost"),
                        result.getString("checkoutNotificationRecipient") != "" ?
                                UUID.fromString(result.getString("checkoutNotificationRecipient")) : null,
                        result.getLong("stayLength"), result.getInt("x"), result.getInt("y"), result.getInt("z")));
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
    public static void closeDoor(HotelRoom room) {
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
    public static Location locFromSign(Sign sign) {
        BlockFace facing = getFace(sign.getRawData());
        if (facing == null) {
            return null;
        }
        Location loc = null;
        switch (facing) {
            case NORTH:
                loc = new Location(sign.getWorld(), sign.getX() + 0.5, sign.getY() - 1, sign.getZ() - 0.5, 0, 0);
                break;
            case EAST:
                loc = new Location(sign.getWorld(), sign.getX() + 1.5, sign.getY() - 1, sign.getZ() + 0.5, 90, 0);
                break;
            case SOUTH:
                loc = new Location(sign.getWorld(), sign.getX() + 0.5, sign.getY() - 1, sign.getZ() + 1.5, 180, 0);
                break;
            case WEST:
                loc = new Location(sign.getWorld(), sign.getX() - 0.5, sign.getY() - 1, sign.getZ() + 0.5, -90, 0);
                break;
        }
        return loc;
    }

    public static BlockFace getFace(byte data) {
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

    public static HotelRoom getRoom(String name) {
        for (HotelRoom room : MagicAssistant.hotelRooms) {
            if (room.getName().equalsIgnoreCase(name)) {
                return room;
            }
        }
        return null;
    }

    public static void addRoom(HotelRoom room) {
        if (getRoom(room.getName()) != null) {
            updateHotelRoom(room);
            return;
        }
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("INSERT INTO `hotelrooms` values(0,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
        MagicAssistant.hotelRooms.add(room);
    }

    public static void updateHotelRoom(HotelRoom room) {
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE `hotelrooms` SET currentOccupant=?, occupantName=?, checkoutTime=?, checkoutNotificationRecipient=? WHERE name=?");
            sql.setString(1, room.getCurrentOccupant() == null ? "" : room.getCurrentOccupant().toString());
            sql.setString(2, room.getOccupantName() == null ? "" : room.getOccupantName());
            sql.setLong(3, room.getCheckoutTime());
            sql.setString(4, room.getCheckoutNotificationRecipient() == null ? "" : room.getCheckoutNotificationRecipient().toString());
            sql.setString(5, room.getName());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addRoomWithoutCheckingExistance(HotelRoom room) {
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("INSERT INTO `hotelrooms` values(0,?,?,?,?,?,?,?,?,?)");
            sql.setString(1, room.getHotelName());
            sql.setInt(2, room.getRoomNumber());
            sql.setString(3, room.getCurrentOccupant() != null ? room.getCurrentOccupant().toString() : "");
            sql.setLong(4, room.getCheckoutTime());
            sql.setString(5, room.getWarp() != null ? room.getWarp().toDatabaseString() : "");
            sql.setInt(6, room.getCost());
            sql.setString(7, room.getCheckoutNotificationRecipient() != null ? room.getCheckoutNotificationRecipient().toString() : "");
            sql.setString(8, room.getName());
            sql.setLong(9, room.getStayLength());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeRoom(HotelRoom room) {
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("DELETE FROM `hotelrooms` WHERE name=?");
            sql.setString(1, room.getName());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateRooms() {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("UpdateHotelRooms");
            out.writeUTF(MCMagicCore.getMCMagicConfig().serverName);
            PlayerUtil.randomPlayer().sendPluginMessage(MagicAssistant.getInstance(), "BungeeCord",
                    b.toByteArray());
        } catch (IOException e) {
            Bukkit.getServer()
                    .getLogger()
                    .severe("There was an error contacting the Bungee server to update Hotel Rooms!");
        }
    }

    public static void refreshRooms() {
        MagicAssistant.hotelRooms.clear();
        for (HotelRoom room : HotelUtil.getRooms()) {
            MagicAssistant.hotelRooms.add(room);
            Block b = new Location(Bukkit.getWorlds().get(0), room.getX(), room.getY(), room.getZ()).getBlock();
            if (!MagicAssistant.rideManager.isSign(b.getLocation())) {
                continue;
            }
            Sign s = (Sign) b.getState();
            s.setLine(1, "" + ChatColor.GOLD + room.getRoomNumber());
            s.setLine(2, ChatColor.DARK_GREEN + room.getHotelName());
            if (room.isOccupied()) {
                s.setLine(3, room.getOccupantName());
            } else {
                s.setLine(3, ChatColor.GREEN + Integer.toString(room.getCost()));
            }
            s.update();
        }
    }

    public static void serverStop() {
        for (HotelRoom room : MagicAssistant.hotelRooms) {
            if (room.getCheckoutTime() <= (System.currentTimeMillis() / 1000)) {
                room.setCheckoutNotificationRecipient(room.getCurrentOccupant());
                room.setCurrentOccupant(null);
                room.setCheckoutTime(0);
                updateHotelRoom(room);
            }
        }
    }

    public static Block getDoorFromSign(Block b) {
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

    public static HotelRoom getRoomFromDoor(Block b, Player p) {
        for (int ix = b.getLocation().getBlockX() - 1; ix < b.getLocation().getBlockX() + 2; ix++) {
            for (int iz = b.getLocation().getBlockZ() - 1; iz < b.getLocation().getBlockZ() + 2; iz++) {
                for (int iy = b.getLocation().getBlockY() - 1; iy < b.getLocation().getBlockY() + 2; iy++) {
                    Block targetBlock = b.getWorld().getBlockAt(ix, iy, iz);
                    Material type = targetBlock.getType();
                    if (type == Material.SIGN_POST || type == Material.WALL_SIGN) {
                        Sign s = (Sign) targetBlock.getState();
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

    public static HotelRoom getRoomFromSign(Sign s) {
        if (!ChatColor.stripColor(s.getLine(0)).equalsIgnoreCase("[hotel]") || s.getLine(1).equals("") || s.getLine(2).equals("")) {
            return null;
        }
        return HotelUtil.getRoom(ChatColor.stripColor(s.getLine(2)) + " #" + ChatColor.stripColor(s.getLine(1)));
    }

    public static void rentRoom(HotelRoom room, Player player) {
        room.setCurrentOccupant(player.getUniqueId());
        room.setOccupantName(player.getName());
        room.setCheckoutTime((System.currentTimeMillis() / 1000) + room.getStayLength());
        HotelUtil.updateHotelRoom(room);
        HotelUtil.updateRooms();
        Block b = player.getWorld().getBlockAt(room.getX(), room.getY(), room.getZ());
        Material type = b.getType();
        if (type.equals(Material.SIGN) || type.equals(Material.SIGN_POST) || type.equals(Material.WALL_SIGN)) {
            Sign s = (Sign) b.getState();
            s.setLine(3, player.getName());
            s.update();
        }
        player.closeInventory();
        player.sendMessage(ChatColor.GREEN + "You have booked the " + room.getName() + " room for " + room.getCost() +
                " coins!");
        player.sendMessage(ChatColor.GREEN + "You can travel to your room using the My Hotel Rooms menu on your MagicBand.");
    }

    public static void checkout(HotelRoom room, boolean lapsed) {
        Player tp = Bukkit.getPlayer(room.getCurrentOccupant());
        HotelUtil.closeDoor(room);
        room.setCheckoutTime(0);
        if (tp != null) {
            if (room.getWarp() != null) {
                Warp w = room.getWarp();
                if (tp.getLocation().distance(w.getLocation()) < 25) {
                    tp.teleport(w.getLocation());
                }
            }
            if (lapsed) {
                tp.sendMessage(ChatColor.GREEN + "Your reservation of the " + room.getName() +
                        " room has lapsed and you have been checked out. Please come stay with us again soon!");
                HotelUtil.expire.send(tp);
                tp.playSound(tp.getLocation(), Sound.BLAZE_DEATH, 10f, 1f);
            } else {
                tp.sendMessage(ChatColor.GREEN + "You have checked out of your room. Have a wonderful rest of your visit!");
            }
        } else {
            room.setCheckoutNotificationRecipient(room.getCurrentOccupant());
        }
        Block b = tp.getWorld().getBlockAt(room.getX(), room.getY(), room.getZ());
        Material type = b.getType();
        if (type.equals(Material.SIGN) || type.equals(Material.SIGN_POST) || type.equals(Material.WALL_SIGN)) {
            Sign s = (Sign) b.getState();
            s.setLine(3, "" + ChatColor.GREEN + room.getCost());
            s.update();
        }
        room.setCurrentOccupant(null);
        room.setOccupantName(null);
        HotelUtil.updateHotelRoom(room);
        HotelUtil.updateRooms();
    }
}
