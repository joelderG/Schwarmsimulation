package engine.math;

public class Vector2D {
    public double x, y;

    public Vector2D() { this(0, 0); }

    public Vector2D(double x, double y) {
        setX(x);
        setY(y);
    }

    public Vector2D(Vector2D vec) {
        this(vec.x, vec.y);
    }

    public Vector2D(double x, double y, double x2, double y2) {
        this(x2 - x, y2 - y);
    }

    public Vector2D(Vector2D vec1, Vector2D vec2) {
        this(vec2.x - vec1.x, vec2.y - vec1.y);
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setPosition(Vector2D vec) {
        setX(vec.x);
        setY(vec.y);
    }

    public void add(Vector2D vec1) {
        x += vec1.x;
        y += vec1.y;
    }

    public void sub(Vector2D vec1) {
        x -= vec1.x;
        y -= vec1.y;
    }

    public void mult(double s) {
        x *= s;
        y *= s;
    }

    public boolean div(double s) {
        if (s!=0) {
            x /= s;
            y /= s;
            return true;
        }
        return false;
    }

    public boolean isNullvector() {
        return (x == 0 && y == 0);
    }

    public boolean isEqual(Vector2D vec) {
        return (x == vec.x && y == vec.y);
    }

    public boolean isNotEqual(Vector2D vec) {
        return !isEqual(vec);
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public double lengthSquare() {
        return x * x + y * y;
    }

    // TODO: Vergleiche Nullvektorbehandlung
    public void normalize() {
//        if (this.isNullvector()) {
//            setPosition(linearAlgebra.mult(this, (1.0 / this.length() + 0.00001)));
//        } else {
//            setPosition(linearAlgebra.div(this, this.length()));
//        }

        if (this.isNullvector()) {
            throw new IllegalArgumentException("Cannot normalize a null vector.");
        }

        double length = this.length();
        if (length == 0) {
            throw new ArithmeticException("Vector length is zero. Cannot normalize.");
        }

        setPosition(linearAlgebra.div(this, length));
    }

    public void truncate(double max) {
        if (length() > max) {
            normalize();
            mult(max);
        }
    }

    public String toString() {
        return ("(" + x + ", " + y + ")");
    }

    public void show() {
        System.out.println(toString());
    }
}
