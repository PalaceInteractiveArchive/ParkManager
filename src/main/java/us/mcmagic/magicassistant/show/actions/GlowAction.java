package us.mcmagic.magicassistant.show.actions;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import us.mcmagic.magicassistant.show.Show;

import java.util.Arrays;

/**
 * Created by Marc on 9/6/15
 */
public class GlowAction extends ShowAction {
    private int radius;
    private ItemStack helm;
    private Location loc;

    public GlowAction(Show show, long time, Color color, Location loc, int radius) {
        super(show, time);
        helm = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) helm.getItemMeta();
        meta.setColor(color);
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Glow With The Show Ears");
        meta.setLore(Arrays.asList(ChatColor.LIGHT_PURPLE + "This is part of the Show, don't move it!"));
        helm.setItemMeta(meta);
        this.loc = loc;
        this.radius = radius;
    }

    @Override
    public void play() {
        for (Player tp : Bukkit.getOnlinePlayers()) {
            if (tp.getLocation().distance(loc) <= radius) {
                tp.getInventory().setHelmet(helm);
            }
        }
    }
}