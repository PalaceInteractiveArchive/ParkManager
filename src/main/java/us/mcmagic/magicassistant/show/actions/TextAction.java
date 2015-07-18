package us.mcmagic.magicassistant.show.actions;


import us.mcmagic.magicassistant.show.Show;

public class TextAction extends ShowAction {
    public String Text;

    public TextAction(Show show, long time, String text) {
        super(show, time);

        Text = text;
    }

    @Override
    public void play() {
        show.displayText(Text);
    }
}
