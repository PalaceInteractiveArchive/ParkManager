package us.mcmagic.magicassistant.watch;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.listeners.BlockEdit;
import us.mcmagic.mcmagiccore.actionbar.ActionBarManager;

/**
 * Created by Marc on 10/11/15
 */
public class WatchTask implements Runnable {

    @Override
    public void run() {
        String msg = ChatColor.YELLOW + "" + ChatColor.BOLD + "Current time in EST: " + ChatColor.GREEN +
                MagicAssistant.bandUtil.currentTime();
        for (Player tp : Bukkit.getOnlinePlayers()) {
            if (tp.getInventory().getHeldItemSlot() != 6) {
                continue;
            }
            if (BlockEdit.isInBuildMode(tp.getUniqueId())) {
                continue;
            }
            ActionBarManager.sendMessage(tp, msg);
        }
    }
}