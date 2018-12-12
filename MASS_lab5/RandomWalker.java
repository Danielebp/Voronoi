package edu.uwb.css534;

import java.util.Random;
import edu.uw.bothell.css.dsl.MASS.Agent;
import edu.uw.bothell.css.dsl.MASS.MASS;

public class RandomWalker extends Agent {
    public static final int METHOD_ID = 1;

    public RandomWalker(Object obj){}
    @Override
    public Object callMethod(int functionId, Object argument) {
        switch (functionId) {
            case METHOD_ID:
                position(argument);
            default:
        }

        return null;
    }

    void position(Object argument) {
        Integer size = (Integer) argument;
        Random rand = new Random();
        int[] dest = new int[getIndex().length];
        dest[0] = getIndex()[0] + rand.nextInt() % size;
        dest[1] = getIndex()[1] + rand.nextInt() % size;
        MASS.getLogger().debug( "size = "+ size+ " "+dest[0]+" "+dest[1] );
        migrate(dest);
    }

}
