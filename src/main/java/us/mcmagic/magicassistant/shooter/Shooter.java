package us.mcmagic.magicassistant.shooter;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.magicassistant.MagicAssistant;

/**
 * Created by Jacob on 1/18/15.
 */
@SuppressWarnings("deprecation")
public class Shooter implements Listener {
    private ItemStack stack;
    public MagicAssistant pl;

    public Shooter(MagicAssistant instance) {
        pl = instance;

    }

    @EventHandler
    public void oninventoryclick(InventoryClickEvent event) {
        if (event.getCurrentItem().equals(this.stack))
            event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().equals(this.stack)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.getInventory().removeItem(this.stack);
        player.setLevel(0);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.PHYSICAL) {
            return;
        }
        if (e.getPlayer().getItemInHand().equals(this.stack)) {
            e.setCancelled(true);
            e.getPlayer().launchProjectile(Snowball.class);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (((projectile instanceof Snowball)) && ((projectile.getShooter() instanceof Player))) {
            Snowball snowball = (Snowball) projectile;
            Player player = (Player) projectile.getShooter();
            final Location loc = projectile.getLocation().add(projectile.getVelocity().normalize());


            if ((snowball.getShooter() instanceof Player)) {
                ((Player) snowball.getShooter()).playSound(snowball.getLocation(), Sound.NOTE_PLING, 10.0F, 1.0F);
            }
            final int amount = getPoint(loc.getBlock().getType());
            player.setLevel(player.getLevel() + amount);
            if (amount > 0) {
                player.sendMessage(ChatColor.BLUE + "+" + amount);
                loc.getBlock().setType(Material.REDSTONE_BLOCK);
                Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
                    public void run() {
                        loc.getBlock().setType(getMaterial(amount));
                    }
                }
                        , 100L);
            }

        }
    }

    public int getPoint(Material type) {
        switch (type) {
            case GOLD_BLOCK:
                return 1;
            case DIAMOND_BLOCK:
                return 5;
            case EMERALD_BLOCK:
                return 100;
            default:
                return 0;


        }
    }

    public Material getMaterial(int amount) {
        switch (amount) {
            case 1:
                return Material.GOLD_BLOCK;
            case 5:
                return Material.DIAMOND_BLOCK;
            case 100:
                return Material.EMERALD_BLOCK;
            default:
                return Material.AIR;
        }
    }

}



