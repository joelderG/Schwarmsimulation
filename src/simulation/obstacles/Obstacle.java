package simulation.obstacles;

import engine.core.Renderer;
import engine.math.Vector2D;
import engine.math.linearAlgebra;
import engine.rendering.PrimitiveRenderer;

public class Obstacle {
    private Vector2D position;
    private double radius;
    private boolean isVisible;

    public Obstacle(Vector2D position, double radius) {
        this.position = new Vector2D(position);
        this.radius = radius;
        this.isVisible = true;
    }

    public void render() {
        if (!isVisible) return;

        Renderer.pushMatrix();

        Renderer.setColor(0.3f, 0.3f, 0.3f, 0.8f);
        PrimitiveRenderer.renderCircle(
                (float) position.x,
                (float) position.y,
                (float) radius,
                32
        );

        Renderer.setColor(0.1f, 0.1f, 0.1f, 1.0f);
        PrimitiveRenderer.renderCircleOutline(
                (float) position.x,
                (float) position.y,
                (float) radius,
                32
        );

        Renderer.resetColor();
        Renderer.popMatrix();
    }

    public boolean isInsideObstacle(Vector2D point) {
        double distance = linearAlgebra.euclideanDistance(position, point);
        return distance <= radius;
    }

    public double getDistanceToEdge(Vector2D point) {
        double distance = linearAlgebra.euclideanDistance(position, point);
        return Math.max(0, distance - radius);
    }

    public Vector2D getAvoidanceForce(Vector2D agentPosition, double agentRadius) {
        Vector2D toAgent = new Vector2D(agentPosition);
        toAgent.sub(position);

        double distance = toAgent.length();
        double combinedRadius = radius + agentRadius;

        if (distance > combinedRadius + 20) {
            return new Vector2D();
        }

        if (distance > 0) {
            toAgent.normalize();
            double force = (combinedRadius + 20 - distance) / 20.0;
            toAgent.mult(force * 100);
            return toAgent;
        }

        return new Vector2D(Math.random() - 0.5, Math.random() - 0.5);
    }

    public Vector2D getPosition() {
        return new Vector2D(position);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }
}
