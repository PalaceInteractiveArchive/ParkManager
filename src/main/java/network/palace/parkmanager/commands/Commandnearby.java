package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkwarp.handlers.Warp;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.HashMap;

@CommandMeta(description = "Find nearby warps")
public class Commandnearby extends CoreCommand {
    public static final int DEFAULT_SEARCH_DISTANCE = 100;

    public Commandnearby() {
        super("nearby");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Location center = player.getLocation().clone();
        HashMap<Warp, Integer> nearby = new HashMap<>();
        for (Warp warp : ParkManager.getInstance().getWarps()) {
            if (!warp.getServer().equals(Core.getInstanceName())) {
                continue;
            }
            if (warp.getLocation() == null) {
                continue;
            }
            if (warp.getName().startsWith("dvc") && player.getRank().getRankId() < Rank.DVCMEMBER.getRankId()) {
                continue;
            }
            if (warp.getName().startsWith("char") && player.getRank().getRankId() < Rank.CHARACTER.getRankId()) {
                continue;
            }
            if (warp.getName().startsWith("staff") && player.getRank().getRankId() < Rank.SQUIRE.getRankId()) {
                continue;
            }
            int distance = (int) warp.getLocation().distance(center);
            if (distance <= DEFAULT_SEARCH_DISTANCE) {
                nearby.put(warp, distance);
            }
        }
        if (!nearby.isEmpty()) {
            FormattedMessage message = new FormattedMessage("Nearby locations: (Click to warp there)\n");
            message.color(ChatColor.GREEN);
            for (int i = 0; i < nearby.keySet().size(); i++) {
                Warp warp = (Warp) nearby.keySet().toArray()[i];
                message.then(" - ");
                message.color(ChatColor.GREEN);
                message.then(warp.getName()).color(ChatColor.AQUA).command("/warp " +
                        warp.getName()).tooltip(ChatColor.GREEN + "Click to warp").color(ChatColor.GREEN).then(" (" +
                        nearby.get(warp) + " blocks)").color(ChatColor.AQUA);
                if (i < nearby.keySet().size() - 1) message.then("\n");
            }
            message.send(player);
        } else {
            player.sendMessage(ChatColor.RED + "Could not find any warps within " + DEFAULT_SEARCH_DISTANCE +
                    " blocks.");
        }
    }
}
