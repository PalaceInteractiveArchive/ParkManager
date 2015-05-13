package us.mcmagic.magicassistant.ridemanager;

import net.minecraft.server.v1_8_R2.BlockMinecartTrackAbstract;
import net.minecraft.server.v1_8_R2.EntityMinecartRideable;
import net.minecraft.server.v1_8_R2.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftEntity;
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
    private BlockFace directionTo;
    private BlockFace directionFrom = BlockFace.SELF;
    private boolean atStation = false;
    private Station station;
    private boolean slowdown = false;
    private boolean playerEnter = true;
    private boolean playerExit;
    private double power = 0.1;

    public Cart(World world, double d0, double d1, double d2, double power) {
        this(world, d0, d1, d2);
        this.power = power;
    }

    public Cart(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    @Override
    public void move(double x, double y, double z) {
        Location from = new Location(this.getWorld().getWorld(), locX, locY, locZ);
        updateDirection();
        double cos = FaceUtil.cos(direction);
        double sin = FaceUtil.sin(direction);
        motX = power * FaceUtil.cos(direction);
        motZ = power * FaceUtil.sin(direction);
        System.out.println(direction);
        super.move(x, y, z);
    }

    /*
    public void move(double dx, double dy, double dz) {
        Location from = new Location(this.getWorld().getWorld(), locX, locY, locZ);
        double x = motX;
        double y = motY;
        double z = motZ;
        Vector v = getFlyingVelocityMod();
        x *= v.getX();
        y *= v.getY();
        z *= v.getZ();
        //Bukkit.broadcastMessage(v.getX() + ", " + v.getY() + "," + v.getZ());
        Location to = from.clone().add(motX, motY, motZ);
        CartMoveEvent e = new CartMoveEvent(this, from, to);
        Bukkit.getPluginManager().callEvent(e);
        if (!e.isCancelled()) {
            Block b = from.getWorld().getBlockAt(RideManager.locInt(locX), RideManager.locInt(locY), RideManager.locInt(locZ));
            if (MagicAssistant.rideManager.isRail(b.getLocation())) {
                BlockMinecartTrackAbstract.EnumTrackPosition pos = MagicAssistant.rideManager.getTrackPosition(b);
                int[][] aint = matrix[pos.a()];
                double d1 = (double) (aint[1][0] - aint[0][0]);
                double d2 = (double) (aint[1][2] - aint[0][2]);
                double d3 = Math.sqrt(d1 * d1 + d2 * d2);
                double d5 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
                this.motX = d5 * d1 / d3;
                this.motZ = d5 * d2 / d3;
                super.velocityChanged = true;
                return;
            }
        }
        super.motX = 0.0;
        super.motY = 0.0;
        super.motZ = 0.0;
        super.positionChanged = false;
    }

    /*
    public void move(double x, double y, double z) {
        Location from = new Location(this.getWorld().getWorld(), locX, locY, locZ);
        Location to = from.clone().add(x, y, z);
        CartMoveEvent e = new CartMoveEvent(this, from, to);
        Bukkit.getPluginManager().callEvent(e);
        if (!e.isCancelled()) {
            if (!slowdown) {
                updateSpeed();
                setSpeed(getSpeed());
            }
            super.move(x, y, z);
            return;
        }
        super.motX = 0.0;
        super.motY = 0.0;
        super.motZ = 0.0;
        super.positionChanged = false;
    }
    */

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
        //Bukkit.broadcastMessage(vec.toString());
        if (direction == null) {
            direction = FaceUtil.getDirection(vec);
        }
        BlockFace mdir = getMovementDirection(vec);
        updateDirection(mdir);
    }

    public void updateDirection(BlockFace movement) {
        if (this.direction == null) {
            this.direction = movement;
        }
        if (this.directionTo == null) {
            if (FaceUtil.isSubCardinal(movement))
                this.directionTo = FaceUtil.getDirection(getBukkitEntity().getVelocity(), false);
            else {
                this.directionTo = movement;
            }
        }
        boolean fromInvalid = this.directionFrom == BlockFace.SELF;
        if (fromInvalid) {
            this.directionFrom = this.directionTo;
        }
        this.direction = movement;
        if (FaceUtil.isSubCardinal(this.direction)) {
            BlockFace raildirection = getRailDirection();
            if (this.direction == BlockFace.NORTH_EAST) {
                this.directionTo = (raildirection == BlockFace.NORTH_WEST ? BlockFace.EAST : BlockFace.NORTH);
            } else if (this.direction == BlockFace.SOUTH_EAST) {
                this.directionTo = (raildirection == BlockFace.NORTH_EAST ? BlockFace.SOUTH : BlockFace.EAST);
            } else if (this.direction == BlockFace.SOUTH_WEST) {
                this.directionTo = (raildirection == BlockFace.NORTH_WEST ? BlockFace.SOUTH : BlockFace.WEST);
            } else if (this.direction == BlockFace.NORTH_WEST) {
                this.directionTo = (raildirection == BlockFace.NORTH_EAST ? BlockFace.WEST : BlockFace.NORTH);
            }
        } else {
            this.directionTo = this.direction;
        }
        if (fromInvalid) {
            this.directionFrom = this.directionTo;
        }
    }

    private boolean isSloped() {
        BlockMinecartTrackAbstract.EnumTrackPosition pos =
                MagicAssistant.rideManager.getTrackPosition(getBukkitEntity().getLocation().getBlock());
        return pos.name().contains("ASCENDING");
    }

    public BlockFace getMovementDirection(Vector movement) {
        BlockFace raildirection = getRailDirection();
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
                System.out.println("sub");
                direction = movementDir;
            } else {
                System.out.println("not");
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

    public BlockFace getRailDirection() {
        Location loc = getBukkitEntity().getLocation();
        BlockMinecartTrackAbstract.EnumTrackPosition pos = MagicAssistant.rideManager.getTrackPosition(loc.getBlock());
        switch (pos) {
            case NORTH_SOUTH:
                return BlockFace.SOUTH;
            case EAST_WEST:
                return BlockFace.WEST;
            case ASCENDING_EAST:
                return BlockFace.EAST;
            case ASCENDING_WEST:
                return BlockFace.WEST;
            case ASCENDING_NORTH:
                return BlockFace.NORTH;
            case ASCENDING_SOUTH:
                return BlockFace.SOUTH;
            case SOUTH_EAST:
                return BlockFace.SOUTH_EAST;
            case SOUTH_WEST:
                return BlockFace.SOUTH_WEST;
            case NORTH_WEST:
                return BlockFace.NORTH_WEST;
            case NORTH_EAST:
                return BlockFace.NORTH_EAST;
            default:
                return BlockFace.NORTH;
        }
    }
}