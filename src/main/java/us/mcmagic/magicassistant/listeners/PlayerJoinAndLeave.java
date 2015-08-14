package us.mcmagic.magicassistant.listeners;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.commands.Commandvanish;
import us.mcmagic.magicassistant.designstation.DesignStation;
import us.mcmagic.magicassistant.handlers.HotelRoom;
import us.mcmagic.magicassistant.handlers.PlayerData;
import us.mcmagic.magicassistant.handlers.Warp;
import us.mcmagic.magicassistant.hotels.HotelManager;
import us.mcmagic.magicassistant.utils.SqlUtil;
import us.mcmagic.magicassistant.utils.VisibleUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class PlayerJoinAndLeave implements Listener {
    private static HashMap<UUID, ItemStack[]> invData = new HashMap<>();
    private static HashMap<UUID, ItemStack[]> armorData = new HashMap<>();
    private static HashMap<UUID, ItemStack[]> endData = new HashMap<>();
    private static HashMap<UUID, byte[]> storedInv = new HashMap<>();
    private static HashMap<UUID, byte[]> storedArmor = new HashMap<>();
    private static HashMap<UUID, byte[]> storedEnd = new HashMap<>();
    private List<UUID> firstJoins = new ArrayList<>();

    public PlayerJoinAndLeave() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    update(tp.getUniqueId());
                }
            }
        }, 0L, 6000L);
    }

    private void update(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }
        byte[] invcont = serial(player.getInventory().getContents());
        byte[] armcont = serial(player.getInventory().getArmorContents());
        byte[] endcont = serial(player.getEnderChest().getContents());
        boolean inv = true;
        boolean armor = true;
        boolean end = true;
        if (storedInv.containsKey(uuid)) {
            if (invcont.equals(storedInv.get(uuid))) {
                inv = false;
            } else {
                storedInv.remove(uuid);
                storedInv.put(uuid, invcont);
            }
        }
        if (storedArmor.containsKey(uuid)) {
            if (armcont.equals(storedArmor.get(uuid))) {
                armor = false;
            } else {
                storedInv.remove(uuid);
                storedInv.put(uuid, armcont);
            }
        }
        if (storedEnd.containsKey(uuid)) {
            if (endcont.equals(storedEnd.get(uuid))) {
                end = false;
            } else {
                storedInv.remove(uuid);
                storedInv.put(uuid, endcont);
            }
        }
        if (!inv && !armor && !end) {
            return;
        }
        final boolean finalInv = inv;
        final boolean finalArmor = armor;
        final boolean finalEnd = end;
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement contains = connection.prepareStatement("SELECT uuid FROM inventory WHERE uuid=?");
            contains.setString(1, player.getUniqueId().toString());
            ResultSet result = contains.executeQuery();
            if (!result.next()) {
                result.close();
                contains.close();
                PreparedStatement insert = connection.prepareStatement("INSERT INTO inventory values(0,?,?,?,?)");
                insert.setString(1, player.getUniqueId().toString());
                insert.setBytes(2, invcont);
                insert.setBytes(3, armcont);
                insert.setBytes(4, endcont);
                insert.execute();
                insert.close();
            } else {
                result.close();
                contains.close();
            }
            PreparedStatement sql = connection.prepareStatement(getQuery(finalInv, finalArmor, finalEnd));
            int num = 1;
            if (finalInv) {
                sql.setBytes(num, invcont);
                num++;
            }
            if (finalArmor) {
                sql.setBytes(num, armcont);
                num++;
            }
            if (finalEnd) {
                sql.setBytes(num, endcont);
                num++;
            }
            sql.setString(num, player.getUniqueId().toString());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onAsyncLogin(AsyncPlayerPreLoginEvent event) {
        try {
            UUID uuid = event.getUniqueId();
            if (MagicAssistant.crossServerInv) {
                List<byte[]> stuff = invContents(uuid);
                if (stuff != null) {
                    ItemStack[] inv = deserial(stuff.get(0));
                    ItemStack[] armor = deserial(stuff.get(1));
                    ItemStack[] end = deserial(stuff.get(2));
                    if (invData.containsKey(uuid)) {
                        invData.remove(uuid);
                    }
                    if (armorData.containsKey(uuid)) {
                        armorData.remove(uuid);
                    }
                    if (endData.containsKey(uuid)) {
                        endData.remove(uuid);
                    }
                    if (inv != null) {
                        invData.put(uuid, inv);
                    }
                    if (armor != null) {
                        armorData.put(uuid, armor);
                    }
                    if (end != null) {
                        endData.put(uuid, end);
                    }
                    storedInv.put(uuid, stuff.get(0));
                    storedArmor.put(uuid, stuff.get(1));
                    storedEnd.put(uuid, stuff.get(2));
                } else {
                    firstJoins.add(uuid);
                }
            }
            MagicAssistant.bandUtil.setupPlayerData(uuid);
            MagicAssistant.autographManager.setBook(uuid);
            if (MagicAssistant.getPlayerData(uuid) == null) {
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage("There was an error joining this server! (Error Code 106)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (MCMagicCore.serverStarting) {
            return;
        }
        User user = MCMagicCore.getUser(player.getUniqueId());
        if (user == null) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        }
        /*
        if (Bukkit.getOnlinePlayers().size() >= 150 && user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId() &&
                !MagicAssistant.hubServer) {
            event.setKickMessage("This park is at capacity, sorry!");
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        }
        */
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            final Player player = event.getPlayer();
            User user = MCMagicCore.getUser(player.getUniqueId());
            if (!MagicAssistant.userCache.containsKey(player.getUniqueId())) {
                MagicAssistant.userCache.put(player.getUniqueId(), player.getName());
            } else {
                if (!MagicAssistant.userCache.get(player.getUniqueId()).equals(player.getName())) {
                    MagicAssistant.userCache.remove(player.getUniqueId());
                    MagicAssistant.userCache.put(player.getUniqueId(), player.getName());
                }
            }
            PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
            if (!data.getVisibility()) {
                VisibleUtil.addToHideAll(player);
            }
            if (user.getRank().getRankId() >= Rank.CASTMEMBER.getRankId()) {
                player.setGameMode(GameMode.CREATIVE);
            } else {
                player.setGameMode(GameMode.ADVENTURE);
            }
            if (MagicAssistant.spawnOnJoin || !player.hasPlayedBefore()) {
                player.performCommand("spawn");
            } else {
                warpToNearestWarp(player);
            }
            for (String msg : MagicAssistant.joinMessages) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
            if (user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                VisibleUtil.hideForHideAll(player);
            }
            if (user.getRank().getRankId() > Rank.CASTMEMBER.getRankId()) {
                Commandvanish.hidden.add(player.getUniqueId());
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    if (MCMagicCore.getUser(tp.getUniqueId()).getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                        tp.hidePlayer(player);
                    }
                }
            }
            if (MagicAssistant.hubServer) {
                if (firstJoins.contains(player.getUniqueId())) {
                    firstJoins.remove(player.getUniqueId());
                    PlayerInventory pi = player.getInventory();
                    for (Map.Entry<Integer, Integer> item : MagicAssistant.firstJoinItems.entrySet()) {
                        ItemStack i = new ItemStack(item.getKey(), item.getValue());
                        pi.addItem(i);
                    }
                }
            }
            if (MagicAssistant.crossServerInv) {
                PlayerInventory pi = player.getInventory();
                if (invData.containsKey(player.getUniqueId())) {
                    ItemStack[] inv = invData.remove(player.getUniqueId());
                    if (inv == null) {
                        player.sendMessage(ChatColor.RED +
                                "An error has occured! Please report this to a Staff Member (Error Code 102)");
                    } else {
                        pi.setContents(inv);
                    }
                }
                if (armorData.containsKey(player.getUniqueId())) {
                    ItemStack[] armor = armorData.remove(player.getUniqueId());
                    if (armor == null) {
                        player.sendMessage(ChatColor.RED +
                                "An error has occured! Please report this to a Staff Member (Error Code 103)");
                    } else {
                        pi.setArmorContents(armor);
                    }
                }
                if (endData.containsKey(player.getUniqueId())) {
                    ItemStack[] end = endData.remove(player.getUniqueId());
                    if (end == null) {
                        player.sendMessage(ChatColor.RED +
                                "An error has occured! Please report this to a Staff Member (Error Code 104)");
                    } else {
                        player.getEnderChest().setContents(end);
                    }
                }
                if (MagicAssistant.shooter != null) {
                    pi.remove(MagicAssistant.shooter.getItem().getType());
                }
                ItemStack helm = player.getInventory().getHelmet();
                if (helm != null && helm.getItemMeta() != null) {
                    if (helm.getItemMeta().getDisplayName().toLowerCase().endsWith("mickey ears")) {
                        player.getInventory().setHelmet(new ItemCreator(Material.AIR));
                    }
                }
                MagicAssistant.bandUtil.giveBandToPlayer(player);
                MagicAssistant.autographManager.giveBook(player);
                player.sendMessage(ChatColor.GREEN + "Inventory updated!");
            } else {
                ItemStack helm = player.getInventory().getHelmet();
                if (helm != null && helm.getItemMeta() != null && helm.getItemMeta().getDisplayName() != null) {
                    if (helm.getItemMeta().getDisplayName().toLowerCase().endsWith("mickey ears")) {
                        player.getInventory().setHelmet(new ItemStack(Material.AIR));
                    }
                }
                if (MagicAssistant.shooter.game != null) {
                    player.getInventory().remove(MagicAssistant.shooter.getItem().getType());
                }
                MagicAssistant.bandUtil.giveBandToPlayer(player);
                MagicAssistant.autographManager.giveBook(player);
            }
            if (MCMagicCore.getMCMagicConfig().serverName.equals("Resorts")) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(MagicAssistant.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        HotelManager manager = MagicAssistant.hotelManager;
                        for (HotelRoom room : manager.getHotelRooms()) {
                            if (room.getCheckoutNotificationRecipient() != null &&
                                    room.getCheckoutNotificationRecipient().equals(player.getUniqueId())) {
                                room.setCheckoutNotificationRecipient(null);
                                room.setCheckoutTime(0);
                                manager.updateHotelRoom(room);
                                manager.updateRooms();
                                player.sendMessage(ChatColor.GREEN + "Your reservation of the " + room.getName() +
                                        " room has lapsed and you have been checked out. Please come stay with us again soon!");
                                manager.expire.send(player);
                                player.playSound(player.getLocation(), Sound.BLAZE_DEATH, 10f, 1f);
                                return;
                            }
                            if (room.getCheckoutTime() <= (System.currentTimeMillis() / 1000) && room.getCurrentOccupant()
                                    != null && room.getCurrentOccupant().equals(player.getUniqueId())) {
                                manager.checkout(room, true);
                            }
                        }
                    }
                }, 60L);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void warpToNearestWarp(Player player) {
        Location loc = getLoc(player);
        loc.setWorld(Bukkit.getWorlds().get(0));
        Warp w = null;
        double distance = -1;
        for (Warp warp : new ArrayList<>(MagicAssistant.warps)) {
            if (!warp.getServer().equals(MCMagicCore.getMCMagicConfig().serverName)) {
                continue;
            }
            if (warp.getLocation() == null) {
                continue;
            }
            if (distance == -1) {
                w = warp;
                distance = warp.getLocation().distance(loc);
                continue;
            }
            double d = warp.getLocation().distance(loc);
            if (d < distance) {
                w = warp;
                distance = d;
            }
        }
        if (w == null) {
            player.performCommand("spawn");
            return;
        }
        player.teleport(w.getLocation());
    }

    private Location getLoc(Player player) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        if (ep == null) {
            Bukkit.broadcastMessage("Null!");
            return new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
        }
        return new Location(Bukkit.getWorlds().get(0), ep.locX, ep.locY, ep.locZ);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (player.getVehicle() != null) {
            player.getVehicle().eject();
        }
        if (MCMagicCore.getMCMagicConfig().serverName.equals("Resorts")) {
            HotelManager manager = MagicAssistant.hotelManager;
            for (HotelRoom room : manager.getRooms()) {
                if (room.isOccupied() && room.getCurrentOccupant().equals(player.getUniqueId())) {
                    manager.closeDoor(room);
                    break;
                }
            }
        }
        MagicAssistant.queueManager.silentLeaveAllQueues(player);
        MagicAssistant.tradeManager.logout(player);
        MagicAssistant.autographManager.logout(player);
        MagicAssistant.bandUtil.cancelLoadPlayerData(player.getUniqueId());
        MagicAssistant.bandUtil.removePlayerData(player);
        MagicAssistant.stitch.logout(player);
        VisibleUtil.logout(player.getUniqueId());
        Commandvanish.hidden.remove(player.getUniqueId());
        MagicAssistant.blockChanger.logout(player);
        if (MagicAssistant.shooter != null) {
            if (player.getInventory().contains(MagicAssistant.shooter.getItem())) {
                player.getInventory().remove(MagicAssistant.shooter.getItem());
            }
        }
        DesignStation.removePlayerVehicle(player.getUniqueId());
    }

    private List<byte[]> invContents(UUID uuid) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM inventory WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet results = sql.executeQuery();
            if (!results.next()) {
                results.close();
                sql.close();
                return null;
            }
            byte[] inv = results.getBytes("inventory");
            byte[] armor = results.getBytes("armor");
            byte[] end = results.getBytes("endinv");
            results.close();
            sql.close();
            return Arrays.asList(inv, armor, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ItemStack[] deserial(byte[] b) {
        return deserializeItemStacks(uncompress(b));
    }

    public static byte[] serial(ItemStack[] stacks) {
        for (int i = 0; i < stacks.length; i++) {
            ItemStack s = stacks[i];
            if (s == null) {
                continue;
            }
            if (s.getType().equals(Material.SKULL_ITEM)) {
                stacks[i] = new ItemStack(Material.AIR);
            }
        }
        return compress(serializeItemStacks(stacks));
    }

    public static byte[] serializeItemStacks(ItemStack[] inv) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                try (BukkitObjectOutputStream bos = new BukkitObjectOutputStream(os)) {
                    bos.writeObject(inv);
                    return os.toByteArray();
                }
            } finally {
                if (Collections.singletonList(os).get(0) != null)
                    os.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ItemStack[] deserializeItemStacks(byte[] b) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(b);
            try {
                try (BukkitObjectInputStream bois = new BukkitObjectInputStream(bais)) {
                    return (ItemStack[]) bois.readObject();
                }
            } finally {
                if (Collections.singletonList(bais).get(0) != null)
                    bais.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static byte[] compress(byte[] uncomp) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                GZIPOutputStream compressor = new GZIPOutputStream(os);
                compressor.write(uncomp);
                compressor.close();

                return os.toByteArray();
            } finally {
                if (Collections.singletonList(os).get(0) != null)
                    os.close();

            }

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public byte[] uncompress(byte[] comp) {
        GZIPInputStream decompressor = null;
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(comp);
            try {
                decompressor = new GZIPInputStream(is);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] b = new byte[256];
                int tmp;
                while ((tmp = decompressor.read(b)) != -1) {
                    buffer.write(b, 0, tmp);
                }
                buffer.close();
                byte[] arrayOfByte1 = buffer.toByteArray();

                if (Collections.singletonList(is).get(0) != null)
                    is.close();

                return arrayOfByte1;
            } finally {
                if (Collections.singletonList(is).get(0) != null)
                    is.close();

            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                if (decompressor != null)
                    decompressor.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static void updateInventory(UUID uuid, final String server, final boolean response) {
        final Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }
        boolean inv = true;
        boolean armor = true;
        boolean end = true;
        if (storedInv.containsKey(uuid)) {
            if (serial(player.getInventory().getContents()).equals(storedInv.remove(uuid))) {
                inv = false;
            }
        }
        if (storedArmor.containsKey(uuid)) {
            if (serial(player.getInventory().getArmorContents()).equals(storedArmor.remove(uuid))) {
                armor = false;
            }
        }
        if (storedEnd.containsKey(uuid)) {
            if (serial(player.getEnderChest().getContents()).equals(storedEnd.remove(uuid))) {
                end = false;
            }
        }
        if (!inv && !armor && !end) {
            if (response) {
                finishUpdate(player, server);
            }
            return;
        }
        final boolean finalInv = inv;
        final boolean finalArmor = armor;
        final boolean finalEnd = end;
        Bukkit.getScheduler().runTaskAsynchronously(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                try (Connection connection = SqlUtil.getConnection()) {
                    PreparedStatement contains = connection.prepareStatement("SELECT uuid FROM inventory WHERE uuid=?");
                    contains.setString(1, player.getUniqueId().toString());
                    ResultSet result = contains.executeQuery();
                    if (!result.next()) {
                        result.close();
                        contains.close();
                        PreparedStatement insert = connection.prepareStatement("INSERT INTO inventory values(0,?,?,?,?)");
                        insert.setString(1, player.getUniqueId().toString());
                        insert.setBytes(2, serial(player.getInventory().getContents()));
                        insert.setBytes(3, serial(player.getInventory().getArmorContents()));
                        insert.setBytes(4, serial(player.getEnderChest().getContents()));
                        insert.execute();
                        insert.close();
                    } else {
                        result.close();
                        contains.close();
                    }
                    PreparedStatement sql = connection.prepareStatement(getQuery(finalInv, finalArmor, finalEnd));
                    int num = 1;
                    if (finalInv) {
                        sql.setBytes(num, serial(player.getInventory().getContents()));
                        num++;
                    }
                    if (finalArmor) {
                        sql.setBytes(num, serial(player.getInventory().getArmorContents()));
                        num++;
                    }
                    if (finalEnd) {
                        sql.setBytes(num, serial(player.getEnderChest().getContents()));
                        num++;
                    }
                    sql.setString(num, player.getUniqueId().toString());
                    sql.execute();
                    sql.close();
                    if (response) {
                        finishUpdate(player, server);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void finishUpdate(Player player, String server) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("MagicInventory");
            out.writeUTF(player.getUniqueId().toString());
            out.writeUTF(server);
            player.sendPluginMessage(MagicAssistant.getInstance(), "BungeeCord", b.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getQuery(boolean inv, boolean armor, boolean end) {
        String q = "UPDATE inventory SET ";
        int count = 0;
        if (inv) {
            q += "inventory=?";
            count++;
        }
        if (armor) {
            if (count == 1) {
                q += ",";
            }
            q += "armor=?";
            count++;
        }

        if (end) {
            if (count > 0) {
                q += ",";
            }
            q += "endinv=?";
        }
        return q += " WHERE uuid=?";
    }
}