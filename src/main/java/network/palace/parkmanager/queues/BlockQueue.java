package network.palace.parkmanager.queues;

import lombok.Getter;
import lombok.Setter;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.ParkType;
import network.palace.parkmanager.handlers.QueueType;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;
import java.util.UUID;

public class BlockQueue extends Queue {
    @Getter @Setter private Location blockLocation;

    public BlockQueue(String id, ParkType park, UUID uuid, String name, String warp, int groupSize, int delay, boolean open, Location station, List<QueueSign> signs, Location blockLocation) {
        super(id, park, uuid, name, warp, groupSize, delay, open, station, signs);
        this.blockLocation = blockLocation;
    }

    @Override
    public QueueType getQueueType() {
        return QueueType.BLOCK;
    }

    @Override
    protected void handleSpawn(List<CPlayer> players) {
        ParkManager.getDelayUtil().logDelay(blockLocation, 20, Material.REDSTONE_BLOCK);
    }
}
