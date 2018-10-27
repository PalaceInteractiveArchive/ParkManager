package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.leaderboard.LeaderboardManager;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@CommandMeta(description = "Get top ride leaderboards", rank = Rank.DEVELOPER)
public class LeaderboardCommand extends CoreCommand {

    public LeaderboardCommand() {
        super("leaderboard");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 1 && args[0].equalsIgnoreCase("update")) {
            sender.sendMessage(ChatColor.GREEN + "Updating Ride Counter Leaderboards...");
            Core.runTaskAsynchronously(() -> {
                ParkManager.getInstance().getLeaderboardManager().update();
                sender.sendMessage(ChatColor.GREEN + "Leaderboards updated!");
            });
            return;
        }
        if (args.length < 2 || !MiscUtil.checkIfInt(args[0])) {
            sender.sendMessage(ChatColor.RED + "/leaderboard [Top #] [Ride]");
            return;
        }

        sender.sendMessage(ChatColor.AQUA + "Gathering leaderboard data...");

        Core.runTaskAsynchronously(() -> {
            int top = Integer.parseInt(args[0]);
            StringBuilder name = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                name.append(args[i]).append(" ");
            }

            String rideName = name.toString().trim();

            List<Document> list = Core.getMongoHandler().getRideCounterLeaderboard(rideName, top);

            List<String> messages = new ArrayList<>();
            for (Document doc : list) {
                UUID uuid = UUID.fromString(doc.getString("uuid"));
                String n;
                if (ParkManager.getInstance().getUserCache().containsKey(uuid)) {
                    n = ParkManager.getInstance().getUserCache().get(uuid);
                } else {
                    n = Core.getMongoHandler().uuidToUsername(uuid);
                    ParkManager.getInstance().addToUserCache(uuid, n);
                }
                Rank r = Core.getMongoHandler().getRank(uuid);
                int count = doc.getInteger("total");
                messages.add(ChatColor.BLUE + "" + count + ": " + r.getTagColor() + n);
            }
            LeaderboardManager.sortLeaderboardMessages(messages);
            sender.sendMessage(ChatColor.BLUE + "Ride Counter Leaderboard for " + ChatColor.GOLD + rideName + ":");
            messages.forEach(sender::sendMessage);
        });
    }
}
