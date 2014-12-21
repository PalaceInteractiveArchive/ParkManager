package us.mcmagic.magicassistant.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.PlayerData;
import us.mcmagic.magicassistant.magicband.BandColor;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.uuidconverter.UUIDConverter;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by Marc on 12/13/14
 */
public class BandUtil {
    private static Connection connection;
    private static ItemStack back = new ItemStack(Material.PAPER);

    public static void initialize() {
        ItemMeta bm = back.getItemMeta();
        bm.setDisplayName(ChatColor.GREEN + "Back");
        back.setItemMeta(bm);
    }

    public synchronized static void closeConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized static void openConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://"
                            + MCMagicCore.config.getString("sql.ip") + ":"
                            + MCMagicCore.config.getString("sql.port") + "/"
                            + MCMagicCore.config.getString("sql.database"),
                    MCMagicCore.config.getString("sql.username"),
                    MCMagicCore.config.getString("sql.password"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setupPlayerData(Player player) {
        openConnection();
        try {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM `player_data` WHERE uuid=?");
            sql.setString(1, player.getUniqueId() + "");
            ResultSet result = sql.executeQuery();
            result.next();
            HashMap<UUID, String> friendlist = new HashMap<>();
            int pages;
            if (!result.getString("friends").equals("")) {
                String[] friends = result.getString("friends").split(" ");
                List<String> flist = new ArrayList<>();
                Collections.addAll(flist, friends);
                for (String friend : flist) {
                    String name = UUIDConverter.convert(friend);
                    UUID uuid = UUID.fromString(friend.replaceAll(" ", ""));
                    friendlist.put(uuid, name);
                }
                pages = (int) Math.ceil(flist.size() / 7);
            } else {
                pages = 1;
            }
            player.sendMessage("Test " + pages);
            HashMap<Integer, List<String>> plist = new HashMap<>();
            if (pages > 1) {
                int i = 1;
                int i2 = 1;
                for (Map.Entry<UUID, String> entry : friendlist.entrySet()) {
                    if (i2 >= 8) {
                        i++;
                    }
                    if (i2 == 1) {
                        plist.put(i, Arrays.asList(entry.getValue()));
                    } else {
                        plist.get(i).add(entry.getValue());
                    }
                    i2++;
                }
            }
            PlayerData data = new PlayerData(player.getUniqueId(),
                    getBandNameColor(result.getString("namecolor")),
                    getBandColor(result.getString("bandcolor")),
                    friendlist, plist);
            result.close();
            sql.close();
            MagicAssistant.playerData.add(data);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public static void removePlayerData(Player player) {
        try {
            MagicAssistant.playerData.remove(MagicAssistant.getPlayerData(player.getUniqueId()));
        } catch (Exception ignored) {
        }
    }

    public static long getOnlineTime(String uuid) {
        openConnection();
        try {
            PreparedStatement sql = connection.prepareStatement("SELECT lastseen FROM `player_data` WHERE uuid=?");
            sql.setString(1, uuid);
            ResultSet result = sql.executeQuery();
            result.next();
            Timestamp time = result.getTimestamp("lastseen");
            result.close();
            sql.close();
            return time.getTime();
        } catch (SQLException e) {
            e.printStackTrace();
            return System.currentTimeMillis();
        } finally {
            closeConnection();
        }
    }

    public static ItemStack getBackItem() {
        return back;
    }

    public static BandColor getBandColor(String string) {
        switch (string) {
            case "red":
                return BandColor.RED;
            case "yellow":
                return BandColor.YELLOW;
            case "green":
                return BandColor.GREEN;
            case "blue":
                return BandColor.BLUE;
            default:
                return BandColor.BLUE;
        }
    }

    public static ChatColor getBandNameColor(String string) {
        switch (string) {
            case "red":
                return ChatColor.RED;
            case "orange":
                return ChatColor.GOLD;
            case "yellow":
                return ChatColor.YELLOW;
            case "green":
                return ChatColor.GREEN;
            case "blue":
                return ChatColor.BLUE;
            case "purple":
                return ChatColor.DARK_PURPLE;
            case "pink":
                return ChatColor.LIGHT_PURPLE;
            default:
                return ChatColor.GOLD;
        }
    }

    public static Material getBandMaterial(BandColor color) {
        switch (color) {
            case BLUE:
                return Material.PAPER;
            case GREEN:
                return Material.IRON_BARDING;
            case YELLOW:
                return Material.GOLD_BARDING;
            case RED:
                return Material.DIAMOND_BARDING;
            default:
                return Material.PAPER;
        }
    }

    public static void giveBandToPlayer(Player player) {
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        ItemStack mb = new ItemStack(BandUtil.getBandMaterial(data.getBandColor()));
        ItemMeta mbm = mb.getItemMeta();
        mbm.setDisplayName(data.getBandName() + "MagicBand");
        mbm.setLore(Arrays.asList(ChatColor.GREEN + "Click me to open",
                ChatColor.GREEN + "the MagicBand menu!"));
        mb.setItemMeta(mbm);
        player.getInventory().setItem(8, mb);
    }

    public static String currentTime() {
        Date current = new Date(System.currentTimeMillis() + 10800);
        String h = new SimpleDateFormat("HH").format(current);
        String minute = new SimpleDateFormat("mm").format(current);
        String second = new SimpleDateFormat("ss").format(current);
        String hour;
        if (Integer.parseInt(h) > 12) {
            hour = (Integer.parseInt(h) - 12) + ":" + minute + ":" + second;
        } else {
            hour = h + ":" + minute + ":" + second;
        }
        return hour;
    }
}