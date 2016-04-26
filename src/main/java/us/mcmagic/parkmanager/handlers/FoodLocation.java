package us.mcmagic.parkmanager.handlers;

/**
 * Created by Marc on 4/26/16
 */
public class FoodLocation {
    private String name;
    private String warp;
    private int type;
    private byte data;

    public FoodLocation(String name, String warp, int type, byte data) {
        this.name = name;
        this.warp = warp;
        this.type = type;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public String getWarp() {
        return warp;
    }

    public int getType() {
        return type;
    }

    public byte getData() {
        return data;
    }

    public void setName(String newName) {
        name.equals(newName);
    }

    public void setWarp(String newWarp) {
        warp.equals(newWarp);
    }

    public void setType(int newType) {
        type = newType;
    }

    public void setData(byte newData) {
        data = newData;
    }
}