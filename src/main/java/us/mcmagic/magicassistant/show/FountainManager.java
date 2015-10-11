package us.mcmagic.magicassistant.show;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.MathHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.util.Vector;
import us.mcmagic.magicassistant.MagicAssistant;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FountainManager implements Listener {
    public List<Fountain> fountains = new ArrayList<>();
    private List<UUID> blocks = new ArrayList<>();
    private int num = 0;

    public FountainManager() {
        /*
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(MagicAssistant.getInstance(),
                PacketType.Play.Server.SPAWN_ENTITY, PacketType.Play.Server.SPAWN_POSITION,
                PacketType.Play.Server.ENTITY_TELEPORT, PacketType.Play.Server.UPDATE_ENTITY_NBT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                List<Integer> ints = event.getPacket().getIntegers().getValues();
                String msg = ChatColor.ITALIC + "";
                for (int i : ints) {
                    msg += i + " ";
                }
                Bukkit.broadcastMessage(event.getPacket().getType().name() + " " + msg);
            }
        });
        */
        start();
    }

    @SuppressWarnings("deprecation")
    public void start() {
        final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        Bukkit.getScheduler().runTaskTimer(MagicAssistant.getInstance(), new Runnable() {
            public void run() {
                for (Fountain fon : new ArrayList<>(fountains)) {
                    double duration = fon.getDuration();
                    fon.setDuration(duration - 0.1);
                    if (duration <= 0) {
                        fountains.remove(fon);
                        continue;
                    }
                    Location loc = fon.getLocation();
                    int type = fon.getType();
                    byte data = fon.getData();
                    Vector force = fon.getForce();
                    PacketContainer pc = manager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
                    double x = force.getX();
                    double y = force.getY();
                    double z = force.getZ();
                    double var = 3.9D;
                    if (x < -var) {
                        x = -var;
                    }
                    if (y < -var) {
                        y = -var;
                    }
                    if (z < -var) {
                        z = -var;
                    }
                    if (x > var) {
                        x = var;
                    }
                    if (y > var) {
                        y = var;
                    }
                    if (z > var) {
                        z = var;
                    }
                    double e = (int) (x * 8000.0D);
                    double f = (int) (y * 8000.0D);
                    double g = (int) (z * 8000.0D);
                    try {
                        pc.getIntegers()
                                .write(0, getId())
                                .write(1, getLoc("x", loc))
                                .write(2, getLoc("y", loc))
                                .write(3, getLoc("z", loc))
                                .write(4, (int) e)
                                .write(5, (int) f)
                                .write(6, (int) g)
                                .write(7, getLoc("yaw", loc))
                                .write(8, getLoc("pitch", loc))
                                .write(9, 70)
                                .write(10, type | (data << 0x10));
                    } catch (NoSuchFieldException | IllegalAccessException e1) {
                        e1.printStackTrace();
                    }
                    for (Player tp : Bukkit.getOnlinePlayers()) {
                        try {
                            manager.sendServerPacket(tp, pc);
                        } catch (InvocationTargetException e1) {
                            e1.printStackTrace();
                        }
                    }
                    /*
                    Entity fe = new EntityFallingBlock(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(),
                            loc.getZ(), Block.getById(type).fromLegacyData(data));
                    Bukkit.broadcastMessage(type + ":" + data);
                    fe.motX = force.getX();
                    fe.motY = force.getY();
                    fe.motZ = force.getZ();
                    PacketPlayOutSpawnEntity ftn = new PacketPlayOutSpawnEntity(fe, 1);
                    for (Player tp : Bukkit.getOnlinePlayers()) {
                        ((CraftPlayer) tp).getHandle().playerConnection.sendPacket(ftn);
                    }
                    FallingBlock fb = loc.getWorld().spawnFallingBlock(loc, type, data);
                    fb.setVelocity(force);
                    fb.setDropItem(false);
                    //f.setDuration(duration - 0.1);
                    blocks.add(fb.getUniqueId());
                    */
                }
            }
        }, 0L, 2L);
    }

    private Integer getId() throws NoSuchFieldException, IllegalAccessException {
        Field f = Entity.class.getDeclaredField("entityCount");
        f.setAccessible(true);
        Object o = f.get(null);
        int i = (int) o;
        f.setInt(null, i + 1);
        return i;
    }

    private Integer getLoc(String pos, Location loc) {
        if (pos.equalsIgnoreCase("yaw")) {
            return MathHelper.d(loc.getYaw() * 256.0F / 360.0F);
        }
        if (pos.equalsIgnoreCase("pitch")) {
            return MathHelper.d(loc.getPitch() * 256.0F / 360.0F);
        }
        double val = 0.0;
        switch (pos) {
            case "x":
                val = loc.getX();
                break;
            case "y":
                val = loc.getY();
                break;
            case "z":
                val = loc.getZ();
                break;
        }
        return MathHelper.floor(val * 32.0D);
    }

    @EventHandler
    public void entityToBlock(EntityChangeBlockEvent event) {
        if (blocks.contains(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
            event.getEntity().remove();
        }
    }

    public void addFountain(Fountain fountain) {
        fountains.add(fountain);
    }
}