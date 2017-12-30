package network.palace.parkmanager;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.plugin.Plugin;
import network.palace.core.plugin.PluginInfo;
import network.palace.parkmanager.autograph.AutographManager;
import network.palace.parkmanager.blockchanger.BlockChanger;
import network.palace.parkmanager.commands.*;
import network.palace.parkmanager.dashboard.PacketListener;
import network.palace.parkmanager.designstation.DesignStation;
import network.palace.parkmanager.fastpasskiosk.FPKioskManager;
import network.palace.parkmanager.handlers.*;
import network.palace.parkmanager.hotels.HotelManager;
import network.palace.parkmanager.listeners.*;
import network.palace.parkmanager.outline.OutlineManager;
import network.palace.parkmanager.queue.QueueManager;
import network.palace.parkmanager.queue.handlers.AbstractQueueRide;
import network.palace.parkmanager.queue.handlers.PluginRideQueue;
import network.palace.parkmanager.queue.tot.TowerManager;
import network.palace.parkmanager.resourcepack.PackManager;
import network.palace.parkmanager.shooter.MessageTimer;
import network.palace.parkmanager.shooter.Shooter;
import network.palace.parkmanager.shop.ShopManager;
import network.palace.parkmanager.shop.WardrobeManager;
import network.palace.parkmanager.show.schedule.ScheduleManager;
import network.palace.parkmanager.stitch.Stitch;
import network.palace.parkmanager.storage.StorageManager;
import network.palace.parkmanager.tsm.ToyStoryMania;
import network.palace.parkmanager.uso.mib.MenInBlack;
import network.palace.parkmanager.uso.rrr.RipRideRockit;
import network.palace.parkmanager.utils.*;
import network.palace.parkmanager.watch.WatchTask;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@PluginInfo(name = "ParkManager", version = "2.3.0", depend = {"Core", "ProtocolLib", "WorldEdit"}, softdepend = {"RideManager"})
public class ParkManager extends Plugin implements Listener {
    public static ParkManager instance;
    private List<FoodLocation> foodLocations = new ArrayList<>();
    private HashMap<UUID, PlayerData> playerData = new HashMap<>();
    @Getter private Resort resort;
    @Getter private Stitch stitch;
    private List<Warp> warps = new ArrayList<>();
    private List<Ride> rides = new ArrayList<>();
    private List<Ride> attractions = new ArrayList<>();
    private List<Ride> meetandgreets = new ArrayList<>();
    @Getter @Setter private Location spawn;
    @Getter @Setter private Location hub;
    @Getter private PlayerJoinAndLeave playerJoinAndLeave;
    @Getter private boolean spawnOnJoin;
    @Getter private boolean crossServerInv;
    @Getter private boolean hotelServer;
    @Getter private boolean rideManager;
    @Getter private YamlConfiguration config = FileUtil.configurationYaml();
    @Getter private TeleportUtil teleportUtil;
    private List<String> joinMessages = config.getStringList("join-messages");
    private Map<UUID, String> userCache = new HashMap<>();
    @Getter private boolean ttcServer;
    @Getter private BlockChanger blockChanger;
    @Getter private PackManager packManager;
    @Getter private BandUtil bandUtil;
    @Getter private InventoryUtil inventoryUtil;
    @Getter private ShopManager shopManager;
    @Getter private HotelManager hotelManager;
    @Getter private QueueManager queueManager;
    @Getter private AutographManager autographManager;
    @Getter private StorageManager storageManager;
    @Getter private VisibilityUtil visibilityUtil;
    @Getter private Shooter shooter = null;
    @Getter private ScheduleManager scheduleManager;
    @Getter private WardrobeManager wardrobeManager;
    @Getter private FPKioskManager fpKioskManager;
    @Getter private ToyStoryMania toyStoryMania;
    @Getter private MenInBlack menInBlack;
    @Getter private RipRideRockit ripRideRockit;
    @Getter private OutlineManager outlineManager;
    @Setter private String activityURL;
    @Setter private String activityUser;
    @Setter private String activityPassword;

