package engine.objects.renderable;

import engine.math.Vector2D;

// improvements
// TODO: consider adding more common attributes like: color, rotation (angle), scale
// TODO: id field is present but not utilized, remove it when no need to identify objects

public abstract class baseObject {
    public int id;
    public Vector2D position;

    public baseObject() {
        this(new Vector2D(0, 0));
    }

    public baseObject(Vector2D position) {
        this.position = new Vector2D(position);
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D pos) {
        this.position = new Vector2D(pos);
    }

    public abstract void render();
}
