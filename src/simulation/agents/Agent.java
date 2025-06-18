package simulation.agents;

import engine.core.Renderer;
import engine.math.Vector2D;
import engine.math.linearAlgebra;
import engine.objects.renderable.baseObject;
import engine.rendering.PrimitiveRenderer;
import simulation.behaviors.MosquitoSwarmBehavior;
import simulation.behaviors.MosquitoCirclingBehavior;

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
    public double MASS = SimulationConstants.DEFAULT_AGENT_MASS;
    public double MAX_SPEED = SimulationConstants.DEFAULT_MAX_SPEED;
    public double MAX_FORCE = SimulationConstants.DEFAULT_MAX_FORCE;
    public double MAX_TURN_RATE = Math.toRadians(300); // 300 degrees per second max turn rate

    // World and rendering properties
    public int WIDTH = SimulationConstants.DEFAULT_WINDOW_WIDTH;
    public int HEIGHT = SimulationConstants.DEFAULT_WINDOW_HEIGHT;
    public float radius = (float) SimulationConstants.DEFAULT_AGENT_RADIUS;

    // Behavior system
    private MosquitoSwarmBehavior attractionBehavior;
    private MosquitoCirclingBehavior circlingBehavior;
    private MosquitoType mosquitoType;

    // Perception range for finding neighbors
    public double SWARM_DISTANCE = SimulationConstants.DEFAULT_SWARM_DISTANCE;

    // Path tracking
    private dynamic2DPath path;
    private static Random random = new Random();

    private double nervousness = 0.5;

    public Agent(Vector2D randomPos) {
        super(randomPos);
        initialize();
        this.attractionBehavior = new MosquitoSwarmBehavior();
        this.circlingBehavior = new MosquitoCirclingBehavior();

        // Randomly assign mosquito type
        this.mosquitoType = random.nextBoolean() ? MosquitoType.LIGHTER : MosquitoType.CIRCLER;
    }

    public Agent(Vector2D randomPos, MosquitoType type) {
        super(randomPos);
        initialize();
        this.attractionBehavior = new MosquitoSwarmBehavior();
        this.circlingBehavior = new MosquitoCirclingBehavior();
        this.mosquitoType = type;
    }

    private void initialize() {
        acceleration = new Vector2D(0, 0);
        lastAcceleration = new Vector2D(0, 0);

        double initialSpeed = (0.3 + random.nextDouble() * 0.7) * MAX_SPEED;
        double initialAngle = random.nextDouble() * 2 * Math.PI;
        velocity = new Vector2D(
                Math.cos(initialAngle) * initialSpeed,
                Math.sin(initialAngle) * initialSpeed
        );

        nervousness = 0.2 + random.nextDouble() * 0.8;

        if (!velocity.isNullvector()) {
            heading = new Vector2D(velocity);
            heading.normalize();
        } else {
            heading = new Vector2D(1, 0);
        }

        side = linearAlgebra.vertical(heading);
        path = new dynamic2DPath(20);
    }

    // Update the update method signature and add wind handling
    public void update(double deltaTime, List<Agent> neighbors, Vector2D mousePosition) {
        if (path.getSize() == 0 ||
                linearAlgebra.euclideanDistance(position, path.getElement(0)) > radius * 2) {
            path.addWaypoint(new Vector2D(position));
        }

        // Get behavior force
        Vector2D force = getBehaviorForce(neighbors);

        // Add wind force
        Vector2D windForce = simulation.environment.WindManager.getWindForce(position, mousePosition, MASS);
        force.add(windForce);

        // Add nervousness
        Vector2D nervousnessForce = getNervousnessForce();
        force.add(nervousnessForce);

        force.truncate(MAX_FORCE);

        // Physics update with turning constraints
        Vector2D deltaAcceleration = linearAlgebra.mult(force, 1.0 / MASS);
        acceleration = deltaAcceleration;

        velocity.add(linearAlgebra.mult(acceleration, deltaTime));
        velocity.mult(0.995);

        // Apply turning constraints before limiting speed
        updatePhysicsWithTurningConstraints(deltaTime);

        velocity.truncate(MAX_SPEED);

        Vector2D deltaPosition = linearAlgebra.mult(velocity, deltaTime);
        position.add(deltaPosition);

        updateOrientationFast();
        handleWorldBounds();
        updateStatus();
    }

    private Vector2D getBehaviorForce(List<Agent> neighbors) {
        switch (mosquitoType) {
            case LIGHTER:
                return attractionBehavior.getWeightedForce(this, neighbors);
            case CIRCLER:
                return circlingBehavior.getWeightedForce(this, neighbors);
            default:
                return attractionBehavior.getWeightedForce(this, neighbors);
        }
    }

    private Vector2D getNervousnessForce() {
        double intensity = nervousness * MAX_FORCE * 0.3;
        return new Vector2D(
                (random.nextGaussian() - 0.5) * intensity,
                (random.nextGaussian() - 0.5) * intensity
        );
    }

    private void updateOrientationFast() {
        if (!velocity.isNullvector()) {
            Vector2D newDesiredHeading = new Vector2D(velocity);
            newDesiredHeading.normalize();

            if (!heading.isNullvector() && !newDesiredHeading.isNullvector()) {
                // Calculate the angle between current and desired heading
                double currentAngle = Math.atan2(heading.y, heading.x);
                double desiredAngle = Math.atan2(newDesiredHeading.y, newDesiredHeading.x);

                // Calculate the angular difference (shortest path)
                double angleDiff = desiredAngle - currentAngle;

                // Normalize angle difference to [-π, π]
                while (angleDiff > Math.PI) angleDiff -= 2 * Math.PI;
                while (angleDiff < -Math.PI) angleDiff += 2 * Math.PI;

                // Limit turning rate
                double maxTurnThisFrame = MAX_TURN_RATE * (1.0 / 60.0); // Assuming 60 FPS
                double actualTurn = Math.max(-maxTurnThisFrame, Math.min(maxTurnThisFrame, angleDiff));

                // Apply the limited turn
                double newAngle = currentAngle + actualTurn;
                heading.x = Math.cos(newAngle);
                heading.y = Math.sin(newAngle);

            } else {
                heading = newDesiredHeading;
            }

            side = linearAlgebra.vertical(heading);
        }
    }

    private void updatePhysicsWithTurningConstraints(double deltaTime) {
        // Current velocity direction
        Vector2D currentDirection = new Vector2D();
        if (!velocity.isNullvector()) {
            currentDirection = new Vector2D(velocity);
            currentDirection.normalize();
        } else {
            currentDirection = new Vector2D(heading);
        }

        // Desired direction based on forces
        Vector2D desiredDirection = new Vector2D(acceleration);
        if (!desiredDirection.isNullvector()) {
            desiredDirection.normalize();
        } else {
            desiredDirection = new Vector2D(currentDirection);
        }

        // Limit direction change based on turn rate
        if (!currentDirection.isNullvector() && !desiredDirection.isNullvector()) {
            double currentAngle = Math.atan2(currentDirection.y, currentDirection.x);
            double desiredAngle = Math.atan2(desiredDirection.y, desiredDirection.x);

            double angleDiff = desiredAngle - currentAngle;
            while (angleDiff > Math.PI) angleDiff -= 2 * Math.PI;
            while (angleDiff < -Math.PI) angleDiff += 2 * Math.PI;

            double maxTurn = MAX_TURN_RATE * deltaTime;
            double actualTurn = Math.max(-maxTurn, Math.min(maxTurn, angleDiff));

            double newAngle = currentAngle + actualTurn;
            Vector2D limitedDirection = new Vector2D(Math.cos(newAngle), Math.sin(newAngle));

            // Apply the limited direction to velocity
            double currentSpeed = velocity.length();
            velocity = linearAlgebra.mult(limitedDirection, currentSpeed);
        }
    }


    private void updateStatus() {
        double speedRatio = velocity.length() / MAX_SPEED;
        nervousness = 0.2 + speedRatio * 0.6 + random.nextGaussian() * 0.1;
        nervousness = Math.max(0.1, Math.min(1.0, nervousness));
    }

    private void handleWorldBounds() {
        boolean wrapped = false;

        if (position.x < -radius * 0.5) {
            position.x = WIDTH + radius * 0.5;
            wrapped = true;
        }
        if (position.x > WIDTH + radius * 0.5) {
            position.x = -radius * 0.5;
            wrapped = true;
        }
        if (position.y < -radius * 0.5) {
            position.y = HEIGHT + radius * 0.5;
            wrapped = true;
        }
        if (position.y > HEIGHT + radius * 0.5) {
            position.y = -radius * 0.5;
            wrapped = true;
        }

        if (wrapped) {
            path = new dynamic2DPath(20);
        }
    }

    @Override
    public void render() {
        engine.rendering.PrimitiveRenderer.renderMosquito(
                (float) position.x,
                (float) position.y,
                SimulationConstants.MOSQUITO_SIZE,
                (float) velocity.x,
                (float) velocity.y
        );
    }

    public void setWorldBounds(int width, int height) { this.WIDTH = width; this.HEIGHT = height; }
    public void setSwarmDistance(double distance) { this.SWARM_DISTANCE = distance; }
    public double getDistanceTo(Agent other) { return linearAlgebra.euclideanDistance(this.position, other.position); }
    public boolean canSee(Agent other) { return getDistanceTo(other) <= SWARM_DISTANCE; }
}