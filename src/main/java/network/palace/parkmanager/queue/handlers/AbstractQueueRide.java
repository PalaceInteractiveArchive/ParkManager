package network.palace.parkmanager.queue.handlers;

import network.palace.parkmanager.handlers.RideCategory;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Marc
 * @since 8/25/17
 */
public abstract class AbstractQueueRide {
    protected List<Location> signs = new ArrayList<>();
    protected List<Location> fpsigns = new ArrayList<>();

    protected boolean flat;

    private boolean frozen;

    public abstract String getName();

    public abstract String approximateWaitTime();

    public abstract int getDelay();

    public abstract int getAmount();

    public abstract int getQueueSize();

    public abstract int getFPQueueSize();

    public abstract String getWarp();

    public abstract int getPosition(UUID uuid);

    public abstract void updateSigns();

    public abstract List<UUID> getQueue();

    public abstract List<UUID> getFPQueue();

    public abstract String getWaitFor(UUID uuid);

    public abstract void moveToStation();

    public abstract void spawn();

    public abstract String getShortName();

    public abstract int getFastpassSize();

    public abstract void addFPSign(Location location, boolean b);

    public abstract void addSign(Location location, boolean b);

    public abstract List<Location> getSigns();

    public abstract void removeSign(Location loc);

    public abstract void removeFPSign(Location loc);

    public abstract List<Location> getFPsigns();

    public abstract void leaveFPQueue(Player player);

    public abstract boolean isFPQueued(UUID uuid);

    public abstract boolean isQueued(UUID uuid);

    public abstract void leaveQueue(Player player);

    public abstract boolean isFrozen();

    public abstract RideCategory getCategory();

    public abstract void joinFPQueue(Player player);

    public abstract void joinQueue(Player player);

    public abstract void leaveQueueSilent(Player player);

    public abstract void ejectQueue();

    public abstract boolean toggleFastpass(CommandSender sender);

    public abstract void setPaused(boolean b);

    public abstract boolean toggleFreeze();

    public abstract void setStation(Location location) throws IOException;

    public abstract void setSpawner(Location location) throws IOException;

    public abstract int getTimeToNextRide();
}
