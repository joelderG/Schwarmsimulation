package engine.core;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

// improvements
// TODO: methods for handling input (keyboard, mouse) within LWJGLWindow
// or providing interfaces/callbacks for input handling
// TODO: improve error handling -> throw custom exceptions or using a logging mechanism

public abstract class Window {
    public int WIDTH, HEIGHT;
    public String TITLE;

    public Window() {
        this("Window", 1280, 720);
    }

    public Window(String title, int width, int height) {
        this.TITLE = title;
        this.WIDTH = width;
        this.HEIGHT = height;
    }

    public void initDisplay() {
        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.setTitle(TITLE);
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        renderLoop();
        Display.destroy();
        System.exit(0);
    }

    public abstract void renderLoop();
}
