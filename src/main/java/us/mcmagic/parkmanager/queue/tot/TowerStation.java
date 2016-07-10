package us.mcmagic.parkmanager.queue.tot;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.RideCategory;
import us.mcmagic.parkmanager.queue.QueueRide;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 2/19/16
 */
public class TowerStation extends QueueRide {
    private final Location station1;
    private final Location station2;
    private final Location station3;
    private final Location station4;
    private final Location spawner1;
    private final Location spawner2;
    private final Location spawner3;
    private final Location spawner4;
    private final Block spawn1;
    private final Block spawn2;
    private final Block spawn3;
    private final Block spawn4;
    private int stationNumber = 4;

    public TowerStation(String name, Location station1, Location station2, Location station3, Location station4,
                        Location spawner1, Location spawner2, Location spawner3, Location spawner4, int delay,
                        int amountOfRiders, String warp) {
        super(name, station1, spawner1, delay, amountOfRiders, warp, RideCategory.THRILL, "totstation");
        this.station1 = station1;
        this.station2 = station2;
        this.station3 = station3;
        this.station4 = station4;
        this.spawner1 = spawner1;
        this.spawner2 = spawner2;
        this.spawner3 = spawner3;
        this.spawner4 = spawner4;
        spawn1 = spawner1.getBlock();
        spawn2 = spawner2.getBlock();
        spawn3 = spawner3.getBlock();
        spawn4 = spawner4.getBlock();
    }

    @Override
    public void moveToStation() {
        if (frozen) {
            return;
        }
        changeStation();
        List<UUID> fullList = getQueue();
        List<UUID> fps = getFPQueue();
        if (fps.size() > fullList.size()) {
            int place = 1;
            for (int i = 0; i < fullList.size(); i++) {
                if (place > i) {
                    break;
                }
                fullList.add(place, fps.remove(i));
                place += 2;
            }
            for (UUID uuid : fps) {
                fullList.add(uuid);
            }
        } else {
            int place = 1;
            if (fullList.isEmpty()) {
                fullList = fps;
                fps.clear();
            } else {
                for (UUID uuid : fps) {
                    fullList.add(place, uuid);
                    place += 2;
                }
            }
        }
        Location loc = getStation(stationNumber);
        if (fullList.size() >= amountOfRiders) {
            for (int i = 0; i < amountOfRiders; i++) {
                Player tp = Bukkit.getPlayer(fullList.get(0));
                if (tp == null) {
                    i--;
                    continue;
                }
                if (fps.contains(tp.getUniqueId())) {
                    chargeFastpass(ParkManager.getPlayerData(tp.getUniqueId()));
                    tp.sendMessage(ChatColor.GREEN + "You were charged " + ChatColor.YELLOW + "1 " +
                            getCategory().getName() + " FastPass!");
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
            if (fps.contains(tp.getUniqueId())) {
                chargeFastpass(ParkManager.getPlayerData(tp.getUniqueId()));
                tp.sendMessage(ChatColor.GREEN + "You were charged " + ChatColor.YELLOW + "1 " +
                        getCategory().getName() + " FastPass!");
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
            case 3:
                return spawn3;
            case 4:
                return spawn4;
        }
        return spawn1;
    }

    private void changeStation() {
        switch (stationNumber) {
            case 1:
                stationNumber = 4;
                break;
            case 2:
                stationNumber = 4;
                break;
            case 3:
                stationNumber = 4;
                break;
            case 4:
                stationNumber = 3;
                break;
        }
    }

    public Location getStation(int num) {
        switch (num) {
            case 1:
                return station1;
            case 2:
                return station2;
            case 3:
                return station3;
            case 4:
                return station4;
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
            case 3:
                return spawner3;
            case 4:
                return spawner4;
            default:
                return spawner1;
        }
    }
}