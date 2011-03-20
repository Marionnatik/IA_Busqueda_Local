package transports;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class Successor_SA implements SuccessorFunction {

	@Override
	public List getSuccessors(Object state) {
		// TODO Auto-generated method stub
        int i, i2, j, j2, h1, h2, j3, capinsuf, difcap;
        boolean a, b1;
        Iterator<Peticio>it, it2;
        ArrayList retVal = new ArrayList();
        Estat ne, estat = (Estat) state;
        LinkedList<Peticio> pp, pp2;
        Peticio p, p2;
        Heuristica_Ganancia HF = new Heuristica_Ganancia();
        do{
        	i = (int) (Constants.nc * Math.random());
        	h1 = (int) ((Constants.ht+1)*Math.random());
	        do{
	        	h2 = (int)((Constants.ht+1)*Math.random());
	        }while(h1==h2);
	        pp = estat.getPeticions(i, h1);
	        a = true;
	        for(it = pp.iterator(); it.hasNext() && a;){
	        	p = it.next();
	        	pp2 = estat.getPeticions(i, h2);
	        	for(it2 = pp2.iterator(); it2.hasNext() && a;){
	        		p2 = it2.next();
	        		difcap = p.getCan() - p2.getCan();
	        		ne = estat;       			
	        		b1 = ne.canvi_peticiones(i, h1, h2, p, p2);
	        		if(b1){
	        			a = false;
	        			double v = HF.getHeuristicValue(ne);
	        			//System.out.println(v);
	        			String S = new String("Q:" + p.getCan() + ", " + p2.getCan() + " H:" + p.getH() + ", " + p2.getH() + " de " + h1 + " fins " + h2 + " en centre" + i);
	        			retVal.add(new Successor(S, ne));
	        		}
	        		else{
	        			if(difcap>0){
	        				capinsuf = ne.getCap(i, h2);
	        				j = h1;
	        				j2 = h2;
	        			}
	        			else{
	        				capinsuf = ne.getCap(i, h1);
	        				j = h2;
	        				j2 = h1;
	        			}
	        			if(capinsuf<Constants.cap[Constants.cap.length-1]){
	        				if(ne.getCap(i, j)>capinsuf && ne.getCapO(i, j)-Math.abs(difcap)<=capinsuf){
	        					a = false;
	        					ne.canvi_camion(i, j2, ne.getCap(i, j), i, j, capinsuf);
	        					b1 = ne.canvi_peticiones(i, h1, h2, p, p2);
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
	                					b1 = ne.canvi_peticiones(i, h1, h2, p, p2);
	        						}
	        					}
	        					if(a){        								
	        						for(;i2<Constants.nc && a;i2++){
	        							for(j3 = 1; j3<Constants.ht && a;j3++){
	        								if(ne.getCap(i2, j3)>capinsuf && ne.getCapO(i2, j3)<=capinsuf){
	        									a = false;
	        									ne.canvi_camion(i, j2, ne.getCap(i2, j3), i2, j3, capinsuf);
	        		        					b1 = ne.canvi_peticiones(i, h1, h2, p, p2);
	        								}
	        							}
	        						}
	        						if(a){
	        							for(i2=0;i2<i && a;i2++){
	        								for(j3 = 1; j<Constants.ht && a;){
	        									if(ne.getCap(i2, j3)>capinsuf && ne.getCapO(i2, j3)<=capinsuf){
	        										a = false;
	        										ne.canvi_camion(i, j2, ne.getCap(i2, j3), i2, j3, capinsuf);
	        										b1 = ne.canvi_peticiones(i, h1, h2, p, p2);
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
        	        				String S = new String("Q:" + p.getCan() + ", " + p2.getCan() + " H:" + p.getH() + ", " + p2.getH() + " da " + h1 + " a " + h2 + "in" + i);
        							retVal.add(new Successor(S, ne));
        						}
        					}
        				}
        			}
        		}
	        }
		}while(a);
		return retVal;
	}

}
