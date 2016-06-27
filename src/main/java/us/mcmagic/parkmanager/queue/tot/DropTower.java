package us.mcmagic.parkmanager.queue.tot;

import java.util.Random;

/**
 * Created by Marc on 12/30/15
 */
public enum DropTower {
    ECHO, FOXTROT;

    private TowerLayout layout;
    private int count = 0;

    DropTower() {
        randomizeLayout();
    }

    public TowerLayout getLayout() {
        return layout;
    }

    public TowerLayout randomizeLayout() {
        count++;
        return layout = new TowerLayout(getRandomBetween(91, 101), getRandomBetween(68, 79));
    }

    public int getCount() {
        return count;
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

    public void resetCount() {
        this.count = 0;
    }
}