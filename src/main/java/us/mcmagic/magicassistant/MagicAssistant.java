package us.mcmagic.magicassistant;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.mcmagic.magicassistant.blockchanger.BlockChanger;
import us.mcmagic.magicassistant.commands.*;
import us.mcmagic.magicassistant.designstation.DesignStation;
import us.mcmagic.magicassistant.handlers.*;
import us.mcmagic.magicassistant.hotels.HotelManager;
import us.mcmagic.magicassistant.listeners.*;
import us.mcmagic.magicassistant.queue.QueueManager;
import us.mcmagic.magicassistant.resourcepack.PackManager;
import us.mcmagic.magicassistant.ridemanager.Cart;
import us.mcmagic.magicassistant.ridemanager.RideManager;
import us.mcmagic.magicassistant.shooter.MessageTimer;
import us.mcmagic.magicassistant.shooter.Shooter;
import us.mcmagic.magicassistant.shop.ShopManager;
import us.mcmagic.magicassistant.show.ticker.Ticker;
import us.mcmagic.magicassistant.stitch.Stitch;
import us.mcmagic.magicassistant.trade.TradeManager;
import us.mcmagic.magicassistant.uoe.UniverseEnergyRide;
import us.mcmagic.magicassistant.utils.*;
import us.mcmagic.mcmagiccore.MCMagicCore;

import java.io.*;
import java.util.*;

public class MagicAssistant extends JavaPlugin implements Listener {
    public static List<FoodLocation> foodLocations = new ArrayList<>();
    public static HashMap<UUID, PlayerData> playerData = new HashMap<>();
    public static Stitch stitch;
    public static UniverseEnergyRide universeEnergyRide;
    public static List<Warp> warps = new ArrayList<>();
    public static HashMap<Integer, List<Ride>> ridePages = new HashMap<>();
    public static HashMap<Integer, List<Attraction>> attPages = new HashMap<>();
    public static Location spawn;
    public static Location hub;
    public static boolean spawnOnJoin;
    public static boolean crossServerInv;
    public static boolean resortsServer;
    public static FileConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/MagicAssistant/config.yml"));
    private WorldGuardPlugin wg;
    public static List<String> joinMessages = config.getStringList("join-messages");
    public static Map<Integer, Integer> firstJoinItems = new HashMap<>();
    public static Map<UUID, String> userCache = new HashMap<>();
    public static List<String> newJoinMessage = new ArrayList<>();
    public static boolean party = false;
    public static List<String> partyServer = new ArrayList<>();
    public static boolean hubServer;
    public static MagicAssistant instance;
    public static BlockChanger blockChanger = new BlockChanger();
    public static PackManager packManager;
    public static BandUtil bandUtil;
    public static RideManager rideManager;
    public static InventoryUtil inventoryUtil;
    public static ShopManager shopManager;
    public static TradeManager tradeManager;
    public static HotelManager hotelManager;
    public static AutographUtil autographUtil;
    public static QueueManager queueManager;

    public void onEnable() {
        instance = this;
        SqlUtil.initialize();
        log("Initializing Ride Manager...");
        rideManager = new RideManager();
        log("Ride Manager Initialized!");
        stitch = new Stitch();
        log("Initializing Pack Manager...");
        packManager = new PackManager();
        log("Pack Manager Initialized!");
        autographUtil = new AutographUtil();
        inventoryUtil = new InventoryUtil();
        universeEnergyRide = new UniverseEnergyRide();
        tradeManager = new TradeManager();
        log("Initializing Queue Manager...");
        queueManager = new QueueManager();
        log("Queue Manager Initialized!");
        bandUtil = new BandUtil();
        bandUtil.askForParty();
        registerListeners();
        registerCommands();
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new PluginMessage(this));
        saveConfig();
        FileUtil.setupConfig();
        FileUtil.setupMenuFile();
        try {
            blockChanger.initialize();
        } catch (FileNotFoundException ignored) {
            File file = new File("plugins/MagicAssistant/blockchanger.yml");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        warps.clear();
        setupFirstJoinItems();
        log("Initializing Warps...");
        WarpUtil.refreshWarps();
        log("Warps Initialized!");
        shopManager = new ShopManager();
        log("Initializing Hotel Rooms...");
        hotelManager = new HotelManager();
        log("Hotel Rooms Initialized!");
        log("Initializing Food Locations...");
        setupFoodLocations();
        log("Food Locations Initialized!");
        log("Initializing Rides...");
        setupRides();
        log("Rides Initialized!");
        setupAttractions();
        if (config.getBoolean("show-server")) {
            // Show Ticker
            System.out.println("Starting Show Timer");
            Bukkit.getScheduler().runTaskTimer(this, new Ticker(), 1, 1);
            System.out.println("Show Timer Started!");
        }
        hub = new Location(Bukkit.getWorld(config.getString("hub.world")), config.getDouble("hub.x"),
                config.getDouble("hub.y"), config.getDouble("hub.z"), config.getInt("hub.yaw"), config.getInt("hub.pitch"));
        spawn = new Location(Bukkit.getWorld(config.getString("spawn.world")), config.getDouble("spawn.x"),
                config.getDouble("spawn.y"), config.getDouble("spawn.z"), config.getInt("spawn.yaw"),
                config.getInt("spawn.pitch"));
        spawnOnJoin = getConfig().getBoolean("spawn-on-join");
        crossServerInv = getConfig().getBoolean("transfer-inventories");
        resortsServer = MCMagicCore.getMCMagicConfig().serverName == "Resorts";
        hubServer = getConfig().getBoolean("hub-server");
        packManager.initialize();
        DesignStation.initialize();
    }

