package network.palace.parkmanager.pixelator.manager;

import network.palace.parkmanager.ParkManager;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Random;

public abstract class Manager implements Listener {

    protected static final Random RANDOM = new Random();
    protected ParkManager plugin;


    public Manager(ParkManager plugin) {
        this.plugin = plugin;
    }

    public abstract boolean initialize();

    public abstract void disable();

    public void reload() {
        this.disable();
        this.initialize();
    }

    protected void registerListener() {
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    protected void unregisterListener() {
        HandlerList.unregisterAll();
    }
}
