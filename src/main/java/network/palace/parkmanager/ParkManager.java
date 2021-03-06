package network.palace.parkmanager;

import lombok.Getter;
import network.palace.core.plugin.Plugin;
import network.palace.core.plugin.PluginInfo;
import network.palace.parkmanager.attractions.AttractionManager;
import network.palace.parkmanager.autograph.AutographManager;
import network.palace.parkmanager.commands.*;
import network.palace.parkmanager.food.FoodManager;
import network.palace.parkmanager.fpkiosk.FastPassKioskManager;
import network.palace.parkmanager.handlers.Resort;
import network.palace.parkmanager.leaderboard.LeaderboardManager;
import network.palace.parkmanager.listeners.*;
import network.palace.parkmanager.magicband.MagicBandManager;
import network.palace.parkmanager.outline.OutlineManager;
import network.palace.parkmanager.packs.PackManager;
import network.palace.parkmanager.queues.QueueManager;
import network.palace.parkmanager.queues.virtual.VirtualQueueManager;
import network.palace.parkmanager.shop.ShopManager;
import network.palace.parkmanager.shows.ShowMenuManager;
import network.palace.parkmanager.shows.schedule.ScheduleManager;
import network.palace.parkmanager.storage.StorageManager;
import network.palace.parkmanager.utils.*;
import network.palace.parkmanager.wardrobe.WardrobeManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;

@PluginInfo(name = "ParkManager", version = "3.3.9", depend = {"Core", "ProtocolLib", "WorldEdit", "Cosmetics", "ParkWarp"}, softdepend = {"RideManager", "Show"})
public class ParkManager extends Plugin {
    @Getter private static ParkManager instance;
    @Getter private static FileUtil fileUtil;
    @Getter private static ParkUtil parkUtil;

    @Getter private static AttractionManager attractionManager;
    @Getter private static AutographManager autographManager;
    @Getter private static BuildUtil buildUtil;
    @Getter private static ConfigUtil configUtil;
    @Getter private static DelayUtil delayUtil;
    @Getter private static FastPassKioskManager fastPassKioskManager;
    @Getter private static FoodManager foodManager;
    @Getter private static InventoryUtil inventoryUtil;
    @Getter private static LeaderboardManager leaderboardManager;
    @Getter private static MagicBandManager magicBandManager;
    @Getter private static OutlineManager outlineManager;
    @Getter private static PackManager packManager;
    @Getter private static PlayerUtil playerUtil;
    @Getter private static QueueManager queueManager;
    @Getter private static RideCounterUtil rideCounterUtil;
    @Getter private static ScheduleManager scheduleManager;
    @Getter private static ShopManager shopManager;
    @Getter private static ShowMenuManager showMenuManager;
    @Getter private static StorageManager storageManager;
    @Getter private static TeleportUtil teleportUtil;
    @Getter private static TimeUtil timeUtil;
    @Getter private static VirtualQueueManager virtualQueueManager;
    @Getter private static VisibilityUtil visibilityUtil;
    @Getter private static WardrobeManager wardrobeManager;

    @Override
    protected void onPluginEnable() throws Exception {
        instance = this;

        fileUtil = new FileUtil();
        parkUtil = new ParkUtil();

        attractionManager = new AttractionManager();
        autographManager = new AutographManager();
        buildUtil = new BuildUtil();
        configUtil = new ConfigUtil();
        delayUtil = new DelayUtil();
        fastPassKioskManager = new FastPassKioskManager();
        foodManager = new FoodManager();
        inventoryUtil = new InventoryUtil();
        leaderboardManager = new LeaderboardManager();
        magicBandManager = new MagicBandManager();
        outlineManager = new OutlineManager();
        packManager = new PackManager();
        playerUtil = new PlayerUtil();
        queueManager = new QueueManager();
        rideCounterUtil = new RideCounterUtil();
        scheduleManager = new ScheduleManager();
        showMenuManager = new ShowMenuManager();
        storageManager = new StorageManager();
        teleportUtil = new TeleportUtil();
        timeUtil = new TimeUtil();
        virtualQueueManager = new VirtualQueueManager();
        visibilityUtil = new VisibilityUtil();
        wardrobeManager = new WardrobeManager();
        shopManager = new ShopManager();

        storageManager.initialize();
        virtualQueueManager.initializeFromDatabase();

        registerListeners();
        registerCommands();
    }

    @Override
    protected void onPluginDisable() throws Exception {
        virtualQueueManager.clearQueues();
        Bukkit.getWorlds().forEach(world -> world.getEntities().stream().filter(e -> e instanceof Minecart).forEach(Entity::remove));
    }

    public void registerCommands() {
        registerCommand(new AddRideCounterCommand());
        registerCommand(new AttractionCommand());
        registerCommand(new AutographCommand());
        registerCommand(new BackCommand());
        registerCommand(new BroadcastCommand());
        registerCommand(new BroadcastGlobalCommand());
        registerCommand(new BuildCommand());
        registerCommand(new CosmeticsCommand());
        registerCommand(new DayCommand());
        registerCommand(new DelayCommand());
        registerCommand(new FoodCommand());
        registerCommand(new HealCommand());
        registerCommand(new InvSeeCommand());
        registerCommand(new ItemCommand());
        registerCommand(new KioskCommand());
        registerCommand(new LeaderboardCommand());
        registerCommand(new MoreCommand());
        registerCommand(new MuteChatCommand());
        registerCommand(new NightCommand());
        registerCommand(new NightVisionCommand());
        registerCommand(new NoonCommand());
        registerCommand(new OutfitCommand());
        registerCommand(new OutlineCommand());
        registerCommand(new PackCommand());
        registerCommand(new ParkConfigCommand());
        registerCommand(new PlayerTimeCommand());
        registerCommand(new PlayerWeatherCommand());
        registerCommand(new QueueCommand());
        registerCommand(new SetSignCommand());
        registerCommand(new ShopCommand());
        registerCommand(new ShowScheduleCommand());
        registerCommand(new ShowsCommand());
        registerCommand(new SignCommand());
        registerCommand(new SpawnCommand());
        registerCommand(new SpeedCommand());
        registerCommand(new TeleportCommand());
        registerCommand(new VirtualQueueCommand());
    }

    public void registerListeners() {
        registerListener(new BlockEdit());
        registerListener(new ChatListener());
        registerListener(new CommandListener());
        registerListener(new EntityDamage());
        registerListener(new FoodLevel());
        registerListener(new InventoryListener());
        registerListener(new PacketListener());
        registerListener(packManager);
        registerListener(new PlayerDropItem());
        registerListener(new PlayerGameModeChange());
        registerListener(new PlayerInteract());
        registerListener(new PlayerJoinAndLeave());
        registerListener(new PlayerTeleport());
        registerListener(new RideListener());
        registerListener(new SignChange());
    }

    public static Resort getResort() {
        return configUtil.getResort();
    }
}