package us.mcmagic.magicassistant.ridemanager;

import net.minecraft.server.v1_8_R2.EnumDirection;
import net.minecraft.server.v1_8_R2.WorldServer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Marc on 4/1/15
 */
public class RideManager implements Listener {
    private List<Train> trains = new ArrayList<>();
    private List<BlockFace> faces = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

    public Cart spawn(Location loc) {
        if (!canSpawn(loc)) {
            return null;
        }
        Cart cart = new Cart(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ());
        WorldServer realWorld = ((CraftWorld) loc.getWorld()).getHandle();
        realWorld.addEntity(cart);
        return cart;
    }

    private boolean canSpawn(Location loc) {
        for (Entity e : loc.getWorld().getEntities()) {
            if (!e.getType().equals(EntityType.MINECART)) {
                continue;
            }
            if (e.getLocation().distance(loc) < 3) {
                return false;
            }
        }
        return true;
    }

    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent event) {
        try {
            if (event.getNewCurrent() < 1) {
                return;
            }
            Block ob = event.getBlock();
            for (BlockFace face : faces) {
                Block b = ob.getRelative(face);
                if (!(b.getType().equals(Material.SIGN_POST) || b.getType().equals(Material.SIGN) ||
                        b.getType().equals(Material.WALL_SIGN))) {
                    //Not a sign
                    continue;
                }
                Sign s = (Sign) b.getState();
                if (!isRideSign(s)) {
                    //Not Ride Sign
                    continue;
                }
                SignType type = getSignType(s);
                if (type == null) {
                    //Unable to get type
                    continue;
                }
                if (type.equals(SignType.SPAWN)) {
                    Location loc = getRailFromSign(s);
                    if (loc == null) {
                        //No rail
                        continue;
                    }
                    String[] list = s.getLine(1).split(" ");
                    if (list.length == 1) {
                        //Spawning
                        spawn(loc.add(0.5, 0, 0.5));
                    } else {
                        Cart cart = spawn(loc.add(0.5, 0, 0.5));
                        EnumDirection d = cart.getDirection();
                        double power = 0;
                        try {
                            power += Double.parseDouble(list[1]);
                        } catch (NumberFormatException nfe) {
                            s.setLine(1, ChatColor.RED + "Number Error");
                            s.update();
                            return;
                        }
                        cart.setPower(power);
                        switch (d) {
                            case NORTH:
                                cart.setSpeed(new Vector(0, 0, -power));
                                break;
                            case EAST:
                                cart.setSpeed(new Vector(power, 0, 0));
                                break;
                            case SOUTH:
                                cart.setSpeed(new Vector(0, 0, power));
                                break;
                            case WEST:
                                cart.setSpeed(new Vector(-power, 0, 0));
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Vector getVector(EnumDirection d, double power) {
        switch (d) {
            case NORTH:
                return new Vector(0, 0, -power);
            case EAST:
                return new Vector(power, 0, 0);
            case SOUTH:
                return new Vector(0, 0, power);
            case WEST:
                return new Vector(-power, 0, 0);
        }
        return new Vector(0, 0, 0);
    }

    @EventHandler
    public void onCartMove(CartMoveEvent event) {
        Cart cart = event.getCart();
        if (cart.isAtStation()) {
            cart.getStation().handleMove(event);
            return;
        }
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
            Sign s = getSignFromRail(to);
            if (s == null) {
                return;
            }
            if (!isRideSign(s)) {
                return;
            }
            SignType type = getSignType(s);
            if (type == null) {
                return;
            }
            if (type.equals(SignType.STATION)) {
                Station station = new Station(s);
                cart.setStation(station);
                to.getWorld().playSound(to, Sound.FIZZ, 10, 2);
            }
            if (type.equals(SignType.DESTROY)) {
                for (Cart c : cart.getTrain().getCarts()) {
                    c.die();
                }
            }
            return;
        }
        event.setCancelled(false);
    }

    @EventHandler
    public void onCartDestroy(CartDestroyEvent event) {
    }

    public boolean isRideSign(Sign s) {
        String line = s.getLine(0);
        return line.equals("[+train]") || line.equals("[train]") || line.equals("[+cart]") || line.equals("[cart]");
    }

    public SignType getSignType(Sign s) {
        String line2 = s.getLine(1).toLowerCase();
        if (line2.startsWith("spawn")) {
            return SignType.SPAWN;
        }
        if (line2.startsWith("station")) {
            return SignType.STATION;
        }
        switch (line2) {
            case "destroy":
                return SignType.DESTROY;
            case "property":
                String line3 = s.getLine(2);
                switch (line3) {
                    case "maxspeed":
                        return SignType.SPEED;
                    case "speedlimit":
                        return SignType.SPEED;
                }
            default:
                return null;
        }
    }

    public Location getRailFromSign(Sign s) {
        Location loc = s.getLocation().clone().add(0, 2, 0);
        if (isRail(loc)) {
            return loc;
        } else {
            return null;
        }
    }

    public Sign getSignFromRail(Location loc) {
        if (isSign(loc.add(0, -2, 0))) {
            return (Sign) loc.getBlock().getState();
        } else {
            return null;
        }
    }

    public boolean isSign(Location loc) {
        Block b = loc.getBlock();
        return b.getType().equals(Material.SIGN_POST) || b.getType().equals(Material.SIGN) ||
                b.getType().equals(Material.WALL_SIGN);
    }

    public boolean isRail(Location loc) {
        Material type = loc.getBlock().getType();
        return type.equals(Material.RAILS) || type.equals(Material.DETECTOR_RAIL) || type.equals(Material.ACTIVATOR_RAIL)
                || type.equals(Material.POWERED_RAIL);
    }
}