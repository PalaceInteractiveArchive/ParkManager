package network.palace.parkmanager.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.BlockPosition;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.InventoryType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * Created by Marc on 10/12/15
 */
public class InventoryOpen implements Listener {

    public InventoryOpen() {
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
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inv = event.getInventory();
        if (inv.getType().equals(org.bukkit.event.inventory.InventoryType.ENDER_CHEST)) {
            event.setCancelled(true);
            ParkManager.getInstance().getInventoryUtil().openInventory(player, InventoryType.LOCKER);
        }
    }
}