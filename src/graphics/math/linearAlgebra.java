package graphics.math;

public class linearAlgebra {
    private linearAlgebra() {};

    public static Vector2D add(Vector2D vec1, Vector2D vec2) {
        return new Vector2D(vec1.x + vec2.x, vec1.y + vec2.y);
    }

    public static Vector3D add(Vector3D vec1, Vector3D vec2) {
        return new Vector3D(vec1.x + vec2.x, vec1.y + vec2.y, vec1.z + vec2.z);
    }

    public static Vector2D sub(Vector2D vec1, Vector2D vec2) {
        return new Vector2D(vec1.x - vec2.x, vec1.y - vec2.y);
    }

    public static Vector3D sub(Vector3D vec1, Vector3D vec2) {
        return new Vector3D(vec1.x - vec2.x, vec1.y - vec2.y, vec1.z - vec2.z);
    }

    public static Vector2D mult(Vector2D vec, double s) {
        return new Vector2D(vec.x * s, vec.y * s);
    }

    public static Vector3D mult(Vector3D vec, double s) {
        return new Vector3D(vec.x * s, vec.y * s, vec.z * s);
    }

    public static Vector2D mult(double s, Vector2D vec) {
        return new Vector2D(mult(vec,s));
    }

    public static Vector3D mult(double s, Vector3D vec) {
        return new Vector3D(mult(vec, s));
    }

    public static Vector2D div(Vector2D vec, double s) {
        return (s != 0) ? new Vector2D(vec.x / s, vec.y / s) : new Vector2D(0,0);
    }

    public static Vector3D div(Vector3D vec, double s) {
        return (s != 0) ? new Vector3D(vec.x / s, vec.y / s, vec.z / s) : new Vector3D(0, 0, 0);
    }

    public static Vector2D div(double s, Vector2D vec) {
        return div(vec,s);
    }

    public static Vector3D div(double s, Vector3D vec) {
        return div(vec,s);
    }

    public boolean isEqual(Vector2D vec1, Vector2D vec2) {
        return (vec1.x == vec2.x && vec1.y == vec2.y);
    }

    public boolean isEqual(Vector3D vec1, Vector3D vec2) {
        return (vec1.x == vec2.x && vec1.y == vec2.y && vec1.z == vec2.z);
    }

    public static double lengthSquare(Vector2D vec) {
        return vec.x * vec.x + vec.y * vec.y;
    }

    public static double lengthSquare(Vector3D vec) {
        return vec.x * vec.x + vec.y * vec.y + vec.z * vec.z;
    }

    public static double length(Vector2D vec) {
        return Math.sqrt(lengthSquare(vec));
    }

    public static double length(Vector3D vec) {
        return Math.sqrt(lengthSquare(vec));
    }

    public static double euclideanDistance(Vector2D vec1, Vector2D vec2) {
        return length(sub(vec2, vec1));
    }

    public static double euclideanDistance(Vector3D vec1, Vector3D vec2) {
        return length(sub(vec2, vec1));
    }

    public static Vector2D normalize(Vector2D vec) {
        return (vec.isNullvector()) ? new Vector2D(mult(vec, (1.0 / vec.length() + 0.00001)))
                : new Vector2D(div(vec, vec.length()));
    }

    public static Vector3D normalize(Vector3D vec) {
        return (vec.isNullvector()) ? new Vector3D(mult(vec, (1.0 / vec.length() + 0.00001)))
                : new Vector3D(div(vec, vec.length()));
    }

    public static Vector3D crossProduct(Vector3D vec1, Vector3D vec2) {
        return new Vector3D(vec1.y * vec2.z - vec1.z * vec2.y, vec1.z * vec2.x - vec1.x * vec2.z,
                vec1.x * vec2.y - vec1.y * vec2.x);
    }

    public static double dotProduct(Vector2D vec1, Vector2D vec2) {
        return vec1.x * vec2.x + vec1.y * vec2.y;
    }

    public static double dotProduct(Vector3D vec1, Vector3D vec2) {
        return vec1.x * vec2.x + vec1.y * vec2.y + vec1.z * vec2.z;
    }

