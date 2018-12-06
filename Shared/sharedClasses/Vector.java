package sharedClasses;

/** Represents a vector in 2D space */
public class Vector {

    private double x;
    private double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector(Point point1, Point point2) {
        this.x = point2.getX() - point1.getX();
        this.y = point2.getY() - point1.getY();
    }

    public Vector(String stringValue) {
        String[] split = stringValue.split(" ");
        x = Double.valueOf(split[0]);
        y = Double.valueOf(split[1]);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    /** Returns new vector which is perpendicular to this vector */
    public Vector getPerpendicularVector() {
        return new Vector(y, -x);
    }

    /** Multiplies vector with given scalar and returns resulting new vector */
    public Vector multiply(double scalar) {
        return new Vector(x * scalar, y * scalar);
    }

    @Override
    public String toString() {
        return "" + x + " " + y;
    }
}
