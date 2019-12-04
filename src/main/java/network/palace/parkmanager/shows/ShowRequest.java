package network.palace.parkmanager.shows;

import lombok.Getter;
import network.palace.core.player.CPlayer;

import java.util.UUID;

@Getter
public class ShowRequest {
    private UUID requestId;
    private UUID uuid;
    private ShowEntry show;
    private boolean canBeApproved = false;

    public ShowRequest(UUID requestId, UUID uuid, ShowEntry show) {
        this.requestId = requestId;
        this.uuid = uuid;
        this.show = show;
    }

    public void setCanBeApproved(boolean canBeApproved) {
        this.canBeApproved = canBeApproved;
    }
}
