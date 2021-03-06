import java.util.ArrayList;
import java.io.*;

public class Polygon implements java.io.Serializable{

    private ArrayList<Point> points;

	public Polygon(ArrayList<Point> points) {
        this.points = points;
    }

    /** Parses polygon points from string value */
	public Polygon (String line) {
		this.points = new ArrayList<Point>();
		for (String val : line.split(";")) {
            points.add(new Point(val));
        }
	}

    public Polygon() {
        points = new ArrayList<Point>();
    }

    /** Creates new polygon representing the bounding box of the voronoi diagram */
    public Polygon(int sizeX, int sizeY) {
        points = new ArrayList<Point>();
        addPoint(new Point(0, 0));
        addPoint(new Point(sizeX, 0));
        addPoint(new Point(sizeX, sizeY));
        addPoint(new Point(0, sizeY));
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public void removePoint(Point point) {
    	points.remove(point);
    }

    public Polygon getCopy() {
    	Polygon copy = new Polygon();

    	for(Point p : points) {
    		copy.addPoint(p);
    	}

    	return copy;
    }

    // updates the current polygon with a better one if possible
    public void splitPolygon(Line line, Point initialPoint) {
    	// 2 new polygons when splitting original with line
    	Polygon p1 = new Polygon();
    	Polygon p2 = new Polygon();

    	// points of intersection of line with polygon
    	// lines would either not intersect the polygon or intersect it exactly twice
    	Point intersection1 = null;
    	Point intersection2 = null;

    	// first point pairs with the last
    	Point last = points.get(points.size() - 1);;

    	// iterates over all the points that delimits the cell trying to find the intersections
        for (Point point : points) {
        	// skips the initial point
            if (point.equals(initialPoint)) continue;
            
            // depending on how many intersections it has found it will decide where to put the next point 
    		if(intersection1 == null || intersection2 != null) {
    			p1.addPoint(last);
            } else {
                p2.addPoint(last);
            }

    		// tries to find an intersection between the line passed and the side of the 
    		// polygon delimited by the current and the last point
            Point intersection = line.findIntersectionWithSide(last, point);
        	if(intersection != null) {
	        	// if intersection is over the previous point it was already added
        		if(intersection.equals(last)) continue;

	        	// if intersection was found add the intersection to both polygons
	        	p1.addPoint(intersection);
	        	if(p2!=null)p2.addPoint(intersection);

	        	// stores the intersections, each line should intersect the polygon twice 
	        	if (intersection1 == null) {
	        		intersection1 = intersection;
                } else if (intersection2 == null) {
                    intersection2 = intersection;
                } else {
                    System.err.println("Found three intersections");
	        	}
            }

            last = point;
        }

        if (intersection1 != null && intersection2 != null) {
    		// calculates in which side of the line the initial point is
    		double value = (intersection2.getX() - intersection1.getX())*(initialPoint.getY() - intersection1.getY()) -
    						(initialPoint.getX() - intersection1.getX())*(intersection2.getY() - intersection1.getY());
    		// initial point is in p1
    		if (value > 0) {
                points = p1.points;
            } else {// initial point in p2
                points = p2.points;
			}
    	}
	}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Point point : points) {
            sb.append(point.toString());
            sb.append(";");
        }

        sb.setLength(sb.length() - 1);

        return sb.toString();
    }
}
