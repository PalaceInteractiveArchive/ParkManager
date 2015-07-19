package us.mcmagic.magicassistant.ridemanager;

import net.minecraft.server.v1_8_R3.BlockMinecartTrackAbstract;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.particles.ParticleUtil;

/**
 * Created by Marc on 7/16/15
 */
@SuppressWarnings("deprecation")
public class RailRider {
    private Cart cart;

    public RailRider(Cart cart) {
        this.cart = cart;
    }

    public Location next() {
        Location original = cart.getLoc().clone();
        Location loc = cart.getLoc();
        double power = cart.getPower();
        BlockFace lastdir = cart.lastDirection;
        BlockMinecartTrackAbstract.EnumTrackPosition pos = cart.getTrackType(loc.getBlock().getData());
        Block b = null;
        Bukkit.broadcastMessage(loc.toString() + " " + ChatColor.RED + pos.name());
        if (pos.equals(BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH) && !MagicAssistant.rideManager.isRail(loc)) {
            pos = cart.getTrackType(loc.add(0, -1, 0).getBlock().getData());
            Bukkit.broadcastMessage("FUN " + pos.name());
        }
        ParticleUtil.spawnParticle(ParticleEffect.DRIP_WATER, loc.clone().add(0, 0.1, 0), 0f, 0f, 0f, 0f, 1);
        switch (pos) {
            case NORTH_SOUTH:
                if (lastdir.equals(BlockFace.NORTH)) {
                    b = loc.add(0, 0, -power).getBlock();
                } else {
                    b = loc.add(0, 0, power).getBlock();
                }
                break;
            case EAST_WEST:
                if (lastdir.equals(BlockFace.WEST)) {
                    b = loc.add(-power, 0, 0).getBlock();
                } else {
                    b = loc.add(power, 0, 0).getBlock();
                }
                break;
            case ASCENDING_EAST:
                if (lastdir.equals(BlockFace.EAST)) {
                    b = loc.add(power, 0, 0).getBlock();
                } else {
                    b = loc.add(-power, 0, 0).getBlock();
                }
                break;
            case ASCENDING_WEST:
                if (lastdir.equals(BlockFace.WEST)) {
                    b = loc.add(-power, 0, 0).getBlock();
                } else {
                    b = loc.add(power, 0, 0).getBlock();
                }
                break;
            case ASCENDING_NORTH:
                if (lastdir.equals(BlockFace.NORTH)) {
                    b = loc.add(0, 0, -power).getBlock();
                } else {
                    b = loc.add(0, 0, power).getBlock();
                }
                break;
            case ASCENDING_SOUTH:
                if (lastdir.equals(BlockFace.SOUTH)) {
                    b = loc.add(0, 0, power).getBlock();
                } else {
                    b = loc.add(0, 0, -power).getBlock();
                }
                break;
            case SOUTH_EAST:
                break;
            case SOUTH_WEST:
                break;
            case NORTH_WEST:
                break;
            case NORTH_EAST:
                break;
        }
        if (b == null) {
            return original;
        }
        if (!MagicAssistant.rideManager.isRail(b.getLocation())) {
            return original;
        } else {
            Bukkit.broadcastMessage(ChatColor.YELLOW + pos.name());
            if (isAscending(pos)) {
                Bukkit.broadcastMessage("ASC");
                return loc.add(0, 1, 0);
            }
            return loc;
        }
    }

    private boolean isAscending(BlockMinecartTrackAbstract.EnumTrackPosition pos) {
        return pos.name().toLowerCase().contains("ascending");
    }
}