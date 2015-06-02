package us.mcmagic.magicassistant.ridemanager;

import net.minecraft.server.v1_8_R3.BlockMinecartTrackAbstract;
import net.minecraft.server.v1_8_R3.EntityMinecartRideable;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.TrigMath;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.util.Vector;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.utils.FaceUtil;
import us.mcmagic.magicassistant.utils.MathUtil;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.particles.ParticleUtil;

import java.util.UUID;

/**
 * Created by Marc on 4/1/15
 */
public class Cart extends EntityMinecartRideable {
    private static final int[][][] matrix = new int[][][]{{{0, 0, -1}, {0, 0, 1}}, {{-1, 0, 0}, {1, 0, 0}},
            {{-1, -1, 0}, {1, 0, 0}}, {{-1, 0, 0}, {1, -1, 0}}, {{0, 0, -1}, {0, -1, 1}}, {{0, -1, -1}, {0, 0, 1}},
            {{0, 0, 1}, {1, 0, 0}}, {{0, 0, 1}, {-1, 0, 0}}, {{0, 0, -1}, {-1, 0, 0}}, {{0, 0, -1}, {1, 0, 0}}};
    private Train train;
    private UUID passenger;
    private BlockFace direction;
    private BlockFace lastDirection;
    private BlockFace directionFrom = BlockFace.SELF;
    private boolean atStation = false;
    private Station station;
    private boolean slowdown = false;
    private boolean playerEnter = true;
    private boolean playerExit;
    private double power = 0.1;
    private double lastSubtraction = 0;
    private float yaw;

    public Cart(World world, double d0, double d1, double d2, double power, BlockFace dir) {
        this(world, d0, d1, d2);
        this.power = power;
        this.lastDirection = dir;
        this.direction = dir;
        this.yaw = yawFromDir(dir);
    }

    private float yawFromDir(BlockFace dir) {
        switch (dir) {
            case NORTH:
                return -180;
            case EAST:
                return -90;
            case SOUTH:
                return 0;
            case WEST:
                return 90;
        }
        return 0;
    }

