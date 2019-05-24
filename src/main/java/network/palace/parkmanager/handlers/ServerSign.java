package network.palace.parkmanager.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.block.Sign;

@Getter
@AllArgsConstructor
public enum ServerSign {
    DISPOSAL("[Disposal]"), RIDE_LEADERBOARD("[Leaderboard]"), SERVER("[Server]"), WARP("[Warp]");

    private String signHeader;

    public static ServerSign fromSign(Sign s) {
        String line1 = s.getLine(0);
        for (ServerSign sign : values()) {
            if (line1.contains(sign.getSignHeader())) {
                return sign;
            }
        }
        return null;
    }
}
