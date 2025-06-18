package engine.rendering;

import engine.math.Vector2D;
import engine.math.linearAlgebra;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

public class PrimitiveRenderer {
    private PrimitiveRenderer() {}

    public static void renderCircle(float x, float y, float radius, int segments) {
        if (segments < 3) segments = 3; // Minimum segments for a circle

        glBegin(GL_TRIANGLE_FAN);
        glVertex2f(x, y); // Center point

        float angleStep = (float)(2 * Math.PI / segments);
        for(int i = 0; i <= segments; i++) {
            float angle = i * angleStep;
            glVertex2f(x + (float)Math.cos(angle) * radius,
                    y + (float)Math.sin(angle) * radius);
        }
        glEnd();
    }

    public static void renderCircleOutline(float x, float y, float radius, int segments) {
        if (segments < 3) segments = 3;

        glBegin(GL_LINE_LOOP);
        float angleStep = (float)(2 * Math.PI / segments);
        for(int i = 0; i < segments; i++) {
            float angle = i * angleStep;
            glVertex2f(x + (float)Math.cos(angle) * radius,
                    y + (float)Math.sin(angle) * radius);
        }
        glEnd();
    }

    public static void renderLine2D(float x1, float y1, float x2, float y2) {
        glBegin(GL_LINES);
        glVertex2f(x1, y1);
        glVertex2f(x2, y2);
        glEnd();
    }

    public static void renderArrow(float x, float y, int off, float angle, int size) {
        glLoadIdentity();
        glTranslated(x, y, 0);

        glRotatef(angle, 0, 0, 1);
        glTranslated(off, 0, 0);
        glScaled(size, size, size);

        glBegin(GL_LINES);
        glVertex3d(  0f,  0f, 0);
        glVertex3d(-off/15., 0, 0);
        glEnd();

        glBegin(GL_TRIANGLES);
        glVertex3d(  0f,  .2f, 0);
        glVertex3d(  0f, -.2f, 0);
        glVertex3d( .5f,   0f, 0);
        glEnd();
    }

    public static void renderStar(float x, float y, int points, float outerRadius, float innerRadius) {
        if (points < 3) return;

        float angleStep = (float)(Math.PI / points);

        glBegin(GL_TRIANGLE_FAN);
        glVertex2f(x, y); // Center point

        for (int i = 0; i <= points * 2; i++) {
            float angle = i * angleStep;
            float radius = (i % 2 == 0) ? outerRadius : innerRadius;

            float px = x + (float)(Math.cos(angle) * radius);
            float py = y + (float)(Math.sin(angle) * radius);
            glVertex2f(px, py);
        }
        glEnd();
    }

    public static void renderRectangle(float x, float y, float width, float height) {
        float halfWidth = width / 2;
        float halfHeight = height / 2;

        glBegin(GL_QUADS);
        glVertex2f(x - halfWidth, y - halfHeight);
        glVertex2f(x + halfWidth, y - halfHeight);
        glVertex2f(x + halfWidth, y + halfHeight);
        glVertex2f(x - halfWidth, y + halfHeight);
        glEnd();
    }

    public static void renderSphere(float radius, int latSteps, int lonSteps) {
        if (latSteps < 2) latSteps = 2;
        if (lonSteps < 3) lonSteps = 3;

        for (int i = 0; i < latSteps; i++) {
            float theta1 = (float)(Math.PI * i / latSteps);
            float theta2 = (float)(Math.PI * (i + 1) / latSteps);

            glBegin(GL_TRIANGLE_STRIP);
            for (int j = 0; j <= lonSteps; j++) {
                float phi = (float)(2 * Math.PI * j / lonSteps);

                // First vertex
                float x1 = (float)(Math.sin(theta1) * Math.cos(phi));
                float y1 = (float)Math.cos(theta1);
                float z1 = (float)(Math.sin(theta1) * Math.sin(phi));

                // Second vertex
                float x2 = (float)(Math.sin(theta2) * Math.cos(phi));
                float y2 = (float)Math.cos(theta2);
                float z2 = (float)(Math.sin(theta2) * Math.sin(phi));

                glNormal3f(x1, y1, z1); // Normal for lighting
                glVertex3f(x1 * radius, y1 * radius, z1 * radius);

                glNormal3f(x2, y2, z2);
                glVertex3f(x2 * radius, y2 * radius, z2 * radius);
            }
            glEnd();
        }
    }

