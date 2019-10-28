package network.palace.parkmanager.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.BlockPosition;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.magicband.MenuType;
import network.palace.parkmanager.utils.InventoryUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryListener implements Listener {

    public InventoryListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(ParkManager.getInstance(),
                PacketType.Play.Server.BLOCK_ACTION) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                StructureModifier<BlockPosition> modifier = packet.getBlockPositionModifier();
                List<BlockPosition> list = modifier.getValues();
                for (BlockPosition pos : list) {
                    if (event.getPlayer().getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()).getType()
                            .equals(Material.ENDER_CHEST)) {
                        event.setCancelled(true);
                    }
                }
            }
        });
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getWhoClicked().getUniqueId());
        if (player == null) return;
        InventoryUtil.InventoryState state = ParkManager.getInventoryUtil().getInventoryState(player);
        if (state.equals(InventoryUtil.InventoryState.BUILD) ||
                (event.getClickedInventory() != null && !event.getClickedInventory().equals(player.getInventory())))
            //if player is in build mode or they're clicking on an inventory other than their personal inventory, skip
            return;
        if (state.equals(InventoryUtil.InventoryState.RIDE) ||
                event.getSlotType().equals(InventoryType.SlotType.ARMOR) ||
                InventoryUtil.isReservedSlot(event.getSlot()) ||
                (event.getClick().equals(ClickType.NUMBER_KEY) && InventoryUtil.isReservedSlot(event.getHotbarButton()))) {
            //if player is in ride mode, or is clicking on reserved/armor slots, or is swapping items to reserved slots, cancel
            event.setCancelled(true);
        }
        //otherwise (player is in guest mode, clicking on personal inventory slots not reserved), continue
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (player == null) {
            event.setCancelled(true);
            return;
        }
        Inventory inv = event.getInventory();
        if (inv.getType().equals(org.bukkit.event.inventory.InventoryType.ENDER_CHEST)) {
            event.setCancelled(true);
            ParkManager.getInventoryUtil().openMenu(player, MenuType.LOCKER);
        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        event.setCancelled(true);
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null || player.getRank().getRankId() < Rank.TRAINEEBUILD.getRankId()) return;
        if (ParkManager.getBuildUtil().isInBuildMode(player)) {
            ItemStack toMainhand = event.getMainHandItem();
            ItemStack toOffhand = event.getOffHandItem();
            if (toMainhand != null && toMainhand.getType().equals(Material.MAP)) {
                if (toOffhand != null && !toOffhand.getType().equals(Material.AIR)) {
                    player.sendMessage(ChatColor.RED + "You can only swap hands if your main hand is empty!");
                } else {
                    event.setCancelled(false);
                }
                return;
            }
            if (toOffhand != null && toOffhand.getType().equals(Material.MAP)) {
                event.setCancelled(false);
                return;
            }
        }
        player.performCommand("build");
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null || ParkManager.getBuildUtil().isInBuildMode(player)) return;
        if (event.getNewSlot() == 6) {
            ParkManager.getTimeUtil().selectWatch(player);
        } else if (event.getPreviousSlot() == 6) {
            ParkManager.getTimeUtil().unselectWatch(player);
        }
    }
}