package network.palace.parkmanager.commands.queue;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.QueueType;
import network.palace.parkmanager.queues.BlockQueue;
import network.palace.parkmanager.queues.Queue;
import org.bukkit.ChatColor;
import org.bukkit.Location;

@CommandMeta(description = "Edit an existing queue")
public class EditCommand extends CoreCommand {

    public EditCommand() {
        super("edit");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 2) {
            helpMenu(player);
            return;
        }
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        Queue queue = ParkManager.getQueueManager().getQueueById(args[0], park.getId());
        if (queue == null) {
            player.sendMessage(ChatColor.RED + "Could not find an queue by id " + args[0] + "!");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "id": {
                if (args.length < 3) {
                    helpMenu(player);
                    return;
                }
                String newId = args[2];
                if (ParkManager.getQueueManager().getQueueById(args[2], park.getId()) != null) {
                    player.sendMessage(ChatColor.RED + "This id is already used by another queue!");
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "See current queue ids with: " + ChatColor.YELLOW + "/queue list");
                    return;
                }

                player.sendMessage(ChatColor.GREEN + "Set " + queue.getName() + "'s " + ChatColor.GREEN +
                        "id to " + ChatColor.YELLOW + newId);

                queue.setId(newId);

                ParkManager.getQueueManager().saveToFile();
                return;
            }
            case "name": {
                if (args.length < 3) {
                    helpMenu(player);
                    return;
                }
                StringBuilder name = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    name.append(args[i]).append(" ");
                }
                String displayName = ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', name.toString().trim());

                player.sendMessage(ChatColor.GREEN + "Set " + queue.getName() + "'s " + ChatColor.GREEN +
                        "display name to " + ChatColor.YELLOW + displayName);

                queue.setName(displayName);

                ParkManager.getQueueManager().saveToFile();
                return;
            }
            case "warp": {
                if (args.length < 3) {
                    helpMenu(player);
                    return;
                }
                queue.setWarp(args[2]);

                player.sendMessage(ChatColor.GREEN + "Set " + queue.getName() + "'s " + ChatColor.GREEN +
                        "warp to " + ChatColor.YELLOW + args[2]);

                ParkManager.getQueueManager().saveToFile();
                return;
            }
            case "groupsize": {
                if (args.length < 3) {
                    helpMenu(player);
                    return;
                }
                if (!MiscUtil.checkIfInt(args[2])) {
                    player.sendMessage(ChatColor.RED + "'" + args[2] + "' is not an integer!");
                    return;
                }
                int groupSize = Integer.parseInt(args[2]);

                queue.setGroupSize(groupSize);

                player.sendMessage(ChatColor.GREEN + "Set " + queue.getName() + "'s " + ChatColor.GREEN +
                        "groupSize to " + ChatColor.YELLOW + groupSize);

                ParkManager.getQueueManager().saveToFile();
                return;
            }
            case "delay": {
                if (args.length < 3) {
                    helpMenu(player);
                    return;
                }
                if (!MiscUtil.checkIfInt(args[2])) {
                    player.sendMessage(ChatColor.RED + "'" + args[2] + "' is not an integer!");
                    return;
                }
                int delay = Integer.parseInt(args[2]);

                queue.setDelay(delay);

                player.sendMessage(ChatColor.GREEN + "Set " + queue.getName() + "'s " + ChatColor.GREEN +
                        "delay to " + ChatColor.YELLOW + delay);

                ParkManager.getQueueManager().saveToFile();
                return;
            }
            case "station": {
                queue.setStation(player.getLocation());

                player.sendMessage(ChatColor.GREEN + "Updated " + queue.getName() + "'s " + ChatColor.GREEN + "station location to where you're standing!");

                ParkManager.getQueueManager().saveToFile();
                return;
            }
            case "blocklocation": {
                if (!queue.getQueueType().equals(QueueType.BLOCK)) {
                    player.sendMessage(ChatColor.RED + "This queue isn't a Block queue!");
                    return;
                }
                Location loc = player.getLocation();
                ((BlockQueue) queue).setBlockLocation(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));

                player.sendMessage(ChatColor.GREEN + "Updated " + queue.getName() + "'s " + ChatColor.GREEN + "block spawn location to where you're standing!");

                ParkManager.getQueueManager().saveToFile();
                return;
            }
        }
        helpMenu(player);
    }

    private void helpMenu(CPlayer player) {
        player.sendMessage(ChatColor.RED + "/queue edit [current id] id [new id]");
        player.sendMessage(ChatColor.RED + "/queue edit [id] name [name]");
        player.sendMessage(ChatColor.RED + "/queue edit [id] warp [warp]");
        player.sendMessage(ChatColor.RED + "/queue edit [id] groupsize [groupSize]");
        player.sendMessage(ChatColor.RED + "/queue edit [id] delay [delay]");
        player.sendMessage(ChatColor.RED + "/queue edit [id] station");
        player.sendMessage(ChatColor.RED + "Block Type:");
        player.sendMessage(ChatColor.RED + "/queue edit [id] blocklocation");
    }
}
