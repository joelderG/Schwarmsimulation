package engine.rendering;

import engine.objects.geometry.faceTriangle;
import engine.objects.renderable.Model;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.*;

public class ModelLoader {
    private ModelLoader() {}

    public static Model loadObjModel(String filePath) throws FileNotFoundException, IOException {
        return loadObjModel(new File(filePath));
    }

    public static Model loadObjModel(File file) throws FileNotFoundException, IOException {
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
}
