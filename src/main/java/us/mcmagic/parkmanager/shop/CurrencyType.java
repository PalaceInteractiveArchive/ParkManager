package us.mcmagic.parkmanager.shop;

/**
 * Created by Marc on 11/12/15
 */
public enum CurrencyType {
    MONEY("$"), TOKEN("✪ ");

    private String s;

    CurrencyType(String s) {
        this.s = s;
    }

    public String getIcon() {
        return s;
    }

    public static CurrencyType fromString(String s) {
        switch (s.toLowerCase()) {
            case "money":
                return MONEY;
            case "token":
                return TOKEN;
        }
        return null;
    }
}