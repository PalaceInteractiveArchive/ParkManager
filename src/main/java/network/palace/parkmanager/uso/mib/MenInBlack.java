package us.mcmagic.parkmanager.uso.mib;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.SpawnEgg;
import us.mcmagic.mcmagiccore.actionbar.ActionBarManager;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.tsm.handlers.Hit;
import us.mcmagic.parkmanager.tsm.handlers.ShooterSession;

import java.util.*;

/**
 * Created by Marc on 4/5/17.
 */
public class MenInBlack implements Listener {
    private HashMap<UUID, ShooterSession> sessions = new HashMap<>();
    public static ItemStack item;
    private ItemStack blank = new SpawnEgg(EntityType.ENDERMAN).toItemStack();
    private ItemStack red = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
    private ItemStack blue = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 11);
    private ItemStack green = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
    private ItemStack purple = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 10);
    private BlockFace[] blockFaces = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    private HashMap<HitReset, Long> resetMap = new HashMap<>();

    public MenInBlack() {
        item = new ItemCreator(Material.STONE_HOE, ChatColor.DARK_PURPLE + "Blaster",
                Arrays.asList(ChatColor.YELLOW + "Right-Click to Shoot!"));
        Bukkit.getScheduler().runTaskTimer(ParkManager.getInstance(), new Runnable() {
            @Override
            public void run() {
                long cur = System.currentTimeMillis();
                for (Map.Entry<HitReset, Long> entry : new HashMap<>(resetMap).entrySet()) {
                    if ((entry.getValue() + 4000) > cur) {
                        continue;
                    }
                    HitReset reset = entry.getKey();
                    ItemFrame frame = reset.getItemFrame();
                    frame.setItem(new ItemStack(Material.INK_SACK, 1, reset.getData()));
                    resetMap.remove(reset);
                }
                for (Map.Entry<UUID, ShooterSession> entry : new HashMap<>(sessions).entrySet()) {
                    Player player = Bukkit.getPlayer(entry.getKey());
                    ShooterSession session = entry.getValue();
                    List<Hit> hits = session.getHits();
                    int total = 0;
                    for (Hit h : hits) {
                        total += h.getPoints();
                    }
                    String m = "";
                    if (!hits.isEmpty()) {
                        Hit h = hits.get(hits.size() - 1);
                        long t = h.getTime();
                        if (t + 2000 > cur) {
                            m = ChatColor.GREEN + " +" + h.getPoints() + " Points";
                        }
                    }
                    ActionBarManager.sendMessage(player, ChatColor.LIGHT_PURPLE + "MIB Score: " + total + m);
                }
            }
        }, 0L, 10L);
    }

    public void join(Player player) {
        if (sessions.containsKey(player.getUniqueId())) {
            return;
        }
        sessions.put(player.getUniqueId(), new ShooterSession(player.getUniqueId()));
        player.sendMessage(ChatColor.DARK_PURPLE + "----------------------------------------------------");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Welcome to MEN IN BLACK Alient Attack!");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Right-Click with your Blaster to fire at targets.");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Good luck, Agent!");
        player.sendMessage(ChatColor.DARK_PURPLE + "----------------------------------------------------");
        PlayerInventory inv = player.getInventory();
        inv.setItem(4, item);
        inv.setHeldItemSlot(4);
    }

    public void done(Player player) {
        if (!sessions.containsKey(player.getUniqueId())) {
            return;
        }
        ShooterSession session = sessions.remove(player.getUniqueId());
        List<Hit> hits = session.getHits();
        int points = 0;
        for (Hit hit : hits) {
            points += hit.getPoints();
        }
        player.sendMessage(ChatColor.DARK_PURPLE + "----------------------------------------------------");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Nice shooting, Agent!");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "You hit " + ChatColor.GREEN + hits.size() + ChatColor.YELLOW + " targets");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Total Points: " + ChatColor.GREEN + points);
        player.sendMessage(ChatColor.DARK_PURPLE + "----------------------------------------------------");
        player.getInventory().setItem(4, new ItemCreator(Material.THIN_GLASS, 1, ChatColor.GRAY +
                "This Slot is Reserved for " + ChatColor.BLUE + "Ride Items", Arrays.asList(ChatColor.GRAY +
                "This is for games such as " + ChatColor.GREEN + "Buzz", ChatColor.GREEN +
                "Lightyear's Space Ranger Spin ", ChatColor.GRAY + "and " + ChatColor.YELLOW +
                "Toy Story Midway Mania.")));
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (sessions.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }
        if (sessions.containsKey(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getType().equals(item.getType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        try {
            sessions.remove(event.getPlayer().getUniqueId());
        } catch (Exception ignored) {
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        try {
            sessions.remove(event.getPlayer().getUniqueId());
        } catch (Exception ignored) {
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack inHand = player.getItemInHand();
        ItemMeta meta = inHand.getItemMeta();
        if (event.getAction().equals(Action.PHYSICAL) || meta == null || meta.getDisplayName() == null ||
                !sessions.containsKey(player.getUniqueId())) {
            return;
        }
        String displayName = meta.getDisplayName();
        if (inHand.getType().equals(item.getType()) && meta.getDisplayName().equals(item.getItemMeta().getDisplayName())) {
            player.launchProjectile(Snowball.class);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack inHand = player.getItemInHand();
        ItemMeta meta = inHand.getItemMeta();
        if (meta == null || meta.getDisplayName() == null || !sessions.containsKey(player.getUniqueId())) {
            return;
        }
        event.setCancelled(true);
        String displayName = meta.getDisplayName();
        if (inHand.getType().equals(item.getType()) && meta.getDisplayName().equals(item.getItemMeta().getDisplayName())) {
            player.launchProjectile(Snowball.class);
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType().equals(EntityType.PLAYER) && sessions.containsKey(event.getDamager().getUniqueId())) {
            event.setCancelled(true);
            return;
        }
        if (!event.getDamager().getType().equals(EntityType.SNOWBALL) ||
                !event.getEntity().getType().equals(EntityType.ITEM_FRAME)) {
            return;
        }
        Snowball sb = (Snowball) event.getDamager();
        ItemFrame item = (ItemFrame) event.getEntity();
        ItemStack stack = item.getItem();
        if (sb.getShooter() == null) {
            return;
        }
        if (!(sb.getShooter() instanceof Player)) {
            return;
        }
        if (!sessions.containsKey(((Player) sb.getShooter()).getUniqueId())) {
            return;
        }
        event.setCancelled(true);
        if (!stack.getType().equals(Material.INK_SACK)) {
            return;
        }
        Player player = (Player) sb.getShooter();
        ShooterSession session = sessions.get(player.getUniqueId());
        byte data = stack.getData().getData();
        Location loc = item.getLocation();
        int total = 0;
        for (Hit h : session.getHits()) {
            total += h.getPoints();
        }
        Hit hit = null;
        switch (data) {
            case 6: {
                hit = new Hit(player.getUniqueId(), 25);
                ActionBarManager.sendMessage(player, ChatColor.LIGHT_PURPLE + "MIB Score: " + (total + 25) + ChatColor.GREEN + " +25 Points");
                item.setItem(blank);
                break;
            }
            case 11: {
                hit = new Hit(player.getUniqueId(), 50);
                ActionBarManager.sendMessage(player, ChatColor.LIGHT_PURPLE + "MIB Score: " + (total + 50) + ChatColor.GREEN + " +50 Points");
                item.setItem(blank);
                break;
            }
            case 10: {
                hit = new Hit(player.getUniqueId(), 75);
                ActionBarManager.sendMessage(player, ChatColor.LIGHT_PURPLE + "MIB Score: " + (total + 75) + ChatColor.GREEN + " +75 Points");
                item.setItem(blank);
                break;
            }
            case 5: {
                hit = new Hit(player.getUniqueId(), 100);
                ActionBarManager.sendMessage(player, ChatColor.LIGHT_PURPLE + "MIB Score: " + (total + 100) + ChatColor.GREEN + " +100 Points");
                item.setItem(blank);
                break;
            }
        }
        if (hit == null) {
            return;
        }
        resetMap.put(new HitReset(item, data), System.currentTimeMillis());
        session.addHit(hit);
    }

    public boolean isInGame(Player player) {
        return sessions.containsKey(player.getUniqueId());
    }
}