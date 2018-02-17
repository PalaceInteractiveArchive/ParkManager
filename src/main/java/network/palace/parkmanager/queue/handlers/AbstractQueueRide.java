package network.palace.parkmanager.queue.handlers;

import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.handlers.FastPassData;
import network.palace.parkmanager.handlers.PlayerData;
import network.palace.parkmanager.handlers.RideCategory;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Marc
 * @since 8/25/17
 */
public abstract class AbstractQueueRide {
    @Getter protected List<Location> signs = new ArrayList<>();
    @Getter protected List<Location> fpsigns = new ArrayList<>();
    @Getter protected long lastSpawn = System.currentTimeMillis() / 1000;
    @Getter protected RideCategory category;
    @Getter protected boolean fpoff = false;

    @Getter protected boolean flat;

    private boolean frozen;

    protected long getTime() {
        return System.currentTimeMillis() / 1000;
    }

    public abstract boolean canStart();

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

    public abstract String getShortName();

    public abstract int getFastpassSize();

    public abstract void addFPSign(Location location, boolean b);

    public abstract void addSign(Location location, boolean b);

    public abstract void removeSign(Location loc);

    public abstract void removeFPSign(Location loc);

    public abstract void leaveFPQueue(CPlayer player);

    public abstract boolean isFPQueued(UUID uuid);

    public abstract boolean isQueued(UUID uuid);

    public abstract void leaveQueue(CPlayer player);

    public abstract boolean isFrozen();

    public abstract void joinFPQueue(CPlayer player);

    public abstract void joinQueue(CPlayer player);

    public abstract void leaveQueueSilent(CPlayer player);

    public abstract void ejectQueue();

    public abstract boolean toggleFastpass(CommandSender sender);

    public abstract boolean toggleFreeze();

    public abstract void setStation(Location location) throws IOException;

    public abstract int getTimeToNextRide();

    protected void chargeFastpass(final PlayerData data) {
        final FastPassData fpdata = data.getFastPassData();
        fpdata.setPass(category, fpdata.getPass(category) - 1);

        CPlayer p = Core.getPlayerManager().getPlayer(data.getUniqueId());
        if (p == null) return;

        Core.getMongoHandler().chargeFastPass(p.getUniqueId(), category.getDBName(), -1);
    }
}
