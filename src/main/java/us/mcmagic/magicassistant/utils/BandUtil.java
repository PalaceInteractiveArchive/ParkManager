package us.mcmagic.magicassistant.utils;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.BandColor;
import us.mcmagic.magicassistant.handlers.DataResponse;
import us.mcmagic.magicassistant.handlers.PlayerData;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by Marc on 12/13/14
 */
public class BandUtil {
    private static ItemStack back = new ItemStack(Material.FIREWORK_CHARGE);
    private HashMap<UUID, DataResponse> dataResponses = new HashMap<>();

    public BandUtil() {
        initialize();
    }

    private void initialize() {
        FireworkEffectMeta bm = (FireworkEffectMeta) back.getItemMeta();
        bm.setDisplayName(ChatColor.GREEN + "Back");//41, 106, 255
        bm.setEffect(FireworkEffect.builder().withColor(Color.fromRGB(0, 153, 0)).build());
        back.setItemMeta(bm);
        Bukkit.getScheduler().runTaskTimer(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, DataResponse> entry : new HashSet<>(dataResponses.entrySet())) {
                    DataResponse response = dataResponses.remove(entry.getKey());
                    User user = MCMagicCore.getUser(entry.getKey());
                    if (user == null) {
                        continue;
                    }
                    Player player = Bukkit.getPlayer(user.getUniqueId());
                    if (player == null) {
                        continue;
                    }
                    Rank rank = user.getRank();
                    Inventory inv = player.getOpenInventory().getTopInventory();
                    if (inv == null) {
                        continue;
                    }
                    ItemStack pinfo = inv.getItem(15);
                    if (pinfo == null) {
                        continue;
                    }
                    ItemMeta meta = pinfo.getItemMeta();
                    meta.setLore(Arrays.asList(ChatColor.GREEN + "Name: " + ChatColor.YELLOW + user.getName(),
                            ChatColor.GREEN + "Rank: " + rank.getNameWithBrackets(),
                            ChatColor.GREEN + "Balance: " + ChatColor.YELLOW + "$" + response.getBalance(),
                            ChatColor.GREEN + "Tokens: " + ChatColor.YELLOW + "✪ " + response.getTokens(),
                            ChatColor.GREEN + "Online Time: " + ChatColor.YELLOW + response.getOnlineTime()));
                    pinfo.setItemMeta(meta);
                    inv.setItem(15, pinfo);
                }
            }
        }, 0L, 10L);
    }

    private NBTTagCompound getHeadTag(Player player) {
        return getHeadTag(player, new NBTTagCompound());
    }

    private NBTTagCompound getHeadTag(Player player, NBTTagCompound current) {
        User user = MCMagicCore.getUser(player.getUniqueId());
        user.setTextureHash(((CraftPlayer) player).getHandle().getProfile().getProperties().get("textures").iterator().next().getValue());
        NBTTagCompound name = current.getCompound("SkullOwner");
        name.setString("Id", player.getUniqueId().toString());
        current.set("SkullOwner", name);
        return current;
    }

    public boolean isLoading(Player player) {
        return dataResponses.containsKey(player.getUniqueId());
    }

    public void sparkleBand(Player player) {
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        final ItemStack band = player.getInventory().getItem(8);
        if (band == null) {
            return;
        }
        if (!getBandMaterial(data.getBandColor()).equals(band.getType())) {
            return;
        }
        if (band.getEnchantments().containsKey(Enchantment.PROTECTION_ENVIRONMENTAL)) {
            return;
        }
        band.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 10f, 1f);
        Bukkit.getScheduler().runTaskLater(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                band.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
            }
        }, 10L);
    }

    public PlayerData setupPlayerData(UUID uuid) {
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT friends,bandcolor,rank,namecolor,flash,visibility," +
                    "fountain,hotel,fastpass,dailyfp,fpday FROM player_data WHERE uuid=?");
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
                    result.getInt("fountain") == 1, result.getInt("hotel") == 1, result.getInt("fastpass"),
                    result.getInt("dailyfp"), result.getInt("fpday"));
            result.close();
            sql.close();
            List<Integer> purch = new ArrayList<>();
            PreparedStatement pur = connection.prepareStatement("SELECT item FROM purchases WHERE uuid=?");
            pur.setString(1, uuid.toString());
            ResultSet purr = pur.executeQuery();
            while (purr.next()) {
                purch.add(purr.getInt("item"));
            }
            purr.close();
            pur.close();
            data.setPurchases(purch);
            HashMap<String, Integer> rides = new HashMap<>();
            PreparedStatement counts = connection.prepareStatement("SELECT name,count from ridecounter WHERE uuid=? AND server=?");
            counts.setString(1, uuid.toString());
            counts.setString(2, MCMagicCore.getMCMagicConfig().serverName);
            ResultSet results = counts.executeQuery();
            while (results.next()) {
                rides.put(results.getString("name"), results.getInt("count"));
            }
            results.close();
            counts.close();
            data.setRideCounts(rides);
            MagicAssistant.playerData.put(uuid, data);
            dataResponses.remove(uuid);
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setSetting(final UUID uuid, final String name, final boolean value) {
        Bukkit.getScheduler().runTaskAsynchronously(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                try (Connection connection = SqlUtil.getConnection()) {
                    PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET " + name + "=? WHERE uuid=?");
                    sql.setInt(1, value ? 1 : 0);
                    sql.setString(2, uuid.toString());
                    sql.execute();
                    sql.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void removePlayerData(Player player) {
        MagicAssistant.playerData.remove(player.getUniqueId());
    }

    public long getOnlineTime(UUID uuid) {
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT lastseen FROM player_data WHERE uuid=?");
            sql.setString(1, uuid.toString());
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
            mbm.setDisplayName(data.getBandName() + "MagicBand " + ChatColor.GRAY + "(Right-Click)");
            mbm.setLore(Arrays.asList(ChatColor.GREEN + "Click me to open",
                    ChatColor.GREEN + "the MagicBand menu!"));
            mb.setItemMeta(mbm);
        } else {
            mb = new ItemStack(Material.FIREWORK_CHARGE);
            FireworkEffectMeta mbm = (FireworkEffectMeta) mb.getItemMeta();
            mbm.setEffect(FireworkEffect.builder().withColor(getBandColor(data.getBandColor())).build());
            mbm.setDisplayName(data.getBandName() + "MagicBand " + ChatColor.GRAY + "(Right-Click)");
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

    public void loadPlayerData(Player player) {
        if (dataResponses.containsKey(player.getUniqueId())) {
            return;
        }
        final UUID uuid = player.getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                DataResponse response = new DataResponse(uuid, MCMagicCore.economy.getBalance(uuid),
                        MCMagicCore.economy.getTokens(uuid), DateUtil.formatDateDiff(getOnlineTime(uuid)));
                dataResponses.put(uuid, response);
            }
        });
    }

    public void cancelLoadPlayerData(UUID uuid) {
        dataResponses.remove(uuid);
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
        Date current = new Date(System.currentTimeMillis());
        String h = new SimpleDateFormat("HH").format(current);
        String minute = new SimpleDateFormat("mm").format(current);
        String second = new SimpleDateFormat("ss").format(current);
        String hour;
        if (Integer.parseInt(h) > 12) {
            hour = (Integer.parseInt(h) - 12) + ":" + minute + ":" + second + " PM";
        } else if (Integer.parseInt(h) == 0) {
            hour = 12 + ":" + minute + ":" + second + " AM";
        } else {
            hour = h + ":" + minute + ":" + second + " AM";
        }
        return hour;
    }
}