    public Cart(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    @Override
    public void move(double x, double y, double z) {
        Location from = new Location(this.getWorld().getWorld(), locX, locY, locZ);
        //updateDirection2();
        //Bukkit.broadcastMessage(yaw + " " + direction.toString());
        //double cos = FaceUtil.cos(direction);
        //double sin = FaceUtil.sin(direction);
        updateFactors();
        Location to = from.add(motX, motY, motZ);
        super.move(x, y, z);
    }

    private void updateFactors() {
        if (motX != 0 && motZ != 0) {
            double tan = Math.tan(motX / motZ);
            if (tan != lastSubtraction) {
                yaw = (float) (yaw - tan);
            }
        }
        Bukkit.broadcastMessage(ChatColor.RED + "" + yaw);
    }

    private BlockFace getStraightDirection(BlockFace direction) {
        switch (direction) {
            case NORTH:
                return BlockFace.NORTH;
            case EAST:
                return BlockFace.EAST;
            case SOUTH:
                return BlockFace.SOUTH;
            case WEST:
                return BlockFace.WEST;
            case NORTH_EAST:
                if (lastDirection.equals(BlockFace.NORTH_NORTH_EAST)) {
                    return BlockFace.EAST;
                } else {
                    return BlockFace.NORTH;
                }
            case NORTH_WEST:
                if (lastDirection.equals(BlockFace.NORTH_NORTH_WEST)) {
                    return BlockFace.WEST;
                } else {
                    return BlockFace.NORTH;
                }
            case SOUTH_EAST:
                if (lastDirection.equals(BlockFace.SOUTH_SOUTH_EAST)) {
                    return BlockFace.EAST;
                } else {
                    return BlockFace.SOUTH;
                }
            case SOUTH_WEST:
                if (lastDirection.equals(BlockFace.SOUTH_SOUTH_WEST)) {
                    return BlockFace.WEST;
                } else {
                    return BlockFace.SOUTH;
                }
            case WEST_NORTH_WEST:
                if (lastDirection.equals(BlockFace.WEST)) {
                    return BlockFace.NORTH;
                } else {
                    return BlockFace.WEST;
                }
            case NORTH_NORTH_WEST:
                if (lastDirection.equals(BlockFace.NORTH)) {
                    return BlockFace.WEST;
                } else {
                    return BlockFace.NORTH;
                }
            case NORTH_NORTH_EAST:
                if (lastDirection.equals(BlockFace.NORTH)) {
                    return BlockFace.EAST;
                } else {
                    return BlockFace.NORTH;
                }
            case EAST_NORTH_EAST:
                if (lastDirection.equals(BlockFace.EAST)) {
                    return BlockFace.NORTH;
                } else {
                    return BlockFace.EAST;
                }
            case EAST_SOUTH_EAST:
                if (lastDirection.equals(BlockFace.EAST)) {
                    return BlockFace.SOUTH;
                } else {
                    return BlockFace.EAST;
                }
            case SOUTH_SOUTH_EAST:
                if (lastDirection.equals(BlockFace.SOUTH)) {
                    return BlockFace.EAST;
                } else {
                    return BlockFace.SOUTH;
                }
            case SOUTH_SOUTH_WEST:
                if (lastDirection.equals(BlockFace.SOUTH)) {
                    return BlockFace.WEST;
                } else {
                    return BlockFace.SOUTH;
                }
            case WEST_SOUTH_WEST:
                if (lastDirection.equals(BlockFace.WEST)) {
                    return BlockFace.SOUTH;
                } else {
                    return BlockFace.WEST;
                }
        }
        return BlockFace.NORTH;
    }

    private void updateFactorss() {
        BlockMinecartTrackAbstract.EnumTrackPosition pos =
                MagicAssistant.rideManager.getTrackPosition(getBukkitEntity().getLocation().getBlock());
        double x = 0;
        double z = 0;
        Bukkit.broadcastMessage(ChatColor.BLUE + direction.name());
        switch (pos) {
            case NORTH_SOUTH:
                if (direction.equals(BlockFace.NORTH)) {
                    x = 0;
                    z = -power;
                } else {
                    direction = getStraightDirection(direction);
                    x = 0;
                    z = power;
                }
                break;
            case EAST_WEST:
                if (direction.equals(BlockFace.EAST)) {
                    x = power;
                    z = 0;
                } else {
                    direction = getStraightDirection(direction);
                    x = -power;
                    z = 0;
                }
                break;
            case ASCENDING_EAST:
                if (direction.equals(BlockFace.EAST)) {
                    x = power;
                    z = 0;
                } else {
                    direction = getStraightDirection(direction);
                    x = -power;
                    z = 0;
                }
                break;
            case ASCENDING_WEST:
                if (direction.equals(BlockFace.EAST)) {
                    x = power;
                    z = 0;
                } else {
                    direction = getStraightDirection(direction);
                    x = -power;
                    z = 0;
                }
                break;
            case ASCENDING_NORTH:
                if (direction.equals(BlockFace.NORTH)) {
                    x = 0;
                    z = -power;
                } else {
                    direction = getStraightDirection(direction);
                    x = 0;
                    z = power;
                }
                break;
            case ASCENDING_SOUTH:
                if (direction.equals(BlockFace.NORTH)) {
                    x = 0;
                    z = -power;
                } else {
                    direction = getStraightDirection(direction);
                    x = 0;
                    z = power;
                }
                break;
            case SOUTH_EAST:
                switch (direction) {
                    case NORTH:
                        direction = BlockFace.NORTH_NORTH_EAST;
                        x = power * 0.25;
                        z = (-power) * 0.75;
                        break;
                    case WEST:
                        direction = BlockFace.WEST_SOUTH_WEST;
                        x = (-power) * 0.75;
                        z = power * 0.25;
                        break;
                    case EAST:
                        x = power;
                        z = 0;
                        break;
                    case SOUTH:
                        x = 0;
                        z = power;
                    case NORTH_EAST:
                        direction = BlockFace.EAST_NORTH_EAST;
                        x = power * 0.75;
                        z = (-power) * 0.25;
                        break;
                    case SOUTH_WEST:
                        direction = BlockFace.SOUTH_SOUTH_WEST;
                        x = (-power) * 0.25;
                        z = power * 0.75;
                        break;
                    case NORTH_NORTH_EAST:
                        direction = BlockFace.NORTH_EAST;
                        x = power * 0.55;
                        z = (-power) * 0.45;
                        break;
                    case EAST_NORTH_EAST:
                        direction = BlockFace.EAST;
                        x = power;
                        z = 0;
                        break;
                    case SOUTH_SOUTH_WEST:
                        direction = BlockFace.SOUTH;
                        x = 0;
                        z = power;
                        break;
                    case WEST_SOUTH_WEST:
                        direction = BlockFace.SOUTH_WEST;
                        x = (-power) * 0.45;
                        z = power * 0.55;
                        break;
                }
                break;
            case SOUTH_WEST:
                if (direction.equals(BlockFace.NORTH)) {
                    direction = BlockFace.WEST;
                    x = 0;
                    z = -power;
                } else if (direction.equals(BlockFace.SOUTH)) {
                    x = 0;
                    z = power;
                } else if (direction.equals(BlockFace.EAST)) {
                    direction = BlockFace.SOUTH;
                    x = power;
                    z = 0;
                } else if (direction.equals(BlockFace.WEST)) {
                    x = -power;
                    z = 0;
                } else if (direction.equals(BlockFace.NORTH_WEST)) {
                    direction = BlockFace.WEST;
                    x = -power;
                    x = -power;
                } else if (direction.equals(BlockFace.SOUTH_EAST)) {
                    direction = BlockFace.SOUTH;
                    x = power;
                    z = power;
                }
                break;
            case NORTH_WEST:
                if (direction.equals(BlockFace.NORTH)) {
                    x = 0;
                    z = -power;
                } else if (direction.equals(BlockFace.SOUTH)) {
                    direction = BlockFace.WEST;
                    x = 0;
                    z = power;
                } else if (direction.equals(BlockFace.EAST)) {
                    direction = BlockFace.NORTH;
                    x = power;
                    z = 0;
                } else if (direction.equals(BlockFace.WEST)) {
                    x = -power;
                    z = 0;
                } else if (direction.equals(BlockFace.NORTH_EAST)) {
                    direction = BlockFace.NORTH;
                    x = power;
                    x = -power;
                } else if (direction.equals(BlockFace.SOUTH_WEST)) {
                    direction = BlockFace.WEST;
                    x = -power;
                    z = power;
                }
                break;
            case NORTH_EAST:
                if (direction.equals(BlockFace.NORTH)) {
                    x = 0;
                    z = -power;
                } else if (direction.equals(BlockFace.SOUTH)) {
                    direction = BlockFace.EAST;
                    x = 0;
                    z = power;
                } else if (direction.equals(BlockFace.EAST)) {
                    x = power;
                    z = 0;
                } else if (direction.equals(BlockFace.WEST)) {
                    direction = BlockFace.NORTH;
                    x = -power;
                    z = 0;
                } else if (direction.equals(BlockFace.NORTH_WEST)) {
                    direction = BlockFace.NORTH;
                    x = -power;
                    x = -power;
                } else if (direction.equals(BlockFace.SOUTH_EAST)) {
                    direction = BlockFace.EAST;
                    x = power;
                    z = power;
                }
                break;
        }
        final double origx = motX;
        final double origz = motZ;
        motX = x;
        motZ = z;
        velocityChanged = motX != origx || motZ != origz;
        lastDirection = direction;
    }

    public void updateDirection2() {
        float yaw = -180;
        Bukkit.broadcastMessage("x: " + motX + " y: " + motY + " z: " + motZ);
        TrigMath.atan2(1, 2);
        direction = faceFromYaw(yaw);
    }

    private float getYaw() {
        double x = motX;
        double z = motZ;
        double absX = Math.abs(x);
        double absZ = Math.abs(z);
        BlockMinecartTrackAbstract.EnumTrackPosition pos =
                MagicAssistant.rideManager.getTrackPosition(getBukkitEntity().getLocation().getBlock());
        if (!isSloped()) {
            if (!(pos.equals(BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH) ||
                    pos.equals(BlockMinecartTrackAbstract.EnumTrackPosition.EAST_WEST))) {
                return 0;
            }
        }
        if (absX == 0) {
            if (z > 0) {
                return 0;
            }
            return -180;
        }
        return 0;
    }

    private boolean isBetween(double original, double from, double to) {
        return original >= from && original <= to;
    }

    private BlockFace faceFromYaw(float y) {
        float yaw;
        if (y == -180) {
            yaw = 90;
        } else {
            yaw = y - 90;
        }
        //yaw = atan2(dz, dx) − 90∘
        Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(Math.atan2(motZ, motX) - 90f));
        if (isBetween(Math.abs(yaw), 157.5, 180)) {
            return BlockFace.NORTH;
        }
        if (isBetween(yaw, -137.5, 112.5)) {
            return BlockFace.NORTH_EAST;
        }
        if (isBetween(yaw, -112.5, -67.5)) {
            return BlockFace.EAST;
        }
        if (isBetween(yaw, -67.5, -22.5)) {
            return BlockFace.SOUTH_EAST;
        }
        if (isBetween(yaw, -22.5, 22.5)) {
            return BlockFace.SOUTH;
        }
        if (isBetween(yaw, 22.5, 67.5)) {
            return BlockFace.SOUTH_WEST;
        }
        if (isBetween(yaw, 67.5, 112.5)) {
            return BlockFace.WEST;
        }
        if (isBetween(yaw, 112.5, 157.5)) {
            return BlockFace.NORTH_WEST;
        }
        return BlockFace.NORTH;
    }

