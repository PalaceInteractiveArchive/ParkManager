package us.mcmagic.magicassistant.shooter;

import org.bukkit.*;
import org.bukkit.block.Block;
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
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;

import java.util.*;

/**
 * Created by Jacob on 1/18/15.
 */
@SuppressWarnings("deprecation")
public class Shooter implements Listener {
    private static ItemStack stack;
    private static HashMap<UUID, ItemStack> itemMap = new HashMap<>();
    private static HashMap<Long, Block> locations = new HashMap<>();
    public static List<UUID> ingame = new ArrayList<>();
    public MagicAssistant pl;
    public static String game;

    public Shooter(MagicAssistant instance) {
        pl = instance;
        if (pl.getConfig().getString("shooter").equalsIgnoreCase("buzz")) {
            stack = new ItemCreator(Material.WOOD_HOE, ChatColor.BLUE + "Ray Gun", Arrays.asList(ChatColor.GREEN +
                    "Click to shoot!"));
            game = pl.getConfig().getString("shooter");
        } else if (pl.getConfig().getString("shooter").equalsIgnoreCase("tsm")) {
            stack = new ItemCreator(Material.STONE_HOE, ChatColor.GOLD + "Blaster", Arrays.asList(ChatColor.GREEN +
                    "Click to shoot!"));
            game = pl.getConfig().getString("shooter");
        } else if (pl.getConfig().getString("shooter").equalsIgnoreCase("mm")) {
            stack = new ItemCreator(Material.GOLD_HOE, ChatColor.RED + "Boo Blaster", Arrays.asList(ChatColor.GREEN +
                    "Click to shoot!"));
            game = pl.getConfig().getString("shooter");
        } else {
            stack = null;
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }
        if (event.getInventory().contains(stack)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(event.getItemDrop().getItemStack().getType().equals(stack.getType()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        try {
            ingame.remove(event.getPlayer().getUniqueId());
        } catch (Exception ignored) {
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        event.setCancelled(event.getPlayer().getInventory().contains(stack));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL)) {
            return;
        }
        Player player = event.getPlayer();
        if (locations.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
            Block block = locations.get(player.getUniqueId());
            player.sendBlockChange(block.getLocation(), block.getType(), (byte) 0);
        }
        if (player.getItemInHand().getType().equals(stack.getType())) {
            event.setCancelled(true);
            player.throwSnowball();
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if ((projectile instanceof Snowball) && (projectile.getShooter() instanceof Player)) {
            Snowball snowball = (Snowball) projectile;
            final Player player = (Player) projectile.getShooter();
            if (!player.getInventory().contains(stack)) {
                return;
            }
            final Location loc = projectile.getLocation().add(projectile.getVelocity().normalize());
            final Block block = loc.getBlock();
            if (locations.containsValue(block)) {
                return;
            }
            final int amount = getPoint(block.getType());
            if (amount > 0) {
                final long time = System.currentTimeMillis();
                player.playSound(snowball.getLocation(), Sound.NOTE_PLING, 10.0F, 1.0F);
                player.setMetadata("shooter", new FixedMetadataValue(pl, player.getMetadata("shooter").get(0).asInt() + amount));
                sendMessage(player, "+" + amount);
                player.sendBlockChange(loc, Material.REDSTONE_BLOCK, (byte) 0);
                locations.put(time, block);
                Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
                    public void run() {
                        locations.remove(time);
                        player.sendBlockChange(loc, getMaterial(amount), (byte) 0);
                    }
                }, 100L);
            }

        }
    }

    public static void sendGameMessage(Player player) {
        switch (game) {
            case "buzz":
                player.sendMessage(ChatColor.AQUA + "----------------------------------------------------");
                player.sendMessage(ChatColor.BLUE + "Welcome to Buzz Lightyear's Space Ranger Spin!");
                player.sendMessage(ChatColor.BLUE + "Click with your Ray Gun to fire at targets.");
                player.sendMessage(ChatColor.BLUE + "Points will be kept track in your XP bar.");
                player.sendMessage(ChatColor.BLUE + "Good luck, Space Ranger!");
                player.sendMessage(ChatColor.AQUA + "----------------------------------------------------");
                return;
            case "tsm":
                player.sendMessage(ChatColor.GOLD + "----------------------------------------------------");
                player.sendMessage(ChatColor.YELLOW + "Welcome to Toy Story Midway Mania!");
                player.sendMessage(ChatColor.YELLOW + "Click with your Blaster to fire at targets.");
                player.sendMessage(ChatColor.YELLOW + "Points will be kept track in your XP bar.");
                player.sendMessage(ChatColor.YELLOW + "Good luck, Partner!");
                player.sendMessage(ChatColor.GOLD + "----------------------------------------------------");
                return;
            case "mm":
                player.sendMessage(ChatColor.YELLOW + "----------------------------------------------------");
                player.sendMessage(ChatColor.RED + "Welcome to Monstropolis Mayhem");
                player.sendMessage(ChatColor.RED + "Click with your Blaster to fire at targets.");
                player.sendMessage(ChatColor.RED + "Points will be kept track in your XP bar.");
                player.sendMessage(ChatColor.RED + "Good luck!");
                player.sendMessage(ChatColor.YELLOW + "----------------------------------------------------");
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

    public static void done(Player player) {
        ingame.remove(player.getUniqueId());
        player.getInventory().remove(stack.getType());
        switch (game) {
            case "buzz":
                String rank = getRank(player.getMetadata("shooter").get(0).asInt());
                player.sendMessage(ChatColor.AQUA + "----------------------------------------------------");
                player.sendMessage(ChatColor.BLUE + "Good job, Space Ranger! Your final score is "
                        + player.getMetadata("shooter").get(0).asInt() + "!");
                player.sendMessage(ChatColor.BLUE + rank);
                player.sendMessage(ChatColor.AQUA + "----------------------------------------------------");
                break;
            case "tsm":
                player.sendMessage(ChatColor.GOLD + "----------------------------------------------------");
                player.sendMessage(ChatColor.YELLOW + "Good job, Partner! Your final score is "
                        + player.getMetadata("shooter").get(0).asInt() + "!");
                player.sendMessage(ChatColor.GOLD + "----------------------------------------------------");
                break;
            case "mm":
                player.sendMessage(ChatColor.YELLOW + "----------------------------------------------------");
                player.sendMessage(ChatColor.RED + "Good job! Your final score is "
                        + player.getMetadata("shooter").get(0).asInt() + "!");
                player.sendMessage(ChatColor.YELLOW + "----------------------------------------------------");
        }
        if (itemMap.containsKey(player.getUniqueId())) {
            player.getInventory().setItem(4, itemMap.get(player.getUniqueId()));
        }
    }

    public static ItemStack getItem() {
        return stack;
    }

    /*
    else if ((player.getLevel() >= 21) && (player.getLevel() <= 40)) {
        player.sendMessage(ChatColor.GREEN + "✹ Level 3 Planetary Pilot: 21 – 40 ✹");
    }
    else if ((player.getLevel() >= 41) && (player.getLevel() <= 60)) {
        player.sendMessage(ChatColor.GREEN + "✹ Level 4 Space Scout:  41 – 60 ✹");
    }
    else if ((player.getLevel() >= 61) && (player.getLevel() <= 80)) {
        player.sendMessage(ChatColor.GREEN + "✹ Level 5 Ranger 1st Class: 61 – 80 ✹");
    }
    else if ((player.getLevel() >= 81) && (player.getLevel() <= 100)) {
        player.sendMessage(ChatColor.GREEN + "✹ Level 6 Cosmic Commando: 81 – 100 ✹");
    }
    else if ((player.getLevel() >= 100) && (player.getLevel() <= 1970)) {
        player.sendMessage(ChatColor.GREEN + "✹ Level 7 Galactic Hero: 100+ ✹");
    }
    else if (player.getLevel() == 1971) {
        player.sendMessage(ChatColor.BLUE + "✹ On Friday October 1, 1971 - after seven years of planning - about 10,000 visitors converged near Orlando, Florida, to witness the grand opening of Walt Disney World. ✹");
    }
    */

    public static String getRank(int score) {
        if (score < 11) {
            return "✹ Level 1 Star Cadet: 0 - 10 ✹";
        }
        if (score < 21) {
            return "✹ Level 2 Space Ace: 11 - 20 ✹";
        }
        if (score < 41) {
            return "✹ Level 3 Planetary Pilot: 21 - 40 ✹";
        }
        if (score < 61) {
            return "✹ Level 4 Space Scout: 41 - 60 ✹";
        }
        if (score < 81) {
            return "✹ Level 5 Ranger 1st Class: 61 - 80 ✹";
        }
        if (score < 101) {
            return "✹ Level 6 Cosmic Commando: 81 - 100 ✹";
        }
        if (score > 100 && score != 1971) {
            return "✹ Level 7 Galactic Hero: 100+ ✹";
        }
        if (score == 1971) {
            return "On Friday October 1, 1971 - after seven years of planning - about 10,000 visitors converged near " +
                    "Orlando, Florida, to witness the grand opening of Walt Disney World.";
        }
        return "";
    }

    public static void addToHashMap(UUID uuid, ItemStack stack) {
        if (itemMap.containsKey(uuid)) {
            itemMap.remove(uuid);
        }
        itemMap.put(uuid, stack);
    }

    public static ItemStack removeFromHashMap(UUID uuid) {
        try {
            return itemMap.remove(uuid);
        } catch (Exception ignored) {
            return null;
        }
    }

    public void sendMessage(Player player, String msg) {
        switch (game) {
            case "buzz":
                player.sendMessage(ChatColor.WHITE + "[" + ChatColor.BLUE + "" + ChatColor.BOLD + "Buzz"
                        + ChatColor.WHITE + "] " + ChatColor.AQUA + msg);
                return;
            case "tsm":
                player.sendMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "" + ChatColor.BOLD + "Toy Story Mania"
                        + ChatColor.WHITE + "] " + ChatColor.YELLOW + msg);
                return;
            case "mm":
                player.sendMessage(ChatColor.WHITE + "[" + ChatColor.YELLOW + "" + ChatColor.BOLD + "Monstropolis Mayhem"
                        + ChatColor.WHITE + "] " + ChatColor.RED + msg);

        }
    }
}