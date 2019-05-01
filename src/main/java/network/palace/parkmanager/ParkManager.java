package network.palace.parkmanager;

import lombok.Getter;
import network.palace.core.plugin.Plugin;
import network.palace.core.plugin.PluginInfo;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

@PluginInfo(name = "ParkManager", version = "3.0-1.13", depend = {"Core", "ProtocolLib", "WorldEdit"}, softdepend = {"RideManager", "ParkWarp"})
public class ParkManager extends Plugin {
    @Getter public static ParkManager instance;

    @Override
    protected void onPluginEnable() throws Exception {
        instance = this;
        registerListeners();
        registerCommands();
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
    }

    public void registerListeners() {
    }
}