    @Override
    public void die() {
        CartDestroyEvent e = new CartDestroyEvent(this);
        Bukkit.getPluginManager().callEvent(e);
        if (!e.isCancelled()) {
            CraftEntity entity = getBukkitEntity();
            ParticleUtil.spawnParticle(ParticleEffect.SMOKE, entity.getLocation().add(0, 0.3, 0), 0.1f, 0.1f, 0.1f, 0, 5);
            entity.getWorld().playSound(getBukkitEntity().getLocation(), Sound.FIZZ, 10, 2);
            super.die();
        }
    }

    public boolean hasPassenger() {
        return passenger != null;
    }

    public void setPassenger(UUID passenger) {
        this.passenger = passenger;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getPower() {
        return power;
    }

    public Station getStation() {
        return station;
    }

    public void removeStation() {
        station = null;
        atStation = false;
    }

    public boolean isAtStation() {
        return atStation;
    }

    public void setStation(Station s) {
        atStation = s != null;
        station = s;
        Bukkit.getScheduler().runTaskLater(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                setPower(station.getLaunchPower());
                removeStation();
            }
        }, (long) (s.getLength() * 20));
    }

    public UUID getPassenger() {
        return passenger;
    }

    private void checkTrainNotNull() {
        if (train == null) {
            train = new Train(this);
        }
    }

    public void addCartToTrain(Cart cart) {
        checkTrainNotNull();
        train.addCart(cart);
    }

    public void removeCartFromTrain(Cart cart) {
        checkTrainNotNull();
        train.removeCart(cart);
    }

    public Train getTrain() {
        checkTrainNotNull();
        return train;
    }

    @Override
    public EnumMinecartType s() {
        return EnumMinecartType.RIDEABLE;
    }

    public void setPlayerEnter(boolean playerEnter) {
        this.playerEnter = playerEnter;
    }

    public boolean playerCanEnter() {
        return playerEnter;
    }

    public void setPlayerExit(boolean playerExit) {
        this.playerExit = playerExit;
    }

    public boolean playerCanExit() {
        return playerExit;
    }

    public boolean canSlowdown() {
        return slowdown;
    }

    public void setSlowdown(boolean slowdown) {
        this.slowdown = slowdown;
    }

    private void updateDirection() {
        Vector vec = getBukkitEntity().getVelocity();
        if (direction == null) {
            direction = FaceUtil.getDirection(vec);
        }
        if (this.lastDirection == null) {
            this.lastDirection = FaceUtil.getDirection(vec, false);
        }
        BlockFace mdir = getMovementDirection(vec);
        updateDirection(mdir);
    }

    public void updateDirection(BlockFace movement) {
        if (this.direction == null) {
            this.direction = movement;
        }
        if (this.lastDirection == null) {
            if (FaceUtil.isSubCardinal(movement))
                this.lastDirection = FaceUtil.getDirection(getBukkitEntity().getVelocity(), false);
            else {
                this.lastDirection = movement;
            }
        }
        boolean fromInvalid = this.directionFrom == BlockFace.SELF;
        if (fromInvalid) {
            this.directionFrom = this.lastDirection;
        }
        this.direction = movement;
        if (FaceUtil.isSubCardinal(this.direction)) {
            BlockFace raildirection = MagicAssistant.rideManager.getRailDirection(getBukkitEntity().getLocation().getBlock());
            if (this.direction == BlockFace.NORTH_EAST) {
                this.lastDirection = (raildirection == BlockFace.NORTH_WEST ? BlockFace.EAST : BlockFace.NORTH);
            } else if (this.direction == BlockFace.SOUTH_EAST) {
                this.lastDirection = (raildirection == BlockFace.NORTH_EAST ? BlockFace.SOUTH : BlockFace.EAST);
            } else if (this.direction == BlockFace.SOUTH_WEST) {
                this.lastDirection = (raildirection == BlockFace.NORTH_WEST ? BlockFace.SOUTH : BlockFace.WEST);
            } else if (this.direction == BlockFace.NORTH_WEST) {
                this.lastDirection = (raildirection == BlockFace.NORTH_EAST ? BlockFace.WEST : BlockFace.NORTH);
            }
        } else {
            this.lastDirection = this.direction;
        }
        if (fromInvalid) {
            this.directionFrom = this.lastDirection;
        }
    }

    private boolean isSloped() {
        BlockMinecartTrackAbstract.EnumTrackPosition pos =
                MagicAssistant.rideManager.getTrackPosition(getBukkitEntity().getLocation().getBlock());
        return pos.name().contains("ASCENDING");
    }

    public BlockFace getMovementDirection(Vector movement) {
        BlockFace raildirection = MagicAssistant.rideManager.getRailDirection(getBukkitEntity().getLocation().getBlock());
        boolean isHorizontalMovement = (Math.abs(movement.getX()) >= 0.0001D) || (Math.abs(movement.getZ()) >= 0.0001D);
        BlockFace direction = null;
        if (isSloped()) {
            if (isHorizontalMovement) {
                float moveYaw = MathUtil.getLookAtYaw(movement);
                float diff1 = MathUtil.getAngleDifference(moveYaw, FaceUtil.faceToYaw(raildirection));
                float diff2 = MathUtil.getAngleDifference(moveYaw, FaceUtil.faceToYaw(raildirection.getOppositeFace()));

                if (diff1 == diff2) {
                    diff1 = FaceUtil.getFaceYawDifference(directionFrom, raildirection);
                    diff2 = FaceUtil.getFaceYawDifference(directionFrom, raildirection.getOppositeFace());
                }
                if (diff1 > diff2)
                    direction = raildirection.getOppositeFace();
                else
                    direction = raildirection;
            } else {
                if (Math.abs(movement.getY()) > 0.0001D) {
                    if (movement.getY() > 0.0D)
                        direction = raildirection;
                    else
                        direction = raildirection.getOppositeFace();
                } else {
                    direction = raildirection.getOppositeFace();
                }
            }
        } else if (isCurved()) {
            BlockFace movementDir = FaceUtil.getDirection(movement);
            BlockFace[] possibleDirections = FaceUtil.getFaces(raildirection.getOppositeFace());
            //System.out.println(movementDir);
            if (FaceUtil.isSubCardinal(movementDir)) {
                direction = movementDir;
            } else {
                BlockFace directionTo;
                if (possibleDirections[0] == movementDir) {
                    directionTo = possibleDirections[0];
                } else {
                    if (possibleDirections[1] == movementDir) {
                        directionTo = possibleDirections[1];
                    } else {
                        if (possibleDirections[0].getOppositeFace() == movementDir) {
                            directionTo = possibleDirections[1];
                        } else {
                            if (possibleDirections[1].getOppositeFace() == movementDir) {
                                directionTo = possibleDirections[0];
                            } else
                                directionTo = movementDir;
                        }
                    }
                }
                direction = FaceUtil.getRailsCartDirection(raildirection);
                if (!mapContains(directionTo, FaceUtil.getFaces(direction))) {
                    direction = direction.getOppositeFace();
                }
            }
        } else {
            float angleSide1 = FaceUtil.faceToYaw(raildirection);
            float angleSide2 = FaceUtil.faceToYaw(raildirection.getOppositeFace());
            float movAngle = MathUtil.getLookAtYaw(movement);
            Bukkit.broadcastMessage("Important: " + movement.toString());
            if (MathUtil.getAngleDifference(angleSide1, movAngle) < MathUtil.getAngleDifference(angleSide2, movAngle))
                direction = raildirection;
            else {
                direction = raildirection.getOppositeFace();
            }
        }
        return direction;
    }

    private boolean mapContains(Object object, Object[] array) {
        for (Object o : array) {
            if (o == null && object == null) {
                return true;
            }
            if (object == o) {
                return true;
            }
        }
        return false;
    }

    private boolean isCurved() {
        boolean alongX = FaceUtil.isAlongX(direction);
        boolean alongZ = FaceUtil.isAlongZ(direction);
        boolean alongY = FaceUtil.isAlongY(direction);
        return ((!alongZ) && (!alongY) && (!alongX));
    }
}