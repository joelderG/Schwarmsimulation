package simulation.agents;

import engine.math.Vector2D;

public class AgentBuilder {
    private Vector2D position = new Vector2D();
    private double mass = SimulationConstants.DEFAULT_AGENT_MASS;
    private double maxSpeed = SimulationConstants.DEFAULT_MAX_SPEED;
    private double maxForce = SimulationConstants.DEFAULT_MAX_FORCE;
    private double radius = SimulationConstants.DEFAULT_AGENT_RADIUS;
    private double swarmDistance = SimulationConstants.DEFAULT_SWARM_DISTANCE;
    private int worldWidth = SimulationConstants.DEFAULT_WINDOW_WIDTH;
    private int worldHeight = SimulationConstants.DEFAULT_WINDOW_HEIGHT;
    private MosquitoType mosquitoType = null; // null means random selection

    public AgentBuilder position(Vector2D position) {
        this.position = position;
        return this;
    }

    public AgentBuilder mass(double mass) {
        this.mass = mass;
        return this;
    }

    public AgentBuilder maxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
        return this;
    }

    public AgentBuilder maxForce(double maxForce) {
        this.maxForce = maxForce;
        return this;
    }

    public AgentBuilder radius(double radius) {
        this.radius = radius;
        return this;
    }

    public AgentBuilder swarmDistance(double swarmDistance) {
        this.swarmDistance = swarmDistance;
        return this;
    }

    public AgentBuilder worldBounds(int width, int height) {
        this.worldWidth = width;
        this.worldHeight = height;
        return this;
    }

    public AgentBuilder mosquitoType(MosquitoType type) {
        this.mosquitoType = type;
        return this;
    }

    public Agent build() {
        Agent agent;

        if (mosquitoType != null) {
            agent = new Agent(position, mosquitoType);
        } else {
            agent = new Agent(position); // Random type selection
        }

        agent.setWorldBounds(worldWidth, worldHeight);
        agent.setSwarmDistance(swarmDistance);

        agent.MASS = mass;
        agent.MAX_SPEED = maxSpeed;
        agent.MAX_FORCE = maxForce;
        agent.radius = (float) radius;

        return agent;
    }
}