package graphics.utils;

import graphics.math.linearAlgebra;
import graphics.math.Vector2D;
import graphics.objects.Model;
import graphics.objects.faceTriangle;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import java.io.*;
import static org.lwjgl.opengl.GL11.*;

// improvements
// TODO: maybe check for OpenGL errors in rendering methods

public class POGL {
    private POGL() {}

    public static void renderCircle(float x, float y, float step, float radius) {
        glBegin(GL_TRIANGLE_FAN);
        glVertex2f(x, y); // Mittelpunkt

        for(int angle = 0; angle <= 360; angle += step) {
            double radians = Math.toRadians(angle);
            glVertex2f(x + (float) Math.cos(radians) * radius,
                    y + (float) Math.sin(radians) * radius);
        }
        glEnd();
    }

    public static void renderArrow(float x, float y, int off, float winkel, int size) {
        glLoadIdentity();
        glTranslated(x, y, 0);

        glRotatef(winkel, 0, 0, 1);
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

    public static void clearBackgroundWithColor(float r, float g, float b, float a) {
        glClearColor(r,g,b,a);
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public static void setBackgroundColorClearDepth(float r, float g, float b) {
        glClearColor(r,g,b,1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public static Model loadModel(File file) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        Model model = new Model();
        String line;
        String[] lineElements;
        float x, y, z;
        Vector3f vertexIndices = null;
        Vector3f texCoordsIndices = null;
        Vector3f normalIndices = null;

        while((line = reader.readLine()) != null) {
            if (line.startsWith("v ")) {
                lineElements = line.split(" ");
                x = Float.valueOf(lineElements[1]);
                y = Float.valueOf(lineElements[2]);
                z = Float.valueOf(lineElements[3]);
                model.vertices.add(new Vector3f(x, y, z));
            } else if (line.startsWith("vn ")) {
                lineElements = line.split(" ");
                x = Float.valueOf(lineElements[1]);
                y = Float.valueOf(lineElements[2]);
                z = Float.valueOf(lineElements[3]);
                model.normals.add(new Vector3f(x, y, z));
            } else if (line.startsWith("vt ")) {
                lineElements = line.split(" ");
                x = Float.valueOf(lineElements[1]);
                y = Float.valueOf(lineElements[2]);
                model.texCoords.add(new Vector2f(x, y));
            } else if (line.startsWith("f ")) {
                vertexIndices 		= null;
                texCoordsIndices 	= null;
                normalIndices 		= null;

                lineElements = line.split(" ");
                if (line.contains("/") && lineElements[1].split("/").length > 1) {
                    vertexIndices = new Vector3f(Float.valueOf(lineElements[1].split("/")[0]),
                            Float.valueOf(lineElements[2].split("/")[0]),
                            Float.valueOf(lineElements[3].split("/")[0]));
                    texCoordsIndices = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[1]),
                            Float.valueOf(lineElements[2].split("/")[1]),
                            Float.valueOf(lineElements[3].split("/")[1]));
                    if (lineElements[1].split("/").length == 3) {
                        normalIndices = new Vector3f(Float.valueOf(lineElements[1].split("/")[2]),
                                Float.valueOf(lineElements[2].split("/")[2]),
                                Float.valueOf(lineElements[3].split("/")[2]));
                    }
                } else {
                    // nur drei Vertices f�r ein Dreieck vorhanden
                    vertexIndices = new Vector3f(
                            Float.valueOf(lineElements[1]),
                            Float.valueOf(lineElements[2]),
                            Float.valueOf(lineElements[3]));
                }
                model.faces.add(new faceTriangle(vertexIndices, texCoordsIndices, normalIndices));
            }
        }
        reader.close();
        return model;
    }

    public static void renderObject(Model model) {
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

    public static void renderObjectWithForces(float x, float y, int radius, Vector2D velocity, Vector2D acceleration) {
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
        renderArrow(x, y, off, (float)angle, 15);

        // Acceleration
        off = radius + 1 + (int)(acceleration.length()/10);
        angle = linearAlgebra.angleDegree(acceleration, new Vector2D(1,0));
        if (acceleration.y<0)
            angle = 180 + (180-angle);

        glColor4f(1, 0, 0, 1);
        renderArrow(x, y, off, (float)angle, 15);

    }
}
