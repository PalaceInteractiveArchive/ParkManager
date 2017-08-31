package network.palace.parkmanager.commands;

import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.io.NbtTextSerializer;
import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.blockchanger.Changer;
import network.palace.parkmanager.handlers.Outfit;
import network.palace.parkmanager.handlers.PlayerData;
import network.palace.parkmanager.handlers.Resort;
import network.palace.parkmanager.handlers.RideCount;
import network.palace.parkmanager.listeners.BlockEdit;
import network.palace.parkmanager.queue.handlers.AbstractQueueRide;
import network.palace.parkmanager.queue.tot.DropTower;
import network.palace.parkmanager.queue.tot.TowerLayout;
import network.palace.parkmanager.utils.WorldUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Marc on 1/7/15
 */
@CommandMeta(description = "Command with magical stuff")
@CommandPermission(rank = Rank.KNIGHT)
public class Commandmagic extends CoreCommand {
    public Location location;

    public Commandmagic() {
        super("magic");
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            helpMenu("main", sender);
            return;
        }
        if (sender instanceof Player) {
            CPlayer player = Core.getPlayerManager().getPlayer(((Player) sender).getUniqueId());
            if (player.getRank().getRankId() < Rank.KNIGHT.getRankId()) {
                return;
            }
        }
        ParkManager parkManager = ParkManager.getInstance();
        switch (args[0]) {
            case "effect":
                if (args.length == 1) {
                    helpMenu("effect", sender);
                    return;
                }
                if (args[1].equalsIgnoreCase("particle")) {
                    if (args.length == 11) {
                        Particle effect = getParticle(args[2]);
                        Location loc = locFromArray(args[3], args[4], args[5]);
                        try {
                            double offsetX = Float.parseFloat(args[6]);
                            double offsetY = Float.parseFloat(args[7]);
                            double offsetZ = Float.parseFloat(args[8]);
                            float speed = Float.parseFloat(args[9]);
                            int amount = getInt(args[10]);
                            for (CPlayer tp : Core.getPlayerManager().getOnlinePlayers()) {
                                if (tp.getLocation().distance(loc) > 50) {
                                    continue;
                                }
                                PlayerData data = parkManager.getPlayerData(tp.getUniqueId());
                                if (!data.isFlash()) {
                                    continue;
                                }
                                tp.getParticles().send(loc, effect, amount, (float) offsetX, (float) offsetY,
                                        (float) offsetZ, speed);
                            }
                            return;
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
                            byte d = block.getData();
                            final List<UUID> uuids = new ArrayList<>();
                            for (Player tp : Bukkit.getOnlinePlayers()) {
                                if (tp.getLocation().distance(loc) > 50) {
                                    continue;
                                }
                                PlayerData data = parkManager.getPlayerData(tp.getUniqueId());
                                if (!data.isFlash()) {
                                    continue;
                                }
                                uuids.add(tp.getUniqueId());
                            }
//                            PacketContainer packetFirst = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_CHANGE);
//                            StructureModifier<WrappedBlockData> mFirst = packetFirst.getBlockData();
//                            com.comphenix.protocol.wrappers.BlockPosition posFirst = new com.comphenix.protocol.wrappers.BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
//                            packetFirst.getBlockPositionModifier().write(0, posFirst);
//                            mFirst.getValues().get(0).setTypeAndData(Material.REDSTONE_LAMP_OFF, 0);
//                            PacketContainer packetSecond = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_CHANGE);
//                            StructureModifier<WrappedBlockData> mSecond = packetSecond.getBlockData();
//                            com.comphenix.protocol.wrappers.BlockPosition posSecond = new com.comphenix.protocol.wrappers.BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
//                            packetSecond.getBlockPositionModifier().write(0, posSecond);
//                            mSecond.getValues().get(0).setTypeAndData(type, d);
//                            ProtocolManager pm = ProtocolLibrary.getProtocolManager();
                            for (UUID uuid : uuids) {
                                Player tp = Bukkit.getPlayer(uuid);
                                if (tp == null) {
                                    continue;
                                }
                                tp.sendBlockChange(loc, Material.REDSTONE_LAMP_ON, (byte) 0);
//                                try {
//                                    pm.sendServerPacket(tp, packetFirst);
//                                } catch (InvocationTargetException e) {
//                                    e.printStackTrace();
//                                }
                            }
                            Bukkit.getScheduler().runTaskLater(parkManager, () -> {
                                for (UUID uuid : uuids) {
                                    Player tp = Bukkit.getPlayer(uuid);
                                    if (tp == null) {
                                        continue;
                                    }
                                    tp.sendBlockChange(loc, type, d);
//                                    try {
//                                        pm.sendServerPacket(tp, packetSecond);
//                                    } catch (InvocationTargetException e) {
//                                        e.printStackTrace();
//                                    }
                                }
                            }, 6L);
                            return;
                        } catch (NumberFormatException ignored) {
                            sender.sendMessage(ChatColor.RED + "There was an error with numbers!");
                            return;
                        }
                    }
                }
                helpMenu("effect", sender);
                return;
            case "rc":
                if (args.length > 3 && args[1].equalsIgnoreCase("add")) {
                    final CPlayer tp = Core.getPlayerManager().getPlayer(Bukkit.getPlayer(args[2]));
                    if (tp == null) {
                        sender.sendMessage(ChatColor.RED + "Player not found!");
                        return;
                    }
                    if (!tp.getBukkitPlayer().isInsideVehicle()) {
                        sender.sendMessage(ChatColor.RED + "This player is not in a vehicle!");
                        return;
                    }
                    StringBuilder rideName = new StringBuilder();
                    for (int i = 3; i < args.length; i++) {
                        rideName.append(args[i]);
                        if ((i - 2) < (args.length)) {
                            rideName.append(" ");
                        }
                    }
                    final String finalRideName = rideName.toString();
                    Bukkit.getScheduler().runTaskAsynchronously(parkManager, () -> {
                        PlayerData data = parkManager.getPlayerData(tp.getUniqueId());
                        TreeMap<String, RideCount> rides = data.getRideCounts();
                        try (Connection connection = Core.getSqlUtil().getConnection()) {
                            PreparedStatement sql = connection.prepareStatement("INSERT INTO ride_counter (uuid, name, server, time) VALUES (?,?,?,?)");
                            sql.setString(1, tp.getUniqueId().toString());
                            sql.setString(2, finalRideName);
                            sql.setString(3, Core.getInstanceName());
                            sql.setInt(4, (int) (System.currentTimeMillis() / 1000));
                            sql.execute();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        if (rides.containsKey(finalRideName)) {
                            rides.get(finalRideName).addCount(1);
                        } else {
                            rides.put(finalRideName, new RideCount(finalRideName, Core.getInstanceName()));
                        }
                        if (rides.size() >= 30) {
                            tp.giveAchievement(15);
                        } else if (rides.size() >= 20) {
                            tp.giveAchievement(14);
                        } else if (rides.size() >= 10) {
                            tp.giveAchievement(13);
                        } else if (rides.size() >= 1) {
                            tp.giveAchievement(12);
                        }
                        data.setRideCounts(rides);
                        sender.sendMessage(ChatColor.GREEN + "Added 1 to " + tp.getName() + "'s counter for " +
                                finalRideName);
                        tp.sendMessage(ChatColor.GREEN + "--------------" + ChatColor.GOLD + "" + ChatColor.BOLD +
                                "Ride Counter" + ChatColor.GREEN + "-------------\n" + ChatColor.YELLOW +
                                "Ride Counter for " + ChatColor.AQUA + finalRideName + ChatColor.YELLOW +
                                "is now at " + ChatColor.AQUA + data.getRideCounts().get(finalRideName).getCount() +
                                ChatColor.GREEN + "\n----------------------------------------");
                        tp.playSound(tp.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 100f, 0.75f);
                    });
                    return;
                }
                helpMenu("ride", sender);
                return;
            case "sge":
                if (!parkManager.isResort(Resort.WDW)) {
                    return;
                }
                switch (args.length) {
                    case 2:
                        if (args[1].equalsIgnoreCase("lock")) {
                            parkManager.getStitch().toggleLock(sender);
                        } else if (args[1].equalsIgnoreCase("eject")) {
                            parkManager.getStitch().ejectAll(sender);
                        } else if (args[1].equalsIgnoreCase("add")) {
                            if (!(sender instanceof Player)) {
                                return;
                            }
                            Player player = (Player) sender;
                            try {
                                parkManager.getStitch().addSeat(player);
                            } catch (IOException e) {
                                player.sendMessage(ChatColor.RED + "There was an error doing this command!");
                                e.printStackTrace();
                            }
                        }
                        return;
                    case 3:
                        if (args[1].equalsIgnoreCase("join")) {
                            Player tp = Bukkit.getPlayer(args[2]);
                            if (tp == null) {
                                sender.sendMessage(ChatColor.RED + "Player not found!");
                                return;
                            }
                            parkManager.getStitch().joinShow(tp);
                        }
                        return;
                    case 4:
                        if (args[1].equalsIgnoreCase("effect")) {
                            if (args[2].equalsIgnoreCase("burp")) {
                                Location loc = WorldUtil.strToLoc(Bukkit.getWorlds().get(0).getName() + "," + args[3]);
                                for (CPlayer tp : Core.getPlayerManager().getOnlinePlayers()) {
                                    tp.getParticles().send(loc, Particle.VILLAGER_HAPPY, 150, (float) 3,
                                            (float) 3, (float) 3, 0);
                                }
                                return;
                            }
                            if (args[2].equalsIgnoreCase("spit")) {
                                Location loc = WorldUtil.strToLoc(Bukkit.getWorlds().get(0).getName() + "," + args[3]);
                                for (CPlayer tp : Core.getPlayerManager().getOnlinePlayers()) {
                                    tp.getParticles().send(loc, Particle.LAVA, 150, (float) 3, (float) 3,
                                            (float) 3, 0);
                                }
                                return;
                            }
                        }
                        return;
                    default:
                        helpMenu("sge", sender);
                        return;
                }
            case "shooter":
                if (parkManager.getShooter() == null) {
                    sender.sendMessage(ChatColor.RED + "Shooter is Disabled!");
                    return;
                }
                switch (args.length) {
                    case 3:
                        if (args[1].equalsIgnoreCase("add")) {
                            Player player = Bukkit.getPlayer(args[2]);
                            if (player == null) {
                                sender.sendMessage(ChatColor.RED + "Could not find Player!");
                                return;
                            }
                            PlayerInventory inv = player.getInventory();
                            player.setMetadata("shooter", new FixedMetadataValue(parkManager, 0));
                            inv.setItem(4, parkManager.getShooter().getItem());
                            inv.setHeldItemSlot(4);
                            parkManager.getShooter().sendGameMessage(player);
                            parkManager.getShooter().join(player);
                            return;
                        }
                        if (args[1].equalsIgnoreCase("remove")) {
                            Player player = Bukkit.getPlayer(args[2]);
                            if (player == null) {
                                sender.sendMessage(ChatColor.RED + "Could not find Player!");
                                return;
                            }
                            PlayerInventory inv = player.getInventory();
                            if (!inv.contains(parkManager.getShooter().getItem())) {
                                return;
                            }
                            parkManager.getShooter().done(player);
                            return;
                        }
                }
                helpMenu("shooter", sender);
                break;
            case "outfit":
                if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("list")) {
                        List<Outfit> outfits = parkManager.getWardrobeManager().getOutfits();
                        sender.sendMessage(ChatColor.GREEN + "Current Outfits:");
                        for (Outfit o : outfits) {
                            sender.sendMessage(ChatColor.AQUA + "- " + ChatColor.GREEN + "ID: " + o.getId() + " Name: "
                                    + o.getName());
                        }
                        break;
                    }
                } else if (args.length == 3) {
                    if (isInt(args[2])) {
                        if (args[1].equalsIgnoreCase("remove")) {
                            final Integer id = Integer.parseInt(args[2]);
                            Outfit o = parkManager.getWardrobeManager().getOutfit(id);
                            if (o == null) {
                                sender.sendMessage(ChatColor.RED + "No outfit found by that ID!");
                                break;
                            }
                            sender.sendMessage(ChatColor.GREEN + "Removed the Outfit with the ID " + id + "!");
                            Bukkit.getScheduler().runTaskAsynchronously(parkManager, () -> parkManager.getWardrobeManager().removeOutfit(id));
                            break;
                        } else if (args[1].equalsIgnoreCase("info")) {
                            Integer id = Integer.parseInt(args[2]);
                            Outfit o = parkManager.getWardrobeManager().getOutfit(id);
                            if (o == null) {
                                sender.sendMessage(ChatColor.RED + "No outfit found by that ID!");
                                break;
                            }
                            break;
                        }
                    }
                }
                if (args.length >= 3) {
                    if (args[1].equalsIgnoreCase("create")) {
                        if (!(sender instanceof Player)) {
                            return;
                        }
                        final Player player = (Player) sender;
                        player.sendMessage(ChatColor.GREEN + "Preparing outfit...");
                        StringBuilder name = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            name.append(args[i]);
                            if (i < (args.length - 1)) {
                                name.append(" ");
                            }
                        }
                        PlayerInventory inv = player.getInventory();
                        ItemStack h = inv.getHelmet();
                        ItemStack c = inv.getChestplate();
                        ItemStack p = inv.getLeggings();
                        ItemStack b = inv.getBoots();
                        String htag = new NbtTextSerializer().serialize(NbtFactory.fromItemTag(h));
                        String ctag = new NbtTextSerializer().serialize(NbtFactory.fromItemTag(c));
                        String ptag = new NbtTextSerializer().serialize(NbtFactory.fromItemTag(p));
                        String btag = new NbtTextSerializer().serialize(NbtFactory.fromItemTag(b));
                        String finalName = name.toString();
                        final int resort;
                        switch (ParkManager.getInstance().getResort()) {
                            case WDW:
                                resort = 0;
                                break;
                            case DLR:
                                resort = 1;
                                break;
                            case USO:
                                resort = 2;
                                break;
                            default:
                                resort = 0;
                                break;
                        }
                        Bukkit.getScheduler().runTaskAsynchronously(parkManager, () -> {
                            try (Connection conneciton = Core.getSqlUtil().getConnection()) {
                                PreparedStatement sql = conneciton.prepareStatement("INSERT INTO outfits (id,name," +
                                        "hid,hdata,head,cid,cdata,chestplate,lid,ldata,leggings,bid,bdata,boots,resort) " +
                                        "VALUES (0,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                                sql.setString(1, finalName);
                                sql.setInt(2, h.getTypeId());
                                sql.setInt(3, h.getData().getData());
                                sql.setString(4, htag == null ? "" : htag);
                                sql.setInt(5, c.getTypeId());
                                sql.setInt(6, c.getData().getData());
                                sql.setString(7, ctag == null ? "" : ctag);
                                sql.setInt(8, p.getTypeId());
                                sql.setInt(9, p.getData().getData());
                                sql.setString(10, ptag == null ? "" : ptag);
                                sql.setInt(11, b.getTypeId());
                                sql.setInt(12, b.getData().getData());
                                sql.setString(13, btag == null ? "" : btag);
                                sql.setInt(14, resort);
                                sql.execute();
                                sql.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            parkManager.getWardrobeManager().initialize();
                            player.sendMessage(ChatColor.GREEN + "Created outfit!");
                        });
                        break;
                    }
                }
                helpMenu("outfit", sender);
                break;
            case "changer":
                switch (args.length) {
                    case 2:
                        switch (args[1]) {
                            case "list":
                                List<String> list = parkManager.getBlockChanger().changerList();
                                if (list.isEmpty()) {
                                    sender.sendMessage(ChatColor.RED + "No Changers on this server!");
                                    return;
                                }
                                sender.sendMessage(ChatColor.GREEN + "Changer List:");
                                for (String s : list) {
                                    sender.sendMessage(ChatColor.AQUA + s);
                                }
                                return;
                            case "debug":
                                if (sender instanceof Player) {
                                    if (parkManager.getBlockChanger().toggleDebug(((Player) sender))) {
                                        sender.sendMessage(ChatColor.YELLOW + "You're no longer in Changer Debugging mode!");
                                        return;
                                    } else {
                                        sender.sendMessage(ChatColor.YELLOW + "You're now in Changer Debugging mode!");
                                        return;
                                    }
                                }
                                return;
                            case "wand":
                                if (!(sender instanceof Player)) {
                                    return;
                                }
                                ItemStack wand = ItemUtil.create(Material.DIAMOND_AXE, ChatColor.LIGHT_PURPLE +
                                        "Changer Wand", Collections.singletonList(""));
                                ((Player) sender).getInventory().addItem(wand);
                                sender.sendMessage(ChatColor.LIGHT_PURPLE + "There's your wand!");
                                sender.sendMessage(ChatColor.RED + "REMEMBER: NorthWest bottom to SouthEast top");
                                sender.sendMessage(ChatColor.RED + "If done any other way, it won't work!");
                                return;
                        }
                    case 3:
                        switch (args[1]) {
                            case "debug":
                                Player tp = Bukkit.getPlayer(args[2]);
                                if (tp == null) {
                                    sender.sendMessage(ChatColor.RED + "Player not found!");
                                    return;
                                }
                                Rank rank = Core.getPlayerManager().getPlayer(tp.getUniqueId()).getRank();
                                if (rank.getRankId() < Rank.KNIGHT.getRankId()) {
                                    sender.sendMessage(ChatColor.RED + "Could not toggle for user " + tp.getName() +
                                            ", that user is not at least the " + Rank.KNIGHT.getFormattedName()
                                            + ChatColor.RED + " Rank!");
                                    return;
                                }
                                if (parkManager.getBlockChanger().toggleDebug(tp)) {
                                    sender.sendMessage(ChatColor.YELLOW + "Removed " + tp.getName() + " from Changer Debugging!");
                                    tp.sendMessage(ChatColor.YELLOW + "You're no longer in Changer Debugging mode!");
                                    return;
                                } else {
                                    sender.sendMessage(ChatColor.YELLOW + "Added " + tp.getName() + " to Changer Debugging!");
                                    tp.sendMessage(ChatColor.YELLOW + "You're now in Changer Debugging mode!");
                                    return;
                                }
                            case "remove":
                                final Changer changer = parkManager.getBlockChanger().getChanger(args[2]);
                                if (changer == null) {
                                    sender.sendMessage(ChatColor.RED + "Changer not found by the name of " +
                                            ChatColor.GREEN + args[2]);
                                    return;
                                }
                                final List<Changer> l = parkManager.getBlockChanger().getChangers();
                                Bukkit.getScheduler().runTaskAsynchronously(parkManager, () -> {
                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        l.stream().filter(c -> c.getFirstLocation().distance(p.getLocation()) < 75)
                                                .forEach(c -> c.sendReverse(p));
                                    }
                                    parkManager.getBlockChanger().removeChanger(changer.getName());
                                    sender.sendMessage(ChatColor.GREEN + "Removed changer " + ChatColor.AQUA + args[2]);
                                });
                                return;
                            case "info":
                                Changer chngr = parkManager.getBlockChanger().getChanger(args[2]);
                                if (chngr == null) {
                                    sender.sendMessage(ChatColor.RED + "Changer not found by the name of " +
                                            ChatColor.GREEN + args[2]);
                                    return;
                                }
                                Location l1 = chngr.getFirstLocation();
                                Location l2 = chngr.getSecondLocation();
                                List<Material> mats = chngr.getFrom();
                                StringBuilder f = new StringBuilder();
                                for (int i = 0; i < mats.size(); i++) {
                                    Material m = mats.get(i);
                                    f.append(m.name());
                                    if (i < (mats.size() - 1)) {
                                        f.append(", ");
                                    }
                                }
                                String[] list = new String[]{ChatColor.AQUA + "Info for " + chngr.getName() + " Changer: ",
                                        "Location 1: " + l1.getBlockX() + "," + l1.getBlockY() + "," + l1.getBlockZ(),
                                        "Location 2: " + l2.getBlockX() + "," + l2.getBlockY() + "," + l2.getBlockZ(),
                                        "From: " + f, "To: " + chngr.getTo(), "Sender: " + chngr.getSender()};
                                for (String s : list) {
                                    sender.sendMessage(ChatColor.GREEN + s);
                                }
                                return;
                        }
                    case 6:
                        if (args[1].equalsIgnoreCase("create")) {
                            try {
                                String name = args[2];
                                if (parkManager.getBlockChanger().getChanger(name) != null) {
                                    sender.sendMessage(ChatColor.RED + "A changer with that name already exists!");
                                    return;
                                }
                                HashMap<Material, Byte> from = parkManager.getBlockChanger().blocksFromString(args[3]);
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
                                Location loc1 = parkManager.getBlockChanger().getSelection(0, ((Player) sender));
                                //loc1 = getLocation(((Player) sender).getWorld(), args[6]);
                                Location loc2 = parkManager.getBlockChanger().getSelection(1, ((Player) sender));
                                //loc2 = getLocation(((Player) sender).getWorld(), args[7]);
                                if (loc1 == null || loc2 == null) {
                                    sender.sendMessage(ChatColor.RED + "You don't have a full selection selected!");
                                    return;
                                }
                                Changer changer = new Changer(name, loc1, loc2, from, Material.getMaterial(to), data,
                                        Material.getMaterial(send));
                                parkManager.getBlockChanger().addChanger(changer);
                                sender.sendMessage(ChatColor.GREEN + "Created Changer " + ChatColor.AQUA + name);
                                parkManager.getBlockChanger().clearSelection(((Player) sender).getUniqueId());
                                return;
                            } catch (Exception e) {
                                sender.sendMessage(ChatColor.RED + "There was an error creating that Changer!");
                                e.printStackTrace();
                                return;
                            }
                        }
                    default:
                        helpMenu("changer", sender);
                        return;
                }
            case "schedule":
                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("reload")) {
                        sender.sendMessage(ChatColor.BLUE + "Reloading Show Schedule...");
                        Bukkit.getScheduler().runTaskAsynchronously(parkManager, () -> {
                            parkManager.getScheduleManager().update();
                            sender.sendMessage(ChatColor.BLUE + "Show Schedule reloaded!");
                        });
                        return;
                    }
                }
                helpMenu("schedule", sender);
                return;
            case "show":
                StringBuilder cmd = new StringBuilder();
                for (String a : args) {
                    cmd.append(a).append(" ");
                }
                Bukkit.dispatchCommand(sender, cmd.toString());
                return;
            case "hotel":
                sender.sendMessage(ChatColor.BLUE + "Reloading hotel rooms...");
                parkManager.getHotelManager().refreshRooms();
                sender.sendMessage(ChatColor.BLUE + "Hotel rooms reloaded!");
                return;
            case "queue":
                if (args.length == 3) {
                    AbstractQueueRide ride = parkManager.getQueueManager().getRide(args[1]);
                    if (ride == null) {
                        sender.sendMessage(ChatColor.RED + "Ride not found!");
                        return;
                    }
                    if (args[2].equalsIgnoreCase("info")) {
                        String wait = "Wait Time: " + ride.approximateWaitTime();
                        sender.sendMessage(ChatColor.GREEN + ride.getName() + ChatColor.YELLOW + "\n" + (ride.getQueueSize()
                                <= 0 ? "Wait Time: No Wait" : wait) + "\nIn Queue: " + ride.getQueueSize() +
                                "\nRiders per Group: " + ride.getAmount() + "\nDelay between rides: " +
                                ride.getDelay());
                        return;
                    }
                    if (args[2].equalsIgnoreCase("eject")) {
                        ride.ejectQueue();
                        sender.sendMessage(ride.getName() + ChatColor.GREEN + "'s Queue has been ejected!");
                        return;
                    }
                    if (args[2].equalsIgnoreCase("fp")) {
                        ride.toggleFastpass(sender);
//                        if (ride.toggleFastpass()) {
//                            sender.sendMessage(ChatColor.GREEN + "The FastPass line has been closed!");
//                        } else {
//                            sender.sendMessage(ChatColor.GREEN + "The FastPass line has been opened!");
//                        }
                        return;
                    }
                    if (args[2].equalsIgnoreCase("pause")) {
                        ride.setPaused(true);
                        sender.sendMessage(ChatColor.GREEN + "Paused!");
                        return;
                    }
                    if (args[2].equalsIgnoreCase("unpause")) {
                        ride.setPaused(false);
                        sender.sendMessage(ChatColor.GREEN + "Un-Paused!");
                        return;
                    }
                    if (args[2].equalsIgnoreCase("freeze")) {
                        if (ride.toggleFreeze()) {
                            sender.sendMessage(ChatColor.GREEN + "Queue frozen!");
                        } else {
                            sender.sendMessage(ChatColor.GREEN + "Queue unfrozen!");
                        }
                        return;
                    }
                    if (args[2].equalsIgnoreCase("list")) {
                        List<UUID> users = ride.getQueue();
                        StringBuilder s = new StringBuilder(ChatColor.GREEN + "Currently in Queue for " + ride.getName() + ChatColor.GREEN +
                                ": (" + users.size() + ")\n" + ChatColor.YELLOW);
                        for (int i = 0; i < users.size(); i++) {
                            Player tp = Bukkit.getPlayer(users.get(i));
                            if (tp == null) {
                                continue;
                            }
                            if (i == users.size() - 1) {
                                s.append(tp.getName());
                            } else {
                                s.append(tp.getName()).append(", ");
                            }
                        }
                        if (users.size() == 0) {
                            s.append("None");
                        }
                        sender.sendMessage(s.toString());
                        return;
                    }
                    helpMenu("queue", sender);
                    return;
                }
                if (args.length == 4) {
                    AbstractQueueRide ride = parkManager.getQueueManager().getRide(args[1]);
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
                    return;
                }
                helpMenu("queue", sender);
                return;
            case "iasw": {
                if (args.length == 6) {
                    if (args[1].equalsIgnoreCase("sign")) {
                        try {
                            Integer x = Integer.parseInt(args[2]);
                            Integer y = Integer.parseInt(args[3]);
                            Integer z = Integer.parseInt(args[4]);
                            Block b = Bukkit.getWorlds().get(0).getBlockAt(x, y, z);
                            if (!b.getType().name().toLowerCase().contains("sign")) {
                                sender.sendMessage(ChatColor.RED + "Please place a sign at the desired location!");
                                return;
                            }
                            Sign s = (Sign) b.getState();
                            Player player = Bukkit.getPlayer(args[5]);
                            if (player == null) {
                                s.setLine(0, ChatColor.BLUE + "Goodbye!");
                                s.setLine(1, "");
                                s.setLine(2, "");
                                s.setLine(3, "");
                                s.update();
                            } else {
                                s.setLine(0, ChatColor.BLUE + "Goodbye");
                                s.setLine(1, "");
                                s.setLine(2, ChatColor.LIGHT_PURPLE + player.getName());
                                s.setLine(3, "");
                                s.update();
                            }
                            return;
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "Error formatting numbers!");
                            return;
                        }
                    }
                    helpMenu("iasw", sender);
                    return;
                }
                helpMenu("iasw", sender);
                return;
            }
            case "fp": {
                if (!parkManager.isResort(Resort.WDW) && !parkManager.isResort(Resort.DLR)) {
                    return;
                }
                if (args.length < 0) {
                    helpMenu("fp", sender);
                    return;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "You must be a Player to do this!");
                    return;
                }
                CPlayer player = Core.getPlayerManager().getPlayer(((Player) sender).getUniqueId());
                if (player == null) {
                    return;
                }
                if (args[1].equalsIgnoreCase("create")) {
                    parkManager.getFpKioskManager().create(player);
                    return;
                }
                helpMenu("fp", sender);
                return;
            }
            case "tot": {
                if (!parkManager.isResort(Resort.WDW) && !parkManager.isResort(Resort.DLR)) {
                    return;
                }
                if (!(sender instanceof Player || sender instanceof BlockCommandSender)) {
                    sender.sendMessage(ChatColor.RED + "Only Players and Command Blocks can do this!");
                    return;
                }
                if (args.length != 3) {
                    helpMenu("tot", sender);
                    return;
                }
                if (args[1].equalsIgnoreCase("reset")) {
                    String tname = args[2];
                    DropTower tower = DropTower.fromString(tname);
                    if (tower == null) {
                        helpMenu("tot", sender);
                        return;
                    }
                    tower.resetCount();
                    return;
                }
                if (args[1].equalsIgnoreCase("randomize")) {
                    String tname = args[2];
                    DropTower tower = DropTower.fromString(tname);
                    if (tower == null) {
                        helpMenu("tot", sender);
                        return;
                    }
                    TowerLayout layout = tower.randomizeLayout();
                    int high = layout.getHigh();
                    int low = layout.getLow();
                    World world = sender instanceof Player ? ((Player) sender).getWorld() :
                            (sender instanceof BlockCommandSender ? ((BlockCommandSender) sender).getBlock().getWorld() : null);
                    if (world == null) {
                        sender.sendMessage(ChatColor.RED + "Are you sure you're a Player or a Command Block?");
                        return;
                    }
                    int x1 = 0;
                    int z1 = 0;
                    int x2 = 0;
                    int z2 = 0;
                    switch (tower) {
                        case ECHO:
                            x1 = -188;
                            z1 = -122;
                            x2 = -189;
                            z2 = -123;
                            break;
                        case FOXTROT:
                            x1 = -178;
                            z1 = -112;
                            x2 = -179;
                            z2 = -113;
                            break;
                    }
                    DecimalFormat df = new DecimalFormat("#.##");
                    double topWait = (0.25 * new Random().nextDouble()) + 0.25;
                    double bottomWait = (0.25 * new Random().nextDouble()) + 0.25;
                    topWait = Double.parseDouble(df.format(topWait));
                    bottomWait = Double.parseDouble(df.format(bottomWait));
                    Block high1 = world.getBlockAt(x1, high, z1);
                    Block low1 = world.getBlockAt(x1, low, z1);
                    Block high2 = world.getBlockAt(x2, high, z2);
                    Block low2 = world.getBlockAt(x2, low, z2);
                    for (int i = 68; i <= 79; i++) {
                        world.getBlockAt(x1, i, z1).setType(Material.AIR);
                        world.getBlockAt(x2, i, z2).setType(Material.AIR);
                    }
                    for (int i = 91; i <= 101; i++) {
                        world.getBlockAt(x1, i, z1).setType(Material.AIR);
                        world.getBlockAt(x2, i, z2).setType(Material.AIR);
                    }
                    int count = tower.getCount();
                    if (count == 2 || count > 10) {
                        return;
                    }
                    high1.setType(Material.WALL_SIGN);
                    high1.setData((byte) 2);
                    low1.setType(Material.WALL_SIGN);
                    low1.setData((byte) 2);
                    high2.setType(Material.WALL_SIGN);
                    high2.setData((byte) 5);
                    low2.setType(Material.WALL_SIGN);
                    low2.setData((byte) 5);
                    Sign high1Sign = (Sign) high1.getState();
                    Sign low1Sign = (Sign) low1.getState();
                    Sign high2Sign = (Sign) high2.getState();
                    Sign low2Sign = (Sign) low2.getState();
                    high1Sign.setLine(0, "[+train]");
                    high1Sign.setLine(1, "station");
                    high1Sign.setLine(2, topWait + "");
                    high1Sign.setLine(3, "backward");
                    high2Sign.setLine(0, "[+train]");
                    high2Sign.setLine(1, "station");
                    high2Sign.setLine(2, topWait + "");
                    high2Sign.setLine(3, "backward");
                    low1Sign.setLine(0, "[+train]");
                    low1Sign.setLine(1, "station 6");
                    low1Sign.setLine(2, bottomWait + "");
                    low1Sign.setLine(3, "backward");
                    low2Sign.setLine(0, "[+train]");
                    low2Sign.setLine(1, "station 6");
                    low2Sign.setLine(2, bottomWait + "");
                    low2Sign.setLine(3, "backward");
                    high1Sign.update();
                    high2Sign.update();
                    low1Sign.update();
                    low2Sign.update();
                    return;
                }
                helpMenu("tot", sender);
                return;
            }
            case "tsm": {
                if (parkManager.getToyStoryMania() == null) {
                    sender.sendMessage(ChatColor.RED + "Shooter is Disabled!");
                    return;
                }
                switch (args.length) {
                    case 3: {
                        if (args[1].equalsIgnoreCase("add")) {
                            Player player = Bukkit.getPlayer(args[2]);
                            if (player == null) {
                                sender.sendMessage(ChatColor.RED + "Could not find Player!");
                                return;
                            }
                            if (BlockEdit.isInBuildMode(player.getUniqueId())) {
                                player.performCommand("build");
                                Bukkit.getScheduler().runTaskLater(parkManager, () -> parkManager.getToyStoryMania().join(player), 20L);
                                return;
                            }
                            parkManager.getToyStoryMania().join(player);
                            return;
                        }
                        if (args[1].equalsIgnoreCase("remove")) {
                            Player player = Bukkit.getPlayer(args[2]);
                            if (player == null) {
                                sender.sendMessage(ChatColor.RED + "Could not find Player!");
                                return;
                            }
                            PlayerInventory inv = player.getInventory();
                            if (!parkManager.getToyStoryMania().isInGame(player)) {
                                return;
                            }
                            parkManager.getToyStoryMania().done(player);
                            return;
                        }
                        helpMenu("tsm", sender);
                        break;
                    }
                    case 4: {
                        if (args[1].equalsIgnoreCase("randomize")) {
                            Location loc1 = WorldUtil.strToLoc("dhs," + args[2]);
                            Location loc2 = WorldUtil.strToLoc("dhs," + args[3]);
                            if (loc2.getBlockX() < loc1.getBlockX()) {
                                final int x = loc1.getBlockX();
                                loc1.setX(loc2.getBlockX());
                                loc2.setX(x);
                            }
                            if (loc2.getBlockY() < loc1.getBlockY()) {
                                final int y = loc1.getBlockY();
                                loc1.setY(loc2.getBlockY());
                                loc2.setY(y);
                            }
                            if (loc2.getBlockZ() < loc1.getBlockZ()) {
                                final int z = loc1.getBlockZ();
                                loc1.setZ(loc2.getBlockZ());
                                loc2.setZ(z);
                            }
                            parkManager.getToyStoryMania().randomize(loc1, loc2);
                            return;
                        }
                        if (args[1].equalsIgnoreCase("reset")) {
                            Location loc1 = WorldUtil.strToLoc("dhs," + args[2]);
                            Location loc2 = WorldUtil.strToLoc("dhs," + args[3]);
                            if (loc2.getBlockX() < loc1.getBlockX()) {
                                final int x = loc1.getBlockX();
                                loc1.setX(loc2.getBlockX());
                                loc2.setX(x);
                            }
                            if (loc2.getBlockY() < loc1.getBlockY()) {
                                final int y = loc1.getBlockY();
                                loc1.setY(loc2.getBlockY());
                                loc2.setY(y);
                            }
                            if (loc2.getBlockZ() < loc1.getBlockZ()) {
                                final int z = loc1.getBlockZ();
                                loc1.setZ(loc2.getBlockZ());
                                loc2.setZ(z);
                            }
                            parkManager.getToyStoryMania().reset(loc1, loc2);
                            return;
                        }
                        helpMenu("tsm", sender);
                        break;
                    }
                    case 5: {
                        if (args[1].equalsIgnoreCase("map")) {
                            Location loc1 = WorldUtil.strToLoc("dhs," + args[2]);
                            Location loc2 = WorldUtil.strToLoc("dhs," + args[3]);
                            if (loc2.getBlockX() < loc1.getBlockX()) {
                                final int x = loc1.getBlockX();
                                loc1.setX(loc2.getBlockX());
                                loc2.setX(x);
                            }
                            if (loc2.getBlockY() < loc1.getBlockY()) {
                                final int y = loc1.getBlockY();
                                loc1.setY(loc2.getBlockY());
                                loc2.setY(y);
                            }
                            if (loc2.getBlockZ() < loc1.getBlockZ()) {
                                final int z = loc1.getBlockZ();
                                loc1.setZ(loc2.getBlockZ());
                                loc2.setZ(z);
                            }
                            Location chest = WorldUtil.strToLoc("dhs," + args[4]);
                            if (!chest.getBlock().getType().equals(Material.CHEST)) {
                                sender.sendMessage(ChatColor.RED + "No chest at " + chest.getBlockX() + "," +
                                        chest.getBlockY() + "," + chest.getBlockZ() + ",");
                                return;
                            }
                            parkManager.getToyStoryMania().setMap(loc1, loc2, (Chest) chest.getBlock().getState());
                        }
                        helpMenu("tsm", sender);
                        break;
                    }
                }
                helpMenu("tsm", sender);
                return;
            }
            case "reload":
                sender.sendMessage(ChatColor.BLUE + "Reloading Plugin...");
                parkManager.setupFoodLocations();
                parkManager.setupRides();
                parkManager.getStitch().initialize();
                parkManager.getScheduleManager().update();
                parkManager.getHotelManager().refreshRooms();
                /*
                try {
                    parkManager.getBlockChanger().reload();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }*/
                parkManager.getPackManager().initialize();
                parkManager.getShopManager().initialize();
                parkManager.getWardrobeManager().initialize();
                sender.sendMessage(ChatColor.BLUE + "Plugin Reloaded!");
                return;
            default:
                helpMenu("main", sender);
        }
    }

    private Location locFromArray(String... args) throws NumberFormatException {
        if (args.length != 3) {
            return null;
        }
        return new Location(Bukkit.getWorlds().get(0), Double.parseDouble(args[0]), Double.parseDouble(args[1]),
                Double.parseDouble(args[2]));
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

    private Particle getParticle(String s) {
        switch (s.toLowerCase()) {
            case "barrier":
                return Particle.BARRIER;
            case "bubble":
                return Particle.WATER_BUBBLE;
            case "cloud":
                return Particle.CLOUD;
            case "crit":
                return Particle.CRIT;
            case "depthsuspend":
                return Particle.SUSPENDED_DEPTH;
            case "dragonbreath":
                return Particle.DRAGON_BREATH;
            case "driplava":
                return Particle.DRIP_LAVA;
            case "dripwater":
                return Particle.DRIP_WATER;
            case "enchantmenttable":
                return Particle.ENCHANTMENT_TABLE;
            case "explode":
                return Particle.EXPLOSION_NORMAL;
            case "fireworksspark":
                return Particle.FIREWORKS_SPARK;
            case "flame":
                return Particle.FLAME;
            case "footstep":
                return Particle.FOOTSTEP;
            case "happyvillager":
                return Particle.VILLAGER_HAPPY;
            case "heart":
                return Particle.HEART;
            case "hugeexplosion":
                return Particle.EXPLOSION_HUGE;
            case "instantspell":
                return Particle.SPELL_INSTANT;
            case "largeexplode":
                return Particle.EXPLOSION_LARGE;
            case "largesmoke":
                return Particle.SMOKE_LARGE;
            case "lava":
                return Particle.LAVA;
            case "magiccrit":
                return Particle.CRIT_MAGIC;
            case "mobspell":
                return Particle.SPELL_MOB;
            case "mobspellambient":
                return Particle.SPELL_MOB_AMBIENT;
            case "note":
                return Particle.NOTE;
            case "portal":
                return Particle.PORTAL;
            case "reddust":
                return Particle.REDSTONE;
            case "slime":
                return Particle.SLIME;
            case "smoke":
                return Particle.SMOKE_NORMAL;
            case "snowballpoof":
                return Particle.SNOWBALL;
            case "snowshovel":
                return Particle.SNOW_SHOVEL;
            case "spell":
                return Particle.SPELL;
            case "spit":
                return Particle.SPIT;
            case "splash":
                return Particle.WATER_SPLASH;
            case "suspend":
                return Particle.SUSPENDED;
            case "totem":
                return Particle.TOTEM;
            case "townaura":
                return Particle.TOWN_AURA;
            case "wake":
                return Particle.WATER_WAKE;
            case "witchmagic":
                return Particle.SPELL_WITCH;
        }
        return Particle.valueOf(s);
    }

    private static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
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
                sender.sendMessage(ChatColor.GREEN + "/magic fp " + ChatColor.AQUA + "- Manage FastPass Kiosks");
                //sender.sendMessage(ChatColor.GREEN + "/magic pin " + ChatColor.AQUA + "- Pin Trading Command");
                sender.sendMessage(ChatColor.GREEN + "/magic tot " + ChatColor.AQUA + "- Tower of Terror Randomizer");
                sender.sendMessage(ChatColor.GREEN + "/magic rc " + ChatColor.AQUA + "- Ride Counters");
                sender.sendMessage(ChatColor.GREEN + "/magic schedule " + ChatColor.AQUA + "- Show Schedule");
                sender.sendMessage(ChatColor.GREEN + "/magic outfit " + ChatColor.AQUA + "- Outfit Manager");
                sender.sendMessage(ChatColor.GREEN + "/magic iasw " + ChatColor.AQUA + "- IASW Manager");
                sender.sendMessage(ChatColor.GREEN + "/magic tsm " + ChatColor.AQUA + "- Toy Story Mania Game");
                break;
            case "tsm":
                sender.sendMessage(ChatColor.GREEN + "Toy Story Mania Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic tsm add [Name] " + ChatColor.AQUA + "- Add player to game");
                sender.sendMessage(ChatColor.GREEN + "/magic tsm remove [Name] " + ChatColor.AQUA + "- Remove player from game");
                sender.sendMessage(ChatColor.GREEN + "/magic tsm randomize x1,y1,z1 x2,y2,z2 " + ChatColor.AQUA +
                        "- Randomize game screen in an area");
                sender.sendMessage(ChatColor.GREEN + "/magic tsm reset x1,y1,z1 x2,y2,z2 " + ChatColor.AQUA +
                        "- Set all screens in the area to black");
                sender.sendMessage(ChatColor.GREEN + "/magic tsm map x1,y1,z1 x2,y2,z2 xC,yC,zC " + ChatColor.AQUA +
                        "- Copy maps from a chest to item frames");
                break;
            case "tot":
                sender.sendMessage(ChatColor.GREEN + "Tower of Terror Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic tot randomize [Echo/Foxtrot] " + ChatColor.AQUA +
                        "- Randomize one of the towers");
                break;
            case "fp":
                sender.sendMessage(ChatColor.GREEN + "FastPass Kiosk Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic fp create " + ChatColor.AQUA +
                        "- Create a FastPass Kiosk where you're standing");
                break;
            case "pin":
                sender.sendMessage(ChatColor.GREEN + "Pin Trading Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic pin [Username] " + ChatColor.AQUA +
                        "- 10% chance to give the player a Pin");
                break;
            case "outfit":
                sender.sendMessage(ChatColor.GREEN + "Outfit Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic outfit create [Name] " + ChatColor.AQUA +
                        "- Create new outfit based on what you are currently wearing");
                sender.sendMessage(ChatColor.GREEN + "/magic outfit list " + ChatColor.AQUA + "- List all outfits");
                sender.sendMessage(ChatColor.GREEN + "/magic outfit remove [ID] " + ChatColor.AQUA +
                        "- Remove outfit based on numeric ID");
                break;
            case "shooter":
                sender.sendMessage(ChatColor.GREEN + "Shooter Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic shooter add [Name] " + ChatColor.AQUA + "- Adds player to Shooter Game");
                sender.sendMessage(ChatColor.GREEN + "/magic shooter remove [Name] " + ChatColor.AQUA + "- Removes player from Shooter Game");
                break;
            case "sge":
                sender.sendMessage(ChatColor.GREEN + "Stitch Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic sge lock " + ChatColor.AQUA + "- Locks/Unlocks Show Room");
                sender.sendMessage(ChatColor.GREEN + "/magic sge eject " + ChatColor.AQUA + "- Ejects all players from their seats");
                sender.sendMessage(ChatColor.GREEN + "/magic sge join [Player name] " + ChatColor.AQUA + "- Adds a player to the Show");
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
                break;
            case "effect":
                sender.sendMessage(ChatColor.GREEN + "Effect Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic effect particle type x y z xd yd zd speed amount");
                sender.sendMessage(ChatColor.GREEN + "/magic effect flash x y z");
                break;
            case "schedule":
                sender.sendMessage(ChatColor.GREEN + "Schedule Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic schedule reload" + ChatColor.AQUA + " - Reload Show Schedule");
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
            case "iasw":
                sender.sendMessage(ChatColor.GREEN + "IASW Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic iasw sign x y z player " + ChatColor.AQUA +
                        "- Create a Goodbye Sign at the given coordinates with a player's name on it");
                break;
            case "ride":
                sender.sendMessage(ChatColor.GREEN + "Ride Counter Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic rc add [Player] [Ride Name] " + ChatColor.AQUA +
                        "- Increment a player's Ride Count value.");
        }
    }
}