package simulation.behaviors;

import engine.math.Vector2D;
import simulation.agents.Agent;
import simulation.obstacles.Obstacle;

import java.util.ArrayList;
import java.util.List;

public class FishBehavior {
    private List<Behavior> behaviors;
    private double maxForce;

    public FishBehavior(double maxForce) {
        this.maxForce = maxForce;
        this.behaviors = new ArrayList<>();

        // Initialize with default fish behaviors
        initializeDefaultBehaviors();
    }

    private void initializeDefaultBehaviors() {
        behaviors.add(new SeparationBehavior(3.5, 18.0));
        behaviors.add(new AlignmentBehavior(1.75, 35.0));
        behaviors.add(new CohesionBehavior(1.0, 60.0));
        behaviors.add(new SwimmingBehavior(0.8, 5.0, 6.0));
    }

    public Vector2D calculateSteeringForce(Agent agent, List<Agent> neighbors) {
        Vector2D totalForce = new Vector2D();

        // Calculate and combine all behavior forces
        for (Behavior behavior : behaviors) {
            Vector2D force = behavior.getWeightedForce(agent, neighbors);
            totalForce.add(force);
        }

        // Limit the total force
        if (totalForce.length() > maxForce) {
            totalForce.normalize();
            totalForce.mult(maxForce);
        }

        return totalForce;
    }

    public void setSeparationWeight(double weight) {
        getBehavior(SeparationBehavior.class).setWeight(weight);
    }

    public void setAlignmentWeight(double weight) {
        getBehavior(AlignmentBehavior.class).setWeight(weight);
    }

    public void setCohesionWeight(double weight) {
        getBehavior(CohesionBehavior.class).setWeight(weight);
    }

    public double getSeparationWeight() {
        return getBehavior(SeparationBehavior.class).getWeight();
    }

    public double getAlignmentWeight() {
        return getBehavior(AlignmentBehavior.class).getWeight();
    }

    public double getCohesionWeight() {
        return getBehavior(CohesionBehavior.class).getWeight();
    }

    public void setSeparationDistance(double distance) {
        getBehavior(SeparationBehavior.class).setInfluenceDistance(distance);
    }

    public void setAlignmentDistance(double distance) {
        getBehavior(AlignmentBehavior.class).setInfluenceDistance(distance);
    }

    public void setCohesionDistance(double distance) {
        getBehavior(CohesionBehavior.class).setInfluenceDistance(distance);
    }

    public double getSeparationDistance() {
        return getBehavior(SeparationBehavior.class).getInfluenceDistance();
    }

    public double getAlignmentDistance() {
        return getBehavior(AlignmentBehavior.class).getInfluenceDistance();
    }

    public double getCohesionDistance() {
        return getBehavior(CohesionBehavior.class).getInfluenceDistance();
    }


    public void setSwimmingWeight(double weight) {
        getBehavior(SwimmingBehavior.class).setWeight(weight);
    }

    public double getSwimmingWeight() {
        return getBehavior(SwimmingBehavior.class).getWeight();
    }

    public void setSwimmingAmplitude(double amplitude) {
        ((SwimmingBehavior) getBehavior(SwimmingBehavior.class)).setAmplitude(amplitude);
    }

    public double getSwimmingAmplitude() {
        return ((SwimmingBehavior) getBehavior(SwimmingBehavior.class)).getAmplitude();
    }

    public void setSwimmingFrequency(double frequency) {
        ((SwimmingBehavior) getBehavior(SwimmingBehavior.class)).setFrequency(frequency);
    }

    public double getSwimmingFrequency() {
        return ((SwimmingBehavior) getBehavior(SwimmingBehavior.class)).getFrequency();
    }

    public void addObstacleAvoidance(List<Obstacle> obstacles) {
        behaviors.removeIf(b -> b instanceof ObstacleAvoidanceBehavior);
        behaviors.add(new ObstacleAvoidanceBehavior(5.0, obstacles));
    }

    public void setObstacleAvoidanceWeight(double weight) {
        for (Behavior behavior : behaviors) {
            if (behavior instanceof ObstacleAvoidanceBehavior) {
                behavior.setWeight(weight);
                break;
            }
        }
    }

    public double getObstacleAvoidanceWeight() {
        for (Behavior behavior : behaviors) {
            if (behavior instanceof ObstacleAvoidanceBehavior) {
                return behavior.getWeight();
            }
        }
        return 0.0;
    }

    public void updateObstacles(List<Obstacle> obstacles) {
        for (Behavior behavior : behaviors) {
            if (behavior instanceof ObstacleAvoidanceBehavior) {
                ((ObstacleAvoidanceBehavior) behavior).setObstacles(obstacles);
                break;
            }
        }
    }


    @SuppressWarnings("unchecked")
    private <T extends Behavior> T getBehavior(Class<T> behaviorClass) {
        for (Behavior behavior : behaviors) {
            if (behaviorClass.isInstance(behavior)) {
                return (T) behavior;
            }
        }
        throw new IllegalArgumentException("Behavior of type " + behaviorClass.getSimpleName() + " not found");
    }

    public void addBehavior(Behavior behavior) {
        behaviors.add(behavior);
    }

    public void removeBehavior(Class<? extends Behavior> behaviorClass) {
        behaviors.removeIf(b -> behaviorClass.isInstance(b));
    }

    public boolean hasBehavior(Class<? extends Behavior> behaviorClass) {
        return behaviors.stream().anyMatch(b -> behaviorClass.isInstance(b));
    }

    public List<Behavior> getAllBehaviors() {
        return new ArrayList<>(behaviors);
    }

    public void setMaxForce(double maxForce) {
        this.maxForce = maxForce;
    }

    public double getMaxForce() {
        return maxForce;
    }

    public void resetToDefaults() {
        behaviors.clear();
        initializeDefaultBehaviors();
    }
}

