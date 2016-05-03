package us.mcmagic.parkmanager.bb8;

// Author: BeMacized
// http://models.bemacized.net/

public class MenuItem {

    private CustomIS item;
    private Runnable exec;

    public MenuItem(CustomIS item, Runnable exec) {
        this.item = item;
        this.exec = exec;
    }

    public CustomIS getItem() {
        return item;
    }

    public Runnable getExec() {
        return exec;
    }

}