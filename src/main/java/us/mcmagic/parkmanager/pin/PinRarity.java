package us.mcmagic.parkmanager.pin;

import org.bukkit.ChatColor;

/**
 * Created by Marc on 2/19/16
 */
public enum PinRarity {
    COMMON(ChatColor.GREEN, 0, 0), ORDINARY(ChatColor.BLUE, 0, 0), RARE(ChatColor.YELLOW, 0, 0), LIMITED_EDITION(ChatColor.LIGHT_PURPLE, 0, 0);

    private ChatColor color;
    private double rarity;
    private double amount;

    PinRarity(ChatColor color, int rarity, double amount) {
        this.color = color;
        this.rarity = rarity;
        this.amount = amount;
    }

    public ChatColor getColor() {
        return color;
    }

    public double getRarity() {
        return rarity;
    }

    public void setRarity(double rarity) {
        this.rarity = rarity;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return color + capFirst(name());
    }

    private String capFirst(String name) {
        if (name.contains("_")) {
            return "Limited Edition";
        }
        char[] array = name.toCharArray();
        if (array.length == 0) {
            return "";
        }
        array[0] = Character.toUpperCase(name.charAt(0));
        return String.valueOf(array);
    }

    public static PinRarity fromString(String s) {
        switch (s.toLowerCase()) {
            case "common":
                return COMMON;
            case "ordinary":
                return ORDINARY;
            case "rare":
                return RARE;
            case "limited":
            case "limitededition":
            case "limited_edition":
                return LIMITED_EDITION;
        }
        return COMMON;
    }
}