import java.io.IOException;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

public class Voronoi {

	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {

		private Point[] points;

		public void configure(JobConf conf) {
			// Get list of all points
			points = new Point[Integer.valueOf(conf.get("keyCount"))];
			for (int i = 0; i < points.length; i++) {
				points[i] = new Point(conf.get("point" + i));
			}
		}

		public void map(LongWritable docId,
						Text value,
						OutputCollector<Text, Text> output,
						Reporter reporter) throws IOException {
			Point otherPoint = new Point(value.toString());

			// Calculate equidistant line for each point with other point
			for (Point point : points) {
				if (point.equals(otherPoint))
					continue;

				Line line = point.getEquidistantLine(otherPoint);
				output.collect(new Text(point.toString()), new Text(line.toString()));
			}
		}
	}

	public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> {

		private int sizeX;
		private int sizeY;
		private int keyCount;

		public void configure(JobConf conf) {
			sizeX = Integer.valueOf(conf.get("sizeX"));
			sizeY = Integer.valueOf(conf.get("sizeY"));
			keyCount = Integer.valueOf(conf.get("keyCount"));
		}

		public void reduce(Text key,
						   Iterator<Text> values,
						   OutputCollector<Text, Text> output,
						   Reporter reporter) throws IOException {
			List<Line> lines = new ArrayList<>();

			while (values.hasNext()) {
				String value = values.next().toString();

				// Check if value contains lines
				if (value.contains("_")) {
					// Collect all lines together
					String[] linesStr = value.split(":");
					for (String lineStr : linesStr) {
						lines.add(new Line(lineStr));
					}
				} else {
					// If it conatins points it's already the right polygon
					output.collect(key, new Text(value));
					return;
				}
			}

			// Check if all lines were collected
			if (lines.size() == keyCount - 1) {
				// If we have all lines we can build a polygon
				Point point = new Point(key.toString());
				Polygon polygon = new Polygon(sizeX, sizeY);

				for (Line line : lines) {
					polygon.splitPolygon(line, point);
				}

				output.collect(key, new Text(polygon.toString()));
			} else {
				// If we don't have all lines we can only collect all combined lines
				StringBuilder sb = new StringBuilder();
				for (Line line : lines) {
					sb.append(line.toString());
					sb.append(":");
				}

				sb.setLength(sb.length() - 1);

				output.collect(key, new Text(sb.toString()));
			}
		}
	}

	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();

		JobConf conf = new JobConf(Voronoi.class);
		conf.setJobName("voronoi");

		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);

		conf.setMapperClass(Map.class);
		conf.setCombinerClass(Reduce.class);
		conf.setReducerClass(Reduce.class);

		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);

		FileInputFormat.setInputPaths(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));

		setKeyPoints(conf, args[2]);

		conf.set("sizeX", args[3]);
		conf.set("sizeY", args[4]);

		JobClient.runJob(conf);

		long duration = System.currentTimeMillis() - startTime;
		System.out.println("Duration: " + (duration / 1_000) + "s");
	}

	/**
	Sets every point contained in the file with the given filename as a key for
	the map phase in the job conf
	*/
	private static void setKeyPoints(JobConf conf, String filename) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			int keyCount = 0;

			String line = br.readLine();
			while (line != null) {
				conf.set("point" + keyCount, line);
				keyCount ++;
				line = br.readLine();
			}

			// Set total number of keys
			conf.set("keyCount", String.valueOf(keyCount));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
