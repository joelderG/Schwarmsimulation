package simulation.behaviors;

import engine.math.Vector2D;
import engine.math.linearAlgebra;
import simulation.agents.Agent;

import java.util.List;

public class AlignmentBehavior extends Behavior {
    public AlignmentBehavior(double weight, double distance) {
        super(weight, distance);
    }

    public Vector2D calculateForce(Agent agent, List<Agent> neighbors) {
        Vector2D averageVel = new Vector2D();
        int count = 0;

        for (Agent other : neighbors) {
            double distance = linearAlgebra.euclideanDistance(agent.position, other.position);

            if (distance > 0 && distance < influenceDistance) {
                averageVel.add(other.velocity);
                count++;
            }
        }

        if (count > 0) {
            averageVel.div(count);
            averageVel.normalize();
            averageVel.mult(agent.MAX_SPEED);

            Vector2D steer = linearAlgebra.sub(averageVel, agent.velocity);
            return steer;
        }

        return new Vector2D();
    }
}
