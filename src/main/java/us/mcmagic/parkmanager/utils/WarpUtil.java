package us.mcmagic.parkmanager.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.Warp;
import us.mcmagic.mcmagiccore.MCMagicCore;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class WarpUtil {

    public static boolean warpExists(String warp) {
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("SELECT * FROM warps WHERE name = ?");
            sql.setString(1, warp);
            ResultSet result = sql.executeQuery();
            boolean contains = result.next();
            result.close();
            sql.close();
            return contains;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getServer(String warp) {
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("SELECT * FROM warps WHERE name = ?");
            sql.setString(1, warp);
            ResultSet result = sql.executeQuery();
            result.next();
            String server = result.getString("server");
            result.close();
            sql.close();
            return server;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Location getLocation(String warp) {
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("SELECT * FROM warps WHERE name=?");
            sql.setString(1, warp);
            ResultSet result = sql.executeQuery();
            result.next();
            String world = result.getString("world");
            double x = result.getDouble("x");
            double y = result.getDouble("y");
            double z = result.getDouble("z");
            float yaw = result.getFloat("yaw");
            float pitch = result.getFloat("pitch");
            result.close();
            sql.close();
            return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void crossServerWarp(final UUID uuid, final String warp, final String server) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("MagicWarp");
            out.writeUTF(uuid.toString());
            out.writeUTF(server);
            out.writeUTF(warp);
            Bukkit.getPlayer(uuid).sendPluginMessage(ParkManager.getInstance(), "BungeeCord", b.toByteArray());
        } catch (IOException e) {
            Bukkit.getPlayer(uuid).sendMessage(ChatColor.RED + "There was a problem joining that server!");
        }
    }

    public synchronized static List<Warp> getWarps() {
        List<String> names = new ArrayList<>();
        List<Warp> warps = new ArrayList<>();
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("SELECT * FROM warps");
            ResultSet result = sql.executeQuery();
            while (result.next()) {
                names.add(result.getString("name"));
                warps.add(new Warp(result.getString("name"),
                        result.getString("server"), result.getDouble("x"),
                        result.getDouble("y"), result.getDouble("z"),
                        result.getFloat("yaw"), result.getFloat("pitch"),
                        result.getString("world")));
            }
            result.close();
            sql.close();
            Collections.sort(names);
            List<Warp> finalWarps = new ArrayList<>();
            for (String name : names) {
                for (Warp warp : warps) {
                    if (warp.getName().equals(name)) {
                        finalWarps.add(warp);
                        break;
                    }
                }
            }
            return finalWarps;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public synchronized static void addWarp(Warp warp) {
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("INSERT INTO warps values(0,?,?,?,?,?,?,?,?)");
            sql.setString(1, warp.getName());
            sql.setDouble(2, warp.getX());
            sql.setDouble(3, warp.getY());
            sql.setDouble(4, warp.getZ());
            sql.setFloat(5, warp.getYaw());
            sql.setFloat(6, warp.getPitch());
            sql.setString(7, warp.getWorld().getName());
            sql.setString(8, warp.getServer());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void removeWarp(Warp warp) {
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("DELETE FROM warps WHERE name=?");
            sql.setString(1, warp.getName());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Warp findWarp(String name) {
        List<Warp> warps = ParkManager.getWarps();
        for (Warp warp : warps) {
            if (warp.getName().toLowerCase().equals(name.toLowerCase())) {
                return warp;
            }
        }
        return null;
    }

    public synchronized static void updateWarps() {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("UpdateWarps");
            out.writeUTF(MCMagicCore.getMCMagicConfig().serverName);
            Bukkit.getServer().sendPluginMessage(ParkManager.getInstance(), "BungeeCord", b.toByteArray());
        } catch (IOException e) {
            Bukkit.getServer().getLogger().severe("There was an error contacting the Bungee server to update Warps!");
        }
    }

    public synchronized static void refreshWarps() {
        ParkManager.clearWarps();
        for (Warp warp : WarpUtil.getWarps()) {
            ParkManager.addWarp(warp);
        }
    }
}