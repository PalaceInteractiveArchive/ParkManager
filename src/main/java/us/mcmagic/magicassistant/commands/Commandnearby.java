package us.mcmagic.magicassistant.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.Warp;
import us.mcmagic.mcmagiccore.chat.formattedmessage.FormattedMessage;
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
            int distance = (int) warp.getLocation().distance(center);
            if (distance <= DEFAULT_SEARCH_DISTANCE) {
                nearby.put(warp, distance);
            }
        }
        if (!nearby.isEmpty()) {
            player.sendMessage(ChatColor.GREEN + "Nearby: (Click to warp)");
            for (Warp warp : nearby.keySet()) {
                FormattedMessage message = new FormattedMessage(ChatColor.GREEN + " - ");
                message.color(ChatColor.AQUA).then(warp.getName()).then(" (" + nearby.get(warp) + " blocks)").command("warp " + warp.getName());
                message.send(player);
            }
        } else {
            player.sendMessage(ChatColor.RED + "Could not find any warps within " + DEFAULT_SEARCH_DISTANCE + " blocks.");
        }
        return true;
    }
}
