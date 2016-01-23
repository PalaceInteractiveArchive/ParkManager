package us.mcmagic.parkmanager.pixelator.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class PlayerUtil {

    public static boolean hasEnoughSpace(Player p, ItemStack i) {
        int s = 0;
        ItemStack[] var6;
        int var5 = (var6 = p.getInventory().getContents()).length;

        for (int var4 = 0; var4 < var5; ++var4) {
            ItemStack is = var6[var4];
            if (is == null) {
                s += 64;
            } else if (is.isSimilar(i)) {
                s += 64 - is.getAmount();
            }
        }
        return s >= i.getAmount();
    }
}
