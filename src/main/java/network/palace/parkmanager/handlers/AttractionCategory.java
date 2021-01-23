package network.palace.parkmanager.handlers;

public enum AttractionCategory {
    BIG_DROPS, SMALL_DROPS, SLOW_RIDE, THRILL_RIDE, WATER_RIDE, SPINNING, DARK, LOUD,
    SCARY, CLASSIC, ANIMAL_ENCOUNTERS, INDOOR, INTERACTIVE, STAGE_SHOW, AUDIO_SERVER, PHOTOPASS;

    public String getShortName() {
        return name().toLowerCase().replaceAll("_", "");
    }

    public String getFormattedName() {
        String[] words = name().toLowerCase().split("_");
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            formatted.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1));
            if (i < (words.length - 1)) {
                formatted.append(" ");
            }
        }
        return formatted.toString();
    }

    public static AttractionCategory fromString(String s) {
        for (AttractionCategory category : values()) {
            if (category.getShortName().equalsIgnoreCase(s)) {
                return category;
            }
        }
        return null;
    }
}
