import sharedClasses.*;

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
			points = new Point[Integer.valueOf(conf.get("keyCount"))];
			for (int i = 0; i < points.length; i++) {
				points[i] = new Point(conf.get("point" + i));
			}
		}

		public void map(LongWritable docId,
						Text value,
						OutputCollector<Text, Text> output,
						Reporter reporter) throws IOException {

			for (Point point : points) {
				Point otherPoint = new Point(value.toString());
				Line line = point.getEquidistantLine(otherPoint);

				output.collect(new Text(point.toString()), new Text(line.toString()));
			}
		}
	}

	public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> {

		private int sizeX;
		private int sizeY;

		public void configure(JobConf conf) {
			sizeX = Integer.valueOf(conf.get("sizeX"));
			sizeY = Integer.valueOf(conf.get("sizeY"));
		}

		public void reduce(Text key,
						   Iterator<Text> values,
						   OutputCollector<Text, Text> output,
						   Reporter reporter) throws IOException {
			Point point = new Point(key.toString());
			Polygon polygon = new Polygon();
			polygon.addPoint(new Point(0, 0));
			polygon.addPoint(new Point(sizeX, 0));
			polygon.addPoint(new Point(sizeX, sizeY));
			polygon.addPoint(new Point(0, sizeY));

			while (values.hasNext()) {
				String stringValue = values.next().toString();
				Line line = new Line(stringValue);
				polygon.splitPolygon(line, point);
			}

			output.collect(key, new Text(polygon.toString()));
		}
	}

	public static void main(String[] args) throws Exception {
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

		try {
			BufferedReader br = new BufferedReader(new FileReader(args[2]));
			int keyCount = 0;

			String line = br.readLine();
			while (line != null) {
				conf.set("point" + keyCount, line);
				keyCount ++;
				line = br.readLine();
			}

			conf.set("keyCount", String.valueOf(keyCount));
			conf.set("sizeX", args[3]);
			conf.set("sizeY", args[4]);

			JobClient.runJob(conf);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
