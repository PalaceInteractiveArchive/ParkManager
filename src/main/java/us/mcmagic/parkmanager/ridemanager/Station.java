package us.mcmagic.parkmanager.ridemanager;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;

/**
 * Created by Marc on 4/6/15
 */
public class Station {
    private long start;
    private double length;
    private double launchPower;

    public Station(Sign s) {
        start = System.currentTimeMillis();
        try {
            this.length = Double.parseDouble(s.getLine(2));
        } catch (NumberFormatException nfe) {
            length = 5;
            s.setLine(2, ChatColor.RED + "Number Error");
            s.update();
        }
        try {
            String[] list = s.getLine(1).split(" ");
            if (list.length == 1) {
                launchPower = 0.5;
                return;
            }
            this.launchPower = Double.parseDouble(list[1]);
        } catch (NumberFormatException nfe) {
            launchPower = 0.5;
            s.setLine(1, ChatColor.RED + "Number Error");
            s.update();
        }
    }

    public Station(double length, double launchPower) {
        start = System.currentTimeMillis();
        this.length = length;
        this.launchPower = launchPower;
    }

    public double getLength() {
        return length;
    }

    public double getLaunchPower() {
        return launchPower;
    }

    public void handleMove(CartMoveEvent event) {
        if (start + (length * 1000) < (System.currentTimeMillis() + (length * 1000))) {
            event.setCancelled(true);
        }
    }
}
