package us.mcmagic.parkmanager.pin;

import org.bukkit.Bukkit;
import org.json.JSONArray;
import org.json.JSONObject;
import us.mcmagic.parkmanager.ParkManager;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Marc on 6/24/16
 */
public class PinManager {
    public String url = "https://spreadsheets.google.com/feeds/cells/1TqQDdkv8etTkX2gNLVE4jfppEomIoUISRQWHIxYgtxo/od6/public/basic?alt=json";

    public PinManager() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(ParkManager.getInstance(), this::loadConfigurations, 0L, 36000L);
    }

    public void loadConfigurations() {
        JSONObject obj = readJsonFromUrl(url);
        if (obj == null) {
            return;
        }
        JSONArray array = obj.getJSONObject("feed").getJSONArray("entry");
        PinRarity nextRarity = null;
        for (int i = 0; i < array.length(); i++) {
            JSONObject ob = array.getJSONObject(i);
            JSONObject s = ob.getJSONObject("content");
            JSONObject id = ob.getJSONObject("title");
            String column = id.getString("$t");
            Integer row = Integer.parseInt(column.substring(1, 2));
            switch (column.substring(0, 1).toLowerCase()) {
                case "a":
                    nextRarity = PinRarity.fromString(s.getString("$t"));
                    break;
                case "b":
                    nextRarity.setRarity(s.getDouble("$t"));
                    break;
                case "c":
                    nextRarity.setAmount(s.getDouble("$t"));
                    break;
            }
        }
    }

    private static JSONObject readJsonFromUrl(String url) {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}