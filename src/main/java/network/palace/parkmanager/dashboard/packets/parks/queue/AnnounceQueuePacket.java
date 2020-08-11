package network.palace.parkmanager.dashboard.packets.parks.queue;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.parkmanager.dashboard.packets.PacketID;

@Getter
public class AnnounceQueuePacket extends BasePacket {
    private String queueId, announcement;

    public AnnounceQueuePacket() {
        this("", "");
    }

    public AnnounceQueuePacket(String queueId, String announcement) {
        super(PacketID.Park.ANNOUNCE_QUEUE.getID());
        this.queueId = queueId;
        this.announcement = announcement;
    }

    public AnnounceQueuePacket fromJSON(JsonObject obj) {
        this.id = obj.get("id").getAsInt();
        this.queueId = obj.get("queueId").getAsString();
        this.announcement = obj.get("announcement").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", this.id);
        obj.addProperty("queueId", this.queueId);
        obj.addProperty("announcement", this.announcement);
        return obj;
    }
}
