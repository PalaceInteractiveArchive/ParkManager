package network.palace.parkmanager.queue.handlers;

import lombok.Getter;
import network.palace.parkmanager.handlers.RideCategory;
import network.palace.ridemanager.RideManager;
import network.palace.ridemanager.handlers.Ride;
import network.palace.ridemanager.handlers.RideType;
import network.palace.ridemanager.handlers.TeacupsRide;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author Marc
 * @since 8/25/17
 */
public class PluginRideQueue extends AbstractQueueRide {
    @Getter private final String name;
    @Getter private final String shortName;
    @Getter private final Ride ride;
    @Getter private final Location station;
    @Getter private final int amount;
    @Getter private final String warp;
    @Getter private final RideCategory category;
    @Getter private final RideType type;

    public PluginRideQueue(String name, String displayName, Location station, Location exit, int delay, int amount,
                           String warp, RideCategory category, RideType type, YamlConfiguration config) {
        this.name = displayName;
        this.shortName = name;
        this.station = station;
        this.amount = amount;
        this.warp = warp;
        this.category = category;
        this.type = type;
        switch (type) {
            case TEACUPS:
                Location center = RideManager.parseLocation(config.getConfigurationSection("ride." + name + ".queue.center"));
                ride = new TeacupsRide(name, displayName, delay, exit, center);
                break;
            default:
                ride = null;
        }
    }

    @Override
    public String approximateWaitTime() {
        return "";
    }

    @Override
    public int getDelay() {
        return 0;
    }

    @Override
    public int getAmountOfRiders() {
        return 0;
    }

    @Override
    public int getQueueSize() {
        return 0;
    }

    @Override
    public int getPosition(UUID uuid) {
        return 0;
    }

    @Override
    public void updateSigns() {

    }

    @Override
    public List<UUID> getQueue() {
        return null;
    }

    @Override
    public List<UUID> getFPQueue() {
        return null;
    }

    @Override
    public String getWaitFor(UUID uuid) {
        return null;
    }

    @Override
    public void moveToStation() {

    }

    @Override
    public void spawn() {

    }

    @Override
    public int getFastpassSize() {
        return 0;
    }

    @Override
    public void addFPSign(Location location, boolean b) {

    }

    @Override
    public void addSign(Location location, boolean b) {

    }

    @Override
    public List<Location> getSigns() {
        return null;
    }

    @Override
    public void removeSign(Location loc) {

    }

    @Override
    public void removeFPSign(Location loc) {

    }

    @Override
    public List<Location> getFPsigns() {
        return null;
    }

    @Override
    public void leaveFPQueue(Player player) {

    }

    @Override
    public boolean isFPQueued(UUID uuid) {
        return false;
    }

    @Override
    public boolean isQueued(UUID uuid) {
        return false;
    }

    @Override
    public void leaveQueue(Player player) {

    }

    @Override
    public boolean isFrozen() {
        return false;
    }

    @Override
    public void joinFPQueue(Player player) {

    }

    @Override
    public void joinQueue(Player player) {

    }

    @Override
    public void leaveQueueSilent(Player player) {

    }

    @Override
    public void ejectQueue() {

    }

    @Override
    public boolean toggleFastpass(CommandSender sender) {
        return false;
    }

    @Override
    public void setPaused(boolean b) {

    }

    @Override
    public boolean toggleFreeze() {
        return false;
    }

    @Override
    public void setStation(Location location) throws IOException {

    }

    @Override
    public void setSpawner(Location location) throws IOException {

    }
}
