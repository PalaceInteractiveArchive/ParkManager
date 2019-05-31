package network.palace.parkmanager.handlers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class RideCount {
    private final String name;
    private final String server;
    @Setter private int count = 1;

    public void addCount(int i) {
        this.count += i;
    }

    public boolean serverEquals(String s) {
        if (server.equalsIgnoreCase(s)) return true;
        return s.replaceAll("[^A-Za-z ]", "").equalsIgnoreCase(s);
    }
}