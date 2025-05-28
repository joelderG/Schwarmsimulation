package main;

import engine.core.Window;
import org.lwjgl.opengl.Display;

public class SimulationApplication extends Window {
    public SimulationApplication(String title, int w, int h) {
        super(title, w, h);
        initDisplay();
    }

    public void renderLoop() {
        long start = System.nanoTime();

        // RenderLoop
        while(!Display.isCloseRequested()) {
            double t = (System.nanoTime() - start) / 1e9;



            Display.update();
            Display.sync(60);
        }
    }

    public static void main(String[] args) {
        new SimulationApplication("Swarmsimulation",1280,720).start();
    }
}
