package us.mcmagic.parkmanager.designstation;

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
        SOLAR, FUELCELL, ELECTRIC, HYBRID, GASOLINE, SUPERCHARGED, PLASMA;

        public static int getEfficiency(EngineType t) {
            switch (t) {
                case SOLAR:
                    return 30;
                case FUELCELL:
                    return 20;
                case ELECTRIC:
                    return 10;
                case HYBRID:
                    return 0;
                case GASOLINE:
                    return -5;
                case SUPERCHARGED:
                    return -10;
                case PLASMA:
                    return -20;
                default:
                    return 0;
            }
        }

        public static int getPower(EngineType t) {
            switch (t) {
                case SOLAR:
                    return -15;
                case FUELCELL:
                    return -10;
                case ELECTRIC:
                    return -5;
                case HYBRID:
                    return 0;
                case GASOLINE:
                    return 10;
                case SUPERCHARGED:
                    return 20;
                case PLASMA:
                    return 30;
                default:
                    return 0;
            }
        }
    }

    public int getWidthOffset() {
        if (type == carType) {
            return 7;
        } else if (type == truckType) {
            return 6;
        } else if (type == ecoCarType) {
            return 4;
        }
        return 0;
    }

    //Score baseline is 50pts
    //Cars get +Power and +Responsiveness                (baseline: P60 R60 C50 E50)
    //Trucks get +Power, +Capability, and -Efficiency    (baseline: P60 R50 C60 E40)
    //Smartcars get +Efficiency and -Power               (baseline: P40 R50 C50 E60)

    public int getPowerScore() {
        if (type == carType) {
            int score = 60 + EngineType.getPower(engineType);
            int widthReduction = getWidthRatioAmount(score);
            int heightReduction = getHeightRatioAmount(score);
            return score - widthReduction - heightReduction;
        } else if (type == truckType) {
            int score = 60 + EngineType.getPower(engineType);
            int widthReduction = getWidthRatioAmount(score);
            int heightReduction = getHeightRatioAmount(score);
            return score - widthReduction - heightReduction;
        } else if (type == ecoCarType) {
            int score = 40 + EngineType.getPower(engineType);
            int widthReduction = getWidthRatioAmount(score);
            int heightReduction = getHeightRatioAmount(score);
            return score - widthReduction - heightReduction;
        }

        return -1;
    }

    public int getResponsivenessScore() {
        if (type == carType) {
            return 60 - getWidthRatioAmount(60);
        } else if (type == truckType) {
            return 50 - getWidthRatioAmount(50);
        } else if (type == ecoCarType) {
            return 50 - getWidthRatioAmount(50);
        }

        return -1;
    }

    public int getCapabilityScore() {
        if (type == carType) {
            return 50 - getHeightRatioAmount(50);
        } else if (type == truckType) {
            return 60 - getHeightRatioAmount(60);
        } else if (type == ecoCarType) {
            return 50 - getHeightRatioAmount(50);
        }

        return -1;
    }

    public int getEfficiencyScore() {
        if (type == carType) {
            int score = 50 + EngineType.getEfficiency(engineType);
            int widthAddition = getWidthRatioAmount(100 - score);
            int heightReduction = getHeightRatioAmount(score);
            return (score + widthAddition) - heightReduction;
        } else if (type == truckType) {
            int score = 40 + EngineType.getEfficiency(engineType);
            int widthAddition = getWidthRatioAmount(100 - score);
            int heightReduction = getHeightRatioAmount(score);
            return (score + widthAddition) - heightReduction;
        } else if (type == ecoCarType) {
            int score = 60 + EngineType.getEfficiency(engineType);
            int widthAddition = getWidthRatioAmount(100 - score);
            int heightReduction = getHeightRatioAmount(score);
            return (score + widthAddition) - heightReduction;
        }

        return -1;
    }

    private int getWidthRatioAmount(int relativeAmount) {
        int scale = getWidthOffset() + 9;
        return ((int) (((double) width / (double) scale) * (double) relativeAmount));
    }

    private int getHeightRatioAmount(int relativeAmount) {
        return ((int) (((double) height / 11) * (double) relativeAmount));
    }

}
