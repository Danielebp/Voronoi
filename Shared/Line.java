
/** Line in 2D space represented by a starting point and a vector */
public class Line {

    private Point point;
    private Vector vector;

    public Line(Point point, Vector vector) {
        this.point = point;
        this.vector = vector;
    }

    public Line(String stringValue) {
        String[] split = stringValue.split("|");
        point = new Point(split[0]);
        vector = new Vector(split[1]);
    }
    
    public Point findIntersection(Line line) {
    	//TODO: find intersection between 2 lines
    	
    	return null;
    }
    
    public Point findIntersectionWithSide(Point a, Point b) {
    	//TODO: find intersection between a line and a side of a polygon
    	
    	return null;
    }
}
