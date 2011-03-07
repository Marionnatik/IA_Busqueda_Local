package transports;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import aima.search.framework.SuccessorFunction;

public class Successor_HC implements SuccessorFunction {

	@Override
	public List getSuccessors(Object state) {
		// TODO Auto-generated method stub
        int i, j, j2;
        Iterator<Peticio>it, it2;
        ArrayList retVal = new ArrayList();
        Estat ne, estat = (Estat) state;
        LinkedList<Peticio> pp, pp2;
        Peticio p;
        Heuristica_Ganancia HF = new Heuristica_Ganancia();
        for(i=0;i<Constants.nc;i++){
        	for(j=0;j<Constants.ht+1;j++){
        		pp = estat.getPeticions(i, j);
        		for(it = pp.iterator(); it.hasNext();){
        			p = it.next();
        			for(j2=0;j2<Constants.ht+1;j2++){
        				if(j2!=j){
        					pp2 = estat.getPeticions(i, j2);
        	        		for(it2 = pp2.iterator(); it2.hasNext();){
        	        			it2.next();
        	        			
        	        		}
        				}
        			}
        		}
        	}
        }
        
		return retVal;
	}

}
