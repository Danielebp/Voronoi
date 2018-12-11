import java.util.List;
import java.util.ArrayList;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.Charset;

public class PointsGen {

	public static void main(String[] args) {
		if (args.length != 4) {
			System.err.println("usage: java PointsGen num_points size_x size_y filename");
			return;
		}

		int numPoints = Integer.parseInt(args[0]);
		int sizeX = Integer.parseInt(args[1]);
		int sizeY = Integer.parseInt(args[2]);
		String filename = args[3];

		List<String> points = createPoints(numPoints, sizeX, sizeY);

		try {
			Path file = Paths.get(filename);
			Files.write(file, points, Charset.forName("UTF-8"));
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// creates random set of points to generate the voronoi diagram
	private static List<String> createPoints(int numPoints, int sizeX, int sizeY) {
		List<String> points = new ArrayList<String>();
		for (int i = 0; i < numPoints; i++) {
			int x = (int) (Math.random() * sizeX);
			int y = (int) (Math.random() * sizeY);

			points.add("" + x + " " + y);
		}

		return points;
	}
}
