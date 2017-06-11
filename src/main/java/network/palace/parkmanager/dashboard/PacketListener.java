package network.palace.parkmanager.dashboard;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import network.palace.core.Core;
import network.palace.core.events.DashboardConnectEvent;
import network.palace.core.events.IncomingPacketEvent;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.dashboard.packets.parks.PacketImAPark;
import network.palace.parkmanager.dashboard.packets.parks.PacketInventoryContent;
import network.palace.parkmanager.dashboard.packets.parks.PacketRefreshHotels;
import network.palace.parkmanager.dashboard.packets.parks.PacketRefreshWarps;
import network.palace.parkmanager.utils.WarpUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
             /*case 58: {
                PacketInventoryStatus packet = new PacketInventoryStatus().fromJSON(object);
                UUID uuid = packet.getUniqueId();
                int status = packet.getStatus();
                if (status != 1) {
                    return;
                }
                Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), () -> ParkManager.storageManager.downloadInventory(uuid, false));
                break;
            }*/
            /**
             * Cross-server Inventory
             */
            case 58: {
                PacketInventoryContent packet = new PacketInventoryContent().fromJSON(object);
                ParkManager.storageManager.updateInventory(packet);
                break;
            }
            /**
             * Refresh Hotel Rooms
             */
            case 59: {
                PacketRefreshHotels packet = new PacketRefreshHotels().fromJSON(object);
                if (packet.getServer().equals(Core.getInstanceName())) {
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
                if (packet.getServer().equals(Core.getInstanceName())) {
                    return;
                }
                WarpUtil.refreshWarps();
                break;
            }
        }
    }

    @EventHandler
    public void onDashboardConnect(DashboardConnectEvent event) {
        Core.getDashboardConnection().send(new PacketImAPark());
    }
}
