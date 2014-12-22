package us.mcmagic.magicassistant.utils;

import org.bukkit.Bukkit;
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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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
    private static List<UUID> loading = new ArrayList<>();

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

    public static boolean isLoading(Player player) {
        return loading.contains(player.getUniqueId());
    }

    public static void setupPlayerData(Player player) {
        loading.add(player.getUniqueId());
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
                Collections.sort(flist);
                for (String friend : flist) {
                    String name = UUIDConverter.convert(friend);
                    UUID uuid = UUID.fromString(friend.replaceAll(" ", ""));
                    friendlist.put(uuid, name);
                }
                pages = (int) Math.ceil(flist.size() / 7);
            } else {
                pages = 1;
            }
            HashMap<Integer, List<String>> plist = new HashMap<>();
            if (pages > 1) {
                int i = 1;
                int i2 = 1;
                for (Map.Entry<UUID, String> entry : friendlist.entrySet()) {
                    if (i2 >= 8) {
                        i++;
                        i2 = 1;
                    }
                    if (i2 == 1) {
                        plist.put(i, new ArrayList<String>());
                        plist.get(i).add(entry.getValue());
                    } else {
                        plist.get(i).add(entry.getValue());
                    }
                    i2++;
                }
            } else {
                List<String> list = new ArrayList<>();
                for (Map.Entry<UUID, String> entry : friendlist.entrySet()) {
                    list.add(entry.getValue());
                }
                plist.put(1, list);
            }
            PlayerData data = new PlayerData(player.getUniqueId(),
                    getBandNameColor(result.getString("namecolor")),
                    getBandColor(result.getString("bandcolor")),
                    friendlist, plist);
            result.close();
            sql.close();
            MagicAssistant.playerData.add(data);
            loading.remove(player.getUniqueId());
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

    public static void setBandColor(Player player, BandColor color) {
        openConnection();
        try {
            PreparedStatement sql = connection.prepareStatement("UPDATE `player_data` SET bandcolor=? WHERE uuid=?");
            sql.setString(1, color.getName());
            sql.setString(2, player.getUniqueId() + "");
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        data.setBandColor(color);
        giveBandToPlayer(player);
        player.sendMessage(ChatColor.GREEN + "You have changed the color of your " + data.getBandName() + "MagicBand!");
    }

    public static void setBandName(Player player, ChatColor color) {
        openConnection();
        try {
            PreparedStatement sql = connection.prepareStatement("UPDATE `player_data` SET namecolor=? WHERE uuid=?");
            sql.setString(1, getBandNameColor(color));
            sql.setString(2, player.getUniqueId() + "");
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        data.setBandName(color);
        giveBandToPlayer(player);
        player.sendMessage(ChatColor.GREEN + "You have changed the name color of your " + data.getBandName() + "MagicBand!");
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

    public static String getBandNameColor(ChatColor color) {
        switch (color) {
            case RED:
                return "red";
            case GOLD:
                return "orange";
            case YELLOW:
                return "yellow";
            case GREEN:
                return "green";
            case DARK_GREEN:
                return "darkgreen";
            case BLUE:
                return "blue";
            case DARK_PURPLE:
                return "purple";
            default:
                return "orange";
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
            case "darkgreen":
                return ChatColor.DARK_GREEN;
            case "blue":
                return ChatColor.BLUE;
            case "purple":
                return ChatColor.DARK_PURPLE;
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

    public static void friendTeleport(Player player, String friendUUID) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("FriendTeleport");
            out.writeUTF(player.getUniqueId() + "");
            out.writeUTF(friendUUID);
            player.sendPluginMessage(
                    Bukkit.getPluginManager().getPlugin("MagicAssistant"),
                    "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED
                    + "Sorry! It looks like something went wrong! It's probably out fault. We will try to fix it as soon as possible!");
        }
    }

    public static void askForParty() {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("PartyRequest");
            out.writeUTF(MagicAssistant.serverName);
            Bukkit.getServer().sendPluginMessage(
                    Bukkit.getPluginManager().getPlugin("MagicAssistant"),
                    "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            System.out.println("Error requesting Party Info");
        }
    }

    public static void createParty() {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("MagicPartySetup");
            out.writeUTF(MagicAssistant.serverName);
            Bukkit.getServer().sendPluginMessage(
                    Bukkit.getPluginManager().getPlugin("MagicAssistant"),
                    "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            System.out.println("Error requesting Party Setup");
        }
    }

    public static void removeParty() {
        MagicAssistant.party = false;
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("MagicPartyRemove");
            out.writeUTF(MagicAssistant.serverName);
            Bukkit.getServer().sendPluginMessage(
                    Bukkit.getPluginManager().getPlugin("MagicAssistant"),
                    "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            System.out.println("Error requesting Party Setup");
        }
    }

    public static void joinParty(Player player) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("PartyJoin");
            out.writeUTF(player.getUniqueId() + "");
            Bukkit.getServer().sendPluginMessage(
                    Bukkit.getPluginManager().getPlugin("MagicAssistant"),
                    "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            System.out.println("Error requesting Party Setup");
        }
    }
}