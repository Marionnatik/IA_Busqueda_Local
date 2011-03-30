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

		int centre, hora1, hora2, i2, j3, capinsuf ;
		boolean a ;
		Iterator<Peticio> it ;
		Peticio peti;
		ArrayList<Successor> retVal = new ArrayList<Successor>();
		String s, saux1, saux2;
		Estat estat = (Estat) state ;

		ArrayList<Peticio> _p = new ArrayList<Peticio>();
		ArrayList<Integer> _c = new ArrayList<Integer>();
		ArrayList<Integer> _hOri = new ArrayList<Integer>();
		ArrayList<Integer> _hDest = new ArrayList<Integer>();
		ArrayList<Boolean> _xcap = new ArrayList<Boolean>();
		ArrayList<Integer> _xcen = new ArrayList<Integer>();
		ArrayList<Integer> _xhor = new ArrayList<Integer>();
		do{
			centre = (int) (Constants.nc * Math.random());
			hora1 = (int) ((Constants.ht+1)*Math.random());
			a = true;
			do{
				hora2 = (int)((Constants.ht+1)*Math.random());
			}while(hora1==hora2);
			LinkedList<Peticio> pp = estat.getPeticions(centre, hora1);
			// Per cada peticio
			for(it = pp.iterator(); it.hasNext() && a;)
			{
				Peticio p = it.next();
				// Per cada hora diferent
				if(estat.desplazamientoPosible(p, centre, hora2))
				{
					a = false;
					_p.add(p);
					_c.add(centre);
					_hOri.add(hora1);
					_hDest.add(hora2);
					_xcap.add(false);
					_xcen.add(0);
					_xhor.add(0);
				}
				else 
				{
					capinsuf = estat.getCap(centre, hora2);								
					if(capinsuf < Constants.cap[Constants.cap.length-1])
					{
						if(hora1 != 0 && estat.getCap(centre, hora1) > capinsuf && estat.getCapO(centre, hora1)-p.getCan() <= capinsuf)
						{
							// Se pueden intercambiar las capacidades del estado de origen y de destinacion
							a = false;
							_p.add(p);
							_c.add(centre);
							_hOri.add(hora1);
							_hDest.add(hora2);
							_xcap.add(true);
							_xcen.add(centre);
							_xhor.add(hora1);
						}
						else {
							i2 = centre;
							for(j3 = hora2+1; j3<Constants.ht && a;j3++){
								if(estat.getCap(centre, j3)>capinsuf && estat.getCapO(centre, j3)<=capinsuf){
									a = false;
									_p.add(p);
									_c.add(centre);
									_hOri.add(hora1);
									_hDest.add(hora2);
									_xcap.add(true);
									_xcen.add(centre);
									_xhor.add(j3);
								}
							}
							if(a){        								
								for(;i2<Constants.nc && a;i2++){
									for(j3 = 1; j3<Constants.ht && a;j3++){
										if(estat.getCap(i2, j3)>capinsuf && estat.getCapO(i2, j3)<=capinsuf){
											a = false;
											_p.add(p);
											_c.add(centre);
											_hOri.add(hora1);
											_hDest.add(hora2);
											_xcap.add(true);
											_xcen.add(i2);
											_xhor.add(j3);
										}
									}
								}
								if(a){
									for(i2=0;i2<centre && a;i2++){
										for(j3 = 1; j3<Constants.ht && a; j3++){
											if(estat.getCap(i2, j3)>capinsuf && estat.getCapO(i2, j3)<=capinsuf){
												a = false;
												_p.add(p);
												_c.add(centre);
												_hOri.add(hora1);
												_hDest.add(hora2);
												_xcap.add(true);
												_xcen.add(i2);
												_xhor.add(j3);
											}
										}
									}
									if(a){
										for(j3 = 1; j3<hora2 && a;j3++){
											if(estat.getCap(i2, j3)>capinsuf && estat.getCapO(i2, j3)<=capinsuf){
												a = false;
												_p.add(p);
												_c.add(centre);
												_hOri.add(hora1);
												_hDest.add(hora2);
												_xcap.add(true);
												_xcen.add(i2);
												_xhor.add(j3);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}while(a);
		for(int i = 0 ; i < _p.size(); i++)
		{
			Estat successor = new Estat(estat) ;
			if(!_xcap.get(i))
			{
				successor.desplazar(_p.get(i), _c.get(i), _hOri.get(i), _hDest.get(i));
				if(_hOri.get(i)==0)saux1 = Integer.toString(0);
				else saux1 = Integer.toString(_hOri.get(i)+7);
				if(_hDest.get(i)==0)saux2 = Integer.toString(0);
				else saux2 = Integer.toString(_hDest.get(i)+7);
				s = new String("Peticion " + _p.get(i).getID() + " del centro " + (_c.get(i)+1) + " con hora de entrega : " + _p.get(i).getH() + "h desplazada de " + (saux1) + "h a " + (saux2) + "h.");
			}
			else{
				peti = successor.removePeticio(_p.get(i), _c.get(i), _hOri.get(i));
				successor.canvi_camion(_c.get(i), _hDest.get(i), estat.getCap(_xcen.get(i), _xhor.get(i)), _xcen.get(i), _xhor.get(i), estat.getCap(_c.get(i), _hDest.get(i)));				
				s = new String("\nCapacitats intercanviades entre l'hora " + (_hDest.get(i)+7) + " del centre " + (_c.get(i)+1) + " (" + estat.getCap(_c.get(i), _hDest.get(i)) + "kgs) i l'hora " + (_xhor.get(i)+7) + " del centre " + (_xcen.get(i)+1) + " (" + estat.getCap(_xcen.get(i), _xhor.get(i)) + "kgs).");
				a = successor.addPeticio(peti, _c.get(i), _hDest.get(i));
				if(_hOri.get(i)==0)saux1 = Integer.toString(0);
				else saux1 = Integer.toString(_hOri.get(i)+7);
				if(_hDest.get(i)==0)saux2 = Integer.toString(0);
				else saux2 = Integer.toString(_hDest.get(i)+7);
				s = s.concat("Peticion " + _p.get(i).getID() + " del centro " + (_c.get(i)+1) + " con hora de entrega : " + _p.get(i).getH() + "h desplazada de " + (_hOri.get(i)+7) + "h a " + (_hDest.get(i)+7) + "h.");
			}
			int b1 = estat.getBenefici();
			int b2 = successor.getBenefici();
			s = s.concat(" El benefici passa de " + b1 + " a " + b2 + ".");
			retVal.add(new Successor(s, successor));
			
		}
		System.out.println(retVal.size() + " successores generados.");
		return retVal;
	}
}