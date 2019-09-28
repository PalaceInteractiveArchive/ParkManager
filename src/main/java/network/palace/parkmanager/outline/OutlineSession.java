package network.palace.parkmanager.outline;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.UUID;

/**
 * @author Marc
 * @since 10/20/17
 */
@Getter
@Setter
@RequiredArgsConstructor
@SuppressWarnings("deprecation")
public class OutlineSession {
    private final UUID uuid;
    private Point sessionPoint = null;
    private Material type = Material.GOLD_BLOCK;
    private Location undoLocation = null;
    private Material undoType = null;
    private byte undoData = 0;

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
        undoType = b.getType();
        undoData = b.getData();
        b.setType(type);

        return loc;
    }

    public boolean undo() {
        if (undoLocation == null || undoType == null) return false;
        Block b = undoLocation.getBlock();
        final Material preType = b.getType();
        final byte preData = b.getData();
        b.setType(undoType);
        b.setData(undoData);
        undoType = preType;
        undoData = preData;
        return true;
    }
}
