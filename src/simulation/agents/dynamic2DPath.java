package simulation.agents;

import engine.math.Vector2D;

import java.util.ArrayList;

public class dynamic2DPath {
    private ArrayList<Vector2D> list;
    private int max;

    public dynamic2DPath(int max) {
        list = new ArrayList<Vector2D>();
        this.max = max;
    }

    public void addWaypoint(Vector2D element) {
        list.add(0, element);
        if (list.size() >= max) {
            list.remove(list.size()-1);
        }
    }

    public int getSize() {
        return list.size();
    }

    public Vector2D getElement(int index) {
        return list.get(index);
    }

    public Vector2D[] getElementList() {
        Vector2D[] list = new Vector2D[this.list.size()];

        for(int i = 0; i < this.list.size(); i++) {
            list[i] = this.getElement(i);
        }
        return list;
    }
}
