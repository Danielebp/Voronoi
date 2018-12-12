import edu.uw.bothell.css.dsl.MASS.Place;
import edu.uw.bothell.css.dsl.MASS.MASS;
import java.util.List;

public class Cell extends Place {

    public final static int MAKE_CELL = 1;

    private int sizeX = 20000;
    private int sizeY = 10000;
    private Point[] points;

    public Cell(Object o) {
    	MyArgs myArgs = (MyArgs)o;
        
    	// reads arguments
    	sizeX  = myArgs.maxX;
    	sizeY  = myArgs.maxY;
    	points = myArgs.getPoints();
    }

    /**
    * This method is called when "callAll" is invoked from the master node
    */
    public Object callMethod(int method, Object o) {
        switch (method) {
        case MAKE_CELL:
                return makeCell(o);
        default:
                return new String("Unknown Method Number: " + method);
        }
    }

    private Object makeCell(Object o) {
        Point initialPoint = (Point) o;
        MASS.getLogger().debug( "entering make cell" );
        MASS.getLogger().debug( "Initial point: " + initialPoint.toString() );

        // polygon that represents the cell starts will the whole space
        Polygon polygon = new Polygon(sizeX, sizeY);
        
        // iterates over all other points
        for (Point point : points) {
        	// skip if its the same point
            if (point.equals(initialPoint)) continue;
            
            // calculates the equidistant line between the initial point and every other point
            Line line = initialPoint.getEquidistantLine(point);
            
            // tries to reduce the cell with the equidistant line
            polygon.splitPolygon(line, initialPoint);
        }

        MASS.getLogger().debug( "exiting makeCell" );

        return initialPoint.toString() + "\t" + polygon.toString();
    }
}

