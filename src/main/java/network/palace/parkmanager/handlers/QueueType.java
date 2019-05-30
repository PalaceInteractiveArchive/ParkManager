package network.palace.parkmanager.handlers;

public enum QueueType {
    BLOCK;

    public static QueueType fromString(String s) {
        for (QueueType type : values()) {
            if (type.name().equalsIgnoreCase(s)) {
                return type;
            }
        }
        return null;
    }
}
