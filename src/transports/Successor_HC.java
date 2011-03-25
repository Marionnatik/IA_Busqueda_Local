package transports;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class Successor_HC implements SuccessorFunction {

	@Override
	public List<Successor> getSuccessors(Object state) {

		int i2, j3, capinsuf ;
		boolean a ;
		Iterator<Peticio> it ;
		Peticio peti;
		ArrayList<Successor> retVal = new ArrayList<Successor>();
		String s;
		Estat estat = (Estat) state ;
		System.out.println("Benefici del estat : " + estat.getBenefici());
		
		ArrayList<Peticio> _p = new ArrayList<Peticio>();
		ArrayList<Integer> _c = new ArrayList<Integer>();
		ArrayList<Integer> _hOri = new ArrayList<Integer>();
		ArrayList<Integer> _hDest = new ArrayList<Integer>();
		ArrayList<Boolean> _xcap = new ArrayList<Boolean>();
		ArrayList<Integer> _xcen = new ArrayList<Integer>();
		ArrayList<Integer> _xhor = new ArrayList<Integer>();

		// Per cada centre
		for(int centre = 0 ; centre < Constants.nc ; centre++)
		{
			// Per cada hora
			for(int hora1 = 0 ; hora1 < Constants.ht + 1 ; hora1++)
			{
				LinkedList<Peticio> pp = estat.getPeticions(centre, hora1);

				// Per cada peticio
				for(it = pp.iterator(); it.hasNext();)
				{
					Peticio p = it.next();

					// Per cada hora diferent
					for(int hora2 = 1 ; hora2 < Constants.ht+1 ; hora2++)
					{						
						if(hora2 != hora1)
						{
							if(estat.desplazamientoPosible(p, centre, hora2))
							{
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
										a = true;
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
					}
				}
			}
		}
		for(int i = 0 ; i < _p.size(); i++)
		{
			Estat successor = new Estat(estat) ;
			if(!_xcap.get(i))
			{
				successor.desplazar(_p.get(i), _c.get(i), _hOri.get(i), _hDest.get(i));
				s = new String("Peticion " + _p.get(i).getID() + " del centro " + _c.get(i) + " con hora de entrega : " + _p.get(i).getH() + "h desplazada de " + _hOri.get(i) + "h a " + _hDest.get(i) + "h.");
			}
			else{
				peti = successor.removePeticio(_p.get(i), _c.get(i), _hOri.get(i));
				successor.canvi_camion(_c.get(i), _hDest.get(i), estat.getCap(_xcen.get(i), _xhor.get(i)), _xcen.get(i), _xhor.get(i), estat.getCap(_c.get(i), _hDest.get(i)));				
				s = new String("\nCapacitats intercanviades entre l'hora " + _hDest.get(i) + " del centre " + _c.get(i) + " (" + estat.getCap(_c.get(i), _hDest.get(i)) + "kgs) i l'hora " + _xhor.get(i) + " del centre " + _xcen.get(i) + " (" + estat.getCap(_xcen.get(i), _xhor.get(i)) + "kgs).");
				a = successor.addPeticio(peti, _c.get(i), _hDest.get(i));
				s = s.concat("Peticion " + _p.get(i).getID() + " del centro " + _c.get(i) + " con hora de entrega : " + _p.get(i).getH() + "h desplazada de " + _hOri.get(i) + "h a " + _hDest.get(i) + "h.");
			}
			retVal.add(new Successor(s, successor));
		}
		return retVal;
	}
}