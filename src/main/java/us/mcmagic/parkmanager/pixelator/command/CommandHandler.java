package us.mcmagic.parkmanager.pixelator.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.parkmanager.ParkManager;

import java.util.*;
import java.util.Map.Entry;

public abstract class CommandHandler implements CommandExecutor, Iterable {
    private ParkManager plugin;
    private Map commands;
    private List sorted;
    public CommandHelpPage helpPage;
    private List masterPermissions;


    public CommandHandler(ParkManager plugin, String command, String helpHeader, String helpFooter, String helpCommandLabel, int helpCommandsPerPage, String... masterPermissions) {
        this.plugin = plugin;
        plugin.getCommand(command).setExecutor(this);
        this.commands = new HashMap();
        this.sorted = new ArrayList();
        this.registerCommands();
        this.helpPage = new CommandHelpPage(this, helpHeader, helpFooter, helpCommandLabel, helpCommandsPerPage);
        this.masterPermissions = Arrays.asList(masterPermissions);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            this.showUsage(sender, (ICommand) this.commands.get("help"));
        } else {
            ICommand c = this.getMatchingCommand(args[0].toLowerCase());
            if (c == null) {
                this.showUsage(sender, (ICommand) this.commands.get("help"));
            } else {
                CommandDetails cd = this.getDetails(c);
                String[] params = this.trimParams(args);
                if (!(sender instanceof Player) && !cd.executableAsConsole()) {
                    sender.sendMessage("§cThis command can\'t be executed as console!");
                } else if (!cd.permission().equals("None") && !sender.hasPermission(cd.permission()) && !this.hasMasterPermission(sender)) {
                    sender.sendMessage("§cYou don\'t have permission for this command!");
                } else if (!this.checkUsage(c, params)) {
                    this.showUsage(sender, c);
                } else {
                    c.execute(this.plugin, sender, params);
                }
            }
        }

        return true;
    }

    private ICommand getMatchingCommand(String name) {
        for (Object o : this.commands.entrySet()) {
            Entry e = (Entry) o;
            if (e.getKey().equals(name)) {
                return (ICommand) e.getValue();
            }
        }

        return null;
    }

    private CommandDetails getDetails(ICommand cmd) {
        return cmd.getClass().getAnnotation(CommandDetails.class);
    }

    private String[] trimParams(String[] args) {
        return Arrays.copyOfRange(args, 1, args.length);
    }

    private boolean checkUsage(ICommand cmd, String[] params) {
        String[] p = this.getDetails(cmd).usage().split(" ");
        p = Arrays.copyOfRange(p, 2, p.length);
        int min = 0;
        int max = 0;
        for (String s : p) {
            ++max;
            if (!s.matches("\\[.*\\]")) {
                ++min;
            }
        }
        return params.length >= min && params.length <= max;
    }

    public void showUsage(CommandSender sender, ICommand cmd) {
        CommandDetails cd = this.getDetails(cmd);
        sender.sendMessage("§cInvalid usage!\n§6" + cd.usage());
    }

    private boolean hasMasterPermission(CommandSender sender) {
        for (Object masterPermission : this.masterPermissions) {
            String m = (String) masterPermission;
            if (sender.hasPermission(m)) {
                return true;
            }
        }

        return false;
    }

    protected abstract void registerCommands();

    @SuppressWarnings("unchecked")
    protected void register(Class cmd) {
        CommandDetails cd = (CommandDetails) cmd.getAnnotation(CommandDetails.class);
        if (cd != null) {
            try {
                ICommand e = (ICommand) cmd.newInstance();
                this.commands.put(cd.name(), e);
                this.sorted.add(e);
            } catch (Exception var4) {
                var4.printStackTrace();
            }
        }

    }

    public List getMasterPermissions() {
        return this.masterPermissions;
    }

    public Iterator iterator() {
        return this.sorted.iterator();
    }
}
