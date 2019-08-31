package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.RideCount;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.TreeMap;

@CommandMeta(description = "Add a ride counter for a player", rank = Rank.MOD)
public class AddRideCounterCommand extends CoreCommand {

    public AddRideCounterCommand() {
        super("rc");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/rc [username] [ride name]");
            return;
        }
        CPlayer tp = Core.getPlayerManager().getPlayer(args[0]);
        if (tp == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        if (!tp.isInVehicle()) {
            sender.sendMessage(ChatColor.RED + "That player is not in a vehicle!");
            return;
        }
        StringBuilder rideName = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            rideName.append(args[i]);
            rideName.append(" ");
        }
        String finalRideName = rideName.toString().trim();
        HashMap<String, RideCount> cache = ((HashMap<String, RideCount>) tp.getRegistry().getEntry("rideCounterCache"));
        if (!cache.containsKey(finalRideName)) {
            cache.put(finalRideName, new RideCount(finalRideName, Core.getInstanceName()));
        } else {
            cache.get(finalRideName).addCount(1);
        }
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
            Core.getMongoHandler().logRideCounter(tp.getUniqueId(), finalRideName);

            TreeMap<String, RideCount> rides = ParkManager.getRideCounterUtil().getRideCounters(tp);
            if (rides.containsKey(finalRideName)) {
                rides.get(finalRideName).addCount(1);
            } else {
                rides.put(finalRideName, new RideCount(finalRideName, Core.getServerType()));
            }
            if (rides.size() >= 30) {
                tp.giveAchievement(15);
            } else if (rides.size() >= 20) {
                tp.giveAchievement(14);
            } else if (rides.size() >= 10) {
                tp.giveAchievement(13);
            } else if (rides.size() >= 1) {
                tp.giveAchievement(12);
            }

            sender.sendMessage(ChatColor.GREEN + "Added 1 to " + tp.getName() + "'s counter for " + finalRideName);
            tp.sendMessage(ChatColor.GREEN + "--------------" + ChatColor.GOLD + "" + ChatColor.BOLD +
                    "Ride Counter" + ChatColor.GREEN + "-------------\n" + ChatColor.YELLOW +
                    "Ride Counter for " + ChatColor.AQUA + finalRideName + ChatColor.YELLOW +
                    " is now at " + ChatColor.AQUA + rides.get(finalRideName).getCount() +
                    ChatColor.GREEN + "\n----------------------------------------");
            tp.playSound(tp.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 100f, 0.75f);
        });
    }
}
