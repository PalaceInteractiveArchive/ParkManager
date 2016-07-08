package us.mcmagic.parkmanager.handlers;

/**
 * Created by Marc on 7/8/16
 */
public class RideCount {
    private String name;
    private String server;
    private int count = 1;

    public RideCount(String name, String server) {
        this.name = name;
        this.server = server;
    }

    public String getName() {
        return name;
    }

    public String getServer() {
        return server;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void addCount(int i) {
        this.count += i;
    }
}