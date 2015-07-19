package us.mcmagic.magicassistant.ridemanager;

import net.minecraft.server.v1_8_R3.BlockMinecartTrackAbstract;
import net.minecraft.server.v1_8_R3.EntityMinecartRideable;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
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
    private boolean atStation = false;
    private Station station;
    private boolean slowdown = false;
    private boolean playerEnter = true;
    private double power = 0.1;
    private boolean ascending = false;
    private RailRider railRider;
    public BlockFace lastDirection;

    public Cart(World world, double d0, double d1, double d2, double power, BlockFace dir) {
        this(world, d0, d1, d2);
        this.power = power;
        lastDirection = dir;
    }

    public Cart(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
        railRider = new RailRider(this);
        lastDirection = BlockFace.NORTH;
    }

    @Override
    public void move(double x1, double y1, double z1) {
        Bukkit.broadcastMessage(ChatColor.GREEN + "START" + locY);
        Location from = getLoc();
        Location to = railRider.next();
        ParticleUtil.spawnParticle(ParticleEffect.DRIP_LAVA, to, 0f, 0f, 0f, 0f, 1);
        CartMoveEvent event = new CartMoveEvent(this, from.clone(), to.clone());
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        /*
        this.motX = v.getX();
        this.motY = v.getY();
        this.motZ = v.getZ();
        */
        Bukkit.broadcastMessage(to.toString());
        setPosition(to.getX(), to.getY(), to.getZ());
        Bukkit.broadcastMessage(ChatColor.RED + "STOP " + locY);
    }

    public Location getLoc() {
        return new Location(getWorld().getWorld(), locX, locY, locZ);
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
}