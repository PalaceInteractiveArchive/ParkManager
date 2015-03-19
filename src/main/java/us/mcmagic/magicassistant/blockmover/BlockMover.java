package us.mcmagic.magicassistant.blockmover;

import net.minecraft.server.v1_8_R2.EntityFallingBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marc on 3/9/15
 */
public class BlockMover {

    @SuppressWarnings("deprecation")
    public void moveBlocks(Location loc1, Location loc2, Material move, byte data, Vector add, float speed) {
        BlockIterator iter = new BlockIterator(loc1.getWorld(), loc1.toVector(), loc1.getDirection(), 0, 10);
        World world = Bukkit.getWorlds().get(0);
        int id = move.getId();
        List<Block> blockList = getBlocks(loc1, loc2, id);
        for (Block block : blockList) {
            block.setType(Material.AIR);
            EntityFallingBlock entity = new EntityFallingBlock(((CraftWorld) loc1.getWorld()).getHandle(),
                    loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ(),
                    net.minecraft.server.v1_8_R2.Block.getById(move.getId()).fromLegacyData(data));
            FallingBlock fb = world.spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
            fb.setVelocity(block.getLocation().add(add).toVector());
        }
    }

    @SuppressWarnings("deprecation")
    public static List<Block> getBlocks(Location min, Location max, int select) {
        List<Block> list = new ArrayList<>();
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    Block blk = min.getWorld().getBlockAt(new Location(min.getWorld(), x, y, z));
                    if (blk.getTypeId() == select) {
                        Bukkit.broadcastMessage("Block Added " + blk.getLocation());
                        list.add(blk);
                    }
                }
            }
        }
        return list;
    }
}