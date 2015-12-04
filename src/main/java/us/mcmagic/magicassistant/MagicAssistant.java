package us.mcmagic.magicassistant;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.mcmagic.magicassistant.autograph.AutographManager;
import us.mcmagic.magicassistant.blockchanger.BlockChanger;
import us.mcmagic.magicassistant.chairs.ArrowFactory;
import us.mcmagic.magicassistant.chairs.ChairListener;
import us.mcmagic.magicassistant.chairs.ChairManager;
import us.mcmagic.magicassistant.chairs.IArrowFactory;
import us.mcmagic.magicassistant.commands.*;
import us.mcmagic.magicassistant.designstation.DesignStation;
import us.mcmagic.magicassistant.handlers.*;
import us.mcmagic.magicassistant.hotels.HotelManager;
import us.mcmagic.magicassistant.listeners.*;
import us.mcmagic.magicassistant.parksounds.ParkSoundManager;
import us.mcmagic.magicassistant.pixelator.Pixelator;
import us.mcmagic.magicassistant.queue.QueueManager;
import us.mcmagic.magicassistant.resourcepack.PackManager;
import us.mcmagic.magicassistant.ridemanager.Cart;
import us.mcmagic.magicassistant.ridemanager.RideManager;
import us.mcmagic.magicassistant.shooter.MessageTimer;
import us.mcmagic.magicassistant.shooter.Shooter;
import us.mcmagic.magicassistant.shop.ShopManager;
import us.mcmagic.magicassistant.shop.WardrobeManager;
import us.mcmagic.magicassistant.show.ArmorStandManager;
import us.mcmagic.magicassistant.show.FountainManager;
import us.mcmagic.magicassistant.show.actions.SchematicAction;
import us.mcmagic.magicassistant.show.schedule.ScheduleManager;
import us.mcmagic.magicassistant.show.ticker.Ticker;
import us.mcmagic.magicassistant.stitch.Stitch;
import us.mcmagic.magicassistant.storage.StorageManager;
import us.mcmagic.magicassistant.uoe.UniverseEnergyRide;
import us.mcmagic.magicassistant.utils.*;
import us.mcmagic.magicassistant.watch.WatchTask;
import us.mcmagic.mcmagiccore.MCMagicCore;

