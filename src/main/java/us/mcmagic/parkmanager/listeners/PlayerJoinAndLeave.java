package us.mcmagic.parkmanager.listeners;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.commands.Commandvanish;
import us.mcmagic.parkmanager.designstation.DesignStation;
import us.mcmagic.parkmanager.handlers.HotelRoom;
import us.mcmagic.parkmanager.handlers.PlayerData;
import us.mcmagic.parkmanager.handlers.Warp;
import us.mcmagic.parkmanager.hotels.HotelManager;
import us.mcmagic.parkmanager.utils.WarpUtil;
import us.mcmagic.parkmanager.watch.WatchTask;

import java.util.*;

public class PlayerJoinAndLeave implements Listener {
    private HashMap<UUID, Long> needInvSet = new HashMap<>();

    public PlayerJoinAndLeave() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(ParkManager.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, Long> entry : new HashMap<>(needInvSet).entrySet()) {
                    UUID uuid = entry.getKey();
                    Long time = entry.getValue();
                    if (time + 5000 <= System.currentTimeMillis()) {
                        needInvSet.remove(uuid);
                        Player tp = Bukkit.getPlayer(uuid);
                        if (tp == null) {
                            continue;
                        }
                        tp.sendMessage(ChatColor.GREEN + "Inventory took too long to download, forcing download.");
                        ParkManager.storageManager.downloadInventory(uuid);
                    }
                }
            }
        }, 0L, 20L);
    }

    @EventHandler
    public void onAsyncLogin(AsyncPlayerPreLoginEvent event) {
        try {
            UUID uuid = event.getUniqueId();
            ParkManager.bandUtil.setupPlayerData(uuid);
            ParkManager.autographManager.setBook(uuid);
            if (ParkManager.getPlayerData(uuid) == null) {
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage("There was an error joining this server! (Error Code 106)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (MCMagicCore.serverStarting) {
            return;
        }
        User user = MCMagicCore.getUser(player.getUniqueId());
        if (user == null) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            return;
        }
        if (MCMagicCore.getMCMagicConfig().serverName.equals("NewHWS")) {
            if (user.getRank().getRankId() < Rank.DVCMEMBER.getRankId()) {
                event.setKickMessage(ChatColor.RED + "You must be the " + Rank.DVCMEMBER.getNameWithBrackets()
                        + ChatColor.RED + " rank or above to preview NewHWS!");
                event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                return;
            }
        }
        if (event.getResult().equals(PlayerLoginEvent.Result.ALLOWED)) {
            player.getInventory().clear();
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            final Player player = event.getPlayer();
            final User user = MCMagicCore.getUser(player.getUniqueId());
            if (!needInvSet.containsKey(player.getUniqueId())) {
                needInvSet.put(player.getUniqueId(), System.currentTimeMillis());
            }
            ParkManager.userCache.remove(player.getUniqueId());
            ParkManager.userCache.put(player.getUniqueId(), player.getName());
            PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
            if (!data.getVisibility()) {
                ParkManager.vanishUtil.addToHideAll(player);
            }
            for (PotionEffect type : player.getActivePotionEffects()) {
                player.removePotionEffect(type.getType());
            }
            if (user.getRank().getRankId() >= Rank.CASTMEMBER.getRankId()) {
                player.setGameMode(GameMode.SURVIVAL);
                player.setAllowFlight(true);
            } else {
                player.setGameMode(GameMode.ADVENTURE);
                player.setAllowFlight(user.getRank().getRankId() >= Rank.INTERN.getRankId());
            }
            if (MCMagicCore.getMCMagicConfig().serverName.equalsIgnoreCase("newhws") &&
                    user.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
                player.teleport(WarpUtil.findWarp("sharehws").getLocation());
            } else {
                if (ParkManager.spawnOnJoin || !player.hasPlayedBefore()) {
                    player.performCommand("spawn");
                } else {
                    warpToNearestWarp(player);
                }
            }
            for (String msg : ParkManager.joinMessages) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
            ParkManager.vanishUtil.login(player);
            if (user.getRank().getRankId() >= Rank.CASTMEMBER.getRankId() &&
                    !MCMagicCore.getMCMagicConfig().serverName.equals("NewHWS")) {
                Commandvanish.vanish(player.getUniqueId());
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    if (MCMagicCore.getUser(tp.getUniqueId()).getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                        tp.hidePlayer(player);
                    }
                }
            }
            if (ParkManager.ttcServer) {
                if (user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                    if (player.getLocation().distance(ParkManager.spawn) <= 5) {
                        for (Player tp : Bukkit.getOnlinePlayers()) {
                            if (tp.getUniqueId().equals(player.getUniqueId())) {
                                continue;
                            }
                            tp.hidePlayer(player);
                        }
                    }
                }
            }
            final PlayerInventory inv = player.getInventory();
            PlayerData.Clothing c = data.getClothing();
            inv.clear();
            inv.setHelmet(c.getHead());
            inv.setChestplate(c.getShirt());
            inv.setLeggings(c.getPants());
            inv.setBoots(c.getBoots());
            setInventory(player, false);
            Bukkit.getScheduler().runTaskLaterAsynchronously(ParkManager.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if (!ParkManager.hotelServer) {
                        return;
                    }
                    HotelManager manager = ParkManager.hotelManager;
                    for (HotelRoom room : manager.getHotelRooms()) {
                        if (room.getCheckoutNotificationRecipient() != null &&
                                room.getCheckoutNotificationRecipient().equals(player.getUniqueId())) {
                            room.setCheckoutNotificationRecipient(null);
                            room.setCheckoutTime(0);
                            manager.updateHotelRoom(room);
                            manager.updateRooms();
                            player.sendMessage(ChatColor.GREEN + "Your reservation of the " + room.getName() +
                                    " room has lapsed and you have been checked out. Please come stay with us again soon!");
                            manager.expire.send(player);
                            player.playSound(player.getLocation(), Sound.BLAZE_DEATH, 10f, 1f);
                            return;
                        }
                        if (room.getCurrentOccupant() != null &&
                                room.getCurrentOccupant().equals(player.getUniqueId()) &&
                                room.getCheckoutTime() <= (System.currentTimeMillis() / 1000)) {
                            manager.checkout(room, true);
                        }
                    }
                }
            }, 20L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setInventory(Player player, boolean book) {
        Inventory inv = player.getInventory();
        ItemStack[] barrier = new ItemStack[36];
        for (int i = 9; i < barrier.length; i++) {
            barrier[i] = new ItemCreator(Material.INK_SACK, 1, (byte) 7, ChatColor.RED + "You can't use this area!",
                    Arrays.asList(ChatColor.GREEN + "Use your Backpack for Storage."));
        }
        inv.setContents(barrier);
        inv.setItem(5, new ItemCreator(Material.CHEST, ChatColor.GREEN + "Backpack " + ChatColor.GRAY + "(Right-Click)"));
        inv.setItem(6, new ItemCreator(Material.WATCH, ChatColor.GREEN + "Watch " + ChatColor.GRAY + "(Right-Click)",
                Arrays.asList(ChatColor.GRAY + "Right-Click to open the", ChatColor.GRAY + "Show Schedule Menu")));
        if (book) {
            ParkManager.autographManager.giveBook(player);
        }
        ParkManager.bandUtil.giveBandToPlayer(player);
        inv.setItem(4, new ItemCreator(Material.INK_SACK, 1, (byte) 7, ChatColor.GRAY +
                "This Slot is Reserved for " + ChatColor.BLUE + "Ride Items", Arrays.asList(ChatColor.GRAY +
                "This is for games such as " + ChatColor.GREEN + "Buzz", ChatColor.GREEN +
                "Lightyear's Space Ranger Spin ", ChatColor.GRAY + "and " + ChatColor.YELLOW +
                "Toy Story Midway Mania.")));
    }

    private void warpToNearestWarp(Player player) {
        Location loc = getLoc(player);
        loc.setWorld(Bukkit.getWorlds().get(0));
        Warp w = null;
        double distance = -1;
        for (Warp warp : new ArrayList<>(ParkManager.warps)) {
            if (!warp.getServer().equals(MCMagicCore.getMCMagicConfig().serverName)) {
                continue;
            }
            if (warp.getLocation() == null) {
                continue;
            }
            if (warp.getName().startsWith("dvc") || warp.getName().startsWith("share") ||
                    warp.getName().startsWith("char") || warp.getName().startsWith("staff")) {
                continue;
            }
            if (distance == -1) {
                w = warp;
                distance = warp.getLocation().distance(loc);
                continue;
            }
            double d = warp.getLocation().distance(loc);
            if (d < distance) {
                w = warp;
                distance = d;
            }
        }
        if (w == null) {
            player.performCommand("spawn");
            return;
        }
        player.teleport(w.getLocation());
    }

    private Location getLoc(Player player) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        if (ep == null) {
            return new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
        }
        return new Location(Bukkit.getWorlds().get(0), ep.locX, ep.locY, ep.locZ);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ParkManager.storageManager.logout(player);
        if (player.getVehicle() != null) {
            player.getVehicle().eject();
        }
        if (ParkManager.hotelServer) {
            HotelManager manager = ParkManager.hotelManager;
            for (HotelRoom room : manager.getHotelRooms()) {
                if (room.isOccupied() && room.getCurrentOccupant().equals(player.getUniqueId())) {
                    manager.closeDoor(room);
                    break;
                }
            }
        }
        BlockEdit.logout(player.getUniqueId());
        ParkManager.queueManager.silentLeaveAllQueues(player);
        ParkManager.autographManager.logout(player);
        ParkManager.parkSoundManager.logout(player);
        ParkManager.bandUtil.cancelLoadPlayerData(player.getUniqueId());
        ParkManager.stitch.logout(player);
        ParkManager.vanishUtil.logout(player);
        WatchTask.removeFromMessage(player.getUniqueId());
        Commandvanish.unvanish(player.getUniqueId());
        ParkManager.blockChanger.logout(player);
        DesignStation.removePlayerVehicle(player.getUniqueId());
        ParkManager.playerData.remove(player.getUniqueId());
    }

    public void setInventory(UUID uuid) {
        needInvSet.remove(uuid);
    }
}