package mpiVersion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import mpi.*;
import sharedClasses.*;

public class Voronoi {

	public static void main(String[] args) {
		ArrayList<Point> VoronoiPoints = loadPoints("points.txt");
		Polygon initialBoundary = defineBoundaries (VoronoiPoints);
		
		ArrayList<Polygon> finalDiagram = new ArrayList<Polygon>();
		
		MPI.Init( args );		      // Start MPI computation
		
	    if ( MPI.COMM_WORLD.rank() == 0 ) { // rank 0�sender
	    	// do something
	    	MPI.COMM_WORLD.Send( "Hello World!", 12, MPI.CHAR, 1, tag0 );
	      	MPI.COMM_WORLD.Send( loop, 1, MPI.INT, 1, tag0 );
	    } else {           
	    	// do something else
	    	MPI::COMM_WORLD.Recv( msg, 12, MPI.CHAR, 0, tag0 );
	    	MPI::COMM_WORLD.Recv( loop, 1, MPI.INT, 0, tag0 );
	    }
		
		for(Point initialPoint : VoronoiPoints) {
			Polygon cell = initialBoundary.getCopy();
			
			for(Point pair : VoronoiPoints) {
				Line middleLine = pair.getEquidistantLine(initialPoint);
				cell.splitPolygon(middleLine, initialPoint);
			}
			
			finalDiagram.add(cell);
		}
		
	    MPI.Finalize( );		      // Finish MPI computation

		for(Polygon p : finalDiagram) {
			//p.plot();
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
	
	public static Polygon defineBoundaries (ArrayList<Point> points) {
		double minX = 0.0;
		double maxX = 0.0;
		double minY = 0.0;
		double maxY = 0.0;
		
		for(Point p : points) {
			if(p.getX() < minX) minX = p.getX();
			if(p.getX() > maxX) maxX = p.getX();
			if(p.getY() < minX) minY = p.getY();
			if(p.getY() < minX) maxY = p.getY();
		}
		
		Polygon boundaryBox = new Polygon();
		
		boundaryBox.addPoint(new Point(minX -1, minY -1));
		boundaryBox.addPoint(new Point(minX -1, maxY +1));
		boundaryBox.addPoint(new Point(maxX +1, maxY +1));
		boundaryBox.addPoint(new Point(maxX +1, minY -1));
		
		return boundaryBox;
	}
	
	
	
}