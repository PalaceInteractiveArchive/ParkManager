package us.mcmagic.magicassistant.utils;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import us.mcmagic.magicassistant.MagicAssistant;

public class ScoreboardUtil implements Listener {
    static MagicAssistant pl;

    public ScoreboardUtil(MagicAssistant instance) {
        pl = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
        final Team admin = sb.registerNewTeam("admin");
        final Team cm = sb.registerNewTeam("cm");
        final Team sg = sb.registerNewTeam("specialguest");
        final Team intern = sb.registerNewTeam("intern");
        final Team character = sb.registerNewTeam("character");
        final Team donor = sb.registerNewTeam("donor");
        final Team guest = sb.registerNewTeam("guest");
        admin.setPrefix(ChatColor.GOLD + "");
        cm.setPrefix(ChatColor.GREEN + "");
        sg.setPrefix(ChatColor.DARK_PURPLE + "");
        intern.setPrefix(ChatColor.DARK_GREEN + "");
        character.setPrefix(ChatColor.BLUE + "");
        donor.setPrefix(ChatColor.AQUA + "");
        guest.setPrefix(ChatColor.GRAY + "");
        player.setScoreboard(sb);
        Bukkit.getScheduler().runTaskAsynchronously(pl, new Runnable() {
            @Override
            public void run() {
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    setScoreboardColorForOtherPlayer(tp, player);
                    if (isInPermGroup(tp, "mayor") || isInPermGroup(tp, "technician")
                            || isInPermGroup(tp, "manager")) {
                        admin.addPlayer(tp);
                    } else if (isInPermGroup(tp, "moderator")) {
                        cm.addPlayer(tp);
                    } else if (isInPermGroup(tp, "specialguest")) {
                        sg.addPlayer(tp);
                    } else if (isInPermGroup(tp, "character")
                            || isInPermGroup(tp, "characterguest")) {
                        character.addPlayer(tp);
                    } else if (isInPermGroup(tp, "intern")) {
                        intern.addPlayer(tp);
                    } else if (isInPermGroup(tp, "donor")) {
                        donor.addPlayer(tp);
                    } else {
                        guest.addPlayer(tp);
                    }
                }
            }
        });
    }

    public static void setScoreboardColorForOtherPlayer(Player player, Player tp) {
        Scoreboard sb = player.getScoreboard();
        if (isInPermGroup(tp, "mayor") || isInPermGroup(tp, "technician")
                || isInPermGroup(tp, "manager")) {
            sb.getTeam("admin").addPlayer(tp);
        } else if (isInPermGroup(tp, "moderator")) {
            sb.getTeam("cm").addPlayer(tp);
        } else if (isInPermGroup(tp, "specialguest")) {
            sb.getTeam("specialguest").addPlayer(tp);
        } else if (isInPermGroup(tp, "character")
                || isInPermGroup(tp, "characterguest")) {
            sb.getTeam("character").addPlayer(tp);
        } else if (isInPermGroup(tp, "intern")) {
            sb.getTeam("intern").addPlayer(tp);
        } else if (isInPermGroup(tp, "donor")) {
            sb.getTeam("donor").addPlayer(tp);
        } else {
            sb.getTeam("guest").addPlayer(tp);
        }
    }

    public static boolean isInPermGroup(Player player, String group) {
        String[] groups = WorldGuardPlugin.inst().getGroups(player);
        for (int i = 0; i < groups.length; i++) {
            if (groups[i].toLowerCase().equals(group.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}