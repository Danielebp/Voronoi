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
		String fileIn = args[0];
		String fileOut = args [1];
		Integer maxX = Integer.parseInt(args[2]);
		Integer maxY = Integer.parseInt(args[3]);

				
		int[] nPoints = new int[1];
		Polygon initialBoundary = new Polygon();
		
		MPI.Init( args );		      // Start MPI computation
		
		int rankSize = MPI.COMM_WORLD.Size();
		int rank = MPI.COMM_WORLD.Rank( );   
		
		
		ArrayList<Point> points = new ArrayList();
		
		if (rank == 0) {
			points = loadPoints(fileIn);
			nPoints[0] = points.size();
		}

		MPI.COMM_WORLD.Bcast(nPoints, 0, 1, MPI.INT, 0);
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
		
		MPI.COMM_WORLD.Bcast(VoronoiPoints, 0, nPoints[0], MPI.OBJECT, 0);
		MPI.COMM_WORLD.Bcast(strBounds, 0, 1, MPI.OBJECT, 0);
		
		MPI.COMM_WORLD.Scatter(VoronoiPoints,0,nPoints[0]/rankSize,MPI.OBJECT,myPoints,0,nPoints[0]/rankSize,MPI.OBJECT,0);
		
		System.out.println(rank + ": " + (String)strBounds[0]);
    	initialBoundary = new Polygon((String)strBounds[0]);
		
    	Object [] myDiagram = new Object[myPoints.length];
    	int i = 0;
		for(Object ob1 : myPoints) {
			Point initialPoint = (Point)ob1;
			System.out.println(rank + ": " + initialPoint.toString());
			Polygon cell = initialBoundary.getCopy();
			
			for(Object ob2 : VoronoiPoints) {
				Point pair = (Point)ob2;
				if(initialPoint.equals(pair)) continue;
				Line middleLine = pair.getEquidistantLine(initialPoint);
				if (middleLine == null) continue;
				cell.splitPolygon(middleLine, initialPoint);
			}
			
			myDiagram[i++] = (initialPoint.toString() + "\t" + cell.toString());
		}
		
		MPI.COMM_WORLD.Gather(myDiagram,0,nPoints[0]/rankSize,MPI.OBJECT, finalDiagram,0,nPoints[0]/rankSize,MPI.OBJECT, 0);
		
	    MPI.Finalize( );		      // Finish MPI computation

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
