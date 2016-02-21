package us.mcmagic.parkmanager.handlers;

/**
 * Created by Marc on 2/19/16
 */
public enum RideCategory {
    SLOW, MODERATE, THRILL;

    public static RideCategory fromString(String s) {
        switch (s.toLowerCase()) {
            case "slow":
                return SLOW;
            case "moderate":
                return MODERATE;
            case "thrill":
                return THRILL;
        }
        return SLOW;
    }

    public String getSqlName() {
        return name().toLowerCase();
    }
}