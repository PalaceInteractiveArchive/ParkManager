package us.mcmagic.magicassistant.listeners;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.commands.Commandvanish;
import us.mcmagic.magicassistant.designstation.DesignStation;
import us.mcmagic.magicassistant.handlers.HotelRoom;
import us.mcmagic.magicassistant.handlers.PlayerData;
import us.mcmagic.magicassistant.shooter.Shooter;
import us.mcmagic.magicassistant.utils.HotelUtil;
import us.mcmagic.magicassistant.utils.InventorySql;
import us.mcmagic.magicassistant.utils.VisibleUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerJoinAndLeave implements Listener {
    private static HashMap<UUID, ItemStack[]> invData = new HashMap<>();
    private static HashMap<UUID, ItemStack[]> armorData = new HashMap<>();
    private static HashMap<UUID, ItemStack[]> endData = new HashMap<>();

    @EventHandler
    public void onAsyncLogin(AsyncPlayerPreLoginEvent event) {
        try {
            UUID uuid = event.getUniqueId();
            if (MagicAssistant.crossServerInv) {
                List<ItemStack[]> stuff = InventorySql.invContents(uuid);
                ItemStack[] inv = stuff.get(0);
                ItemStack[] armor = stuff.get(1);
                ItemStack[] end = InventorySql.endInvContents(uuid);
                if (invData.containsKey(uuid)) {
                    invData.remove(uuid);
                }
                if (armorData.containsKey(uuid)) {
                    armorData.remove(uuid);
                }
                if (endData.containsKey(uuid)) {
                    endData.remove(uuid);
                }
                if (inv != null) {
                    invData.put(uuid, inv);
                }
                if (armor != null) {
                    armorData.put(uuid, inv);
                }
                if (end != null) {
                    endData.put(uuid, inv);
                }
            }
            PlayerData data = MagicAssistant.bandUtil.setupPlayerData(uuid);
            if (MagicAssistant.getPlayerData(uuid) == null) {
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
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
        if (user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
            event.setKickMessage("This server will be available soon!");
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            final Player player = event.getPlayer();
            User user = MCMagicCore.getUser(player.getUniqueId());
            if (!MagicAssistant.userCache.containsKey(player.getUniqueId())) {
                MagicAssistant.userCache.put(player.getUniqueId(), player.getName());
            } else {
                if (!MagicAssistant.userCache.get(player.getUniqueId()).equals(player.getName())) {
                    MagicAssistant.userCache.put(player.getUniqueId(), player.getName());
                }

            }
            PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
            if (!data.getVisibility()) {
                VisibleUtil.addToHideAll(player);
            }
            if (user.getRank().getRankId() >= Rank.CASTMEMBER.getRankId()) {
                player.setGameMode(GameMode.CREATIVE);
            } else {
                player.setGameMode(GameMode.ADVENTURE);
            }
            if (MagicAssistant.spawnOnJoin) {
                player.performCommand("spawn");
            }
            for (String msg : MagicAssistant.joinMessages) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
            if (user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                VisibleUtil.hideForHideAll(player);
            }
            if (MagicAssistant.hubServer) {
                if (!player.hasPlayedBefore()) {
                    int total = Bukkit.getOfflinePlayers().length + 100000;
                    for (String msg : MagicAssistant.newJoinMessage) {
                        String nmsg = msg.replaceAll("%pl%", player.getName());
                        Bukkit.broadcastMessage(nmsg.replaceAll("%total%", "" + (total)));
                    }
                    PlayerInventory pi = player.getInventory();
                    for (Map.Entry<Integer, Integer> item : MagicAssistant.firstJoinItems.entrySet()) {
                        ItemStack i = new ItemStack(item.getKey(), item.getValue());
                        pi.addItem(i);
                    }
                }
            }
            if (MagicAssistant.crossServerInv) {
                PlayerInventory pi = player.getInventory();
                if (invData.containsKey(player.getUniqueId())) {
                    ItemStack[] inv = invData.remove(player.getUniqueId());
                    if (inv == null) {
                        player.sendMessage(ChatColor.RED +
                                "An error has occured! Please report this to a Staff Member (Error Code 102)");
                    } else {
                        pi.setContents(inv);
                    }
                }
                if (armorData.containsKey(player.getUniqueId())) {
                    ItemStack[] armor = armorData.remove(player.getUniqueId());
                    if (armor == null) {
                        player.sendMessage(ChatColor.RED +
                                "An error has occured! Please report this to a Staff Member (Error Code 103)");
                    } else {
                        pi.setArmorContents(armor);
                    }
                }
                if (endData.containsKey(player.getUniqueId())) {
                    ItemStack[] end = endData.remove(player.getUniqueId());
                    if (end == null) {
                        player.sendMessage(ChatColor.RED +
                                "An error has occured! Please report this to a Staff Member (Error Code 104)");
                    } else {
                        player.getEnderChest().setContents(end);
                    }
                }
                MagicAssistant.bandUtil.giveBandToPlayer(player);
                if (Shooter.game != null) {
                    pi.remove(Shooter.getItem().getType());
                }
                pi.remove(Shooter.getItem().getType());
                ItemStack helm = player.getInventory().getHelmet();
                if (helm != null && helm.getItemMeta() != null) {
                    if (helm.getItemMeta().getDisplayName().toLowerCase().endsWith("mickey ears")) {
                        player.getInventory().setHelmet(new ItemCreator(Material.AIR));
                    }
                }
                player.sendMessage(ChatColor.GREEN + "Inventory updated!");
            } else {
                ItemStack helm = player.getInventory().getHelmet();
                if (helm != null && helm.getItemMeta() != null && helm.getItemMeta().getDisplayName() != null) {
                    if (helm.getItemMeta().getDisplayName().toLowerCase().endsWith("mickey ears")) {
                        player.getInventory().setHelmet(new ItemStack(Material.AIR));
                    }
                }
                if (Shooter.game != null) {
                    player.getInventory().remove(Shooter.getItem().getType());
                }
            }
            MagicAssistant.bandUtil.giveBandToPlayer(player);
            Bukkit.getScheduler().runTaskLaterAsynchronously(MagicAssistant.getInstance(), new Runnable() {
                @Override
                public void run() {
                    for (HotelRoom room : MagicAssistant.hotelRooms) {
                        if (room.getCheckoutNotificationRecipient() != null &&
                                room.getCheckoutNotificationRecipient().equals(player.getUniqueId())) {
                            room.setCheckoutNotificationRecipient(null);
                            room.setCheckoutTime(0);
                            HotelUtil.updateHotelRoom(room);
                            HotelUtil.updateRooms();
                            player.sendMessage(ChatColor.GREEN + "Your reservation of the " + room.getName() +
                                    " room has lapsed and you have been checked out. Please come stay with us again soon!");
                            HotelUtil.expire.send(player);
                            player.playSound(player.getLocation(), Sound.BLAZE_DEATH, 10f, 1f);
                            return;
                        }
                        if (room.getCheckoutTime() <= (System.currentTimeMillis() / 1000) && room.getCurrentOccupant()
                                != null && room.getCurrentOccupant().equals(player.getUniqueId())) {
                            HotelUtil.checkout(room, true);
                        }
                    }
                }
            }, 60L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (MagicAssistant.crossServerInv) {
            Bukkit.getScheduler().runTaskAsynchronously(MagicAssistant.getInstance(), new Runnable() {
                @Override
                public void run() {
                    InventorySql.updateInventory(player);
                    InventorySql.updateEndInventory(player);
                }
            });
        }
        if (player.getVehicle() != null) {
            player.getVehicle().eject();
        }
        for (HotelRoom room : HotelUtil.getRooms()) {
            if (room.isOccupied() && room.getCurrentOccupant().equals(player.getUniqueId())) {
                HotelUtil.closeDoor(room);
                break;
            }
        }
        MagicAssistant.bandUtil.cancelLoadPlayerData(player.getUniqueId());
        MagicAssistant.bandUtil.removePlayerData(player);
        VisibleUtil.logout(player.getUniqueId());
        Commandvanish.hidden.remove(player.getUniqueId());
        MagicAssistant.blockChanger.logout(player);
        if (Shooter.getItem() != null) {
            if (player.getInventory().contains(Shooter.getItem())) {
                player.getInventory().remove(Shooter.getItem());
            }
        }
        DesignStation.removePlayerVehicle(player.getUniqueId());
    }
}
