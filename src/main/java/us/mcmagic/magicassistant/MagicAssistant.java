package us.mcmagic.magicassistant;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.mcmagic.magicassistant.commands.*;
import us.mcmagic.magicassistant.listeners.*;
import us.mcmagic.magicassistant.magicband.Attraction;
import us.mcmagic.magicassistant.magicband.Ride;
import us.mcmagic.magicassistant.magicband.Warp;
import us.mcmagic.magicassistant.utils.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.*;

public class MagicAssistant extends JavaPlugin implements Listener {
    public static MagicAssistant plugin;
    public static Inventory ni;
    public static List<FoodLocation> foodLocations = new ArrayList<>();
    public static List<PlayerData> playerData = new ArrayList<>();
    public int randomNumber = 0;
    public final HashMap<Player, ArrayList<Block>> watching = new HashMap<>();
    public final HashMap<Player, ArrayList<Block>> chattimeout = new HashMap<>();
    public static List<Warp> warps = new ArrayList<>();
    public static HashMap<Integer, List<Ride>> ridePages = new HashMap<>();
    public static HashMap<Integer, List<Attraction>> attPages = new HashMap<>();
    public static String serverName;
    public static Location spawn;
    public static Location hub;
    public static boolean spawnOnJoin;
    public static boolean crossServerInv;
    public static boolean resortsServer;
    public static FileConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/MagicAssistant/config.yml"));
    private WorldGuardPlugin wg;
    public static List<String> joinMessages = config
            .getStringList("join-messages");
    public static List<ItemStack> firstJoinItems = new ArrayList<>();
    public static Map<Integer, Integer> items = new HashMap<>();
    public static List<String> newJoinMessage = new ArrayList<>();
    public static boolean party = false;
    public static List<String> partyServer = new ArrayList<>();
    public static boolean hubServer;

