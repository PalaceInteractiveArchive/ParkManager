package network.palace.parkmanager.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Marc on 2/20/16
 */
@AllArgsConstructor
public class KioskData {
    @Getter @Setter private long monthSettler;
    @Getter @Setter private long monthDweller;
    @Getter @Setter private long monthNoble;
    @Getter @Setter private long monthMajestic;
    @Getter @Setter private long monthHonorable;
    @Getter @Setter private long vote;
    @Getter @Setter private int lastVote;
}
