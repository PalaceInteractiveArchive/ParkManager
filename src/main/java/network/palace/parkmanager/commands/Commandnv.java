package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;

/**
 * Created by Marc on 4/11/15
 */
@CommandMeta(description = "Night vision")
@CommandPermission(rank = Rank.SQUIRE)
public class Commandnv extends CoreCommand {

    public Commandnv() {
        super("nv");
    }

    @Override
    protected void handleCommand(CPlayer cp, String[] args) throws CommandException {
        Player player = cp.getBukkitPlayer();
        Collection<PotionEffect> effects = player.getActivePotionEffects();
        boolean contains = false;
        for (PotionEffect e : effects) {
            if (e.getType().equals(PotionEffectType.NIGHT_VISION)) {
                contains = true;
                break;
            }
        }
        if (contains) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.sendMessage(ChatColor.GRAY + "You no longer have Night Vision!");
        } else {
            PotionEffect effect = new PotionEffect(PotionEffectType.NIGHT_VISION, 200000, 0, true, false);
            player.addPotionEffect(effect);
            player.sendMessage(ChatColor.GRAY + "You now have Night Vision!");
        }
    }
}
