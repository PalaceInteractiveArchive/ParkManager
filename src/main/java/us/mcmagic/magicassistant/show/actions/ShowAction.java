package us.mcmagic.magicassistant.show.actions;


import us.mcmagic.magicassistant.show.Show;

public abstract class ShowAction {
    public Show Show;
    public long Time;

    public ShowAction(Show show, long time) {
        Show = show;
        Time = time;
    }

    public abstract void Play();
}