    @Override
    protected void onPluginEnable() throws Exception {
        instance = this;
        resort = Resort.fromString(FileUtil.getResort());
        stitch = new Stitch();
        packManager = new PackManager();
        autographManager = new AutographManager();
        queueManager = new QueueManager();
        ttcServer = Core.getServerType().equalsIgnoreCase("ttc");
        bandUtil = new BandUtil();
        storageManager = new StorageManager();
        inventoryUtil = new InventoryUtil();
        visibilityUtil = new VisibilityUtil();
        teleportUtil = new TeleportUtil();
        blockChanger = new BlockChanger();
        wardrobeManager = new WardrobeManager();
        playerJoinAndLeave = new PlayerJoinAndLeave();
        rideManager = Bukkit.getPluginManager().getPlugin("RideManager") != null;
        registerListeners();
        registerCommands();
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
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
        String sn = Core.getServerType();
        hotelServer = FileUtil.isHotelServer();
        hotelManager = new HotelManager();
        setupFoodLocations();
        setupRides();
        fpKioskManager = new FPKioskManager();
        scheduleManager = new ScheduleManager();
        outlineManager = new OutlineManager();
        //enablePixelator();
        setActivityURL(config.getString("activity.url"));
        setActivityUser(config.getString("activity.user"));
        setActivityPassword(config.getString("activity.password"));
        try {
            hub = new Location(Bukkit.getWorld(config.getString("hub.world")), config.getDouble("hub.x"),
                    config.getDouble("hub.y"), config.getDouble("hub.z"), config.getInt("hub.yaw"), config.getInt("hub.pitch"));
        } catch (Exception e) {
            Core.logMessage("ParkManager", "Could not load Hub location!");
        }
        try {
            spawn = new Location(Bukkit.getWorld(config.getString("spawn.world")), config.getDouble("spawn.x"),
                    config.getDouble("spawn.y"), config.getDouble("spawn.z"), config.getInt("spawn.yaw"),
                    config.getInt("spawn.pitch"));
        } catch (Exception e) {
            Core.logMessage("ParkManager", "Could not load Spawn location!");
        }
        spawnOnJoin = getConfig().getBoolean("spawn-on-join");
        crossServerInv = getConfig().getBoolean("transfer-inventories");
        packManager.initialize();
        DesignStation.initialize();
        long curr = System.currentTimeMillis();
        long time = (curr % 1000) / 50;
        Bukkit.getScheduler().runTaskTimer(this, new WatchTask(), time, 20L);
    }

    @Override
    protected void onPluginDisable() throws Exception {
        for (Player tp : Bukkit.getOnlinePlayers()) {
            tp.kickPlayer(Bukkit.getShutdownMessage());
        }
        hotelManager.serverStop();
        warps.clear();
        for (World world : Bukkit.getWorlds()) {
            world.getEntities().stream().filter(e -> e instanceof Minecart).forEach(org.bukkit.entity.Entity::remove);
        }
        //pixelator.rendererManager.disable();
    }

    public static ParkManager getInstance() {
        return instance;
    }

    public List<FoodLocation> getFoodLocations() {
        return ImmutableList.copyOf(foodLocations);
    }

    public List<String> getJoinMessages() {
        return ImmutableList.copyOf(joinMessages);
    }

    public HashMap<UUID, String> getUserCache() {
        return new HashMap<>(userCache);
    }

    public List<Warp> getWarps() {
        return ImmutableList.copyOf(warps);
    }

    public void clearWarps() {
        warps.clear();
    }

