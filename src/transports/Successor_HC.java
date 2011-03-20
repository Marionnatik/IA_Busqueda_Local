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

		ArrayList<Successor> retVal = new ArrayList<Successor>();

		Estat estat = (Estat) state ;
		
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
			//System.out.println("Centre " + centre);

			// Per cada hora
			for(int hora1 = 0 ; hora1 < Constants.ht + 1 ; hora1++)
			{
				//System.out.println("Hora " + hora1);
				LinkedList<Peticio> pp = estat.getPeticions(centre, hora1);
				int i = 1;

				// Per cada peticio
				for(it = pp.iterator(); it.hasNext();)
				{
					//System.out.println("Peticio " + i + " de " + pp.size());
					Peticio p = it.next();
					//System.out.println(p);
					i++;

					// Per cada hora diferent
					for(int hora2 = 1 ; hora2 < Constants.ht+1 ; hora2++)
					{						
						if(hora2 != hora1)
						{
							if(estat.desplazamientoPosible(p, centre, hora2))
							{
								//System.out.println("Se puede desplazar la peticion a la hora " + hora2);
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
								//no puedo desplazar
								//System.out.println("No se puede desplazar la peticion a la hora " + hora2);

								capinsuf = estat.getCap(centre, hora2);								
								if(capinsuf < Constants.cap[Constants.cap.length-1])
								{
									if(estat.getCap(centre, hora1) > capinsuf && estat.getCapO(centre, hora1)-p.getCan() <= capinsuf)
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
		int ben = 0;
		for(int i = 0 ; i < _p.size() ; i++)
		{
			Estat successor = estat ;
			
			successor.desplazar(_p.get(i), _c.get(i), _hOri.get(i), _hDest.get(i));
			String S = new String("Q:" + _p.get(i).getCan() + " H:" + _p.get(i).getH() + " de " + _hOri.get(i) + " fins " + _hDest.get(i) + "en" + _c.get(i));
			
			if(_xcap.get(i))
			{
				estat.canvi_camion(_c.get(i), _hDest.get(i), estat.getCap(_xcen.get(i), _xhor.get(i)), _xcen.get(i), _xhor.get(i), estat.getCap(_c.get(i), _hDest.get(i)));				
				S = S.concat("Capacitats intercanviades entre l'hora " + _hDest.get(i) + " del centre " + _c.get(i) + " i l'hora " + _xhor.get(i) + " del centre " + _xcen.get(i));
			}
			
			if(estat.getBenefici() < successor.getBenefici()) ben++;
			
			retVal.add(new Successor(S, successor));
		}
		
		System.out.println(retVal.size() + " successores generados, " + ben + " mejores.");
		
		return retVal;
	}
}