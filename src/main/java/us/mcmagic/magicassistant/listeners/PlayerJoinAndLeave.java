package us.mcmagic.magicassistant.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.commands.Command_vanish;
import us.mcmagic.magicassistant.handlers.HotelRoom;
import us.mcmagic.magicassistant.shooter.Shooter;
import us.mcmagic.magicassistant.utils.HotelUtil;
import us.mcmagic.magicassistant.utils.InventorySql;
import us.mcmagic.magicassistant.utils.VisibleUtil;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerJoinAndLeave implements Listener {
    private static HashMap<UUID, ItemStack[]> invData = new HashMap<>();
    private static HashMap<UUID, ItemStack[]> armorData = new HashMap<>();
    private static HashMap<UUID, ItemStack[]> endData = new HashMap<>();

    public void onAsyncLogin(AsyncPlayerPreLoginEvent event) {
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
        MagicAssistant.getInstance().bandUtil.setupPlayerData(uuid);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        MagicAssistant.getInstance().packManager.login(player);
        MagicAssistant.getInstance().bandUtil.loading.add(player.getUniqueId());
        if (!MagicAssistant.userCache.containsKey(player.getUniqueId())) {
            MagicAssistant.userCache.put(player.getUniqueId(), player.getName());
        } else {
            if (!MagicAssistant.userCache.get(player.getUniqueId()).equals(player.getName())) {
                MagicAssistant.userCache.put(player.getUniqueId(), player.getName());
            }

        }
        if (MagicAssistant.spawnOnJoin) {
            player.performCommand("spawn");
        }
        for (String msg : MagicAssistant.joinMessages) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }
        if (!player.hasPermission("band.stayvisible")) {
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
            MagicAssistant.getInstance().bandUtil.giveBandToPlayer(player);
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
            /*
            Bukkit.getScheduler().runTaskLaterAsynchronously(pl,
                    new Runnable() {
                        public void run() {
                            if (!InventorySql.playerDataContainsPlayer(player)) {
                                player.performCommand("spawn");
                                InventorySql.setupData(player);
                            } else {
                                List<ItemStack[]> stuff = InventorySql.invContents(player);
                                player.getInventory().setContents(stuff.get(0));
                                player.getInventory().setArmorContents(stuff.get(1));
                            }
                            if (!InventorySql.endPlayerDataContainsPlayer(player)) {
                                InventorySql.setupEndData(player);
                            } else {
                                player.getEnderChest().setContents(
                                        InventorySql.endInvContents(player));
                            }
                            MagicAssistant.getInstance().bandUtil.giveBandToPlayer(player);
                            player.sendMessage(ChatColor.GREEN + "Inventory Updated!");
                            player.getInventory().remove(Shooter.getItem().getType());
                        }
                    }, 100L);
                    */
        } else {
            ItemStack helm = player.getInventory().getHelmet();
            if (helm != null && helm.getItemMeta() != null) {
                if (helm.getItemMeta().getDisplayName().toLowerCase().endsWith("mickey ears")) {
                    player.getInventory().setHelmet(new ItemStack(Material.AIR));
                }
            }
            if (Shooter.game != null) {
                player.getInventory().remove(Shooter.getItem().getType());
            }
        }
        MagicAssistant.getInstance().bandUtil.giveBandToPlayer(player);
        Bukkit.getScheduler().runTaskLater(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                boolean updateNecessary = false;
                for (HotelRoom room : MagicAssistant.hotelRooms) {
                    if (room.getCheckoutNotificationRecipient() != null && room.getCheckoutNotificationRecipient().equalsIgnoreCase(player.getUniqueId().toString())) {
                        UUID uuid = UUID.fromString(room.getCheckoutNotificationRecipient());
                        if (Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline()) {
                            Bukkit.getPlayer(uuid).sendMessage(ChatColor.GREEN + "Your reservation of the " + room.getName() + " room has lapsed and you have been checked out.  Please come stay with us again soon!");
                            room.setCheckoutNotificationRecipient(null);
                            HotelUtil.updateRoom(room);
                            updateNecessary = true;
                        }
                        break;
                    }
                }
                if (updateNecessary) {
                    HotelUtil.updateRooms();
                }
            }
        }, 60L);

    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (MagicAssistant.serverEnabling) {
            event.setResult(Result.KICK_OTHER);
            event.setKickMessage("This server is still starting up!");
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
        try {
            MagicAssistant.getInstance().bandUtil.loading.remove(player.getUniqueId());
        } catch (Exception ignored) {
        }
        MagicAssistant.getInstance().bandUtil.removePlayerData(player);
        try {
            VisibleUtil.hideall.remove(player.getUniqueId());
        } catch (Exception ignored) {
        }
        try {
            Command_vanish.hidden.remove(player.getUniqueId());
        } catch (Exception ignored) {
        }
        MagicAssistant.getInstance().blockChanger.logout(player);
        if (Shooter.getItem() != null) {
            if (player.getInventory().contains(Shooter.getItem())) {
                player.getInventory().remove(Shooter.getItem());
            }
        }
    }
}
