package engine.math;

public class Vector3D {
    public double x, y, z;

    public Vector3D() {
        this(0, 0, 0);
    }

    public Vector3D(double x, double y, double z) {
        setX(x);
        setY(y);
        setZ(z);
    }

    public Vector3D(Vector3D vec) {
        this(vec.x, vec.y, vec.z);
    }

    public Vector3D(double x, double y, double z, double x2, double y2, double z2) {
        this(x2 - x, y2 - y, z2 - z);
    }

    public Vector3D(Vector3D a, Vector3D b) {
        this(b.x - a.x, b.y - a.y, b.z - a.z);
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getZ() {
        return z;
    }

    public void setPosition(Vector3D vec) {
        x = vec.x;
        y = vec.y;
        z = vec.z;
    }

    public boolean isNullvector() {
        return (x == 0 && y == 0 && z == 0);
    }

    public void add(Vector3D vec) {
        x += vec.x;
        y += vec.y;
        z += vec.z;
    }

    public void sub(Vector3D vec) {
        x -= vec.x;
        y -= vec.y;
        z -= vec.z;
    }

    public void mult(double s) {
        x *= s;
        y *= s;
        z *= s;
    }

    public boolean isEqual(Vector3D vec) {
        return (x == vec.x && y == vec.y && z == vec.z);
    }

    public boolean isNotEqual(Vector3D vec) {
        return !isEqual(vec);
    }

    public double length() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    public double lengthSquare() {
        return x*x + y*y + z*z;
    }

    // TODO: vergleiche Nullvektorbehandlung
    public void normalize() {
//        if (this.isNullvector())
//            setPosition(linearAlgebra.mult(this, (1.0 / this.length() + 0.00001)));
//        else
//            setPosition(linearAlgebra.div(this, this.length()));

        if (this.isNullvector()) {
            throw new IllegalArgumentException("Cannot normalize a null vector.");
        }

        double length = this.length();
        if (length == 0) {
            throw new ArithmeticException("Vector length is zero. Cannot normalize.");
        }

        setPosition(linearAlgebra.div(this, this.length()));
    }

    public void truncate(double max) {
        if (length() > max) {
            normalize();
            mult(max);
        }
    }

    public String toString() {
        return ("("+x+", "+y+", "+z+")");
    }

    public void show() {
        System.out.println(toString());
    }
}
