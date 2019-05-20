package network.palace.parkmanager;

import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.plugin.Plugin;
import network.palace.core.plugin.PluginInfo;
import network.palace.parkmanager.autograph.AutographManager;
import network.palace.parkmanager.commands.AutographCommand;
import network.palace.parkmanager.commands.BuildCommand;
import network.palace.parkmanager.commands.SignCommand;
import network.palace.parkmanager.dashboard.PacketListener;
import network.palace.parkmanager.dashboard.packets.parks.PacketImAPark;
import network.palace.parkmanager.handlers.Resort;
import network.palace.parkmanager.listeners.InventoryListener;
import network.palace.parkmanager.listeners.PlayerGameModeChange;
import network.palace.parkmanager.listeners.PlayerInteract;
import network.palace.parkmanager.listeners.PlayerJoinAndLeave;
import network.palace.parkmanager.storage.StorageManager;
import network.palace.parkmanager.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

@PluginInfo(name = "ParkManager", version = "3.0-1.13", depend = {"Core", "ProtocolLib", "WorldEdit"}, softdepend = {"RideManager", "ParkWarp"}, apiversion = "1.13")
public class ParkManager extends Plugin {
    @Getter public static ParkManager instance;
    @Getter private static AutographManager autographManager;
    @Getter private static BuildUtil buildUtil;
    @Getter private static InventoryUtil inventoryUtil;
    @Getter private static MagicBandUtil magicBandUtil;
    @Getter private static StorageManager storageManager;
    @Getter private static PlayerUtil playerUtil;
    @Getter private static TimeUtil timeUtil;

    @Override
    protected void onPluginEnable() throws Exception {
        instance = this;

        autographManager = new AutographManager();
        buildUtil = new BuildUtil();
        inventoryUtil = new InventoryUtil();
        magicBandUtil = new MagicBandUtil();
        storageManager = new StorageManager();
        playerUtil = new PlayerUtil();
        timeUtil = new TimeUtil();

        storageManager.initialize();

        registerListeners();
        registerCommands();
        Core.getDashboardConnection().send(new PacketImAPark());
    }

    @Override
    protected void onPluginDisable() throws Exception {
        for (Player tp : Bukkit.getOnlinePlayers()) {
            tp.kickPlayer(Bukkit.getShutdownMessage());
        }
        for (World world : Bukkit.getWorlds()) {
            world.getEntities().stream().filter(e -> e instanceof Minecart).forEach(Entity::remove);
        }
    }

    public void registerCommands() {
        registerCommand(new AutographCommand());
        registerCommand(new BuildCommand());
        registerCommand(new SignCommand());
    }

    public void registerListeners() {
        registerListener(new InventoryListener());
        registerListener(new PlayerGameModeChange());
        registerListener(new PlayerInteract());
        registerListener(new PlayerJoinAndLeave());
        registerListener(new PacketListener());
    }

    public static Resort getResort() {
        return Resort.WDW;
    }
}