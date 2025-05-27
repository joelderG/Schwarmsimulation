package simulation.agents;

import engine.math.Vector2D;
import engine.objects.renderable.baseObject;

public abstract class Agent extends baseObject {
    public Vector2D acceleration;
    public Vector2D lastAcceleration;
    public Vector2D velocity;

    public Vector2D heading;
    public Vector2D side;
    public double MASS;
    public double MAX_SPEED;
    public double MAX_TURN_RATE;
    public double SWARM_DISTANCE;
    public int WIDTH, HEIGHT;

}
