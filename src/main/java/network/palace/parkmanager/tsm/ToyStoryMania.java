package network.palace.parkmanager.tsm;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.tsm.handlers.Hit;
import network.palace.parkmanager.tsm.handlers.ShooterSession;
import network.palace.parkmanager.utils.InventoryUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Marc on 6/28/16
 */
public class ToyStoryMania implements Listener {
    private HashMap<UUID, ShooterSession> sessions = new HashMap<>();
    public static ItemStack item;
    private ItemStack black = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
    private ItemStack red = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
    private ItemStack blue = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 11);
    private ItemStack green = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
    private ItemStack purple = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 10);
    private BlockFace[] blockFaces = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    public ToyStoryMania() {
        item = ItemUtil.create(Material.STONE_HOE, ChatColor.YELLOW + "Toy Cannon",
                Collections.singletonList(ChatColor.YELLOW + "Right-Click to Shoot!"));
        Bukkit.getScheduler().runTaskTimer(ParkManager.getInstance(), () -> {
            long cur = System.currentTimeMillis();
            for (Map.Entry<UUID, ShooterSession> entry : new HashMap<>(sessions).entrySet()) {
                CPlayer player = Core.getPlayerManager().getPlayer(entry.getKey());
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
                        ChatColor color = ChatColor.GREEN;
                        switch (h.getPoints()) {
                            case 25:
                                color = ChatColor.RED;
                                break;
                            case 50:
                                color = ChatColor.BLUE;
                                break;
                            case 75:
                                color = ChatColor.GREEN;
                                break;
                            case 100:
                                color = ChatColor.LIGHT_PURPLE;
                                break;
                        }
                        m = color + " +" + h.getPoints() + " Points";
                    }
                }
                player.getActionBar().show(ChatColor.YELLOW + "TSM Score: " + total + m);
            }
        }, 0L, 10L);
    }

    public void join(Player player) {
        if (sessions.containsKey(player.getUniqueId())) {
            return;
        }
        sessions.put(player.getUniqueId(), new ShooterSession(player.getUniqueId()));
        player.sendMessage(ChatColor.GOLD + "----------------------------------------------------");
        player.sendMessage(ChatColor.YELLOW + "Welcome to Toy Story Midway Mania!");
        player.sendMessage(ChatColor.YELLOW + "Right-Click with your Cannon to fire at targets.");
        player.sendMessage(ChatColor.YELLOW + "Good luck, Partner!");
        player.sendMessage(ChatColor.GOLD + "----------------------------------------------------");
        PlayerInventory inv = player.getInventory();
        inv.setItem(4, item);
        inv.setHeldItemSlot(4);
    }

    public void done(Player player) {
        done(Core.getPlayerManager().getPlayer(player));
    }

    public void done(CPlayer player) {
        if (player == null || !sessions.containsKey(player.getUniqueId())) {
            return;
        }
        ShooterSession session = sessions.remove(player.getUniqueId());
        List<Hit> hits = session.getHits();
        int points = 0;
        for (Hit hit : hits) {
            points += hit.getPoints();
        }
        player.sendMessage(ChatColor.GOLD + "----------------------------------------------------");
        player.sendMessage(ChatColor.YELLOW + "Nice shooting, Partner!");
        player.sendMessage(ChatColor.YELLOW + "You hit " + ChatColor.GREEN + hits.size() + ChatColor.YELLOW + " targets");
        player.sendMessage(ChatColor.YELLOW + "Total Points: " + ChatColor.GREEN + points);
        player.sendMessage(ChatColor.GOLD + "----------------------------------------------------");
        player.getInventory().setItem(4, InventoryUtil.getRideItem());
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
        event.setCancelled(event.getItemDrop().getItemStack().getType().equals(item.getType()));
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
        ItemStack inHand = player.getInventory().getItemInMainHand();
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

    @SuppressWarnings("deprecation")
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
        if (!stack.getType().equals(Material.STAINED_GLASS_PANE)) {
            return;
        }
        CPlayer player = Core.getPlayerManager().getPlayer(((Player) sb.getShooter()).getUniqueId());
        ShooterSession session = sessions.get(player.getUniqueId());
        byte data = stack.getData().getData();
        Location loc = item.getLocation();
        int total = 0;
        for (Hit h : session.getHits()) {
            total += h.getPoints();
        }
        Hit hit = null;
        switch (data) {
            case 14: {
                hit = new Hit(player.getUniqueId(), 25);
                player.getActionBar().show(ChatColor.RED + "+25 Points");
                item.setItem(black);
                break;
            }
            case 11: {
                hit = new Hit(player.getUniqueId(), 50);
                player.getActionBar().show(ChatColor.BLUE + "+50 Points");
                item.setItem(black);
                break;
            }
            case 5: {
                hit = new Hit(player.getUniqueId(), 75);
                player.getActionBar().show(ChatColor.GREEN + "+75 Points");
                item.setItem(black);
                break;
            }
            case 10: {
                hit = new Hit(player.getUniqueId(), 100);
                player.getActionBar().show(ChatColor.LIGHT_PURPLE + "+100 Points");
                item.setItem(black);
                break;
            }
        }
        if (hit == null) {
            return;
        }
        session.addHit(hit);
    }

    public boolean isInGame(CPlayer player) {
        return sessions.containsKey(player.getUniqueId());
    }

    public void randomize(Location min, Location max) {
        World world = min.getWorld();
        boolean hasPurple = false;
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    Location loc = new Location(world, x, y, z);
                    ItemFrame frame = getFrame(loc);
                    if (frame == null) continue;
                    byte data = random(hasPurple);
                    if (data == 10) {
                        hasPurple = true;
                    }
                    switch (data) {
                        case 15:
                            frame.setItem(black);
                            break;
                        case 14:
                            frame.setItem(red);
                            break;
                        case 11:
                            frame.setItem(blue);
                            break;
                        case 5:
                            frame.setItem(green);
                            break;
                        case 10:
                            frame.setItem(purple);
                            break;
                    }
                }
            }
        }
    }

    private ItemFrame getFrame(Location loc) {
        Stream<ItemFrame> stream = loc.getWorld().getEntitiesByClass(ItemFrame.class).stream().filter(item -> {
            Location frameLoc = item.getLocation();
            return loc.getBlockX() == frameLoc.getBlockX() &&
                    loc.getBlockY() == frameLoc.getBlockY() &&
                    loc.getBlockZ() == frameLoc.getBlockZ();
        });
        if (stream.findFirst().isPresent()) return stream.findFirst().get();
        return null;
    }

    public void reset(Location min, Location max) {
        World world = min.getWorld();
        boolean hasPurple = false;
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    Location loc = new Location(world, x, y, z);
                    Collection<ItemFrame> allItemFrames = world.getEntitiesByClass(ItemFrame.class);
                    ItemFrame frame = getFrame(loc);
                    if (frame == null) continue;
                    frame.setItem(black);
                }
            }
        }
    }

    private byte random(boolean hasPurple) {
        byte[] array;
        if (hasPurple) {
            array = new byte[]{14, 11, 5, 15, 15};
        } else {
            array = new byte[]{14, 11, 5, 10, 15, 15};
        }
        return array[new Random().nextInt(array.length)];
    }

    @SuppressWarnings("deprecation")
    public void setMap(Location min, Location max, Chest chest) {
        Inventory inv = chest.getBlockInventory();
        Block b = min.getBlock();
        List<ItemStack> maps = new ArrayList<>();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType() == null) {
                continue;
            }
            if (!item.getType().equals(Material.MAP)) {
                continue;
            }
            maps.add(item);
        }
        BlockFace blockFace = null;
        for (BlockFace face : blockFaces) {
            if (b.getRelative(face).getType().equals(Material.WOOL) && b.getRelative(face).getData() == 15) {
                blockFace = face;
                break;
            }
        }
        if (blockFace == null) {
            return;
        }
        int i = 0;
        World world = min.getWorld();
        Collection<ItemFrame> allItemFrames = world.getEntitiesByClass(ItemFrame.class);
        switch (blockFace) {
            case NORTH: {
                int z = min.getBlockZ();
                for (int y = max.getBlockY(); y >= min.getBlockY(); y--) {
                    for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
                        Location loc = new Location(world, x, y, z);
                        ItemFrame frame = getFrame(loc);
                        if (frame == null) continue;
                        frame.setItem(maps.get(i));
                        i++;
                    }
                }
                break;
            }
            case EAST: {
                int x = min.getBlockX();
                for (int y = max.getBlockY(); y >= min.getBlockY(); y--) {
                    for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                        Location loc = new Location(world, x, y, z);
                        ItemFrame frame = getFrame(loc);
                        if (frame == null) continue;
                        frame.setItem(maps.get(i));
                        i++;
                    }
                }
                break;
            }
            case SOUTH: {
                int z = min.getBlockZ();
                for (int y = max.getBlockY(); y >= min.getBlockY(); y--) {
                    for (int x = max.getBlockX(); x >= min.getBlockX(); x--) {
                        Location loc = new Location(world, x, y, z);
                        ItemFrame frame = getFrame(loc);
                        if (frame == null) continue;
                        frame.setItem(maps.get(i));
                        i++;
                    }
                }
                break;
            }
            case WEST: {
                int x = min.getBlockX();
                for (int y = max.getBlockY(); y >= min.getBlockY(); y--) {
                    for (int z = max.getBlockZ(); z >= min.getBlockZ(); z--) {
                        Location loc = new Location(world, x, y, z);
                        ItemFrame frame = getFrame(loc);
                        if (frame == null) continue;
                        frame.setItem(maps.get(i));
                        i++;
                    }
                }
                break;
            }
        }
    }
}