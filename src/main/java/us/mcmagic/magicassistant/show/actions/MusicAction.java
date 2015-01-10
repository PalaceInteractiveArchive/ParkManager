package us.mcmagic.magicassistant.show.actions;

import us.mcmagic.magicassistant.show.Show;

public class MusicAction extends ShowAction {
    public int Record;

    public MusicAction(Show show, long time, int type) {
        super(show, time);

        Record = type;
    }

    @Override
    public void Play() {
        Show.playMusic(Record);
    }
}
