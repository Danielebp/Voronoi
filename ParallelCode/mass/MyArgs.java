public class MyArgs implements java.io.Serializable{
	public int maxX;
	public int maxY;
	public String points;
	
	public MyArgs (int maxX, int maxY, Object[] points) {
		this.maxX = maxX;
		this.maxY = maxY;
		this.points = "";
		
		for(Object p : points)
			this.points += ((Point)p).toString() + "###";
		
	}
	
	public Point[] getPoints() {
		String[] p = myArgs.points.split("###");
		Point[] points = new Point[p.length];
		for (int i = 0; i < p.length; i++) {
            points[i] = new Point(p[i]);
        }
		return points;
	}
	
}