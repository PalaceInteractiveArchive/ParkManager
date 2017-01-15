package network.palace.parkmanager.handlers;

/**
 * Created by Marc on 2/20/16
 */
public class KioskData {
    private long monthGuest;
    private long monthDVC;
    private long monthShare;

    public KioskData(long monthGuest, long monthDVC, long monthShare) {
        this.monthGuest = monthGuest;
        this.monthDVC = monthDVC;
        this.monthShare = monthShare;
    }

    public long getMonthGuest() {
        return monthGuest;
    }

    public long getMonthDVC() {
        return monthDVC;
    }

    public long getMonthShare() {
        return monthShare;
    }

    public void setMonthGuest(long monthGuest) {
        this.monthGuest = monthGuest;
    }

    public void setMonthDVC(long monthDVC) {
        this.monthDVC = monthDVC;
    }

    public void setMonthShare(long monthShare) {
        this.monthShare = monthShare;
    }
}