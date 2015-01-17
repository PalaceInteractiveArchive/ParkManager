package us.mcmagic.magicassistant.commands;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.show.Show;
import us.mcmagic.magicassistant.show.ticker.TickEvent;
import us.mcmagic.magicassistant.stitch.SGE;
import us.mcmagic.magicassistant.utils.WorldUtil;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.particles.ParticleUtil;

import java.io.File;
import java.util.*;

/**
 * Created by Marc on 1/7/15
 */
public class Command_magic implements Listener {
    public MagicAssistant pl;
    public static List<String> containsCommandBlockOnly = Arrays.asList("stitch");

    public Command_magic(MagicAssistant instance) {
        pl = instance;
    }

    private static HashMap<String, Show> songs = new HashMap<>();

    @SuppressWarnings("deprecation")
    public static void execute(final CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            helpMenu("main", sender);
            return;
        }
        switch (args[0]) {
            case "show":
                if (args.length == 3) {
                    switch (args[1]) {
                        case "start":
                            if (songs.containsKey(args[2])) {
                                sender.sendMessage(ChatColor.RED + "That show is already running!");
                                return;
                            }
                            if (!songs.isEmpty()) {
                                sender.sendMessage(ChatColor.RED + "Cannot start show, perhaps there is already a show running?");
                                return;
                            }
                            File file = new File("plugins/MagicAssistant/shows/" + args[2] + ".show");
                            if (!file.exists()) {
                                sender.sendMessage(ChatColor.RED + "----------------------------------------------");
                                sender.sendMessage(ChatColor.GOLD + args[2] + ChatColor.AQUA + " is not an existing show file.");
                                sender.sendMessage(ChatColor.RED + "----------------------------------------------");
                            } else {
                                songs.put(args[2], new Show(MagicAssistant.plugin, file));
                                sender.sendMessage(ChatColor.GOLD + args[2] + ChatColor.AQUA + " has started. Enjoy the show!");
                            }
                            return;
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
                            return;
                        default:
                            return;
                    }
                }
                helpMenu("show", sender);
                return;
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
                    return;
                }
                helpMenu("block", sender);
                return;
            case "sge":
                switch (args.length) {
                    case 2:
                        if (args[1].equalsIgnoreCase("lock")) {
                            SGE.toggleLock(sender);
                            return;
                        }
                        return;
                    case 4:
                        if (args[1].equalsIgnoreCase("effect")) {
                            if (args[2].equalsIgnoreCase("burp")) {
                                Location loc = WorldUtil.strToLoc(Bukkit.getWorlds().get(0).getName() + "," + args[3]);
                                ParticleUtil.spawnParticle(ParticleEffect.HAPPY_VILLAGER, loc, (float) 3, (float) 3, (float) 3, 0, 150);
                                return;
                            }
                            if (args[2].equalsIgnoreCase("spit")) {
                                Location loc = WorldUtil.strToLoc(Bukkit.getWorlds().get(0).getName() + "," + args[3]);
                                ParticleUtil.spawnParticle(ParticleEffect.LAVA, loc, (float) 3, (float) 3, (float) 3, 0, 150);
                                return;
                            }
                        }
                        return;
                    default:
                        helpMenu("sge", sender);
                        return;
                }
            default:
                helpMenu("main", sender);
        }
    }

    public static void helpMenu(String menu, CommandSender sender) {
        switch (menu) {
            case "main":
                sender.sendMessage(ChatColor.GREEN + "Magic Commands:");
                sender.sendMessage(ChatColor.GREEN + "/magic show " + ChatColor.AQUA + "- Control a Show");
                sender.sendMessage(ChatColor.GREEN + "/magic block " + ChatColor.AQUA + "- Manipulate Blocks");
                sender.sendMessage(ChatColor.GREEN + "/magic sge " + ChatColor.AQUA + "- Features for SGE");
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
        }
        if (containsCommandBlockOnly.contains(menu)) {
            sender.sendMessage(ChatColor.RED + "* Only Command Blocks can do this");
        }
    }

    @EventHandler
    public void Update(TickEvent event) {
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
            songs.put(name, new Show(MagicAssistant.plugin, file));
        }
    }
}
