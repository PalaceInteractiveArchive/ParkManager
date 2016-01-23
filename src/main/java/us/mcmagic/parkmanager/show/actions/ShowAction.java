package us.mcmagic.parkmanager.show.actions;

import us.mcmagic.parkmanager.show.Show;

public abstract class ShowAction {
    public Show show;
    public long time;

    public ShowAction(Show show, long time) {
        this.show = show;
        this.time = time;
    }

    public abstract void play();
}
