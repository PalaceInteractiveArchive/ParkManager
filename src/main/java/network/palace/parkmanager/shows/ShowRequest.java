package network.palace.parkmanager.shows;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ShowRequest {
    private UUID requestId;
    private UUID uuid;
    private ShowEntry show;
    private String command;
    private boolean canBeApproved = false;

    public ShowRequest(UUID requestId, UUID uuid, ShowEntry show) {
        this.requestId = requestId;
        this.uuid = uuid;
        this.show = show;
        this.command = show.getCommand();
    }

    public void setCanBeApproved(boolean canBeApproved) {
        this.canBeApproved = canBeApproved;
    }
}
