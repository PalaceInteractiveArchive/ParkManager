package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.WeatherType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandMeta(description = "Set player weather", rank = Rank.CM)
public class PlayerWeatherCommand extends CoreCommand {

    public PlayerWeatherCommand() {
        super("pweather");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/pweather [rain/sun/reset] [Username]");
            return;
        }
        if (args.length < 2) {
            setPlayerWeather(player.getBukkitPlayer(), player, args[0]);
        } else {
            CPlayer target = Core.getPlayerManager().getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            setPlayerWeather(player.getBukkitPlayer(), target, args[0]);
        }
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/pweather [rain/sun/reset] [Username]");
            return;
        }
        CPlayer target = Core.getPlayerManager().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        setPlayerWeather(sender, target, args[0]);
    }

    private void setPlayerWeather(CommandSender sender, CPlayer target, String s) {
        boolean same = (sender instanceof Player) && ((Player) sender).getUniqueId().equals(target.getUniqueId());
        WeatherType type;
        switch (s.toLowerCase()) {
            case "sun": {
                type = WeatherType.CLEAR;
                break;
            }
            case "rain": {
                type = WeatherType.DOWNFALL;
                break;
            }
            case "reset": {
                type = null;
                break;
            }
            default: {
                sender.sendMessage(ChatColor.RED + "/pweather [rain/sun/reset] [Username]");
                return;
            }
        }
        if (type == null) {
            target.getBukkitPlayer().resetPlayerWeather();
            sender.sendMessage(ChatColor.DARK_AQUA + (same ? "Your" : (target.getName() + "'s")) + ChatColor.GREEN +
                    " weather now matches the server.");
        } else {
            target.getBukkitPlayer().setPlayerWeather(type);
            sender.sendMessage(ChatColor.DARK_AQUA + (same ? "Your" : (target.getName() + "'s")) + ChatColor.GREEN +
                    " weather has been set to " + ChatColor.DARK_AQUA + type.name().toLowerCase() + ChatColor.GREEN + "!");
        }
    }
}
