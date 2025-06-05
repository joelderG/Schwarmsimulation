package simulation.agents;

import engine.core.Renderer;
import engine.math.Vector2D;
import engine.math.linearAlgebra;
import engine.objects.renderable.baseObject;
import engine.rendering.PrimitiveRenderer;
import simulation.behaviors.FishBehavior;

import java.util.List;
import java.util.Random;

public class Agent extends baseObject {
    // Physics properties
    public Vector2D acceleration;
    public Vector2D lastAcceleration;
    public Vector2D velocity;
    public Vector2D heading;
    public Vector2D side;

    // Physical constants
    public double MASS = 1.0;
    public double MAX_SPEED = 50.0;
    public double MAX_FORCE = 100.0;
    public double MAX_TURN_RATE = Math.toRadians(180);

    // World and rendering properties
    public int WIDTH = 1280, HEIGHT = 720;
    public float radius = 40.0f;

    // Behavior system
    private FishBehavior fishBehavior;

    // Perception range for finding neighbors
    public double SWARM_DISTANCE = 90.0;

    // Path tracking
    private dynamic2DPath path;
    private static Random random = new Random();

    public Agent(Vector2D randomPos) {
        super(randomPos);
        initialize();
    }

    private void initialize() {
        // Initialize physics
        acceleration = new Vector2D(0, 0);
        lastAcceleration = new Vector2D(0, 0);
        velocity = new Vector2D(random.nextGaussian() * 20, random.nextGaussian() * 20);

        // Initialize behavior system with fish-specific parameters
        fishBehavior = new FishBehavior(MAX_FORCE);

        // Set initial heading based on velocity
        if (!velocity.isNullvector()) {
            heading = new Vector2D(velocity);
            heading.normalize();
        } else {
            heading = new Vector2D(1, 0);
        }

        side = linearAlgebra.vertical(heading);
        path = new dynamic2DPath(50);
    }

    public void update(double deltaTime, List<Agent> neighbors) {
        // Calculate steering force using behavior system
        Vector2D steeringForce = fishBehavior.calculateSteeringForce(this, neighbors);

        // Apply physics
        lastAcceleration = new Vector2D(acceleration);
        acceleration = linearAlgebra.div(steeringForce, MASS);

        // Update velocity
        Vector2D deltaVel = linearAlgebra.mult(acceleration, deltaTime);
        velocity.add(deltaVel);

        // Limit speed
        if (velocity.length() > MAX_SPEED) {
            velocity.normalize();
            velocity.mult(MAX_SPEED);
        }

        // Update position
        Vector2D deltaPos = linearAlgebra.mult(velocity, deltaTime);
        position.add(deltaPos);

        // Update heading and side vectors
        if (!velocity.isNullvector()) {
            heading = linearAlgebra.normalize(velocity);
            side = linearAlgebra.vertical(heading);
        }

        // Handle world boundaries
        wrapAroundWorld();

        // Record path
        path.addWaypoint(new Vector2D(position));
    }

    private void wrapAroundWorld() {
        if (position.x < 0) position.x = WIDTH;
        if (position.x > WIDTH) position.x = 0;
        if (position.y < 0) position.y = HEIGHT;
        if (position.y > HEIGHT) position.y = 0;
    }

    @Override
    public void render() {
        Renderer.pushMatrix();

        // Render agent as circle with force vectors
        PrimitiveRenderer.renderCircleWithForces(
                (float) position.x,
                (float) position.y,
                (int) radius,
                velocity,
                acceleration
        );

        Renderer.popMatrix();
    }

    public void renderPath() {
        if (path.getSize() < 2) return;

        Renderer.setColor(0.5f, 0.5f, 0.5f, 0.3f);

        Vector2D[] pathPoints = path.getElementList();
        for (int i = 0; i < pathPoints.length - 1; i++) {
            Vector2D p1 = pathPoints[i];
            Vector2D p2 = pathPoints[i + 1];

            PrimitiveRenderer.renderLine2D(
                    (float) p1.x, (float) p1.y,
                    (float) p2.x, (float) p2.y
            );
        }

        Renderer.resetColor();
    }

    public void setWorldBounds(int width, int height) {
        this.WIDTH = width;
        this.HEIGHT = height;
    }

    public void setSwarmDistance(double distance) {
        this.SWARM_DISTANCE = distance;
    }

    public void setSeparationWeight(double weight) {
        fishBehavior.setSeparationWeight(weight);
    }

    public void setAlignmentWeight(double weight) {
        fishBehavior.setAlignmentWeight(weight);
    }

    public void setCohesionWeight(double weight) {
        fishBehavior.setCohesionWeight(weight);
    }

    public double getSeparationWeight() {
        return fishBehavior.getSeparationWeight();
    }

    public double getAlignmentWeight() {
        return fishBehavior.getAlignmentWeight();
    }

    public double getCohesionWeight() {
        return fishBehavior.getCohesionWeight();
    }

    public void setSeparationDistance(double distance) {
        fishBehavior.setSeparationDistance(distance);
    }

    public void setAlignmentDistance(double distance) {
        fishBehavior.setAlignmentDistance(distance);
    }

    public void setCohesionDistance(double distance) {
        fishBehavior.setCohesionDistance(distance);
    }

    public FishBehavior getBehaviorSystem() {
        return fishBehavior;
    }

    public void setBehaviorSystem(FishBehavior newBehavior) {
        this.fishBehavior = newBehavior;
    }

    public double getCurrentSpeed() {
        return velocity.length();
    }

    public double getDistanceTo(Agent other) {
        return linearAlgebra.euclideanDistance(this.position, other.position);
    }

    public boolean canSee(Agent other) {
        return getDistanceTo(other) <= SWARM_DISTANCE;
    }

    public void clearPath() {
        path = new dynamic2DPath(50);
    }

    public int getPathSize() {
        return path.getSize();
    }
}

