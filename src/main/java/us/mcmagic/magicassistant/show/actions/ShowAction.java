package us.mcmagic.magicassistant.show.actions;


import us.mcmagic.magicassistant.show.Show;

public abstract class ShowAction {
    public Show show;
    public long time;

    public ShowAction(Show show, long time) {
        this.show = show;
        this.time = time;
    }

    public abstract void play();
}
