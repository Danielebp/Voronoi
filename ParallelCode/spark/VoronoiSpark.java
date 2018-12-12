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

public class VoronoiSpark {

    public static void main(String[] args) throws IOException {
        // start Sparks
        String inputFile = args[0];
        SparkConf conf = new SparkConf().setAppName("Voronoi Diagram");
        conf.set("WindowX", args[1]);
        conf.set("WindowY", args[2]);
        JavaSparkContext jsc = new JavaSparkContext(conf);
        // read input file into RDD lines
        JavaRDD<String> lines = jsc.textFile(inputFile);
        // start timer
        long startTime = System.currentTimeMillis();

        JavaRDD<Point> points = lines.map(s -> new Point(s));

        List<Point> listOfPoints = points.collect();
        // calculate equidistant line and makes a key value pair of
        // key = point value = list of lines
        JavaPairRDD<String, List<Line>> network = points.mapToPair(p -> {
            List<Point> listOfOtherPoints =
                    listOfPoints.stream().filter(point -> !p.equals(point)).collect(Collectors.toList());
            List<Line> listOfLines = listOfOtherPoints.stream().map(o -> {
                return p.getEquidistantLine(o);
            }).collect(Collectors.toList());

            return new Tuple2<>(p.toString(), listOfLines);

        });

        // makes polygon by using the list of list and creates a new
        // key value pair of point and list of intersecting points to make polygon
        JavaPairRDD<String, Polygon> cell = network.flatMapToPair(C -> {
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

            // makes polygon by using the list of list and creates a new
            // key value pair of point and list of intersecting points to make polygon
            for (Line line : C._2()) {
                polygon.splitPolygon(line, center);
            }
            List<Tuple2<String, Polygon>> linesPolygon = new ArrayList<>();
            linesPolygon.add(new Tuple2<>(C._1(), polygon));
            return linesPolygon.iterator();
        });
        // collecting all to driver file
        Map<String, Polygon> polygonMap = cell.collectAsMap();
        long endTime = System.currentTimeMillis();
        System.out.println("Time required = " + (endTime - startTime));
        FileWriter writer = new FileWriter("voronoi_diagram.txt");
        PrintWriter printWriter = new PrintWriter(writer);
        // write output into file
        for (String key : polygonMap.keySet()) {
            printWriter.println(key + "\t" + polygonMap.get(key).toString());
        }
        printWriter.close();

    }

}
