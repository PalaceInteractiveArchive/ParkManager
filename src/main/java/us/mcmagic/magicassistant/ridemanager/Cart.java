package us.mcmagic.magicassistant.ridemanager;

import net.minecraft.server.v1_8_R3.BlockMinecartTrackAbstract;
import net.minecraft.server.v1_8_R3.EntityMinecartRideable;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.util.Vector;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.particles.ParticleUtil;

import java.util.UUID;

/**
 * Created by Marc on 4/1/15
 */
public class Cart extends EntityMinecartRideable {
    private Train train;
    private UUID passenger;
    private BlockFace direction;
    private BlockFace lastDirection;
    private boolean atStation = false;
    private Station station;
    private boolean slowdown = false;
    private boolean playerEnter = true;
    private double power = 0.1;
    private boolean ascending = false;
    private BlockFace turningDirectionTo;

    public Cart(World world, double d0, double d1, double d2, double power, BlockFace dir) {
        this(world, d0, d1, d2);
        this.power = power;
        this.direction = dir;
        this.lastDirection = dir;
        this.turningDirectionTo = dir;
    }

    public Cart(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    @Override
    public void move(double x, double y, double z) {
        Location from = new Location(this.getWorld().getWorld(), locX, locY, locZ);
        Vector v = getVelocity();
        double motX = v.getX();
        double motY = v.getY();
        double motZ = v.getZ();
        Location to = new Location(getWorld().getWorld(), locX + motX, locY + motY, locZ + motZ);
        CartMoveEvent event = new CartMoveEvent(this, from, to);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        setPosition(locX + motX, locY + motY, locZ + motZ);
    }

    @SuppressWarnings("deprecation")
    public Vector getVelocity() {
        BlockFace d = direction;
        String dir = d.name().toLowerCase();
        Vector v = new Vector(0, 0, 0);
        if (!MagicAssistant.rideManager.isRail(getBukkitEntity().getLocation())) {
            return v;
        }
        BlockMinecartTrackAbstract.EnumTrackPosition pos = getTrackType(getBukkitEntity().getLocation().getBlock().getData());
        if (pos == null) {
            return v;
        }
        double turnPwr = power * 0.7071067812;
        switch (pos) {
            case NORTH_SOUTH:
                if (ascending) {
                    ascending = false;
                    v.setY(0.0625);
                }
                lastDirection = d;
                if (direction != BlockFace.NORTH && direction != BlockFace.SOUTH) {
                    direction = turningDirectionTo;
                }
                if (direction.name().toLowerCase().contains("north")) {
                    v.setZ(-power);
                } else {
                    v.setZ(power);
                }
                break;
            case EAST_WEST:
                if (ascending) {
                    ascending = false;
                    v.setY(0.0625);
                }
                lastDirection = d;
                if (direction != BlockFace.EAST && direction != BlockFace.WEST) {
                    direction = turningDirectionTo;
                }
                if (direction.name().toLowerCase().contains("east")) {
                    v.setX(power);
                } else {
                    v.setX(-power);
                }
                break;
            case ASCENDING_EAST:
                ascending = true;
                lastDirection = d;
                if (direction != BlockFace.NORTH && direction != BlockFace.SOUTH) {
                    direction = turningDirectionTo;
                }
                if (direction.name().toLowerCase().contains("east")) {
                    v.setY(power);
                    v.setX(power);
                } else {
                    v.setY(-power);
                    v.setX(-power);
                }
                break;
            case ASCENDING_WEST:
                ascending = true;
                lastDirection = d;
                if (direction != BlockFace.NORTH && direction != BlockFace.SOUTH) {
                    direction = turningDirectionTo;
                }
                if (direction.name().toLowerCase().contains("east")) {
                    v.setY(power);
                    v.setX(-power);
                } else {
                    v.setY(-power);
                    v.setX(power);
                }
                break;
            case ASCENDING_NORTH:
                ascending = true;
                lastDirection = d;
                if (direction != BlockFace.NORTH && direction != BlockFace.SOUTH) {
                    direction = turningDirectionTo;
                }
                if (direction.name().toLowerCase().contains("north")) {
                    v.setY(power);
                    v.setZ(-power);
                } else {
                    v.setY(-power);
                    v.setZ(power);
                }
                break;
            case ASCENDING_SOUTH:
                ascending = true;
                lastDirection = d;
                if (direction != BlockFace.NORTH && direction != BlockFace.SOUTH) {
                    direction = turningDirectionTo;
                }
                if (direction.name().toLowerCase().contains("north")) {
                    v.setY(-power);
                    v.setZ(-power);
                } else {
                    v.setY(power);
                    v.setZ(power);
                }
                break;
            case SOUTH_EAST:
                if (ascending) {
                    ascending = false;
                    v.setY(0.0625);
                }
                lastDirection = d;
                if (dir.contains("north")) {
                    turningDirectionTo = BlockFace.EAST;
                    v.setZ(-turnPwr);
                    v.setX(turnPwr);
                } else {
                    turningDirectionTo = BlockFace.SOUTH;
                    v.setZ(turnPwr);
                    v.setX(-turnPwr);
                }
                break;
            case SOUTH_WEST:
                if (ascending) {
                    ascending = false;
                    v.setY(0.0625);
                }
                lastDirection = d;
                if (dir.contains("north")) {
                    turningDirectionTo = BlockFace.WEST;
                    v.setZ(-turnPwr);
                    v.setX(-turnPwr);
                } else {
                    turningDirectionTo = BlockFace.SOUTH;
                    v.setZ(turnPwr);
                    v.setX(turnPwr);
                }
                break;
            case NORTH_WEST:
                if (ascending) {
                    ascending = false;
                    v.setY(0.0625);
                }
                lastDirection = d;
                if (dir.contains("south")) {
                    turningDirectionTo = BlockFace.WEST;
                    v.setZ(turnPwr);
                    v.setX(-turnPwr);
                } else {
                    turningDirectionTo = BlockFace.NORTH;
                    v.setZ(-turnPwr);
                    v.setX(turnPwr);
                }
                break;
            case NORTH_EAST:
                if (ascending) {
                    ascending = false;
                    v.setY(0.0625);
                }
                lastDirection = d;
                if (dir.contains("south")) {
                    turningDirectionTo = BlockFace.EAST;
                    v.setZ(turnPwr);
                    v.setX(turnPwr);
                } else {
                    turningDirectionTo = BlockFace.NORTH;
                    v.setZ(-turnPwr);
                    v.setX(-turnPwr);
                }
                break;
        }
        return v;
    }

    @Override
    public void die() {
        CartDestroyEvent e = new CartDestroyEvent(this);
        Bukkit.getPluginManager().callEvent(e);
        if (!e.isCancelled()) {
            CraftEntity entity = getBukkitEntity();
            ParticleUtil.spawnParticle(ParticleEffect.SMOKE, entity.getLocation().add(0, 0.3, 0), 0.1f, 0.1f, 0.1f, 0, 5);
            entity.getWorld().playSound(getBukkitEntity().getLocation(), Sound.FIZZ, 2, 2);
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
        if (s != null) {
            Bukkit.getScheduler().runTaskLater(MagicAssistant.getInstance(), new Runnable() {
                @Override
                public void run() {
                    setPower(station.getLaunchPower());
                    removeStation();
                }
            }, (long) (s.getLength() * 20));
        }
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

    public BlockMinecartTrackAbstract.EnumTrackPosition getTrackType(byte data) {
        switch (data) {
            case 0:
                return BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH;
            case 1:
                return BlockMinecartTrackAbstract.EnumTrackPosition.EAST_WEST;
            case 2:
                return BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_EAST;
            case 3:
                return BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_WEST;
            case 4:
                return BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_NORTH;
            case 5:
                return BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_SOUTH;
            case 6:
                return BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST;
            case 7:
                return BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST;
            case 8:
                return BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST;
            case 9:
                return BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST;
        }
        return null;
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

    public boolean canSlowdown() {
        return slowdown;
    }

    public void setSlowdown(boolean slowdown) {
        this.slowdown = slowdown;
    }

    private boolean isSloped() {
        BlockMinecartTrackAbstract.EnumTrackPosition pos =
                MagicAssistant.rideManager.getTrackPosition(getBukkitEntity().getLocation().getBlock());
        return pos.name().toLowerCase().contains("ascending");
    }
}