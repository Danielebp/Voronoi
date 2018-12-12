package edu.uwb.css534;

import java.io.PrintWriter;
import java.util.Date;

import edu.uw.bothell.css.dsl.MASS.Agents;
import edu.uw.bothell.css.dsl.MASS.MASS;
import edu.uw.bothell.css.dsl.MASS.Place;
import edu.uw.bothell.css.dsl.MASS.Places;
import edu.uw.bothell.css.dsl.MASS.logging.LogLevel;

public class SimpleMASS {
	private static final String NODE_FILE = "nodes.xml";
	
	public static void main(String[] args) {
		MASS.init();
		int size1 = Integer.parseInt(args[0]);
        Object obj = new Object();
		int numAgents = Integer.parseInt(args[1]);
		Places array2D = new Places(1, Array2D.class.getName(), obj, size1, size1);
		Agents walkers = new Agents(2, RandomWalker.class.getName(), obj, array2D, numAgents);


		try {
			PrintWriter outPut = new PrintWriter("/home/soheli28_css534/prog5/results2.txt");
			walkers.callAll(RandomWalker.METHOD_ID, (Object) new Integer(size1));
			walkers.manageAll();
			while(walkers.getAgents().hasNext()){
				int[] index = walkers.getAgents().next().getIndex();
				outPut.println(index[0]+ " " + index[1]);
			}

			outPut.close();
		} catch(Exception e){
			MASS.getLogger().error(e.getMessage());
		}
		MASS.finish();
                	
	}

}
