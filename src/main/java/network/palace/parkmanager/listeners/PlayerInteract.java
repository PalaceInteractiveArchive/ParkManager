package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.magicband.MenuType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteract implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null) {
            event.setCancelled(true);
            return;
        }
        ItemStack hand = event.getItem();
        if (hand == null || hand.getType() == null) return;
        int slot = player.getHeldItemSlot();
        if (ParkManager.getBuildUtil().isInBuildMode(player)) {
            return;
        }
        boolean cancel = false;
        switch (slot) {
            case 5:
                //open watch menu
                break;
            case 6:
                cancel = true;
                ParkManager.getInventoryUtil().openMenu(player, MenuType.BACKPACK);
                break;
            case 7:
                //autograph book
                break;
            case 8:
                //open magicband
                break;
        }
        if (cancel) event.setCancelled(true);
    }
}
