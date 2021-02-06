package network.palace.parkmanager.listeners;

import com.google.gson.JsonObject;
import network.palace.core.events.IncomingMessageEvent;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.message.ParkStorageLockPacket;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class PacketListener implements Listener {

    @EventHandler
    public void onIncomingMessage(IncomingMessageEvent event) {
        JsonObject object = event.getPacket();
        if (!object.has("id")) return;
        int id = object.get("id").getAsInt();
        //noinspection SwitchStatementWithTooFewBranches
        switch (id) {
            case 24: {
                ParkStorageLockPacket packet = new ParkStorageLockPacket(object);
                UUID uuid = packet.getUuid();
                ParkManager.getStorageManager().joinLate(uuid);
                break;
            }
        }
    }
}
