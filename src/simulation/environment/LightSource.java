package simulation.environment;

import engine.math.Vector2D;
import engine.core.Renderer;
import engine.rendering.PrimitiveRenderer;

public class LightSource {
    private Vector2D position;
    private double intensity;
    private double radius;

    // Simple flickering
    private long creationTime;
    private double baseIntensity;

    public LightSource(Vector2D position, double intensity, double radius) {
        this.position = new Vector2D(position);
        this.intensity = intensity;
        this.baseIntensity = intensity;
        this.radius = radius;
        this.creationTime = System.currentTimeMillis();
    }

    public void update(double deltaTime) {
        // Simple flickering effect
        double time = (System.currentTimeMillis() - creationTime) / 1000.0;
        double flicker = 1.0 + Math.sin(time * 8.0) * 0.1; // Gentle flicker
        this.intensity = baseIntensity * flicker;
    }

    public void render() {
        Renderer.pushMatrix();

        // Calculate current flicker intensity
        double flickerFactor = intensity / baseIntensity;

        // Layer 3: Outer glow (largest, most transparent)
        Renderer.setColor(1.0f, 1.0f, 0.8f, (float)(0.15 * flickerFactor));
        PrimitiveRenderer.renderCircle(
                (float) position.x,
                (float) position.y,
                (float) (radius * 2.5),
                32
        );

        // Layer 2: Middle glow
        Renderer.setColor(1.0f, 1.0f, 0.85f, (float)(0.3 * flickerFactor));
        PrimitiveRenderer.renderCircle(
                (float) position.x,
                (float) position.y,
                (float) (radius * 1.5),
                24
        );

        // Layer 1: Inner light
        Renderer.setColor(1.0f, 1.0f, 0.9f, (float)(0.7 * flickerFactor));
        PrimitiveRenderer.renderCircle(
                (float) position.x,
                (float) position.y,
                (float) radius,
                16
        );

        // Core: Bright center
        Renderer.setColor(1.0f, 1.0f, 0.95f, (float)(0.9 * flickerFactor));
        PrimitiveRenderer.renderCircle(
                (float) position.x,
                (float) position.y,
                (float) (radius * 0.4),
                8
        );

        Renderer.popMatrix();
        Renderer.resetColor();
    }

    // Getters and setters
    public Vector2D getPosition() { return new Vector2D(position); }
    public double getIntensity() { return intensity; }
    public double getRadius() { return radius; }
}