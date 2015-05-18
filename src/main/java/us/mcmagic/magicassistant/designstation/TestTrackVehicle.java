package us.mcmagic.magicassistant.designstation;

import net.md_5.bungee.api.ChatColor;

/**
 * Created by LukeSmalley on 5/18/2015.
 */
public class TestTrackVehicle {

    public static int carType = 0;
    public static int truckType = 1;
    public static int ecoCarType = 2;

    public int type = 0;
    public int width = 0;
    public int height = 0;
    public ChatColor color = ChatColor.WHITE;
    public EngineType engineType = EngineType.GASOLINE;

    public enum EngineType {
        SOLAR, FUELCELL, ELECTRIC, HYBRID, GASOLINE, SUPERCHARGED, PLASMA
    }

    public int getPowerScore() {
        return 0;
    }

    public int getResponsivenessScore() {
        return 0;
    }

    public int getCapabilityScore() {
        return 0;
    }

    public int getEfficiencyScore() {
        return 0;
    }

}
