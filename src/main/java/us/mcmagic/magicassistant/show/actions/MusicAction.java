package us.mcmagic.magicassistant.show.actions;

import us.mcmagic.magicassistant.show.Show;

public class MusicAction extends ShowAction {
    public int record;

    public MusicAction(Show show, long time, int type) {
        super(show, time);
        record = type;
    }

    @Override
    public void play() {
        show.playMusic(record);
    }
}
