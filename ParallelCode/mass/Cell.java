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
        
    	sizeX  = myArgs.maxX;
    	sizeY  = myArgs.maxY;
        points = new Point[myArgs.points.length];
        for (int i = 0; i < myArgs.points.length; i++) {
            points[i] = (Point) myArgs.points[i];
        }
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

        Polygon polygon = new Polygon(sizeX, sizeY);
        for (Point point : points) {
            if (point.equals(initialPoint)) continue;
            Line line = initialPoint.getEquidistantLine(point);
            polygon.splitPolygon(line, initialPoint);
        }

        MASS.getLogger().debug( "exiting makeCell" );

        return initialPoint.toString() + "\t" + polygon.toString();
    }
}

