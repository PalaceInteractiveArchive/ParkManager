package network.palace.parkmanager.outline;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.UUID;

/**
 * @author Marc
 * @since 10/20/17
 */
@Getter
@Setter
@RequiredArgsConstructor
public class OutlineSession {
    private final UUID uuid;
    private Point sessionPoint = null;
    private Material type = Material.GOLD_BLOCK;
    private Location undoLocation = null;
    private BlockData undoBlockData = null;

    public Location outline(double length, double heading) {
        CPlayer player = Core.getPlayerManager().getPlayer(uuid);
        if (player == null || sessionPoint == null) return null;

        int y = player.getLocation().getBlockY();
        double radAngle = heading / 180.0D * Math.PI;
        int x = sessionPoint.getX() + (int) Math.round(length * Math.sin(radAngle));
        int z = sessionPoint.getZ() - (int) Math.round(length * Math.cos(radAngle));
        Location loc = new Location(player.getWorld(), x, y, z);

        Block b = loc.getBlock();
        undoLocation = loc.clone();
        undoBlockData = b.getBlockData();
        b.setType(type);

        return loc;
    }

    public boolean undo() {
        if (undoLocation == null || undoBlockData == null) return false;
        Block b = undoLocation.getBlock();
        BlockData preBlockData = b.getBlockData();
        b.setBlockData(undoBlockData);
        undoBlockData = preBlockData;
        return true;
    }
}
