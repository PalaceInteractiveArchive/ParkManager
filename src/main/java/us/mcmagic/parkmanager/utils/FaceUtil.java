package us.mcmagic.parkmanager.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.EnumMap;

/**
 * Created by Marc on 4/19/15
 */
public class FaceUtil {
    public static final BlockFace[] AXIS = new BlockFace[4];
    public static final BlockFace[] RADIAL = {BlockFace.WEST, BlockFace.NORTH_WEST, BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST};
    public static final BlockFace[] BLOCK_SIDES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
    public static final BlockFace[] ATTACHEDFACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP};
    public static final BlockFace[] ATTACHEDFACESDOWN = BLOCK_SIDES;
    private static final EnumMap<BlockFace, Integer> notches = new EnumMap<>(BlockFace.class);

    public static int faceToNotch(BlockFace face) {
        Integer notch = notches.get(face);
        return notch == null ? 0 : notch;
    }

    public static boolean isAlongX(BlockFace face) {
        return (face.getModX() != 0) && (face.getModZ() == 0);
    }

    public static boolean isAlongY(BlockFace face) {
        return isVertical(face);
    }

    public static boolean isAlongZ(BlockFace face) {
        return (face.getModZ() != 0) && (face.getModX() == 0);
    }

    public static BlockFace notchToFace(int notch) {
        return RADIAL[(notch & 0x7)];
    }

    public static BlockFace rotate(BlockFace from, int notchCount) {
        return notchToFace(faceToNotch(from) + notchCount);
    }

    public static BlockFace combine(BlockFace from, BlockFace to) {
        if (from == BlockFace.NORTH) {
            if (to == BlockFace.WEST)
                return BlockFace.NORTH_WEST;
            if (to == BlockFace.EAST)
                return BlockFace.NORTH_EAST;
        } else if (from == BlockFace.EAST) {
            if (to == BlockFace.NORTH)
                return BlockFace.NORTH_EAST;
            if (to == BlockFace.SOUTH)
                return BlockFace.SOUTH_EAST;
        } else if (from == BlockFace.SOUTH) {
            if (to == BlockFace.WEST)
                return BlockFace.SOUTH_WEST;
            if (to == BlockFace.EAST)
                return BlockFace.SOUTH_EAST;
        } else if (from == BlockFace.WEST) {
            if (to == BlockFace.NORTH)
                return BlockFace.NORTH_WEST;
            if (to == BlockFace.SOUTH) {
                return BlockFace.SOUTH_WEST;
            }
        }
        return from;
    }

    public static BlockFace subtract(BlockFace face1, BlockFace face2) {
        return notchToFace(faceToNotch(face1) - faceToNotch(face2));
    }

    public static BlockFace add(BlockFace face1, BlockFace face2) {
        return notchToFace(faceToNotch(face1) + faceToNotch(face2));
    }

    public static BlockFace[] getFaces(BlockFace main) {
        switch (main.ordinal()) {
            case 1:
                return new BlockFace[]{BlockFace.SOUTH, BlockFace.EAST};
            case 2:
                return new BlockFace[]{BlockFace.SOUTH, BlockFace.WEST};
            case 3:
                return new BlockFace[]{BlockFace.NORTH, BlockFace.EAST};
            case 4:
                return new BlockFace[]{BlockFace.NORTH, BlockFace.WEST};
        }
        return new BlockFace[]{main, main.getOppositeFace()};
    }

    public static BlockFace getRailsCartDirection(BlockFace raildirection) {
        switch (raildirection.ordinal()) {
            case 2:
            case 3:
                return BlockFace.NORTH_WEST;
            case 1:
            case 4:
                return BlockFace.SOUTH_WEST;
        }
        return raildirection;
    }

    public static BlockFace toRailsDirection(BlockFace direction) {
        switch (direction.ordinal()) {
            case 5:
                return BlockFace.SOUTH;
            case 6:
                return BlockFace.EAST;
        }
        return direction;
    }

    public static boolean isSubCardinal(BlockFace face) {
        switch (face.ordinal()) {
            case 1:
                return true;
            case 2:
                return true;
            case 3:
                return true;
            case 4:
                return true;
        }
        return false;
    }

    public static boolean isVertical(BlockFace face) {
        return (face == BlockFace.UP) || (face == BlockFace.DOWN);
    }

    public static BlockFace getVertical(boolean up) {
        return up ? BlockFace.UP : BlockFace.DOWN;
    }

    public static BlockFace getVertical(double dy) {
        return getVertical(dy >= 0.0D);
    }

    public static boolean hasSubDifference(BlockFace face1, BlockFace face2) {
        return getFaceYawDifference(face1, face2) <= 45;
    }

    public static Vector faceToVector(BlockFace face, double length) {
        return faceToVector(face).multiply(length);
    }

    public static Vector faceToVector(BlockFace face) {
        return new Vector(face.getModX(), face.getModY(), face.getModZ());
    }

    public static BlockFace getDirection(Location from, Location to, boolean useSubCardinalDirections) {
        return getDirection(to.getX() - from.getX(), to.getZ() - from.getZ(), useSubCardinalDirections);
    }

    public static BlockFace getDirection(Block from, Block to, boolean useSubCardinalDirections) {
        return getDirection(to.getX() - from.getX(), to.getZ() - from.getZ(), useSubCardinalDirections);
    }

    public static BlockFace getDirection(Vector movement) {
        return getDirection(movement, true);
    }

    public static BlockFace getDirection(Vector movement, boolean useSubCardinalDirections) {
        return getDirection(movement.getX(), movement.getZ(), useSubCardinalDirections);
    }

    public static BlockFace getDirection(double dx, double dz, boolean useSubCardinalDirections) {
        return yawToFace(MathUtil.getLookAtYaw(dx, dz), useSubCardinalDirections);
    }

    public static int getFaceYawDifference(BlockFace face1, BlockFace face2) {
        return MathUtil.getAngleDifference(faceToYaw(face1), faceToYaw(face2));
    }

    public static double cos(BlockFace face) {
        switch (face) {
            case SOUTH_WEST:
                return -MathUtil.HALFROOTOFTWO;
            case NORTH_WEST:
                return -MathUtil.HALFROOTOFTWO;
            case SOUTH_EAST:
                return MathUtil.HALFROOTOFTWO;
            case NORTH_EAST:
                return MathUtil.HALFROOTOFTWO;
            case EAST:
                return 1;
            case WEST:
                return -1;
            default:
                return 0;
        }
    }

    public static double sin(BlockFace face) {
        switch (face) {
            case NORTH_EAST:
                return -MathUtil.HALFROOTOFTWO;
            case NORTH_WEST:
                return -MathUtil.HALFROOTOFTWO;
            case SOUTH_WEST:
                return MathUtil.HALFROOTOFTWO;
            case SOUTH_EAST:
                return MathUtil.HALFROOTOFTWO;
            case NORTH:
                return -1;
            case SOUTH:
                return 1;
            default:
                return 0;
        }
    }

    public static int faceToYaw(BlockFace face) {
        return MathUtil.wrapAngle(45 * faceToNotch(face));
    }

    public static BlockFace yawToFace(float yaw) {
        return yawToFace(yaw, true);
    }

    public static BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
        if (useSubCardinalDirections) {
            int num = (Math.round(yaw / 45.0F) & 0x7);
            Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(num) + " " + RADIAL[num]);
            return RADIAL[num];
        }
        return AXIS[(Math.round(yaw / 90.0F) & 0x3)];
    }

    static {
        for (int i = 0; i < RADIAL.length; i++) {
            notches.put(RADIAL[i], i);
        }
        for (int i = 0; i < AXIS.length; i++)
            AXIS[i] = RADIAL[(i << 1)];
    }
}
