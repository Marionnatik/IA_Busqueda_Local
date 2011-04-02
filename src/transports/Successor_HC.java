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

		int ci, hi, j = 0;
		boolean a ;
		Iterator<Peticio> it ;
		Peticio peti, pe = null;
		ArrayList<Successor> retVal = new ArrayList<Successor>();
		LinkedList<Peticio> petinoe = new LinkedList<Peticio>();
		String s;
		Estat estat = (Estat) state ;

		ArrayList<Peticio> _p = new ArrayList<Peticio>();
		ArrayList<Integer> _c = new ArrayList<Integer>();
		ArrayList<Integer> _c2 = new ArrayList<Integer>();
		ArrayList<Integer> _hOri = new ArrayList<Integer>();
		ArrayList<Integer> _hDest = new ArrayList<Integer>();
		ArrayList<Boolean> _xcap = new ArrayList<Boolean>();
		ArrayList<Integer> _xcen = new ArrayList<Integer>();
		ArrayList<Integer> _xhor = new ArrayList<Integer>();
		ArrayList<Integer> _xhord = new ArrayList<Integer>();
		
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
					for(int hora2 = 0 ; hora2 < Constants.ht+1 ; hora2++)
					{						
						if(hora2 != hora1)
						{
							if(estat.desplazamientoPosible(p, centre, hora2))
							{
								_p.add(p);
								_c.add(centre);
								_c2.add(-1);
								_hOri.add(hora1);
								_hDest.add(hora2);
								_xcap.add(false);
								_xcen.add(-1);
								_xhor.add(-1);
								_xhord.add(-1);
							}
						}
					}
				}
			}
		}
		for(int centre = 0 ; centre < Constants.nc ; centre++)
		{
			// Per cada hora
			for(int hora1 = 1 ; hora1 < Constants.ht + 1 ; hora1++)
			{
				for(int centre2 = 0 ; centre2 < Constants.nc ; centre2++)
				{
					for(int hora2 = 1 ; hora2 < Constants.ht+1 ; hora2++)
					{						
						if(centre!=centre2 || hora1 != hora2){
							if(estat.getCap(centre, hora1) != estat.getCap(centre2, hora2)){
								if(estat.getCapO(centre, hora1)<=estat.getCap(centre2, hora2) && estat.getCapO(centre2, hora2)<=estat.getCap(centre, hora1)){
									a = true;
									if(estat.getCap(centre, hora1)>estat.getCap(centre2, hora2)){
										ci = centre2;
										hi = hora2;
									}
									else{
										ci = centre;
										hi = hora1;
									}
									petinoe = estat.getPeticions(ci, 0);
									if(petinoe.size()>=1){
										j = -1;
										pe = petinoe.getFirst();
										a = false;
									}
									else{
										while(a){
											for(j = Constants.ht; a && j>=1; j--){
												if(j!=hi){
												petinoe = estat.getPeticions(ci, j);
												if(petinoe.size()>=1){
													pe = petinoe.getFirst();
													a = false;
												}
												}
											}
										}
									}
									if(!a){
									j++;
									_p.add(pe); 
									_c.add(centre);
									_c2.add(centre2);
									_hOri.add(hora1);
									_hDest.add(hora2);
									_xcen.add(ci);
									_xcap.add(true);
									_xhor.add(j);
									_xhord.add(hi);
									}
								}
							}
						}
					}
				}
			}
		}
		for(int i = 0 ; i < _c.size(); i++)
		{
			Estat successor = new Estat(estat) ;
			if(!_xcap.get(i))
			{
				successor.desplazar(_p.get(i), _c.get(i), _hOri.get(i), _hDest.get(i));
				s = new String("Peticion " + _p.get(i).getID() + " del centro " + (_c.get(i)+1) + " con hora de entrega : " + _p.get(i).getH() + "h desplazada de " + (_hOri.get(i)+7) + "h a " + (_hDest.get(i)+7) + "h.");
			}
			else
			{
				peti = successor.removePeticio(_p.get(i), _xcen.get(i), _xhor.get(i));
				successor.canvi_camion(_c.get(i), _hOri.get(i), estat.getCap(_c2.get(i), _hDest.get(i)), _c2.get(i), _hDest.get(i), estat.getCap(_c.get(i), _hOri.get(i)));				
				s = new String("Capacitats intercanviades entre l'hora " + (_hOri.get(i)+7) + " del centre " + (_c.get(i)+1) + " (" + estat.getCap(_c.get(i), _hOri.get(i)) + "kgs) i l'hora " + (_hDest.get(i)+7) + " del centre " + (_c2.get(i)+1) + " (" + estat.getCap(_c2.get(i), _hDest.get(i)) + "kgs).");
				a = successor.addPeticio(peti, _xcen.get(i), _xhord.get(i));
				s = s.concat("Peticion " + _p.get(i).getID() + " del centro " + (_xcen.get(i)+1) + " con hora de entrega : " + _p.get(i).getH() + "h desplazada de " + (_xhor.get(i)+7) + "h a " + (_xhord.get(i)+7) + "h.");			
			}
			int b1 = estat.getBenefici();
			int b2 = successor.getBenefici();
			s = s.concat(" El benefici passa de " + b1 + "€ a " + b2 + "€.");

			int r1 = estat.getRetard();
			int r2 = successor.getRetard();
			s = s.concat(" El retard passa de " + r1 + "h a " + r2 + "h.");

			retVal.add(new Successor(s, successor));
		}
//		System.out.println(retVal.size() + " successores generados.");
		return retVal;
	}
}