package transports;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class Successor_SA implements SuccessorFunction
{
	@Override
	public List<Successor> getSuccessors(Object state) 
	{
		Estat estado = (Estat) state ;

		Peticio op1_p = null;
		int op1_c = 0;
		int op1_hOri = 0;
		int op1_hDest = 0;

		Peticio op2_p = null;
		int op2_c = 0;
		int op2_c2 = 0;
		int op2_hOri = 0;
		int op2_hDest = 0;
		int op2_xcen = 0;
		int op2_xhor = 0;
		int op2_xhord = 0;

		int op3_c = 0;
		int op3_h1 = 0;
		int op3_h2 = 0;
		Peticio op3_p1 = null;
		Peticio op3_p2 = null;


		do{
			double operador = Math.random();

			// OPERADOR : DESPLAZAMIENTO DE PETICIONES
			if(operador < (double)1/3)
			{
				int centre = (int) (Constants.nc * Math.random());
				int hora1 = (int) ((Constants.ht+1)*Math.random());
				int hora2 = 0;
				do
				{
					hora2 = (int)((Constants.ht+1)*Math.random());
				} while(hora1 == hora2);

				LinkedList<Peticio> pp = estado.getPeticions(centre, hora1);
				int np = (int) (pp.size()*Math.random());
				Peticio p = pp.get(np);

				if(estado.desplazamientoPosible(p, centre, hora2))
				{
					op1_p = p;
					op1_c = centre;
					op1_hOri = hora1;
					op1_hDest = hora2;
				}
			}

			// OPERADOR : INTERCAMBIO DE CAPACIDADES DE CAMIONES
			if(operador >= (double)1/3 && operador < (double)2/3)
			{
				boolean a;
				int j=0, ci, hi;
				Peticio pe = null;
				LinkedList<Peticio> petinoe = new LinkedList<Peticio>();

				int centre1 = (int) (Constants.nc * Math.random());
				int hora1 = (int) ((Constants.ht+1)*Math.random());
				int centre2 = 0;
				int hora2 = 0;
				do{
					centre2 = (int) (Constants.nc * Math.random());
					hora2 = (int)((Constants.ht+1)*Math.random());
				} while(estado.getCap(centre1, hora1) == estado.getCap(centre2, hora2));

				//Generacion estado
				if(estado.getCapO(centre1, hora1) <= estado.getCap(centre2, hora2) 
						&&
						estado.getCapO(centre2, hora2) <= estado.getCap(centre1, hora1))
				{
					a = true;
					if(estado.getCap(centre1, hora1) > estado.getCap(centre2, hora2))
					{
						ci = centre2;
						hi = hora2;
					} else {
						ci = centre1;
						hi = hora1;
					}

					petinoe = estado.getPeticions(ci, 0);

					if(petinoe.size() >= 1)
					{
						j = -1;
						pe = petinoe.getFirst();
						a = false;
					} else {
						while(a)
						{
							for(j = Constants.ht; a && j >= 1; j--)
							{
								if(j != hi)
								{
									petinoe = estado.getPeticions(ci, j);

									if(petinoe.size() >= 1)
									{
										pe = petinoe.getFirst();
										a = false;
									}
								}
							}
						}
					}
					if(!a){
						j++;
						op2_p = pe; 
						op2_c = centre1;
						op2_c2 = centre2;
						op2_hOri = hora1;
						op2_hDest = hora2;
						op2_xcen = ci;
						op2_xhor = j;
						op2_xhord = hi;
					}
				}
			}

			// OPERADOR : INTERCAMBIO DE PETICIONES
			if(operador >= (double)2/3)
			{
				int centre = (int) (Constants.nc * Math.random());
				int hora1 = (int) ((Constants.ht+1)*Math.random());
				int hora2 = 0;
				do{
					hora2 = (int)((Constants.ht+1)*Math.random());
				} while(hora1 == hora2);

				LinkedList<Peticio> pp1 = estado.getPeticions(centre, hora1);
				int np1 = (int) (pp1.size()*Math.random());
				Peticio p1 = pp1.get(np1);

				LinkedList<Peticio> pp2 = estado.getPeticions(centre, hora2);
				int np2 = (int) (pp2.size()*Math.random());
				Peticio p2 = pp2.get(np2);

				if(estado.intercambioPosible(centre, hora1, hora2, p1, p2))
				{
					op3_c = centre;
					op3_h1 = hora1;
					op3_h2 = hora2;
					op3_p1 = p1;
					op3_p2 = p2;
				}
			}

		} while(op1_p == null && op2_p == null && op3_p1 == null);

		
		// Construccion del successor
		String s = "";
		Estat successor = new Estat(estado);

		if(op1_p != null)
		{
			successor.desplazar(op1_p, op1_c, op1_hOri, op1_hDest);
			s = new String("Peticion " + op1_p.getID() + " del centro " + (op1_c+1) +
					" con hora de entrega : " + op1_p.getH() +
					"h desplazada de " + (op1_hOri+7) + "h a " + (op1_hDest+7) + "h.");
		}
		else if (op2_p != null) 
		{
			Peticio peti = successor.removePeticio(op2_p, op2_xcen, op2_xhor);
			successor.intercambioCamiones(op2_c, op2_hOri, estado.getCap(op2_c2, op2_hDest), op2_c2, op2_hDest, estado.getCap(op2_c, op2_hOri));				
			s = new String("Capacitats intercanviades entre l'hora " + (op2_hOri+7) + " del centre " + (op2_c+1) + " (" + estado.getCap(op2_c, op2_hOri) + "kgs) i l'hora " + (op2_hDest+7) + " del centre " + (op2_c2+1) + " (" + estado.getCap(op2_c2, op2_hDest) + "kgs).");

			successor.addPeticio(peti, op2_xcen, op2_xhord);
			s = s.concat("Peticion " + op2_p.getID() + " del centro " + (op2_xcen+1) + " con hora de entrega : " + op2_p.getH() + "h desplazada de " + (op2_xhor+7) + "h a " + (op2_xhord+7) + "h.");			
		}
		else if (op3_p1 != null)
		{
			successor.intercambioPeticiones(op3_c, op3_h1, op3_h2, op3_p1, op3_p2);
			s = new String("Se han intercambiado las peticiones " + op3_p1.getID() + 
					" (" + (op3_h1+7) + "h) y " + op3_p2.getID() + " (" + (op3_h2+7) + ") del centro " +
					(op3_c+1));
		}			
		s = s.concat(" El benefici passa de " + estado.getBenefici() + "€ a " + successor.getBenefici() + "€.");
		s = s.concat(" El retard passa de " + estado.getRetard() + "h a " + successor.getRetard() + "h.");

		ArrayList<Successor> retVal = new ArrayList<Successor>();
		retVal.add(new Successor(s, successor));

		System.out.println(retVal.size() + " successores generados.");
		return retVal;
	}
}