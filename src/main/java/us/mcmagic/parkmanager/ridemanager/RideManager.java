package us.mcmagic.parkmanager.ridemanager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.server.v1_8_R3.EntityMinecartRideable;
import net.minecraft.server.v1_8_R3.PacketPlayInSteerVehicle;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import us.mcmagic.parkmanager.ParkManager;

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
    private List<Cart> carts = new ArrayList<>();

    public RideManager() {
        /*
        Bukkit.getScheduler().runTaskTimer(ParkManager.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Cart cart : new ArrayList<>(carts)) {
                    double power = cart.getPower();
                    cart.setSlowWhenEmpty(false);
                    cart.setVelocity(cart.getVelocity().clone().normalize().multiply(power));
                    cart.setMaxSpeed(power);
                }
            }
        }, 0L, 1L);
        registerProtocolLibListener();*/
    }

    public Cart spawn(Location loc) {
        if (!canSpawn(loc)) {
            return null;
        }
        return spawn(loc, 0.1);
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
            if (event.getNewCurrent() < event.getOldCurrent() || event.getNewCurrent() < 1) {
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
                    if (list.length == 2) {
                        double power = 0;
                        BlockFace direction;
                        int amount = 1;
                        try {
                            power += Double.parseDouble(list[1]);
                        } catch (Exception nfe) {
                            s.setLine(3, ChatColor.RED + "Number Error");
                            s.update();
                            return;
                        }
                        try {
                            amount = Integer.parseInt(s.getLine(2));
                        } catch (Exception nfe) {
                            s.setLine(3, ChatColor.RED + "No Amount");
                            s.update();
                            return;
                        }
                        if (power < 0) {
                            s.setLine(2, ChatColor.RED + "Negative Power");
                            s.update();
                            return;
                        }
                        if (power > 5) {
                            s.setLine(2, ChatColor.RED + "Power > 5");
                            s.update();
                            return;
                        }
                        if (amount < 1 || amount > 10) {
                            s.setLine(3, ChatColor.RED + "Invalid Amount");
                            s.update();
                            return;
                        }
                        if (amount == 1) {
                            Cart cart = spawn(loc.add(0.5, 0, 0.5), power);
                            return;
                        }
                        spawnTrain(loc.add(0.5, 0, 0.5), amount, power);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void spawnTrain(Location loc, double amount, double power) {
        List<Cart> carts = new ArrayList<>();
        double addX = 0;
        double addY = 0;
        double addZ = 0;
        for (int i = 0; i < amount; i++) {
            if (i == 0) {
                Cart c = spawn(loc, power);
                carts.add(c);
            }
        }
    }

    private Cart spawn(Location loc, double power) {
        if (!canSpawn(loc)) {
            return null;
        }
        EntityMinecartRideable ent = new EntityMinecartRideable(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(),
                loc.getY(), loc.getZ());
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(ent);
        Cart cart = new Cart(((CraftServer) Bukkit.getServer()), ent);
        cart.setSlowWhenEmpty(false);
        cart.setVelocity(cart.getVelocity().clone().normalize().multiply(power));
        cart.setMaxSpeed(power);
        carts.add(cart);
        return cart;
    }

    private BlockFace dirFromString(String s) throws Exception {
        String dir = s.toLowerCase();
        switch (dir) {
            case "n":
                return BlockFace.NORTH;
            case "e":
                return BlockFace.EAST;
            case "s":
                return BlockFace.SOUTH;
            case "w":
                return BlockFace.WEST;
            default:
                throw new Exception("Direction not set");
        }
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
    }

    private void registerProtocolLibListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(ParkManager.getInstance(),
                PacketType.Play.Client.STEER_VEHICLE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (player.isInsideVehicle()) {
                    Entity vehicle = player.getVehicle();
                    if (!(vehicle instanceof FallingBlock) && !(vehicle instanceof ArmorStand) && !(vehicle instanceof Cart)) {
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
                cart.setStation(station);//ENTITY_GENERIC_BURN
                to.getWorld().playSound(to, Sound.FIZZ, 10, 2);
                return;
            }
            if (type.equals(SignType.DESTROY)) {
                if (!isPowered) {
                    return;
                }
                cart.getTrain().despawn();
                return;
            }
            if (type.equals(SignType.SPEED)) {
                if (!isPowered) {
                    return;
                }
                Double power;
                try {
                    power = Double.parseDouble(s.getLine(3));
                } catch (NumberFormatException nfe) {
                    s.setLine(3, ChatColor.RED + "Number Error");
                    s.update();
                    return;
                }
                if (power > 1) {
                    s.setLine(3, ChatColor.RED + "Power > 1");
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
        return type.name().toLowerCase().contains("rail");
    }

    public static int locInt(double d) {
        if (d < 0) {
            return (int) Math.floor(d);
        }
        return (int) Math.ceil(d);
    }
}