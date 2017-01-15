package network.palace.parkmanager.show.schedule;

import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.show.handlers.ShowDay;
import network.palace.parkmanager.show.handlers.ShowType;
import network.palace.parkmanager.show.handlers.TimeStorage;
import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ScheduleManager {
    public String url = "https://spreadsheets.google.com/feeds/cells/10TSt2OhCQGb8Wh_uUn-PLZExmuLP6ROKXCaywN_Ai1U/od6/public/basic?alt=json";
    private List<ScheduledShow> shows = new ArrayList<>();

    public ScheduleManager() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(ParkManager.getInstance(), this::update, 0L, 36000L);
    }

    public void update() {
        JSONObject obj = readJsonFromUrl(url);
        if (obj == null) {
            return;
        }
        JSONArray array = (JSONArray) ((JSONObject) obj.get("feed")).get("entry");
        shows.clear();
        TimeStorage timeStorage = new TimeStorage();
        for (Object anArray : array) {
            JSONObject ob = (JSONObject) anArray;
            JSONObject sch = (JSONObject) ob.get("content");
            JSONObject id = (JSONObject) ob.get("title");
            String column = (String) id.get("$t");
            Integer row = Integer.parseInt(column.substring(1, 2));
            if (column.substring(0, 1).equalsIgnoreCase("a")) {
                timeStorage.add(row, (String) sch.get("$t"));
                continue;
            }
            ShowType name = ShowType.fromString((String) sch.get("$t"));
            ScheduledShow show = new ScheduledShow(name, dayFromCellID((String) id.get("$t")), timeStorage.getTime(row));
            shows.add(show);
        }
    }

    public List<ScheduledShow> getShows() {
        return new ArrayList<>(shows);
    }

    private ShowDay dayFromCellID(String s) {
        String column = s.substring(0, 1);
        Integer row = Integer.parseInt(s.substring(1));
        ShowDay day = null;
        switch (column.toLowerCase()) {
            case "b":
                day = ShowDay.MONDAY;
                break;
            case "c":
                day = ShowDay.TUESDAY;
                break;
            case "d":
                day = ShowDay.WEDNESDAY;
                break;
            case "e":
                day = ShowDay.THURSDAY;
                break;
            case "f":
                day = ShowDay.FRIDAY;
                break;
            case "g":
                day = ShowDay.SATURDAY;
                break;
            case "h":
                day = ShowDay.SUNDAY;
                break;
        }
        return day;
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
