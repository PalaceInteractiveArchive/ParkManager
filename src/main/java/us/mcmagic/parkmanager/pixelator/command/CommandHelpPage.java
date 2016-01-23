package us.mcmagic.parkmanager.pixelator.command;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unchecked")
public class CommandHelpPage implements Iterable {

    private String header;
    private String footer;
    private String commandLabel;
    private int commandsPerPage;
    private List details;
    private List masterPermissions;


    public CommandHelpPage(CommandHandler handler, String header, String footer, String commandLabel, int commandsPerPage) {
        this.header = header;
        this.footer = footer;
        this.commandLabel = commandLabel;
        this.commandsPerPage = commandsPerPage;
        this.details = new ArrayList();
        for (Object h : handler) {
            ICommand c = (ICommand) h;
            this.details.add(c.getClass().getAnnotation(CommandDetails.class));
        }

        this.masterPermissions = handler.getMasterPermissions();
    }

    private String toString(CommandDetails c) {
        return this.commandLabel.replace("<command>", c.usage()).replace("<description>", c.description()).replace("<permission>", c.permission());
    }

    public void showPage(CommandSender s, int page) {
        List v = this.getVisibleDetails(s);
        StringBuilder b = new StringBuilder();

        int pages;
        for (pages = (page - 1) * this.commandsPerPage; pages <= page * this.commandsPerPage - 1 && pages <= v.size() - 1; ++pages) {
            b.append(b.length() == 0 ? (this.header != null && this.header.length() > 0 ? "\n§r" : "") : "\n§r")
                    .append(this.toString((CommandDetails) v.get(pages)));
        }

        if (this.header != null && this.header.length() > 0) {
            b.insert(0, this.header);
        }

        if (this.footer != null && this.footer.length() > 0) {
            pages = this.getPages(s);
            b.append("\n§r").append(this.footer.replace("<current_page>", (page == pages ? "§6§l" : "§a§l") + page)
                    .replace("<page_amount>", String.valueOf(pages)));
        }

        s.sendMessage(b.toString());
    }

    public boolean hasPage(CommandSender s, int page) {
        return page > 0 && page <= this.getPages(s);
    }

    public int getPages(CommandSender s) {
        double p = (double) this.getVisibleDetails(s).size() / (double) this.commandsPerPage;
        int pr = (int) p;
        return p > (double) pr ? pr + 1 : pr;
    }

    public List getVisibleDetails(CommandSender s) {
        ArrayList visible = new ArrayList();
        for (Object o : this) {
            CommandDetails c = (CommandDetails) o;
            if (s.hasPermission(c.permission())) {
                visible.add(c);
            } else {
                for (Object perm : this.masterPermissions) {
                    String p = (String) perm;
                    if (s.hasPermission(p)) {
                        visible.add(c);
                        break;
                    }
                }
            }
        }

        return visible;
    }

    public String getHeader() {
        return this.header;
    }

    public String getFooter() {
        return this.footer;
    }

    public String getCommandLabel() {
        return this.commandLabel;
    }

    public int getCommandsPerPage() {
        return this.commandsPerPage;
    }

    public Iterator iterator() {
        return this.details.iterator();
    }
}
