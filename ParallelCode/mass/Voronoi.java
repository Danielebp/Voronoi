import edu.uw.bothell.css.dsl.MASS.Agents;
import edu.uw.bothell.css.dsl.MASS.MASS;
import edu.uw.bothell.css.dsl.MASS.Places;
import edu.uw.bothell.css.dsl.MASS.logging.LogLevel;

import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

public class Voronoi {

    private static final String NODE_FILE = "nodes.xml";

    public static void main(String[] args) {
        MASS.setNodeFilePath(NODE_FILE);
        MASS.setLoggingLevel( LogLevel.DEBUG );

        // start MASS
        MASS.getLogger().debug( "Voronoi initializing MASS library..." );
        MASS.init();
        MASS.getLogger().debug( "MASS library initialized" );

        MASS.getLogger().debug( "hello" );

        Object[] points = (Object[]) readPoints(args[0]).toArray();
        MASS.getLogger().debug( "No of points: " + points.length );
        Places places = new Places(1, Cell.class.getName(), points, points.length);
        Object[] objects = (Object[]) places.callAll(Cell.MAKE_CELL, points);

        try {
            PrintWriter out = new PrintWriter("results.txt");
            for (Object object : objects) {
                out.println((String) object);
            }

            out.close();
        } catch(Exception e){
            MASS.getLogger().error(e.getMessage());
        }

        MASS.getLogger().debug( "We're through" );
        MASS.finish();
    }

    private static List<Point> readPoints(String filename) {
        try {
            List<Point> points = new ArrayList<Point>();
			BufferedReader br = new BufferedReader(new FileReader(filename));
			int keyCount = 0;

			String line = br.readLine();
			while (line != null) {
                points.add(new Point(line));
				line = br.readLine();
			}

            return points;
		} catch (Exception e) {
			System.out.println(e.getMessage());
            return null;
		}
    }
}
