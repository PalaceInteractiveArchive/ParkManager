package us.mcmagic.magicassistant.ridemanager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.server.v1_8_R2.BlockMinecartTrackAbstract;
import net.minecraft.server.v1_8_R2.EnumDirection;
import net.minecraft.server.v1_8_R2.PacketPlayInSteerVehicle;
import net.minecraft.server.v1_8_R2.WorldServer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import us.mcmagic.magicassistant.MagicAssistant;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Marc on 4/1/15
 */
public class RideManager implements Listener {
    private List<Train> trains = new ArrayList<>();
    private List<BlockFace> faces = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

    public RideManager() {
        registerProtocolLibListener();
    }

    public Cart spawn(Location loc) {
        if (!canSpawn(loc)) {
            return null;
        }
        Cart cart = new Cart(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ());
        WorldServer realWorld = ((CraftWorld) loc.getWorld()).getHandle();
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
                        double power = 0;
                        /*
                        try {
                            direction = dirFromString(list[1]);
                        } catch (Exception e) {
                            s.setLine(1, ChatColor.RED + "Direction Error");
                            s.update();
                        }
                        */
                        try {
                            power += Double.parseDouble(list[1]);
                        } catch (Exception nfe) {
                            s.setLine(1, ChatColor.RED + "Number Error");
                            s.update();
                            return;
                        }
                        Cart cart = spawn(loc.add(0.5, 0, 0.5), power);
                        cart.setPower(power);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Cart spawn(Location loc, double power) {
        if (!canSpawn(loc)) {
            return null;
        }
        Cart cart = new Cart(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ(), power);
        WorldServer realWorld = ((CraftWorld) loc.getWorld()).getHandle();
        realWorld.addEntity(cart);
        return cart;
    }

    private EnumDirection dirFromString(String s) {
        String dir = s.toLowerCase();
        switch (dir) {
            case "n":
                return EnumDirection.NORTH;
            case "e":
                return EnumDirection.EAST;
            case "s":
                return EnumDirection.SOUTH;
            case "w":
                return EnumDirection.WEST;
            default:
                return null;
        }
    }

    private void registerProtocolLibListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(MagicAssistant.getInstance(),
                PacketType.Play.Client.STEER_VEHICLE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (player.isInsideVehicle()) {
                    Entity vehicle = player.getVehicle();
                    if (!(vehicle instanceof Cart) && !(vehicle instanceof FallingBlock) && !(vehicle instanceof ArmorStand)) {
                        return;
                    }
                    if (event.getPacket().getHandle() instanceof PacketPlayInSteerVehicle) {
                        try {
                            Field f = PacketPlayInSteerVehicle.class.getDeclaredField("d");
                            f.setAccessible(true);
                            f.set(event.getPacket().getHandle(), false);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
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
            boolean isPowered = isSignPowered(s);
            if (type.equals(SignType.STATION)) {
                if (!isPowered) {
                    return;
                }
                Station station = new Station(s);
                cart.setStation(station);
                to.getWorld().playSound(to, Sound.FIZZ, 10, 2);
                return;
            }
            if (type.equals(SignType.DESTROY)) {
                if (!isPowered) {
                    return;
                }
                for (Cart c : cart.getTrain().getCarts()) {
                    c.die();
                }
                return;
            }
            if (type.equals(SignType.SPEED)) {
                if (!isPowered) {
                    return;
                }
                Integer power;
                try {
                    power = Integer.parseInt(s.getLine(3));
                } catch (NumberFormatException nfe) {
                    s.setLine(3, ChatColor.RED + "Number Error");
                    s.update();
                    return;
                }
                cart.setPower(power);
            }
        }
    }

    private boolean isSignPowered(Sign s) {
        if (s.getLine(0).startsWith("[+") || s.getLine(0).startsWith("[!")) {
            return true;
        }
        Block b = s.getBlock();
        for (BlockFace face : faces) {
            Block rel = b.getRelative(face);
            if (rel.isBlockPowered() && rel.getBlockPower() > 1) {
                return true;
            }
            if (rel.getType().equals(Material.REDSTONE_TORCH_ON) || rel.getType().equals(Material.REDSTONE_BLOCK)) {
                return true;
            }
        }
        return false;
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

    public static int locInt(double d) {
        if (d < 0) {
            return (int) Math.floor(d);
        }
        return (int) Math.ceil(d);
    }

    @SuppressWarnings("deprecation")
    public BlockMinecartTrackAbstract.EnumTrackPosition getTrackPosition(Block block) {
        if (!isRail(block.getLocation())) {
            return BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH;
        }
        byte data = block.getData();
        if (data > 9) {
            data -= 10;
        }
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
            default:
                return null;
        }
    }
}