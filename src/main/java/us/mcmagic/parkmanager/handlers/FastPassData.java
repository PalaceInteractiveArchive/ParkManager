package us.mcmagic.parkmanager.handlers;

/**
 * Created by Marc on 2/19/16
 */
public class FastPassData {
    private int slow;
    private int moderate;
    private int thrill;
    private int sday;
    private int mday;
    private int tday;

    public FastPassData(int slow, int moderate, int thrill, int sday, int mday, int tday) {
        this.slow = slow;
        this.moderate = moderate;
        this.thrill = thrill;
        this.sday = sday;
        this.mday = mday;
        this.tday = tday;
    }

    public int getSlow() {
        return slow;
    }

    public int getModerate() {
        return moderate;
    }

    public int getThrill() {
        return thrill;
    }

    public void setSlow(int slow) {
        this.slow = slow;
    }

    public void setModerate(int moderate) {
        this.moderate = moderate;
    }

    public void setThrill(int thrill) {
        this.thrill = thrill;
    }

    public int getSlowDay() {
        return sday;
    }

    public int getModerateDay() {
        return mday;
    }

    public int getThrillDay() {
        return tday;
    }

    public void setSlowDay(int sday) {
        this.sday = sday;
    }

    public void setModerateDay(int mday) {
        this.mday = mday;
    }

    public void setThrillDay(int tday) {
        this.tday = tday;
    }

    public int getPass(RideCategory category) {
        switch (category) {
            case SLOW:
                return getSlow();
            case MODERATE:
                return getModerate();
            case THRILL:
                return getThrill();
        }
        return 0;
    }

    public void setPass(RideCategory category, int count) {
        switch (category) {
            case SLOW:
                setSlow(count);
                return;
            case MODERATE:
                setModerate(count);
                return;
            case THRILL:
                setThrill(count);
        }
    }
}