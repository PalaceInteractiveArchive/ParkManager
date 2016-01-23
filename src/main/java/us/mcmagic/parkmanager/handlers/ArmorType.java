package us.mcmagic.parkmanager.handlers;

/**
 * Created by Marc on 11/14/15
 */
public enum ArmorType {
    HELMET, CHESTPLATE, LEGGINGS, BOOTS;

    public static ArmorType fromString(String s) {
        switch (s.toLowerCase()) {
            case "helmet":
                return HELMET;
            case "chestplate":
                return CHESTPLATE;
            case "leggings":
                return LEGGINGS;
            case "boots":
                return BOOTS;
        }
        return null;
    }
}