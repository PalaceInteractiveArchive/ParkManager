package us.mcmagic.parkmanager.pixelator.renderer;

import us.mcmagic.parkmanager.pixelator.renderer.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


@SuppressWarnings("unchecked")
public enum SourceType {

    FILE("FILE", 0, "File"),
    URL("URL", 1, "URL");
    private static final Map NAME_MAP = new HashMap();
    private String name;
    // $FF: synthetic field
    private static final SourceType[] ENUM$VALUES = new SourceType[]{FILE, URL};


    static {
        SourceType[] var3;
        int var2 = (var3 = values()).length;

        for (int var1 = 0; var1 < var2; ++var1) {
            SourceType s = var3[var1];
            NAME_MAP.put(s.name, s);
        }

    }

    SourceType(String var1, int var2, String name) {
        this.name = name;
    }

    public static SourceType fromName(String name) {
        if (name != null) {
            for (Object o : NAME_MAP.entrySet()) {
                Entry e = (Entry) o;
                if (((String) e.getKey()).equalsIgnoreCase(name)) {
                    return (SourceType) e.getValue();
                }
            }
        }

        return null;
    }

    public static SourceType determine(String s) {
        return isValidFile(s) ? FILE : (isValidURL(s) ? URL : null);
    }

    public static boolean isValidFile(File f) {
        return f.exists() && !f.isDirectory();
    }

    public static boolean isValidFile(String file) {
        return isValidFile(new File(file));
    }

    public static boolean isValidURL(URL u) {
        try {
            HttpURLConnection e = (HttpURLConnection) u.openConnection();
            e.setRequestMethod("GET");
            e.connect();
            return e.getResponseCode() == 200;
        } catch (Exception var2) {
            return false;
        }
    }

    public static boolean isValidURL(String url) {
        try {
            return isValidURL(new URL(url));
        } catch (Exception var2) {
            return false;
        }
    }

    public BufferedImage loadImage(String s) {
        return ImageUtil.loadImage(s, this);
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }
}
