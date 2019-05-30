package network.palace.parkmanager;

import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.plugin.Plugin;
import network.palace.core.plugin.PluginInfo;
import network.palace.parkmanager.attractions.AttractionManager;
import network.palace.parkmanager.autograph.AutographManager;
import network.palace.parkmanager.commands.*;
import network.palace.parkmanager.dashboard.PacketListener;
import network.palace.parkmanager.dashboard.packets.parks.PacketImAPark;
import network.palace.parkmanager.food.FoodManager;
import network.palace.parkmanager.handlers.Resort;
import network.palace.parkmanager.leaderboard.LeaderboardManager;
import network.palace.parkmanager.listeners.*;
import network.palace.parkmanager.magicband.MagicBandManager;
import network.palace.parkmanager.outline.OutlineManager;
import network.palace.parkmanager.queues.QueueManager;
import network.palace.parkmanager.showschedule.ScheduleManager;
import network.palace.parkmanager.storage.StorageManager;
import network.palace.parkmanager.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;

@PluginInfo(name = "ParkManager", version = "3.0-1.13", depend = {"Core", "ProtocolLib", "WorldEdit", "Cosmetics", "ParkWarp"}, softdepend = {"RideManager"}, apiversion = "1.13")
public class ParkManager extends Plugin {
    @Getter public static ParkManager instance;
    @Getter private static AttractionManager attractionManager;
    @Getter private static AutographManager autographManager;
    @Getter private static BuildUtil buildUtil;
    @Getter private static DelayUtil delayUtil;
    @Getter private static FileUtil fileUtil;
    @Getter private static FoodManager foodManager;
    @Getter private static InventoryUtil inventoryUtil;
    @Getter private static LeaderboardManager leaderboardManager;
    @Getter private static MagicBandManager magicBandManager;
    @Getter private static OutlineManager outlineManager;
    @Getter private static PlayerUtil playerUtil;
    @Getter private static QueueManager queueManager;
    @Getter private static ScheduleManager scheduleManager;
    @Getter private static StorageManager storageManager;
    @Getter private static TeleportUtil teleportUtil;
    @Getter private static TimeUtil timeUtil;
    @Getter private static VisibilityUtil visibilityUtil;

    @Override
    protected void onPluginEnable() throws Exception {
        instance = this;

        fileUtil = new FileUtil();

        attractionManager = new AttractionManager();
        autographManager = new AutographManager();
        buildUtil = new BuildUtil();
        delayUtil = new DelayUtil();
        foodManager = new FoodManager();
        inventoryUtil = new InventoryUtil();
        leaderboardManager = new LeaderboardManager();
        magicBandManager = new MagicBandManager();
        outlineManager = new OutlineManager();
        playerUtil = new PlayerUtil();
        queueManager = new QueueManager();
        scheduleManager = new ScheduleManager();
        storageManager = new StorageManager();
        teleportUtil = new TeleportUtil();
        timeUtil = new TimeUtil();
        visibilityUtil = new VisibilityUtil();

        storageManager.initialize();

        registerListeners();
        registerCommands();
        Core.getDashboardConnection().send(new PacketImAPark());
    }

    @Override
    protected void onPluginDisable() throws Exception {
        Bukkit.getWorlds().forEach(world -> world.getEntities().stream().filter(e -> e instanceof Minecart).forEach(Entity::remove));
    }

    public void registerCommands() {
        registerCommand(new AttractionCommand());
        registerCommand(new AutographCommand());
        registerCommand(new BackCommand());
        registerCommand(new BroadcastCommand());
        registerCommand(new BroadcastGlobalCommand());
        registerCommand(new BuildCommand());
        registerCommand(new CosmeticsCommand());
        registerCommand(new DayCommand());
        registerCommand(new DelayCommand());
        registerCommand(new FlyCommand());
        registerCommand(new FoodCommand());
        registerCommand(new HealCommand());
        registerCommand(new InvSeeCommand());
        registerCommand(new ItemCommand());
        registerCommand(new LeaderboardCommand());
        registerCommand(new MoreCommand());
        registerCommand(new MsgCommand());
        registerCommand(new MuteChatCommand());
        registerCommand(new NightCommand());
        registerCommand(new NightVisionCommand());
        registerCommand(new NoonCommand());
        registerCommand(new OutlineCommand());
        registerCommand(new PlayerTimeCommand());
        registerCommand(new PlayerWeatherCommand());
        registerCommand(new QueueCommand());
        registerCommand(new ShowScheduleCommand());
        registerCommand(new SignCommand());
        registerCommand(new SpeedCommand());
        registerCommand(new TeleportCommand());
    }

    public void registerListeners() {
        registerListener(new BlockEdit());
        registerListener(new InventoryListener());
        registerListener(new PlayerGameModeChange());
        registerListener(new PlayerInteract());
        registerListener(new PlayerJoinAndLeave());
        registerListener(new PacketListener());
        registerListener(new SignChange());
    }

    public static Resort getResort() {
        return Resort.WDW;
    }
}