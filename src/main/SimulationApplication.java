package main;

import engine.core.Renderer;
import engine.core.Window;
import engine.math.Vector2D;
import engine.math.linearAlgebra;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import simulation.agents.Agent;
import simulation.agents.AgentBuilder;
import simulation.agents.SimulationConstants;
import simulation.environment.LightSource;
import simulation.environment.LightSourceManager;
import simulation.environment.WindManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulationApplication extends Window {
    private List<Agent> mosquitos;
    private Random random;
    private long lastTime;

    // Wind tracking
    private Vector2D currentMousePosition = new Vector2D();

    public SimulationApplication() {
        super("Mückenschwarm Simulation",
                SimulationConstants.DEFAULT_WINDOW_WIDTH,
                SimulationConstants.DEFAULT_WINDOW_HEIGHT);

        mosquitos = new ArrayList<>();
        random = new Random();
        initDisplay();
        initMosquitos();
    }

    private void initMosquitos() {
        mosquitos = new ArrayList<>();

        for (int i = 0; i < SimulationConstants.DEFAULT_MOSQUITO_COUNT; i++) {
            Vector2D randomPos = new Vector2D(
                    random.nextDouble() * WIDTH,
                    random.nextDouble() * HEIGHT
            );

            Agent mosquito = new AgentBuilder()
                    .position(randomPos)
                    .worldBounds(WIDTH, HEIGHT)
                    .build();

            mosquitos.add(mosquito);
        }
    }

    public void start() {
        renderLoop();
    }

    @Override
    public void renderLoop() {
        // Zurück zu 2D-Rendering
        Renderer.init2D(WIDTH, HEIGHT);
        lastTime = System.nanoTime();

        while (!Display.isCloseRequested()) {
            long currentTime = System.nanoTime();
            double deltaTime = (currentTime - lastTime) / 1_000_000_000.0;
            lastTime = currentTime;

            handleInput(deltaTime);
            update(deltaTime);
            render();

            Display.update();
            Display.sync(60);
        }

        Display.destroy();
    }

    private void handleInput(double deltaTime) {
        // Update mouse position
        int mouseX = Mouse.getX();
        int mouseY = HEIGHT - Mouse.getY(); // LWJGL Y-coordinate conversion
        currentMousePosition = new Vector2D(mouseX, mouseY);

        // Left click + mouse movement creates wind
        boolean leftMousePressed = Mouse.isButtonDown(0);
        WindManager.updateWind(currentMousePosition, leftMousePressed, deltaTime);

        // Right click for light sources - now smaller!
        if (Mouse.isButtonDown(1)) {
            LightSource newLight = new LightSource(
                    new Vector2D(currentMousePosition),
                    60.0,
                    25.0
            );
            LightSourceManager.addLightSource(newLight);
        }
    }

    private void update(double deltaTime) {
        // Update light sources for flickering
        LightSourceManager.updateAll(deltaTime);

        // Update each mosquito
        for (Agent mosquito : mosquitos) {
            List<Agent> neighbors = findNeighbors(mosquito);
            mosquito.update(deltaTime, neighbors, currentMousePosition);
        }
    }

    private List<Agent> findNeighbors(Agent agent) {
        List<Agent> neighbors = new ArrayList<>();

        for (Agent other : mosquitos) {
            if (other != agent && agent.canSee(other)) {
                neighbors.add(other);
            }
        }

        return neighbors;
    }

    private void render() {
        Renderer.clearBuffers();
        Renderer.clearBackgroundWithColor(0.1f, 0.1f, 0.2f, 1.0f);

        // Render wind visualization
        renderWindVisualization();

        // Render light sources
        LightSourceManager.renderAll();

        // Render mosquitos
        Renderer.setColor(0.3f, 0.2f, 0.1f, 0.9f);
        for (Agent mosquito : mosquitos) {
            mosquito.render();
        }

        Renderer.resetColor();
    }


    private void renderWindVisualization() {
        if (WindManager.hasWind()) {
            Vector2D windDir = WindManager.getWindDirection();
            double windStrength = WindManager.getWindStrength();

            if (!windDir.isNullvector() && windStrength > 5.0) {
                // Draw wind indicator at mouse position
                Renderer.setColor(0.3f, 0.6f, 1.0f, 0.7f);

                Vector2D windEnd = linearAlgebra.add(currentMousePosition,
                        linearAlgebra.mult(windDir, windStrength * 0.5));

                // Draw wind arrow
                engine.rendering.PrimitiveRenderer.renderLine2D(
                        (float) currentMousePosition.x, (float) currentMousePosition.y,
                        (float) windEnd.x, (float) windEnd.y
                );

                // Draw wind circle to show affected area
                Renderer.setColor(0.3f, 0.6f, 1.0f, 0.2f);
                engine.rendering.PrimitiveRenderer.renderCircleOutline(
                        (float) currentMousePosition.x, (float) currentMousePosition.y,
                        100, 16
                );
            }
        }
    }

    public static void main(String[] args) {
        new SimulationApplication().start();
    }
}