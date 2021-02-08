package network.palace.parkmanager.message;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.messagequeue.packets.MQPacket;

public class RemoveQueuePacket extends MQPacket {
    @Getter private final String queueId;

    public RemoveQueuePacket(JsonObject object) {
        super(PacketID.Global.REMOVE_QUEUE.getId(), object);
        this.queueId = object.get("queueId").getAsString();
    }

    public RemoveQueuePacket(String queueId) {
        super(PacketID.Global.REMOVE_QUEUE.getId(), null);
        this.queueId = queueId;
    }

    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("queueId", queueId);
        return object;
    }
}
