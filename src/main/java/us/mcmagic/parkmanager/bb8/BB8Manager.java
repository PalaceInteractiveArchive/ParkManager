package us.mcmagic.parkmanager.bb8;

import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Marc on 4/27/16
 */
public class BB8Manager {
    private static BB8Manager instance;
    private ArrayList<BB8> droids;

    public static BB8Manager get() {
        if (instance == null) instance = new BB8Manager();
        return instance;
    }

    BB8Manager() {
        droids = new ArrayList<>();
    }

    protected void registerDroid(BB8 droid) {
        if (!droids.contains(droid)) droids.add(droid);
    }

    protected void removeDroid(BB8 droid) {
        droids.remove(droid);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public BB8 getDroid(Player p) {
        return droids.stream().filter(b -> p.equals(b.getOwner())).findFirst().orElse(null);
    }

    public ArrayList<BB8> getAllDroids() {
        return new ArrayList<>(droids);
    }
}