package us.mcmagic.magicassistant.utils;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.HotelRoom;
import us.mcmagic.magicassistant.handlers.Warp;
import us.mcmagic.mcmagiccore.MCMagicCore;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Greenlock28 on 1/23/2015.
 */
public class HotelUtil implements Listener {

    public static MagicAssistant pl;

    public HotelUtil(MagicAssistant instance) {
        pl = instance;
    }

    public synchronized static List<HotelRoom> getRooms() {
        List<HotelRoom> rooms = new ArrayList<>();
        try (Connection connection = MCMagicCore.getInstance().permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM `hotelrooms`");
            ResultSet result = sql.executeQuery();
            while (result.next()) {
                rooms.add(new HotelRoom(result.getString("hotelName"),
                        result.getInt("roomNumber"),
                        result.getString("currentOccupant") != "" ? result.getString("currentOccupant") : null,
                        result.getInt("occupationCooldown"),
                        Warp.fromDatabaseString(result.getString("roomWarp") != "" ? result.getString("roomWarp") : null),
                        result.getInt("cost"),
                        result.getString("checkoutNotificationRecipient") != "" ? result.getString("checkoutNotificationRecipient") : null));
            }
            result.close();
            sql.close();
            return rooms;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static HotelRoom getRoom(String name) {
        for (HotelRoom room : pl.hotelRooms) {
            if (room.getName().equalsIgnoreCase(name)) {
                return room;
            }
        }
        return null;
    }

    public synchronized static void addRoom(HotelRoom room) {
        if (getRoom(room.getName()) != null) {
            updateRoom(room);
            return;
        }

        try (Connection connection = MCMagicCore.getInstance().permSqlUtil.getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("INSERT INTO `hotelrooms` values(0,?,?,?,?,?,?,?,?)");
            sql.setString(1, room.getHotelName());
            sql.setInt(2, room.getRoomNumber());
            sql.setString(3, room.getCurrentOccupant() != null ? room.getCurrentOccupant() : "");
            sql.setInt(4, room.getOccupationCooldown());
            sql.setString(5, room.getWarp() != null ? room.getWarp().toDatabaseString() : "");
            sql.setInt(6, room.getCost());
            sql.setString(7, room.getCheckoutNotificationRecipient() != null ? room.getCheckoutNotificationRecipient() : "");
            sql.setString(8, room.getName());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void addRoomWithoutCheckingExistance(HotelRoom room) {
        try (Connection connection = MCMagicCore.getInstance().permSqlUtil.getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("INSERT INTO `hotelrooms` values(0,?,?,?,?,?,?,?,?)");
            sql.setString(1, room.getHotelName());
            sql.setInt(2, room.getRoomNumber());
            sql.setString(3, room.getCurrentOccupant() != null ? room.getCurrentOccupant() : "");
            sql.setInt(4, room.getOccupationCooldown());
            sql.setString(5, room.getWarp() != null ? room.getWarp().toDatabaseString() : "");
            sql.setInt(6, room.getCost());
            sql.setString(7, room.getCheckoutNotificationRecipient() != null ? room.getCheckoutNotificationRecipient() : "");
            sql.setString(8, room.getName());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void removeRoom(HotelRoom room) {
        try (Connection connection = MCMagicCore.getInstance().permSqlUtil.getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("DELETE FROM `hotelrooms` WHERE name=?");
            sql.setString(1, room.getName());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void updateRoom(HotelRoom room) {
        removeRoom(room);
        addRoomWithoutCheckingExistance(room);
    }

    public synchronized static void updateRooms() {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("UpdateHotelRooms");
            out.writeUTF(MagicAssistant.serverName);
            Bukkit.getServer().sendPluginMessage(pl, "BungeeCord",
                    b.toByteArray());
        } catch (IOException e) {
            Bukkit.getServer()
                    .getLogger()
                    .severe("There was an error contacting the Bungee server to update Hotel Rooms!");
        }
    }

    public synchronized static void refreshRooms() {
        MagicAssistant.hotelRooms.clear();
        for (HotelRoom room : HotelUtil.getRooms()) {
            MagicAssistant.hotelRooms.add(room);
        }
    }
}
