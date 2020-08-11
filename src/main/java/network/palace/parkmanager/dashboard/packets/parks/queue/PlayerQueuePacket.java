package network.palace.parkmanager.dashboard.packets.parks.queue;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.parkmanager.dashboard.packets.PacketID;

import java.util.UUID;

@Getter
public class PlayerQueuePacket extends BasePacket {
    private String queueId;
    private UUID uuid;
    private boolean join;

    public PlayerQueuePacket() {
        this("", null, true);
    }

    public PlayerQueuePacket(String queueId, UUID uuid, boolean join) {
        super(PacketID.Park.PLAYER_QUEUE.getID());
        this.queueId = queueId;
        this.uuid = uuid;
        this.join = join;
    }

    public PlayerQueuePacket fromJSON(JsonObject obj) {
        this.id = obj.get("id").getAsInt();
        this.queueId = obj.get("queueId").getAsString();
        this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        this.join = obj.get("join").getAsBoolean();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", this.id);
        obj.addProperty("queueId", this.queueId);
        obj.addProperty("uuid", this.uuid.toString());
        obj.addProperty("join", this.join);
        return obj;
    }
}
