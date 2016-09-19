package us.mcmagic.parkmanager.dashboard.packets.parks;

import com.google.gson.JsonObject;
import us.mcmagic.mcmagiccore.dashboard.packets.BasePacket;
import us.mcmagic.parkmanager.dashboard.packets.PacketID;

/**
 * Created by Marc on 9/18/16
 */
public class PacketRefreshHotels extends BasePacket {
    private String server;

    public PacketRefreshHotels() {
        this("");
    }

    public PacketRefreshHotels(String server) {
        this.id = PacketID.Park.REFRESHHOTELS.getID();
        this.server = server;
    }

    public String getServer() {
        return server;
    }

    public PacketRefreshHotels fromJSON(JsonObject obj) {
        this.id = obj.get("id").getAsInt();
        this.server = obj.get("server").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", this.id);
        obj.addProperty("server", this.server);
        return obj;
    }
}