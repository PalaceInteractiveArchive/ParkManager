package network.palace.parkmanager.dashboard.packets.parks;

import com.google.gson.JsonObject;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.parkmanager.dashboard.packets.PacketID;

public class PacketImAPark extends BasePacket {

    public PacketImAPark() {
        super(PacketID.Park.IMAPARK.getID());
    }

    public PacketImAPark fromJSON(JsonObject obj) {
        this.id = obj.get("id").getAsInt();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", this.id);
        return obj;
    }
}