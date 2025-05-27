package main;

import engine.core.Window;
import engine.math.Vector2D;
import engine.core.Renderer;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.GL11.*;

public class SimulationApplication extends Window {
    public SimulationApplication(String title, int w, int h) {
        super(title, w, h);
        initDisplay();
    }

    public void renderLoop() {
        // OpenGL setup
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, WIDTH, HEIGHT, 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);

        long start = System.nanoTime();

        // RenderLoop
        while(!Display.isCloseRequested()) {
            double t = (System.nanoTime() - start) / 1e9;
            Renderer.clearBackgroundWithColor(1,1,1,1);

            glColor3f(1.0f,0.5f,0.0f);

            Renderer.renderObjectWithForces(640,360,10,new Vector2D(1,5),new Vector2D(5,10));
            Display.update();
            Display.sync(60);
        }
    }

    public static void main(String[] args) {
        new SimulationApplication("Swarmsimulation",1280,720).start();
    }
}
