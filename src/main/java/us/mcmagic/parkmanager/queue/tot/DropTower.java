package us.mcmagic.parkmanager.queue.tot;

import java.util.Random;

/**
 * Created by Marc on 12/30/15
 */
public enum DropTower {
    ECHO, FOXTROT;

    private TowerLayout layout;

    DropTower() {
        randomizeLayout();
    }

    public TowerLayout getLayout() {
        return layout;
    }

    public TowerLayout randomizeLayout() {
        return layout = new TowerLayout(getRandomBetween(89, 103), getRandomBetween(69, 85));
    }

    private int getRandomBetween(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}