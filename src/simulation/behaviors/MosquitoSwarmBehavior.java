
package simulation.behaviors;

import engine.math.Vector2D;
import engine.math.linearAlgebra;
import simulation.agents.Agent;
import simulation.environment.LightSourceManager;

import java.util.List;
import java.util.Random;

public class MosquitoSwarmBehavior {
    // Gewichtungen für chaotisches Mückenverhalten
    private double separationWeight = 3.0;
    private double cohesionWeight = 0.3;
    private double randomWeight = 2.5;
    private double lightAttractionWeight = 4.0;

    private static Random random = new Random();

    // Zusätzliche Parameter für wildes Verhalten
    private double chaosIntensity = 1.5;
    private double directionChangeFrequency = 0.1; // 10% Chance pro Frame für abrupte Richtungsänderung

    public Vector2D getWeightedForce(Agent agent, List<Agent> neighbors) {
        Vector2D totalForce = new Vector2D();

        // Separation (starke Abstand-Haltung)
        Vector2D separation = getSeparation(agent, neighbors);
        separation.mult(separationWeight);
        totalForce.add(separation);

        // Schwache Cohesion (Mücken bilden keine starken Gruppen)
        Vector2D cohesion = getCohesion(agent, neighbors);
        cohesion.mult(cohesionWeight);
        totalForce.add(cohesion);

        // Starke zufällige Bewegung für chaotisches Verhalten
        Vector2D randomMovement = getWildRandomMovement(agent);
        randomMovement.mult(randomWeight);
        totalForce.add(randomMovement);

        // Lichtanziehung
        Vector2D lightAttraction = getLightAttraction(agent);
        lightAttraction.mult(lightAttractionWeight);
        totalForce.add(lightAttraction);

        // Gelegentliche abrupte Richtungsänderungen
        Vector2D chaosForce = getChaosForce(agent);
        totalForce.add(chaosForce);

        return totalForce;
    }

    private Vector2D getSeparation(Agent agent, List<Agent> neighbors) {
        Vector2D separationForce = new Vector2D();
        int count = 0;

        for (Agent neighbor : neighbors) {
            double distance = agent.getDistanceTo(neighbor);
            // Größerer Separationsbereich für wilde Mücken
            if (distance > 0 && distance < agent.SWARM_DISTANCE * 0.8) {
                Vector2D diff = linearAlgebra.sub(agent.position, neighbor.position);
                if (!diff.isNullvector()) {
                    diff.normalize();
                    // Exponentiell stärkere Kraft bei geringerer Entfernung
                    double strength = 1.0 / (distance * distance + 0.1);
                    diff.mult(strength);
                    separationForce.add(diff);
                    count++;
                }
            }
        }

        if (count > 0) {
            separationForce.mult(1.0 / count);
            separationForce.normalize();
        }

        return separationForce;
    }

    private Vector2D getCohesion(Agent agent, List<Agent> neighbors) {
        Vector2D centerOfMass = new Vector2D();
        int count = 0;

        // Nur sehr nahe Nachbarn für schwache Cohesion
        for (Agent neighbor : neighbors) {
            double distance = agent.getDistanceTo(neighbor);
            if (distance > 0 && distance < agent.SWARM_DISTANCE * 0.6) {
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

    private Vector2D getWildRandomMovement(Agent agent) {
        // Reduced intensity to minimize bias accumulation
        double intensity = 2.0;

        Vector2D randomForce = new Vector2D();

        // Pure centered Gaussian random movement
        randomForce.add(new Vector2D(
                (random.nextGaussian() - 0.0) * intensity,  // Explicitly center at 0
                (random.nextGaussian() - 0.0) * intensity
        ));

        // Reduced impulse chance and strength
        if (random.nextDouble() < 0.03) { // Reduced from 5% to 3%
            double angle = random.nextDouble() * 2 * Math.PI;
            Vector2D impulse = new Vector2D(
                    Math.cos(angle) * intensity * 1.5,  // Reduced from 2.0
                    Math.sin(angle) * intensity * 1.5
            );
            randomForce.add(impulse);
        }

        // Remove time-based oscillation that could cause drift
        // Instead use agent-specific random oscillation
        double agentSeed = agent.position.x + agent.position.y; // Agent-specific seed
        double phase1 = agentSeed * 0.1;
        double phase2 = agentSeed * 0.15;

        Vector2D oscillation = new Vector2D(
                Math.sin(phase1) * intensity * 0.3,  // Reduced intensity
                Math.cos(phase2) * intensity * 0.3
        );
        randomForce.add(oscillation);

        return randomForce;
    }

    private Vector2D getChaosForce(Agent agent) {
        // Gelegentliche abrupte Richtungsänderungen
        if (random.nextDouble() < directionChangeFrequency) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double strength = chaosIntensity * agent.MAX_FORCE * 0.8;

            return new Vector2D(
                    Math.cos(angle) * strength,
                    Math.sin(angle) * strength
            );
        }

        return new Vector2D();
    }

    private Vector2D getLightAttraction(Agent agent) {
        return LightSourceManager.getAttractionForce(agent);
    }
}
