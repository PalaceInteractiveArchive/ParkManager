package network.palace.parkmanager.handlers;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Marc on 2/20/16
 */
public class KioskData {
    @Getter @Setter private long monthSettler;
    @Getter @Setter private long monthDweller;
    @Getter @Setter private long monthNoble;
    @Getter @Setter private long monthMajestic;
    @Getter @Setter private long monthHonorable;

    public KioskData(long monthSettler, long monthDweller, long monthNoble, long monthMajestic, long monthHonorable) {
        this.monthSettler = monthSettler;
        this.monthDweller = monthDweller;
        this.monthNoble = monthNoble;
        this.monthMajestic = monthMajestic;
        this.monthHonorable = monthHonorable;
    }
}
