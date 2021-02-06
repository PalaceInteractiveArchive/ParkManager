package network.palace.parkmanager.utils;

import lombok.Getter;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerUtil {
    private final HashMap<UUID, Document> loginData = new HashMap<>();
    @Getter private final HashMap<UUID, String> userCache = new HashMap<>();

    public Document getLoginData(UUID uuid) {
        return loginData.get(uuid);
    }

    public void addLoginData(UUID uuid, Document document, List<UUID> friends) {
        document.put("friends", friends);
        loginData.put(uuid, document);
    }

    public Document removeLoginData(UUID uuid) {
        return loginData.remove(uuid);
    }

    public void addToUserCache(UUID uuid, String name) {
        userCache.put(uuid, name);
    }
}
