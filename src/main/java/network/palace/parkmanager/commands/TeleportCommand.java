package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.MathUtil;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandMeta(description = "Teleport command", rank = Rank.TRAINEE)
public class TeleportCommand extends CoreCommand {

    public TeleportCommand() {
        super("tp");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        switch (args.length) {
            case 1: {
                CPlayer target = Core.getPlayerManager().getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(ChatColor.RED + "Player not found!");
                    player.sendMessage(ChatColor.RED + "/tp " + ChatColor.BOLD + "[player]");
                    return;
                }
                teleport(player.getBukkitPlayer(), player, target, false);
                return;
            }
            case 2: {
                teleport(player.getBukkitPlayer(),
                        Core.getPlayerManager().getPlayer(args[0]),
                        Core.getPlayerManager().getPlayer(args[1]),
                        false);
                return;
            }
            case 3:
            case 5:
                try {
                    Location target;
                    if (args.length == 3) {
                        Location playerLoc = player.getLocation();
                        target = new Location(player.getWorld(),
                                getDouble(player.getBukkitPlayer(), args[0], "x"),
                                getDouble(player.getBukkitPlayer(), args[1], "y"),
                                getDouble(player.getBukkitPlayer(), args[2], "z"),
                                playerLoc.getYaw(),
                                playerLoc.getPitch());
                    } else {
                        target = new Location(player.getWorld(),
                                getDouble(player.getBukkitPlayer(), args[0], "x"),
                                getDouble(player.getBukkitPlayer(), args[1], "y"),
                                getDouble(player.getBukkitPlayer(), args[2], "z"),
                                (float) getDouble(player.getBukkitPlayer(), args[3], "yaw"),
                                (float) getDouble(player.getBukkitPlayer(), args[4], "pitch"));
                    }
                    teleport(player.getBukkitPlayer(), player, target);
                } catch (NumberFormatException e) {
                    player.sendMessage(e.getMessage());
                }
                return;
            case 4:
            case 6: {
                CPlayer moving = Core.getPlayerManager().getPlayer(args[0]);
                if (moving == null) {
                    player.sendMessage(ChatColor.RED + "Player not found!");
                    player.sendMessage(ChatColor.RED + "/tp " + ChatColor.BOLD + "[player] " + ChatColor.RED
                            + "[x] [y] [z] <yaw> <pitch>");
                    return;
                }
                try {
                    teleport(player.getBukkitPlayer(), moving, getLocation(args, player.getBukkitPlayer(), moving));
                } catch (NumberFormatException e) {
                    player.sendMessage(e.getMessage());
                }
                return;
            }
        }
        player.sendMessage(ChatColor.RED + "/tp [player] <target>");
        player.sendMessage(ChatColor.RED + "/tp [x] [y] [z] <yaw> <pitch>");
        player.sendMessage(ChatColor.RED + "/tp [player] [x] [y] [z] <yaw> <pitch>");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        switch (args.length) {
            case 2: {
                teleport(sender, Core.getPlayerManager().getPlayer(args[0]),
                        Core.getPlayerManager().getPlayer(args[1]), true);
                return;
            }
            case 4:
            case 6: {
                CPlayer moving = Core.getPlayerManager().getPlayer(args[0]);
                if (moving == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    sender.sendMessage(ChatColor.RED + "/tp " + ChatColor.BOLD + "[player] " + ChatColor.RED
                            + "[x] [y] [z] <yaw> <pitch>");
                    return;
                }
                try {
                    teleport(sender, moving, getLocation(args, sender, moving));
                } catch (NumberFormatException e) {
                    sender.sendMessage(e.getMessage());
                }
                return;
            }
        }
        sender.sendMessage(ChatColor.RED + "/tp [player] [target]");
        sender.sendMessage(ChatColor.RED + "/tp [player] [x] [y] [z] <yaw> <pitch>");
    }

    private Location getLocation(String[] args, CommandSender sender, CPlayer target) throws NumberFormatException {
        if (args.length == 4) {
            Location playerLoc = target.getLocation();
            return new Location(target.getWorld(),
                    getDouble(sender, args[1], "x"),
                    getDouble(sender, args[2], "y"),
                    getDouble(sender, args[3], "z"),
                    playerLoc.getYaw(),
                    playerLoc.getPitch());
        }
        return new Location(target.getWorld(),
                getDouble(sender, args[1], "x"),
                getDouble(sender, args[2], "y"),
                getDouble(sender, args[3], "z"),
                (float) getDouble(sender, args[4], "yaw"),
                (float) getDouble(sender, args[5], "pitch"));
    }

    /**
     * Teleport a player to another player
     *
     * @param messenger     the handler to send messages to
     * @param player        the player being teleported
     * @param target        the target of the teleportation
     * @param consoleSender whether the command is being sent by console
     */
    private void teleport(CommandSender messenger, CPlayer player, CPlayer target, boolean consoleSender) {
        if (player == null) {
            messenger.sendMessage(ChatColor.RED + "First player not found!");
            messenger.sendMessage(ChatColor.RED + "/tp " + ChatColor.BOLD + "[player] " + ChatColor.RED +
                    (consoleSender ? "[target]" : "<target>"));
            return;
        }
        if (target == null) {
            messenger.sendMessage(ChatColor.RED + "Second player not found!");
            messenger.sendMessage(ChatColor.RED + "/tp [player] " + ChatColor.BOLD +
                    (consoleSender ? "[target]" : "<target>"));
            return;
        }
        if (target.isInVehicle()) {
            messenger.sendMessage(ChatColor.RED + "Can't teleport to " + target.getName() + ", they're on a ride!");
            return;
        }
        teleport(null, player, target.getLocation());
        messenger.sendMessage(ChatColor.GRAY + "Teleported " +
                ((messenger instanceof Player) && ((Player) messenger).getUniqueId().equals(player.getUniqueId()) ? "you" : player.getName())
                + " to " + target.getName() + "!");
    }

    /**
     * Teleport a player to a location
     *
     * @param messenger the handler to send messages to
     * @param player    the player being teleported
     * @param loc       the location
     * @implNote if no messages should be sent, pass in 'null' for messenger
     */
    private void teleport(CommandSender messenger, CPlayer player, Location loc) {
        if (loc == null) return;
        ParkManager.getTeleportUtil().log(player);
        player.teleport(loc);
        MathUtil.round(loc, 4);
        if (messenger != null) {
            messenger.sendMessage(ChatColor.GRAY + "Teleported " +
                    ((messenger instanceof Player) && ((Player) messenger).getUniqueId().equals(player.getUniqueId()) ? "you" : player.getName())
                    + " to [" + loc.getX() + "," + loc.getY() + "," + loc.getZ()
                    + " | " + loc.getYaw() + "," + loc.getPitch() + "]");
        }
    }

    private double getDouble(CommandSender sender, String s, String arg) throws NumberFormatException {
        try {
            if (s.startsWith("~")) {
                Location loc = null;
                if (sender instanceof Player) {
                    loc = ((Player) sender).getLocation();
                } else if (sender instanceof BlockCommandSender) {
                    loc = ((BlockCommandSender) sender).getBlock().getLocation().add(0.5, 0, 0.5);
                }
                if (loc != null) {
                    double value;
                    switch (arg) {
                        case "x":
                            value = loc.getX();
                            break;
                        case "y":
                            value = loc.getY();
                            break;
                        case "z":
                            value = loc.getZ();
                            break;
                        case "yaw":
                            value = loc.getYaw();
                            break;
                        case "pitch":
                            value = loc.getPitch();
                            break;
                        default:
                            return 0;
                    }
                    if (s.length() > 1) {
                        try {
                            double addition = Double.parseDouble(s.substring(1));
                            return value + addition;
                        } catch (NumberFormatException ignored) {
                        }
                    } else {
                        return value;
                    }
                }
            }
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(ChatColor.RED + "Couldn't parse [" + arg + "] number: " + s);
        }
    }
}