    public static void renderCube(float size) {
        float half = size / 2;

        glBegin(GL_QUADS);

        // Front face
        glNormal3f(0, 0, 1);
        glVertex3f(-half, -half, half);
        glVertex3f( half, -half, half);
        glVertex3f( half,  half, half);
        glVertex3f(-half,  half, half);

        // Back face
        glNormal3f(0, 0, -1);
        glVertex3f(-half, -half, -half);
        glVertex3f(-half,  half, -half);
        glVertex3f( half,  half, -half);
        glVertex3f( half, -half, -half);

        // Left face
        glNormal3f(-1, 0, 0);
        glVertex3f(-half, -half, -half);
        glVertex3f(-half, -half,  half);
        glVertex3f(-half,  half,  half);
        glVertex3f(-half,  half, -half);

        // Right face
        glNormal3f(1, 0, 0);
        glVertex3f(half, -half, -half);
        glVertex3f(half,  half, -half);
        glVertex3f(half,  half,  half);
        glVertex3f(half, -half,  half);

        // Bottom face
        glNormal3f(0, -1, 0);
        glVertex3f(-half, -half, -half);
        glVertex3f( half, -half, -half);
        glVertex3f( half, -half,  half);
        glVertex3f(-half, -half,  half);

        // Top face
        glNormal3f(0, 1, 0);
        glVertex3f(-half, half, -half);
        glVertex3f(-half, half,  half);
        glVertex3f( half, half,  half);
        glVertex3f( half, half, -half);

        glEnd();
    }

    public static void renderPyramid(float baseSize, float height) {
        float halfBase = baseSize / 2;

        // Base (square)
        glBegin(GL_QUADS);
        glNormal3f(0, -1, 0); // Normal pointing down
        glVertex3f(-halfBase, 0, -halfBase);
        glVertex3f( halfBase, 0, -halfBase);
        glVertex3f( halfBase, 0,  halfBase);
        glVertex3f(-halfBase, 0,  halfBase);
        glEnd();

        // Calculate normals for side faces
        glBegin(GL_TRIANGLES);

        // Front face
        glNormal3f(0, 0.5f, 0.5f);
        glVertex3f(0, height, 0);
        glVertex3f(-halfBase, 0, halfBase);
        glVertex3f(halfBase, 0, halfBase);

        // Right face
        glNormal3f(0.5f, 0.5f, 0);
        glVertex3f(0, height, 0);
        glVertex3f(halfBase, 0, halfBase);
        glVertex3f(halfBase, 0, -halfBase);

        // Back face
        glNormal3f(0, 0.5f, -0.5f);
        glVertex3f(0, height, 0);
        glVertex3f(halfBase, 0, -halfBase);
        glVertex3f(-halfBase, 0, -halfBase);

        // Left face
        glNormal3f(-0.5f, 0.5f, 0);
        glVertex3f(0, height, 0);
        glVertex3f(-halfBase, 0, -halfBase);
        glVertex3f(-halfBase, 0, halfBase);

        glEnd();
    }

    public static void renderCircleWithForces(float x, float y, int radius, Vector2D velocity, Vector2D acceleration) {
        glLoadIdentity();
        glTranslated(x, y, 0);

        glColor4f(0.05f, 0.39f, 0.51f, 1.0f);
        renderCircle(0, 0, 5, radius);
        glColor4f(0.66f, 0.87f, 0.95f, 1.0f);
        // TODO: radius-2 hardcoded
        renderCircle(0, 0, 5, radius-2);

        // velocity
        // TODO: velocity.length()/5 hardcoded
        int off = radius + 1 + (int)(velocity.length()/5);
        double angle = linearAlgebra.angleDegree(velocity, new Vector2D(1,0));

        // angle correction
        if (velocity.y<0)
            angle = 180 + (180-angle);

        glColor4f(0.35f, 0.63f, 0.73f, 1.0f);
        //renderArrow(x, y, off, (float)angle, 15);

        // Acceleration
        off = radius + 1 + (int)(acceleration.length()/10);
        angle = linearAlgebra.angleDegree(acceleration, new Vector2D(1,0));
        if (acceleration.y<0)
            angle = 180 + (180-angle);

        glColor4f(1, 0, 0, 1);
        //renderArrow(x, y, off, (float)angle, 15);
    }

