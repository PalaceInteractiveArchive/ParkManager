package network.palace.parkmanager.message;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.messagequeue.packets.MQPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UpdateQueuePacket extends MQPacket {
    @Getter private final String queueId;
    @Getter private final boolean open;
    @Getter private final List<UUID> queue;

    public UpdateQueuePacket(JsonObject object) {
        super(PacketID.Global.UPDATE_QUEUE.getId(), object);
        this.queueId = object.get("queueId").getAsString();
        this.open = object.get("open").getAsBoolean();
        if (object.has("queue")) {
            queue = new ArrayList<>();
            JsonArray queueJSON = object.get("queue").getAsJsonArray();
            for (JsonElement e : queueJSON) {
                try {
                    queue.add(UUID.fromString(e.getAsString()));
                } catch (Exception ignored) {
                }
            }
        } else {
            queue = null;
        }
    }

    public UpdateQueuePacket(String queueId, boolean open, List<UUID> queue) {
        super(PacketID.Global.UPDATE_QUEUE.getId(), null);
        this.queueId = queueId;
        this.open = open;
        this.queue = queue;
    }

    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("queueId", queueId);
        object.addProperty("open", this.open);
        if (queue != null) {
            try {
                Gson gson = new Gson();
                List<String> list = new ArrayList<>();
                for (UUID uuid : queue) {
                    list.add(uuid.toString());
                }
                object.add("queue", gson.toJsonTree(list).getAsJsonArray());
            } catch (Exception e) {
                return null;
            }
        }
        return object;
    }
}
