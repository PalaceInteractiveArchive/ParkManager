package us.mcmagic.parkmanager.pixelator.manager;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import us.mcmagic.parkmanager.ParkManager;

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
