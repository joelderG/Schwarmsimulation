package simulation.environment;

import engine.math.Vector2D;
import engine.math.linearAlgebra;
import simulation.agents.Agent;

import java.util.ArrayList;
import java.util.List;

public class LightSourceManager {
    private static List<LightSource> lightSources = new ArrayList<>();

    public static void addLightSource(LightSource lightSource) {
        lightSources.add(lightSource);
    }

    public static void removeLightSource(LightSource lightSource) {
        lightSources.remove(lightSource);
    }

    public static void clearLightSources() {
        lightSources.clear();
    }

    public static Vector2D getAttractionForce(Agent agent) {
        Vector2D totalAttraction = new Vector2D();

        for (LightSource light : lightSources) {
            double distance = linearAlgebra.euclideanDistance(agent.position, light.getPosition());

            if (distance > 0 && distance < light.getRadius() * 3) {
                Vector2D attraction = linearAlgebra.sub(light.getPosition(), agent.position);
                attraction.normalize();

                double strength = (light.getIntensity() * light.getRadius()) / (distance + 1);
                attraction.mult(strength);

                totalAttraction.add(attraction);
            }
        }

        return totalAttraction;
    }

    public static void renderAll() {
        for (LightSource light : lightSources) {
            light.render();
        }
    }

    public static void updateAll(double deltaTime) {
        for (LightSource light : lightSources) {
            light.update(deltaTime);
        }
    }

    public static List<LightSource> getLightSources() {
        return new ArrayList<>(lightSources);
    }
}