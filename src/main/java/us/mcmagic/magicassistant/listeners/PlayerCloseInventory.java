package us.mcmagic.magicassistant.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import us.mcmagic.magicassistant.MagicAssistant;

/**
 * Created by Marc on 3/20/15
 */
public class PlayerCloseInventory implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getTitle().contains("'s MagicBand")) {
            MagicAssistant.getInstance().bandUtil.cancelLoadPlayerData(event.getPlayer().getUniqueId());
        }
    }
}