    public static double angleRad(Vector2D vec1, Vector2D vec2) {
        return Math.acos(cosineFormula(vec1, vec2));
    }

    public static double cosineFormula(Vector2D vec1, Vector2D vec2) {
        return dotProduct(vec1, vec2) / (vec1.length() * vec2.length());
    }

    public static double cosineFormula(Vector3D vec1, Vector3D vec2) {
        return dotProduct(vec1, vec2) / (vec1.length() * vec2.length());
    }

    public static double sineFormula(Vector2D vec1, Vector2D vec2) {
        return determinant(vec1, vec2) / (vec1.length() * vec2.length());
    }

    public static double angleRad(Vector3D vec1, Vector3D vec2) {
        return Math.acos(dotProduct(vec1, vec2) / (vec1.length() * vec2.length()));
    }

    public static double angleRadFast(Vector2D vec1, Vector2D vec2) {
        return Math.acos(dotProduct(vec1, vec2) / Math.sqrt(vec1.lengthSquare() * vec2.lengthSquare()));
    }

    public static double angleRadFast(Vector3D vec1, Vector3D vec2) {
        return Math.acos(dotProduct(vec1, vec2) / Math.sqrt(vec1.lengthSquare() * vec2.lengthSquare()));
    }

    public static double angleDegree(Vector2D vec1, Vector2D vec2) {
        return radToDegree(Math.acos(dotProduct(vec1, vec2) / (vec1.length() * vec2.length())));
    }

    public static double angleDegree(Vector3D vec1, Vector3D vec2) {
        return radToDegree(Math.acos(dotProduct(vec1, vec2) / (vec1.length() * vec2.length())));
    }

    public static double determinant(Vector2D v, Vector2D w) {
        return v.x * w.y - v.y * w.x;
    }

    public static double radToDegree(double rad) {
        return 180 * rad / Math.PI;
    }

    public static double degreeToRad(double degree) {
        return Math.PI * degree / 180;
    }

    public static Vector2D abs(Vector2D vec) {
        return new Vector2D(Math.abs(vec.x), Math.abs(vec.y));
    }

    public static Vector3D abs(Vector3D vec) {
        return new Vector3D(Math.abs(vec.x), Math.abs(vec.y), Math.abs(vec.z));
    }

    public static Vector2D truncate(Vector2D vec, double max) {
        Vector2D newVec = new Vector2D(vec);
        if (newVec.length() > max) {
            newVec.normalize();
            newVec.mult(max);
        }
        return newVec;
    }

    public static Vector2D rotate(Vector2D vec, double degree) {
        double rad = degreeToRad(degree);
        return (new Vector2D(Math.cos(rad) * vec.x - Math.sin(rad) * vec.y,
                Math.sin(rad) * vec.x + Math.cos(rad) * vec.y));
    }

    public static Vector2D vertical(Vector2D vec) {
        Vector2D newVec = new Vector2D(vec);
        return rotate(newVec, 90);
    }

    public static double clamp(double x, double min, double max) {
        if (x < min)
            return min;
        else if (x > max)
            return max;
        return x;
    }

    public static double barycentricInterpolation(Vector2D P, Vector2D A, Vector2D B, Vector2D C, double m_a, double m_b, double m_c) {
        double denom = 1./((B.x-A.x) * (C.y-B.y) - (B.y-A.y) * (C.x-B.x));
        double u = ((B.x-P.x) * (C.y-P.y) - (C.x-P.x) * (B.y-P.y)) * denom;
        double v = ((C.x-P.x) * (A.y-P.y) - (A.x-P.x) * (C.y-P.y)) * denom;
        double w = 1. - u - v;
        return u*m_a+v*m_b+w*m_c;
    }

    public static void show(Vector2D vec) {
        System.out.println("(" + vec.x + ", " + vec.y + ")");
    }

    public static void show(Vector3D vec) {
        System.out.println("(" + vec.x + ", " + vec.y + ", " + vec.z + ")");
    }
}
