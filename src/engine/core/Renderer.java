package engine.core;

import engine.math.linearAlgebra;
import engine.math.Vector2D;
import engine.objects.renderable.Model;
import engine.objects.geometry.faceTriangle;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import java.io.*;
import static org.lwjgl.opengl.GL11.*;

// improvements
// TODO: maybe check for OpenGL errors in rendering methods
// opengl standard methods

public class Renderer {
    private Renderer() {}

    public static void clearBackgroundWithColor(float r, float g, float b, float a) {
        glClearColor(r,g,b,a);
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public static void setBackgroundColorClearDepth(float r, float g, float b) {
        glClearColor(r,g,b,1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public static void clearColorBuffer() {
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public static void clearDepthBuffer() {
        glClear(GL_DEPTH_BUFFER_BIT);
    }

    public static void clearBuffers() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public static void setColor(float r, float g, float b, float a) {
        glColor4f(r,g,b,a);
    }

    public static void setColor(float r, float g, float b) {
        glColor4f(r,g,b,1.0f);
    }

    public static void resetColor() {
        glColor4f(1.0f,1.0f,1.0f,1.0f);
    }

    public static void pushMatrix() {
        glPushMatrix();
    }

    public static void popMatrix() {
        glPopMatrix();
    }

    public static void loadIdentity() {
        glLoadIdentity();
    }

    public static void translate(float x, float y, float z) {
        glTranslatef(x,y,z);
    }

    public static void rotate(float angle, float x, float y, float z) {
        glRotatef(angle,x,y,z);
    }

    public static void scale(float x, float y, float z) {
        glScalef(x, y, z);
    }

    public static void scale(float scale) {
        glScalef(scale, scale, scale);
    }

    public static void enableDepthTest() {
        glEnable(GL_DEPTH_TEST);
    }

    public static void disableDepthTest() {
        glDisable(GL_DEPTH_TEST);
    }

    public static void enableBlending() {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void disableBlending() {
        glDisable(GL_BLEND);
    }

    public static void setLineWidth(float width) {
        glLineWidth(width);
    }

    public static void setPointSize(float size) {
        glPointSize(size);
    }

    public static void bindTexture(int textureId) {
        glBindTexture(GL_TEXTURE_2D, textureId);
    }

    public static void unbindTexture() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public static void enableTexture2D() {
        glEnable(GL_TEXTURE_2D);
    }

    public static void disableTexture2D() {
        glDisable(GL_TEXTURE_2D);
    }

    public static void setViewport(int x, int y, int width, int height) {
        glViewport(x,y,width,height);
    }

    public static void renderModel(Model model) {
        glBegin(GL_TRIANGLES);
        for (faceTriangle face : model.faces) {
            if (face.normal != null) {
                Vector3f n1 = model.normals.get((int) face.normal.x - 1);
                glNormal3f(n1.x, n1.y, n1.z);
            }
            if (face.texCoords != null) {
                Vector2f t1 = model.texCoords.get((int) face.texCoords.x - 1);
                glTexCoord2f(t1.x, t1.y);
            }
            Vector3f v1 = model.vertices.get((int) face.vertex.x - 1);
            glVertex3f(v1.x, v1.y, v1.z);

            if (face.normal != null) {
                Vector3f n2 = model.normals.get((int) face.normal.y - 1);
                glNormal3f(n2.x, n2.y, n2.z);
            }
            if (face.texCoords != null) {
                Vector2f t2 = model.texCoords.get((int) face.texCoords.y - 1);
                glTexCoord2f(t2.x, t2.y);
            }
            Vector3f v2 = model.vertices.get((int) face.vertex.y - 1);
            glVertex3f(v2.x, v2.y, v2.z);

            if (face.normal != null) {
                Vector3f n3 = model.normals.get((int) face.normal.z - 1);
                glNormal3f(n3.x, n3.y, n3.z);
            }
            if (face.texCoords != null) {
                Vector2f t3 = model.texCoords.get((int) face.texCoords.z - 1);
                glTexCoord2f(t3.x, t3.y);
            }
            Vector3f v3 = model.vertices.get((int) face.vertex.z - 1);
            glVertex3f(v3.x, v3.y, v3.z);
        }
        glEnd();
        glPopMatrix();
    }

    public static void renderModelTransformed(Model model, float x, float y, float z, float rotationAngle, float rotationX, float rotationY, float rotationZ, float scale) {
        pushMatrix();
        translate(x,y,z);
        rotate(rotationAngle, rotationX, rotationY, rotationZ);
        scale(scale);
        renderModel(model);
        popMatrix();
    }

    public static void init() {
        glShadeModel(GL_SMOOTH);
        glColor4f(1.0f,1.0f,1.0f,1.0f);
        glLineWidth(1.0f);
        glPointSize(1.0f);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }

    public static void setup2D(int width, int height) {
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glViewport(0,0,width,height);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0,width,height,0,0,1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }

    public static void setup2DCustom(float left, float right, float bottom, float top) {
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(left, right, bottom, top, -1, 1);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }

    public static void setup3D(int width, int height, float fov, float nearPlane, float farPlane) {
        // Enable depth test for 3D
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glClearDepth(1.0);

        // Enable backface culling for performance
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CCW);

        // Enable blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Enable normalize for proper lighting
        glEnable(GL_NORMALIZE);

        // Set viewport
        glViewport(0, 0, width, height);

        // Set projection matrix for 3D perspective
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        // Calculate perspective
        float aspect = (float) width / (float) height;
        float fH = (float) Math.tan(Math.toRadians(fov) / 2.0) * nearPlane;
        float fW = fH * aspect;
        glFrustum(-fW, fW, -fH, fH, nearPlane, farPlane);

        // Set modelview matrix
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }

    public static void init2D(int width, int height) {
        init();
        setup2D(width, height);

        // Set light gray background like your example
        glClearColor(0.95f, 0.95f, 0.95f, 1.0f);
    }

    public static void init3D(int width, int height) {
        init();
        setup3D(width, height, 45.0f, 0.1f, 1000.0f);

        // Set dark gray background for 3D
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
    }

}
