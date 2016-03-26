package us.mcmagic.parkmanager.show.actions;

import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import us.mcmagic.parkmanager.show.Show;

import java.util.UUID;

/**
 * Created by Marc on 7/1/15
 */
public class FakeBlockAction extends ShowAction {
    private final Show show;
    private PacketPlayOutBlockChange packet;

    public FakeBlockAction(Show show, long time, Location loc, int id, byte data) {
        super(show, time);
        this.show = show;
        BlockPosition pos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        packet = new PacketPlayOutBlockChange(((CraftWorld) loc.getWorld()).getHandle(), pos);
        packet.block = Block.getById(id).fromLegacyData(data);
    }

    @Override
    public void play() {
        for (UUID uuid : show.getNearPlayers()) {
            Player tp = Bukkit.getPlayer(uuid);
            if (tp == null) {
                continue;
            }
            ((CraftPlayer) tp).getHandle().playerConnection.sendPacket(packet);
        }
    }
}