    private void log(String s) {
        getLogger().info(s);
    }

    public void onDisable() {
        for (Player tp : Bukkit.getOnlinePlayers()) {
            tp.kickPlayer(Bukkit.getShutdownMessage());
        }
        hotelManager.serverStop();
        warps.clear();
        for (World world : Bukkit.getWorlds()) {
            for (Entity e : world.getEntities()) {
                if (e instanceof Cart) {
                    ((Cart) e).die();
                }
            }
        }
    }

    public static MagicAssistant getInstance() {
        return instance;
    }

    public static List<Warp> getWarps() {
        return new ArrayList<>(warps);
    }

    public static void clearWarps() {
        warps.clear();
    }

    public void setupFirstJoinItems() {
        firstJoinItems.clear();
        FileConfiguration config = getConfig();
        for (String s : config.getStringList("first-join-items")) {
            String[] items = s.split(" ");
            try {
                firstJoinItems.put(Integer.parseInt(items[0]),
                        Integer.parseInt(items[1]));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    public void setHub(Location loc) {
        hub = loc;
        config.set("hub.x", loc.getX());
        config.set("hub.y", loc.getY());
        config.set("hub.z", loc.getZ());
        config.set("hub.yaw", loc.getYaw());
        config.set("hub.pitch", loc.getPitch());
        config.set("hub.world", loc.getWorld().getName());
        try {
            config.save(new File("plugins/MagicAssistant/config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WorldGuardPlugin getWG() {
        return wg;
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void sendToServer(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(this, "BungeeCord", b.toByteArray());
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
        return playerData.get(uuid);
    }

    public void setupFoodLocations() {
        foodLocations.clear();
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

    public void setupRides() {
        ridePages.clear();
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

    public static void removeWarp(Warp warp) {
        warps.remove(warp);
    }

    public static void addWarp(Warp warp) {
        warps.add(warp);
    }

    public void registerCommands() {
        Commandmagic magic = new Commandmagic();
        getCommand("autograph").setExecutor(new Commandautograph());
        getCommand("autograph").setAliases(Arrays.asList("a", "auto"));
        getCommand("bc").setExecutor(new Commandbc());
        getCommand("day").setExecutor(new Commandday());
        getCommand("delay").setExecutor(new Commanddelay());
        getCommand("delwarp").setExecutor(new Commanddelwarp());
        getCommand("enderchest").setExecutor(new Commandenderchest());
        getCommand("fly").setExecutor(new Commandfly());
        getCommand("give").setExecutor(new Commandgive());
        getCommand("gwts").setExecutor(new Commandgwts());
        getCommand("head").setExecutor(new Commandhead());
        getCommand("heal").setExecutor(new Commandheal());
        getCommand("helpop").setExecutor(new Commandhelpop());
        getCommand("helpop").setAliases(Collections.singletonList("ac"));
        getCommand("hub").setExecutor(new Commandhub());
        getCommand("invsee").setExecutor(new Commandinvsee());
        getCommand("item").setExecutor(new Commanditem());
        getCommand("item").setAliases(Collections.singletonList("i"));
        getCommand("magic").setExecutor(magic);
        getCommand("mb").setExecutor(new Commandmb());
        getCommand("more").setExecutor(new Commandmore());
        getCommand("msg").setExecutor(new Commandmsg());
        getCommand("msg").setAliases(Arrays.asList("tell", "t", "w", "whisper", "m"));
        getCommand("night").setExecutor(new Commandnight());
        getCommand("noon").setExecutor(new Commandnoon());
        getCommand("nv").setExecutor(new Commandnv());
        getCommand("pack").setExecutor(new Commandpack());
        getCommand("ptime").setExecutor(new Commandptime());
        getCommand("pweather").setExecutor(new Commandpweather());
        getCommand("serverparty").setExecutor(new Commandserverparty());
        getCommand("sethub").setExecutor(new Commandsethub());
        getCommand("setspawn").setExecutor(new Commandsetspawn());
        getCommand("setwarp").setExecutor(new Commandsetwarp());
        getCommand("smite").setExecutor(new Commandsmite());
        getCommand("spawn").setExecutor(new Commandspawn());
        getCommand("top").setExecutor(new Commandtop());
        getCommand("tp").setExecutor(new Commandtp());
        getCommand("trade").setExecutor(new Commandtrade());
        getCommand("uwarp").setExecutor(new Commanduwarp());
        getCommand("vanish").setExecutor(new Commandvanish());
        getCommand("vanish").setAliases(Collections.singletonList("v"));
        getCommand("warp").setExecutor(new Commandwarp());
        getCommand("wrl").setExecutor(new Commandwrl());
        Bukkit.getPluginManager().registerEvents(magic, this);
    }

    public void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
        pm.registerEvents(new ChatListener(), this);
        pm.registerEvents(new PlayerJoinAndLeave(), this);
        pm.registerEvents(new SignChange(), this);
        pm.registerEvents(new BlockEdit(), this);
        pm.registerEvents(new InventoryClick(), this);
        pm.registerEvents(new PlayerDropItem(), this);
        pm.registerEvents(new PlayerInteract(), this);
        pm.registerEvents(new EntityDamage(), this);
        pm.registerEvents(blockChanger, this);
        pm.registerEvents(packManager, this);
        pm.registerEvents(new VisibleUtil(this), this);
        pm.registerEvents(new FountainUtil(this), this);
        pm.registerEvents(new PlayerCloseInventory(), this);
        pm.registerEvents(rideManager, this);
        if (getConfig().getBoolean("shooter-enabled")) {
            MessageTimer.start(this);
            pm.registerEvents(new Shooter(this), this);
        }
    }
}
