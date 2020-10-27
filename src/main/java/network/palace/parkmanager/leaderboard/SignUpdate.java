package network.palace.parkmanager.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpdate {
    private LeaderboardSign sign;
    private String[] lines;
}
