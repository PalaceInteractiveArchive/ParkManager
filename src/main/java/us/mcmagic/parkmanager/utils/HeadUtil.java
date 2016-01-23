package us.mcmagic.parkmanager.utils;

import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.MojangsonParseException;
import net.minecraft.server.v1_8_R3.MojangsonParser;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.mcmagiccore.player.User;

import java.util.UUID;

/**
 * Created by Marc on 7/1/15
 */
public class HeadUtil {

    public static ItemStack getPlayerHead(User user) throws MojangsonParseException {
        return getPlayerHead(user.getTextureHash());
    }

    public static ItemStack getPlayerHead(String hash) throws MojangsonParseException {
        return getPlayerHead(hash, "Head");
    }

    public static ItemStack getPlayerHead(String hash, String display) throws MojangsonParseException {
        net.minecraft.server.v1_8_R3.ItemStack i = new net.minecraft.server.v1_8_R3.ItemStack(Item.getById(397), 1);
        i.setData(3);
        i.setTag(MojangsonParser.parse("{display:{Name:\"" + display + ChatColor.RESET + "\"},SkullOwner:{Id:\"" +
                UUID.randomUUID() + "\",Properties:{textures:[{Value:\"" + hash + "\"}]}}}"));
        return CraftItemStack.asBukkitCopy(i);
    }
}
