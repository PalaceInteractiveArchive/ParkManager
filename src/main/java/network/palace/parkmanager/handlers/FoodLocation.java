package network.palace.parkmanager.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Marc on 4/26/16
 */
@Getter
@Setter
@AllArgsConstructor
public class FoodLocation {
    private String name;
    private String warp;
    private int type;
    private byte data;
}