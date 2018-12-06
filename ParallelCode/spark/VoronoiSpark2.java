package spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.netbeans.mdr.handlers.ImmutableList;
import scala.Tuple2;
import sharedClasses.Line;
import sharedClasses.Point;
import sharedClasses.Polygon;
import sharedClasses.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VoronoiSpark2 {

    public static void main(String[] args) {
        // start Sparks and read a given input file
        String inputFile = args[0];
        //final Pattern SPACES = Pattern.compile("=|,|;");
        SparkConf conf = new SparkConf().setAppName("Voronoi Diagram");
        conf.set("WindowX", args[1]);
        conf.set("WindowY", args[2]);
        JavaSparkContext jsc = new JavaSparkContext(conf);

        JavaRDD<String> lines = jsc.textFile(inputFile);

        long startTime = System.currentTimeMillis();

        JavaRDD<Point> points = lines.map(s -> new Point(s));

        List<Point> listOfPoints = points.collect();
        JavaPairRDD<String, Point> pair = points.flatMapToPair(pr -> {
            List<Tuple2<String, Point>> pointPairs = new ArrayList<>();
            for (Point point : listOfPoints) {
                if (!point.equals(pr)) {
                    pointPairs.add(new Tuple2<>(pr.toString(), point));
                }
            }
            return pointPairs.iterator();
        });

        JavaPairRDD<String, Line> lineNetwork = pair.mapToPair(line -> {
            Point p1 = new Point(line._1());
            Point bisectPoint = new Point((p1.getX() + line._2().getX() / 2), (p1.getY() + line._2().getY()) / 2);
            Vector vector = new Vector(p1, line._2());
            return new Tuple2<>(line._1(), new Line(bisectPoint, vector.getPerpendicularVector()));
        });

        JavaPairRDD<String, Iterable<Line>> network = lineNetwork.groupByKey();


        JavaPairRDD<String, Polygon> cell = network.flatMapToPair(C -> {
            Point center = new Point(C._1());
            Double X = Double.parseDouble(conf.get("WindowX"));
            Double Y = Double.parseDouble(conf.get("WindowY"));
            Point p1 = new Point(0, 0);
            Point p2 = new Point(X, 0);
            Point p3 = new Point(0, Y);
            Point p4 = new Point(X, Y);
            Polygon polygon = new Polygon(List.of(p1, p2, p3, p4));

            for (Line line : C._2()) {
                polygon.splitPolygon(line, center);
            }
            return List.of(new Tuple2<>(C._1(), polygon)).iterator();
        });

        Map<String, Polygon> polygonMap = cell.collectAsMap();
        for (String key : polygonMap.keySet()) {
            System.out.println("sitePOint: " + key + " " + polygonMap.toString());
        }

    }

}
