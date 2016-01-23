package us.mcmagic.parkmanager.pixelator.renderer.util;

import org.bukkit.entity.Player;
import us.mcmagic.parkmanager.pixelator.renderer.ImageRenderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public class RendererList extends ArrayList {

    private static final long serialVersionUID = 480832239116565687L;


    public RendererList() {
    }

    public RendererList(Collection c) {
        super(c);
    }

    public void remove(short id) {
        for (int i = 0; i < this.size(); ++i) {
            short o = ((ImageRenderer) this.get(i)).getId();
            if (o == id) {
                this.remove(i);
            }
        }

    }

    public void handleQuit(Player p) {
        for (Object o : this) {
            ((ImageRenderer) o).handleQuit(p);
        }

    }

    public ImageRenderer get(short id) {
        for (Object o1 : this) {
            ImageRenderer e = (ImageRenderer) o1;
            short o = e.getId();
            if (o == id) {
                return e;
            }
        }
        return null;
    }

    public boolean contains(short id) {
        return this.get(id) != null;
    }

    public List getIds() {
        ArrayList list = new ArrayList();
        for (int i = 0; i < this.size(); ++i) {
            list.add(((ImageRenderer) this.get(i)).getId());
        }
        return list;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Object o : this) {
            ImageRenderer e = (ImageRenderer) o;
            if (s.length() > 0) {
                s.append("#");
            }
            s.append(e.toString());
        }
        return s.toString();
    }
}
