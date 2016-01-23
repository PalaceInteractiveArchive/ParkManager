package us.mcmagic.parkmanager.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.chat.formattedmessage.FormattedMessage;
import us.mcmagic.mcmagiccore.permissions.Rank;

/**
 * Created by Marc on 10/23/15
 */
public class PlayerGameModeChange implements Listener {
    private FormattedMessage needBuild = new FormattedMessage("To enter Creative Mode, you must be in ").color(ChatColor.GREEN)
            .then("Build Mode! ").color(ChatColor.YELLOW).style(ChatColor.BOLD).then("Click here to switch modes")
            .color(ChatColor.AQUA).tooltip(ChatColor.DARK_AQUA + "Command: /build").command("/build");
    private FormattedMessage noBuild = new FormattedMessage("To enter Survival Mode, you cannot be in ").color(ChatColor.GREEN)
            .then("Build Mode! ").color(ChatColor.YELLOW).style(ChatColor.BOLD).then("Click here to switch modes")
            .color(ChatColor.AQUA).tooltip(ChatColor.DARK_AQUA + "Command: /build").command("/build");

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        GameMode newMode = event.getNewGameMode();
        switch (newMode) {
            case SPECTATOR:
                break;
            case CREATIVE:
                if (!BlockEdit.isInBuildMode(player.getUniqueId())) {
                    event.setCancelled(true);
                    needBuild.send(player);
                }
                break;
            case ADVENTURE:
                if (MCMagicCore.getUser(player.getUniqueId()).getRank().getRankId() <= Rank.CASTMEMBER.getRankId()) {
                    return;
                }
                event.setCancelled(true);
                player.sendMessage(ChatColor.GREEN + "You cannot enter Adventure Mode, change to " + ChatColor.YELLOW +
                        "Survival Mode!");
                break;
            case SURVIVAL:
                if (BlockEdit.isInBuildMode(player.getUniqueId())) {
                    event.setCancelled(true);
                    noBuild.send(player);
                }
                break;
        }
    }
}