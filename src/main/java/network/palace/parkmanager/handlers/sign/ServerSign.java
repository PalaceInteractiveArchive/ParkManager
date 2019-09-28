package network.palace.parkmanager.handlers.sign;

import lombok.Getter;
import lombok.Setter;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class ServerSign {
    private static List<SignEntry> entries = new ArrayList<>();

    public static void registerSign(String header, SignHandler handler) {
        entries.add(new SignEntry(header, handler));
    }

    public static SignEntry getByHeader(String s) {
        for (SignEntry entry : entries) {
            if (entry.getHeader().equalsIgnoreCase(ChatColor.stripColor(s))) {
                return entry;
            }
        }
        return null;
    }

    @Getter
    public static class SignEntry {
        private String header;
        private SignHandler handler;

        public SignEntry(String header, SignHandler handler) {
            this.header = header;
            this.handler = handler;
            this.handler.setSignEntry(this);
        }
    }

    @Getter
    @Setter
    public abstract static class SignHandler {
        private SignEntry signEntry;

        public void onSignChange(CPlayer player, SignChangeEvent event) {
        }

        public void onInteract(CPlayer player, Sign s, PlayerInteractEvent event) {
        }

        public void onBreak(CPlayer player, Sign s, BlockBreakEvent event) {
        }
    }
}

/*@Getter
@AllArgsConstructor
public enum ServerSign {
    DISPOSAL("[Disposal]"), RIDE_LEADERBOARD("[Leaderboard]"),
    SERVER("[Server]"), WARP("[Warp]"), QUEUE("[Queue]"),
    SHOP("[Shop]");

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
*/