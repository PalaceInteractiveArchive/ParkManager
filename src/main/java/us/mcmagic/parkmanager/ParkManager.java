package us.mcmagic.parkmanager;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.parkmanager.autograph.AutographManager;
import us.mcmagic.parkmanager.blockchanger.BlockChanger;
import us.mcmagic.parkmanager.chairs.ArrowFactory;
import us.mcmagic.parkmanager.chairs.ChairListener;
import us.mcmagic.parkmanager.chairs.ChairManager;
import us.mcmagic.parkmanager.chairs.IArrowFactory;
import us.mcmagic.parkmanager.commands.*;
import us.mcmagic.parkmanager.designstation.DesignStation;
import us.mcmagic.parkmanager.fastpasskiosk.FPKioskManager;
import us.mcmagic.parkmanager.handlers.*;
import us.mcmagic.parkmanager.hotels.HotelManager;
import us.mcmagic.parkmanager.listeners.*;
import us.mcmagic.parkmanager.pixelator.Pixelator;
import us.mcmagic.parkmanager.queue.QueueManager;
import us.mcmagic.parkmanager.queue.QueueRide;
import us.mcmagic.parkmanager.queue.tot.TowerManager;
import us.mcmagic.parkmanager.resourcepack.PackManager;
import us.mcmagic.parkmanager.ridemanager.RideManager;
import us.mcmagic.parkmanager.shooter.MessageTimer;
import us.mcmagic.parkmanager.shooter.Shooter;
import us.mcmagic.parkmanager.shop.ShopManager;
import us.mcmagic.parkmanager.shop.WardrobeManager;
import us.mcmagic.parkmanager.show.ArmorStandManager;
import us.mcmagic.parkmanager.show.FountainManager;
import us.mcmagic.parkmanager.show.actions.SchematicAction;
import us.mcmagic.parkmanager.show.schedule.ScheduleManager;
import us.mcmagic.parkmanager.show.ticker.Ticker;
import us.mcmagic.parkmanager.stitch.Stitch;
import us.mcmagic.parkmanager.storage.StorageManager;
import us.mcmagic.parkmanager.tsm.ToyStoryMania;
import us.mcmagic.parkmanager.utils.*;
import us.mcmagic.parkmanager.watch.WatchTask;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ParkManager extends JavaPlugin implements Listener {
    public static ParkManager instance;
    public static List<FoodLocation> foodLocations = new ArrayList<>();
    public static HashMap<UUID, PlayerData> playerData = new HashMap<>();
    public static Stitch stitch;
    //public static UniverseEnergyRide universeEnergyRide;
    public static List<Warp> warps = new ArrayList<>();
    public static List<Ride> rides = new ArrayList<>();
    public static List<Ride> attractions = new ArrayList<>();
    public static List<Ride> meetandgreets = new ArrayList<>();
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
    public static Map<UUID, String> userCache = new HashMap<>();
    public static boolean ttcServer;
    public static BlockChanger blockChanger;
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
    public static VisibilityUtil visibilityUtil;
    public static Shooter shooter = null;
    public static ChairManager chairManager;
    public static IArrowFactory chairFactory;
    public static ScheduleManager scheduleManager;
    public static WardrobeManager wardrobeManager;
    public static Pixelator pixelator;
    public static FPKioskManager fpKioskManager;
    public static ToyStoryMania toyStoryMania;

    public void onEnable() {
        instance = this;
        SqlUtil.initialize();
        log("Initializing ParkManager...");
        rideManager = new RideManager();
        stitch = new Stitch();
        packManager = new PackManager();
        //universeEnergyRide = new UniverseEnergyRide();
        autographManager = new AutographManager();
        queueManager = new QueueManager();
        ttcServer = MCMagicCore.getMCMagicConfig().serverName.contains("TTC");
        bandUtil = new BandUtil();
        storageManager = new StorageManager();
        inventoryUtil = new InventoryUtil();
        visibilityUtil = new VisibilityUtil();
        teleportUtil = new TeleportUtil();
        itemUtil = new ItemUtil();
        blockChanger = new BlockChanger();
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
        WarpUtil.refreshWarps();
        shopManager = new ShopManager();
        String sn = MCMagicCore.getMCMagicConfig().serverName;
        hotelServer = sn.equals("Resorts") || sn.equals("DCL");
        hotelManager = new HotelManager();
        setupFoodLocations();
        setupRides();
        fpKioskManager = new FPKioskManager();
        scheduleManager = new ScheduleManager();
        //enablePixelator();
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
        packManager.initialize();
        DesignStation.initialize();
        long curr = System.currentTimeMillis();
        long time = (curr % 1000) / 50;
        Bukkit.getScheduler().runTaskTimer(this, new WatchTask(), time, 20L);
        log("ParkManager Initialized!");
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
            world.getEntities().stream().filter(e -> e instanceof Minecart).forEach(org.bukkit.entity.Entity::remove);
        }
        //pixelator.rendererManager.disable();
    }

    public static ParkManager getInstance() {
        return instance;
    }

    public static List<Warp> getWarps() {
        return new ArrayList<>(warps);
    }

    public static void clearWarps() {
        warps.clear();
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
        YamlConfiguration config = FileUtil.menuYaml();
        List<String> locations = config.getStringList("food-names");
        for (String location : locations) {
            String name = config.getString("food." + location + ".name");
            String warp = config.getString("food." + location + ".warp");
            int type = config.getInt("food." + location + ".type");
            byte data;
            if (config.contains("food." + location + ".data")) {
                data = (byte) config.getInt("food." + location + ".data");
            } else {
                data = 0;
            }
            FoodLocation loc = new FoodLocation(name, warp, type, data);
            foodLocations.add(loc);
        }
    }

    public void setupRides() {
        getRides().stream().filter(r -> r.getQueue() != null).forEach(r -> r.getQueue().ejectQueue());
        rides.clear();
        attractions.clear();
        YamlConfiguration config = FileUtil.menuYaml();
        List<String> locations = config.getStringList("ride-names");
        for (String s : locations) {
            String name = config.getString("ride." + s + ".name");
            String warp = config.getString("ride." + s + ".warp");
            int type = config.getInt("ride." + s + ".type");
            byte data;
            if (config.contains("ride." + s + ".data")) {
                data = (byte) config.getInt("ride." + s + ".data");
            } else {
                data = 0;
            }
            RideCategory category = RideCategory.fromString(config.getString("ride." + s + ".category"));
            QueueRide queue = null;
            if (config.getBoolean("ride." + s + ".has-queue")) {
                queue = queueManager.createQueue(s, config);
            }
            String otype = config.getString("ride." + s + ".otype");
            if (otype.equalsIgnoreCase("ride")) {
                Ride ride = new Ride(name, warp, type, data, category, queue, s, config.getBoolean("ride." + s + ".has-item"));
                rides.add(ride);
            } else if (otype.equalsIgnoreCase("attraction")) {
                Ride attraction = new Ride(name, warp, type, data, category, queue, s, config.getBoolean("ride." + s + ".has-item"));
                attractions.add(attraction);
            } else if (otype.equalsIgnoreCase("meetandgreet")) {
                queue.toggleFreeze();
                Ride meetandgreet = new Ride(name, warp, type, data, RideCategory.SLOW, queue, s, true);
                meetandgreets.add(meetandgreet);
            }
        }
    }

    public static Ride getRide(String name) {
        for (Ride ride : getRides()) {
            if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', ride.getDisplayName())).equals(name)) {
                return ride;
            }
        }
        return null;
    }

    public static Ride getRide2(String shortName) {
        for (Ride ride : getRides()) {
            if (ride.getShortName().equalsIgnoreCase(shortName)) {
                return ride;
            }
        }
        return null;
    }

    public static List<Ride> getRides() {
        return new ArrayList<>(rides).stream().collect(Collectors.toList());
    }

    public static List<Ride> getAttractions() {
        return new ArrayList<>(attractions).stream().collect(Collectors.toList());
    }

    public static List<Ride> getMeetAndGreets() {
        return new ArrayList<>(meetandgreets).stream().collect(Collectors.toList());
    }

    public static Ride getAttraction(String name) {
        for (Ride ride : new ArrayList<>(attractions)) {
            if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', ride.getDisplayName())).equals(name)) {
                return ride;
            }
        }
        return null;
    }

    public static Ride getMeetAndGreet(String name) {
        for (Ride ride : new ArrayList<>(meetandgreets)) {
            if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', ride.getDisplayName())).equals(name)) {
                return ride;
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

    public static void logActivity(Player player, String activity, String description) {
        try (Connection connection = DriverManager.getConnection(MCMagicCore.getMCMagicConfig().sqlConnectionUrl
                        .replace("MainServer", "mymcmagic"), MCMagicCore.getMCMagicConfig().sqlUser,
                MCMagicCore.getMCMagicConfig().sqlPassword)) {
            PreparedStatement sql = connection.prepareStatement("INSERT INTO activity (uuid, action, description) VALUES (?,?,?)");
            sql.setString(1, player.getUniqueId().toString());
            sql.setString(2, activity);
            sql.setString(3, description);
            sql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void registerCommands() {
        Commandmagic magic = new Commandmagic();
        getCommand("autograph").setExecutor(new Commandautograph());
        getCommand("autograph").setAliases(Arrays.asList("a", "auto"));
        getCommand("b").setExecutor(new Commandb());
        getCommand("back").setExecutor(new Commandback());
        //getCommand("bb8").setExecutor(new Commandbb8());
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
        getCommand("mc").setExecutor(new Commandmc());
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
        if (MCMagicCore.getMCMagicConfig().serverName.equals("MK")) {
            pm.registerEvents(stitch, this);
        }
        pm.registerEvents(new PlayerInteract(), this);
        pm.registerEvents(new EntityDamage(), this);
        //pm.registerEvents(blockChanger, this);
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
        if (MCMagicCore.getMCMagicConfig().serverName.equalsIgnoreCase("dhs")) {
            pm.registerEvents(new TowerManager(Bukkit.getWorlds().get(0)), this);
            toyStoryMania = new ToyStoryMania();
            pm.registerEvents(toyStoryMania, this);
        }
    }
}