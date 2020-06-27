package network.palace.parkmanager.dashboard.packets.parks.queue;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.parkmanager.dashboard.packets.PacketID;

@Getter
public class AdmitQueuePacket extends BasePacket {
    private String queueId;

    public AdmitQueuePacket() {
        this("");
    }

    public AdmitQueuePacket(String queueId) {
        super(PacketID.Park.ADMIT_QUEUE.getID());
        this.queueId = queueId;
    }

    public AdmitQueuePacket fromJSON(JsonObject obj) {
        this.id = obj.get("id").getAsInt();
        this.queueId = obj.get("queueId").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", this.id);
        obj.addProperty("queueId", this.queueId);
        return obj;
    }
}
