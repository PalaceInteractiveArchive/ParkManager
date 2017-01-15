package network.palace.parkmanager.pin;

import network.palace.parkmanager.ParkManager;
import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
        JSONArray array = (JSONArray) ((JSONObject) obj.get("feed")).get("entry");
        PinRarity nextRarity = null;
        for (Object anArray : array) {
            JSONObject ob = (JSONObject) anArray;
            JSONObject s = (JSONObject) ob.get("content");
            JSONObject id = (JSONObject) ob.get("title");
            String column = (String) id.get("$t");
            Integer row = Integer.parseInt(column.substring(1, 2));
            switch (column.substring(0, 1).toLowerCase()) {
                case "a":
                    nextRarity = PinRarity.fromString((String) s.get("$t"));
                    break;
                case "b":
                    nextRarity.setRarity((Double) s.get("$t"));
                    break;
                case "c":
                    nextRarity.setAmount((Double) s.get("$t"));
                    break;
            }
        }
    }

    private static JSONObject readJsonFromUrl(String url) {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(jsonText);
        } catch (IOException | ParseException e) {
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