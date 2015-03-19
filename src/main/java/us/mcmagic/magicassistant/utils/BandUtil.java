package us.mcmagic.magicassistant.utils;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.PlayerData;
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
    private static ItemStack back = new ItemStack(Material.FIREWORK_CHARGE);
    public static List<UUID> loading = new ArrayList<>();

    public static void initialize() {
        ItemMeta bm = back.getItemMeta();
        bm.setDisplayName(ChatColor.GREEN + "Back");
        back.setItemMeta(bm);
    }

    public static boolean isLoading(Player player) {
        return loading.contains(player.getUniqueId());
    }

    public static void setupPlayerData(Player player) {
        try (Connection connection = MCMagicCore.getInstance().permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM `player_data` WHERE uuid=?");
            sql.setString(1, player.getUniqueId() + "");
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                player.kickPlayer("Sorry, there was an issue! Please report this to a staff member.");
                return;
            }
            HashMap<UUID, String> friendlist = new HashMap<>();
            int pages;
            if (!result.getString("friends").equals("")) {
                String[] friends = result.getString("friends").split(" ");
                List<String> flist = new ArrayList<>();
                Collections.addAll(flist, friends);
                Collections.sort(flist);
                for (String friend : flist) {
                    UUID uuid = UUID.fromString(friend.replaceAll(" ", ""));
                    String name;
                    if (MagicAssistant.userCache.containsKey(uuid)) {
                        name = MagicAssistant.userCache.get(uuid);
                    } else {

                        name = UUIDConverter.convert(friend);
                        MagicAssistant.userCache.put(uuid, name);
                    }
                    friendlist.put(uuid, name);
                }
                pages = (int) Math.ceil(flist.size() / 7);
            } else {
                pages = 1;
            }
            HashMap<Integer, List<String>> plist = new HashMap<>();
            if (!friendlist.isEmpty()) {
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
            }
            boolean special = getBandColor(result.getString("bandcolor")).getName().startsWith("s");
            PlayerData data = new PlayerData(player.getUniqueId(),
                    getBandNameColor(result.getString("namecolor")),
                    getBandColor(result.getString("bandcolor")),
                    friendlist, plist, special);
            result.close();
            sql.close();
            MagicAssistant.playerData.add(data);
            loading.remove(player.getUniqueId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removePlayerData(Player player) {
        try {
            MagicAssistant.playerData.remove(MagicAssistant.getPlayerData(player.getUniqueId()));
        } catch (Exception ignored) {
        }
    }

    public static long getOnlineTime(String uuid) {
        try (Connection connection = MCMagicCore.getInstance().permSqlUtil.getConnection()) {
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
        }
    }

    public static void setBandColor(Player player, BandColor color) {
        try (Connection connection = MCMagicCore.getInstance().permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE `player_data` SET bandcolor=? WHERE uuid=?");
            sql.setString(1, color.getName());
            sql.setString(2, player.getUniqueId() + "");
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        data.setBandColor(color);
        data.setSpecial(color.getName().startsWith("s"));
        giveBandToPlayer(player);
        player.sendMessage(ChatColor.GREEN + "You have changed the color of your " + data.getBandName() + "MagicBand!");
    }

    public static void setBandColor(Player player, Material color) {
        try (Connection connection = MCMagicCore.getInstance().permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE `player_data` SET bandcolor=? WHERE uuid=?");
            sql.setString(1, getBandName(color));
            sql.setString(2, player.getUniqueId() + "");
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        data.setBandColor(getBandColor(getBandName(color)));
        data.setSpecial(getBandColor(getBandName(color)).getName().startsWith("s"));
        giveBandToPlayer(player);
        player.sendMessage(ChatColor.GREEN + "You have changed the color of your " + data.getBandName() + "MagicBand!");
    }

    public static void giveBandToPlayer(Player player) {
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        ItemStack mb;
        if (data.getSpecial()) {
            mb = new ItemStack(getBandMaterial(data.getBandColor()));
            ItemMeta mbm = mb.getItemMeta();
            mbm.setDisplayName(data.getBandName() + "MagicBand");
            mbm.setLore(Arrays.asList(ChatColor.GREEN + "Click me to open",
                    ChatColor.GREEN + "the MagicBand menu!"));
            mb.setItemMeta(mbm);
        } else {
            mb = new ItemStack(Material.FIREWORK_CHARGE);
            FireworkEffectMeta mbm = (FireworkEffectMeta) mb.getItemMeta();
            mbm.setEffect(FireworkEffect.builder().withColor(BandUtil.getBandColor(data.getBandColor())).build());
            mbm.setDisplayName(data.getBandName() + "MagicBand");
            mbm.setLore(Arrays.asList(ChatColor.GREEN + "Click me to open",
                    ChatColor.GREEN + "the MagicBand menu!"));
            mb.setItemMeta(mbm);
        }
        player.getInventory().setItem(8, mb);
    }

    public static String getBandName(Material color) {
        switch (color) {
            case PAPER:
                return "s1";
            case IRON_BARDING:
                return "s2";
            case GOLD_BARDING:
                return "s3";
            case DIAMOND_BARDING:
                return "s4";
            case GHAST_TEAR:
                return "s5";
            default:
                return "blue";
        }
    }

    public static void setBandName(Player player, ChatColor color) {
        try (Connection connection = MCMagicCore.getInstance().permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE `player_data` SET namecolor=? WHERE uuid=?");
            sql.setString(1, getBandNameColor(color));
            sql.setString(2, player.getUniqueId() + "");
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
            case "orange":
                return BandColor.ORANGE;
            case "yellow":
                return BandColor.YELLOW;
            case "green":
                return BandColor.GREEN;
            case "blue":
                return BandColor.BLUE;
            case "purple":
                return BandColor.PURPLE;
            case "pink":
                return BandColor.PINK;
            case "s1":
                return BandColor.SPECIAL1;
            case "s2":
                return BandColor.SPECIAL2;
            case "s3":
                return BandColor.SPECIAL3;
            case "s4":
                return BandColor.SPECIAL4;
            case "s5":
                return BandColor.SPECIAL5;
            default:
                return BandColor.BLUE;
        }
    }

    public static Material getBandMaterial(BandColor color) {
        switch (color) {
            case SPECIAL1:
                return Material.PAPER;
            case SPECIAL2:
                return Material.IRON_BARDING;
            case SPECIAL3:
                return Material.GOLD_BARDING;
            case SPECIAL4:
                return Material.DIAMOND_BARDING;
            case SPECIAL5:
                return Material.GHAST_TEAR;
            default:
                return Material.FIREWORK_CHARGE;
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

    public static Color getBandColor(BandColor color) {
        switch (color) {
            case RED:
                return Color.fromRGB(255, 40, 40);
            case ORANGE:
                return Color.fromRGB(247, 140, 0);
            case YELLOW:
                return Color.fromRGB(239, 247, 0);
            case GREEN:
                return Color.fromRGB(0, 192, 13);
            case BLUE:
                return Color.fromRGB(41, 106, 255);
            case PURPLE:
                return Color.fromRGB(176, 0, 220);
            case PINK:
                return Color.fromRGB(246, 120, 255);
            default:
                return Color.fromRGB(0, 102, 255);
        }
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
            PlayerUtil.randomPlayer().sendPluginMessage(
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
            PlayerUtil.randomPlayer().sendPluginMessage(
                    Bukkit.getPluginManager().getPlugin("MagicAssistant"),
                    "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            System.out.println("Error requesting Party Setup");
        }
    }

    public static void removeParty() {
        MagicAssistant.party = false;
        MagicAssistant.partyServer.clear();
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("MagicPartyRemove");
            out.writeUTF(MagicAssistant.serverName);
            PlayerUtil.randomPlayer().sendPluginMessage(
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
            PlayerUtil.randomPlayer().sendPluginMessage(
                    Bukkit.getPluginManager().getPlugin("MagicAssistant"),
                    "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            System.out.println("Error requesting Party Setup");
        }
    }
}