package simulation.behaviors;

import engine.math.Vector2D;
import simulation.agents.Agent;

import java.util.List;

public abstract class Behavior {
    protected double weight;
    protected double influenceDistance;

    public Behavior(double weight, double influenceDistance) {
        this.weight = weight;
        this.influenceDistance = influenceDistance;
    }

    public abstract Vector2D calculateForce(Agent agent, List<Agent> neighbors);

    public Vector2D getWeightedForce(Agent agent, List<Agent> neighbors) {
        Vector2D force = calculateForce(agent, neighbors);
        force.mult(weight);
        return force;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getInfluenceDistance() {
        return influenceDistance;
    }

    public void setInfluenceDistance(double distance) {
        this.influenceDistance = distance;
    }
}
