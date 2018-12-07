import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.*;

import mpi.*;
import sharedClasses.*;

public class Voronoi {

	public static void main(String[] args) throws MPIException{
		String fileIn = args[0];
		String fileOut = args [1];
		Integer maxX = Integer.parseInt(args[2]);
		Integer maxY = Integer.parseInt(args[3]);

				
		int nPoints = 0;
		Polygon initialBoundary = new Polygon();
		
		Map<Point, Polygon> finalDiagram = new HashMap<Point, Polygon>();
		Polygon cell = new Polygon();
		
		MPI.Init( args );		      // Start MPI computation
		
		int rankSize = MPI.COMM_WORLD.Size();
		int rank = MPI.COMM_WORLD.Rank( );   
		ArrayList<Point> points = new ArrayList();
		
		if (rank == 0) {
			points = loadPoints(fileIn);
			nPoints = points.size();
		}

		MPI.COMM_WORLD.Bcast(nPoints, 0, 1, MPI.INT, 0);
		Point[] VoronoiPoints = new Point[nPoints]; 
		
		if (rank == 0) {
			VoronoiPoints = points.toArray(new Point[nPoints]);
			initialBoundary = defineBoundaries (maxX, maxY);
		}
		
		Point[] myPoints = new Point[nPoints/rankSize];
		Map<Point, Polygon> myDiagram = new HashMap<Point, Polygon>();
		
		if(rank == 0) {
			System.out.println(VoronoiPoints.length);
			System.out.println(nPoints);
		}
		
		MPI.COMM_WORLD.Bcast(VoronoiPoints, 0, nPoints, MPI.OBJECT, 0);
    	MPI.COMM_WORLD.Bcast(initialBoundary, 0, 1, MPI.OBJECT, 0);
    	MPI.COMM_WORLD.Scatter(VoronoiPoints,0,nPoints/rankSize,MPI.OBJECT,myPoints,0,nPoints/rankSize,MPI.OBJECT,0);
    	
		for(Point initialPoint : myPoints) {
			//Polygon cell = initialBoundary.getCopy();
			
			for(Point pair : VoronoiPoints) {
				if(initialPoint.equals(pair)) continue;
				Line middleLine = pair.getEquidistantLine(initialPoint);
				if (middleLine == null) continue;
				cell.splitPolygon(middleLine, initialPoint);
			}
			
			myDiagram.put(initialPoint, cell);
		}
		
		MPI.COMM_WORLD.Gather(finalDiagram,0,nPoints/rankSize,MPI.OBJECT,myDiagram,0,nPoints/rankSize,MPI.OBJECT, 0);
		
	    MPI.Finalize( );		      // Finish MPI computation

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
