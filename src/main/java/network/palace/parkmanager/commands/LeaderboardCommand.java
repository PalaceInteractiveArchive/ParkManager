package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.core.utils.MiscUtil;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandMeta(description = "Get top ride leaderboards", rank = Rank.DEVELOPER)
public class LeaderboardCommand extends CoreCommand {

    public LeaderboardCommand() {
        super("leaderboard");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 2 || !MiscUtil.checkIfInt(args[0])) {
            sender.sendMessage(ChatColor.RED + "/leaderboard [Top #] [Ride]");
            return;
        }

        int top = Integer.parseInt(args[0]);
        StringBuilder name = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            name.append(args[i]).append(" ");
        }

        List<Document> list = Core.getMongoHandler().getRideCounterLeaderboard(name.toString().trim(), top);

        for (Document doc : list) {
            sender.sendMessage(doc.getString("uuid") + ": " + doc.getInteger("total"));
        }
    }
}
