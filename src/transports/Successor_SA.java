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
		boolean a = true, b1;
		Iterator<Peticio>it, it2;
		ArrayList retVal = new ArrayList();
		Estat ne, estat = (Estat) state;
		LinkedList<Peticio> pp, pp2;
		Peticio p, p2;
		do{
			i = (int) (Constants.nc * Math.random());
			h1 = (int) ((Constants.ht+1)*Math.random());
			do{
				h2 = (int)((Constants.ht+1)*Math.random());
			}while(h1==h2);
			pp = estat.getPeticions(i, h1);
			for(it = pp.iterator(); it.hasNext();){
				p = it.next();
				ne = estat;       					
				if(ne.desplazar(p, i, h1, h2)){
					//puedo desplazar
					//System.out.println(v);
					String S = new String("Q:" + p.getCan() + " H:" + p.getH() + " de " + h1 + " fins " + h2 + "en" + i);
					retVal.add(new Successor(S, ne));
				}
				else{
					//no puedo desplazar
					capinsuf = ne.getCap(i, h2);
					if(capinsuf<Constants.cap[Constants.cap.length-1]){
						if(ne.getCap(i, h1)>capinsuf && ne.getCapO(i, h2)<=capinsuf){
							a = false;
							ne.canvi_camion(i, h2, ne.getCap(i, h1), i, h1, capinsuf);
							ne.desplazar(p, i, h1, h2);
							i2 = i;
							j3 = h1;
						}
						else {
							a = true;
							i2 = i;
							for(j3 = h2+1; j3<Constants.ht && a;j3++){
								if(ne.getCap(i, j3)>capinsuf && ne.getCapO(i, j3)<=capinsuf){
									a = false;
									ne.canvi_camion(i, h2, ne.getCap(i, j3), i, j3, capinsuf);
									ne.desplazar(p, i, h1, h2);
								}
							}
							if(a){        								
								for(;i2<Constants.nc && a;i2++){
									for(j3 = 1; j3<Constants.ht && a;j3++){
										if(ne.getCap(i2, j3)>capinsuf && ne.getCapO(i2, j3)<=capinsuf){
											a = false;
											ne.canvi_camion(i, h2, ne.getCap(i2, j3), i2, j3, capinsuf);
											ne.desplazar(p, i, h1, h2);
										}
									}
								}
								if(a){
									for(i2=0;i2<i && a;i2++){
										for(j3 = 1; h1<Constants.ht && a;){
											if(ne.getCap(i2, j3)>capinsuf && ne.getCapO(i2, j3)<=capinsuf){
												a = false;
												ne.canvi_camion(i, h2, ne.getCap(i2, j3), i2, j3, capinsuf);
												ne.desplazar(p, i, h1, h2);
											}
										}
									}
									if(a){
										for(j3 = 1; j3<h2 && a;j3++){
											if(ne.getCap(i2, j3)>capinsuf && ne.getCapO(i2, j3)<=capinsuf){
												a = false;
												ne.canvi_camion(i, h2, ne.getCap(i2, j3), i2, j3, capinsuf);
												ne.desplazar(p, i, h1, h2);
											}
										}
									}
								}
							}
							if(!a){
								//System.out.println(v);
								String S = new String("C:" + i + " Q:" + p.getCan() + " H:" + p.getH() + " de " + h1 + " fins " + h2 + " camion entre" + i + "-" + h2 + " e " + i2 + "-" + j3);
								retVal.add(new Successor(S, ne));
							}
						}
					}
				}
			}

		}while(a);
		return retVal;
	}

}
