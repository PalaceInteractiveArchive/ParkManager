package us.mcmagic.magicassistant.show.actions;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.magicassistant.show.Show;

/**
 * Created by Marc on 9/7/15
 */
public class GlowDoneAction extends ShowAction {
    public GlowDoneAction(Show show, long time) {
        super(show, time);
    }

    @Override
    public void play() {
        for (Player tp : Bukkit.getOnlinePlayers()) {
            ItemStack item = tp.getInventory().getHelmet();
            if (item == null || item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null) {
                continue;
            }
            String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            if (!name.equalsIgnoreCase("Glow With The Show Ears")) {
                continue;
            }
            tp.getInventory().setHelmet(new ItemStack(Material.AIR));
        }
    }
}