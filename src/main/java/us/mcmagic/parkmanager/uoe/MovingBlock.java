package us.mcmagic.parkmanager.uoe;

import net.minecraft.server.v1_8_R3.EntityFallingBlock;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

/**
 * Created by Marc on 5/6/15
 */
public class MovingBlock extends EntityFallingBlock {
    private double iX;
    private double iY;
    private double iZ;
    private int amount;
    private int i = 1;
    private Material type;
    private byte data;

    protected MovingBlock(World world, double d0, double d1, double d2, IBlockData iblockdata, double iX, double iY,
                          double iZ, int amount, Material type, byte data) {
        super(world, d0, d1, d2, iblockdata);
        this.iX = iX;
        this.iY = iY;
        this.iZ = iZ;
        this.amount = amount;
        this.type = type;
        this.data = data;
        locX = locX + 0.5;
        locZ = locZ + 0.5;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void move(double x, double y, double z) {
        motX = iX;
        motY = iY;
        motZ = iZ;
        velocityChanged = true;
        this.locX = (locX + iX);
        this.locY = (locY + iY);
        this.locZ = (locZ + iZ);
        positionChanged = true;
        i++;
        Location loc = new Location(getWorld().getWorld(), locX, locY, locZ);
        if (i > amount) {
            Block b = loc.getBlock();
            b.setType(type);
            b.setData(data);
            die();
        }
    }

    @Override
    public void die() {
        if (i <= amount) {
            return;
        }
        if (passenger != null) {
            ArmorStand stand = getBukkitEntity().getWorld().spawn(getBukkitEntity().getLocation().add(0, -0.74625, 0),
                    ArmorStand.class);
            stand.setGravity(false);
            stand.setVisible(false);
            for (Entity e : stand.getWorld().getEntities()) {
                if (passenger.equals(e.getUniqueId())) {
                    e.setPassenger(stand);
                }
            }
            dead = true;
        }
    }
}