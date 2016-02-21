package us.mcmagic.parkmanager.handlers;

/**
 * Created by Marc on 2/20/16
 */
public class KioskData {
    private long lastVote;
    private long monthGuest;
    private long monthDVC;
    private long monthShare;

    public KioskData(long lastVote, long monthGuest, long monthDVC, long monthShare) {
        this.lastVote = lastVote;
        this.monthGuest = monthGuest;
        this.monthDVC = monthDVC;
        this.monthShare = monthShare;
    }

    public long getLastVote() {
        return lastVote;
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

    public void setLastVote(int lastVote) {
        this.lastVote = lastVote;
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