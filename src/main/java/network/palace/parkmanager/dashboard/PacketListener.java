package network.palace.parkmanager.dashboard;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import network.palace.core.Core;
import network.palace.core.dashboard.packets.dashboard.PacketUpdateEconomy;
import network.palace.core.events.DashboardConnectEvent;
import network.palace.core.events.IncomingPacketEvent;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.dashboard.packets.parks.PacketImAPark;
import network.palace.parkmanager.dashboard.packets.parks.PacketInventoryContent;
import network.palace.parkmanager.dashboard.packets.parks.PacketRefreshHotels;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
        ParkManager parkManager = ParkManager.getInstance();
        switch (id) {
             /*case 58: {
                PacketInventoryStatus packet = new PacketInventoryStatus().fromJSON(object);
                UUID uuid = packet.getUniqueId();
                int status = packet.getStatus();
                if (status != 1) {
                    return;
                }
                Bukkit.getScheduler().runTaskAsynchronously(parkManager, () -> parkManager.getStorageManager().downloadInventory(uuid, false));
                break;
            }*/
            /**
             * Cross-server Inventory
             */
            case 58: {
                PacketInventoryContent packet = new PacketInventoryContent().fromJSON(object);
                parkManager.getStorageManager().updateInventory(packet);
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
//                parkManager.getHotelManager().refreshRooms();
                break;
            }
            /**
             * Update economy
             * Used for voting
             */
            case 67: {
                PacketUpdateEconomy packet = new PacketUpdateEconomy().fromJSON(object);
                UUID uuid = packet.getUniqueId();
                parkManager.getFpKioskManager().updateVoteData(uuid);
                break;
            }
        }
    }

    @EventHandler
    public void onDashboardConnect(DashboardConnectEvent event) {
        Core.getDashboardConnection().send(new PacketImAPark());
    }
}
