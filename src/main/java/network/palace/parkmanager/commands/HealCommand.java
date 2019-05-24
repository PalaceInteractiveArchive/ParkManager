package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;

@CommandMeta(description = "Heal a player", rank = Rank.TRAINEE)
public class HealCommand extends CoreCommand {

    public HealCommand() {
        super("heal");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length > 0) {
            heal(player.getBukkitPlayer(), args[0]);
        } else {
            healPlayers(player);
        }
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length > 0) {
            heal(sender, args[0]);
        } else {
            sender.sendMessage(ChatColor.RED + "/heal [target]");
        }
    }

    private void heal(CommandSender sender, String s) {
        if (s.equals("**")) {
            healPlayers(Core.getPlayerManager().getOnlinePlayers().toArray(new CPlayer[0]));
            sender.sendMessage(ChatColor.GRAY + "Healed all players!");
        } else {
            CPlayer tp = Core.getPlayerManager().getPlayer(s);
            if (tp == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            healPlayers(tp);
            sender.sendMessage(ChatColor.GRAY + "You healed " + tp.getName());
        }
    }

    private void healPlayers(CPlayer... players) {
        Arrays.asList(players).forEach(p -> {
            if (p == null) return;
            p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            p.setFoodLevel(20);
            p.getBukkitPlayer().getMaxFireTicks();
            p.setFireTicks(0);
            for (PotionEffect effect : p.getActivePotionEffects()) {
                p.removePotionEffect(effect.getType());
            }
            p.sendMessage(ChatColor.GRAY + "You have been healed.");
        });
    }
}