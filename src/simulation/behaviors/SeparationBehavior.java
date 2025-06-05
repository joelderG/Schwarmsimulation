package simulation.behaviors;

import engine.math.Vector2D;
import engine.math.linearAlgebra;
import simulation.agents.Agent;

import java.util.List;

public class SeparationBehavior extends Behavior {
    public SeparationBehavior(double weight, double distance) {
        super(weight,distance);
    }

    public Vector2D calculateForce(Agent agent, List<Agent> neighbors) {
        Vector2D steer = new Vector2D();
        int count = 0;

        for (Agent other : neighbors) {
            double distance = linearAlgebra.euclideanDistance(agent.position, other.position);

            if (distance > 0 && distance < influenceDistance) {
                Vector2D diff = linearAlgebra.sub(agent.position, other.position);
                diff.normalize();
                diff.div(distance);
                steer.add(diff);
                count++;
            }
        }

        if (count > 0) {
            steer.div(count);
            steer.normalize();
            steer.mult(agent.MAX_SPEED);
            steer.sub(agent.velocity);
        }

        return steer;
    }
}
