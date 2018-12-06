package sharedClasses;
import java.util.List;
import java.util.ArrayList;

public class Polygon {

    private List<Point> points;

    public Polygon(List<Point> points) {
        this.points = points;
    }

    public Polygon() {
        points = new ArrayList<>();
    }

    public void addPoint(Point point) {
        points.add(point);
    }
    
    public void removePoint(Point point) {
    	points.remove(point);
    }

    public boolean containsPoint(Point point){
    	//TODO: check if polygon contain point
    	
    	return false;
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
    	
    	// iterates over all points that form the polygon
        for (Point p : points) {
		if(p.equals(initialPoint)) continue;        	
    		// if we did not find any intersection or if we have seen both then we are on the first polygon
    		if(intersection1 == null || intersection2 != null) {
        		// adds last point to current polygon
    			p1.addPoint(last);
        		
    			// check for intersection between current side and the line
    			// current side is the line between last and current point
	        	Point intersection = line.findIntersectionWithSide(last, p);
	        	if(intersection != null) {
	        		// if intersection was found add the intersection to both polygons
	        		p1.addPoint(intersection);
	        		p2.addPoint(intersection);
	        		
	        		// saves intersection
	        		intersection1 = intersection;
	        	}
    		} // if we have seen one of the intersections but have not seen the second yet then we are navigating the second polygon
    		else {
    			// adds last point to current polygon
    			p2.addPoint(last);
    			
    			// check for intersection between current side and the line
    			// current side is the line between last and current point
    			Point intersection = line.findIntersectionWithSide(last, p);
	        	if(intersection != null) {
	        		// if intersection was found add the intersection to both polygons
	        		p1.addPoint(intersection);
	        		p2.addPoint(intersection);
	        		
	        		// saves intersection
	        		intersection2 = intersection;
	        		
	        		// calculates in which side of the line the initial point is
	        		double value = (intersection2.getX() - intersection1.getX())*(initialPoint.getY() - intersection1.getY()) - 
	        						(initialPoint.getX() - intersection1.getX())*(intersection2.getY() - intersection1.getY());
	        		// initial point is in p1
    				if (value > 0) {
    					p2 = null;
    				}
    				else {// initial point in p2 
    					p1 = null;
    					break; // if point is in p2 we dont need to find the rest of p1
		        	}
        		}
        		
        	}
        	
        	// updates last point with current
        	last = p;
        }
        
    	if(p2 != null) {
    		// polygon was only touched in one vertex so we just keep the original polygon
    		if(p2.points.size() <= 2) return;
    		
    		// point was in p2, we have to update the current polygon
    		this.points = p2.points;
    	}
    	// if point is in p1 then we check if any intersection was even found
    	// if not we do not have to update anything
    	else if (intersection1 != null){
    		// point is in p1, we have to update the current polygon
    		this.points = p1.points;
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
