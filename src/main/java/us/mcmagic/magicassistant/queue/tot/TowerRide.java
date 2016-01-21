package us.mcmagic.magicassistant.queue.tot;

import java.util.HashMap;

/**
 * Created by Marc on 12/30/15
 */
public class TowerRide {
    private HashMap<Integer, TowerLayout> sequences = new HashMap<>();

    public TowerRide(HashMap<Integer, TowerLayout> sequences) {
        this.sequences = sequences;
    }

    public HashMap<Integer, TowerLayout> getSequences() {
        return sequences;
    }

    public TowerLayout getSequence(int i) {
        return sequences.get(i);
    }
}