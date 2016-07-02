package us.mcmagic.parkmanager.shooter;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.utils.FileUtil;

import java.util.*;

/**
 * Created by Jacob on 1/18/15.
 */
public class Shooter implements Listener {
    private ItemStack stack;
    private HashMap<UUID, ItemStack> itemMap = new HashMap<>();
    private HashMap<Long, Block> locations = new HashMap<>();
    private List<UUID> ingame = new ArrayList<>();
    public String game;


    public Shooter(ParkManager instance) {
        YamlConfiguration config = FileUtil.configurationYaml();
        if (config.getString("shooter").equalsIgnoreCase("buzz")) {
            stack = new ItemCreator(Material.WOOD_HOE, ChatColor.BLUE + "Ray Gun",
                    Collections.singletonList(ChatColor.GREEN + "Click to shoot!"));
            game = config.getString("shooter");
        } else if (config.getString("shooter").equalsIgnoreCase("tsm")) {
            stack = new ItemCreator(Material.STONE_HOE, ChatColor.GOLD + "Blaster",
                    Collections.singletonList(ChatColor.GREEN + "Click to shoot!"));
            game = config.getString("shooter");
        } else if (config.getString("shooter").equalsIgnoreCase("mm")) {
            stack = new ItemCreator(Material.GOLD_HOE, ChatColor.RED + "Boo Blaster",
                    Collections.singletonList(ChatColor.GREEN + "Click to shoot!"));
            game = config.getString("shooter");
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
        Player player = event.getPlayer();
        if (ingame.contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack inHand = player.getItemInHand();
        ItemMeta meta = inHand.getItemMeta();
        if (inHand == null || event.getAction().equals(Action.PHYSICAL) || meta == null ||
                meta.getDisplayName() == null || !ingame.contains(player.getUniqueId())) {
            return;
        }
        String displayName = meta.getDisplayName();
        if (inHand.getType().equals(stack.getType()) && meta.getDisplayName().equals(stack.getItemMeta().getDisplayName())) {
            player.launchProjectile(Snowball.class);
            event.setCancelled(true);
        }
        if (locations.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
            Block block = locations.get(player.getUniqueId());
            player.sendBlockChange(block.getLocation(), block.getType(), (byte) 0);
        }
    }

    @SuppressWarnings("deprecation")
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
                player.setMetadata("shooter", new FixedMetadataValue(ParkManager.getInstance(),
                        player.getMetadata("shooter").get(0).asInt() + amount));
                sendMessage(player, "+" + amount);
                player.sendBlockChange(loc, Material.REDSTONE_BLOCK, (byte) 0);
                locations.put(time, block);
                Bukkit.getScheduler().runTaskLater(ParkManager.getInstance(), () -> {
                    locations.remove(time);
                    player.sendBlockChange(loc, getMaterial(amount), (byte) 0);
                }, 100L);
            }

        }
    }

    public void sendGameMessage(Player player) {
        switch (game) {
            case "buzz":
                player.sendMessage(ChatColor.AQUA + "----------------------------------------------------");
                player.sendMessage(ChatColor.BLUE + "Welcome to Buzz Lightyear's Space Ranger Spin!");
                player.sendMessage(ChatColor.BLUE + "Click with your Ray Gun to fire at targets.");
                player.sendMessage(ChatColor.BLUE + "Good luck, Space Ranger!");
                player.sendMessage(ChatColor.AQUA + "----------------------------------------------------");
                return;
            case "tsm":
                player.sendMessage(ChatColor.GOLD + "----------------------------------------------------");
                player.sendMessage(ChatColor.YELLOW + "Welcome to Toy Story Midway Mania!");
                player.sendMessage(ChatColor.YELLOW + "Click with your Blaster to fire at targets.");
                player.sendMessage(ChatColor.YELLOW + "Good luck, Partner!");
                player.sendMessage(ChatColor.GOLD + "----------------------------------------------------");
                return;
            case "mm":
                player.sendMessage(ChatColor.YELLOW + "----------------------------------------------------");
                player.sendMessage(ChatColor.RED + "Welcome to Monstropolis Mayhem");
                player.sendMessage(ChatColor.RED + "Click with your Blaster to fire at targets.");
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

    public void done(Player player) {
        ingame.remove(player.getUniqueId());
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
        player.getInventory().setItem(4, new ItemCreator(Material.INK_SACK, 1, (byte) 7, ChatColor.GRAY +
                "This Slot is Reserved for " + ChatColor.BLUE + "Ride Items", Arrays.asList(ChatColor.GRAY +
                "This is for games such as " + ChatColor.GREEN + "Buzz", ChatColor.GREEN +
                "Lightyear's Space Ranger Spin ", ChatColor.GRAY + "and " + ChatColor.YELLOW +
                "Toy Story Midway Mania.")));
    }

    public ItemStack getItem() {
        return stack;
    }

    public String getRank(int score) {
        if (score == 1971) {
            return "On Friday October 1, 1971 - after seven years of planning - about 10,000 visitors converged near " +
                    "Orlando, Florida, to witness the grand opening of Walt Disney World.";
        }
        if (score < 101) {
            return "✹ Level 1 Star Cadet: 0 - 100 ✹";
        }
        if (score < 201) {
            return "✹ Level 2 Space Ace: 101 - 200 ✹";
        }
        if (score < 401) {
            return "✹ Level 3 Planetary Pilot: 201 - 400 ✹";
        }
        if (score < 601) {
            return "✹ Level 4 Space Scout: 401 - 600 ✹";
        }
        if (score < 801) {
            return "✹ Level 5 Ranger 1st Class: 601 - 800 ✹";
        }
        if (score < 1000) {
            return "✹ Level 6 Cosmic Commando: 801 - 1000 ✹";
        }
        if (score > 1000) {
            return "✹ Level 7 Galactic Hero: 1000+ ✹";
        }
        return "";
    }

    public void addToHashMap(UUID uuid, ItemStack stack) {
        if (itemMap.containsKey(uuid)) {
            itemMap.remove(uuid);
        }
        itemMap.put(uuid, stack);
    }

    public ItemStack removeFromHashMap(UUID uuid) {
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

    public void join(Player tp) {
        if (!ingame.contains(tp.getUniqueId())) {
            ingame.add(tp.getUniqueId());
        }
    }

    public void warp(Player tp) {
        if (!ingame.contains(tp.getUniqueId())) {
            return;
        }
        done(tp);
    }

    public List<UUID> getIngame() {
        return new ArrayList<>(ingame);
    }
}