    public void onEnable() {
        registerListeners();
        BandUtil.askForParty();
        InventoryUtil.initialize();
        BandUtil.initialize();
        getConfig().options().copyDefaults(true);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new PluginMessage(this));
        saveConfig();
        FileUtil.setupConfig();
        FileUtil.setupMenuFile();
        warps.clear();
        setupFirstJoinItems();
        setupNewJoinMessages();
        getLogger().info("Initializing Warps...");
        WarpUtil.refreshWarps();
        getLogger().info("Warps Initialized!");
        getLogger().info("Initializing Food Locations...");
        setupFoodLocations();
        getLogger().info("Food Locations Initialized!");
        getLogger().info("Initializing Rides...");
        setupRides();
        getLogger().info("Rides Initialized!");
        getLogger().info("Initializing Attractions...");
        setupAttractions();
        getLogger().info("Attractions Initialized!");
        hub = new Location(Bukkit.getWorlds().get(0), getConfig().getDouble("hub.x"), getConfig().getDouble("hub.y"), getConfig().getDouble("hub.z"), getConfig().getInt("hub.yaw"), getConfig().getInt("hub.pitch"));
        spawn = new Location(Bukkit.getWorld(getConfig().getString("spawn.world")), getConfig().getDouble("spawn.x"), getConfig().getDouble("spawn.y"), getConfig().getDouble("spawn.z"), getConfig().getInt("spawn.yaw"), getConfig().getInt("spawn.pitch"));
        serverName = getConfig().getString("server-name");
        spawnOnJoin = getConfig().getBoolean("spawn-on-join");
        crossServerInv = getConfig().getBoolean("transfer-inventories");
        resortsServer = serverName == "Resorts";
        hubServer = getConfig().getBoolean("hub-server");
        getLogger().info("Magic Assistant is ready to help!");
    }

    public void setupNewJoinMessages() {
        FileConfiguration config = getConfig();
        List<String> msgs = config.getStringList("first-join-message");
        for (String msg : msgs) {
            newJoinMessage.add(ChatColor.translateAlternateColorCodes('&', msg));
        }
    }

    public void setupFirstJoinItems() {
        FileConfiguration config = getConfig();
        List<String> items = config.getStringList("first-join-items");
        for (String item : items) {
            String[] list = item.split(":");
            if (list.length == 1) {
                ItemStack i = new ItemStack(Integer.parseInt(list[0]));
                firstJoinItems.add(i);
                continue;
            }
            if (list.length == 2) {
                ItemStack i = new ItemStack(Integer.parseInt(list[0]), 1, (byte) Integer.parseInt(list[1]));
                firstJoinItems.add(i);
                continue;
            }
            if (list.length == 3) {
                ItemStack i = new ItemStack(Integer.parseInt(list[0]), Integer.parseInt(list[2]), (byte) Integer.parseInt(list[1]));
                firstJoinItems.add(i);
                continue;
            }
        }
    }

    public WorldGuardPlugin getWG() {
        return wg;
    }

    public void onDisable() {
        getLogger().info("Magic Assistant is taking a coffee break.");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args) {
        if (label.equalsIgnoreCase("mb")) {
            Command_mb.execute(sender, label, args);
            return true;
        } else if (label.equalsIgnoreCase("bc")) {
            Command_bc.execute(sender, label, args);
            return true;
        } else if (label.equalsIgnoreCase("fly")) {
            Command_fly.execute(sender, label, args);
            return true;
        } else if (label.equalsIgnoreCase("vanish")
                || label.equalsIgnoreCase("v")) {
            Command_vanish.execute(sender, label, args);
            return true;
        } else if (label.equalsIgnoreCase("msg") || label.equalsIgnoreCase("tell") || label.equalsIgnoreCase("t")
                || label.equalsIgnoreCase("w") || label.equalsIgnoreCase("whisper") || label.equalsIgnoreCase("m")) {
            Command_msg.execute(sender, label, args);
            return true;
        } else if (label.equalsIgnoreCase("invupdate")) {
            Command_invupdate.execute(sender, label, args);
            return true;
        } else if (label.equalsIgnoreCase("warp"))

        {
            Command_warp.execute(label, sender, args);
            return true;
        } else if (label.equalsIgnoreCase("top"))

        {
            Command_top.execute(sender, label, args);
            return true;
        } else if (label.equalsIgnoreCase("sethub"))

        {
            Player player = (Player) sender;
            if (player.isOp()) {
                double x = player.getLocation().getX();
                double y = player.getLocation().getY();
                double z = player.getLocation().getZ();
                double yaw = player.getLocation().getYaw();
                double pitch = player.getLocation().getPitch();
                getConfig().set("hub.x", x);
                getConfig().set("hub.y", y);
                getConfig().set("hub.z", z);
                getConfig().set("hub.yaw", yaw);
                getConfig().set("hub.pitch", pitch);
                getConfig().set("hubworld", player.getWorld().getName());
                hub = new Location(Bukkit.getWorlds().get(0), getConfig().getDouble("hub.x"), getConfig().getDouble("hub.y"), getConfig().getDouble("hub.z"), getConfig().getInt("hub.yaw"), getConfig().getInt("hub.pitch"));
                saveConfig();
                player.sendMessage(ChatColor.DARK_AQUA
                        + "The hub location has been set!");
            } else {
                player.sendMessage(ChatColor.RED
                        + "You do not have permission to use this command!");
            }
            return true;
        } else if (label.equalsIgnoreCase("serverparty"))

        {
            Command_serverparty.execute(sender, label, args);
            return true;
        } else if (label.equalsIgnoreCase("hub"))

        {
            if (!(sender instanceof Player)) {
                if (args.length > 0) {
                    Player tp = PlayerUtil.findPlayer(args[0]);
                    if (tp == null) {
                        sender.sendMessage(ChatColor.RED + "Player not found!");
                        return true;
                    }
                    tp.teleport(hub);
                    tp.sendMessage(ChatColor.DARK_AQUA + "You have arrived at the Hub!");
                }
                return true;
            }
            ((Player) sender).teleport(hub);
            sender.sendMessage(ChatColor.DARK_AQUA + "You have arrived at the Hub!");
            return true;
        } else if (label.equalsIgnoreCase("setwarp"))

        {
            Command_setwarp.execute(sender, label, args);
            return true;
        } else if (label.equalsIgnoreCase("uwarp"))

        {
            Command_uwarp.execute(sender, label, args);
            return true;
        } else if (label.equalsIgnoreCase("wrl"))

        {
            Command_wrl.execute(sender, label, args);
            return true;
        } else if (label.equalsIgnoreCase("invsee"))

        {
            Command_invsee.execute(sender, label, args);
            return true;
        } else if (label.equalsIgnoreCase("enderchest"))

        {
            Command_enderchest.execute(sender, label, args);
            return true;
        } else if (label.equalsIgnoreCase("give")
                || label.equalsIgnoreCase("item")
                || label.equalsIgnoreCase("i"))

        {
            Command_give.execute(sender, label, args);
        } else if (label.equalsIgnoreCase("delwarp")
                || label.equalsIgnoreCase("removewarp"))

        {
            Command_delwarp.execute(sender, label, args);
            return true;
        } else if (label.equalsIgnoreCase("delay"))

        {
            Command_delay.execute(sender, args);
            return true;
        } else if (label.equalsIgnoreCase("spawn"))

        {
            Command_spawn.execute(sender, label, args);
            return true;
        } else if (label.equalsIgnoreCase("setspawn"))

        {
            Command_setspawn.execute(sender, label, args);
            return true;
        } else if (label.equalsIgnoreCase("smite"))

        {
            Command_smite.execute(sender, label, args);
            return true;
        } else if (label.equalsIgnoreCase("more"))

        {
            Command_more.execute(sender, label, args);
            return true;
        } else if (label.equalsIgnoreCase("heal"))

        {
            Command_heal.execute(sender, label, args);
            return true;
        } else if (label.equalsIgnoreCase("helpop")
                || label.equalsIgnoreCase("ac"))

        {
            Command_helpop.execute(sender, label, args);
        } else if (label.equalsIgnoreCase("ptime"))

        {
            Command_ptime.execute(sender, label, args);
            return true;
        } else if (label.equalsIgnoreCase("pweather"))

        {
            Command_pweather.execute(sender, label, args);
            return true;
        } else if (label.equalsIgnoreCase("invcheck"))

        {
            Command_invcheck.execute(sender, label, args);
            return true;
        } else if (label.equalsIgnoreCase("day"))

        {
            Command_day.execute(label, args, sender);
            return true;
        } else if (label.equalsIgnoreCase("night"))

        {
            Command_night.execute(label, args, sender);
            return true;
        } else if (label.equalsIgnoreCase("noon"))

        {
            Command_noon.execute(label, args, sender);
            return true;
        } else if (label.equalsIgnoreCase("gwts"))

        {
            // List of types of hats:
            ItemStack clear = new ItemStack(Material.AIR);
            ItemStack red = new ItemStack(Material.LEATHER_HELMET, 1);
            ItemStack orj = new ItemStack(Material.LEATHER_HELMET, 1);
            ItemStack yel = new ItemStack(Material.LEATHER_HELMET, 1);
            ItemStack grn = new ItemStack(Material.LEATHER_HELMET, 1);
            ItemStack blu = new ItemStack(Material.LEATHER_HELMET, 1);
            ItemStack pur = new ItemStack(Material.LEATHER_HELMET, 1);
            ItemStack wht = new ItemStack(Material.LEATHER_HELMET, 1);
            ItemStack blk = new ItemStack(Material.LEATHER_HELMET, 1);
            ItemStack pnk = new ItemStack(Material.LEATHER_HELMET, 1);
            ItemStack aqa = new ItemStack(Material.LEATHER_HELMET, 1);
            LeatherArmorMeta rarm = (LeatherArmorMeta) red.getItemMeta();
            LeatherArmorMeta oarm = (LeatherArmorMeta) orj.getItemMeta();
            LeatherArmorMeta yarm = (LeatherArmorMeta) yel.getItemMeta();
            LeatherArmorMeta garm = (LeatherArmorMeta) grn.getItemMeta();
            LeatherArmorMeta barm = (LeatherArmorMeta) blu.getItemMeta();
            LeatherArmorMeta parm = (LeatherArmorMeta) pur.getItemMeta();
            LeatherArmorMeta warm = (LeatherArmorMeta) wht.getItemMeta();
            LeatherArmorMeta blarm = (LeatherArmorMeta) blk.getItemMeta();
            LeatherArmorMeta piarm = (LeatherArmorMeta) pnk.getItemMeta();
            LeatherArmorMeta aqam = (LeatherArmorMeta) aqa.getItemMeta();
            rarm.setColor(Color.fromRGB(170, 0, 0));
            oarm.setColor(Color.fromRGB(255, 102, 0));
            yarm.setColor(Color.fromRGB(255, 222, 0));
            garm.setColor(Color.fromRGB(0, 153, 0));
            barm.setColor(Color.fromRGB(51, 51, 255));
            parm.setColor(Color.fromRGB(39, 31, 155));
            warm.setColor(Color.fromRGB(255, 255, 255));
            blarm.setColor(Color.fromRGB(0, 0, 0));
            piarm.setColor(Color.fromRGB(255, 0, 255));
            aqam.setColor(Color.fromRGB(0, 255, 255));
            rarm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
            oarm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
            yarm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
            garm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
            barm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
            parm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
            warm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
            blarm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
            piarm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
            aqam.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
            red.setItemMeta(rarm);
            orj.setItemMeta(oarm);
            yel.setItemMeta(yarm);
            grn.setItemMeta(garm);
            blu.setItemMeta(barm);
            pur.setItemMeta(parm);
            wht.setItemMeta(warm);
            blk.setItemMeta(blarm);
            pnk.setItemMeta(piarm);
            aqa.setItemMeta(aqam);
            // Referring to console
            if (!(sender instanceof Player)) {
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.DARK_AQUA
                            + "/gwts <red,orange,yellow,green,blue,aqua,purple,pink,white,black,done> [player]");
                } else if (args.length == 1) {
                    sender.sendMessage(ChatColor.RED
                            + "Error: Cannot execute command through console");
                } else if (args.length == 2) {
                    Player tp = sender.getServer().getPlayer(args[1]);
                    PlayerInventory tpi = tp.getInventory();
                    if (args[0].equals("red")) {
                        tpi.setHelmet(red);
                    } else if (args[0].equalsIgnoreCase("orange")) {
                        tpi.setHelmet(orj);
                    } else if (args[0].equalsIgnoreCase("yellow")) {
                        tpi.setHelmet(yel);
                    } else if (args[0].equalsIgnoreCase("green")) {
                        tpi.setHelmet(grn);
                    } else if (args[0].equalsIgnoreCase("blue")) {
                        tpi.setHelmet(blu);
                    } else if (args[0].equalsIgnoreCase("aqua")) {
                        tpi.setHelmet(aqa);
                    } else if (args[0].equalsIgnoreCase("purple")) {
                        tpi.setHelmet(pur);
                    } else if (args[0].equalsIgnoreCase("white")) {
                        tpi.setHelmet(wht);
                    } else if (args[0].equalsIgnoreCase("black")) {
                        tpi.setHelmet(blk);
                    } else if (args[0].equalsIgnoreCase("pink")) {
                        tpi.setHelmet(pnk);
                    } else if (args[0].equalsIgnoreCase("done")) {
                        tpi.remove(red);
                        tpi.remove(orj);
                        tpi.remove(yel);
                        tpi.remove(grn);
                        tpi.remove(blu);
                        tpi.remove(pur);
                        tpi.remove(wht);
                        tpi.remove(blk);
                        tpi.remove(pnk);
                        tpi.remove(aqa);
                        tpi.setHelmet(clear);
                    }
                }
                // As a player command
            } else if (sender.hasPermission("magicassistant.gwts")) {
                Player player = (Player) sender;
                if (args.length == 0) {
                    player.sendMessage(ChatColor.DARK_AQUA
                            + "/gwts <red,orange,yellow,green,blue,aqua,purple,pink,white,black,done> [player]");
                } else if (args.length == 1) {
                    PlayerInventory pi = player.getInventory();
                    if (args[0].equalsIgnoreCase("red")) {
                        pi.setHelmet(red);
                    } else if (args[0].equalsIgnoreCase("orange")) {
                        pi.setHelmet(orj);
                    } else if (args[0].equalsIgnoreCase("yellow")) {
                        pi.setHelmet(yel);
                    } else if (args[0].equalsIgnoreCase("green")) {
                        pi.setHelmet(grn);
                    } else if (args[0].equalsIgnoreCase("blue")) {
                        pi.setHelmet(blu);
                    } else if (args[0].equalsIgnoreCase("aqua")) {
                        pi.setHelmet(aqa);
                    } else if (args[0].equalsIgnoreCase("purple")) {
                        pi.setHelmet(pur);
                    } else if (args[0].equalsIgnoreCase("white")) {
                        pi.setHelmet(wht);
                    } else if (args[0].equalsIgnoreCase("black")) {
                        pi.setHelmet(blk);
                    } else if (args[0].equalsIgnoreCase("pink")) {
                        pi.setHelmet(pnk);
                    } else if (args[0].equalsIgnoreCase("done")) {
                        pi.remove(red);
                        pi.remove(orj);
                        pi.remove(yel);
                        pi.remove(grn);
                        pi.remove(blu);
                        pi.remove(pur);
                        pi.remove(wht);
                        pi.remove(blk);
                        pi.remove(pnk);
                        pi.remove(aqa);
                        pi.setHelmet(clear);
                    }
                } else if (args.length == 2) {
                    Player tp = sender.getServer().getPlayer(args[1]);
                    PlayerInventory tpi = tp.getInventory();
                    if (args[0].equals("red")) {
                        tpi.setHelmet(red);
                    } else if (args[0].equals("orange")) {
                        tpi.setHelmet(orj);
                    } else if (args[0].equals("yellow")) {
                        tpi.setHelmet(yel);
                    } else if (args[0].equals("green")) {
                        tpi.setHelmet(grn);
                    } else if (args[0].equals("blue")) {
                        tpi.setHelmet(blu);
                    } else if (args[0].equalsIgnoreCase("aqua")) {
                        tpi.setHelmet(aqa);
                    } else if (args[0].equals("purple")) {
                        tpi.setHelmet(pur);
                    } else if (args[0].equals("white")) {
                        tpi.setHelmet(wht);
                    } else if (args[0].equals("black")) {
                        tpi.setHelmet(blk);
                    } else if (args[0].equalsIgnoreCase("pink")) {
                        tpi.setHelmet(pnk);
                    } else if (args[0].equals("done")) {
                        tpi.remove(red);
                        tpi.remove(orj);
                        tpi.remove(yel);
                        tpi.remove(grn);
                        tpi.remove(blu);
                        tpi.remove(pur);
                        tpi.remove(wht);
                        tpi.remove(blk);
                        tpi.remove(pnk);
                        tpi.remove(aqa);
                        tpi.setHelmet(clear);
                    }
                }
            }
        } else if (label.equalsIgnoreCase("magicassistant"))

        {
            if (args.length == 0) {
                Player player = (Player) sender;
                player.sendMessage(ChatColor.GREEN
                        + "----------------------------------------------------");
                player.sendMessage(ChatColor.DARK_GREEN
                        + "Magic assistant was created by " + ChatColor.BLUE
                        + "Legobuilder0813");
                player.sendMessage(ChatColor.DARK_GREEN
                        + "Version Number: "
                        + ChatColor.GOLD
                        + Bukkit.getServer().getPluginManager()
                        .getPlugin("magicassistant").getDescription()
                        .getVersion());
                player.sendMessage(ChatColor.GREEN
                        + "----------------------------------------------------");
            } else if (args.length == 1) {
                Player player = (Player) sender;
                if (args[0].equalsIgnoreCase("reload")
                        && player.hasPermission("magicassistant.reload")) {
                    player.sendMessage(ChatColor.AQUA + "[magicassistant]"
                            + ChatColor.BLUE + " Now reloading configuration");
                    this.reloadConfig();
                    player.sendMessage(ChatColor.AQUA + "[magicassistant]"
                            + ChatColor.BLUE + " Configuration reloaded!");
                }
            }
        } else if (label.equalsIgnoreCase("save"))

        {
            if (!(sender instanceof Player)) {
                return true;
            }
            Player player = (Player) sender;
            player.performCommand("save-all");
        } else if (label.equalsIgnoreCase("tp"))

        {
            Command_tp.execute(label, args, sender);
            return true;
        } else if (label.equalsIgnoreCase("head"))

        {
            Player player = (Player) sender;
            PlayerInventory pi = player.getInventory();
            if (args.length == 1) {
                ItemStack head = new ItemStack(Material.SKULL_ITEM, 1,
                        (short) 3);
                SkullMeta headm = (SkullMeta) head.getItemMeta();
                headm.setOwner(args[0]);
                head.setItemMeta(headm);
                pi.addItem(head);
                player.sendMessage(ChatColor.AQUA + "[magicassistant]"
                        + ChatColor.BLUE + " Enjoy your new head of " + args[0]
                        + "!");
            } else {
                player.sendMessage(ChatColor.RED + "/head [playerhead]");
            }
        }

        return false;
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void sendToServer(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(
                    Bukkit.getPluginManager().getPlugin("MagicAssistant"),
                    "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED
                    + "Sorry! It looks like something went wrong! It's probably out fault. We will try to fix it as soon as possible!");
        }
    }

    public static YamlConfiguration config() {
        return YamlConfiguration.loadConfiguration(new File(
                "plugins/magicassistant/config.yml"));
    }

    public static boolean isInPermGroup(Player player, String group) {
        String[] groups = WorldGuardPlugin.inst().getGroups(player);
        for (String group1 : groups) {
            if (group1.toLowerCase().equals(group.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static PlayerData getPlayerData(UUID uuid) {
        for (PlayerData data : playerData) {
            if (data.getUuid().equals(uuid)) {
                return data;
            }
        }
        return null;
    }

    public void setupFoodLocations() {
        File file = new File("plugins/MagicAssistant/menus.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> locations = config.getStringList("food-names");
        for (String location : locations) {
            String name = config
                    .getString("food." + location + ".name");
            String warp = config
                    .getString("food." + location + ".warp");
            int type = config.getInt("food." + location + ".type");
            byte data;
            if (config.contains("food." + location + ".data")) {
                data = (byte) config.getInt("food." + location
                        + ".data");
            } else {
                data = 0;
            }
            FoodLocation loc = new FoodLocation(name, warp, type, data);
            foodLocations.add(loc);
        }
    }

    private void setupRides() {
        File file = new File("plugins/MagicAssistant/menus.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> locations = config.getStringList("ride-names");
        List<Ride> rides = new ArrayList<>();
        for (String location : locations) {
            String name = config
                    .getString("ride." + location + ".name");
            String warp = config
                    .getString("ride." + location + ".warp");
            int type = config.getInt("ride." + location + ".type");
            byte data;
            if (config.contains("ride." + location + ".data")) {
                data = (byte) config.getInt("ride." + location
                        + ".data");
            } else {
                data = 0;
            }
            Ride ride = new Ride(name, warp, type, data);
            rides.add(ride);
        }
        int pages;
        if (locations.isEmpty()) {
            pages = 1;
        } else {
            pages = ((int) Math.ceil(locations.size() / 21)) + 1;
        }
        ridePages.clear();
        if (pages > 1) {
            int i = 1;
            int i2 = 1;
            for (Ride ride : rides) {
                if (i2 >= 22) {
                    i++;
                    i2 = 1;
                }
                if (i2 == 1) {
                    ridePages.put(i, new ArrayList<Ride>());
                    ridePages.get(i).add(ride);
                } else {
                    ridePages.get(i).add(ride);
                }
                i2++;
            }
        } else {
            ridePages.put(1, rides);
        }
    }

    private void setupAttractions() {
        File file = new File("plugins/MagicAssistant/menus.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> locations = config.getStringList("attraction-names");
        List<Attraction> attractions = new ArrayList<>();
        for (String location : locations) {
            getLogger().info("Test " + location);
            String name = config
                    .getString("attraction." + location + ".name");
            String warp = config
                    .getString("attraction." + location + ".warp");
            int type = config.getInt("attraction." + location + ".type");
            byte data;
            if (config.contains("attraction." + location + ".data")) {
                data = (byte) config.getInt("attraction." + location
                        + ".data");
            } else {
                data = 0;
            }
            Attraction att = new Attraction(name, warp, type, data);
            attractions.add(att);
        }
        int pages;
        if (locations.isEmpty()) {
            pages = 1;
        } else {
            pages = ((int) Math.ceil(locations.size() / 21)) + 1;
        }
        attPages.clear();
        if (pages > 1) {
            getLogger().info("Bigger than 1 " + locations.size());
            int i = 1;
            int i2 = 1;
            for (Attraction att : attractions) {
                if (i2 >= 22) {
                    i++;
                    i2 = 1;
                }
                if (i2 == 1) {
                    attPages.put(i, new ArrayList<Attraction>());
                    attPages.get(i).add(att);
                } else {
                    attPages.get(i).add(att);
                }
                i2++;
            }
        } else {
            getLogger().info("1 Page " + locations.size());
            attPages.put(1, attractions);
        }
    }

    public static Ride getRide(String name) {
        for (Map.Entry<Integer, List<Ride>> rides : ridePages.entrySet()) {
            for (Ride ride : rides.getValue()) {
                if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', ride.getDisplayName())).equals(name)) {
                    return ride;
                }
            }
        }
        return null;
    }

    public static Attraction getAttraction(String name) {
        for (Map.Entry<Integer, List<Attraction>> attractions : attPages.entrySet()) {
            for (Attraction ride : attractions.getValue()) {
                if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', ride.getDisplayName())).equals(name)) {
                    return ride;
                }
            }
        }
        return null;
    }

    public void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
        pm.registerEvents(new ChatListener(this), this);
        pm.registerEvents(new PlayerJoinAndLeave(this), this);
        pm.registerEvents(new SignChange(this), this);
        pm.registerEvents(new BlockEdit(this), this);
        pm.registerEvents(new InventoryClick(this), this);
        pm.registerEvents(new PlayerDropItem(this), this);
        pm.registerEvents(new PlayerInteract(this), this);
        pm.registerEvents(new VisibleUtil(this), this);
        pm.registerEvents(new WarpUtil(this), this);
        pm.registerEvents(new InventoryUtil(this), this);
    }
}
