package graphics.objects;

import org.lwjgl.util.vector.Vector3f;

public class faceTriangle {
    public Vector3f vertex = new Vector3f();
    public Vector3f texCoords = new Vector3f();
    public Vector3f normal = new Vector3f();

    public faceTriangle(Vector3f vertex, Vector3f texCoords, Vector3f normal) {
        this.vertex = vertex;
        this.texCoords = texCoords;
        this.normal = normal;
    }
}
