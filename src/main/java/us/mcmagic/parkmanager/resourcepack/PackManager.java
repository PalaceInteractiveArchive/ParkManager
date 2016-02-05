package us.mcmagic.parkmanager.resourcepack;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.player.User;
import us.mcmagic.mcmagiccore.resource.CurrentPackReceivedEvent;
import us.mcmagic.mcmagiccore.resource.ResourceManager;
import us.mcmagic.mcmagiccore.resource.ResourcePack;
import us.mcmagic.mcmagiccore.resource.ResourceStatusEvent;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.InventoryType;
import us.mcmagic.parkmanager.utils.BandUtil;
import us.mcmagic.parkmanager.utils.FileUtil;

import java.util.*;

/**
 * Created by Marc on 3/14/15
 */
public class PackManager implements Listener {
    private HashMap<String, ItemStack> packItems = new HashMap<>();

    @SuppressWarnings("deprecation")
    public void initialize() {
        packItems.clear();
        YamlConfiguration config = FileUtil.packsYaml();
        for (String s : config.getStringList("pack-list")) {
            String name = config.getString("packs." + s + ".name");
            packItems.put(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', name)),
                    new ItemCreator(Material.getMaterial(config.getInt("packs." + s + ".id")),
                            ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', config.getString("packs." +
                                    s + ".name"))));
        }
    }

    @EventHandler
    public void onCurrentPackReceived(CurrentPackReceivedEvent event) {
        User user = event.getUser();
        final Player player = Bukkit.getPlayer(user.getUniqueId());
        String current = event.getPacks();
        String preferred = user.getPreferredPack();
        boolean doSeasonal = MCMagicCore.resourceManager.getPack("Seasonal") != null;
        if (preferred.equals("none")) {
            player.sendMessage(ChatColor.GREEN + "Please select a Resource Pack. If you do not want one, select " +
                    ChatColor.RED + "Disabled.");
            Bukkit.getScheduler().runTaskLater(ParkManager.getInstance(), new Runnable() {
                @Override
                public void run() {
                    openMenu(player);
                }
            }, 20L);
            return;
        }
        if (preferred.equalsIgnoreCase("disabled")) {
            return;
        }
        if (preferred.equals("NoPrefer")) {
            if (MCMagicCore.getMCMagicConfig().serverName.equals("Seasonal") && doSeasonal) {
                if (!user.getResourcePack().equals("Seasonal")) {
                    MCMagicCore.resourceManager.sendPack(player, "Seasonal");
                }
                return;
            }
            if (!user.getResourcePack().equals("none")) {
                MCMagicCore.resourceManager.sendPack(player, "Blank");
            }
            user.setResourcePack("none");
            return;
        }
        if (MCMagicCore.getMCMagicConfig().serverName.equals("Seasonal") && doSeasonal) {
            MCMagicCore.resourceManager.sendPack(player, "Seasonal");
            return;
        }
        if (!current.equals(preferred)) {
            ResourcePack pack = MCMagicCore.resourceManager.getPack(preferred);
            if (pack == null) {
                player.sendMessage(ChatColor.RED + "Your selected Resource Pack is not available. Please select a new one.");
                Bukkit.getScheduler().runTaskLater(ParkManager.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        openMenu(player);
                    }
                }, 20L);
                return;
            }
            MCMagicCore.resourceManager.sendPack(player, pack.getName());
        }
    }

    public void handleClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        User user = MCMagicCore.getUser(player.getUniqueId());
        boolean doSeasonal = !Bukkit.hasWhitelist();
        if (item.equals(BandUtil.getBackItem())) {
            ParkManager.inventoryUtil.openInventory(player, InventoryType.MYPROFILE);
            return;
        }
        if (item.getItemMeta() == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        if (name.endsWith("(SELECTED)")) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You already have this selected!");
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 100, 0);
            return;
        }
        if (event.getSlot() == 8) {
            player.sendMessage(ChatColor.RED + "You disabled the Auto-Resource Pack!");
            MCMagicCore.resourceManager.setPreferredPack(player.getUniqueId(), "Disabled");
            player.closeInventory();
            if (!user.getResourcePack().equalsIgnoreCase("none")) {
                MCMagicCore.resourceManager.sendPack(player, "Blank");
            }
            user.setResourcePack("none");
            return;
        }
        if (event.getSlot() == 0) {
            player.sendMessage(ChatColor.RED +
                    "You don't prefer any of the available packs, but still want packs like Seasonal to be installed.");
            MCMagicCore.resourceManager.setPreferredPack(player.getUniqueId(), "NoPrefer");
            player.closeInventory();
            if (MCMagicCore.getMCMagicConfig().serverName.equals("Seasonal") && doSeasonal) {
                if (!user.getResourcePack().equals("Seasonal")) {
                    MCMagicCore.resourceManager.sendPack(player, "Seasonal");
                }
                return;
            }
            if (!user.getResourcePack().equals("none")) {
                MCMagicCore.resourceManager.sendPack(player, "Blank");
            }
            user.setResourcePack("none");
            return;
        }
        for (Map.Entry<String, ItemStack> entry : packItems.entrySet()) {
            ItemStack stack = entry.getValue();
            if (stack.getType().equals(item.getType())) {
                user.setPreferredPack(entry.getKey());
                user.setResourcePack(entry.getKey());
                player.closeInventory();
                MCMagicCore.resourceManager.sendPack(player, entry.getKey());
                MCMagicCore.resourceManager.setPreferredPack(player.getUniqueId(), entry.getKey());
                return;
            }
        }
    }

    public void openMenu(Player player) {
        User user = MCMagicCore.getUser(player.getUniqueId());
        List<ResourcePack> packs = getPacks(packItems.keySet());
        Inventory menu = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Resource Pack Menu");
        int place = 13;
        String preferred = user.getPreferredPack();
        ItemStack none;
        if (preferred.equalsIgnoreCase("NoPrefer")) {
            none = new ItemCreator(Material.STONE, 1, (byte) 6, ChatColor.BLUE + "None " + ChatColor.GREEN +
                    "(SELECTED)", Arrays.asList(ChatColor.DARK_AQUA + "Selecting this still allows",
                    ChatColor.DARK_AQUA + "other Packs like Seasonal", ChatColor.DARK_AQUA + "to be installed automatically"));
        } else {
            none = new ItemCreator(Material.STONE, 1, (byte) 6, ChatColor.BLUE + "None", Arrays.asList(ChatColor.DARK_AQUA
                            + "Selecting this still allows", ChatColor.DARK_AQUA + "other Packs like Seasonal",
                    ChatColor.DARK_AQUA + "to be installed automatically"));
        }
        int amount = 1;
        //If even, increase place by 1
        if (packs.size() % 2 == 0) {
            place++;
            amount++;
        }
        for (Map.Entry<String, ItemStack> entry : packItems.entrySet()) {
            if (place > 16) {
                break;
            }
            ItemStack pack = new ItemStack(entry.getValue());
            if (entry.getKey().equalsIgnoreCase(preferred)) {
                ItemMeta meta = pack.getItemMeta();
                meta.setDisplayName(meta.getDisplayName() + ChatColor.GREEN + " (SELECTED)");
                pack.setItemMeta(meta);
            }
            menu.setItem(place, pack);
            //If even
            if (amount % 2 == 0) {
                place -= amount;
            } else {
                place += amount;
            }
            amount++;
        }
        menu.setItem(0, none);
        menu.setItem(22, BandUtil.getBackItem());
        player.openInventory(menu);
    }

    private List<ResourcePack> getPacks(Set<String> plist) {
        List<ResourcePack> list = new ArrayList<>();
        ResourceManager rm = MCMagicCore.resourceManager;
        for (String s : plist) {
            ResourcePack pack = rm.getPack(s);
            list.add(pack);
        }
        return list;
    }

    /*
    public void sendPack(Player player, ResourcePack pack) {
        player.sendMessage(ChatColor.GREEN + "Attempting to send you the " + ChatColor.YELLOW + pack.getName() +
                ChatColor.GREEN + " Resource Pack! \n" + pack.getUrl());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutResourcePackSend(pack.getUrl(),
                "null"));
        MCMagicCore.getUser(player.getUniqueId()).setResourcePack(pack.getName());
    }

    public void sendPack(Player player, String name) {
        ResourcePack pack = MCMagicCore.resourceManager.getPack(name);
        if (pack == null) {
            player.sendMessage(ChatColor.RED + "We tried to send you a Resource Pack, but it was not found!");
            player.sendMessage(ChatColor.RED + "Please contact a Staff Member about this. (Error Code 101)");
            return;
        }
        sendPack(player, pack);
    }
    */

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
                for (int i = 0; i < 5; i++) {
                    player.sendMessage(" ");
                }
                player.sendMessage(ChatColor.RED + "You have declined the Resource Pack!");
                player.sendMessage(ChatColor.YELLOW + "For help with this, visit: " + ChatColor.AQUA +
                        "http://mcmagic.us/rphelp");
                break;
            default:
                player.sendMessage(ChatColor.RED + "Download failed! Please report this to a Staff Member. (Error Code 101)");
        }
    }
}