import java.io.IOException;
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
    public static PlayerJoinAndLeave playerJoinAndLeave;
    public static boolean spawnOnJoin;
    public static boolean crossServerInv;
    public static boolean hotelServer;
    public static FileConfiguration config = FileUtil.configurationYaml();
    public static FountainManager fountainManager;
    public static TeleportUtil teleportUtil;
    public static List<String> joinMessages = config.getStringList("join-messages");
    public static Map<Integer, Integer> firstJoinItems = new HashMap<>();
    public static Map<UUID, String> userCache = new HashMap<>();
    public static boolean ttcServer;
    public static MagicAssistant instance;
    public static BlockChanger blockChanger;
    public static ParkSoundManager parkSoundManager;
    public static PackManager packManager;
    public static BandUtil bandUtil;
    public static RideManager rideManager;
    public static InventoryUtil inventoryUtil;
    public static ArmorStandManager armorStandManager;
    public static ItemUtil itemUtil;
    public static ShopManager shopManager;
    public static HotelManager hotelManager;
    public static QueueManager queueManager;
    public static AutographManager autographManager;
    public static StorageManager storageManager;
    public static VisibilityUtil vanishUtil;
    public static Shooter shooter = null;
    public static ChairManager chairManager;
    public static IArrowFactory chairFactory;
    public static ScheduleManager scheduleManager;
    public static WardrobeManager wardrobeManager;
    public static Pixelator pixelator;

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
        universeEnergyRide = new UniverseEnergyRide();
        autographManager = new AutographManager();
        log("Initializing Queue Manager...");
        queueManager = new QueueManager();
        log("Queue Manager Initialized!");
        bandUtil = new BandUtil();
        storageManager = new StorageManager();
        inventoryUtil = new InventoryUtil();
        vanishUtil = new VisibilityUtil();
        teleportUtil = new TeleportUtil();
        itemUtil = new ItemUtil();
        blockChanger = new BlockChanger();
        parkSoundManager = new ParkSoundManager();
        armorStandManager = new ArmorStandManager();
        chairManager = new ChairManager();
        wardrobeManager = new WardrobeManager();
        chairFactory = new ArrowFactory();
        playerJoinAndLeave = new PlayerJoinAndLeave();
        registerListeners();
        registerCommands();
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new PluginMessage());
        saveConfig();
        FileUtil.setupConfig();
        /*
        try {
            blockChanger.initialize();
        } catch (FileNotFoundException ignored) {
            File file = FileUtil.blockchangerFile();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        warps.clear();
        setupFirstJoinItems();
        log("Initializing Warps...");
        WarpUtil.refreshWarps();
        log("Warps Initialized!");
        shopManager = new ShopManager();
        String sn = MCMagicCore.getMCMagicConfig().serverName;
        hotelServer = sn.equals("Resorts") || sn.equals("DCL");
        log("Initializing Hotel Rooms...");
        hotelManager = new HotelManager();
        log("Hotel Rooms Initialized!");
        log("Initializing Food Locations...");
        setupFoodLocations();
        log("Food Locations Initialized!");
        log("Initializing Rides...");
        setupRides();
        log("Rides Initialized!");
        log("Initializing Show Schedule...");
        scheduleManager = new ScheduleManager();
        log("Show Schedule Initialized!");
        //enablePixelator();
        setupAttractions();
        if (config.getBoolean("show-server")) {
            // Show Ticker
            Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
            if (plugin instanceof WorldEditPlugin) {
                SchematicAction.setWorldEdit((WorldEditPlugin) plugin);
                log("Starting Show Timer");
                Bukkit.getScheduler().runTaskTimer(this, new Ticker(), 1, 1);
                log("Show Timer Started!");
            } else {
                log("Error finding WorldEdit!");
            }
        }
        hub = new Location(Bukkit.getWorld(config.getString("hub.world")), config.getDouble("hub.x"),
                config.getDouble("hub.y"), config.getDouble("hub.z"), config.getInt("hub.yaw"), config.getInt("hub.pitch"));
        spawn = new Location(Bukkit.getWorld(config.getString("spawn.world")), config.getDouble("spawn.x"),
                config.getDouble("spawn.y"), config.getDouble("spawn.z"), config.getInt("spawn.yaw"),
                config.getInt("spawn.pitch"));
        spawnOnJoin = getConfig().getBoolean("spawn-on-join");
        crossServerInv = getConfig().getBoolean("transfer-inventories");
        ttcServer = getConfig().getBoolean("hub-server");
        packManager.initialize();
        parkSoundManager.initialize();
        DesignStation.initialize();
        long curr = System.currentTimeMillis();
        long time = (curr % 1000) / 50;
        Bukkit.getScheduler().runTaskTimer(this, new WatchTask(), time, 20L);
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
        chairManager.emptyAllData();
        for (World world : Bukkit.getWorlds()) {
            for (Entity e : world.getEntities()) {
                if (e instanceof Cart) {
                    ((Cart) e).die();
                }
            }
        }
        //pixelator.rendererManager.disable();
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
            config.save(FileUtil.configurationFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PlayerData getPlayerData(UUID uuid) {
        return playerData.get(uuid);
    }

    public void setupFoodLocations() {
        foodLocations.clear();
        YamlConfiguration config = FileUtil.menusYaml();
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
        YamlConfiguration config = FileUtil.menusYaml();
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
        YamlConfiguration config = FileUtil.menusYaml();
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

    private void enablePixelator() {
        pixelator = new Pixelator();
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
        getCommand("back").setExecutor(new Commandback());
        getCommand("bc").setExecutor(new Commandbc());
        getCommand("build").setExecutor(new Commandbuild());
        getCommand("day").setExecutor(new Commandday());
        getCommand("delay").setExecutor(new Commanddelay());
        getCommand("delwarp").setExecutor(new Commanddelwarp());
        getCommand("enderchest").setExecutor(new Commandenderchest());
        getCommand("fly").setExecutor(new Commandfly());
        getCommand("give").setExecutor(new Commandgive());
        getCommand("head").setExecutor(new Commandhead());
        getCommand("heal").setExecutor(new Commandheal());
        getCommand("helpop").setExecutor(new Commandhelpop());
        getCommand("helpop").setAliases(Collections.singletonList("ac"));
        getCommand("hub").setExecutor(new Commandhub());
        getCommand("invsee").setExecutor(new Commandinvsee());
        getCommand("item").setExecutor(new Commanditem());
        getCommand("item").setAliases(Collections.singletonList("i"));
        getCommand("magic").setExecutor(magic);
        getCommand("more").setExecutor(new Commandmore());
        getCommand("msg").setExecutor(new Commandmsg());
        getCommand("msg").setAliases(Arrays.asList("tell", "t", "w", "whisper", "m"));
        getCommand("nearby").setExecutor(new Commandnearby());
        getCommand("night").setExecutor(new Commandnight());
        getCommand("noon").setExecutor(new Commandnoon());
        getCommand("nv").setExecutor(new Commandnv());
        getCommand("pack").setExecutor(new Commandpack());
        getCommand("ptime").setExecutor(new Commandptime());
        getCommand("pweather").setExecutor(new Commandpweather());
        getCommand("sethub").setExecutor(new Commandsethub());
        getCommand("setspawn").setExecutor(new Commandsetspawn());
        getCommand("setwarp").setExecutor(new Commandsetwarp());
        getCommand("sign").setExecutor(new Commandsign());
        getCommand("sign").setAliases(Collections.singletonList("s"));
        getCommand("smite").setExecutor(new Commandsmite());
        getCommand("spawn").setExecutor(new Commandspawn());
        getCommand("top").setExecutor(new Commandtop());
        getCommand("tp").setExecutor(new Commandtp());
        getCommand("tpa").setExecutor(new Commandtpa());
        getCommand("tpaccept").setExecutor(new Commandtpaccept());
        getCommand("tpdeny").setExecutor(new Commandtpdeny());
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
        pm.registerEvents(playerJoinAndLeave, this);
        pm.registerEvents(new SignChange(), this);
        pm.registerEvents(new ChunkUnload(), this);
        pm.registerEvents(new BlockEdit(), this);
        pm.registerEvents(new InventoryClick(), this);
        pm.registerEvents(new PlayerGameModeChange(), this);
        pm.registerEvents(new FoodLevel(), this);
        pm.registerEvents(new PlayerDropItem(), this);
        pm.registerEvents(stitch, this);
        pm.registerEvents(new PlayerInteract(), this);
        pm.registerEvents(new EntityDamage(), this);
        pm.registerEvents(blockChanger, this);
        pm.registerEvents(packManager, this);
        pm.registerEvents(new InventoryOpen(), this);
        fountainManager = new FountainManager();
        pm.registerEvents(fountainManager, this);
        pm.registerEvents(new PlayerCloseInventory(), this);
        pm.registerEvents(new ChairListener(), this);
        //pm.registerEvents(rideManager, this);
        if (getConfig().getBoolean("shooter-enabled")) {
            shooter = new Shooter(this);
            pm.registerEvents(shooter, this);
            MessageTimer.start();
        }
    }
}