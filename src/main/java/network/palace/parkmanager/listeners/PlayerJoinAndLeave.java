package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.designstation.DesignStation;
import network.palace.parkmanager.handlers.HotelRoom;
import network.palace.parkmanager.handlers.PlayerData;
import network.palace.parkmanager.handlers.Resort;
import network.palace.parkmanager.handlers.Warp;
import network.palace.parkmanager.hotels.HotelManager;
import network.palace.parkmanager.watch.WatchTask;
import org.bukkit.*;
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
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class PlayerJoinAndLeave implements Listener {
    private HashMap<UUID, Long> needInvSet = new HashMap<>();

    public PlayerJoinAndLeave() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(ParkManager.getInstance(), () -> {
            HashMap<UUID, Long> localMap = getPartOfMap(needInvSet, 5);
            for (Map.Entry<UUID, Long> entry : new HashMap<>(localMap).entrySet()) {
                Long time = entry.getValue();
                if (time + 5000 <= System.currentTimeMillis()) {
                    UUID uuid = entry.getKey();
                    needInvSet.remove(entry.getKey());
                    localMap.remove(entry.getKey());
                    Player tp = Bukkit.getPlayer(uuid);
                    if (tp == null) {
                        continue;
                    }
                    tp.sendMessage(ChatColor.GREEN + "Inventory took too long to download, forcing download.");
                    ParkManager.storageManager.downloadInventory(uuid, true);
                }
            }
        }, 0L, 20L);
        /*Bukkit.getScheduler().runTaskTimerAsynchronously(ParkManager.getInstance(), () -> {
            for (Map.Entry<UUID, Long> entry : new HashMap<>(needInvSet).entrySet()) {
                needInvSet.remove(entry.getKey());
                UUID uuid = entry.getKey();
                Long time = entry.getValue();
                if (time + 5000 <= System.currentTimeMillis()) {
                    Player tp = Bukkit.getPlayer(uuid);
                    if (tp == null) {
                        continue;
                    }
                    tp.sendMessage(ChatColor.GREEN + "Inventory took too long to download, forcing download.");
                    ParkManager.storageManager.downloadInventory(uuid);
                }
            }
        }, 0L, 20L);*/
    }

    private HashMap<UUID, Long> getPartOfMap(HashMap<UUID, Long> map, int amount) {
        if (map.size() < amount) {
            return new HashMap<>(map);
        }
        HashMap<UUID, Long> temp = new HashMap<>();
        int i = 0;
        for (Map.Entry<UUID, Long> entry : map.entrySet()) {
            if (i >= amount) {
                break;
            }
            temp.put(entry.getKey(), entry.getValue());
            i++;
        }
        return temp;
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
        if (Core.isStarting()) {
            return;
        }
        CPlayer user = Core.getPlayerManager().getPlayer(player.getUniqueId());
        if (user == null) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            return;
        }
        if (Bukkit.hasWhitelist() && user.getRank().getRankId() < Rank.SQUIRE.getRankId()) {
            event.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
            return;
        }
        // If Disneyland and user rank is below Noble, don't allow them to connect
        if ((ParkManager.isResort(Resort.DLR)) &&
                user.getRank().getRankId() < Rank.NOBLE.getRankId()) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ChatColor.AQUA + "You must be the " + Rank.NOBLE.getNameWithBrackets() +
                    ChatColor.AQUA + " rank or above to preview Disneyland!");
            return;
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
            final CPlayer cp = Core.getPlayerManager().getPlayer(player.getUniqueId());
            cp.giveAchievement(0);
            switch (Core.getServerType()) {
                case "MK":
                    cp.giveAchievement(3);
                    break;
                case "Epcot":
                    cp.giveAchievement(4);
                    break;
                case "DHS":
                    cp.giveAchievement(5);
                    break;
                case "AK":
                    cp.giveAchievement(6);
                    break;
                case "Typhoon":
                    cp.giveAchievement(7);
                    break;
                case "DCL":
                    cp.giveAchievement(8);
                    break;
            }
            if (!needInvSet.containsKey(player.getUniqueId())) {
                needInvSet.put(player.getUniqueId(), System.currentTimeMillis());
            }
            ParkManager.userCache.put(player.getUniqueId(), player.getName());
            PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
            if (!cp.hasAchievement(12) && !data.getRideCounts().isEmpty()) {
                int size = data.getRideCounts().size();
                cp.giveAchievement(12);
                if (size >= 10) {
                    cp.giveAchievement(13);
                }
                if (size >= 20) {
                    cp.giveAchievement(14);
                }
                if (size >= 30) {
                    cp.giveAchievement(15);
                }
            }
            if (!data.getVisibility()) {
                ParkManager.visibilityUtil.addToHideAll(cp);
            }
            for (PotionEffect type : player.getActivePotionEffects()) {
                player.removePotionEffect(type.getType());
            }
            GameMode mode = player.getGameMode();
            if (cp.getRank().getRankId() >= Rank.KNIGHT.getRankId()) {
                player.setGameMode(GameMode.SURVIVAL);
                if (!player.getAllowFlight()) {
                    player.setAllowFlight(true);
                }
            } else {
                player.setGameMode(GameMode.ADVENTURE);
                boolean fly = cp.getRank().getRankId() >= Rank.SQUIRE.getRankId();
                if (player.getAllowFlight() != fly) {
                    player.setAllowFlight(fly);
                }
            }
            if (ParkManager.spawnOnJoin || !player.hasPlayedBefore()) {
                player.performCommand("spawn");
            } else {
                warpToNearestWarp(player);
            }
            for (String msg : ParkManager.joinMessages) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
            final PlayerInventory inv = player.getInventory();
            clearArmor(inv);
            PlayerData.Clothing c = data.getClothing();
            if (c.getHead() != null) {
                inv.setHelmet(c.getHead());
            }
            if (c.getShirt() != null) {
                inv.setChestplate(c.getShirt());
            }
            if (c.getPants() != null) {
                inv.setLeggings(c.getPants());
            }
            if (c.getBoots() != null) {
                inv.setBoots(c.getBoots());
            }
            setInventory(cp, false);
            if ((ParkManager.isResort(Resort.DLR)) &&
                    cp.getRank().getRankId() >= Rank.NOBLE.getRankId() && cp.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                player.setGameMode(GameMode.ADVENTURE);
                player.setAllowFlight(true);
                player.setFlying(true);
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200000, 0, true, false));
                return;
            }
            if (!ParkManager.hotelServer) {
                return;
            }
            Bukkit.getScheduler().runTaskLaterAsynchronously(ParkManager.getInstance(), () -> {
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
                        player.sendTitle(ChatColor.RED + "Your Hotel Room Expired", "", 40, 60, 40);
                        player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_DEATH, 10f, 1f);
                        return;
                    }
                    if (room.getCurrentOccupant() != null &&
                            room.getCurrentOccupant().equals(player.getUniqueId()) &&
                            room.getCheckoutTime() <= (System.currentTimeMillis() / 1000)) {
                        manager.checkout(room, true);
                    }
                }
            }, 20L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearArmor(PlayerInventory inv) {
        ItemStack air = new ItemStack(Material.AIR);
        inv.setHelmet(air);
        inv.setChestplate(air);
        inv.setLeggings(air);
        inv.setBoots(air);
    }

    public void setInventory(CPlayer player, boolean book) {
        Inventory inv = player.getInventory();
        ItemStack[] barrier = new ItemStack[36];
        for (int i = 9; i < barrier.length; i++) {
            barrier[i] = ItemUtil.create(Material.THIN_GLASS, 1, ChatColor.RED + "You can't use this area!",
                    Arrays.asList(ChatColor.GREEN + "Use your Backpack for Storage."));
        }
        inv.setContents(barrier);
        inv.setItem(5, ItemUtil.create(Material.CHEST, ChatColor.GREEN + "Backpack " + ChatColor.GRAY + "(Right-Click)"));
        inv.setItem(6, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "Watch " + ChatColor.GRAY + "(Right-Click)",
                Arrays.asList(ChatColor.GRAY + "Right-Click to open the", ChatColor.GRAY + "Show Schedule Menu")));
        if (book) {
            ParkManager.autographManager.giveBook(player);
        }
        ParkManager.bandUtil.giveBandToPlayer(player);
        inv.setItem(4, ItemUtil.create(Material.THIN_GLASS, 1, ChatColor.GRAY +
                "This Slot is Reserved for " + ChatColor.BLUE + "Ride Items", Arrays.asList(ChatColor.GRAY +
                "This is for games such as " + ChatColor.GREEN + "Buzz", ChatColor.GREEN +
                "Lightyear's Space Ranger Spin ", ChatColor.GRAY + "and " + ChatColor.YELLOW +
                "Toy Story Midway Mania.")));
    }

    private void warpToNearestWarp(Player player) {
        Location loc = player.getLocation();
        loc.setWorld(Bukkit.getWorlds().get(0));
        Warp w = null;
        double distance = -1;
        for (Warp warp : new ArrayList<>(ParkManager.warps)) {
            if (!warp.getServer().equals(Core.getServerType())) {
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

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CPlayer cp = Core.getPlayerManager().getPlayer(event.getPlayer());
        ParkManager.storageManager.logout(player);
        if (player.getVehicle() != null) {
            player.getVehicle().eject();
        }
        if (ParkManager.hotelServer) {
            ParkManager.hotelManager.closeDoor(player);
        }
        BlockEdit.logout(player.getUniqueId());
        ParkManager.queueManager.silentLeaveAllQueues(player);
        ParkManager.autographManager.logout(cp);
        ParkManager.bandUtil.cancelLoadPlayerData(player.getUniqueId());
        ParkManager.visibilityUtil.logout(Core.getPlayerManager().getPlayer(player));
        WatchTask.removeFromMessage(player.getUniqueId());
        ParkManager.blockChanger.logout(player);
        if (Core.getServerType().equals("MK")) {
            ParkManager.stitch.logout(player);
        }
        if (Core.getServerType().equals("Epcot")) {
            DesignStation.removePlayerVehicle(player.getUniqueId());
        }
        ParkManager.playerData.remove(player.getUniqueId());
    }

    public void setInventory(UUID uuid) {
        needInvSet.remove(uuid);
    }

    public boolean isSet(UUID uuid) {
        return !needInvSet.containsKey(uuid);
    }
}