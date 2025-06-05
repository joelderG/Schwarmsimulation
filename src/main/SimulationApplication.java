package main;

import engine.core.Renderer;
import engine.core.Window;
import engine.math.Vector2D;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import simulation.agents.Agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulationApplication extends Window {
    // Simulation
    private List<Agent> agents;
    private int agentCount = 3;
    private Random random = new Random();

    // Timing
    private double lastTime;
    private double deltaTime;

    // Simulation State
    private boolean paused = false;
    private boolean showPaths = false;

    // UI Parameter (können später mit echtem UI ersetzt werden)
    private double separationWeight = 2.0;
    private double alignmentWeight = 1.0;
    private double cohesionWeight = 1.0;

    public SimulationApplication(String title, int w, int h) {
        super(title, w, h);
        initDisplay();
        initSimulation();
    }

    private void initSimulation() {
        // Renderer für 2D setup
        Renderer.init2D(WIDTH, HEIGHT);

        // Agenten erstellen
        agents = new ArrayList<>();
        for (int i = 0; i < agentCount; i++) {
            Vector2D randomPos = new Vector2D(
                    random.nextDouble() * WIDTH,
                    random.nextDouble() * HEIGHT
            );

            Agent agent = new Agent(randomPos);
            agent.setWorldBounds(WIDTH, HEIGHT);
            agents.add(agent);
            System.out.println("Position des Agenten: " + agent.position);
        }

        lastTime = System.nanoTime() / 1e9;

        System.out.println("=== Schwarmsimulation gestartet ===");
        System.out.println("Agenten: " + agentCount);
        System.out.println("Steuerung:");
        System.out.println("  SPACE - Pause/Resume");
        System.out.println("  P - Pfade ein/aus");
        System.out.println("  R - Reset");
        System.out.println("  1/2 - Separation +/-");
        System.out.println("  3/4 - Alignment +/-");
        System.out.println("  5/6 - Cohesion +/-");
        System.out.println("  ESC - Beenden");
    }

    @Override
    public void renderLoop() {
        while (!Display.isCloseRequested()) {
            double currentTime = System.nanoTime() / 1e9;
            deltaTime = currentTime - lastTime;
            lastTime = currentTime;

            // Input verarbeiten
            handleInput();

            // Simulation updaten (wenn nicht pausiert)
            if (!paused) {
                updateSimulation();
            }

            // Rendern
            render();

            // Display updaten
            Display.update();
            Display.sync(60); // 60 FPS
        }
    }

    private void handleInput() {
        // Keyboard input
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) { // Key pressed
                switch (Keyboard.getEventKey()) {
                    case Keyboard.KEY_SPACE:
                        paused = !paused;
                        System.out.println("Simulation " + (paused ? "pausiert" : "fortgesetzt"));
                        break;

                    case Keyboard.KEY_P:
                        showPaths = !showPaths;
                        System.out.println("Pfade " + (showPaths ? "an" : "aus"));
                        break;

                    case Keyboard.KEY_R:
                        resetSimulation();
                        break;

                    case Keyboard.KEY_1:
                        changeSeparationWeight(0.1);
                        break;
                    case Keyboard.KEY_2:
                        changeSeparationWeight(-0.1);
                        break;

                    case Keyboard.KEY_3:
                        changeAlignmentWeight(0.1);
                        break;
                    case Keyboard.KEY_4:
                        changeAlignmentWeight(-0.1);
                        break;

                    case Keyboard.KEY_5:
                        changeCohesionWeight(0.1);
                        break;
                    case Keyboard.KEY_6:
                        changeCohesionWeight(-0.1);
                        break;

                    case Keyboard.KEY_ESCAPE:
                        Display.destroy();
                        System.exit(0);
                        break;
                }
            }
        }

        // Mouse input (für zukünftige Features wie Agent hinzufügen)
        while (Mouse.next()) {
            if (Mouse.getEventButtonState()) {
                int mouseX = Mouse.getEventX();
                int mouseY = HEIGHT - Mouse.getEventY(); // Y-Koordinate umkehren

                if (Mouse.getEventButton() == 0) { // Linke Maustaste
                    addAgentAtPosition(mouseX, mouseY);
                }
            }
        }
    }

    private void updateSimulation() {
        // Jeden Agent updaten
        for (Agent agent : agents) {
            // Finde Nachbarn für diesen Agent
            List<Agent> neighbors = findNeighbors(agent);

            // Update den Agent
            agent.update(deltaTime, neighbors);
        }
    }

    private List<Agent> findNeighbors(Agent agent) {
        List<Agent> neighbors = new ArrayList<>();

        for (Agent other : agents) {
            if (other != agent) {
                double distance = engine.math.linearAlgebra.euclideanDistance(
                        agent.position, other.position
                );

                // Nur Agenten in Sichtweite als Nachbarn betrachten
                if (distance <= agent.SWARM_DISTANCE) {
                    neighbors.add(other);
                }
            }
        }

        return neighbors;
    }

    private void render() {
        // Hintergrund löschen
        Renderer.clearBuffers();

        // Alle Agenten rendern
        for (Agent agent : agents) {
            // Pfad rendern (wenn aktiviert)
            if (showPaths) {
                agent.renderPath();
            }

            // Agent rendern
            agent.render();
        }

        // Simple Text-Info rendern (mit OpenGL immediate mode)
        renderUI();
    }

    private void renderUI() {
        // Einfache Text-Ausgabe in der Konsole für Debug-Info
        // (Echtes UI könnte später hinzugefügt werden)

        // Status in Fenstertitel anzeigen
        String title = String.format("Schwarmsimulation - Agenten: %d | %s | Sep: %.1f, Ali: %.1f, Coh: %.1f",
                agents.size(),
                paused ? "PAUSIERT" : "LÄUFT",
                separationWeight,
                alignmentWeight,
                cohesionWeight
        );

        Display.setTitle(title);
    }

    private void resetSimulation() {
        agents.clear();

        for (int i = 0; i < agentCount; i++) {
            Vector2D randomPos = new Vector2D(
                    random.nextDouble() * WIDTH,
                    random.nextDouble() * HEIGHT
            );

            Agent agent = new Agent(randomPos);
            agent.setWorldBounds(WIDTH, HEIGHT);
            updateAgentWeights(agent);
            agents.add(agent);
        }

        System.out.println("Simulation zurückgesetzt");
    }

    private void addAgentAtPosition(int x, int y) {
        Agent newAgent = new Agent(new Vector2D(x, y));
        newAgent.setWorldBounds(WIDTH, HEIGHT);
        updateAgentWeights(newAgent);
        agents.add(newAgent);

        System.out.println("Agent hinzugefügt at (" + x + ", " + y + ")");
    }

    private void changeSeparationWeight(double delta) {
        separationWeight = Math.max(0, separationWeight + delta);
        updateAllAgentWeights();
        System.out.printf("Separation Weight: %.1f\n", separationWeight);
    }

    private void changeAlignmentWeight(double delta) {
        alignmentWeight = Math.max(0, alignmentWeight + delta);
        updateAllAgentWeights();
        System.out.printf("Alignment Weight: %.1f\n", alignmentWeight);
    }

    private void changeCohesionWeight(double delta) {
        cohesionWeight = Math.max(0, cohesionWeight + delta);
        updateAllAgentWeights();
        System.out.printf("Cohesion Weight: %.1f\n", cohesionWeight);
    }

    private void updateAllAgentWeights() {
        for (Agent agent : agents) {
            updateAgentWeights(agent);
        }
    }

    private void updateAgentWeights(Agent agent) {
        agent.setSeparationWeight(separationWeight);
        agent.setAlignmentWeight(alignmentWeight);
        agent.setCohesionWeight(cohesionWeight);
    }

    public static void main(String[] args) {
        try {
            new SimulationApplication("Schwarmsimulation", 1280, 720).start();
        } catch (Exception e) {
            System.err.println("Fehler beim Starten der Simulation:");
            e.printStackTrace();
        }
    }
}
