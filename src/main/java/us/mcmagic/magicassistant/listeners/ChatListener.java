package us.mcmagic.magicassistant.listeners;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.PlayerUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ChatListener implements Listener {
    public MagicAssistant pl;

    public ChatListener(MagicAssistant instance) {
        pl = instance;
    }

    public static int silenceTimeMS = 3;
    private HashMap<String, Long> time = new HashMap<>();
    public static List<String> whitelistedDomains = Arrays.asList("mcmagic.us",
            "mcmagic.us/servers/downloads");
    public YamlConfiguration config = YamlConfiguration
            .loadConfiguration(new File("plugins/magicassistant/config.yml"));
    public String ipFilter = config.getString("ipFilter");
    public String domainFilter = config.getString("domainFilter");

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) {
            return;
        }
        String msg = event.getMessage();
        String name = player.getName();
        if (!player.hasPermission("chat.exempt")) {
            if (msg.length() >= 5) {
                msg = stripCaps(msg);
            }
            if (isAdvertisement(msg)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Please do not advertise!");
                Bukkit.broadcast(ChatColor.RED + player.getName()
                                + " possibly advertises: " + ChatColor.WHITE + msg,
                        "chat.viewhidden");
                Bukkit.getLogger().info(
                        player.getName() + " possibly advertises: " + msg);
                return;
            }
        }
        Rank rank = MCMagicCore.getUser(player.getUniqueId()).getRank();
        String emsg;
        if (player.hasPermission("chat.color")) {
            emsg = ChatColor.translateAlternateColorCodes('&', msg);
        } else {
            emsg = msg;
        }
        String servername = MagicAssistant.serverName;
        String message = ChatColor.WHITE + "[" + ChatColor.GREEN + servername
                + ChatColor.WHITE + "] " + "" + rank.getNameWithBrackets() + ChatColor.WHITE + " "
                + ChatColor.GRAY + player.getName() + ": " + ChatColor.WHITE
                + rank.getChatColor() + emsg;
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("ParkChat");
            out.writeUTF(player.getName());
            out.writeUTF(message);
            player.sendPluginMessage(
                    Bukkit.getPluginManager().getPlugin("MagicAssistant"),
                    "BungeeCord", b.toByteArray());
            event.setCancelled(true);
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED
                    + "There is currently a problem with chat. Please message a staff member!");
        }
    }

    private String stripCaps(String msg) {
        int[] caps = checkCaps(msg);
        if (percentageCaps(caps) >= 50 || checkCapsInRow(caps) >= 5) {
            String[] parts = msg.split(" ");
            boolean capsAllowed = false;
            for (int i = 0; i < parts.length; i++) {
                boolean isOnWhitelist = false;
                for (String whitelist : new ArrayList<String>()) {
                    if (whitelist.equalsIgnoreCase(parts[i])) {
                        isOnWhitelist = true;
                        capsAllowed = true;
                        break;
                    }
                }

                if (!isOnWhitelist) {
                    if (!capsAllowed) {
                        char firstChar = parts[i].charAt(0);
                        parts[i] = (firstChar + parts[i].toLowerCase()
                                .substring(1));
                    } else {
                        parts[i] = parts[i].toLowerCase();
                    }

                    capsAllowed = (!parts[i].endsWith("."))
                            && (!parts[i].endsWith("!"));
                }
            }

            return StringUtils.join(parts, " ");
        } else {
            return msg;
        }
    }

    private boolean isAdvertisement(String msg) {
        String finalMsg = msg.replaceAll("[(\\[\\])]", "");
        for (String domain : whitelistedDomains) {
            if (msg.toLowerCase().matches(".*" + domain + ".*")) {
                return false;
            }
        }
        return finalMsg.matches(".*" + ipFilter + ".*")
                || finalMsg.matches(".*" + domainFilter + ".*");
    }

    public static int[] checkCaps(String message) {
        int[] editedMsg = new int[message.length()];
        String[] parts = message.split(" ");
        for (int i = 0; i < parts.length; i++) {
            for (String whitelisted : new ArrayList<String>()) {
                if (whitelisted.equalsIgnoreCase(parts[i])) {
                    parts[i] = parts[i].toLowerCase();
                }
            }
        }

        String msg = StringUtils.join(parts, " ");

        for (int j = 0; j < msg.length(); j++) {
            if ((Character.isUpperCase(msg.charAt(j)))
                    && (Character.isLetter(msg.charAt(j))))
                editedMsg[j] = 1;
            else {
                editedMsg[j] = 0;
            }
        }
        return editedMsg;
    }

    public static int percentageCaps(int[] caps) {
        int sum = 0;
        for (int cap : caps) {
            sum += cap;
        }
        double ratioCaps = sum / caps.length;
        return (int) (100.0D * ratioCaps);
    }

    public static int checkCapsInRow(int[] caps) {
        int sum = 0;
        int sumTemp = 0;
        int j = caps.length;
        for (int i2 : caps) {
            if (i2 == 1) {
                sumTemp++;
                sum = Math.max(sum, sumTemp);
            } else {
                sumTemp = 0;
            }
        }
        return sum;
    }
}