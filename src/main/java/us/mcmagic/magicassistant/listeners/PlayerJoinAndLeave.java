package us.mcmagic.magicassistant.listeners;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
import us.mcmagic.magicassistant.shooter.Shooter;
import us.mcmagic.magicassistant.hotels.HotelManager;
import us.mcmagic.magicassistant.utils.InventorySql;
import us.mcmagic.magicassistant.utils.SqlUtil;
import us.mcmagic.magicassistant.utils.VisibleUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;
import us.mcmagic.mcmagiccore.title.TitleObject;

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
    public UUID lego = UUID.fromString("9ab3b4c4-71d8-47c9-9e7d-adf040c53d2b");
    private static HashMap<UUID, byte[]> storedInv = new HashMap<>();
    private static HashMap<UUID, byte[]> storedArmor = new HashMap<>();
    private static HashMap<UUID, byte[]> storedEnd = new HashMap<>();

    @EventHandler
    public void onAsyncLogin(AsyncPlayerPreLoginEvent event) {
        try {
            UUID uuid = event.getUniqueId();
            if (MagicAssistant.crossServerInv || uuid.equals(lego)) {
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
                }
            }
            PlayerData data = MagicAssistant.bandUtil.setupPlayerData(uuid);
            if (MagicAssistant.getPlayerData(uuid) == null) {
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
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
        if (user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
            event.setKickMessage("This server will be available soon!");
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            final Player player = event.getPlayer();
            User user = MCMagicCore.getUser(player.getUniqueId());
            if (!MagicAssistant.userCache.containsKey(player.getUniqueId())) {
                MagicAssistant.userCache.put(player.getUniqueId(), player.getName());
            } else {
                if (!MagicAssistant.userCache.get(player.getUniqueId()).equals(player.getName())) {
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
                //warpToNearestWarp(player);
            }
            for (String msg : MagicAssistant.joinMessages) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
            if (user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                VisibleUtil.hideForHideAll(player);
            }
            if (MagicAssistant.hubServer) {
                if (!player.hasPlayedBefore()) {
                    /*
                    int total = Bukkit.getOfflinePlayers().length + 100000;
                    for (String msg : MagicAssistant.newJoinMessage) {
                        String nmsg = msg.replaceAll("%pl%", player.getName());
                        Bukkit.broadcastMessage(nmsg.replaceAll("%total%", "" + (total)));
                    }
                    */
                    PlayerInventory pi = player.getInventory();
                    for (Map.Entry<Integer, Integer> item : MagicAssistant.firstJoinItems.entrySet()) {
                        ItemStack i = new ItemStack(item.getKey(), item.getValue());
                        pi.addItem(i);
                    }
                }
            }
            if (MagicAssistant.crossServerInv || player.getUniqueId().equals(lego)) {
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
                if (Shooter.game != null) {
                    pi.remove(Shooter.getItem().getType());
                }
                ItemStack helm = player.getInventory().getHelmet();
                if (helm != null && helm.getItemMeta() != null) {
                    if (helm.getItemMeta().getDisplayName().toLowerCase().endsWith("mickey ears")) {
                        player.getInventory().setHelmet(new ItemCreator(Material.AIR));
                    }
                }
                //startLoginScreen(player);
                MagicAssistant.bandUtil.giveBandToPlayer(player);
                player.sendMessage(ChatColor.GREEN + "Inventory updated!");
            } else {
                ItemStack helm = player.getInventory().getHelmet();
                if (helm != null && helm.getItemMeta() != null && helm.getItemMeta().getDisplayName() != null) {
                    if (helm.getItemMeta().getDisplayName().toLowerCase().endsWith("mickey ears")) {
                        player.getInventory().setHelmet(new ItemStack(Material.AIR));
                    }
                }
                if (Shooter.game != null) {
                    player.getInventory().remove(Shooter.getItem().getType());
                }
            }
            MagicAssistant.bandUtil.giveBandToPlayer(player);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void warpToNearestWarp(Player player) {
        Location loc = player.getLocation();
        Warp w = null;
        double distance = -1;
        for (Warp warp : new ArrayList<>(MagicAssistant.warps)) {
            if (distance == -1) {
                w = warp;
                distance = loc.distance(warp.getLocation());
                continue;
            }
            if (loc.distance(warp.getLocation()) < distance) {
                w = warp;
            }
        }
        if (w == null) {
            return;
        }
        player.teleport(w.getLocation());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (MagicAssistant.crossServerInv) {
            Bukkit.getScheduler().runTaskAsynchronously(MagicAssistant.getInstance(), new Runnable() {
                @Override
                public void run() {
                    InventorySql.updateInventory(player);
                    InventorySql.updateEndInventory(player);
                }
            });
        }
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
        MagicAssistant.tradeManager.logout(player);
        MagicAssistant.bandUtil.cancelLoadPlayerData(player.getUniqueId());
        MagicAssistant.bandUtil.removePlayerData(player);
        VisibleUtil.logout(player.getUniqueId());
        Commandvanish.hidden.remove(player.getUniqueId());
        MagicAssistant.blockChanger.logout(player);
        if (Shooter.getItem() != null) {
            if (player.getInventory().contains(Shooter.getItem())) {
                player.getInventory().remove(Shooter.getItem());
            }
        }
        DesignStation.removePlayerVehicle(player.getUniqueId());
    }

    private void startLoginScreen(final Player player) {
        Bukkit.getScheduler().runTaskTimer(MagicAssistant.getInstance(), new Runnable() {
            int i = 0;

            @Override
            public void run() {
                i++;
                switch (i) {
                    case 2:
                        TitleObject title = new TitleObject(ChatColor.AQUA + "Welcome to " + ChatColor.LIGHT_PURPLE +
                                "MCMagic,", ChatColor.GREEN + player.getName()).setFadeIn(20).setStay(40).setFadeOut(5);
                        title.send(player);
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1);
                        break;
                    case 6:
                        TitleObject quiz = new TitleObject(ChatColor.AQUA + "Please take a quiz,", ChatColor.GREEN +
                                "then you get to play!").setFadeIn(5).setStay(40).setFadeOut(5);
                        quiz.send(player);
                        break;
                    case 9:
                        TitleObject click = new TitleObject(ChatColor.AQUA + "Click the answer", ChatColor.GREEN +
                                "by opening chat and clicking.").setFadeIn(5).setStay(40).setFadeOut(5);
                        click.send(player);
                        break;
                    case 10:
                        MagicAssistant.quizManager.start(player);
                }
            }
        }, 0L, 20L);
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