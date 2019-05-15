package network.palace.parkmanager;

import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.plugin.Plugin;
import network.palace.core.plugin.PluginInfo;
import network.palace.parkmanager.commands.BuildCommand;
import network.palace.parkmanager.dashboard.PacketListener;
import network.palace.parkmanager.dashboard.packets.parks.PacketImAPark;
import network.palace.parkmanager.handlers.Resort;
import network.palace.parkmanager.listeners.InventoryListener;
import network.palace.parkmanager.listeners.PlayerInteract;
import network.palace.parkmanager.listeners.PlayerJoinAndLeave;
import network.palace.parkmanager.storage.StorageManager;
import network.palace.parkmanager.utils.BuildUtil;
import network.palace.parkmanager.utils.InventoryUtil;
import network.palace.parkmanager.utils.MagicBandUtil;
import network.palace.parkmanager.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

@PluginInfo(name = "ParkManager", version = "3.0-1.13", depend = {"Core", "ProtocolLib", "WorldEdit"}, softdepend = {"RideManager", "ParkWarp"}, apiversion = "1.13")
public class ParkManager extends Plugin {
    @Getter public static ParkManager instance;
    @Getter private static BuildUtil buildUtil = new BuildUtil();
    @Getter private static InventoryUtil inventoryUtil = new InventoryUtil();
    @Getter private static MagicBandUtil magicBandUtil = new MagicBandUtil();
    @Getter private static StorageManager storageManager = new StorageManager();
    @Getter private static PlayerUtil playerUtil = new PlayerUtil();

    @Override
    protected void onPluginEnable() throws Exception {
        instance = this;
        registerListeners();
        registerCommands();
        storageManager.initialize();
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
        registerCommand(new BuildCommand());
    }

    public void registerListeners() {
        registerListener(new InventoryListener());
        registerListener(new PlayerInteract());
        registerListener(new PlayerJoinAndLeave());
        registerListener(new PacketListener());
    }

    public static Resort getResort() {
        return Resort.WDW;
    }
}