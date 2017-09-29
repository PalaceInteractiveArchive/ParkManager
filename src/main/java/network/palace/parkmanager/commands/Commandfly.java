package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandMeta(description = "Toggle fly mode")
@CommandPermission(rank = network.palace.core.player.Rank.SPECIALGUEST)
public class Commandfly extends CoreCommand {

    public Commandfly() {
        super("fly");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            if (args.length == 1) {
                Player player = Bukkit.getPlayer(args[0]);
                if (player == null) {
                    sender.sendMessage(ChatColor.RED
                            + "I can'commands find that player!");
                    return;
                }
                if (player.getAllowFlight()) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.sendMessage(ChatColor.RED + "You can't fly anymore!");
                    sender.sendMessage(player.getName() + " can't fly anymore!");
                    return;
                }
                player.setAllowFlight(true);
                player.sendMessage(ChatColor.GREEN + "You can fly!");
                sender.sendMessage(player.getName() + " can now fly!");
                return;
            }
            sender.sendMessage(ChatColor.RED + "/fly [Username]");
            return;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            Rank rank = Core.getPlayerManager().getPlayer(player.getUniqueId()).getRank();
            if (rank.getRankId() < Rank.MOD.getRankId()) {
                if (player.getAllowFlight()) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.sendMessage(ChatColor.RED + "You can't fly anymore!");
                    return;
                }
                player.setAllowFlight(true);
                player.sendMessage(ChatColor.GREEN + "You can fly!");
                return;
            }
            Player tp = Bukkit.getPlayer(args[0]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "I can't find that player!");
                return;
            }
            if (tp.getAllowFlight()) {
                tp.setAllowFlight(false);
                tp.setFlying(false);
                tp.sendMessage(ChatColor.RED + "You can't fly anymore!");
                player.sendMessage(ChatColor.RED + tp.getName() + " can't fly anymore!");
                return;
            }
            tp.setAllowFlight(true);
            tp.sendMessage(ChatColor.GREEN + "You can fly!");
            player.sendMessage(ChatColor.GREEN + tp.getName() + " can now fly!");
            return;
        }
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            player.sendMessage(ChatColor.RED + "You can't fly anymore!");
            return;
        }
        player.setAllowFlight(true);
        player.sendMessage(ChatColor.GREEN + "You can fly!");
    }
}
