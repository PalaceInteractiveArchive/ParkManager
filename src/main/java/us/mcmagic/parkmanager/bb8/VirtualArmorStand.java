package us.mcmagic.parkmanager.bb8;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.parkmanager.ParkManager;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Marc on 4/27/16
 */
public class VirtualArmorStand {

    private EntityArmorStand e;
    private ArrayList<UUID> visible_to;
    private ArrayList<UUID> white_black_list;
    private boolean whitelist;
    private Location loc;
    private double range = 200.0d;
    private int timer;
    private ItemStack[] items;
    private float[] headpos;
    private String nameplate;

    public VirtualArmorStand(Location loc, boolean visible) {
        headpos = new float[3];
        e = new EntityArmorStand(((CraftWorld) loc.getWorld()).getHandle());
        e.setInvisible(!visible);
        visible_to = new ArrayList<>();
        white_black_list = new ArrayList<>();
        whitelist = false;
        e.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0, 0);
        items = new ItemStack[5];
        timer = Bukkit.getScheduler().scheduleSyncRepeatingTask(ParkManager.getInstance(), this::tick, 5L, 1L);
    }

    public Location getLocation() {
        return loc.clone();
    }

    private void tick() {
        e.getBukkitEntity().getWorld().getPlayers().forEach(p -> setVisible(p, (!whitelist ^ white_black_list.contains(p.getUniqueId())) && p.getWorld().equals(loc.getWorld()) && p.getLocation().distance(loc) <= range));
        new ArrayList<>(visible_to).stream().filter(uuid -> Bukkit.getPlayer(uuid) == null || !Bukkit.getPlayer(uuid).isOnline()).forEach(visible_to::remove);
    }

    public String getNameplate() {
        return nameplate;
    }

    public void setNameplate(String nameplate) {
        if (!nameplate.equals(this.nameplate)) {
            this.nameplate = nameplate;
            e.getBukkitEntity().getWorld().getPlayers().forEach(this::updateNamePlate);
        }
    }

    public void addToAccessList(UUID uuid) {
        white_black_list.add(uuid);
    }

    public void removeFromAccessList(UUID uuid) {
        white_black_list.remove(uuid);
    }

    private void setVisible(Player p, boolean visible) {
        if (visible) {
            if (!visible_to.contains(p.getUniqueId())) {
                visible_to.add(p.getUniqueId());
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(e));
                updateEquipment(p);
                updateNamePlate(p);
            }
        } else {
            if (p != null && visible_to.contains(p.getUniqueId())) {
                visible_to.remove(p.getUniqueId());
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(e.getId()));
            }
        }
    }

    public void teleport(Location loc) {
        this.loc = loc.clone();
        double x = loc.getX() * 32.0D;
        double y = loc.getY() * 32.0D;
        double z = loc.getZ() * 32.0D;
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(e.getId(), (int) x, (int) y, (int) z, (byte) 0, (byte) 0, false);
        e.getBukkitEntity().getWorld().getPlayers().stream().filter(p -> (!whitelist ^ white_black_list.contains(p.getUniqueId())) && p.getWorld().equals(loc.getWorld()) && p.getLocation().distance(loc) <= range).forEach(p -> ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet));
    }

    public float[] getHeadPos() {
        return headpos.clone();
    }

    public void setHeadPos(float yaw, float pitch, float roll) {
        headpos[0] = yaw;
        headpos[1] = pitch;
        headpos[2] = roll;
        DataWatcher dw = e.getDataWatcher();
        dw.watch(11, new Vector3f(headpos[1], headpos[0], headpos[2]));
        e.getBukkitEntity().getWorld().getPlayers().stream()/*.filter(p -> visible_to.contains(p))*/.forEach(p -> ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityMetadata(e.getId(), dw, true)));
    }

    public void setYaw(float yaw) {
        setHeadPos(yaw, headpos[1], headpos[2]);
    }

    public void setPitch(float pitch) {
        setHeadPos(headpos[0], pitch, headpos[2]);
    }

    public void setRoll(float roll) {
        setHeadPos(headpos[0], headpos[1], roll);
    }


    public void remove() {
        Bukkit.getScheduler().cancelTask(timer);
        for (UUID uuid : new ArrayList<>(visible_to)) {
            setVisible(Bukkit.getPlayer(uuid), false);
        }
    }

    public void setVisible(boolean visible) {
        e.setInvisible(!visible);
        for (Player p : Bukkit.getOnlinePlayers()) {
            setVisible(p, false);
            setVisible(p, true);
        }
    }

    public boolean isWhitelist() {
        return whitelist;
    }

    public void setWhitelist(boolean whitelist) {
        this.whitelist = whitelist;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public VirtualArmorStand setItemInHand(ItemStack is) {
        setEquipment(0, is);
        return this;
    }

    public VirtualArmorStand setBoots(ItemStack is) {
        setEquipment(1, is);
        return this;
    }

    public VirtualArmorStand setLeggings(ItemStack is) {
        setEquipment(2, is);
        return this;
    }

    public VirtualArmorStand setChestplate(ItemStack is) {
        setEquipment(3, is);
        return this;
    }

    public VirtualArmorStand setHelmet(ItemStack is) {
        setEquipment(4, is);
        return this;
    }

    private void setEquipment(int slotid, ItemStack is) {
        items[slotid] = is;
        e.getBukkitEntity().getWorld().getPlayers().stream().filter(visible_to::contains).forEach(this::updateEquipment);
    }

    private void updateEquipment(Player p) {
        for (int i = 0; i < items.length; i++) {
            ItemStack is = items[i];
            if (is != null)
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(e.getId(), i, CraftItemStack.asNMSCopy(items[i])));
        }
    }

    private void updateNamePlate(Player p) {
        DataWatcher dw = e.getDataWatcher();
        dw.watch(2, ((nameplate) == null) ? "" : nameplate);
        dw.watch(3, (nameplate != null && !nameplate.isEmpty()) ? (byte) 1 : (byte) 0);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityMetadata(e.getId(), dw, true));
    }
}