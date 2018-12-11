import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.*;

import mpi.*;

public class Voronoi {

	public static void main(String[] args) throws MPIException{
		// read parameters
		String fileIn = args[0];
		String fileOut = args [1];
		Integer maxX = Integer.parseInt(args[2]);
		Integer maxY = Integer.parseInt(args[3]);

		int[] nPoints = new int[1];
		Polygon initialBoundary = new Polygon();
		
		// Start MPI computation
		MPI.Init( args );
		
		// read mpi details
		int rankSize = MPI.COMM_WORLD.Size();
		int rank = MPI.COMM_WORLD.Rank( );   
		
		// tracking time
		long start;
		if(rank == 0) start = System.currentTimeMillis();
		
		ArrayList<Point> points = new ArrayList();
		
		// rank 0  read points from input file
		if (rank == 0) {
			points = loadPoints(fileIn);
			nPoints[0] = points.size();
		}

		// all ranks should know the number of points
		MPI.COMM_WORLD.Bcast(nPoints, 0, 1, MPI.INT, 0);
		
		// prepares data for sending accross all ranks
		Object[] VoronoiPoints = new Object[nPoints[0]]; 
		if (rank == 0) {
			VoronoiPoints = points.toArray();
			initialBoundary = defineBoundaries (maxX, maxY);
		}
		Object[] myPoints = new Object[nPoints[0]/rankSize];
		Object[] finalDiagram = new Object[nPoints[0]];
		Object[] strBounds = new Object[1];
		if(rank == 0) {
			strBounds[0] = initialBoundary.toString();
		}
		
		// broadcasts the points and boundaryBox
		MPI.COMM_WORLD.Bcast(VoronoiPoints, 0, nPoints[0], MPI.OBJECT, 0);
		MPI.COMM_WORLD.Bcast(strBounds, 0, 1, MPI.OBJECT, 0);
		
		// scatter the data so each rank has a set of points to compute
		MPI.COMM_WORLD.Scatter(VoronoiPoints,0,nPoints[0]/rankSize,MPI.OBJECT,myPoints,0,nPoints[0]/rankSize,MPI.OBJECT,0);
		
		// loads the initial boundary that is common to all points
    	initialBoundary = new Polygon((String)strBounds[0]);
    	
    	// partial diagram created by each rank
    	Object [] myDiagram = new Object[myPoints.length];
    	int i = 0;

    	// each rank runs computation for each of its points
		for(Object ob1 : myPoints) {
			
			// creates point and initial cell
			Point initialPoint = (Point)ob1;
			Polygon cell = initialBoundary.getCopy();
			
			// iterates over each other points
			for(Object ob2 : VoronoiPoints) {
				Point pair = (Point)ob2;
				
				// do not need to compare with the point itself
				if(initialPoint.equals(pair)) continue;
				
				// calculates the equidistant line
				Line middleLine = pair.getEquidistantLine(initialPoint);
				if (middleLine == null) continue;

				// tries to reduce the cell using equidistant line
				cell.splitPolygon(middleLine, initialPoint);
			}
			
			// stores the cell to the partial diagram
			myDiagram[i++] = (initialPoint.toString() + "\t" + cell.toString());
		}
		
		// rank_0 gets all the partial diagrams from the other ranks
		MPI.COMM_WORLD.Gather(myDiagram,0,nPoints[0]/rankSize,MPI.OBJECT, finalDiagram,0,nPoints[0]/rankSize,MPI.OBJECT, 0);
		
		long finish;
		
		if(rank == 0) finish = System.currentTimeMillis();
		
		// Finish MPI computation
	    MPI.Finalize( );

	    // write output file
	    try {
		    PrintWriter out = new PrintWriter(fileOut);		   
	
		    for ( Object entry : finalDiagram ) {
		    	out.println((String)entry);
		    }
			out.close();
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
	    
	    // prints time
	    if(rank == 0) {
	    	long timeElapsed = finish - start;
	    	System.out.println("Elapsed Time: " + timeElapsed/1000);
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
