package engine.rendering;

import engine.objects.geometry.faceTriangle;
import engine.objects.renderable.Model;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ModelLoader {
    private ModelLoader() {}

    public static Model loadObjModel(String filePath) throws ModelLoadException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new ModelLoadException("Model file not found: " + filePath);
        }
        return loadObjModel(path.toFile());
    }

    public static Model loadObjModel(File file) throws ModelLoadException {
        if (!file.exists() || !file.canRead()) {
            throw new ModelLoadException("Cannot read model file: " + file.getAbsolutePath());
        }

        Model model = new Model();
        int lineNumber = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                try {
                    if (line.startsWith("v ")) {
                        parseVertex(line, model);
                    } else if (line.startsWith("vn ")) {
                        parseNormal(line, model);
                    } else if (line.startsWith("vt ")) {
                        parseTextureCoordinate(line, model);
                    } else if (line.startsWith("f ")) {
                        parseFace(line, model);
                    }
                    // Ignore other OBJ elements (materials, groups, etc.)
                } catch (Exception e) {
                    throw new ModelLoadException(
                            String.format("Error parsing line %d in file %s: %s",
                                    lineNumber, file.getName(), e.getMessage()));
                }
            }
        } catch (IOException e) {
            throw new ModelLoadException("IO error reading file: " + file.getAbsolutePath(), e);
        }

        validateModel(model, file.getName());
        return model;
    }

    private static void parseVertex(String line, Model model) throws ModelLoadException {
        String[] parts = line.split("\\s+");
        if (parts.length < 4) {
            throw new ModelLoadException("Invalid vertex format: " + line);
        }

        try {
            float x = Float.parseFloat(parts[1]);
            float y = Float.parseFloat(parts[2]);
            float z = Float.parseFloat(parts[3]);
            model.vertices.add(new Vector3f(x, y, z));
        } catch (NumberFormatException e) {
            throw new ModelLoadException("Invalid vertex coordinates: " + line);
        }
    }

    private static void parseNormal(String line, Model model) throws ModelLoadException {
        String[] parts = line.split("\\s+");
        if (parts.length < 4) {
            throw new ModelLoadException("Invalid normal format: " + line);
        }

        try {
            float x = Float.parseFloat(parts[1]);
            float y = Float.parseFloat(parts[2]);
            float z = Float.parseFloat(parts[3]);
            model.normals.add(new Vector3f(x, y, z));
        } catch (NumberFormatException e) {
            throw new ModelLoadException("Invalid normal coordinates: " + line);
        }
    }

    private static void parseTextureCoordinate(String line, Model model) throws ModelLoadException {
        String[] parts = line.split("\\s+");
        if (parts.length < 3) {
            throw new ModelLoadException("Invalid texture coordinate format: " + line);
        }

        try {
            float u = Float.parseFloat(parts[1]);
            float v = Float.parseFloat(parts[2]);
            model.texCoords.add(new Vector2f(u, v));
        } catch (NumberFormatException e) {
            throw new ModelLoadException("Invalid texture coordinates: " + line);
        }
    }

    private static void parseFace(String line, Model model) throws ModelLoadException {
        String[] parts = line.split("\\s+");

        if (parts.length < 4) {
            throw new ModelLoadException("Face must have at least 3 vertices: " + line);
        }

        // Convert polygons to triangles (fan triangulation)
        List<FaceVertex> vertices = new ArrayList<>();

        for (int i = 1; i < parts.length; i++) {
            vertices.add(parseFaceVertex(parts[i], model));
        }

        // Triangulate the face (simple fan triangulation)
        for (int i = 1; i < vertices.size() - 1; i++) {
            FaceVertex v1 = vertices.get(0);
            FaceVertex v2 = vertices.get(i);
            FaceVertex v3 = vertices.get(i + 1);

            Vector3f vertexIndices = new Vector3f(v1.vertexIndex, v2.vertexIndex, v3.vertexIndex);
            Vector3f texIndices = (v1.texIndex > 0) ?
                    new Vector3f(v1.texIndex, v2.texIndex, v3.texIndex) : null;
            Vector3f normalIndices = (v1.normalIndex > 0) ?
                    new Vector3f(v1.normalIndex, v2.normalIndex, v3.normalIndex) : null;

            model.faces.add(new faceTriangle(vertexIndices, texIndices, normalIndices));
        }
    }

    private static class FaceVertex {
        int vertexIndex;
        int texIndex;
        int normalIndex;

        FaceVertex(int vertexIndex, int texIndex, int normalIndex) {
            this.vertexIndex = vertexIndex;
            this.texIndex = texIndex;
            this.normalIndex = normalIndex;
        }
    }

    private static FaceVertex parseFaceVertex(String vertexStr, Model model) throws ModelLoadException {
        String[] indices = vertexStr.split("/");

        try {
            int vertexIndex = Integer.parseInt(indices[0]);
            int texIndex = 0;
            int normalIndex = 0;

            if (indices.length > 1 && !indices[1].isEmpty()) {
                texIndex = Integer.parseInt(indices[1]);
            }
            if (indices.length > 2 && !indices[2].isEmpty()) {
                normalIndex = Integer.parseInt(indices[2]);
            }

            // Validate indices
            if (vertexIndex < 1 || vertexIndex > model.vertices.size()) {
                throw new ModelLoadException("Vertex index out of bounds: " + vertexIndex);
            }
            if (texIndex > model.texCoords.size()) {
                throw new ModelLoadException("Texture coordinate index out of bounds: " + texIndex);
            }
            if (normalIndex > model.normals.size()) {
                throw new ModelLoadException("Normal index out of bounds: " + normalIndex);
            }

            return new FaceVertex(vertexIndex, texIndex, normalIndex);

        } catch (NumberFormatException e) {
            throw new ModelLoadException("Invalid face vertex format: " + vertexStr);
        }
    }

    private static void validateModel(Model model, String filename) throws ModelLoadException {
        if (model.vertices.isEmpty()) {
            throw new ModelLoadException("Model has no vertices: " + filename);
        }
        if (model.faces.isEmpty()) {
            throw new ModelLoadException("Model has no faces: " + filename);
        }
    }
}
