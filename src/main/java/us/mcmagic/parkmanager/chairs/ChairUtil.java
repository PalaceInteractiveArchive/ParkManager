package us.mcmagic.parkmanager.chairs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Stairs;

import java.util.HashMap;

public class ChairUtil {

    private static final HashMap<BlockFace, Float> DIRECTIONAL_YAW = new HashMap<BlockFace, Float>() {{
        put(BlockFace.NORTH, 180F);
        put(BlockFace.EAST, -90F);
        put(BlockFace.SOUTH, 0F);
        put(BlockFace.WEST, 90F);
    }};
    public static final Material[] SUITABLE_CHAIR_TYPES = {
            Material.WOOD_STAIRS, Material.COBBLESTONE_STAIRS, Material.BRICK_STAIRS,
            Material.SMOOTH_STAIRS, Material.NETHER_BRICK_STAIRS, Material.SANDSTONE_STAIRS,
            Material.SPRUCE_WOOD_STAIRS, Material.BIRCH_WOOD_STAIRS, Material.JUNGLE_WOOD_STAIRS,
            Material.QUARTZ_STAIRS, Material.ACACIA_STAIRS, Material.DARK_OAK_STAIRS,
            Material.RED_SANDSTONE_STAIRS
    };

    private ChairUtil() {
    }

    public static Location sitLocation(Block block, float playerYaw) {
        double height = 0.7;
        Stairs stairs = null;
        if (block.getState().getData() instanceof Stairs) {
            stairs = (Stairs) block.getState().getData();
        }
        Location location = block.getLocation();
        location.add(0.5, (height - 0.5D), 0.5);
        if (stairs != null) {
            if (DIRECTIONAL_YAW.containsKey(stairs.getDescendingDirection())) {
                location.setYaw(DIRECTIONAL_YAW.get(stairs.getDescendingDirection()));
            }
        } else {
            location.setYaw(playerYaw);
        }
        return location;
    }

    public static boolean isSuitableChair(Block block) {
        for (Material material : SUITABLE_CHAIR_TYPES) {
            if (block.getType().equals(material)) {
                return true;
            }
        }
        return false;
    }
}