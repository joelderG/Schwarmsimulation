package simulation.behaviors;

import engine.math.Vector2D;
import simulation.agents.Agent;
import simulation.obstacles.Obstacle;

import java.util.List;

public class ObstacleAvoidanceBehavior extends Behavior {
    private List<Obstacle> obstacles;

    public ObstacleAvoidanceBehavior(double weight, List<Obstacle> obstacles) {
        super(weight, 0.0);
        this.obstacles = obstacles;
    }

    public Vector2D calculateForce(Agent agent, List<Agent> neighbors) {
        Vector2D avoidanceForce = new Vector2D();

        for (Obstacle obstacle : obstacles) {
            Vector2D force = obstacle.getAvoidanceForce(agent.position, agent.radius);
            avoidanceForce.add(force);
        }

        return avoidanceForce;
    }

    public void setObstacles(List<Obstacle> obstacles) {
        this.obstacles = obstacles;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }
}
