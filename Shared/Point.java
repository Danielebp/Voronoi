
/** Represents a point in a 2D space */
public class Point {

    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(String stringValue) {
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

    /**
    Returns a line on which all points are equidistant (have the same distance)
    to the point itself and the other given point
    */
    public Line getEquidistantLine(Point otherPoint) {
        // There is no equidistant line for the same points
        if (x == otherPoint.x && y == otherPoint.y) {
            return null;
        }

        Vector connectingVector = new Vector(this, otherPoint);
        Vector perpendicularVector = connectingVector.getPerpendicularVector();
        Point point = this.addVector(connectingVector.multiply(0.5));

        return new Line(point, perpendicularVector);
    }

    /** Adds vector onto points and returns new point */
    public Point addVector(Vector v) {
        return new Point(x + v.getX(), y + v.getY());
    }

    @Override
    public String toString() {
        return "" + x + " " + y;
    }
}
