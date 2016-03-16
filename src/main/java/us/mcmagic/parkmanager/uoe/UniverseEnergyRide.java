package us.mcmagic.parkmanager.uoe;

import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marc on 3/9/15
 */
public class UniverseEnergyRide {

    @SuppressWarnings("deprecation")
    public void moveBlocks(Location loc1, Location loc2, int move, Byte firstData, double x, double y, double z,
                           double speed, int raidus) {
        List<Block> blocks;
        if (firstData == null) {
            blocks = getBlocks(loc1, loc2, move);
        } else {
            blocks = getBlocks(loc1, loc2, move, firstData);
        }
        double iX = getInc(x, speed);
        double iY = getInc(y, speed);
        double iZ = getInc(z, speed);
        for (Block b : blocks) {
            Location bloc = b.getLocation();
            Material type = b.getType();
            int id = b.getTypeId();
            byte data = b.getData();
            b.setType(Material.AIR);
            MovingBlock fallingBlock;
            fallingBlock = new MovingBlock(((CraftWorld) bloc.getWorld()).getHandle(), bloc.getX(), bloc.getY(),
                    bloc.getZ(), net.minecraft.server.v1_8_R3.Block.getById(id).fromLegacyData(data), iX, iY, iZ,
                    (int) (x / iX), type, data);
            WorldServer realWorld = ((CraftWorld) loc1.getWorld()).getHandle();
            realWorld.addEntity(fallingBlock);
            if (type.name().toLowerCase().contains("stair")) {
                if (fallingBlock.passenger != null) {
                    continue;
                }
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    if (tp.getLocation().distance(fallingBlock.getBukkitEntity().getLocation()) > raidus) {
                        continue;
                    }
                    if (tp.isInsideVehicle()) {
                        if (!(tp.getVehicle() instanceof ArmorStand)) {
                            continue;
                        }
                        tp.getVehicle().remove();
                    }
                    tp.setPassenger((Entity) fallingBlock);
                    break;
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private List<Block> getBlocks(Location min, Location max, int id) {
        List<Block> list = new ArrayList<>();
        boolean all = id == -1;
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    Block blk = min.getWorld().getBlockAt(new Location(min.getWorld(), x, y, z));
                    if (all) {
                        list.add(blk);
                        continue;
                    }
                    if (blk.getTypeId() == id) {
                        list.add(blk);
                    }
                }
            }
        }
        return list;
    }

    private double getInc(double inc, double speed) {
        return (inc / speed) / 20;
    }

    @SuppressWarnings("deprecation")
    public List<Block> getBlocks(Location min, Location max, int id, byte data) {
        List<Block> list = new ArrayList<>();
        boolean all = id == -1;
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    Block blk = min.getWorld().getBlockAt(new Location(min.getWorld(), x, y, z));
                    if (all) {
                        list.add(blk);
                        continue;
                    }
                    if (blk.getTypeId() == id && blk.getData() == data) {
                        list.add(blk);
                    }
                }
            }
        }
        return list;
    }

    private double gi(String arg) {
        return Double.parseDouble(arg);
    }

    public void eject(Location loc, int radius) {
        for (Player tp : Bukkit.getOnlinePlayers()) {
            if (tp.getLocation().distance(loc) > radius) {
                continue;
            }
            if (!tp.isInsideVehicle()) {
                continue;
            }
            Entity vehicle = tp.getVehicle();
            if (vehicle instanceof ArmorStand) {
                vehicle.remove();
                tp.teleport(tp.getLocation().add(0, 1, 0));
            }
        }
    }
}