package us.mcmagic.magicassistant.commands;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.blockchanger.Changer;
import us.mcmagic.magicassistant.shooter.Shooter;
import us.mcmagic.magicassistant.show.Show;
import us.mcmagic.magicassistant.show.ticker.TickEvent;
import us.mcmagic.magicassistant.utils.HotelUtil;
import us.mcmagic.magicassistant.utils.PlayerUtil;
import us.mcmagic.magicassistant.utils.SqlUtil;
import us.mcmagic.magicassistant.utils.WorldUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.particles.ParticleUtil;
import us.mcmagic.mcmagiccore.permissions.Rank;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by Marc on 1/7/15
 */
public class Commandmagic implements Listener, CommandExecutor {
    private static HashMap<String, Show> songs = new HashMap<>();
    public static List<String> containsCommandBlockOnly = Arrays.asList("stitch");
    public ParticleEffect effect;
    public Location location;

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, final String[] args) {
        if (args.length == 0) {
            helpMenu("main", sender);
            return true;
        }
        switch (args[0]) {
            case "effect":
                if (args.length == 8) {
                } else if (args[1].contains("Particle")) {
                    // magic effect(0) Particle(1) type(2) x,y,z(3) oX(4) oY(5) oZ(6) speed(7) amount(8)
                    ParticleEffect effect = ParticleEffect.fromString(args[2]);
                    Location loc = WorldUtil.strToLoc(Bukkit.getWorlds().get(0).getName() + "," + args[3]);
                    try {
                        double offsetX = Float.parseFloat(args[4]);
                        double offsetY = Float.parseFloat(args[5]);
                        double offsetZ = Float.parseFloat(args[6]);
                        float speed = Float.parseFloat(args[7]);
                        int amount = getInt(args[8]);

                        ParticleUtil.spawnParticle(effect, loc, (float) offsetX, (float) offsetY, (float) offsetZ, speed, amount);

                    } catch (NumberFormatException ignored) {
                        sender.sendMessage(ChatColor.RED + "There was an error with numbers!");
                    }
                    helpMenu("effect", sender);
                }

            case "show":
                if (args.length == 3) {
                    switch (args[1]) {
                        case "start":
                            if (songs.containsKey(args[2])) {
                                sender.sendMessage(ChatColor.RED + "That show is already running!");
                                return true;
                            }
                            if (!songs.isEmpty()) {
                                sender.sendMessage(ChatColor.RED + "Cannot start show, perhaps there is already a show running?");
                                return true;
                            }
                            File file = new File("plugins/MagicAssistant/shows/" + args[2] + ".show");
                            if (!file.exists()) {
                                sender.sendMessage(ChatColor.RED + "----------------------------------------------");
                                sender.sendMessage(ChatColor.GOLD + args[2] + ChatColor.AQUA + " is not an existing show file.");
                                sender.sendMessage(ChatColor.RED + "----------------------------------------------");
                            } else {
                                songs.put(args[2], new Show(MagicAssistant.getInstance(), file));
                                sender.sendMessage(ChatColor.GOLD + args[2] + ChatColor.AQUA + " has started. Enjoy the show!");
                            }
                            return true;
                        case "stop":
                            if (!songs.containsKey(args[2])) {
                                sender.sendMessage(ChatColor.RED + "----------------------------------------------");
                                sender.sendMessage(
                                        ChatColor.GOLD + args[2] + ChatColor.AQUA + " is not running!");
                                sender.sendMessage(ChatColor.RED + "----------------------------------------------");
                            } else {
                                sender.sendMessage(ChatColor.GOLD + args[2] + ChatColor.AQUA + " has been stopped!");
                                songs.remove(args[2]);
                            }
                            return true;
                        default:
                            return true;
                    }
                }
                helpMenu("show", sender);
                return true;
            case "block":
                if (args.length == 9) {
                    //magic block move 35 0.5 x1,y1,z1 x2,y2,z2 addX addY addZ
                    sender.sendMessage("Working on it!");
                    try {
                        World world = Bukkit.getWorlds().get(0);
                        String action = args[1];
                        int id = getInt(args[2]);
                        float speed = getFloat(args[3]);
                        Location loc1 = getLocation(world, args[4]);
                        Location loc2 = getLocation(world, args[5]);
                        Bukkit.broadcastMessage("" + loc1 + " " + loc2);
                        double maxX = getDouble(args[6]);
                        double maxY = getDouble(args[7]);
                        double maxZ = getDouble(args[8]);
                        List<Block> blockList = getBlocks(loc1, loc2, id);
                        Vector add = new Vector(maxX, maxY, maxZ);
                        for (Block block : blockList) {
                            block.setType(Material.AIR);
                            FallingBlock fb = world.spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
                            fb.setVelocity(block.getLocation().add(maxX, maxY, maxZ).toVector());
                        }
                        sender.sendMessage("Done!");
                    } catch (NumberFormatException ignored) {
                        sender.sendMessage(ChatColor.RED + "There was an error with numbers!");
                    } catch (Exception e) {
                        sender.sendMessage(ChatColor.RED + "There was an error!");
                        e.printStackTrace();
                    }
                    return true;
                }
                helpMenu("block", sender);
                return true;
            case "sge":
                switch (args.length) {
                    case 2:
                        if (args[1].equalsIgnoreCase("lock")) {
                            MagicAssistant.stitch.toggleLock(sender);
                            return true;
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
                if (!MagicAssistant.getInstance().getConfig().getBoolean("shooter-enabled")) {
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
                            if (inv.getItem(4) != null && !inv.getItem(4).getType().equals(Material.AIR)) {
                                Shooter.addToHashMap(player.getUniqueId(), player.getInventory().getItem(4));
                            }
                            player.setMetadata("shooter", new FixedMetadataValue(MagicAssistant.getInstance(), 0));
                            inv.setItem(4, Shooter.getItem());
                            inv.setHeldItemSlot(4);
                            Shooter.sendGameMessage(player);
                            Shooter.ingame.add(player.getUniqueId());
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("remove")) {
                            Player player = PlayerUtil.findPlayer(args[2]);
                            if (player == null) {
                                sender.sendMessage(ChatColor.RED + "Could not find Player!");
                                return true;
                            }
                            PlayerInventory inv = player.getInventory();
                            if (!inv.contains(Shooter.getItem())) {
                                return true;
                            }
                            Shooter.done(player);
                        }
                }
                break;
            case "changer":
                switch (args.length) {
                    case 2:
                        switch (args[1]) {
                            case "list":
                                List<String> list = MagicAssistant.getInstance().blockChanger.changerList();
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
                                    MagicAssistant.getInstance().blockChanger.reload();
                                } catch (FileNotFoundException e) {
                                    sender.sendMessage(ChatColor.RED + "Error reloading, see console for details.");
                                    e.printStackTrace();
                                    return true;
                                }
                                sender.sendMessage(ChatColor.GREEN + "Reload Complete!");
                                return true;
                            case "debug":
                                if (sender instanceof Player) {
                                    if (MagicAssistant.getInstance().blockChanger.toggleDebug(((Player) sender))) {
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
                                sender.sendMessage(ChatColor.RED + "REMEMBER: NorthEast bottom to SouthWest top");
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
                                if (MagicAssistant.getInstance().blockChanger.toggleDebug(tp)) {
                                    sender.sendMessage(ChatColor.YELLOW + "Removed " + tp.getName() + " from Changer Debugging!");
                                    tp.sendMessage(ChatColor.YELLOW + "You're no longer in Changer Debugging mode!");
                                    return true;
                                } else {
                                    sender.sendMessage(ChatColor.YELLOW + "Added " + tp.getName() + " to Changer Debugging!");
                                    tp.sendMessage(ChatColor.YELLOW + "You're now in Changer Debugging mode!");
                                    return true;
                                }
                            case "remove":
                                final Changer changer = MagicAssistant.getInstance().blockChanger.getChanger(args[2]);
                                if (changer == null) {
                                    sender.sendMessage(ChatColor.RED + "Changer not found by the name of " +
                                            ChatColor.GREEN + args[2]);
                                    return true;
                                }
                                final List<Changer> l = MagicAssistant.getInstance().blockChanger.getChangers();
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
                                        MagicAssistant.getInstance().blockChanger.removeChanger(changer.getName());
                                        sender.sendMessage(ChatColor.GREEN + "Removed changer " + ChatColor.AQUA + args[2]);
                                    }
                                });
                                return true;
                            case "info":
                                Changer chngr = MagicAssistant.getInstance().blockChanger.getChanger(args[2]);
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
                                if (MagicAssistant.getInstance().blockChanger.getChanger(name) != null) {
                                    sender.sendMessage(ChatColor.RED + "A changer with that name already exists!");
                                    return true;
                                }
                                HashMap<Material, Byte> from = MagicAssistant.getInstance().blockChanger.blocksFromString(args[3]);
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
                                Location loc1 = MagicAssistant.getInstance().blockChanger.getSelection(0, ((Player) sender));
                                //loc1 = getLocation(((Player) sender).getWorld(), args[6]);
                                Location loc2 = MagicAssistant.getInstance().blockChanger.getSelection(1, ((Player) sender));
                                //loc2 = getLocation(((Player) sender).getWorld(), args[7]);
                                if (loc1 == null || loc2 == null) {
                                    sender.sendMessage(ChatColor.RED + "You don't have a full selection selected!");
                                    return true;
                                }
                                Changer changer = new Changer(name, loc1, loc2, from, Material.getMaterial(to), data,
                                        Material.getMaterial(send));
                                MagicAssistant.getInstance().blockChanger.addChanger(changer);
                                sender.sendMessage(ChatColor.GREEN + "Created Changer " + ChatColor.AQUA + name);
                                MagicAssistant.getInstance().blockChanger.clearSelection(((Player) sender).getUniqueId());
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
            case "reload":
                MagicAssistant ma = MagicAssistant.getInstance();
                sender.sendMessage(ChatColor.BLUE + "Reloading Plugin...");
                SqlUtil.initialize();
                MagicAssistant.bandUtil.askForParty();
                ma.setupFirstJoinItems();
                ma.setupNewJoinMessages();
                ma.setupFoodLocations();
                ma.setupRides();
                HotelUtil.refreshRooms();
                MagicAssistant.packManager.initialize();
                sender.sendMessage(ChatColor.BLUE + "Plugin Reloaded!");
                return true;
            default:
                helpMenu("main", sender);
        }
        return true;
    }

    public static void helpMenu(String menu, CommandSender sender) {
        switch (menu) {
            case "main":
                sender.sendMessage(ChatColor.GREEN + "Magic Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic reload " + ChatColor.AQUA + "- Reload plugin");
                sender.sendMessage(ChatColor.GREEN + "/magic show " + ChatColor.AQUA + "- Control a Show");
                sender.sendMessage(ChatColor.GREEN + "/magic block " + ChatColor.AQUA + "- Manipulate Blocks");
                sender.sendMessage(ChatColor.GREEN + "/magic sge " + ChatColor.AQUA + "- Features for SGE");
                sender.sendMessage(ChatColor.GREEN + "/magic changer " + ChatColor.AQUA + "- Change blocks for rides!");
                sender.sendMessage(ChatColor.GREEN + "/magic shooter " + ChatColor.AQUA + "- Features for Shooter Games");
                break;
            case "show":
                sender.sendMessage(ChatColor.GREEN + "Show Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic show start [Show Name] " + ChatColor.AQUA + "- Start a Show");
                sender.sendMessage(ChatColor.GREEN + "/magic show stop [Show Name] " + ChatColor.AQUA + "- Stop a Show");
                break;
            case "block":
                sender.sendMessage(ChatColor.GREEN + "Block Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic block move [blockID] [speed] x1,y1,z1 x2,y2,z2 addX addY addZ");
                break;
            case "sge":
                sender.sendMessage(ChatColor.GREEN + "Stitch Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic sge lock " + ChatColor.AQUA + "- Locks/Unlocks Show Room");
                sender.sendMessage(ChatColor.GREEN + "/magic sge eject " + ChatColor.AQUA + "- Ejects all players from their seats");
                sender.sendMessage(ChatColor.GREEN + "/magic sge join [Player name] " + ChatColor.AQUA + "- Adds a player to the Show" + ChatColor.RED + "*");
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
                sender.sendMessage(ChatColor.GREEN + "/magic changer wand " + ChatColor.AQUA + "Get the Changer Wand");
                sender.sendMessage(ChatColor.GREEN + "/magic changer remove [Name] " + ChatColor.AQUA + "Remove a Changer Area");
                sender.sendMessage(ChatColor.GREEN + "/magic changer reload " + ChatColor.AQUA + "- Reload areas");
                break;
        }
        if (containsCommandBlockOnly.contains(menu)) {
            sender.sendMessage(ChatColor.RED + "* Only Command Blocks can do this");
        }
    }

    @EventHandler
    public void onTick(TickEvent event) {
        Iterator<Show> showIterator = songs.values().iterator();
        while (showIterator.hasNext()) {
            Show show = showIterator.next();
            if (show.update()) {
                System.out.print("Show Ended.");
                showIterator.remove();
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
                        Bukkit.broadcastMessage("Block Added " + blk.getLocation());
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
            songs.put(name, new Show(MagicAssistant.getInstance(), file));
        }
    }
}
