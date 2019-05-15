package network.palace.parkmanager.utils;

import org.bson.Document;

import java.util.HashMap;
import java.util.UUID;

public class PlayerUtil {
    private HashMap<UUID, Document> loginData = new HashMap<>();

    public Document getLoginData(UUID uuid) {
        return loginData.get(uuid);
    }

    public void addLoginData(UUID uuid, Document document) {
        loginData.put(uuid, document);
    }

    public Document removeLoginData(UUID uuid) {
        return loginData.remove(uuid);
    }
}
