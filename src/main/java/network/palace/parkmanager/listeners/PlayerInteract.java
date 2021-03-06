package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.magicband.MenuType;
import network.palace.parkmanager.handlers.sign.ServerSign;
import network.palace.parkmanager.magicband.BandInventory;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
        Action action = event.getAction();

        //Check sign clicks
        if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
            Block b = event.getClickedBlock();
            if (b.getType().equals(Material.SIGN) || b.getType().equals(Material.WALL_SIGN) || b.getType().equals(Material.SIGN_POST)) {
                Sign s = (Sign) b.getState();
                ServerSign.SignEntry signEntry = ServerSign.getByHeader(s.getLine(0));
                if (signEntry != null) {
                    signEntry.getHandler().onInteract(player, s, event);
                }
                return;
            }
        }

        if (player.getRank().getRankId() < Rank.TRAINEEBUILD.getRankId() && (action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_BLOCK))) {
            Block clicked = event.getClickedBlock();
            if (clicked != null) {
                switch (clicked.getType()) {
                    case FLOWER_POT:
                    case ANVIL:
                    case ENCHANTMENT_TABLE:
                    case WORKBENCH:
                    case FURNACE:
                    case BURNING_FURNACE:
                        event.setCancelled(true);
                        return;
                }
            }
        }

        //Handle inventory item click
        if (ParkManager.getBuildUtil().isInBuildMode(player)) {
            return;
        }
        ItemStack hand = event.getItem();
        if (hand == null || hand.getType() == null) return;
        int slot = player.getHeldItemSlot();
        boolean cancel = false;
        switch (slot) {
            case 5:
                cancel = true;
                ParkManager.getInventoryUtil().openMenu(player, MenuType.BACKPACK);
                break;
            case 6:
                //open watch menu
                ParkManager.getMagicBandManager().openInventory(player, BandInventory.TIMETABLE);
                player.giveAchievement(11);
                break;
            case 7:
                //autograph book
                if (!event.getAction().equals(Action.PHYSICAL)) {
                    cancel = true;
                    ParkManager.getAutographManager().handleInteract(player);
                }
                break;
            case 8:
                //open magicband
                ParkManager.getMagicBandManager().openInventory(player, BandInventory.MAIN);
                player.giveAchievement(2);
                break;
        }
        if (cancel) event.setCancelled(true);
    }
}
