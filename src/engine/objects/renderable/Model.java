package engine.objects.renderable;

import engine.objects.geometry.faceTriangle;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Model {
    public List<Vector3f> vertices = new ArrayList<>();
    public List<Vector3f> normals = new ArrayList<>();
    public List<Vector2f> texCoords = new ArrayList<>();
    public List<faceTriangle> faces = new ArrayList<>();

    public float size;

    public Model() {

    }
}
