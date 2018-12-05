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
    
    public void splitPolygon(Line line, Point initialPoint) {
    	//TODO: check if line crosses polygon, if it doesnt returns
    	
    	//TODO: divide polygon in 2
    	
    	//TODO: check which polygon contains the initialPoint
    	
    	//TODO: updates this polygon with the polygon that contains the initial point
    }
}
