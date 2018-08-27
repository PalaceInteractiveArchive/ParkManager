package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;

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

        boolean isFly;
        float speed;
        if (args.length == 1) {
            isFly = player.isFlying();
            speed = getMoveSpeed(args[0]);
        } else {
            CPlayer tp = Core.getPlayerManager().getPlayer(args[1]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            isFly = tp.getRank().getRankId() >= Rank.SPECIALGUEST.getRankId() && tp.isFlying();
            speed = getMoveSpeed(args[0]);
            if (isFly) {
                tp.getBukkitPlayer().setFlySpeed(getRealMoveSpeed(speed, isFly, player.getRank().getRankId() >= Rank.MOD.getRankId()));
                player.sendMessage(ChatColor.GREEN + "Set " + tp.getName() + "'s flying speed to " + speed);
            } else {
                tp.getBukkitPlayer().setWalkSpeed(getRealMoveSpeed(speed, isFly, player.getRank().getRankId() >= Rank.MOD.getRankId()));
                player.sendMessage(ChatColor.GREEN + "Set " + tp.getName() + "'s walking speed to " + speed);
            }
            return;
        }

        if (isFly) {
            player.getBukkitPlayer().setFlySpeed(getRealMoveSpeed(speed, isFly, player.getRank().getRankId() >= Rank.MOD.getRankId()));
            player.sendMessage(ChatColor.GREEN + "Set your flying speed to " + speed);
        } else {
            player.getBukkitPlayer().setWalkSpeed(getRealMoveSpeed(speed, isFly, player.getRank().getRankId() >= Rank.MOD.getRankId()));
            player.sendMessage(ChatColor.GREEN + "Set your walking speed to " + speed);
        }
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
}
