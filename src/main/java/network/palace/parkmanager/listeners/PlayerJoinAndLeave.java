package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.events.CorePlayerJoinedEvent;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkwarp.ParkWarp;
import network.palace.parkwarp.handlers.Warp;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerJoinAndLeave implements Listener {

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        ParkManager.getPlayerUtil().addLoginData(uuid,
                Core.getMongoHandler().getParkJoinData(uuid, "buildmode", "settings", "magicband"),
                Core.getMongoHandler().getFriendList(uuid));
    }

    @EventHandler
    public void onPlayerJoin(CorePlayerJoinedEvent event) {
        CPlayer player = event.getPlayer();
        boolean buildMode = false;
        Document loginData = ParkManager.getPlayerUtil().removeLoginData(player.getUniqueId());
        if (loginData == null) {
            player.kick(ChatColor.RED + "An error occurred while you were joining, try again in a few minutes!");
            return;
        }

        if (loginData.containsKey("buildmode")) buildMode = loginData.getBoolean("buildmode");
        player.getRegistry().addEntry("friends", loginData.get("friends"));

        Document magicbandData = (Document) loginData.get("magicband");
        ParkManager.getMagicBandManager().handleJoin(player, magicbandData);

        ParkManager.getStorageManager().handleJoin(player, buildMode);

        Document settings = (Document) loginData.get("settings");
        String visibility;
        if (!settings.containsKey("visibility") || !(settings.get("visibility") instanceof String)) {
            visibility = "all";
        } else {
            visibility = settings.getString("visibility");
        }
        ParkManager.getVisibilityUtil().handleJoin(player, visibility);

        String pack;
        if (!settings.containsKey("pack") || !(settings.get("pack") instanceof String)) {
            pack = "ask";
        } else {
            pack = settings.getString("pack");
        }
        ParkManager.getPackManager().handleJoin(player, pack);

        if (ParkManager.getConfigUtil().isSpawnOnJoin()) {
            player.teleport(ParkManager.getConfigUtil().getSpawn());
        } else if (ParkManager.getConfigUtil().isWarpOnJoin()) {
            Warp w = null;
            double distance = -1;
            for (Warp warp : new ArrayList<>(ParkWarp.getWarpUtil().getWarps())) {
                if (warp.getLocation() == null
                        || !warp.getServer().equals(Core.getServerType())
                        || (warp.getRank() != null && player.getRank().getRankId() < warp.getRank().getRankId()))
                    continue;
                if (distance == -1) {
                    w = warp;
                    distance = warp.getLocation().distance(player.getLocation());
                    continue;
                }
                double d = warp.getLocation().distance(player.getLocation());
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
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        handleDisconnect(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerKick(PlayerKickEvent event) {
        handleDisconnect(event.getPlayer().getUniqueId());
    }

    private void handleDisconnect(UUID uuid) {
        CPlayer player = Core.getPlayerManager().getPlayer(uuid);
        if (player == null) return;

        ParkManager.getStorageManager().logout(player);
    }
}
