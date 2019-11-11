package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.shows.ShowEntry;
import org.bukkit.ChatColor;

@CommandMeta(description = "Allow Shareholders to run Shows with staff approval")
public class ShowsCommand extends CoreCommand {

    public ShowsCommand() {
        super("shows");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (player.getRank().getRankId() < Rank.SHAREHOLDER.getRankId()) {
            player.sendMessage(ChatColor.AQUA + "\nYou must be a " + Rank.SHAREHOLDER.getFormattedName() + ChatColor.AQUA +
                    " to use this! Find out more info at " + ChatColor.GREEN + "https://palnet.us/shareholder" + ChatColor.RESET + "\n");
            return;
        }
        if (player.getRank().getRankId() < Rank.MOD.getRankId() && !player.getRank().equals(Rank.SHAREHOLDER)) {
            player.sendMessage(ChatColor.RED + "You can't use this command!");
            return;
        }
        if (player.getRank().equals(Rank.SHAREHOLDER)) {
            ParkManager.getShowMenu().openShowMenu(player);
            return;
        }
        if (player.getRank().getRankId() < Rank.DEVELOPER.getRankId()) {
            ParkManager.getShowMenu().openRequestMenu(player);
            return;
        }
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "list": {
                    ParkManager.getShowMenu().listShows(player);
                    return;
                }
                case "reload": {
                    player.sendMessage(ChatColor.BLUE + "Reloading Shareholder Show Menu...");
                    ParkManager.getShowMenu().initialize();
                    player.sendMessage(ChatColor.BLUE + "Reloaded Shareholder Show Menu!");
                    return;
                }
                case "menu": {
                    ParkManager.getShowMenu().openShowMenu(player);
                    return;
                }
                case "requests": {
                    ParkManager.getShowMenu().openRequestMenu(player);
                    return;
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            ShowEntry entry = ParkManager.getShowMenu().getShow(args[1]);
            if (entry == null) {
                player.sendMessage(ChatColor.RED + "A show isn't in the menu with the file '" + args[1] + "'!");
                return;
            }
            if (ParkManager.getShowMenu().removeShow(entry)) {
                player.sendMessage(ChatColor.GREEN + "Removed the show file '" + args[1] + "' from the Shareholder Show Menu!");
            } else {
                player.sendMessage(ChatColor.RED + "There was an error removing the show file '" + args[1] + "' from the Shareholder Show Menu!");
            }
            return;
        } else if (args.length >= 4 && args[0].equalsIgnoreCase("add")) {
            StringBuilder name = new StringBuilder();
            for (int i = 3; i < args.length; i++) {
                name.append(args[i]).append(" ");
            }
            ShowEntry entry = new ShowEntry(args[1], args[2], ChatColor.translateAlternateColorCodes('&', name.toString().trim()));
            ParkManager.getShowMenu().addShow(entry);
            player.sendMessage(ChatColor.GREEN + "Added a new file '" + entry.getShowFile() + "' to the Shareholder Show Menu!");
            return;
        }
        helpMenu(player, args);
    }

    private void helpMenu(CPlayer player, String[] args) {
        player.sendMessage(ChatColor.GREEN + "Show Menu Commands:");
        player.sendMessage(ChatColor.GREEN + "/shows reload");
        player.sendMessage(ChatColor.AQUA + "- Reload the Shareholder Show Menu.");
        player.sendMessage(ChatColor.GREEN + "/shows add [ShowFile] [Region] [Display Name]");
        player.sendMessage(ChatColor.AQUA + "- Add a new show to the Shareholder Show Menu.");
        player.sendMessage(ChatColor.AQUA + "- [ShowFile] is the name of the show file to run (probably a pre-show) without the '.show' extension.");
        player.sendMessage(ChatColor.AQUA + "- Shareholders can only run the show when they're in the [Region].");
        player.sendMessage(ChatColor.AQUA + "- [Display Name] supports color codes.");
        player.sendMessage(ChatColor.GREEN + "/shows remove [ShowFile]");
        player.sendMessage(ChatColor.AQUA + "- Remove a show from the Shareholder Show Menu.");
        player.sendMessage(ChatColor.AQUA + "- This does " + ChatColor.RED + "" + ChatColor.ITALIC + "not " + ChatColor.AQUA + "delete the show file.");
        player.sendMessage(ChatColor.GREEN + "/shows list");
        player.sendMessage(ChatColor.AQUA + "- List all shows in the Shareholder Show Menu.");
        player.sendMessage(ChatColor.GREEN + "/shows menu");
        player.sendMessage(ChatColor.AQUA + "- Open the show menu.");
        player.sendMessage(ChatColor.GREEN + "/shows requests");
        player.sendMessage(ChatColor.AQUA + "- Accept/decline show requests on this server.");
    }
}
