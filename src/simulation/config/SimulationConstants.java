package simulation.config;

public class SimulationConstants {
    private SimulationConstants() {}

    // Window settings
    public static final int DEFAULT_WINDOW_WIDTH = 1280;
    public static final int DEFAULT_WINDOW_HEIGHT = 720;
    public static final int TARGET_FPS = 60;

    // Agent settings
    public static final double DEFAULT_AGENT_MASS = 1.0;
    public static final double DEFAULT_MAX_SPEED = 50.0;
    public static final double DEFAULT_MAX_FORCE = 100.0;
    public static final double DEFAULT_AGENT_RADIUS = 40.0;
    public static final double DEFAULT_SWARM_DISTANCE = 90.0;
    public static final int DEFAULT_AGENT_COUNT = 50;

    // Behavior weights
    public static final double DEFAULT_SEPARATION_WEIGHT = 3.5;
    public static final double DEFAULT_ALIGNMENT_WEIGHT = 1.75;
    public static final double DEFAULT_COHESION_WEIGHT = 1.0;
    public static final double DEFAULT_SWIMMING_WEIGHT = 0.8;
    public static final double DEFAULT_SWIMMING_AMPLITUDE = 5.0;
    public static final double DEFAULT_SWIMMING_FREQUENCY = 4.0;

    // Obstacle settings
    public static final double DEFAULT_OBSTACLE_RADIUS = 30.0;
    public static final double OBSTACE_AVOIDANCE_WEIGHT = 5.0;
    public static final double OBSTACLE_RADIUS_MIN = 10.0;
    public static final double OBSTACLE_RADIUS_STEP = 5.0;

    // Rendering settings
    public static final int CIRCLE_SEGMENTS = 32;
    public static final int PATH_MAX_POINTS = 50;

    // Weight adjustment settings
    public static final double WEIGHT_ADJUSTMENT_STEP = 0.1;
    public static final double AMPLITUDE_ADJUSTMENT_STEP = 0.5;
    public static final double MIN_WEIGHT = 0.0;
    public static final double MIN_AMPLITUDE = 0.1;

    // Physics settings
    public static final double MAX_TURN_RATE_DEGREES = 180.0;
    public static final double NEIGHBOR_INFLUENCE_RANGE = 20.0;
}
