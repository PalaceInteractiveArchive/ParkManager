package us.mcmagic.magicassistant.show.schedule;

import org.bukkit.Bukkit;
import org.json.JSONArray;
import org.json.JSONObject;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.show.handlers.schedule.ShowDay;
import us.mcmagic.magicassistant.show.handlers.schedule.ShowTime;
import us.mcmagic.magicassistant.show.handlers.schedule.ShowType;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ShowSchedule {
    public String url = "https://spreadsheets.google.com/feeds/cells/10TSt2OhCQGb8Wh_uUn-PLZExmuLP6ROKXCaywN_Ai1U/od6/public/basic?alt=json";
    private List<ScheduledShow> shows = new ArrayList<>();

    public ShowSchedule() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                update();
            }
        }, 0L, 36000L);
    }

    public void update() {
        JSONObject obj = readJsonFromUrl(url);
        if (obj == null) {
            return;
        }
        JSONArray array = obj.getJSONObject("feed").getJSONArray("entry");
        shows.clear();
        for (int i = 0; i < array.length(); i++) {
            JSONObject ob = array.getJSONObject(i);
            JSONObject sch = ob.getJSONObject("content");
            JSONObject id = ob.getJSONObject("title");
            ShowType name = ShowType.fromString(sch.getString("$t"));
            ScheduledShow show = new ScheduledShow(name, dayFromCellID(id.getString("$t")),
                    timeFromCellID(id.getString("$t")));
            shows.add(show);
        }
    }

    public List<ScheduledShow> getShows() {
        return new ArrayList<>(shows);
    }

    private ShowTime timeFromCellID(String s) {
        String column = s.substring(0, 1);
        Integer row = Integer.parseInt(s.substring(1));
        ShowTime time = null;
        switch (row) {
            case 1:
                time = ShowTime.ELEVEN;
                break;
            case 2:
                time = ShowTime.FOUR;
                break;
            case 3:
                time = ShowTime.NINE;
                break;
        }
        return time;
    }

    private ShowDay dayFromCellID(String s) {
        String column = s.substring(0, 1);
        Integer row = Integer.parseInt(s.substring(1));
        ShowDay day = null;
        switch (column.toLowerCase()) {
            case "a":
                day = ShowDay.MONDAY;
                break;
            case "b":
                day = ShowDay.TUESDAY;
                break;
            case "c":
                day = ShowDay.WEDNESDAY;
                break;
            case "d":
                day = ShowDay.THURSDAY;
                break;
            case "e":
                day = ShowDay.FRIDAY;
                break;
            case "f":
                day = ShowDay.SATURDAY;
                break;
            case "g":
                day = ShowDay.SUNDAY;
                break;
        }
        return day;
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