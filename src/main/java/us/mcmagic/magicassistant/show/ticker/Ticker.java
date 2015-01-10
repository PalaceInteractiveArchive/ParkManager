package us.mcmagic.magicassistant.show.ticker;

import us.mcmagic.magicassistant.MagicAssistant;

public class Ticker implements Runnable {
    private MagicAssistant pl;

    public Ticker(MagicAssistant plugin) {
        pl = plugin;
        pl.getServer().getScheduler().scheduleSyncRepeatingTask(pl, this, 0L, 1L);
    }

    @Override
    public void run() {
        pl.getServer().getPluginManager().callEvent(new TickEvent());
    }
}
