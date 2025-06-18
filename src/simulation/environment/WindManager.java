
package simulation.environment;

import engine.math.Vector2D;
import engine.math.linearAlgebra;

public class WindManager {
    private static Vector2D currentWindDirection = new Vector2D();
    private static double windStrength = 0.0;
    private static Vector2D lastMousePosition = new Vector2D();
    private static boolean isMousePressed = false;

    // Wind decay and smoothing
    private static final double WIND_DECAY = 0.98; // How fast wind dies down
    private static final double WIND_SMOOTHING = 0.7; // How smooth wind changes are
    private static final double MAX_WIND_STRENGTH = 300.0; // Maximum wind force
    private static final double WIND_RADIUS = 150.0; // How far wind effects reach

    public static void updateWind(Vector2D mousePosition, boolean mousePressed, double deltaTime) {
        if (mousePressed && isMousePressed) {
            // Calculate mouse movement
            Vector2D mouseMovement = linearAlgebra.sub(mousePosition, lastMousePosition);
            double mouseSpeed = mouseMovement.length() / deltaTime;

            // Create wind based on mouse movement
            if (mouseSpeed > 10.0 && !mouseMovement.isNullvector()) {
                Vector2D newWindDirection = new Vector2D(mouseMovement);
                newWindDirection.normalize();

                // Smooth wind direction changes
                if (!currentWindDirection.isNullvector()) {
                    currentWindDirection.mult(WIND_SMOOTHING);
                    newWindDirection.mult(1.0 - WIND_SMOOTHING);
                    currentWindDirection.add(newWindDirection);
                    if (!currentWindDirection.isNullvector()) {
                        currentWindDirection.normalize();
                    }
                } else {
                    currentWindDirection = newWindDirection;
                }

                // Wind strength based on mouse speed
                double targetStrength = Math.min(mouseSpeed * 0.5, MAX_WIND_STRENGTH);
                windStrength = Math.max(windStrength, targetStrength);
            }
        }

        // Wind decay over time
        windStrength *= WIND_DECAY;
        if (windStrength < 1.0) {
            windStrength = 0.0;
            currentWindDirection = new Vector2D();
        }

        lastMousePosition = new Vector2D(mousePosition);
        isMousePressed = mousePressed;
    }

    public static Vector2D getWindForce(Vector2D agentPosition, Vector2D mousePosition, double agentMass) {
        if (windStrength <= 0.0 || currentWindDirection.isNullvector()) {
            return new Vector2D();
        }

        // Distance-based wind effect
        double distanceToMouse = linearAlgebra.euclideanDistance(agentPosition, mousePosition);
        double windEffect = Math.max(0.0, 1.0 - (distanceToMouse / WIND_RADIUS));

        // Mass-based resistance (heavier mosquitos resist more)
        double resistance = 1.0 / (1.0 + agentMass);

        Vector2D windForce = new Vector2D(currentWindDirection);
        windForce.mult(windStrength * windEffect * resistance);

        return windForce;
    }

    public static double getWindStrength() {
        return windStrength;
    }

    public static Vector2D getWindDirection() {
        return new Vector2D(currentWindDirection);
    }

    public static boolean hasWind() {
        return windStrength > 0.0;
    }
}