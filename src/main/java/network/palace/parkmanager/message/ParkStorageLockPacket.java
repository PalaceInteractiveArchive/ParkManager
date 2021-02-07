package network.palace.parkmanager.message;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.messagequeue.packets.MQPacket;

import java.util.UUID;

public class ParkStorageLockPacket extends MQPacket {
    @Getter private final UUID uuid;
    @Getter private final String serverName;
    @Getter private final boolean storageLock;

    public ParkStorageLockPacket(JsonObject object) {
        super(PacketID.Global.PARK_STORAGE_LOCK.getId(), object);
        this.uuid = UUID.fromString(object.get("uuid").getAsString());
        this.serverName = object.get("serverName").getAsString();
        this.storageLock = object.get("storageLock").getAsBoolean();
    }

    public ParkStorageLockPacket(UUID uuid, String serverName, boolean storageLock) {
        super(PacketID.Global.PARK_STORAGE_LOCK.getId(), null);
        this.uuid = uuid;
        this.serverName = serverName;
        this.storageLock = storageLock;
    }

    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("uuid", uuid.toString());
        object.addProperty("serverName", serverName);
        object.addProperty("storageLock", storageLock);
        return object;
    }
}