    public static void renderMosquito(float x, float y, float size, float rotation) {
        glPushMatrix();

        // Transform to mosquito position and rotation
        glTranslatef(x, y, 0);
        glRotatef(rotation, 0, 0, 1);

        float bodyLength = size * 1.2f;
        float bodyWidth = size * 0.3f;
        float wingLength = size * 0.8f;
        float wingWidth = size * 0.4f;

        // === WINGS (behind body) ===
        // Left wing
        glBegin(GL_TRIANGLE_FAN);
        glVertex2f(-bodyLength * 0.1f, bodyWidth * 0.5f); // Wing attachment point
        glVertex2f(-wingLength * 0.3f, wingWidth + bodyWidth * 0.5f);
        glVertex2f(-wingLength * 0.8f, wingWidth * 0.8f + bodyWidth * 0.5f);
        glVertex2f(-wingLength * 0.9f, bodyWidth * 0.2f);
        glVertex2f(-wingLength * 0.6f, bodyWidth * 0.3f);
        glEnd();

        // Right wing
        glBegin(GL_TRIANGLE_FAN);
        glVertex2f(-bodyLength * 0.1f, -bodyWidth * 0.5f); // Wing attachment point
        glVertex2f(-wingLength * 0.3f, -wingWidth - bodyWidth * 0.5f);
        glVertex2f(-wingLength * 0.8f, -wingWidth * 0.8f - bodyWidth * 0.5f);
        glVertex2f(-wingLength * 0.9f, -bodyWidth * 0.2f);
        glVertex2f(-wingLength * 0.6f, -bodyWidth * 0.3f);
        glEnd();

        // === LEGS ===
        glBegin(GL_LINES);
        // Front legs
        glVertex2f(bodyLength * 0.2f, bodyWidth * 0.5f);
        glVertex2f(bodyLength * 0.1f, bodyWidth * 1.2f);
        glVertex2f(bodyLength * 0.2f, -bodyWidth * 0.5f);
        glVertex2f(bodyLength * 0.1f, -bodyWidth * 1.2f);

        // Middle legs
        glVertex2f(-bodyLength * 0.1f, bodyWidth * 0.5f);
        glVertex2f(-bodyLength * 0.2f, bodyWidth * 1.3f);
        glVertex2f(-bodyLength * 0.1f, -bodyWidth * 0.5f);
        glVertex2f(-bodyLength * 0.2f, -bodyWidth * 1.3f);

        // Back legs
        glVertex2f(-bodyLength * 0.4f, bodyWidth * 0.5f);
        glVertex2f(-bodyLength * 0.5f, bodyWidth * 1.1f);
        glVertex2f(-bodyLength * 0.4f, -bodyWidth * 0.5f);
        glVertex2f(-bodyLength * 0.5f, -bodyWidth * 1.1f);
        glEnd();

        // === BODY ===
        // Main body (abdomen)
        glBegin(GL_TRIANGLE_FAN);
        glVertex2f(-bodyLength * 0.2f, 0); // Center
        int segments = 8;
        for (int i = 0; i <= segments; i++) {
            float angle = (float)(Math.PI * 2 * i / segments);
            float bodyX = -bodyLength * 0.2f + (float)Math.cos(angle) * bodyLength * 0.5f;
            float bodyY = (float)Math.sin(angle) * bodyWidth * 0.5f;
            glVertex2f(bodyX, bodyY);
        }
        glEnd();

        // Thorax (middle section)
        glBegin(GL_TRIANGLE_FAN);
        glVertex2f(bodyLength * 0.1f, 0);
        for (int i = 0; i <= segments; i++) {
            float angle = (float)(Math.PI * 2 * i / segments);
            float thoraxX = bodyLength * 0.1f + (float)Math.cos(angle) * bodyLength * 0.3f;
            float thoraxY = (float)Math.sin(angle) * bodyWidth * 0.4f;
            glVertex2f(thoraxX, thoraxY);
        }
        glEnd();

        // === HEAD ===
        // Head circle
        renderCircle(bodyLength * 0.45f, 0, bodyWidth * 0.6f, 6);

        // === PROBOSCIS (mosquito "needle") ===
        glBegin(GL_LINES);
        glVertex2f(bodyLength * 0.45f, 0);
        glVertex2f(bodyLength * 0.8f, 0);
        glEnd();

        // === ANTENNAE ===
        glBegin(GL_LINES);
        // Left antenna
        glVertex2f(bodyLength * 0.45f, bodyWidth * 0.2f);
        glVertex2f(bodyLength * 0.6f, bodyWidth * 0.4f);
        // Right antenna
        glVertex2f(bodyLength * 0.45f, -bodyWidth * 0.2f);
        glVertex2f(bodyLength * 0.6f, -bodyWidth * 0.4f);
        glEnd();

        glPopMatrix();
    }

    public static void renderMosquito(float x, float y, float size, float velocityX, float velocityY) {
        float rotation = 0;
        if (velocityX != 0 || velocityY != 0) {
            rotation = (float)(Math.atan2(velocityY, velocityX) * 180.0 / Math.PI);
        }
        renderMosquito(x, y, size, rotation);
    }
}
