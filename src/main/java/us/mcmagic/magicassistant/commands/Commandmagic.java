package us.mcmagic.magicassistant.commands;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.blockchanger.Changer;
import us.mcmagic.magicassistant.handlers.PlayerData;
import us.mcmagic.magicassistant.queue.QueueRide;
import us.mcmagic.magicassistant.show.Show;
import us.mcmagic.magicassistant.show.ticker.TickEvent;
import us.mcmagic.magicassistant.utils.SqlUtil;
import us.mcmagic.magicassistant.utils.WorldUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.particles.ParticleUtil;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.PlayerUtil;
import us.mcmagic.mcmagiccore.player.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Marc on 1/7/15
 */
public class Commandmagic implements Listener, CommandExecutor {
    private static HashMap<String, Show> shows = new HashMap<>();
    public static List<String> containsCommandBlockOnly = new ArrayList<>();
    public ParticleEffect effect;
    public Location location;

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, final String[] args) {
        if (args.length == 0) {
            helpMenu("main", sender);
            return true;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            User user = MCMagicCore.getUser(player.getUniqueId());
            if (user.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
                return true;
            }
        }
        switch (args[0]) {
            case "effect":
                if (args.length == 1) {
                    helpMenu("effect", sender);
                    return true;
                }
                if (args[1].equalsIgnoreCase("particle")) {
                    if (args.length == 11) {
                        ParticleEffect effect = ParticleEffect.fromString(args[2]);
                        Location loc = locFromArray(args[3], args[4], args[5]);
                        try {
                            double offsetX = Float.parseFloat(args[6]);
                            double offsetY = Float.parseFloat(args[7]);
                            double offsetZ = Float.parseFloat(args[8]);
                            float speed = Float.parseFloat(args[9]);
                            int amount = getInt(args[10]);
                            for (Player tp : Bukkit.getOnlinePlayers()) {
                                if (tp.getLocation().distance(loc) > 50) {
                                    continue;
                                }
                                PlayerData data = MagicAssistant.getPlayerData(tp.getUniqueId());
                                if (!data.getFlash()) {
                                    continue;
                                }
                                ParticleUtil.spawnParticleForPlayer(effect, loc, (float) offsetX, (float) offsetY,
                                        (float) offsetZ, speed, amount, tp);
                            }
                            return true;
                        } catch (NumberFormatException ignored) {
                            sender.sendMessage(ChatColor.RED + "There was an error with numbers!");
                        }
                    }
                }
                if (args[1].equalsIgnoreCase("flash")) {
                    // magic effect(0) flash(1) x(2) y(3) z(4)
                    if (args.length == 5) {
                        try {
                            Location loc = new Location(Bukkit.getWorlds().get(0), Double.parseDouble(args[2]),
                                    Double.parseDouble(args[3]), Double.parseDouble(args[4]));
                            double x = loc.getX();
                            double y = loc.getY();
                            double z = loc.getZ();
                            Block block = loc.getBlock();
                            final Material type = block.getType();
                            final List<UUID> uuids = new ArrayList<>();
                            for (Player tp : Bukkit.getOnlinePlayers()) {
                                if (tp.getLocation().distance(loc) > 50) {
                                    continue;
                                }
                                PlayerData data = MagicAssistant.getPlayerData(tp.getUniqueId());
                                if (!data.getFlash()) {
                                    continue;
                                }
                                uuids.add(tp.getUniqueId());
                            }
                            PacketPlayOutBlockChange blockChangeFirst = new PacketPlayOutBlockChange(((CraftWorld)
                                    loc.getWorld()).getHandle(), new BlockPosition(x, y, z));
                            final PacketPlayOutBlockChange blockChangeSecond = new PacketPlayOutBlockChange(((CraftWorld)
                                    loc.getWorld()).getHandle(), new BlockPosition(x, y, z));
                            blockChangeFirst.block = CraftMagicNumbers.getBlock(Material.REDSTONE_LAMP_ON).fromLegacyData(0);
                            blockChangeSecond.block = CraftMagicNumbers.getBlock(type).fromLegacyData(block.getData());
                            for (UUID uuid : uuids) {
                                Player tp = Bukkit.getPlayer(uuid);
                                if (tp == null) {
                                    continue;
                                }
                                ((CraftPlayer) tp).getHandle().playerConnection.sendPacket(blockChangeFirst);
                            }
                            Bukkit.getScheduler().runTaskLater(MagicAssistant.getInstance(), new Runnable() {
                                @Override
                                public void run() {
                                    for (UUID uuid : uuids) {
                                        Player tp = Bukkit.getPlayer(uuid);
                                        if (tp == null) {
                                            continue;
                                        }
                                        ((CraftPlayer) tp).getHandle().playerConnection.sendPacket(blockChangeSecond);
                                    }
                                }
                            }, 6L);
                            return true;
                        } catch (NumberFormatException ignored) {
                            sender.sendMessage(ChatColor.RED + "There was an error with numbers!");
                            return true;
                        }
                    }
                }
                helpMenu("effect", sender);
                return true;
            case "show":
                if (args.length == 3) {
                    switch (args[1]) {
                        case "start":
                            if (shows.containsKey(args[2])) {
                                sender.sendMessage(ChatColor.RED + "That show is already running!");
                                return true;
                            }
                            File file = new File("plugins/MagicAssistant/shows/" + args[2] + ".show");
                            if (!file.exists()) {
                                sender.sendMessage(ChatColor.RED + "----------------------------------------------");
                                sender.sendMessage(ChatColor.GOLD + args[2] + ChatColor.AQUA + " is not an existing show file.");
                                sender.sendMessage(ChatColor.RED + "----------------------------------------------");
                            } else {
                                shows.put(args[2], new Show(MagicAssistant.getInstance(), file));
                                sender.sendMessage(ChatColor.GOLD + args[2] + ChatColor.AQUA + " has started. Enjoy the show!");
                            }
                            return true;
                        case "stop":
                            if (!shows.containsKey(args[2])) {
                                sender.sendMessage(ChatColor.RED + "----------------------------------------------");
                                sender.sendMessage(ChatColor.GOLD + args[2] + ChatColor.AQUA + " is not running!");
                                sender.sendMessage(ChatColor.RED + "----------------------------------------------");
                            } else {
                                sender.sendMessage(ChatColor.GOLD + args[2] + ChatColor.AQUA + " has been stopped!");
                                shows.remove(args[2]);
                            }
                            return true;
                        default:
                            return true;
                    }
                }
                helpMenu("show", sender);
                return true;
            case "rc":
                if (args.length > 3 && args[1].equalsIgnoreCase("add")) {
                    final Player tp = PlayerUtil.findPlayer(args[2]);
                    if (tp == null) {
                        sender.sendMessage(ChatColor.RED + "Player not found!");
                        return true;
                    }
                    String rideName = "";
                    for (int i = 3; i < args.length; i++) {
                        rideName += args[i];
                        if ((i - 2) < (args.length)) {
                            rideName += " ";
                        }
                    }
                    final String finalRideName = rideName;
                    Bukkit.getScheduler().runTaskAsynchronously(MagicAssistant.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            PlayerData data = MagicAssistant.getPlayerData(tp.getUniqueId());
                            HashMap<String, Integer> counts = data.getRideCounts();
                            if (!counts.containsKey(finalRideName)) {
                                counts.put(finalRideName, 1);
                                try (Connection connection = SqlUtil.getConnection()) {
                                    PreparedStatement sql = connection.prepareStatement("INSERT INTO ridecounter (uuid," +
                                            " name, server) VALUES (?,?,?)");
                                    sql.setString(1, tp.getUniqueId().toString());
                                    sql.setString(2, finalRideName);
                                    sql.setString(3, MCMagicCore.getMCMagicConfig().serverName);
                                    sql.execute();
                                    sql.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                counts.put(finalRideName, counts.remove(finalRideName) + 1);
                                try (Connection connection = SqlUtil.getConnection()) {
                                    PreparedStatement sql = connection.prepareStatement("UPDATE ridecounter SET " +
                                            "count=count+1 WHERE uuid=? AND name=?");
                                    sql.setString(1, tp.getUniqueId().toString());
                                    sql.setString(2, finalRideName);
                                    sql.execute();
                                    sql.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                            data.setRideCounts(counts);
                            sender.sendMessage(ChatColor.GREEN + "Added 1 to " + tp.getName() + "'s counter for " +
                                    finalRideName);
                            tp.sendMessage(ChatColor.GOLD + "-" + ChatColor.MAGIC + "------" + ChatColor.RESET + ChatColor.GOLD +
                                    "--------------------------------------" + ChatColor.MAGIC + "------" +
                                    ChatColor.RESET + ChatColor.GOLD + "-\n  " + ChatColor.YELLOW +
                                    "       Ride Counter for " + ChatColor.AQUA + finalRideName + ChatColor.YELLOW +
                                    "is now at " + ChatColor.GREEN + data.getRideCounts().get(finalRideName) + "\n" +
                                    ChatColor.GOLD + "-" + ChatColor.MAGIC + "------" + ChatColor.RESET + ChatColor.GOLD
                                    + "--------------------------------------" + ChatColor.MAGIC + "------" +
                                    ChatColor.RESET + ChatColor.GOLD + "-");
                            tp.playSound(tp.getLocation(), Sound.SUCCESSFUL_HIT, 100f, 0.75f);
                            Bukkit.getScheduler().runTaskLater(MagicAssistant.getInstance(), new Runnable() {
                                @Override
                                public void run() {
                                    tp.playSound(tp.getLocation(), Sound.SUCCESSFUL_HIT, 100f, 1f);
                                }
                            }, 2L);
                        }
                    });
                    return true;
                }
                helpMenu("ride", sender);
                return true;
            case "uoe":
                if (args.length == 4) {
                    if (args[1].equalsIgnoreCase("eject")) {
                        try {
                            Location loc = getLocation(Bukkit.getWorlds().get(0), args[2]);
                            int radius = getInt(args[3]);
                            MagicAssistant.universeEnergyRide.eject(loc, radius);
                        } catch (Exception e) {
                            sender.sendMessage(ChatColor.RED + "There was an error!");
                            e.printStackTrace();
                        }
                        return true;
                    }
                }
                if (args.length == 10) {
                    if (args[1].equalsIgnoreCase("move")) {
                        try {
                            World world = Bukkit.getWorlds().get(0);
                            String[] list = args[2].split(":");
                            int id;
                            Byte data = null;
                            if (list.length == 2) {
                                id = getInt(list[0]);
                                data = Byte.parseByte(list[1]);
                            } else {
                                id = getInt(args[2]);
                            }
                            double speed = getDouble(args[3]);
                            Location loc1 = getLocation(world, args[4]);
                            Location loc2 = getLocation(world, args[5]);
                            double moveX = getDouble(args[6]);
                            double moveY = getDouble(args[7]);
                            double moveZ = getDouble(args[8]);
                            int radius = getInt(args[9]);
                            MagicAssistant.universeEnergyRide.moveBlocks(loc1, loc2, id, data, moveX, moveY,
                                    moveZ, speed, radius);
                            sender.sendMessage(ChatColor.GREEN + "Spawned BlockMover successfully");
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "There was an error with numbers!");
                            e.printStackTrace();
                        } catch (Exception e) {
                            sender.sendMessage(ChatColor.RED + "There was an error!");
                            e.printStackTrace();
                        }
                    }
                    return true;
                }
                helpMenu("uoe", sender);
                return true;
            case "sge":
                switch (args.length) {
                    case 2:
                        if (args[1].equalsIgnoreCase("lock")) {
                            MagicAssistant.stitch.toggleLock(sender);
                        } else if (args[1].equalsIgnoreCase("eject")) {
                            MagicAssistant.stitch.ejectAll(sender);
                        } else if (args[1].equalsIgnoreCase("add")) {
                            if (!(sender instanceof Player)) {
                                return true;
                            }
                            Player player = (Player) sender;
                            try {
                                MagicAssistant.stitch.addSeat(player);
                            } catch (IOException e) {
                                player.sendMessage(ChatColor.RED + "There was an error doing this command!");
                                e.printStackTrace();
                            }
                        }
                        return true;
                    case 3:
                        if (args[1].equalsIgnoreCase("join")) {
                            Player tp = PlayerUtil.findPlayer(args[2]);
                            if (tp == null) {
                                sender.sendMessage(ChatColor.RED + "Player not found!");
                                return true;
                            }
                            MagicAssistant.stitch.joinShow(tp);
                        }
                        return true;
                    case 4:
                        if (args[1].equalsIgnoreCase("effect")) {
                            if (args[2].equalsIgnoreCase("burp")) {
                                Location loc = WorldUtil.strToLoc(Bukkit.getWorlds().get(0).getName() + "," + args[3]);
                                ParticleUtil.spawnParticle(ParticleEffect.HAPPY_VILLAGER, loc, (float) 3, (float) 3, (float) 3, 0, 150);
                                return true;
                            }
                            if (args[2].equalsIgnoreCase("spit")) {
                                Location loc = WorldUtil.strToLoc(Bukkit.getWorlds().get(0).getName() + "," + args[3]);
                                ParticleUtil.spawnParticle(ParticleEffect.LAVA, loc, (float) 3, (float) 3, (float) 3, 0, 150);
                                return true;
                            }
                        }
                        return true;
                    default:
                        helpMenu("sge", sender);
                        return true;
                }
            case "shooter":
                if (MagicAssistant.shooter == null) {
                    sender.sendMessage(ChatColor.RED + "Shooter is Disabled!");
                    return true;
                }
                switch (args.length) {
                    case 3:
                        if (args[1].equalsIgnoreCase("add")) {
                            Player player = PlayerUtil.findPlayer(args[2]);
                            if (player == null) {
                                sender.sendMessage(ChatColor.RED + "Could not find Player!");
                                return true;
                            }
                            PlayerInventory inv = player.getInventory();
                            player.setMetadata("shooter", new FixedMetadataValue(MagicAssistant.getInstance(), 0));
                            inv.setItem(4, MagicAssistant.shooter.getItem());
                            inv.setHeldItemSlot(4);
                            MagicAssistant.shooter.sendGameMessage(player);
                            MagicAssistant.shooter.join(player);
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("remove")) {
                            Player player = PlayerUtil.findPlayer(args[2]);
                            if (player == null) {
                                sender.sendMessage(ChatColor.RED + "Could not find Player!");
                                return true;
                            }
                            PlayerInventory inv = player.getInventory();
                            if (!inv.contains(MagicAssistant.shooter.getItem())) {
                                return true;
                            }
                            MagicAssistant.shooter.done(player);
                            return true;
                        }
                }
                helpMenu("shooter", sender);
                break;
            case "changer":
                switch (args.length) {
                    case 2:
                        switch (args[1]) {
                            case "list":
                                List<String> list = MagicAssistant.blockChanger.changerList();
                                if (list.isEmpty()) {
                                    sender.sendMessage(ChatColor.RED + "No Changers on this server!");
                                    return true;
                                }
                                sender.sendMessage(ChatColor.GREEN + "Changer List:");
                                for (String s : list) {
                                    sender.sendMessage(ChatColor.AQUA + s);
                                }
                                return true;
                            case "reload":
                                sender.sendMessage(ChatColor.GREEN + "Reloading Changer Areas...");
                                try {
                                    MagicAssistant.blockChanger.reload();
                                } catch (FileNotFoundException e) {
                                    sender.sendMessage(ChatColor.RED + "Error reloading, see console for details.");
                                    e.printStackTrace();
                                    return true;
                                }
                                sender.sendMessage(ChatColor.GREEN + "Reload Complete!");
                                return true;
                            case "debug":
                                if (sender instanceof Player) {
                                    if (MagicAssistant.blockChanger.toggleDebug(((Player) sender))) {
                                        sender.sendMessage(ChatColor.YELLOW + "You're no longer in Changer Debugging mode!");
                                        return true;
                                    } else {
                                        sender.sendMessage(ChatColor.YELLOW + "You're now in Changer Debugging mode!");
                                        return true;
                                    }
                                }
                                return true;
                            case "wand":
                                if (!(sender instanceof Player)) {
                                    return true;
                                }
                                ItemStack wand = new ItemCreator(Material.DIAMOND_AXE, ChatColor.LIGHT_PURPLE +
                                        "Changer Wand", Collections.singletonList(""));
                                ((Player) sender).getInventory().addItem(wand);
                                sender.sendMessage(ChatColor.LIGHT_PURPLE + "There's your wand!");
                                sender.sendMessage(ChatColor.RED + "REMEMBER: NorthWest bottom to SouthEast top");
                                sender.sendMessage(ChatColor.RED + "If done any other way, it won't work!");
                                return true;
                        }
                    case 3:
                        switch (args[1]) {
                            case "debug":
                                Player tp = PlayerUtil.findPlayer(args[2]);
                                if (tp == null) {
                                    sender.sendMessage(ChatColor.RED + "Player not found!");
                                    return true;
                                }
                                Rank rank = MCMagicCore.getUser(tp.getUniqueId()).getRank();
                                if (rank.getRankId() < Rank.CASTMEMBER.getRankId()) {
                                    sender.sendMessage(ChatColor.RED + "Could not toggle for user " + tp.getName() +
                                            ", that user is not at least the " + Rank.CASTMEMBER.getNameWithBrackets()
                                            + ChatColor.RED + " Rank!");
                                    return true;
                                }
                                if (MagicAssistant.blockChanger.toggleDebug(tp)) {
                                    sender.sendMessage(ChatColor.YELLOW + "Removed " + tp.getName() + " from Changer Debugging!");
                                    tp.sendMessage(ChatColor.YELLOW + "You're no longer in Changer Debugging mode!");
                                    return true;
                                } else {
                                    sender.sendMessage(ChatColor.YELLOW + "Added " + tp.getName() + " to Changer Debugging!");
                                    tp.sendMessage(ChatColor.YELLOW + "You're now in Changer Debugging mode!");
                                    return true;
                                }
                            case "remove":
                                final Changer changer = MagicAssistant.blockChanger.getChanger(args[2]);
                                if (changer == null) {
                                    sender.sendMessage(ChatColor.RED + "Changer not found by the name of " +
                                            ChatColor.GREEN + args[2]);
                                    return true;
                                }
                                final List<Changer> l = MagicAssistant.blockChanger.getChangers();
                                Bukkit.getScheduler().runTaskAsynchronously(MagicAssistant.getInstance(), new Runnable() {
                                    @Override
                                    public void run() {
                                        for (Player p : Bukkit.getOnlinePlayers()) {
                                            for (Changer c : l) {
                                                if (c.getFirstLocation().distance(p.getLocation()) < 75) {
                                                    c.sendReverse(p);
                                                }
                                            }
                                        }
                                        MagicAssistant.blockChanger.removeChanger(changer.getName());
                                        sender.sendMessage(ChatColor.GREEN + "Removed changer " + ChatColor.AQUA + args[2]);
                                    }
                                });
                                return true;
                            case "info":
                                Changer chngr = MagicAssistant.blockChanger.getChanger(args[2]);
                                if (chngr == null) {
                                    sender.sendMessage(ChatColor.RED + "Changer not found by the name of " +
                                            ChatColor.GREEN + args[2]);
                                    return true;
                                }
                                Location l1 = chngr.getFirstLocation();
                                Location l2 = chngr.getSecondLocation();
                                List<Material> mats = chngr.getFrom();
                                String f = "";
                                for (int i = 0; i < mats.size(); i++) {
                                    Material m = mats.get(i);
                                    f += m.name();
                                    if (i < (mats.size() - 1)) {
                                        f += ", ";
                                    }
                                }
                                String[] list = new String[]{ChatColor.AQUA + "Info for " + chngr.getName() + " Changer: ",
                                        "Location 1: " + l1.getBlockX() + "," + l1.getBlockY() + "," + l1.getBlockZ(),
                                        "Location 2: " + l2.getBlockX() + "," + l2.getBlockY() + "," + l2.getBlockZ(),
                                        "From: " + f, "To: " + chngr.getTo(), "Sender: " + chngr.getSender()};
                                for (String s : list) {
                                    sender.sendMessage(ChatColor.GREEN + s);
                                }
                                return true;
                        }
                    case 6:
                        if (args[1].equalsIgnoreCase("create")) {
                            try {
                                String name = args[2];
                                if (MagicAssistant.blockChanger.getChanger(name) != null) {
                                    sender.sendMessage(ChatColor.RED + "A changer with that name already exists!");
                                    return true;
                                }
                                HashMap<Material, Byte> from = MagicAssistant.blockChanger.blocksFromString(args[3]);
                                int to;
                                byte data;
                                String[] list = args[4].split(":");
                                if (list.length == 1) {
                                    to = Integer.parseInt(list[0]);
                                    data = (byte) 0;
                                } else {
                                    to = Integer.parseInt(list[0]);
                                    data = Byte.valueOf(list[1]);
                                }
                                int send = Integer.parseInt(args[5]);
                                Location loc1 = MagicAssistant.blockChanger.getSelection(0, ((Player) sender));
                                //loc1 = getLocation(((Player) sender).getWorld(), args[6]);
                                Location loc2 = MagicAssistant.blockChanger.getSelection(1, ((Player) sender));
                                //loc2 = getLocation(((Player) sender).getWorld(), args[7]);
                                if (loc1 == null || loc2 == null) {
                                    sender.sendMessage(ChatColor.RED + "You don't have a full selection selected!");
                                    return true;
                                }
                                Changer changer = new Changer(name, loc1, loc2, from, Material.getMaterial(to), data,
                                        Material.getMaterial(send));
                                MagicAssistant.blockChanger.addChanger(changer);
                                sender.sendMessage(ChatColor.GREEN + "Created Changer " + ChatColor.AQUA + name);
                                MagicAssistant.blockChanger.clearSelection(((Player) sender).getUniqueId());
                                return true;
                            } catch (Exception e) {
                                sender.sendMessage(ChatColor.RED + "There was an error creating that Changer!");
                                e.printStackTrace();
                                return true;
                            }
                        }
                    default:
                        helpMenu("changer", sender);
                        return true;
                }
            case "hotel":
                sender.sendMessage(ChatColor.BLUE + "Reloading hotel rooms...");
                MagicAssistant.hotelManager.refreshRooms();
                sender.sendMessage(ChatColor.BLUE + "Hotel rooms reloaded!");
                return true;
            case "queue":
                if (args.length == 3) {
                    QueueRide ride = MagicAssistant.queueManager.getRide(args[1]);
                    if (ride == null) {
                        sender.sendMessage(ChatColor.RED + "Ride not found!");
                        return true;
                    }
                    if (args[2].equalsIgnoreCase("info")) {
                        String wait = "Wait Time: " + ride.appxWaitTime();
                        sender.sendMessage(ChatColor.GREEN + ride.getName() + ChatColor.YELLOW + "\n" + (ride.getQueueSize()
                                <= 0 ? "Wait Time: No Wait" : wait) + "\nIn Queue: " + ride.getQueueSize() +
                                "\nRiders per Group: " + ride.getAmountOfRiders() + "\nDelay between rides: " +
                                ride.getDelay());
                        return true;
                    }
                    if (args[2].equalsIgnoreCase("eject")) {
                        ride.ejectQueue();
                        sender.sendMessage(ride.getName() + ChatColor.GREEN + "'s Queue has been ejected!");
                        return true;
                    }
                    if (args[2].equalsIgnoreCase("fp")) {
                        if (ride.toggleFastpass()) {
                            sender.sendMessage(ChatColor.GREEN + "The FastPass line has been closed!");
                        } else {
                            sender.sendMessage(ChatColor.GREEN + "The FastPass line has been opened!");
                        }
                        return true;
                    }
                    if (args[2].equalsIgnoreCase("pause")) {
                        ride.setPaused(true);
                        sender.sendMessage(ChatColor.GREEN + "Paused!");
                        return true;
                    }
                    if (args[2].equalsIgnoreCase("unpause")) {
                        ride.setPaused(false);
                        sender.sendMessage(ChatColor.GREEN + "Un-Paused!");
                        return true;
                    }
                    if (args[2].equalsIgnoreCase("freeze")) {
                        if (ride.toggleFreeze()) {
                            sender.sendMessage(ChatColor.GREEN + "Queue frozen!");
                        } else {
                            sender.sendMessage(ChatColor.GREEN + "Queue unfrozen!");
                        }
                        return true;
                    }
                    if (args[2].equalsIgnoreCase("list")) {
                        List<UUID> users = ride.getQueue();
                        String s = ChatColor.GREEN + "Currently in Queue for " + ride.getName() + ChatColor.GREEN +
                                ": (" + users.size() + ")\n" + ChatColor.YELLOW;
                        for (int i = 0; i < users.size(); i++) {
                            Player tp = Bukkit.getPlayer(users.get(i));
                            if (tp == null) {
                                continue;
                            }
                            if (i == users.size() - 1) {
                                s += tp.getName();
                            } else {
                                s += tp.getName() + ", ";
                            }
                        }
                        if (users.size() == 0) {
                            s += "None";
                        }
                        sender.sendMessage(s);
                        return true;
                    }
                    helpMenu("queue", sender);
                    return true;
                }
                if (args.length == 4) {
                    QueueRide ride = MagicAssistant.queueManager.getRide(args[1]);
                    if (args[2].equalsIgnoreCase("set")) {
                        if (args[3].equalsIgnoreCase("station")) {
                            try {
                                ride.setStation(((Player) sender).getLocation());
                                sender.sendMessage(ChatColor.GREEN + "Station set!");
                            } catch (IOException e) {
                                e.printStackTrace();
                                sender.sendMessage(ChatColor.RED + "There was an error!");
                            }
                        }
                        if (args[3].equalsIgnoreCase("spawner")) {
                            try {
                                ride.setSpawner(((Player) sender).getLocation());
                                sender.sendMessage(ChatColor.GREEN + "Spawner set!");
                            } catch (IOException e) {
                                e.printStackTrace();
                                sender.sendMessage(ChatColor.RED + "There was an error!");
                            }
                        }
                    }
                    return true;
                }
                helpMenu("queue", sender);
                return true;
            case "reload":
                MagicAssistant ma = MagicAssistant.getInstance();
                sender.sendMessage(ChatColor.BLUE + "Reloading Plugin...");
                SqlUtil.initialize();
                ma.setupFirstJoinItems();
                ma.setupFoodLocations();
                MagicAssistant.parkSoundManager.initialize();
                ma.setupRides();
                MagicAssistant.stitch.initialize();
                MagicAssistant.itemUtil.initialize();
                MagicAssistant.hotelManager.refreshRooms();
                try {
                    MagicAssistant.blockChanger.reload();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                MagicAssistant.packManager.initialize();
                MagicAssistant.shopManager.initialize();
                MagicAssistant.queueManager.initialize();
                sender.sendMessage(ChatColor.BLUE + "Plugin Reloaded!");
                return true;
            default:
                helpMenu("main", sender);
        }
        return true;
    }

    private Location locFromArray(String... args) throws NumberFormatException {
        if (args.length != 3) {
            return null;
        }
        return new Location(Bukkit.getWorlds().get(0), Double.parseDouble(args[0]), Double.parseDouble(args[1]),
                Double.parseDouble(args[2]));
    }

    public static void helpMenu(String menu, CommandSender sender) {
        switch (menu) {
            case "main":
                sender.sendMessage(ChatColor.GREEN + "Magic Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic changer " + ChatColor.AQUA + "- Change blocks for rides!");
                sender.sendMessage(ChatColor.GREEN + "/magic effect " + ChatColor.AQUA + "- Cool effects for shows!");
                sender.sendMessage(ChatColor.GREEN + "/magic reload " + ChatColor.AQUA + "- Reload plugin");
                sender.sendMessage(ChatColor.GREEN + "/magic sge " + ChatColor.AQUA + "- Features for SGE");
                sender.sendMessage(ChatColor.GREEN + "/magic shooter " + ChatColor.AQUA + "- Features for Shooter Games");
                sender.sendMessage(ChatColor.GREEN + "/magic show " + ChatColor.AQUA + "- Control a Show");
                sender.sendMessage(ChatColor.GREEN + "/magic rc " + ChatColor.AQUA + "- Ride Counters");
                sender.sendMessage(ChatColor.GREEN + "/magic uoe " + ChatColor.AQUA + "- Features for Universe of Energy");
                break;
            case "shooter":
                sender.sendMessage(ChatColor.GREEN + "Shooter Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic shooter add [Name] " + ChatColor.AQUA + "- Adds player to Shooter Game");
                sender.sendMessage(ChatColor.GREEN + "/magic shooter remove [Name] " + ChatColor.AQUA + "- Removes player from Shooter Game");
                break;
            case "show":
                sender.sendMessage(ChatColor.GREEN + "Show Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic show start [Show Name] " + ChatColor.AQUA + "- Start a Show");
                sender.sendMessage(ChatColor.GREEN + "/magic show stop [Show Name] " + ChatColor.AQUA + "- Stop a Show");
                break;
            case "uoe":
                sender.sendMessage(ChatColor.GREEN + "UOE Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic uoe move BlockID(:Data) [speed] x1,y1,z1 x2,y2,z2 addX addY addZ radius");
                sender.sendMessage(ChatColor.GREEN + "/magic uoe eject x,y,z radius");
                break;
            case "sge":
                sender.sendMessage(ChatColor.GREEN + "Stitch Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic sge lock " + ChatColor.AQUA + "- Locks/Unlocks Show Room");
                sender.sendMessage(ChatColor.GREEN + "/magic sge eject " + ChatColor.AQUA + "- Ejects all players from their seats");
                sender.sendMessage(ChatColor.GREEN + "/magic sge join [Player name] " + ChatColor.AQUA + "- Adds a player to the Show" + ChatColor.RED + "*");
                sender.sendMessage(ChatColor.GREEN + "/magic sge add " + ChatColor.AQUA + "- Add a seat to the show where you're standing");
                sender.sendMessage(ChatColor.GREEN + "/magic sge effect burp x,y,z" + ChatColor.AQUA + "- Displays the Burp effect at a given location");
                sender.sendMessage(ChatColor.GREEN + "/magic sge effect spit x,y,z" + ChatColor.AQUA + "- Displays the Spit effect at a given location");
                break;
            case "changer":
                sender.sendMessage(ChatColor.GREEN + "Changer Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic changer debug [Player] " + ChatColor.AQUA +
                        "- Toggle debug mode for a player");
                sender.sendMessage(ChatColor.GREEN + "/magic changer list " + ChatColor.AQUA + "- List all Changer Areas");
                sender.sendMessage(ChatColor.GREEN + "/magic changer create [Name] [From] [To] [Sender]" + ChatColor.AQUA
                        + "- Create a Changer Area");
                sender.sendMessage(ChatColor.AQUA + "*Remember, use Block IDs instead of Block Names*");
                sender.sendMessage(ChatColor.GREEN + "/magic changer wand " + ChatColor.AQUA + "- Get the Changer Wand");
                sender.sendMessage(ChatColor.GREEN + "/magic changer remove [Name] " + ChatColor.AQUA + "- Remove a Changer Area");
                sender.sendMessage(ChatColor.GREEN + "/magic changer reload " + ChatColor.AQUA + "- Reload areas");
                break;
            case "effect":
                sender.sendMessage(ChatColor.GREEN + "Effect Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic effect particle type x y z xd yd zd speed amount");
                sender.sendMessage(ChatColor.GREEN + "/magic effect flash x y z");
                break;
            case "queue":
                sender.sendMessage(ChatColor.GREEN + "Queue Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic queue [Queue] info " + ChatColor.AQUA + "- Queue Info");
                sender.sendMessage(ChatColor.GREEN + "/magic queue [Queue] eject " + ChatColor.AQUA +
                        "- Eject all from Queue");
                sender.sendMessage(ChatColor.GREEN + "/magic queue [Queue] freeze " + ChatColor.AQUA +
                        "- Freeze/unfreeze the Queue");
                sender.sendMessage(ChatColor.GREEN + "/magic queue [Queue] list " + ChatColor.AQUA +
                        "- List players in Queue");
                sender.sendMessage(ChatColor.GREEN + "/magic queue [Queue] fp " + ChatColor.AQUA +
                        "- Close/Open the FastPass queue");
                sender.sendMessage(ChatColor.GREEN + "/magic queue [Queue] pause " + ChatColor.AQUA +
                        "- Pause station teleport");
                sender.sendMessage(ChatColor.GREEN + "/magic queue [Queue] unpause " + ChatColor.AQUA +
                        "- Un-Pause station teleport");
                break;
            case "ride":
                sender.sendMessage(ChatColor.GREEN + "Ride Counter Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic rc add [Player] [Ride Name] " + ChatColor.AQUA +
                        "- Increment a player's Ride Count value.");
        }
        if (containsCommandBlockOnly.contains(menu)) {
            sender.sendMessage(ChatColor.RED + "* Only Command Blocks can do this");
        }
    }

    @EventHandler
    public void onTick(TickEvent event) {
        for (Map.Entry<String, Show> entry : new HashSet<>(shows.entrySet())) {
            Show show = entry.getValue();
            if (show.update()) {
                System.out.print("Show " + entry.getKey() + " Ended.");
                shows.remove(entry.getKey());
            }
        }
    }

    public static int getInt(String s) throws NumberFormatException {
        return Integer.parseInt(s);
    }

    public static float getFloat(String s) throws NumberFormatException {
        return Float.parseFloat(s);
    }

    public static double getDouble(String s) throws NumberFormatException {
        return Double.parseDouble(s);
    }

    public static Location getLocation(World world, String s) throws Exception {
        String[] list = s.split(",");
        return new Location(world, Double.parseDouble(list[0]), Double.parseDouble(list[1]), Double.parseDouble(list[2]));
    }

    @SuppressWarnings("deprecation")
    public static List<Block> getBlocks(Location min, Location max, int select) {
        List<Block> list = new ArrayList<>();
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    Block blk = min.getWorld().getBlockAt(new Location(min.getWorld(), x, y, z));
                    if (blk.getTypeId() == select) {
                        list.add(blk);
                    }
                }
            }
        }
        return list;
    }

    public static void startShow(String name) {
        File file = new File("plugins/MagicAssistant/shows/" + name + ".show");
        if (!file.exists()) {
            System.out.print("");
        } else {
            shows.put(name, new Show(MagicAssistant.getInstance(), file));
        }
    }
}
