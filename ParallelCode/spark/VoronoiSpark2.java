
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;

public class VoronoiSpark2 {

    public static void main(String[] args) throws IOException {
        // start Sparks
        String inputFile = args[0];
        SparkConf conf = new SparkConf().setAppName("Voronoi Diagram");
        conf.set("WindowX", args[1]);
        conf.set("WindowY", args[2]);
        JavaSparkContext jsc = new JavaSparkContext(conf);
        // read inputfile into RDD line
        JavaRDD<String> lines = jsc.textFile(inputFile);
        // start timer
        long startTime = System.currentTimeMillis();

        JavaRDD<Point> points = lines.map(s -> new Point(s));
        // creating a key value pair of points
        List<Point> listOfPoints = points.collect();
        JavaPairRDD<String, Point> pair = points.flatMapToPair(pr -> {
            List<Tuple2<String, Point>> pointPairs = new ArrayList<>();
            for (Point point : listOfPoints) {
                if (!point.equals(pr)) {
                    double x1 = Math.abs(pr.getX()- point.getX())
                    double x2 = Math.abs(pr.getY()-point.getY());
                    if(x1 < 200 && x2 < 200){
                        pointPairs.add(new Tuple2<>(pr.toString(), point));
                    }
                }
            }
            return pointPairs.iterator();
        });
        // calculates equidistance line between points and
        // creates key value pair of point and line
        JavaPairRDD<String, Line> lineNetwork = pair.mapToPair(line -> {
            Point p1 = new Point(line._1());
            return new Tuple2<>(line._1(), line._2().getEquidistantLine(p1));
        });
        // collect all the lines for key
        JavaPairRDD<String, Iterable<Line>> network = lineNetwork.groupByKey();

        // makes polygon, creates key value pair of points and a list of
        // intersecting point to make the polygon
        JavaPairRDD<String, String> cell = network.flatMapToPair(C -> {
            Point center = new Point(C._1());
            Double X = Double.parseDouble(conf.get("WindowX"));
            Double Y = Double.parseDouble(conf.get("WindowY"));
            Point p1 = new Point(0, 0);
            Point p2 = new Point(X, 0);
            Point p3 = new Point(X, Y);
            Point p4 = new Point(0, Y);

            Polygon polygon = new Polygon();
            polygon.addPoint(p1);
            polygon.addPoint(p2);
            polygon.addPoint(p3);
            polygon.addPoint(p4);

            for (Line line : C._2()) {
                polygon.splitPolygon(line, center);
            }
            List<Tuple2<String, String>> linesPolygon = new ArrayList<>();
            linesPolygon.add(new Tuple2<>(C._1(), polygon.toString()));
            return linesPolygon.iterator();

        });
        // collecting all to driver
        Map<String, Polygon> polygonMap = cell.collectAsMap();
        long endTime = System.currentTimeMillis();
        System.out.println("Time required = " + (endTime - startTime));
        FileWriter writer = new FileWriter("voronoi_diagram.txt");
        PrintWriter printWriter = new PrintWriter(writer);
        // write output into a file
        for (String key : polygonMap.keySet()) {
            printWriter.println(key + "\t" + polygonMap.get(key).toString());
        }
        printWriter.close();

    }

}
