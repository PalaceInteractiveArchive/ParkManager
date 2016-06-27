package us.mcmagic.parkmanager.bb8;

import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPig;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.mcmagic.parkmanager.ParkManager;

import java.util.Random;
import java.util.UUID;

/**
 * Created by Marc on 4/27/16
 */
public class BB8 implements Listener {
    private Pig ai_entity;
    private int timerid;
    private int tick;
    private Location destination;
    private VirtualArmorStand model_body;
    private VirtualArmorStand model_head;
    private Location last_loc;
    private UUID owner;
    private boolean animating;
    private int mode;

    public BB8(Location loc) {
        tick = 0;
        mode = 0;
        animating = false;
        Bukkit.getPluginManager().registerEvents(this, ParkManager.getInstance());
        ai_entity = (Pig) loc.getWorld().spawnEntity(loc, EntityType.PIG);
        ai_entity.setBreed(false);
        ((CraftEntity) ai_entity).getHandle().b(true);
        ai_entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));
        model_body = new VirtualArmorStand(loc, false);
        model_body.setHelmet(new ItemStack(Material.SHEARS, 1, (short) 2));
        model_head = new VirtualArmorStand(loc, false);
        model_head.setHelmet(new ItemStack(Material.SHEARS, 1, (short) 1));
        timerid = Bukkit.getScheduler().scheduleSyncRepeatingTask(ParkManager.getInstance(), this::tick, 1L, 1L);
        last_loc = loc.clone();
        BB8Manager.get().registerDroid(this);
    }

    public Player getOwner() {
        if (owner == null) return null;
        return Bukkit.getPlayer(owner);
    }

    public void setOwner(Player owner) {
        this.owner = (owner == null) ? null : owner.getUniqueId();
    }

    public void setYaw(float yaw) {
        Location tp = ai_entity.getLocation();
        tp.setPitch(0);
        tp.setYaw(yaw);
        ai_entity.teleport(tp);
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public float getYaw() {
        return ai_entity.getLocation().getYaw();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent evt) {
        if (evt.getEntity().equals(ai_entity)) evt.setCancelled(true);
    }

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent evt) {
        if (evt.getTarget().equals(ai_entity)) evt.setCancelled(true);
    }

    @EventHandler
    public void onTrade(PlayerInteractEntityEvent evt) {
        if (evt.getRightClicked().equals(ai_entity)) evt.setCancelled(true);
        else return;
        Player p = evt.getPlayer();
        if (p.getItemInHand().getType().equals(Material.NETHER_STAR)) {
            this.remove();
            p.sendMessage(ChatColor.RED + "You've removed this droid! It will never see the light of day again.");
            return;
        }
        double yaw = PosRotUtils.getYaw(p.getLocation(), ai_entity.getLocation());
        if (!p.getUniqueId().equals(owner)) {
            if (owner == null && BB8Manager.get().getDroid(p) == null) {
                setOwner(p);
                p.sendMessage(ChatColor.GOLD + "You made a friend!");
                ai_entity.getWorld().playEffect(ai_entity.getLocation().clone().add(0, 1.5, 0), Effect.HEART, 1);
                if (!animating) {
                    animating = true;
                    setYaw((float) yaw);
                    for (int i = 1; i <= 20; i++) {
                        final int a = i;
                        Bukkit.getScheduler().scheduleSyncDelayedTask(ParkManager.getInstance(), () -> {
                            try {
                                model_head.setYaw((float) (yaw + a * 18.0d));
                                if (a == 20) animating = false;
                            } catch (Exception e) {
                                //Shit happens
                            }
                        }, i);
                    }
                }
            } else {
                p.sendMessage(ChatColor.RED + ((owner != null) ? "This droid is already owned by another online player!" : "You cannot own more than one BB8!"));
                if (!animating) {
                    animating = true;
                    setYaw((float) yaw);
                    for (int i = 1; i <= 20; i++) {
                        final int a = i;
                        Bukkit.getScheduler().scheduleSyncDelayedTask(ParkManager.getInstance(), () -> {
                            try {
                                model_head.setYaw((float) (yaw + Math.sin(((double) a) / ((double) 20) * 2.0 * Math.PI) * 35.0d));
                                if (a == 20) animating = false;
                            } catch (Exception e) {
                                //Shit happens
                            }
                        }, i);
                    }
                }
            }
        } else {
            ItemMenu menu = new ItemMenu(getNamePlate(), p);
            menu.addItem(0, new MenuItem(new CustomIS().setMaterial(((mode == 0) ? Material.EYE_OF_ENDER : Material.ENDER_PEARL)).setName(ChatColor.AQUA + "Roaming Mode"), () -> {
                getOwner().sendMessage((mode == 0) ? ChatColor.RED + "BB-8 is currently already in roaming mode!" : ChatColor.GREEN + "BB-8 has entered roaming mode!");
                setMode(0);
                menu.close();
            }));
            menu.addItem(1, new MenuItem(new CustomIS().setMaterial(((mode == 1) ? Material.EYE_OF_ENDER : Material.ENDER_PEARL)).setName(ChatColor.AQUA + "Companion Mode"), () -> {
                getOwner().sendMessage((mode == 1) ? ChatColor.RED + "BB-8 is currently already in companion mode!" : ChatColor.GREEN + "BB-8 has entered companion mode!");
                setMode(1);
                menu.close();
            }));
            menu.addItem(8, new MenuItem(new CustomIS().setMaterial(Material.SLIME_BALL).setName(ChatColor.RED + "Disown"), () -> {
                getOwner().sendMessage(ChatColor.GOLD + "You have disowned your BB-8!");
                setMode(0);
                setOwner(null);
                menu.close();
            }));
            menu.show();
        }
    }

    private long roam_last_set;

    private void tick() {
        if (ai_entity == null || ai_entity.isDead()) {
            ai_entity = (Pig) model_head.getLocation().getWorld().spawnEntity(model_head.getLocation(), EntityType.PIG);
            ai_entity.setBreed(false);
            ((CraftEntity) ai_entity).getHandle().b(true);
        }
        if (owner != null && (getOwner() == null || !getOwner().isOnline() || !getOwner().getWorld().equals(ai_entity.getWorld())))
            owner = null;
        if (destination != null) {
            navigate(destination, false);
        }
        model_body.teleport(ai_entity.getLocation().clone().add(0, -0.8125, 0));
        model_head.teleport(ai_entity.getLocation().clone().add(0, -0.3125, 0));
        float pitch = (float) (ai_entity.getLocation().distance(last_loc) / (Math.PI * 1.125) * 360.0d) + model_body.getHeadPos()[1];
        while (pitch >= 360) pitch -= 360;
        while (pitch < 0) pitch += 360;
        float yaw = ai_entity.getLocation().getYaw();
        model_body.setHeadPos(yaw, pitch, 0);
        if (!animating) model_head.setYaw(yaw);
        last_loc = ai_entity.getLocation().clone();
        model_head.setNameplate(getNamePlate());
        int roam_range = 50;
        int roam_interval = 15000;
        if (mode == 0) {
            if (destination == null || System.currentTimeMillis() - roam_last_set > roam_interval || (getOwner() != null && ai_entity.getLocation().distance(getOwner().getLocation()) > Math.sqrt(Math.pow(roam_range, 2) * 2) * 1.1)) {
                Random rand = new Random();
                Location navloc = null;
                Location source = (getOwner() == null) ? ai_entity.getLocation().clone() : getOwner().getLocation().clone();
                for (int i = 0; (i < roam_range && navloc == null); i++)
                    navloc = PosRotUtils.closestGround(source.add((rand.nextDouble() * 2.0 - 1.0) * 24.0, 0, (rand.nextDouble() * 2.0 - 1.0) * 24.0));
                if (navloc != null) {
                    if (ai_entity.getLocation().distance(navloc) > Math.sqrt(Math.pow(roam_range, 2) * 2) * 1.1)
                        ai_entity.teleport(navloc);
                    else
                        navigate(navloc);
                    roam_last_set = System.currentTimeMillis();
                }
            }
        }
        if (mode == 1) {
            if (getOwner() == null) {
                mode = 0;
                tick++;
                return;
            }
            if (ai_entity.getLocation().distance(getOwner().getLocation()) > 5.0 || System.currentTimeMillis() - roam_last_set > roam_interval * 1.5) {
                Random rand = new Random();
                Location navloc = null;
                for (int i = 0; (i < roam_range && navloc == null); i++)
                    navloc = PosRotUtils.closestGround(getOwner().getLocation().clone().add((rand.nextDouble() * 2.0 - 1.0) * 5.0, 0, (rand.nextDouble() * 2.0 - 1.0) * 5.0));
                if (navloc != null) {
                    if (ai_entity.getLocation().distance(navloc) > 50.0 || Math.abs(Math.max(ai_entity.getLocation().getY(), getOwner().getLocation().getY()) - Math.min(ai_entity.getLocation().getY(), getOwner().getLocation().getY())) > 8.0)
                        ai_entity.teleport(navloc);
                    else
                        navigate(navloc);
                    roam_last_set = System.currentTimeMillis();
                }
            }
        }
        tick++;
    }

    private String getNamePlate() {
        return (owner == null) ? ChatColor.GREEN + "Homeless BB-8" : ChatColor.GOLD + ChatColor.stripColor(getOwner().getDisplayName()) + "'s BB-8";
    }

    private void navigate(Location loc, boolean force) {
        Location target;
        if (ai_entity.getLocation().distance(loc) < 10.0) {
            target = loc.clone();
        } else if (ai_entity.getLocation().distance(loc) < 2) {
            target = null;
        } else {
            target = PosRotUtils.closestGround(ai_entity.getLocation().clone().add(loc.clone().toVector().subtract(ai_entity.getLocation().clone().toVector()).normalize().multiply(10.0)));
        }
        if (tick % 20 == 0 && target != null) {
            ((CraftPig) ai_entity).getHandle().getNavigation().a(target.getX(), target.getY(), target.getZ(), 0.75);
        }
    }

    public void navigate(Location loc) {
        if (loc != null) {
            destination = loc.clone();
            navigate(destination, true);
        } else {
            destination = null;
        }
    }

    public void remove() {
        model_head.remove();
        model_body.remove();
        ai_entity.remove();
        Bukkit.getScheduler().cancelTask(timerid);
        HandlerList.unregisterAll(this);
        BB8Manager.get().removeDroid(this);
    }
}
