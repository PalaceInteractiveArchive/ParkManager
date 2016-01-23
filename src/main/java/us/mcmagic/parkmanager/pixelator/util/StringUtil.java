package us.mcmagic.parkmanager.pixelator.util;

import us.mcmagic.parkmanager.pixelator.renderer.types.MapImageRenderer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

@SuppressWarnings("unchecked")
public abstract class StringUtil {

    private static final Random RANDOM = new Random();
    private static final String[] COLOR_CODE_MODIFIERS = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e"};
    private static final Map EQUAL_COLOR_CODES = new HashMap();


    static {
        EQUAL_COLOR_CODES.put("§1", "§9");
        EQUAL_COLOR_CODES.put("§2", "§a");
        EQUAL_COLOR_CODES.put("§3", "§b");
        EQUAL_COLOR_CODES.put("§4", "§c");
        EQUAL_COLOR_CODES.put("§5", "§d");
        EQUAL_COLOR_CODES.put("§6", "§e");
        EQUAL_COLOR_CODES.put("§7", "§8");
    }

    public static String randomColorCode() {
        return "§" + COLOR_CODE_MODIFIERS[RANDOM.nextInt(COLOR_CODE_MODIFIERS.length)];
    }

    public static String equalColorCode(String c) {

        for (Object o : EQUAL_COLOR_CODES.entrySet()) {
            Entry e = (Entry) o;
            String k = (String) e.getKey();
            String v = (String) e.getValue();
            if (k.equals(c)) {
                return v;
            }

            if (v.equals(c)) {
                return k;
            }
        }

        throw new IllegalArgumentException("Invalid color code");
    }

    public static String toString(List list) {
        StringBuilder s = new StringBuilder();

        for (Object aList : list) {
            MapImageRenderer m = (MapImageRenderer) aList;
            String c = randomColorCode();
            s.append("\n§r ").append(c).append("▻ ").append(equalColorCode(c)).append(m.getId()).append(" §f◈ §7Source: §e")
                    .append(m.getImageSource()).append(" §f◈ §8Source type: §6").append(m.getImageSourceType().getName());
        }

        return s.toString();
    }
}
