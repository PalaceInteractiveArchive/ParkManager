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

@CommandMeta(description = "Set the movement speed of a player", rank = Rank.MOD)
public class SpeedCommand extends CoreCommand {

    public SpeedCommand() {
        super("speed");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/speed [speed] <player>");
            return;
        }
        CPlayer target;
        if (args.length > 1) {
            target = Core.getPlayerManager().getPlayer(args[1]);
        } else {
            target = player;
        }
        setSpeed(player.getBukkitPlayer(), target, args[0]);
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/speed [speed] [player]");
            return;
        }
        setSpeed(sender, Core.getPlayerManager().getPlayer(args[1]), args[0]);
    }

    private void setSpeed(CommandSender sender, CPlayer target, String s) {
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        boolean isFlying = target.getRank().getRankId() >= Rank.SPECIALGUEST.getRankId() && target.isFlying();
        float speed;
        if (isFlying) {
            speed = getMoveSpeed(s);
        } else {
            speed = getMoveSpeed(s);
        }
        target.setFlySpeed(getRealMoveSpeed(speed, isFlying, target.getRank().getRankId() >= Rank.MOD.getRankId()));
        sender.sendMessage(ChatColor.GREEN + "Set " +
                (((sender instanceof Player) && ((Player) sender).getUniqueId().equals(target.getUniqueId())) ? "your" : (target.getName() + "'s"))
                + " " + (isFlying ? "flying" : "walking") + " speed to " + speed);
    }

    private float getMoveSpeed(final String moveSpeed) {
        float userSpeed;
        try {
            userSpeed = Float.parseFloat(moveSpeed);
            if (userSpeed > 10f) {
                userSpeed = 10f;
            } else if (userSpeed < 0.0001f) {
                userSpeed = 0.0001f;
            }
        } catch (NumberFormatException e) {
            return 1;
        }
        return userSpeed;
    }

    private float getRealMoveSpeed(final float userSpeed, final boolean isFly, final boolean isBypass) {
        final float defaultSpeed = isFly ? 0.1f : 0.2f;
        float maxSpeed = 1f;
        if (!isBypass) {
            maxSpeed = (float) 10;
        }

        if (userSpeed < 1f) {
            return defaultSpeed * userSpeed;
        } else {
            float ratio = ((userSpeed - 1) / 9) * (maxSpeed - defaultSpeed);
            return ratio + defaultSpeed;
        }
    }
}
