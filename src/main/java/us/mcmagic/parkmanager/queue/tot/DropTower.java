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
        return layout = new TowerLayout(getRandomBetween(94, 104), getRandomBetween(68, 79));
    }

    private int getRandomBetween(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public static DropTower fromString(String s) {
        switch (s.toLowerCase()) {
            case "echo":
                return ECHO;
            case "foxtrot":
                return FOXTROT;
            default:
                return null;
        }
    }
}