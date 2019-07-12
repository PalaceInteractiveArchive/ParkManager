package network.palace.parkmanager.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RewardData {
    private long settler;
    private long dweller;
    private long noble;
    private long majestic;
    private long honorable;
}
