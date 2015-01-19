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
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem().equals(this.stack))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().equals(this.stack)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.getInventory().removeItem(this.stack);
        player.setLevel(0);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
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
            if (loc.getBlock().getType() == Material.DIAMOND_BLOCK) {
                if ((snowball.getShooter() instanceof Player)) {
                    ((Player) snowball.getShooter()).playSound(snowball.getLocation(), Sound.NOTE_PLING, 10.0F, 1.0F);
                }

                player.setLevel(player.getLevel() + 1);
                player.sendMessage(ChatColor.BLUE + "+1");
                loc.getBlock().setType(Material.REDSTONE_BLOCK);
                Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
                    public void run() {
                        loc.getBlock().setType(Material.DIAMOND_BLOCK);
                    }
                }, 100L);
            }
        }
    }

    @EventHandler
    public void onProjectileHit1(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (((projectile instanceof Snowball)) && ((projectile.getShooter() instanceof Player))) {
            Snowball snowball = (Snowball) projectile;
            Player player = (Player) projectile.getShooter();
            final Location loc = projectile.getLocation().add(projectile.getVelocity().normalize());
            if ((loc.getBlock().getType() == Material.EMERALD_BLOCK) &&
                    ((snowball.getShooter() instanceof Player))) {
                ((Player) snowball.getShooter()).playSound(snowball.getLocation(), Sound.NOTE_PLING, 10.0F, 1.3F);

                player.setLevel(player.getLevel() + 5);
                player.sendMessage(ChatColor.BLUE + "+5");
                loc.getBlock().setType(Material.REDSTONE_BLOCK);
                Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
                    public void run() {
                        loc.getBlock().setType(Material.EMERALD_BLOCK);
                    }
                }
                        , 200L);
            }
        }
    }

    @EventHandler
    public void onProjectileHit2(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (((projectile instanceof Snowball)) && ((projectile.getShooter() instanceof Player))) {
            Snowball snowball = (Snowball) projectile;
            Player player = (Player) projectile.getShooter();
            final Location loc = projectile.getLocation().add(projectile.getVelocity().normalize());

            if ((loc.getBlock().getType() == Material.HUGE_MUSHROOM_1) &&
                    ((snowball.getShooter() instanceof Player))) {
                ((Player) snowball.getShooter()).playSound(snowball.getLocation(), Sound.NOTE_PLING, 10.0F, 1.6F);

                player.setLevel(player.getLevel() + 100);
                player.sendMessage(ChatColor.BLUE + "+100");
                loc.getBlock().setType(Material.REDSTONE_BLOCK);
                Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
                    public void run() {
                        loc.getBlock().setType(Material.HUGE_MUSHROOM_1);
                    }
                }
                        , 300L);
            }
        }
    }
}

