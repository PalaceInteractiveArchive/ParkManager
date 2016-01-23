package us.mcmagic.parkmanager.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.InventoryType;

import java.lang.reflect.Field;

/**
 * Created by Marc on 10/12/15
 */
public class InventoryOpen implements Listener {

    public InventoryOpen() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(ParkManager.getInstance(),
                PacketType.Play.Server.BLOCK_ACTION) {
            @Override
            public void onPacketSending(PacketEvent event) {
                try {
                    PacketContainer packet = event.getPacket();
                    StructureModifier<BlockPosition> modifier = packet.getBlockPositionModifier();
                    Field field = modifier.getField(0);
                    field.setAccessible(true);
                    net.minecraft.server.v1_8_R3.BlockPosition pos = (net.minecraft.server.v1_8_R3.BlockPosition)
                            field.get(modifier.getTarget());
                    if (event.getPlayer().getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()).getType()
                            .equals(Material.ENDER_CHEST)) {
                        event.setCancelled(true);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
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
            ParkManager.inventoryUtil.openInventory(player, InventoryType.LOCKER);
        }
    }
}