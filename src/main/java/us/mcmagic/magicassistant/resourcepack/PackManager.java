package us.mcmagic.magicassistant.resourcepack;

import net.minecraft.server.v1_8_R2.PacketPlayOutResourcePackSend;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.magicassistant.utils.InventoryType;
import us.mcmagic.magicassistant.utils.InventoryUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.player.User;
import us.mcmagic.mcmagiccore.resource.ResourcePack;
import us.mcmagic.mcmagiccore.resource.ResourceStatusEvent;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Marc on 3/14/15
 */
public class PackManager implements Listener {
    private HashMap<String, ItemStack> packItems = new HashMap<>();

    @SuppressWarnings("deprecation")
    public void initialize() {
        Bukkit.getPluginManager().registerEvents(this, MCMagicCore.getInstance());
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/MagicAssistant/packs.yml"));
        for (String s : config.getStringList("pack-list")) {
            String name = config.getString("packs." + s + ".name");
            packItems.put(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', name)),
                    new ItemCreator(Material.getMaterial(config.getInt("packs." + s + ".id")),
                            ChatColor.translateAlternateColorCodes('&', config.getString("packs." + s + ".name"))));
        }
    }

    public void login(Player player) {
        User user = MCMagicCore.getUser(player.getUniqueId());
        String current = user.getPack();
        if (current.equals("none")) {
            player.sendMessage(ChatColor.GREEN + "Please select a Resource Pack. If you do not want one, select " +
                    ChatColor.RED + "Disabled.");
            openMenu(player);
            return;
        }
        if (current.equals("disabled")) {
            return;
        }
        ResourcePack pack = MCMagicCore.getInstance().resourceManager.getPack(current);
        if (pack == null) {
            player.sendMessage(ChatColor.RED + "Your selected Resource Pack is not available. Please select a new one.");
            openMenu(player);
            return;
        }
        sendPack(player, pack.getName());
    }

    //TODO: Finish and test Inventory

    public void handleClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            InventoryUtil.openInventory(player, InventoryType.MAINMENU);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        if (name.endsWith("(SELECTED)")) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You already have this pack selected!");
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 100, 0);
            return;
        }
        for (Map.Entry<String, ItemStack> entry : packItems.entrySet()) {
            ItemStack stack = entry.getValue();
            if (stack.getType().equals(item.getType())) {
                player.closeInventory();
                sendPack(player, entry.getKey());
                return;
            }
        }
    }

    public void openMenu(Player player) {
        List<ResourcePack> packs = MCMagicCore.getInstance().resourceManager.getPacks();
        Inventory menu = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Resource Pack Menu");
        player.closeInventory();
        int place = 13;
        //If odd, increase place by 1
        if (packs.size() % 2 != 1) {
            place++;
        }
        int amount = 1;
        String current = MCMagicCore.getUser(player.getUniqueId()).getPack();
        for (Map.Entry<String, ItemStack> entry : packItems.entrySet()) {
            if (place > 16) {
                break;
            }
            ItemStack pack = entry.getValue();
            if (entry.getKey().equalsIgnoreCase(current)) {
                ItemMeta meta = pack.getItemMeta();
                meta.setDisplayName(meta.getDisplayName() + ChatColor.GREEN + " (SELECTED)");
                pack.setItemMeta(meta);
            }
            menu.setItem(place, pack);
            if (amount % 2 == 0) {
                place -= amount;
            } else {
                place += amount;
            }
            amount++;
        }
        menu.setItem(22, BandUtil.getBackItem());
        player.openInventory(menu);
    }

    public void sendPack(Player player, ResourcePack pack) {
        player.sendMessage(ChatColor.GREEN + "Attempting to send you the " + ChatColor.YELLOW + pack.getName() +
                ChatColor.GREEN + " Resource Pack! \n" + pack.getUrl());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutResourcePackSend(pack.getUrl(),
                "null"));
        MCMagicCore.getUser(player.getUniqueId()).setPack(pack.getName());
        MCMagicCore.getInstance().resourceManager.setPack(player.getUniqueId(), pack.getName());
    }

    public void sendPack(Player player, String name) {
        ResourcePack pack = MCMagicCore.getInstance().resourceManager.getPack(name);
        if (pack == null) {
            player.sendMessage(ChatColor.RED + "We tried to send you a Resource Pack, but it was not found!");
            player.sendMessage(ChatColor.RED + "Please contact a Staff Member about this. (Error Code 101)");
            return;
        }
        sendPack(player, pack);
    }

    @EventHandler
    public void onResourceStatus(ResourceStatusEvent event) {
        Player player = event.getPlayer();
        switch (event.getStatus()) {
            case ACCEPTED:
                player.sendMessage(ChatColor.GREEN + "Resource Pack accepted! Downloading now...");
                break;
            case LOADED:
                player.sendMessage(ChatColor.GREEN + "Resource Pack loaded!");
                break;
            case FAILED:
                player.sendMessage(ChatColor.RED + "Download failed! Please report this to a Staff Member. (Error Code 101)");
                break;
            case DECLINED:
                player.sendMessage(ChatColor.RED + "You have declined the Resource Pack!");
                player.sendMessage(ChatColor.YELLOW + "For help with this, visit: " + ChatColor.AQUA +
                        "http://mcmagic.us/rphelp");
                break;
            default:
                player.sendMessage(ChatColor.RED + "Download failed! Please report this to a Staff Member. (Error Code 101)");
        }
    }
}