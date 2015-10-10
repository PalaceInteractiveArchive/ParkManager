package us.mcmagic.magicassistant.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.Warp;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.chat.formattedmessage.FormattedMessage;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

import java.util.HashMap;

public class Commandnearby implements CommandExecutor {

    public static final int DEFAULT_SEARCH_DISTANCE = 100;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        Location center  = player.getLocation().clone();
        HashMap<Warp, Integer> nearby = new HashMap<>();
        for (Warp warp : MagicAssistant.warps) {
            if (!warp.getServer().equals(MCMagicCore.getMCMagicConfig().serverName)) {
                continue;
            }
            if (warp.getLocation() == null) {
                continue;
            }
            User user = MCMagicCore.getUser(player.getUniqueId());
            if (warp.getName().startsWith("dvc") && user.getRank().getRankId() < Rank.DVCMEMBER.getRankId()) {
                continue;
            }
            if (warp.getName().startsWith("char") && user.getRank().getRankId() < Rank.CHARACTERGUEST.getRankId()) {
                continue;
            }
            if (warp.getName().startsWith("staff") && user.getRank().getRankId() < Rank.INTERN.getRankId()) {
                continue;
            }
            int distance = (int) warp.getLocation().distance(center);
            if (distance <= DEFAULT_SEARCH_DISTANCE) {
                nearby.put(warp, distance);
            }
        }
        if (!nearby.isEmpty()) {
            player.sendMessage("Nearby: (Click to warp)");
            for (Warp warp : nearby.keySet()) {
                FormattedMessage message = new FormattedMessage(" - ").color(ChatColor.GREEN);
                message.then(warp.getName()).color(ChatColor.AQUA).command("/warp " + warp.getName()).tooltip("Click to warp").color(ChatColor.GREEN).then(" (" + nearby.get(warp) + " blocks)").color(ChatColor.AQUA);
                message.send(player);
            }
        } else {
            player.sendMessage(ChatColor.RED + "Could not find any warps within " + DEFAULT_SEARCH_DISTANCE + " blocks.");
        }
        return true;
    }


}
