package main;

import engine.core.Renderer;
import engine.core.Window;
import engine.math.Vector2D;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import simulation.agents.Agent;
import simulation.agents.AgentBuilder;
import simulation.config.SimulationConstants;
import simulation.input.InputHandler;
import simulation.input.InputManager;
import simulation.input.KeyAction;
import simulation.obstacles.Obstacle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulationApplication extends Window implements InputHandler {
    // Core simulation components
    private List<Agent> agents;
    private List<Obstacle> obstacles;
    private InputManager inputManager;
    private Random random = new Random();

    // Sim configs
    private int agentCount = SimulationConstants.DEFAULT_AGENT_COUNT;
    private double obstacleRadius = SimulationConstants.DEFAULT_OBSTACLE_RADIUS;

    // timing
    private double lastTime;
    private double deltaTime;

    // sim state
    private boolean paused = false;
    private boolean showPaths = false;

    public SimulationApplication(String title, int w, int h) {
        super(title, w, h);
        initDisplay();
        initSimulation();
    }

    private void initSimulation() {
        initializeRenderer();
        initializeObstacles();
        initializeAgents();
        initializeInputManager();
        initializeTiming();
        printInstructions();
    }

    private void initializeRenderer() {
        Renderer.init2D(WIDTH, HEIGHT);
    }

    private void initializeObstacles() {
        obstacles = new ArrayList<>();
    }

    private void initializeAgents() {
        agents = new ArrayList<>();
        for (int i = 0; i < agentCount; i++) {
            Agent agent = createRandomAgent();
            agents.add(agent);
        }
    }

    private void initializeInputManager() {
        inputManager = new InputManager(this);
    }

    private void initializeTiming() {
        lastTime = System.nanoTime() / 1e9;
    }

    private Agent createRandomAgent() {
        return new AgentBuilder()
                .position(generateRandomPosition())
                .worldBounds(WIDTH, HEIGHT)
                .obstacles(obstacles)
                .build();
    }

    private Vector2D generateRandomPosition() {
        return new Vector2D(
                random.nextDouble() * WIDTH,
                random.nextDouble() * HEIGHT
        );
    }

    private void printInstructions() {
        System.out.println("=== SWARM SIMULATION ===");
        System.out.println("Agents: " + agentCount);
        System.out.println("Controls:");
        for (KeyAction action : KeyAction.values()) {
            System.out.println("  " + action.getDescription());
        }
        System.out.println("  Left Click - Add Agent");
        System.out.println("  Right Click - Add Obstacle");
    }

    @Override
    public void renderLoop() {
        while (!Display.isCloseRequested()) {
            updateTiming();
            inputManager.processInput(HEIGHT);

            if (!paused) { // Fixed: was "If" instead of "if"
                updateSimulation();
            }

            render();
            updateDisplay();
        }
    }

    private void updateTiming() {
        double currentTime = System.nanoTime() / 1e9;
        deltaTime = currentTime - lastTime;
        lastTime = currentTime;
    }

    private void updateDisplay() {
        Display.update();
        Display.sync(SimulationConstants.TARGET_FPS); // Use constant instead of hardcoded 60
    }

    private void updateSimulation() {
        for (Agent agent : agents) {
            List<Agent> neighbors = findNeighbors(agent);
            agent.update(deltaTime, neighbors); // Fixed: was "uodate" instead of "update"
        }
    }

    private List<Agent> findNeighbors(Agent agent) {
        List<Agent> neighbors = new ArrayList<>();
        for (Agent other : agents) {
            if (other != agent && agent.canSee(other)) {
                neighbors.add(other);
            }
        }
        return neighbors;
    }

    private void render() {
        Renderer.clearBuffers();
        renderObstacles();
        renderAgents();
        renderUI();
    }

    private void renderObstacles() { // Made private instead of public
        for (Obstacle obstacle : obstacles) {
            obstacle.render();
        }
    }

    private void renderAgents() {
        for (Agent agent : agents) {
            if (showPaths) {
                agent.renderPath();
            }
            agent.render();
        }
    }

    private void renderUI() {
        String title = createWindowTitle();
        Display.setTitle(title);
    }

    private String createWindowTitle() {
        if (!agents.isEmpty()) {
            Agent firstAgent = agents.get(0);
            return String.format("Swarm Simulation - Agents: %d | %s | Sep: %.1f, Ali: %.1f, Coh: %.1f, Swim: %.1f",
                    agents.size(),
                    paused ? "PAUSED" : "RUNNING",
                    firstAgent.getSeparationWeight(),
                    firstAgent.getAlignmentWeight(),
                    firstAgent.getCohesionWeight(),
                    firstAgent.getSwimmingWeight()
            );
        } else {
            return String.format("Swarm Simulation - Agents: %d | %s",
                    agents.size(),
                    paused ? "PAUSED" : "RUNNING"
            );
        }
    }

    // InputHandler implementation with @Override annotations
    @Override
    public void handleKeyAction(KeyAction action) {
        switch (action) {
            case PAUSE:
                togglePause();
                break;
            case SHOW_PATHS:
                togglePaths();
                break;
            case RESET:
                resetSimulation();
                break;
            case CLEAR_OBSTACLES:
                clearObstacles();
                break;
            case INCREASE_SEPARATION:
                adjustSeparationWeight(SimulationConstants.WEIGHT_ADJUSTMENT_STEP);
                break;
            case DECREASE_SEPARATION:
                adjustSeparationWeight(-SimulationConstants.WEIGHT_ADJUSTMENT_STEP);
                break;
            case INCREASE_ALIGNMENT:
                adjustAlignmentWeight(SimulationConstants.WEIGHT_ADJUSTMENT_STEP);
                break;
            case DECREASE_ALIGNMENT:
                adjustAlignmentWeight(-SimulationConstants.WEIGHT_ADJUSTMENT_STEP);
                break;
            case INCREASE_COHESION:
                adjustCohesionWeight(SimulationConstants.WEIGHT_ADJUSTMENT_STEP);
                break;
            case DECREASE_COHESION:
                adjustCohesionWeight(-SimulationConstants.WEIGHT_ADJUSTMENT_STEP);
                break;
            case INCREASE_SWIMMING:
                adjustSwimmingWeight(SimulationConstants.WEIGHT_ADJUSTMENT_STEP);
                break;
            case DECREASE_SWIMMING:
                adjustSwimmingWeight(-SimulationConstants.WEIGHT_ADJUSTMENT_STEP);
                break;
            case INCREASE_AMPLITUDE:
                adjustSwimmingAmplitude(SimulationConstants.AMPLITUDE_ADJUSTMENT_STEP);
                break;
            case DECREASE_AMPLITUDE:
                adjustSwimmingAmplitude(-SimulationConstants.AMPLITUDE_ADJUSTMENT_STEP);
                break;
            case INCREASE_OBSTACLE_SIZE:
                adjustObstacleRadius(SimulationConstants.OBSTACLE_RADIUS_STEP);
                break;
            case DECREASE_OBSTACLE_SIZE:
                adjustObstacleRadius(-SimulationConstants.OBSTACLE_RADIUS_STEP);
                break;
            case EXIT:
                exitSimulation();
                break;
        }
    }

    @Override
    public void handleLeftClick(int x, int y) {
        addAgentAtPosition(x, y);
    }

    @Override
    public void handleRightClick(int x, int y) {
        addObstacleAtPosition(x, y);
    }

    // Rest of the methods remain the same...
    private void togglePause() {
        paused = !paused;
        System.out.println("Simulation " + (paused ? "paused" : "resumed"));
    }

    private void togglePaths() {
        showPaths = !showPaths;
        System.out.println("Paths " + (showPaths ? "on" : "off"));
    }

    private void resetSimulation() {
        agents.clear();
        initializeAgents();
        System.out.println("Simulation reset");
    }

    private void clearObstacles() {
        obstacles.clear();
        updateAllAgentsObstacles();
        System.out.println("All obstacles cleared");
    }

    private void exitSimulation() {
        Display.destroy();
        System.exit(0);
    }

    private void addAgentAtPosition(int x, int y) {
        Agent newAgent = new AgentBuilder()
                .position(new Vector2D(x, y))
                .worldBounds(WIDTH, HEIGHT)
                .obstacles(obstacles)
                .build();

        syncAgentWeights(newAgent);
        agents.add(newAgent);
        System.out.println("Agent added at (" + x + ", " + y + ")");
    }

    private void syncAgentWeights(Agent newAgent) {
        if (!agents.isEmpty()) {
            Agent referenceAgent = agents.get(0);
            newAgent.setSeparationWeight(referenceAgent.getSeparationWeight());
            newAgent.setAlignmentWeight(referenceAgent.getAlignmentWeight());
            newAgent.setCohesionWeight(referenceAgent.getCohesionWeight());
            newAgent.setSwimmingWeight(referenceAgent.getSwimmingWeight());
        }
    }

    private void addObstacleAtPosition(int x, int y) {
        Obstacle newObstacle = new Obstacle(new Vector2D(x, y), obstacleRadius);
        obstacles.add(newObstacle);
        updateAllAgentsObstacles();
        System.out.println("Obstacle added at (" + x + ", " + y + ") with radius " + obstacleRadius);
    }

    private void updateAllAgentsObstacles() {
        for (Agent agent : agents) {
            agent.updateObstacles(obstacles);
        }
    }

    private void adjustSeparationWeight(double delta) {
        adjustAgentWeight(delta, "Separation",
                agent -> agent.getSeparationWeight(),
                (agent, weight) -> agent.setSeparationWeight(weight));
    }

    private void adjustAlignmentWeight(double delta) {
        adjustAgentWeight(delta, "Alignment",
                agent -> agent.getAlignmentWeight(),
                (agent, weight) -> agent.setAlignmentWeight(weight));
    }

    private void adjustCohesionWeight(double delta) {
        adjustAgentWeight(delta, "Cohesion",
                agent -> agent.getCohesionWeight(),
                (agent, weight) -> agent.setCohesionWeight(weight));
    }

    private void adjustSwimmingWeight(double delta) {
        adjustAgentWeight(delta, "Swimming",
                agent -> agent.getSwimmingWeight(),
                (agent, weight) -> agent.setSwimmingWeight(weight));
    }

    private void adjustAgentWeight(double delta, String weightName,
                                   java.util.function.Function<Agent, Double> getter,
                                   java.util.function.BiConsumer<Agent, Double> setter) {
        if (!agents.isEmpty()) {
            Agent firstAgent = agents.get(0);
            double currentWeight = getter.apply(firstAgent);
            double newWeight = Math.max(SimulationConstants.MIN_WEIGHT, currentWeight + delta);

            for (Agent agent : agents) {
                setter.accept(agent, newWeight);
            }
            System.out.printf("%s Weight: %.1f\n", weightName, newWeight);
        }
    }

    private void adjustSwimmingAmplitude(double delta) {
        if (!agents.isEmpty()) {
            Agent firstAgent = agents.get(0);
            double currentAmplitude = firstAgent.getSwimmingAmplitude();
            double newAmplitude = Math.max(SimulationConstants.MIN_AMPLITUDE, currentAmplitude + delta);

            for (Agent agent : agents) {
                agent.setSwimmingAmplitude(newAmplitude);
            }
            System.out.printf("Swimming Amplitude: %.1f\n", newAmplitude);
        }
    }

    private void adjustObstacleRadius(double delta) {
        obstacleRadius = Math.max(SimulationConstants.OBSTACLE_RADIUS_MIN, obstacleRadius + delta);
        System.out.printf("Obstacle Radius: %.1f\n", obstacleRadius);
    }

    public static void main(String[] args) {
        try {
            new SimulationApplication("Swarm Simulation",
                    SimulationConstants.DEFAULT_WINDOW_WIDTH,
                    SimulationConstants.DEFAULT_WINDOW_HEIGHT).start();
        } catch (Exception e) {
            System.err.println("Error starting simulation:");
            e.printStackTrace();
        }
    }
}
