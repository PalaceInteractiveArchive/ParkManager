package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.leaderboard.LeaderboardManager;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@CommandMeta(description = "Get top ride leaderboards", rank = Rank.DEVELOPER)
public class LeaderboardCommand extends CoreCommand {

    public LeaderboardCommand() {
        super("leaderboard");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/leaderboard [update]");
            sender.sendMessage(ChatColor.RED + "/leaderboard [top #] [ride]");
            return;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("update")) {
                sender.sendMessage(ChatColor.GREEN + "Updating Ride Counter Leaderboards...");
                Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
                    ParkManager.getLeaderboardManager().update();
                    sender.sendMessage(ChatColor.GREEN + "Leaderboards updated!");
                });
                return;
            }
            sender.sendMessage(ChatColor.RED + "/leaderboard [update]");
            sender.sendMessage(ChatColor.RED + "/leaderboard [top #] [ride]");
            return;
        }
        if (args.length > 1) {
            int top;
            try {
                top = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "'" + args[0] + "' is not a number!");
                return;
            }
            StringBuilder name = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                name.append(args[i]).append(" ");
            }
            String rideName = name.toString().trim();
            sender.sendMessage(ChatColor.AQUA + "Gathering leaderboard data for " + rideName + "...");
            Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {

                List<Document> list = Core.getMongoHandler().getRideCounterLeaderboard(rideName, top);

                List<String> messages = new ArrayList<>();
                for (Document doc : list) {
                    messages.add(ChatColor.BLUE + LeaderboardManager.getFormattedName(doc));
                }
                LeaderboardManager.sortLeaderboardMessages(messages);

                sender.sendMessage(ChatColor.BLUE + "Ride Counter Leaderboard for " + ChatColor.GOLD + rideName + ":");
                messages.forEach(sender::sendMessage);
            });
        }
    }
}
