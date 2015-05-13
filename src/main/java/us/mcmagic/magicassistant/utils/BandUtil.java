package us.mcmagic.magicassistant.utils;

import net.minecraft.server.v1_8_R2.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.BandColor;
import us.mcmagic.magicassistant.handlers.PlayerData;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.coins.Coins;
import us.mcmagic.mcmagiccore.credits.Credits;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

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
    private HashMap<UUID, Inventory> loadingPlayerData = new HashMap<>();

    public void initialize() {
        FireworkEffectMeta bm = (FireworkEffectMeta) back.getItemMeta();
        bm.setDisplayName(ChatColor.GREEN + "Back");
        bm.setEffect(FireworkEffect.builder().withColor(Color.fromRGB(41, 106, 255)).build());
        back.setItemMeta(bm);
        Bukkit.getScheduler().runTaskTimerAsynchronously(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, Inventory> entry : new HashSet<>(loadingPlayerData.entrySet())) {
                    User user = MCMagicCore.getUser(entry.getKey());
                    if (user == null) {
                        loadingPlayerData.remove(entry.getKey());
                        continue;
                    }
                    loadingPlayerData.remove(user.getUniqueId());
                    Player player = Bukkit.getPlayer(user.getUniqueId());
                    if (player == null) {
                        loadingPlayerData.remove(entry.getKey());
                        continue;
                    }
                    Rank rank = user.getRank();
                    List<String> lore = Arrays.asList(ChatColor.GREEN + "Name: " + ChatColor.YELLOW + user.getName(),
                            ChatColor.GREEN + "Rank: " + rank.getNameWithBrackets(),
                            ChatColor.GREEN + "Coins: " + ChatColor.YELLOW + Coins.getSqlCoins(user.getUniqueId()),
                            ChatColor.GREEN + "Credits: " + ChatColor.YELLOW + Credits.getSqlCredits(user.getUniqueId()),
                            ChatColor.GREEN + "Online Time: " + ChatColor.YELLOW +
                                    DateUtil.formatDateDiff(getOnlineTime(user.getUniqueId().toString())));
                    ItemStack pinfo = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                    SkullMeta sm = (SkullMeta) pinfo.getItemMeta();
                    sm.setOwner(player.getName());
                    sm.setDisplayName(ChatColor.GREEN + "My Profile");
                    sm.setLore(lore);
                    pinfo.setItemMeta(sm);
                    net.minecraft.server.v1_8_R2.ItemStack i = CraftItemStack.asNMSCopy(pinfo);
                    NBTTagCompound tag = i.getTag();
                    NBTTagCompound name = tag.getCompound("SkullOwner");
                    name.setString("Id", player.getUniqueId().toString());
                    tag.set("SkullOwner", name);
                    i.setTag(tag);
                    ItemStack done = CraftItemStack.asBukkitCopy(i);
                    entry.getValue().setItem(15, done);

                }
            }
        }, 0L, 10L);
    }

    public boolean isLoading(Player player) {
        return loadingPlayerData.containsKey(player.getUniqueId());
    }

    public PlayerData setupPlayerData(UUID uuid) {
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM player_data WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                return null;
            }
            List<UUID> friendlist = new ArrayList<>();
            if (!result.getString("friends").equals("")) {
                String[] friends = result.getString("friends").split(" ");
                for (String friend : friends) {
                    friendlist.add(UUID.fromString(friend));
                }
            }
            boolean special = getBandColor(result.getString("bandcolor")).getName().startsWith("s");
            PlayerData data = new PlayerData(uuid, result.getString("rank").equals("dvc"),
                    getBandNameColor(result.getString("namecolor")), getBandColor(result.getString("bandcolor")),
                    friendlist, special, result.getInt("flash") == 1, result.getInt("visibility") == 1,
                    result.getInt("loop") == 1, result.getInt("hotel") == 1);
            result.close();
            sql.close();
            MagicAssistant.playerData.put(uuid, data);
            loadingPlayerData.remove(uuid);
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setSetting(UUID uuid, String name, boolean value) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET `" + name + "`=? WHERE uuid=?");
            sql.setInt(1, value ? 1 : 0);
            sql.setString(2, uuid + "");
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removePlayerData(Player player) {
        MagicAssistant.playerData.remove(player.getUniqueId());
    }

    public long getOnlineTime(String uuid) {
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT lastseen FROM player_data WHERE uuid=?");
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

    public void setBandColor(Player player, BandColor color) {
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET bandcolor=? WHERE uuid=?");
            sql.setString(1, color.getName());
            sql.setString(2, player.getUniqueId().toString());
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

    public void setBandColor(Player player, Material color) {
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET bandcolor=? WHERE uuid=?");
            sql.setString(1, getBandName(color));
            sql.setString(2, player.getUniqueId().toString());
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

    public void giveBandToPlayer(Player player) {
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
            mbm.setEffect(FireworkEffect.builder().withColor(getBandColor(data.getBandColor())).build());
            mbm.setDisplayName(data.getBandName() + "MagicBand");
            mbm.setLore(Arrays.asList(ChatColor.GREEN + "Click me to open",
                    ChatColor.GREEN + "the MagicBand menu!"));
            mb.setItemMeta(mbm);
        }
        if (mb == null) {
            player.sendMessage(ChatColor.RED + "An error has occured! Please report this to a Staff Member (Error Code 105)");
        } else {
            player.getInventory().setItem(8, mb);
        }
    }

    public void loadPlayerData(Player player, Inventory inventory) {
        if (loadingPlayerData.containsKey(player.getUniqueId())) {
            return;
        }
        loadingPlayerData.put(player.getUniqueId(), inventory);
    }

    public void cancelLoadPlayerData(UUID uuid) {
        loadingPlayerData.remove(uuid);
    }

    public String getBandName(Material color) {
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

    public void setBandName(Player player, ChatColor color) {
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET namecolor=? WHERE uuid=?");
            sql.setString(1, getBandNameColor(color));
            sql.setString(2, player.getUniqueId().toString());
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

    public BandColor getBandColor(String string) {
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

    public Material getBandMaterial(BandColor color) {
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

    public String getBandNameColor(ChatColor color) {
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

    public ChatColor getBandNameColor(String string) {
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

    public Color getBandColor(BandColor color) {
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

    public String currentTime() {
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

    public void friendTeleport(Player player, String friendUUID) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("FriendTeleport");
            out.writeUTF(player.getUniqueId().toString());
            out.writeUTF(friendUUID);
            player.sendPluginMessage(MagicAssistant.getInstance(), "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED
                    + "Sorry! It looks like something went wrong! It's probably out fault. We will try to fix it as soon as possible!");
        }
    }

    public void askForParty() {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("PartyRequest");
            out.writeUTF(MCMagicCore.getMCMagicConfig().serverName);
            Bukkit.getServer().sendPluginMessage(MagicAssistant.getInstance(), "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            System.out.println("Error requesting Party Info");
        }
    }

    public void createParty() {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("MagicPartySetup");
            out.writeUTF(MCMagicCore.getMCMagicConfig().serverName);
            PlayerUtil.randomPlayer().sendPluginMessage(MagicAssistant.getInstance(), "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            System.out.println("Error creating Party");
        }
    }

    public void removeParty() {
        MagicAssistant.party = false;
        MagicAssistant.partyServer.clear();
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("MagicPartyRemove");
            out.writeUTF(MCMagicCore.getMCMagicConfig().serverName);
            PlayerUtil.randomPlayer().sendPluginMessage(MagicAssistant.getInstance(), "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            System.out.println("Error removing Party");
        }
    }

    public void joinParty(Player player) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("PartyJoin");
            out.writeUTF(player.getUniqueId().toString());
            PlayerUtil.randomPlayer().sendPluginMessage(MagicAssistant.getInstance(), "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Error joining Party");
        }
    }
}