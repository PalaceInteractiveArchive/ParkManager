package us.mcmagic.parkmanager.dashboard;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.dashboard.events.IncomingPacketEvent;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.dashboard.packets.parks.PacketInventoryStatus;
import us.mcmagic.parkmanager.dashboard.packets.parks.PacketRefreshHotels;
import us.mcmagic.parkmanager.dashboard.packets.parks.PacketRefreshWarps;
import us.mcmagic.parkmanager.utils.WarpUtil;

import java.util.UUID;

/**
 * Created by Marc on 9/18/16
 */
public class PacketListener implements Listener {

    @EventHandler
    public void onIncomingPacket(IncomingPacketEvent event) {
        String data = event.getPacket();
        JsonObject object = (JsonObject) new JsonParser().parse(data);
        if (!object.has("id")) {
            return;
        }
        int id = object.get("id").getAsInt();
        switch (id) {
            /**
             * Cross-server Inventory
             */
            case 58: {
                PacketInventoryStatus packet = new PacketInventoryStatus().fromJSON(object);
                UUID uuid = packet.getUniqueId();
                int status = packet.getStatus();
                if (status != 1) {
                    return;
                }
                Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), () -> ParkManager.storageManager.downloadInventory(uuid, false));
                break;
            }
            /**
             * Refresh Hotel Rooms
             */
            case 59: {
                PacketRefreshHotels packet = new PacketRefreshHotels().fromJSON(object);
                if (packet.getServer().equals(MCMagicCore.getMCMagicConfig().instanceName)) {
                    return;
                }
                ParkManager.hotelManager.refreshRooms();
                break;
            }
            /**
             * Refresh Warps
             */
            case 62: {
                PacketRefreshWarps packet = new PacketRefreshWarps().fromJSON(object);
                if (packet.getServer().equals(MCMagicCore.getMCMagicConfig().instanceName)) {
                    return;
                }
                WarpUtil.refreshWarps();
                break;
            }
        }
    }
}