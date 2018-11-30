package network.palace.parkmanager.show.schedule;


import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.parkmanager.show.handlers.ShowDay;
import network.palace.parkmanager.show.handlers.ShowType;

/**
 * Created by Marc on 10/29/15
 */
@Getter
@AllArgsConstructor
public class ScheduledShow {
    private ShowType type;
    private ShowDay day;
    private String time;
}