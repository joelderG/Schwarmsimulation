package simulation.agents;

import engine.core.Renderer;
import engine.math.Vector2D;
import engine.math.linearAlgebra;
import engine.objects.renderable.baseObject;
import engine.rendering.PrimitiveRenderer;

import java.util.List;
import java.util.Random;

public class Agent extends baseObject {
    public Vector2D acceleration;
    public Vector2D lastAcceleration;
    public Vector2D velocity;

    public Vector2D heading;
    public Vector2D side;

    public double MASS = 1.0;
    public double MAX_SPEED = 50.0;
    public double MAX_FORCE = 100.0;
    public double MAX_TURN_RATE = Math.toRadians(180);

    public double SWARM_DISTANCE = 50.0;
    public double SEPARATION_DISTANCE = 25.0;
    public double ALIGNMENT_DISTANCE = 40.0;
    public double COHESION_DISTANCE = 60.0;

    public double SEPARATION_WEIGHT = 2.0;
    public double ALIGNMENT_WEIGHT = 1.0;
    public double COHESION_WEIGHT = 1.0;

    public int WIDTH = 1280, HEIGHT = 720;
    public float radius = 3.0f;

    private dynamic2DPath path;
    private static Random random = new Random();

    public Agent(Vector2D randomPos) {
        super();
        initialize();
    }

    private void initialize() {
        acceleration = new Vector2D(0,0);
        lastAcceleration = new Vector2D(0,0);
        velocity = new Vector2D(random.nextGaussian() * 20, random.nextGaussian() * 20);

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
        Vector2D steeringForce = calculateSteeringForce(neighbors);

        lastAcceleration = new Vector2D(acceleration);
        acceleration = linearAlgebra.div(steeringForce, MASS);

        Vector2D deltaVel = linearAlgebra.mult(acceleration, deltaTime);
        velocity.add(deltaVel);

        if (velocity.length() > MAX_SPEED) {
            velocity.normalize();
            velocity.mult(MAX_SPEED);
        }

        Vector2D deltaPos = linearAlgebra.mult(velocity, deltaTime);
        position.add(deltaPos);

        if (!velocity.isNullvector()) {
            heading = linearAlgebra.normalize(velocity);
            side = linearAlgebra.vertical(heading);
        }

        wrapAroundWorld();

        path.addWaypoint(new Vector2D(position));
    }

    private Vector2D calculateSteeringForce(List<Agent> neighbors) {
        Vector2D separation = calculateSeparation(neighbors);
        Vector2D alignment = calculateAlignment(neighbors);
        Vector2D cohesion = calculateCohesion(neighbors);

        separation.mult(SEPARATION_WEIGHT);
        alignment.mult(ALIGNMENT_WEIGHT);
        cohesion.mult(COHESION_WEIGHT);

        Vector2D totalForce = new Vector2D();
        totalForce.add(separation);
        totalForce.add(alignment);
        totalForce.add(cohesion);

        if (totalForce.length() > MAX_FORCE) {
            totalForce.normalize();
            totalForce.mult(MAX_FORCE);
        }

        return totalForce;
    }

    private Vector2D calculateSeparation(List<Agent> neighbors) {
        Vector2D steer = new Vector2D();
        int count = 0;

        for (Agent other : neighbors) {
            double distance = linearAlgebra.euclideanDistance(position, other.position);

            if (distance > 0 && distance < SEPARATION_DISTANCE) {
                Vector2D diff = linearAlgebra.sub(position, other.position);
                diff.normalize();
                diff.div(distance); // Je näher, desto stärker
                steer.add(diff);
                count++;
            }
        }

        if (count > 0) {
            steer.div(count);
            steer.normalize();
            steer.mult(MAX_SPEED);
            steer.sub(velocity);
        }

        return steer;
    }

    /**
     * Alignment: Geschwindigkeit an Nachbarn anpassen
     */
    private Vector2D calculateAlignment(List<Agent> neighbors) {
        Vector2D averageVel = new Vector2D();
        int count = 0;

        for (Agent other : neighbors) {
            double distance = linearAlgebra.euclideanDistance(position, other.position);

            if (distance > 0 && distance < ALIGNMENT_DISTANCE) {
                averageVel.add(other.velocity);
                count++;
            }
        }

        if (count > 0) {
            averageVel.div(count);
            averageVel.normalize();
            averageVel.mult(MAX_SPEED);

            Vector2D steer = linearAlgebra.sub(averageVel, velocity);
            return steer;
        }

        return new Vector2D();
    }

    /**
     * Cohesion: Zu Zentrum der Nachbargruppe bewegen
     */
    private Vector2D calculateCohesion(List<Agent> neighbors) {
        Vector2D centerOfMass = new Vector2D();
        int count = 0;

        for (Agent other : neighbors) {
            double distance = linearAlgebra.euclideanDistance(position, other.position);

            if (distance > 0 && distance < COHESION_DISTANCE) {
                centerOfMass.add(other.position);
                count++;
            }
        }

        if (count > 0) {
            centerOfMass.div(count);
            return seek(centerOfMass);
        }

        return new Vector2D();
    }

    /**
     * Seek-Verhalten: Zu einem Ziel bewegen
     */
    private Vector2D seek(Vector2D target) {
        Vector2D desired = linearAlgebra.sub(target, position);
        desired.normalize();
        desired.mult(MAX_SPEED);

        Vector2D steer = linearAlgebra.sub(desired, velocity);
        return steer;
    }

    /**
     * Welt-Grenzen: Wrapping um Bildschirmränder
     */
    private void wrapAroundWorld() {
        if (position.x < 0) position.x = WIDTH;
        if (position.x > WIDTH) position.x = 0;
        if (position.y < 0) position.y = HEIGHT;
        if (position.y > HEIGHT) position.y = 0;
    }

    /**
     * Rendering des Agents
     */
    @Override
    public void render() {
        Renderer.pushMatrix();

        // Agent als Kreis mit Kraftvektoren rendern
        PrimitiveRenderer.renderCircleWithForces(
                (float)position.x,
                (float)position.y,
                (int)radius,
                velocity,
                acceleration
        );

        Renderer.popMatrix();
    }

    /**
     * Pfad rendern (optional)
     */
    public void renderPath() {
        if (path.getSize() < 2) return;

        Renderer.setColor(0.5f, 0.5f, 0.5f, 0.3f);

        Vector2D[] pathPoints = path.getElementList();
        for (int i = 0; i < pathPoints.length - 1; i++) {
            Vector2D p1 = pathPoints[i];
            Vector2D p2 = pathPoints[i + 1];

            PrimitiveRenderer.renderLine2D(
                    (float)p1.x, (float)p1.y,
                    (float)p2.x, (float)p2.y
            );
        }

        Renderer.resetColor();
    }

    // Getter/Setter für Parameter-Anpassung
    public void setWorldBounds(int width, int height) {
        this.WIDTH = width;
        this.HEIGHT = height;
    }

    public void setSeparationWeight(double weight) { this.SEPARATION_WEIGHT = weight; }
    public void setAlignmentWeight(double weight) { this.ALIGNMENT_WEIGHT = weight; }
    public void setCohesionWeight(double weight) { this.COHESION_WEIGHT = weight; }

    public double getSeparationWeight() { return SEPARATION_WEIGHT; }
    public double getAlignmentWeight() { return ALIGNMENT_WEIGHT; }
    public double getCohesionWeight() { return COHESION_WEIGHT; }
}
