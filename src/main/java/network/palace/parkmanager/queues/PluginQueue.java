package network.palace.parkmanager.queues;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.economy.CurrencyType;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.handlers.QueueType;
import network.palace.parkmanager.utils.FileUtil;
import network.palace.ridemanager.RideManager;
import network.palace.ridemanager.handlers.ride.Ride;
import network.palace.ridemanager.handlers.ride.RideType;
import network.palace.ridemanager.handlers.ride.file.FileRide;
import network.palace.ridemanager.handlers.ride.flat.AerialCarouselRide;
import network.palace.ridemanager.handlers.ride.flat.CarouselRide;
import network.palace.ridemanager.handlers.ride.flat.TeacupsRide;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class PluginQueue extends Queue {
    private final CurrencyType currencyType;
    private final int currencyAmount;
    private final int honorAmount;
    private final int achievementId;
    private boolean flat = false;

    private Ride ride;

    private List<CPlayer> atStation = new ArrayList<>();
    private int stationCountdown = -1;

    public PluginQueue(int id, UUID uuid, String name, String warp, int groupSize, int delay, boolean open, Location station, List<QueueSign> signs,
                       Location exit, CurrencyType currencyType, int currencyAmount, int honorAmount, int achievementId, JsonObject rideConfig) {
        super(id, uuid, name, warp, groupSize, delay, open, station, signs);
        this.currencyType = currencyType;
        this.currencyAmount = currencyAmount;
        this.honorAmount = honorAmount;
        this.achievementId = achievementId;

        RideType type = RideType.fromString(rideConfig.get("rideType").getAsString());
        switch (type) {
            case FILE:
                ride = new FileRide(name, name, groupSize, delay, exit, rideConfig.get("fileName").getAsString(),
                        currencyType, currencyAmount, honorAmount, achievementId);
                break;
            case TEACUPS:
                ride = new TeacupsRide(name, name, delay, exit, FileUtil.getLocation(rideConfig.getAsJsonObject("center")),
                        currencyType, currencyAmount, honorAmount, achievementId);
                flat = true;
                break;
            case CAROUSEL:
                ride = new CarouselRide(name, name, delay, exit, FileUtil.getLocation(rideConfig.getAsJsonObject("center")),
                        currencyType, currencyAmount, honorAmount, achievementId);
                flat = true;
                break;
            case AERIAL_CAROUSEL:
                ride = new AerialCarouselRide(name, name, delay, exit, FileUtil.getLocation(rideConfig.getAsJsonObject("center")),
                        currencyType, currencyAmount, honorAmount, achievementId, rideConfig.get("aerialRadius").getAsDouble(),
                        rideConfig.get("supportRadius").getAsDouble(), rideConfig.get("small").getAsBoolean(),
                        rideConfig.get("supportAngle").getAsDouble(), rideConfig.get("height").getAsDouble(), rideConfig.get("movein").getAsDouble());
        }
        if (ride != null) RideManager.getMovementUtil().addRide(ride);
    }

    @Override
    public void tick(long currentTime) {
        super.tick(currentTime);
        if (stationCountdown < 0) return;
        if (stationCountdown > 0) {
            //Countdown to dispatch
            atStation.forEach(p -> p.getActionBar().show(ChatColor.GREEN + "Ride starting in " + stationCountdown + " second" + TextUtil.pluralize(stationCountdown)));
        } else {
            //Dispatch
            atStation.forEach(p -> p.getActionBar().show(ChatColor.GREEN + "Ride starting now..."));
            ride.start(atStation);
            atStation.clear();
        }
        stationCountdown--;
    }

    @Override
    protected void handleSpawn(List<CPlayer> players) {
        stationCountdown = 10;
        atStation.clear();
        atStation.addAll(players);
    }

    @Override
    public QueueType getQueueType() {
        if (ride instanceof CarouselRide) {
            return QueueType.CAROUSEL;
        }
        return null;
    }
}
