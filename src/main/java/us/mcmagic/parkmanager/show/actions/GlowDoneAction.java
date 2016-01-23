package us.mcmagic.parkmanager.show.actions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.PlayerData;
import us.mcmagic.parkmanager.show.Show;

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
            PlayerData data = ParkManager.getPlayerData(tp.getUniqueId());
            PlayerData.Clothing c = data.getClothing();
            tp.getInventory().setHelmet(c.getHead() == null ? new ItemStack(Material.AIR) : c.getHead());
        }
    }
}