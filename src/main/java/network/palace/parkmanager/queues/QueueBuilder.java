package network.palace.parkmanager.queues;

import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.QueueType;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class QueueBuilder extends Queue {
    private QueueType type = null;
    private HashMap<String, Object> queueTypeFields = new HashMap<>();

    public QueueBuilder() {
        super(0, null, null, null, 0, 0, false, null, new ArrayList<>());
    }

    @Override
    public QueueType getQueueType() {
        return null;
    }

    @Override
    protected void handleSpawn(List<CPlayer> players) {
    }

    public void nextStep(CPlayer player, String[] args) {
        if (name == null) {
            //Step 1
            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "/queue create [name]");
                return;
            }
            this.name = args[0];
            player.sendMessage(ChatColor.GREEN + "Next, let's give your queue a warp. Run " + ChatColor.YELLOW + "/queue create [warp]");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "The warp doesn't have to exist right now, but if it doesn't players won't be teleported anywhere!");
            return;
        }
        if (warp == null) {
            //Step 2
            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "/queue create [warp]");
                return;
            }
            this.warp = args[0];
            player.sendMessage(ChatColor.GREEN + "Queues bring in a set number of players per group, so let's define that group size. Run "
                    + ChatColor.YELLOW + "/queue create [groupSize]");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "This number must be at least 1, but no more than 100.");
            return;
        }
        if (groupSize == 0) {
            //Step 3
            if (args.length < 1 || !MiscUtil.checkIfInt(args[0])) {
                player.sendMessage(ChatColor.RED + "/queue create [groupSize]");
                return;
            }
            int groupSize = Integer.parseInt(args[0]);
            if (groupSize < 1 || groupSize > 100) {
                player.sendMessage(ChatColor.RED + "/queue create [groupSize]");
                player.sendMessage(ChatColor.RED + "groupSize must be at least 1, but no more than 100! You entered: " + groupSize);
                return;
            }
            this.groupSize = groupSize;
            player.sendMessage(ChatColor.GREEN + "We need to define the number of seconds after bringing in a group to wait before we bring the next group in. Run "
                    + ChatColor.YELLOW + "/queue create [delay in seconds]");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "Delays must be at least 5, but have no upper limit.");
            return;
        }
        if (delay == 0) {
            //Step 4
            if (args.length < 1 || !MiscUtil.checkIfInt(args[0])) {
                player.sendMessage(ChatColor.RED + "/queue create [delay]");
                return;
            }
            int delay = Integer.parseInt(args[0]);
            if (delay < 5) {
                player.sendMessage(ChatColor.RED + "/queue create [delay]");
                player.sendMessage(ChatColor.RED + "delay must be at least 5! You entered: " + delay);
                return;
            }
            this.delay = delay;
            player.sendMessage(ChatColor.GREEN + "Now, let's define where exactly players are brought in, known as the \"station\". Run "
                    + ChatColor.YELLOW + "/queue create");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "This command doesn't have any parameters because it uses where you're standing when you run it!");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "When running this, make sure you're in the exact position you want players to be teleported to.");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Note: " + ChatColor.DARK_AQUA + "" + ChatColor.ITALIC
                    + "Don't forget about where you're looking! Players will be looking exactly where you are when they're teleported in.");
            return;
        }
        if (station == null) {
            //Step 5
            this.station = player.getLocation();
            player.sendMessage(ChatColor.GREEN + "Now we need to define what " + ChatColor.ITALIC + "type " + ChatColor.GREEN + "of queue you want to make. The choices are:");
            player.sendMessage(ChatColor.YELLOW + "'block' - " + ChatColor.GREEN + "This type of queue spawns in a redstone block at a specified location when players are brought in");
            player.sendMessage(ChatColor.GREEN + "Run " + ChatColor.YELLOW + "/queue create [type]");
            return;
        }
        if (type == null) {
            //Step 6
            if (args.length < 1 || QueueType.fromString(args[0]) == null) {
                player.sendMessage(ChatColor.RED + "/queue create [type]");
                player.sendMessage(ChatColor.GREEN + "The options for type are:");
                player.sendMessage(ChatColor.YELLOW + "'block' - " + ChatColor.GREEN + "This type of queue spawns in a redstone block at a specified location when players are brought in");
                return;
            }
            QueueType type = QueueType.fromString(args[0]);
            this.type = type;
            if (type.equals(QueueType.BLOCK)) {
                player.sendMessage(ChatColor.GREEN + "Great! All that's left is to set where the redstone block is spawned in.");
                player.sendMessage(ChatColor.GREEN + "Stand where the redstone block should be placed, then run " + ChatColor.YELLOW + "/queue create");
                player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "This command doesn't have any parameters because it uses where you're standing when you run it!");
                player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "When running this, make sure you're in the exact position you want a redstone block to be placed.");
                player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "This block is placed 1 second after players are teleported in, then removed 1 second later.");
                player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "When the block is removed, it is set to "
                        + ChatColor.AQUA + "AIR" + ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + ", so make sure that doesn't interfere with any builds!");
            }
            return;
        }
        if (type.equals(QueueType.BLOCK)) {
            if (!queueTypeFields.containsKey("blockLocation")) {
                //Step 7
                Location loc = player.getLocation();
                queueTypeFields.put("blockLocation", new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
                player.sendMessage(ChatColor.GREEN + "Great! Finalizing your Block Queue...");
                Queue finalQueue = finishAndCreate();
                if (finalQueue == null) {
                    player.sendMessage(ChatColor.RED + "Uh oh, looks like there was an error creating this queue! Try again and if this problem persists, contact a Developer.");
                    return;
                }
                ParkManager.getQueueManager().addQueue(finalQueue);
                player.sendMessage(ChatColor.GREEN + "Your queue is all ready to go! It's closed by default, but you can change that with " + ChatColor.YELLOW + "/queue open");
            }
        }
    }

    public Queue finishAndCreate() {
        if (type.equals(QueueType.BLOCK)) {
            return new BlockQueue(ParkManager.getQueueManager().getNextId(), UUID.randomUUID(), ChatColor.translateAlternateColorCodes('&', this.name),
                    this.warp, this.groupSize, this.delay, false, this.station, new ArrayList<>(), (Location) queueTypeFields.get("blockLocation"));
        }
        return null;
    }
}
