package network.palace.parkmanager.message;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.messagequeue.packets.MQPacket;

public class CreateQueuePacket extends MQPacket {
    @Getter private final String queueId, queueName, server;
    @Getter private final int holdingArea;

    public CreateQueuePacket(JsonObject object) {
        super(PacketID.Global.CREATE_QUEUE.getId(), object);
        this.queueId = object.get("queueId").getAsString();
        this.queueName = object.get("queueName").getAsString();
        this.holdingArea = object.get("holdingArea").getAsInt();
        this.server = object.get("server").getAsString();
    }

    public CreateQueuePacket(String queueId, String queueName, int holdingArea, String server) {
        super(PacketID.Global.CREATE_QUEUE.getId(), null);
        this.queueId = queueId;
        this.queueName = queueName;
        this.holdingArea = holdingArea;
        this.server = server;
    }

    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("queueId", queueId);
        object.addProperty("queueName", queueName);
        object.addProperty("holdingArea", holdingArea);
        object.addProperty("server", server);
        return object;
    }
}
