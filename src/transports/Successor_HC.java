package transports;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class Successor_HC implements SuccessorFunction {

	@Override
	public List getSuccessors(Object state) {
		// TODO Auto-generated method stub
        int i, i2, j, j2, j3, capinsuf;
        boolean a;
        Iterator<Peticio>it, it2;
        ArrayList retVal = new ArrayList();
        Estat ne, estat = (Estat) state;
        LinkedList<Peticio> pp, pp2;
        Peticio p, p2;
        Heuristica_Ganancia HF = new Heuristica_Ganancia();
        for(i=0;i<Constants.nc;i++){
        	for(j=0;j<Constants.ht+1;j++){
        		pp = estat.getPeticions(i, j);
        		for(it = pp.iterator(); it.hasNext();){
        			p = it.next();
        			for(j2=0;j2<Constants.ht+1;j2++){
        				if(j2!=j){
       	        			ne = estat;       					
        					if(ne.desplazar(p, i, j, j2)){
        		                //puedo desplazar
        						double v = HF.getHeuristicValue(ne);
        		                //System.out.println(v);
        		                String S = new String("Q:" + p.getCan() + " H:" + p.getH() + " de " + j + " fins " + j2 + "en" + i);
        		                retVal.add(new Successor(S, ne));
        					}
        					else{
        						//no puedo desplazar
        						capinsuf = ne.getCap(i, j2);
        						if(capinsuf<Constants.cap[Constants.cap.length-1]){
        							if(ne.getCap(i, j)>capinsuf && ne.getCapO(i, j)<=capinsuf){
    									a = false;
    									ne.canvi_camion(i, j2, ne.getCap(i, j), i, j, capinsuf);
    									ne.desplazar(p, i, j, j2);
    									i2 = i;
    									j3 = j;
    								}
        							else {
        								a = true;
        								i2 = i;
        								for(j3 = j2+1; j3<Constants.ht && a;j3++){
        									if(ne.getCap(i, j3)>capinsuf && ne.getCapO(i, j3)<=capinsuf){
        										a = false;
        										ne.canvi_camion(i, j2, ne.getCap(i, j3), i, j3, capinsuf);
        										ne.desplazar(p, i, j, j2);
        									}
        								}
        								if(a){        								
        									for(;i2<Constants.nc && a;i2++){
        										for(j3 = 1; j3<Constants.ht && a;j3++){
        											if(ne.getCap(i2, j3)>capinsuf && ne.getCapO(i2, j3)<=capinsuf){
                										a = false;
                										ne.canvi_camion(i, j2, ne.getCap(i2, j3), i2, j3, capinsuf);
                										ne.desplazar(p, i, j, j2);
                									}
        										}
        									}
        									if(a){
        										for(i2=0;i2<i && a;i2++){
            										for(j3 = 1; j<Constants.ht && a;){
            											if(ne.getCap(i2, j3)>capinsuf && ne.getCapO(i2, j3)<=capinsuf){
                    										a = false;
                    										ne.canvi_camion(i, j2, ne.getCap(i2, j3), i2, j3, capinsuf);
                    										ne.desplazar(p, i, j, j2);
                    									}
            										}
            									}
        										if(a){
        											for(j3 = 1; j3<j2 && a;j3++){
        												if(ne.getCap(i2, j3)>capinsuf && ne.getCapO(i2, j3)<=capinsuf){
        													a = false;
        													ne.canvi_camion(i, j2, ne.getCap(i2, j3), i2, j3, capinsuf);
        													ne.desplazar(p, i, j, j2);
        												}
        											}
        										}
        									}
        								}
        								if(!a){
        									double v = HF.getHeuristicValue(ne);
        									//System.out.println(v);
        									String S = new String("C:" + i + " Q:" + p.getCan() + " H:" + p.getH() + " de " + j + " fins " + j2 + " camion entre" + i + "-" + j2 + " e " + i2 + "-" + j3);
        									retVal.add(new Successor(S, ne));
        								}
        							}
        						}
        					}
        				}
        			}
        		}
        	}
        }
        
		return retVal;
	}

}
