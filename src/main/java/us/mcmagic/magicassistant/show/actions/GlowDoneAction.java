package us.mcmagic.magicassistant.show.actions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.PlayerData;
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
            PlayerData data = MagicAssistant.getPlayerData(tp.getUniqueId());
            PlayerData.Clothing c = data.getClothing();
            tp.getInventory().setHelmet(c.getHead() == null ? new ItemStack(Material.AIR) : c.getHead());
        }
    }
}