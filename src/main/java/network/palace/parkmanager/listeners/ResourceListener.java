package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.events.CurrentPackReceivedEvent;
import network.palace.core.player.CPlayer;
import network.palace.core.resource.ResourceStatusEvent;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Marc on 1/14/17.
 */
public class ResourceListener implements Listener {

    @EventHandler
    public void onCurrentPackReceive(CurrentPackReceivedEvent event) {
        CPlayer player = event.getPlayer();
        String current = event.getPack();
        PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
        String preferred = data.getPack();
        if (!preferred.equals("no")) {
            if (preferred.equals("yes") && !current.equalsIgnoreCase(ParkManager.packManager.getServerPack())) {
                Core.getResourceManager().sendPack(player, ParkManager.packManager.getServerPack());
            } else if (preferred.equals("blank") && !current.equals("none")) {
                //Send blank
                Core.getResourceManager().sendPack(player, "Blank");
                try (Connection connection = Core.getSqlUtil().getConnection()) {
                    PreparedStatement sql = connection.prepareStatement("");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (!preferred.equals("yes")) {
                //Choose a pack
                player.sendMessage(ChatColor.GREEN + "Please choose a Resource Pack setting for Theme Parks.");
                Bukkit.getScheduler().runTaskLater(ParkManager.getInstance(), () ->
                        ParkManager.packManager.openMenu(player), 20L);
            }
        }
    }

    @EventHandler
    public void onResourceStatus(ResourceStatusEvent event) {
        CPlayer player = event.getPlayer();
        switch (event.getStatus()) {
            case ACCEPTED:
                player.sendMessage(ChatColor.GREEN + "Resource Pack accepted! Downloading now...");
                break;
            case LOADED:
                player.sendMessage(ChatColor.GREEN + "Resource Pack loaded!");
                break;
            case FAILED:
                player.sendMessage(ChatColor.RED + "Download failed! Please report this to a Staff Member. (Error Code 101)");
                break;
            case DECLINED:
                for (int i = 0; i < 5; i++) {
                    player.sendMessage(" ");
                }
                player.sendMessage(ChatColor.RED + "You have declined the Resource Pack!");
                player.sendMessage(ChatColor.YELLOW + "For help with this, visit: " + ChatColor.AQUA +
                        "https://palace.network/rphelp");
                break;
            default:
                player.sendMessage(ChatColor.RED + "Download failed! Please report this to a Staff Member. (Error Code 101)");
        }
    }
}
