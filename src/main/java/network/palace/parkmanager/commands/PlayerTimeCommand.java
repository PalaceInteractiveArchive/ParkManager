package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandMeta(description = "Set player time", rank = Rank.MOD)
public class PlayerTimeCommand extends CoreCommand {

    public PlayerTimeCommand() {
        super("ptime");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/ptime [day/noon/night/1000/reset] [Username]");
            return;
        }
        if (args.length < 2) {
            setPlayerTime(player.getBukkitPlayer(), player, args[0]);
        } else {
            CPlayer target = Core.getPlayerManager().getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            setPlayerTime(player.getBukkitPlayer(), target, args[0]);
        }
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/ptime [day/noon/night/1000/reset] [Username]");
            return;
        }
        CPlayer target = Core.getPlayerManager().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        setPlayerTime(sender, target, args[0]);
    }

    private void setPlayerTime(CommandSender sender, CPlayer target, String s) {
        boolean same = (sender instanceof Player) && ((Player) sender).getUniqueId().equals(target.getUniqueId());
        long time;
        switch (s.toLowerCase()) {
            case "day": {
                time = 1000;
                break;
            }
            case "noon": {
                time = 6000;
                break;
            }
            case "night": {
                time = 16000;
                break;
            }
            case "reset": {
                time = -1;
                break;
            }
            default: {
                try {
                    time = Long.parseLong(s);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "/ptime [day/noon/night/1000/reset] [Username]");
                    return;
                }
                break;
            }
        }
        if (time == -1) {
            target.getBukkitPlayer().resetPlayerTime();
            sender.sendMessage(ChatColor.DARK_AQUA + (same ? "Your" : (target.getName() + "'s")) + ChatColor.GREEN +
                    " time now matches the server.");
        } else {
            target.getBukkitPlayer().setPlayerTime(time, false);
            sender.sendMessage(ChatColor.DARK_AQUA + (same ? "Your" : (target.getName() + "'s")) + ChatColor.GREEN +
                    " time has been set to " + ChatColor.DARK_AQUA + time + ChatColor.GREEN + "!");
        }
    }
}
