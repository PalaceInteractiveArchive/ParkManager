package network.palace.parkmanager.queues;

import lombok.Getter;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.QueueType;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;
import java.util.UUID;

public class BlockQueue extends Queue {
    @Getter private Location blockLocation;

    public BlockQueue(int id, UUID uuid, String name, String warp, int groupSize, int delay, boolean open, Location station, List<QueueSign> signs, Location blockLocation) {
        super(id, uuid, name, warp, groupSize, delay, open, station, signs);
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
