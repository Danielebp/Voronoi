import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.*;

//import mpi.*;
import sharedClasses.*;

public class Voronoi {

	public static void main(String[] args) {
		String fileIn = args[0];
		String fileOut = args [1];
		Integer maxX = Integer.parseInt(args[2]);
		Integer maxY = Integer.parseInt(args[3]);

		ArrayList<Point> VoronoiPoints = loadPoints(fileIn);
		Polygon initialBoundary = defineBoundaries (maxX, maxY);
		
		Map<Point, Polygon> finalDiagram = new HashMap<Point, Polygon>();
		
//		MPI.Init( args );		      // Start MPI computation
		
	    /*if ( MPI.COMM_WORLD.rank() == 0 ) { // rank 
	    	// do something
	    	MPI.COMM_WORLD.Send( "Hello World!", 12, MPI.CHAR, 1, tag0 );
	      	MPI.COMM_WORLD.Send( loop, 1, MPI.INT, 1, tag0 );
	    } else {           
	    	// do something else
	    	MPI::COMM_WORLD.Recv( msg, 12, MPI.CHAR, 0, tag0 );
	    	MPI::COMM_WORLD.Recv( loop, 1, MPI.INT, 0, tag0 );
	    }*/
		
		for(Point initialPoint : VoronoiPoints) {
			Polygon cell = initialBoundary.getCopy();
			
			for(Point pair : VoronoiPoints) {
				if(initialPoint.equals(pair)) continue;
				Line middleLine = pair.getEquidistantLine(initialPoint);
				if (middleLine == null) continue;
				cell.splitPolygon(middleLine, initialPoint);
			}
			
			finalDiagram.put(initialPoint, cell);
		}
		
	    //MPI.Finalize( );		      // Finish MPI computation

		try {
		    PrintWriter out = new PrintWriter(fileOut);		   
	
		    for ( Map.Entry<Point, Polygon> entry : finalDiagram.entrySet() ) {
		    	out.println(entry.getKey().toString() + "\t" + (entry.getValue().toString()));
		    }
			out.close();
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
	}

	public static ArrayList<Point> loadPoints(String filename) {
		ArrayList<Point> points = new ArrayList<Point>();
		
		File file = new File(filename); 
		  
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
		
			String line;
			while ((line = br.readLine()) != null) {
				points.add(new Point(line));
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		return points;
	}
	
	public static Polygon defineBoundaries (Integer mX, Integer mY) {
		double minX = 0.0;
		double maxX = 1.0 *mX;
		double minY = 0.0;
		double maxY = 1.0*mY;
		/*
		for(Point p : points) {
			if(p.getX() < minX) minX = p.getX();
			if(p.getX() > maxX) maxX = p.getX();
			if(p.getY() < minX) minY = p.getY();
			if(p.getY() < minX) maxY = p.getY();
		}
		*/
		Polygon boundaryBox = new Polygon();
		
		boundaryBox.addPoint(new Point(minX , minY ));
		boundaryBox.addPoint(new Point(maxX , minY ));
		boundaryBox.addPoint(new Point(maxX , maxY ));
		boundaryBox.addPoint(new Point(minX , maxY ));
		
		return boundaryBox;
	}
	
	
	
}
