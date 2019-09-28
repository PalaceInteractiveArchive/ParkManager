package network.palace.parkmanager.queues;

import lombok.Getter;
import lombok.Setter;
import network.palace.core.utils.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

@Getter
public class QueueSign {
    private Location location;
    @Setter private String queueName;
    private boolean fastPassSign;
    @Setter private int amount;
    @Setter private String wait;

    public QueueSign(Location location, String queueName, boolean fastPassSign, int amount) {
        this.location = location;
        this.queueName = queueName;
        this.fastPassSign = fastPassSign;
        this.amount = amount;
        this.wait = "No Wait";
    }

    public void updateSign() {
        Block b = location.getBlock();
        if (!b.getType().equals(Material.SIGN) &&
                !b.getType().equals(Material.WALL_SIGN) &&
                !b.getType().equals(Material.SIGN_POST))
            return;
        Sign s = (Sign) b.getState();
        String[] lines;
        if (fastPassSign) {
            lines = new String[]{ChatColor.BLUE + "[FastPass]", queueName, amount
                    + " Player" + TextUtil.pluralize(amount), wait};
        } else {
            lines = new String[]{ChatColor.BLUE + "[Queue]", queueName, amount
                    + " Player" + TextUtil.pluralize(amount), wait};
        }
        boolean updated = false;
        for (int i = 0; i < lines.length; i++) {
            if (!s.getLine(i).equals(lines[i])) {
                s.setLine(i, lines[i]);
                updated = true;
            }
        }
        if (updated) s.update();
    }
}
