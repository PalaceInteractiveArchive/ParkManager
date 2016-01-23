package us.mcmagic.parkmanager.ridemanager;

/**
 * Created by Marc on 4/6/15
 */
public enum SignType {
    SPAWN("spawn"), SPEED("speed"), DESTROY("destroy"), STATION("station");

    String line;

    SignType(String line) {
        this.line = line;
    }

    public String getLine() {
        return line;
    }
}
