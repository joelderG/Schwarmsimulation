
package simulation.behaviors;

import engine.math.Vector2D;
import engine.math.linearAlgebra;
import simulation.agents.Agent;
import simulation.environment.LightSource;
import simulation.environment.LightSourceManager;

import java.util.List;
import java.util.Random;

public class MosquitoCirclingBehavior {
    private double separationWeight = 2.5;
    private double cohesionWeight = 0.8;
    private double randomWeight = 1.5;
    private double circlingWeight = 5.0;
    private double lightAttractionWeight = 2.0;

    private static Random random = new Random();

    private double preferredCirclingRadius = 60.0;
    private double circlingSpeed = 1.2;
    private double chaosIntensity = 0.8;
    private double directionChangeFrequency = 0.05;

    public Vector2D getWeightedForce(Agent agent, List<Agent> neighbors) {
        Vector2D totalForce = new Vector2D();

        Vector2D separation = getSeparation(agent, neighbors);
        separation.mult(separationWeight);
        totalForce.add(separation);

        Vector2D cohesion = getCohesion(agent, neighbors);
        cohesion.mult(cohesionWeight);
        totalForce.add(cohesion);

        Vector2D randomMovement = getModerateRandomMovement(agent);
        randomMovement.mult(randomWeight);
        totalForce.add(randomMovement);

        Vector2D circlingForce = getCirclingForce(agent);
        circlingForce.mult(circlingWeight);
        totalForce.add(circlingForce);

        Vector2D lightAttraction = getWeakLightAttraction(agent);
        lightAttraction.mult(lightAttractionWeight);
        totalForce.add(lightAttraction);

        Vector2D chaosForce = getChaosForce(agent);
        totalForce.add(chaosForce);

        return totalForce;
    }

    private Vector2D getSeparation(Agent agent, List<Agent> neighbors) {
        Vector2D separationForce = new Vector2D();
        int count = 0;

        for (Agent neighbor : neighbors) {
            double distance = agent.getDistanceTo(neighbor);
            if (distance > 0 && distance < agent.SWARM_DISTANCE * 0.7) {
                Vector2D diff = linearAlgebra.sub(agent.position, neighbor.position);
                if (!diff.isNullvector()) {
                    diff.normalize();
                    double strength = 1.0 / (distance * distance + 0.1);
                    diff.mult(strength);
                    separationForce.add(diff);
                    count++;
                }
            }
        }

        if (count > 0) {
            separationForce.mult(1.0 / count);
            if (!separationForce.isNullvector()) {
                separationForce.normalize();
            }
        }

        return separationForce;
    }

    private Vector2D getCohesion(Agent agent, List<Agent> neighbors) {
        Vector2D centerOfMass = new Vector2D();
        int count = 0;

        for (Agent neighbor : neighbors) {
            double distance = agent.getDistanceTo(neighbor);
            if (distance > 0 && distance < agent.SWARM_DISTANCE) {
                centerOfMass.add(neighbor.position);
                count++;
            }
        }

        if (count > 0) {
            centerOfMass.mult(1.0 / count);
            Vector2D cohesionForce = linearAlgebra.sub(centerOfMass, agent.position);
            if (!cohesionForce.isNullvector()) {
                cohesionForce.normalize();
                return cohesionForce;
            }
        }

        return new Vector2D();
    }

    private Vector2D getModerateRandomMovement(Agent agent) {
        double intensity = 1.0;
        Vector2D randomForce = new Vector2D();

        randomForce.add(new Vector2D(
                random.nextGaussian() * intensity,
                random.nextGaussian() * intensity
        ));

        if (random.nextDouble() < 0.02) {
            double angle = random.nextDouble() * 2 * Math.PI;
            Vector2D impulse = new Vector2D(
                    Math.cos(angle) * intensity,
                    Math.sin(angle) * intensity
            );
            randomForce.add(impulse);
        }

        return randomForce;
    }

    private Vector2D getCirclingForce(Agent agent) {
        Vector2D totalCirclingForce = new Vector2D();

        for (LightSource light : LightSourceManager.getLightSources()) {
            double distance = linearAlgebra.euclideanDistance(agent.position, light.getPosition());

            if (distance < light.getRadius() * 4.0 && distance > 10.0) {
                Vector2D circlingForce = calculateCirclingForceForLight(agent, light, distance);
                totalCirclingForce.add(circlingForce);
            }
        }

        return totalCirclingForce;
    }

    private Vector2D calculateCirclingForceForLight(Agent agent, LightSource light, double distance) {
        Vector2D toLight = linearAlgebra.sub(light.getPosition(), agent.position);
        if (toLight.isNullvector()) return new Vector2D();

        Vector2D radialForce = getRadialForce(toLight, distance);

        Vector2D tangentialForce = getTangentialForce(toLight, distance, light.getIntensity());

        Vector2D combinedForce = new Vector2D();
        combinedForce.add(radialForce);
        combinedForce.add(tangentialForce);

        return combinedForce;
    }

    private Vector2D getRadialForce(Vector2D toLight, double distance) {
        Vector2D radialDirection = new Vector2D(toLight);
        radialDirection.normalize();

        double distanceError = distance - preferredCirclingRadius;
        double radialStrength;

        if (distanceError > 0) {
            radialStrength = Math.min(distanceError / 30.0, 1.5);
        } else {
            radialStrength = Math.max(distanceError / 20.0, -2.0);
        }

        radialDirection.mult(radialStrength);
        return radialDirection;
    }

    private Vector2D getTangentialForce(Vector2D toLight, double distance, double lightIntensity) {
        Vector2D tangentialDirection = linearAlgebra.vertical(toLight);
        tangentialDirection.normalize();

        double optimalRadiusFactor = Math.exp(-Math.abs(distance - preferredCirclingRadius) / 25.0);
        double tangentialStrength = circlingSpeed * lightIntensity * optimalRadiusFactor;

        if (random.nextDouble() < 0.1) {
            tangentialDirection.mult(-1);
        }

        tangentialDirection.mult(tangentialStrength);
        return tangentialDirection;
    }

    private Vector2D getWeakLightAttraction(Agent agent) {
        Vector2D totalAttraction = new Vector2D();

        for (LightSource light : LightSourceManager.getLightSources()) {
            double distance = linearAlgebra.euclideanDistance(agent.position, light.getPosition());

            if (distance > preferredCirclingRadius * 1.5 && distance < light.getRadius() * 6.0) {
                Vector2D attraction = linearAlgebra.sub(light.getPosition(), agent.position);
                attraction.normalize();

                double strength = (light.getIntensity() * 0.5) / (distance + 1);
                attraction.mult(strength);

                totalAttraction.add(attraction);
            }
        }

        return totalAttraction;
    }

    private Vector2D getChaosForce(Agent agent) {
        if (random.nextDouble() < directionChangeFrequency) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double strength = chaosIntensity * agent.MAX_FORCE * 0.3;

            return new Vector2D(
                    Math.cos(angle) * strength,
                    Math.sin(angle) * strength
            );
        }

        return new Vector2D();
    }
}