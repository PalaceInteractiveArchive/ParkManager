package network.palace.parkmanager.queues;

import com.google.gson.JsonObject;
import network.palace.core.economy.CurrencyType;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.QueueType;
import network.palace.parkmanager.utils.FileUtil;
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
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                name.append(args[i]);
                if (i < (args.length - 1)) {
                    name.append(" ");
                }
            }
            this.name = ChatColor.translateAlternateColorCodes('&', name.toString());
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
            for (QueueType qt : QueueType.values()) {
                player.sendMessage(ChatColor.YELLOW + "'" + qt.name().toLowerCase() + "' - " + ChatColor.GREEN + qt.getDescription());
            }
            player.sendMessage(ChatColor.GREEN + "Run " + ChatColor.YELLOW + "/queue create [type]");
            return;
        }
        if (type == null) {
            //Step 6
            if (args.length < 1 || QueueType.fromString(args[0]) == null) {
                player.sendMessage(ChatColor.RED + "/queue create [type]");
                player.sendMessage(ChatColor.GREEN + "The options for type are:");
                for (QueueType qt : QueueType.values()) {
                    player.sendMessage(ChatColor.YELLOW + "'" + qt.name().toLowerCase() + "' - " + ChatColor.GREEN + qt.getDescription());
                }
                return;
            }
            QueueType type = QueueType.fromString(args[0]);
            this.type = type;
            JsonObject rideConfig = new JsonObject();
            switch (type) {
                case BLOCK:
                    player.sendMessage(ChatColor.GREEN + "Great! All that's left is to set where the redstone block is spawned in.");
                    player.sendMessage(ChatColor.GREEN + "Stand where the redstone block should be placed, then run " + ChatColor.YELLOW + "/queue create");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "This command doesn't have any parameters because it uses where you're standing when you run it!");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "When running this, make sure you're in the exact position you want a redstone block to be placed.");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "This block is placed 1 second after players are teleported in, then removed 1 second later.");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "When the block is removed, it is set to "
                            + ChatColor.AQUA + "AIR" + ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + ", so make sure that doesn't interfere with any builds!");
                    break;
                case CAROUSEL:
                    player.sendMessage(ChatColor.GREEN + "Okay, now let's finish configuring your Carousel. Stand exactly where the center of the carousel should be and run " + ChatColor.YELLOW + "/queue create");
                    rideConfig.addProperty("rideType", "CAROUSEL");
                    queueTypeFields.put("rideConfig", rideConfig);
                    break;
                case TEACUPS:
                    player.sendMessage(ChatColor.GREEN + "Okay, now let's finish configuring your Teacups ride. Stand exactly where the center of the platform should be and run " + ChatColor.YELLOW + "/queue create");
                    rideConfig.addProperty("rideType", "TEACUPS");
                    queueTypeFields.put("rideConfig", rideConfig);
                    break;
                case AERIAL_CAROUSEL:
                    player.sendMessage(ChatColor.GREEN + "Okay, now let's finish configuring your Aerial Carousel. Stand exactly where the center of the carousel should be and run " + ChatColor.YELLOW + "/queue create");
                    rideConfig.addProperty("rideType", "AERIAL_CAROUSEL");
                    queueTypeFields.put("rideConfig", rideConfig);
                    break;
                case FILE:
                    player.sendMessage(ChatColor.GREEN + "Okay, now let's finish configuring your File ride. Run " + ChatColor.YELLOW + "/queue create [file]");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "[file] is the name of the ride file being used without the file extension");
                    rideConfig.addProperty("rideType", "FILE");
                    queueTypeFields.put("rideConfig", rideConfig);
                    break;
            }
            return;
        }
        switch (type) {
            case BLOCK:
                if (!queueTypeFields.containsKey("blockLocation")) {
                    //Step 7
                    player.getRegistry().removeEntry("queueBuilder");
                    Location loc = player.getLocation();
                    queueTypeFields.put("blockLocation", new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
                    finish(player);
                }
                break;
            case CAROUSEL:
            case TEACUPS: {
                JsonObject rideConfig = (JsonObject) queueTypeFields.get("rideConfig");
                if (!rideConfig.has("center")) {
                    Location loc = player.getLocation();
                    rideConfig.add("center", FileUtil.getJson(new Location(loc.getWorld(), 0.5 * (Math.round(loc.getX() / 0.5)), loc.getBlockY(), 0.5 * (Math.round(loc.getZ() / 0.5)), 0, 0)));
                    player.sendMessage(ChatColor.GREEN + "Alright, next we're going to configure all of the standard plugin-ride settings.");
                }
                handlePluginQueue(player, args);
                break;
            }
            case AERIAL_CAROUSEL: {
                JsonObject rideConfig = (JsonObject) queueTypeFields.get("rideConfig");
                if (!rideConfig.has("center")) {
                    Location loc = player.getLocation();
                    rideConfig.add("center", FileUtil.getJson(new Location(loc.getWorld(), 0.5 * (Math.round(loc.getX() / 0.5)), loc.getBlockY(), 0.5 * (Math.round(loc.getZ() / 0.5)), 0, 0)));
                    player.sendMessage(ChatColor.GREEN + "Next, let's get the vehicle position values set. Run " + ChatColor.YELLOW + "/queue create [aerialRadius] [supportRadius] [small] [supportAngle] [height] [movein]");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "aerialRadius (rec. 6.5) is how far from the center vehicles rotate about");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "supportRadius (rec. 4.5) is how far from the center of the ride the center of the support is (usually about half aerialRadius)");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "small determines whether 12 or 16 vehicles are used");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "supportAngle (rec. 45) is the angle supports are at when the vehicles are on ground level");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "height (rec. 6) is the distance above ground level vehicles max out at (can't move any higher)");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "movein (rec. 0.9) is a value used to determine how fast vehicles move towards the center while ascending");
                    break;
                }
                if (!rideConfig.has("aerialRadius")) {
                    if (args.length < 6) {
                        player.sendMessage(ChatColor.RED + "/queue create [aerialRadius] [supportRadius] [small] [supportAngle] [height] [movein]");
                        break;
                    }
                    double aerialRadius, supportRadius, supportAngle, height, movein;
                    boolean small;
                    try {
                        aerialRadius = Double.parseDouble(args[0]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + args[0] + " isn't a valid double for aerialRadius!");
                        break;
                    }
                    try {
                        supportRadius = Double.parseDouble(args[1]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + args[1] + " isn't a valid double for supportRadius!");
                        break;
                    }
                    try {
                        small = Boolean.parseBoolean(args[2]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + args[2] + " isn't a valid boolean for small!");
                        break;
                    }
                    try {
                        supportAngle = Double.parseDouble(args[3]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + args[3] + " isn't a valid double for supportAngle!");
                        break;
                    }
                    try {
                        height = Double.parseDouble(args[4]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + args[4] + " isn't a valid double for height!");
                        break;
                    }
                    try {
                        movein = Double.parseDouble(args[5]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + args[5] + " isn't a valid double for movein!");
                        break;
                    }
                    rideConfig.addProperty("aerialRadius", aerialRadius);
                    rideConfig.addProperty("supportRadius", supportRadius);
                    rideConfig.addProperty("small", small);
                    rideConfig.addProperty("supportAngle", supportAngle);
                    rideConfig.addProperty("height", height);
                    rideConfig.addProperty("movein", movein);
                    player.sendMessage(ChatColor.GREEN + "Alright, next we're going to configure all of the standard plugin-ride settings.");
                }
                handlePluginQueue(player, args);
                break;
            }
            case FILE: {
                JsonObject rideConfig = (JsonObject) queueTypeFields.get("rideConfig");
                if (!rideConfig.has("file")) {
                    if (args.length < 1) {
                        player.sendMessage(ChatColor.RED + "/queue create [file]");
                        break;
                    }
                    rideConfig.addProperty("file", args[0]);
                    player.sendMessage(ChatColor.GREEN + "Alright, next we're going to configure all of the standard plugin-ride settings.");
                }
                handlePluginQueue(player, args);
                break;
            }
        }
    }

    private void handlePluginQueue(CPlayer player, String[] args) {
        if (!queueTypeFields.containsKey("exit")) {
            player.sendMessage(ChatColor.YELLOW + "First, we need the 'exit' location. This is where players are brought to when they exit the ride.");
            queueTypeFields.put("exit", null);
            return;
        }
        if (queueTypeFields.get("exit") == null) {
            Location loc = player.getLocation();
            queueTypeFields.put("exit", new Location(loc.getWorld(), 0.5 * (Math.round(loc.getX() / 0.5)), loc.getBlockY(), 0.5 * (Math.round(loc.getZ() / 0.5)), 0, 0));
            player.sendMessage(ChatColor.YELLOW + "Lastly, let's define the rewards a player gets from riding this ride.");
            player.sendMessage(ChatColor.YELLOW + "Run /queue create [honor points] [amount of money] <achievement id>");
            player.sendMessage(ChatColor.DARK_AQUA + "Honor points and a money reward is required for all rides.");
            player.sendMessage(ChatColor.DARK_AQUA + "An achievement is optional. If you don't want an achievement to be awarded, leave the field blank or put '0' for the id.");
            return;
        }
        if (!queueTypeFields.containsKey("currencyAmount")) {
            int honor;
            try {
                honor = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + args[0] + " isn't a valid integer for honor points!");
                player.sendMessage(ChatColor.YELLOW + "Run /queue create [honor points] [amount of money] <achievement id>");
                return;
            }
            int money;
            try {
                money = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + args[1] + " isn't a valid integer for money!");
                player.sendMessage(ChatColor.YELLOW + "Run /queue create [honor points] [amount of money] <achievement id>");
                return;
            }
            int achievementId;
            if (args.length > 2) {
                try {
                    achievementId = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + args[2] + " isn't a valid integer for achievement ID!");
                    player.sendMessage(ChatColor.YELLOW + "Run /queue create [honor points] [amount of money] <achievement id>");
                    return;
                }
            } else {
                achievementId = 0;
            }
            queueTypeFields.put("honorAmount", honor);
            queueTypeFields.put("currencyAmount", money);
            queueTypeFields.put("achievementId", achievementId);
            finish(player);
        }
    }

    private void finish(CPlayer player) {
        player.sendMessage(ChatColor.YELLOW + "Great! Finalizing your " + type.name() + " Queue...");
        Queue finalQueue;
        switch (type) {
            case BLOCK:
                finalQueue = new BlockQueue(ParkManager.getQueueManager().getNextId(), UUID.randomUUID(), ChatColor.translateAlternateColorCodes('&', this.name),
                        this.warp, this.groupSize, this.delay, false, this.station, new ArrayList<>(), (Location) queueTypeFields.get("blockLocation"));
                break;
            case CAROUSEL:
            case TEACUPS:
            case AERIAL_CAROUSEL:
            case FILE:
                finalQueue = new PluginQueue(ParkManager.getQueueManager().getNextId(), UUID.randomUUID(), ChatColor.translateAlternateColorCodes('&', this.name),
                        this.warp, this.groupSize, this.delay, false, this.station, new ArrayList<>(), (Location) queueTypeFields.get("exit"),
                        CurrencyType.BALANCE, (int) queueTypeFields.get("currencyAmount"), (int) queueTypeFields.get("honorAmount"),
                        (int) queueTypeFields.get("achievementId"), (JsonObject) queueTypeFields.get("rideConfig"));
                break;
            default:
                finalQueue = null;
        }
        if (finalQueue == null) {
            player.sendMessage(ChatColor.RED + "Uh oh, looks like there was an error creating this queue! Try again and if this problem persists, contact a Developer.");
            return;
        }
        ParkManager.getQueueManager().addQueue(finalQueue);
        player.sendMessage(ChatColor.GREEN + "Your queue is all ready to go! It's closed by default, but you can change that with " + ChatColor.YELLOW + "/queue open");
    }
}
