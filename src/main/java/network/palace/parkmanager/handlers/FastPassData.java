package network.palace.parkmanager.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Marc on 2/19/16
 */
@Getter
@Setter
@AllArgsConstructor
public class FastPassData {
    private int slow;
    private int moderate;
    private int thrill;
    private int slowDay;
    private int moderateDay;
    private int thrillDay;

    public int getPass(RideCategory category) {
        switch (category) {
            case SLOW:
                return getSlow();
            case MODERATE:
                return getModerate();
            case THRILL:
                return getThrill();
        }
        return 0;
    }

    public void setPass(RideCategory category, int count) {
        switch (category) {
            case SLOW:
                setSlow(count);
                return;
            case MODERATE:
                setModerate(count);
                return;
            case THRILL:
                setThrill(count);
        }
    }
}