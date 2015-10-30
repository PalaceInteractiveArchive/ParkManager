package us.mcmagic.magicassistant.show.schedule;

import us.mcmagic.magicassistant.show.handlers.schedule.ShowDay;
import us.mcmagic.magicassistant.show.handlers.schedule.ShowTime;
import us.mcmagic.magicassistant.show.handlers.schedule.ShowType;

/**
 * Created by Marc on 10/29/15
 */
public class ScheduledShow {
    private ShowType type;
    private ShowDay day;
    private ShowTime time;

    public ScheduledShow(ShowType type, ShowDay day, ShowTime time) {
        this.type = type;
        this.day = day;
        this.time = time;
    }

    public ShowType getType() {
        return type;
    }

    public ShowDay getDay() {
        return day;
    }

    public ShowTime getTime() {
        return time;
    }
}