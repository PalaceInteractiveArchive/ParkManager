package us.mcmagic.parkmanager.queue.tot;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import us.mcmagic.parkmanager.handlers.RideCategory;
import us.mcmagic.parkmanager.queue.QueueRide;
import us.mcmagic.parkmanager.queue.tasks.SpawnBlockSetTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 12/30/15
 */
public class TowerPreShow extends QueueRide {
    private final Location station1;
    private final Location station2;
    private final Location spawner1;
    private final Location spawner2;
    private final Block spawn1;
    private final Block spawn2;
    private int stationNumber = 2;

    public TowerPreShow(String name, Location station1, Location station2, Location spawner1, Location spawner2,
                        int delay, int amountOfRiders, String warp) {
        super(name, station1, spawner1, delay, amountOfRiders, warp, RideCategory.SLOW, "totpre");
        this.station1 = station1;
        this.station2 = station2;
        this.spawner1 = spawner1;
        this.spawner2 = spawner2;
        spawn1 = spawner1.getBlock();
        spawn2 = spawner2.getBlock();
    }

    @Override
    public void moveToStation() {
        if (frozen) {
            return;
        }
        changeStation();
        List<UUID> fullList = getQueue();
        Location loc = getStation(stationNumber);
        if (fullList.size() >= amountOfRiders) {
            for (int i = 0; i < amountOfRiders; i++) {
                Player tp = Bukkit.getPlayer(fullList.get(0));
                if (tp == null) {
                    i--;
                    continue;
                }
                tp.teleport(loc);
                leaveQueueSilent(tp);
                fullList.remove(tp.getUniqueId());
            }
            updateSigns();
            return;
        }
        for (UUID uuid : new ArrayList<>(fullList)) {
            Player tp = Bukkit.getPlayer(uuid);
            if (tp == null) {
                continue;
            }
            tp.teleport(loc);
            leaveQueueSilent(tp);
            fullList.remove(tp.getUniqueId());
        }
    }

    @Override
    public Block getSpawnerBlock() {
        switch (stationNumber) {
            case 1:
                return spawn1;
            case 2:
                return spawn2;
        }
        return spawn1;
    }

    private void changeStation() {
        switch (stationNumber) {
            case 1:
                stationNumber = 2;
                break;
            case 2:
                stationNumber = 1;
                break;
            default:
                stationNumber = 1;
        }
    }

    public Location getStation(int num) {
        switch (num) {
            case 1:
                return station1;
            case 2:
                return station2;
            default:
                return station1;
        }
    }

    public Location getSpawner(int num) {
        switch (num) {
            case 1:
                return spawner1;
            case 2:
                return spawner2;
            default:
                return spawner1;
        }
    }
}