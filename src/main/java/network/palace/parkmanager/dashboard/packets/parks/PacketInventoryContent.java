package network.palace.parkmanager.dashboard.packets.parks;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.parkmanager.dashboard.packets.PacketID;
import network.palace.parkmanager.handlers.Resort;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * @author Innectic
 * @since 6/10/2017
 */
public class PacketInventoryContent extends BasePacket {
    @Getter private UUID uuid;

    @Getter private Resort resort;
    @Getter private String backpackJson;

    @Getter private String backpackHash;
    @Getter private int backpackSize;
    @Getter private String lockerJson;

    @Getter private String lockerHash;
    @Getter private int lockerSize;
    @Getter private String hotbarJson;

    @Getter private String hotbarHash;

    @Getter @Setter private ItemStack[] backpack;
    @Getter @Setter private ItemStack[] locker;
    @Getter @Setter private ItemStack[] hotbar;

    public PacketInventoryContent() {
        this(null, Resort.WDW, "", "", 0, "", "", 0, "", "");
    }

    public PacketInventoryContent(UUID uuid, Resort resort, String backpackJson, String backpackHash, int backpackSize,
                                  String lockerJson, String lockerHash, int lockerSize, String hotbarJson, String hotbarHash) {
        super(PacketID.Inventory.INVENTORY_CONTENT.getID());
        this.uuid = uuid;
        this.resort = resort;
        this.backpackJson = backpackJson;
        this.backpackHash = backpackHash;
        this.backpackSize = backpackSize;
        this.lockerJson = lockerJson;
        this.lockerHash = lockerHash;
        this.lockerSize = lockerSize;
        this.hotbarJson = hotbarJson;
        this.hotbarHash = hotbarHash;
    }

    @Override
    public PacketInventoryContent fromJSON(JsonObject obj) {
        this.id = PacketID.Inventory.INVENTORY_CONTENT.getID();
        this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        this.resort = Resort.fromId(obj.get("resort").getAsInt());

        if (obj.get("backpackJson").isJsonNull()) {
            this.backpackJson = "";
        } else {
            this.backpackJson = obj.get("backpackJson").getAsString();
        }
        if (obj.get("backpackHash").isJsonNull()) {
            this.backpackHash = "";
        } else {
            this.backpackHash = obj.get("backpackHash").getAsString();
        }
        if (obj.get("backpackSize").isJsonNull()) {
            this.backpackSize = 0;
        } else {
            this.backpackSize = obj.get("backpackSize").getAsInt();
        }

        if (obj.get("lockerJson").isJsonNull()) {
            this.lockerJson = "";
        } else {
            this.lockerJson = obj.get("lockerJson").getAsString();
        }
        if (obj.get("lockerHash").isJsonNull()) {
            this.lockerHash = "";
        } else {
            this.lockerHash = obj.get("lockerHash").getAsString();
        }
        if (obj.get("lockerSize").isJsonNull()) {
            this.lockerSize = 0;
        } else {
            this.lockerSize = obj.get("lockerSize").getAsInt();
        }

        if (obj.get("hotbarJson").isJsonNull()) {
            this.hotbarJson = "";
        } else {
            this.hotbarJson = obj.get("hotbarJson").getAsString();
        }
        if (obj.get("hotbarHash").isJsonNull()) {
            this.hotbarHash = "";
        } else {
            this.hotbarHash = obj.get("hotbarHash").getAsString();
        }
        return this;
    }

    @Override
    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", this.uuid.toString());
            obj.addProperty("resort", this.resort.getId());
            obj.addProperty("backpackJson", this.backpackJson);
            obj.addProperty("backpackHash", this.backpackHash);
            obj.addProperty("backpackSize", this.backpackSize);
            obj.addProperty("lockerJson", this.lockerJson);
            obj.addProperty("lockerHash", this.lockerHash);
            obj.addProperty("lockerSize", this.lockerSize);
            obj.addProperty("hotbarJson", this.hotbarJson);
            obj.addProperty("hotbarHash", this.hotbarHash);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
