package us.mcmagic.magicassistant.listeners;

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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.commands.Commandvanish;
import us.mcmagic.magicassistant.designstation.DesignStation;
import us.mcmagic.magicassistant.handlers.HotelRoom;
import us.mcmagic.magicassistant.handlers.PlayerData;
import us.mcmagic.magicassistant.handlers.Warp;
import us.mcmagic.magicassistant.hotels.HotelManager;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerJoinAndLeave implements Listener {
    private List<UUID> firstJoins = new ArrayList<>();

    @EventHandler
    public void onAsyncLogin(AsyncPlayerPreLoginEvent event) {
        try {
            UUID uuid = event.getUniqueId();
            MagicAssistant.bandUtil.setupPlayerData(uuid);
            MagicAssistant.autographManager.setBook(uuid);
            if (MagicAssistant.getPlayerData(uuid) == null) {
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
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            final Player player = event.getPlayer();
            User user = MCMagicCore.getUser(player.getUniqueId());
            MagicAssistant.userCache.remove(player.getUniqueId());
            MagicAssistant.userCache.put(player.getUniqueId(), player.getName());
            PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
            if (!data.getVisibility()) {
                MagicAssistant.vanishUtil.addToHideAll(player);
            }
            for (PotionEffect type : player.getActivePotionEffects()) {
                player.removePotionEffect(type.getType());
            }
            if (user.getRank().getRankId() >= Rank.CASTMEMBER.getRankId()) {
                player.setGameMode(GameMode.CREATIVE);
            } else {
                player.setGameMode(GameMode.ADVENTURE);
            }
            if (MagicAssistant.spawnOnJoin || !player.hasPlayedBefore()) {
                player.performCommand("spawn");
            } else {
                warpToNearestWarp(player);
            }
            for (String msg : MagicAssistant.joinMessages) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
            MagicAssistant.vanishUtil.login(player);
            if (user.getRank().getRankId() > Rank.CASTMEMBER.getRankId()) {
                Commandvanish.vanish(player.getUniqueId());
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    if (MCMagicCore.getUser(tp.getUniqueId()).getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                        tp.hidePlayer(player);
                    }
                }
            }
            if (MagicAssistant.hubServer) {
                if (firstJoins.contains(player.getUniqueId())) {
                    firstJoins.remove(player.getUniqueId());
                    PlayerInventory pi = player.getInventory();
                    for (Map.Entry<Integer, Integer> item : MagicAssistant.firstJoinItems.entrySet()) {
                        ItemStack i = new ItemStack(item.getKey(), item.getValue());
                        pi.addItem(i);
                    }
                }
                if (user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                    if (player.getLocation().distance(MagicAssistant.spawn) <= 5) {
                        for (Player tp : Bukkit.getOnlinePlayers()) {
                            if (tp.getUniqueId().equals(player.getUniqueId())) {
                                continue;
                            }
                            tp.hidePlayer(player);
                        }
                    }
                }
            }
            ItemStack helm = player.getInventory().getHelmet();
            if (helm != null && helm.getItemMeta() != null && helm.getItemMeta().getDisplayName() != null) {
                if (helm.getItemMeta().getDisplayName().toLowerCase().endsWith("mickey ears")) {
                    player.getInventory().setHelmet(new ItemStack(Material.AIR));
                }
            }
            if (MagicAssistant.shooter != null) {
                player.getInventory().remove(MagicAssistant.shooter.getItem().getType());
            }
            MagicAssistant.bandUtil.giveBandToPlayer(player);
            MagicAssistant.autographManager.giveBook(player);
            if (MCMagicCore.getMCMagicConfig().serverName.equals("Resorts")) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(MagicAssistant.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        HotelManager manager = MagicAssistant.hotelManager;
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
                            if (room.getCheckoutTime() <= (System.currentTimeMillis() / 1000) && room.getCurrentOccupant()
                                    != null && room.getCurrentOccupant().equals(player.getUniqueId())) {
                                manager.checkout(room, true);
                            }
                        }
                    }
                }, 60L);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void warpToNearestWarp(Player player) {
        Location loc = getLoc(player);
        loc.setWorld(Bukkit.getWorlds().get(0));
        Warp w = null;
        double distance = -1;
        for (Warp warp : new ArrayList<>(MagicAssistant.warps)) {
            if (!warp.getServer().equals(MCMagicCore.getMCMagicConfig().serverName)) {
                continue;
            }
            if (warp.getLocation() == null) {
                continue;
            }
            if (warp.getName().startsWith("dvc") || warp.getName().startsWith("char") ||
                    warp.getName().startsWith("staff")) {
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
            Bukkit.broadcastMessage("Null!");
            return new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
        }
        return new Location(Bukkit.getWorlds().get(0), ep.locX, ep.locY, ep.locZ);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (player.getVehicle() != null) {
            player.getVehicle().eject();
        }
        if (MCMagicCore.getMCMagicConfig().serverName.equals("Resorts")) {
            HotelManager manager = MagicAssistant.hotelManager;
            for (HotelRoom room : manager.getRooms()) {
                if (room.isOccupied() && room.getCurrentOccupant().equals(player.getUniqueId())) {
                    manager.closeDoor(room);
                    break;
                }
            }
        }
        MagicAssistant.queueManager.silentLeaveAllQueues(player);
        MagicAssistant.autographManager.logout(player);
        MagicAssistant.parkSoundManager.logout(player);
        MagicAssistant.bandUtil.cancelLoadPlayerData(player.getUniqueId());
        MagicAssistant.backpackManager.logout(player);
        MagicAssistant.stitch.logout(player);
        MagicAssistant.vanishUtil.logout(player);
        Commandvanish.unvanish(player.getUniqueId());
        MagicAssistant.blockChanger.logout(player);
        if (MagicAssistant.shooter != null) {
            if (player.getInventory().contains(MagicAssistant.shooter.getItem())) {
                player.getInventory().remove(MagicAssistant.shooter.getItem());
            }
        }
        DesignStation.removePlayerVehicle(player.getUniqueId());
        MagicAssistant.bandUtil.removePlayerData(player);
    }
}