    public PlayerData getPlayerData(UUID uuid) {
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

    public void removeRides() {
        getRides().stream().filter(r -> r.getQueue() != null).forEach(r -> {
            r.getQueue().ejectQueue();
            if (r.getQueue() instanceof PluginRideQueue) {
                PluginRideQueue queue = (PluginRideQueue) r.getQueue();
                queue.getRide().despawn();
                network.palace.ridemanager.RideManager.getMovementUtil().removeRide(queue.getRide());
            }
        });
        rides.clear();
        attractions.clear();
    }

    public void setupRides() {
        removeRides();
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
            AbstractQueueRide queue = null;
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
                if (queue == null) {
                    Core.logMessage("Queue Manager", "Queue cannot be null for a meet and greet: " + s);
                    continue;
                }
                queue.toggleFreeze();
                Ride meetandgreet = new Ride(name, warp, type, data, RideCategory.SLOW, queue, s, true);
                meetandgreets.add(meetandgreet);
            }
        }
    }

    public Ride getRide(String name) {
        for (Ride ride : getRides()) {
            if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', ride.getDisplayName())).equals(name)) {
                return ride;
            }
        }
        return null;
    }

    public List<Ride> getRides() {
        return ImmutableList.copyOf(rides);
    }

    public List<Ride> getAttractions() {
        return ImmutableList.copyOf(attractions);
    }

    public List<Ride> getMeetAndGreets() {
        return ImmutableList.copyOf(meetandgreets);
    }

    public Ride getAttraction(String name) {
        for (Ride ride : new ArrayList<>(attractions)) {
            if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', ride.getDisplayName())).equals(name)) {
                return ride;
            }
        }
        return null;
    }

    public Ride getMeetAndGreet(String name) {
        for (Ride ride : new ArrayList<>(meetandgreets)) {
            if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', ride.getDisplayName())).equals(name)) {
                return ride;
            }
        }
        return null;
    }

    public void removeWarp(Warp warp) {
        warps.remove(warp);
    }

    public void addWarp(Warp warp) {
        warps.add(warp);
    }

    public void logActivity(Player player, String activity, String description) {
        try (Connection connection = DriverManager.getConnection(activityURL, activityUser, activityPassword)) {
            PreparedStatement sql = connection.prepareStatement("INSERT INTO activity (uuid, action, description) VALUES (?,?,?)");
            sql.setString(1, player.getUniqueId().toString());
            sql.setString(2, activity);
            sql.setString(3, description);
            sql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isSign(Location loc) {
        Block b = loc.getBlock();
        return b.getType().equals(Material.SIGN_POST) || b.getType().equals(Material.SIGN) ||
                b.getType().equals(Material.WALL_SIGN);
    }

    public boolean isResort(Resort r) {
        return resort.equals(r);
    }

    public void registerCommands() {
        registerCommand(new Commandautograph());
        registerCommand(new Commandb());
        registerCommand(new Commandback());
        registerCommand(new Commandbc());
        registerCommand(new Commandbuild());
        registerCommand(new Commandday());
        registerCommand(new Commanddelay());
        registerCommand(new Commandenderchest());
        registerCommand(new Commandfly());
        registerCommand(new Commandgive());
        registerCommand(new Commandhead());
        registerCommand(new Commandheal());
        registerCommand(new Commandhub());
        registerCommand(new Commandinvsee());
        registerCommand(new Commanditem());
        registerCommand(new Commandmagic());
        registerCommand(new Commandmc());
        registerCommand(new Commandmore());
        registerCommand(new Commandmsg());
        registerCommand(new Commandnearby());
        registerCommand(new Commandnight());
        registerCommand(new Commandnoon());
        registerCommand(new Commandnv());
        registerCommand(new Commandoutline());
        registerCommand(new Commandpack());
        registerCommand(new Commandptime());
        registerCommand(new Commandpweather());
        registerCommand(new Commandsethub());
        registerCommand(new Commandsetspawn());
        registerCommand(new Commandsign());
        registerCommand(new Commandsmite());
        registerCommand(new Commandspawn());
        registerCommand(new Commandtp());
        registerCommand(new Commandupdate());
        registerCommand(new CommandCosmetics());
        if (isResort(Resort.USO)) {
            registerCommand(new Commanduso());
        }
    }

    public void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        registerListener(this);
        registerListener(new BlockEdit());
        registerListener(new ChatListener());
        registerListener(new ChunkUnload());
        registerListener(new EntityDamage());
        registerListener(new FoodLevel());
        registerListener(new InventoryClick());
        registerListener(new InventoryOpen());
        registerListener(new PlayerCloseInventory());
        registerListener(new PlayerDropItem());
        registerListener(new PlayerGameModeChange());
        registerListener(new PlayerInteract());
        registerListener(playerJoinAndLeave);
        registerListener(queueManager);
        registerListener(new SignChange());
        registerListener(new PacketListener());
        registerListener(new ResourceListener());
        if (rideManager) {
            registerListener(new RideListener());
        }
        switch (resort) {
            case WDW: {
                if (Core.getServerType().equals("MK")) {
                    registerListener(stitch);
                }
                if (getConfig().getBoolean("shooter-enabled")) {
                    shooter = new Shooter(this);
                    registerListener(shooter);
                    MessageTimer.start();
                }
                if (Core.getServerType().equalsIgnoreCase("dhs")) {
                    registerListener(new TowerManager(Bukkit.getWorlds().get(0)));
                    toyStoryMania = new ToyStoryMania();
                    registerListener(toyStoryMania);
                }
                break;
            }
            case USO: {
                menInBlack = new MenInBlack();
                registerListener(menInBlack);
                ripRideRockit = new RipRideRockit();
                break;
            }
        }
    }

    public void addToUserCache(UUID uuid, String name) {
        userCache.put(uuid, name);
    }

    public void addPlayerData(PlayerData data) {
        playerData.put(data.getUniqueId(), data);
    }

    public void removePlayerData(UUID uuid) {
        playerData.remove(uuid);
    }
}