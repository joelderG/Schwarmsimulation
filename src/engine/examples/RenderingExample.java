package engine.examples;

import engine.core.Renderer;
import engine.core.Window;
import engine.objects.renderable.Model;
import engine.rendering.ModelLoader;
import engine.rendering.PrimitiveRenderer;
import org.lwjgl.opengl.Display;

import java.io.IOException;

public class RenderingExample extends Window {
    private final int WINDOW_CENTER_WIDTH = WIDTH / 2;
    private final int WINDOW_CENTER_HEIGHT = HEIGHT / 2;
    private int rotation;

    private Model loadedModel;

    public RenderingExample(String title, int width, int height) {
        super(title, width, height);
        initDisplay();
    }

    public void renderLoop() {
        // modelloader for 3D
        loadModel("assets/models/LowPolyFish.obj");

        // initial setup for 2D
        // setup2Dview();

        // initial setup for 3D
        setup3Dview();

        while(!Display.isCloseRequested()) {
            Renderer.clearBuffers();

            rotation += 1.0f; // Rotate objects
            if (rotation > 360) rotation = 0;

            // shows different 2D primitives
            // render2DScene();

            // paints model in 3D
            //showModel();

            // shows different 3D objects
            renderMultiple3DObjects();

            Display.update();
            Display.sync(60);
        }
        Display.destroy();
    }

    private void setup2Dview() {
        Renderer.init2D(WIDTH, HEIGHT);
    }

    private void render2DScene() {
        Renderer.clearBackgroundWithColor(0.9f, 0.9f, 0.9f, 1.0f);

        // Draw various 2D primitives
        Renderer.setColor(1.0f, 0.2f, 0.2f); // Red
        PrimitiveRenderer.renderCircle(150, 150, 50, 32);

        Renderer.setColor(0.2f, 1.0f, 0.2f); // Green
        PrimitiveRenderer.renderRectangle(400, 150, 100, 80);

        Renderer.setColor(0.2f, 0.2f, 1.0f); // Blue
        PrimitiveRenderer.renderStar(650, 150, 5, 40, 20);

        // Draw some outlines
        Renderer.setColor(0.0f, 0.0f, 0.0f); // Black
        Renderer.setLineWidth(2.0f);
        PrimitiveRenderer.renderCircleOutline(150, 400, 60, 32);

        // Draw connecting lines
        Renderer.setColor(0.5f, 0.5f, 0.5f); // Gray
        Renderer.setLineWidth(1.0f);
        PrimitiveRenderer.renderLine2D(150, 150, 400, 150);
        PrimitiveRenderer.renderLine2D(400, 150, 650, 150);

        // Animated rotating star
        Renderer.pushMatrix();
        Renderer.translate(400, 400, 0);
        Renderer.rotate(rotation, 0, 0, 1);
        Renderer.setColor(1.0f, 0.5f, 0.0f); // Orange
        PrimitiveRenderer.renderStar(0, 0, 6, 35, 15);
        Renderer.popMatrix();
    }

    private void setup3Dview() {
        Renderer.init3D(WIDTH, HEIGHT);
        Renderer.loadIdentity();
        Renderer.translate(0,0,-10);
    }

    private void loadModel(String path) {
        try {
            loadedModel = ModelLoader.loadObjModel(path);
        } catch (IOException e) {
            e.printStackTrace();
            loadedModel = null;
        }
    }

    private void showModel() {
        if (loadedModel != null) {
            Renderer.pushMatrix();
            Renderer.translate(0, 0, 0);
            Renderer.rotate(rotation, 0, 1, 0);
            Renderer.setColor(0.8f, 0.8f, 0.2f);
            Renderer.renderModel(loadedModel);
            Renderer.popMatrix();
        }
    }

    private void renderMultiple3DObjects() {
        // Spinning cube
        Renderer.pushMatrix();
        Renderer.translate(-3, 0, 0);
        Renderer.rotate(rotation, 1, 1, 0);
        Renderer.setColor(1.0f, 0.3f, 0.3f);
        PrimitiveRenderer.renderCube(1.5f);
        Renderer.popMatrix();

        // Bouncing sphere
        float bounceHeight = (float)(Math.sin(Math.toRadians(rotation * 2)) * 2);
        Renderer.pushMatrix();
        Renderer.translate(0, bounceHeight, 0);
        Renderer.setColor(0.3f, 1.0f, 0.3f);
        PrimitiveRenderer.renderSphere(0.8f, 16, 16);
        Renderer.popMatrix();

        // Rotating pyramid
        Renderer.pushMatrix();
        Renderer.translate(3, -1, 0);
        Renderer.rotate(rotation, 0, 1, 0);
        Renderer.setColor(0.3f, 0.3f, 1.0f);
        PrimitiveRenderer.renderPyramid(1.5f, 2.0f);
        Renderer.popMatrix();

        // Orbiting small spheres
        for (int i = 0; i < 4; i++) {
            float angle = rotation + (i * 90);
            float x = (float)(Math.cos(Math.toRadians(angle)) * 4);
            float z = (float)(Math.sin(Math.toRadians(angle)) * 4);

            Renderer.pushMatrix();
            Renderer.translate(x, 2, z);
            Renderer.setColor(1.0f, 1.0f, 0.3f);
            PrimitiveRenderer.renderSphere(0.3f, 8, 8);
            Renderer.popMatrix();
        }
    }

    public static void main(String[] args) {
        new RenderingExample("RenderingExample", 1280, 720).start();
    }
}
