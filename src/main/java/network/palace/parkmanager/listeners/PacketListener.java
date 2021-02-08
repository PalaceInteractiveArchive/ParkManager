package network.palace.parkmanager.listeners;

import com.google.gson.JsonObject;
import network.palace.core.events.IncomingMessageEvent;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.message.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class PacketListener implements Listener {

    @EventHandler
    public void onIncomingMessage(IncomingMessageEvent event) {
        JsonObject object = event.getPacket();
        if (!object.has("id")) return;
        int id = object.get("id").getAsInt();
        switch (id) {
            case 24: {
                ParkStorageLockPacket packet = new ParkStorageLockPacket(object);
                UUID uuid = packet.getUuid();
                ParkManager.getStorageManager().joinLate(uuid);
                break;
            }
            case 28: {
                CreateQueuePacket packet = new CreateQueuePacket(object);
                ParkManager.getVirtualQueueManager().handleCreate(packet);
                break;
            }
            case 29: {
                RemoveQueuePacket packet = new RemoveQueuePacket(object);
                ParkManager.getVirtualQueueManager().handleRemove(packet);
                break;
            }
            case 30: {
                UpdateQueuePacket packet = new UpdateQueuePacket(object);
                ParkManager.getVirtualQueueManager().handleUpdate(packet);
                break;
            }
            case 31: {
                PlayerQueuePacket packet = new PlayerQueuePacket(object);
                ParkManager.getVirtualQueueManager().handlePlayer(packet);
                break;
            }
        }
    }
}
