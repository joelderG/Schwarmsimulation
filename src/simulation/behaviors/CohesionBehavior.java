package simulation.behaviors;

import engine.math.Vector2D;
import engine.math.linearAlgebra;
import simulation.agents.Agent;

import java.util.List;

public class CohesionBehavior extends Behavior {
    public CohesionBehavior(double weight, double distance) {
        super(weight, distance);
    }

    public Vector2D calculateForce(Agent agent, List<Agent> neighbors) {
        Vector2D centerOfMass = new Vector2D();
        int count = 0;

        for (Agent other : neighbors) {
            double distance = linearAlgebra.euclideanDistance(agent.position, other.position);

            if (distance > 0 && distance < influenceDistance) {
                centerOfMass.add(other.position);
                count++;
            }
        }

        if (count > 0) {
            centerOfMass.div(count);
            return seek(agent, centerOfMass);
        }

        return  new Vector2D();
    }

    private Vector2D seek(Agent agent, Vector2D target) {
        Vector2D desired = linearAlgebra.sub(target, agent.position);
        desired.normalize();
        desired.mult(agent.MAX_SPEED);

        return linearAlgebra.sub(desired, agent.velocity);
    }
}
