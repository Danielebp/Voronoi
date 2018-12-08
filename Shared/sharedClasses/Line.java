import java.io.*;

/** Line in 2D space represented by a starting point and a vector */
public class Line implements java.io.Serializable{

    private Point point;
    private Vector vector;

    public Line(Point point, Vector vector) {
        this.point = point;
        this.vector = vector;
    }

    public Line(String stringValue) {
        String[] split = stringValue.split("_");
        point = new Point(split[0]);
        vector = new Vector(split[1]);
    }

    @Override
    public String toString() {
        return point.toString() + "_" + vector.toString();
    }

    public Point findIntersection(Line line) {
    	//TODO: find intersection between 2 lines

    	return null;
    }

    public Point findIntersectionWithSide(Point a, Point b) {
        // Followed instructions from:
        // https://www.geeksforgeeks.org/program-for-point-of-intersection-of-two-lines/
    	Vector abVector = new Vector(a, b);

        // Represent line as a1x + b1y = c1
        double a1 = vector.getY();
        double b1 = -vector.getX();
        double c1 = vector.getY() * point.getX() - vector.getX() * point.getY();

        // Represent line from a to b as a2x + b2y = c2
        double a2 = abVector.getY();
        double b2 = -abVector.getX();
        double c2 = abVector.getY() * (a.getX()) - abVector.getX() * (a.getY());

        double determinant = a1 * b2 - a2 * b1;

        if (determinant != 0) {
            double x = roundToPlaces((b2 * c1 - b1 * c2)/determinant, 3);
            double y = roundToPlaces((a1 * c2 - a2 * c1)/determinant, 3);

            double ax = roundToPlaces(a.getX(), 3);
            double ay = roundToPlaces(a.getY(), 3);
            double bx = roundToPlaces(b.getX(), 3);
            double by = roundToPlaces(b.getY(), 3);
            		
            // Check if intersection is in between point a and b
            if (Math.min(ax, bx) <= x && Math.max(ax, bx) >= x &&
                Math.min(ay, by) <= y && Math.max(ay, by) >= y) {
                return new Point(x, y);
            }
        }

        return null;
    }
    
    public double roundToPlaces(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
