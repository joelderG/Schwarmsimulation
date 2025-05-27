package main;

import graphics.core.LWJGLWindow;
import graphics.math.Vector2D;
import graphics.utils.POGL;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.GL11.*;

public class SwarmSimulation extends LWJGLWindow {
    public SwarmSimulation(String title, int w, int h) {
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
            POGL.clearBackgroundWithColor(1,1,1,1);

            glColor3f(1.0f,0.5f,0.0f);

            POGL.renderObjectWithForces(640,360,10,new Vector2D(1,5),new Vector2D(5,10));
            Display.update();
            Display.sync(60);
        }
    }

    public static void main(String[] args) {
        new SwarmSimulation("Swarmsimulation",1280,720).start();
    }
}
