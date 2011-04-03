package transports;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class Successor_HC implements SuccessorFunction
{
	@Override
	public List<Successor> getSuccessors(Object state)
	{
		Estado estado = (Estado) state ;
		
		ArrayList<Successor> retVal = new ArrayList<Successor>();
		String s;
		
		Iterator<Peticio> it ;


		// OPERADOR : DESPLAZAMIENTO DE PETICIONES

		ArrayList<Peticio> op1_p = new ArrayList<Peticio>();
		ArrayList<Integer> op1_c = new ArrayList<Integer>();
		ArrayList<Integer> op1_hOri = new ArrayList<Integer>();
		ArrayList<Integer> op1_hDest = new ArrayList<Integer>();

		for(int centre = 0 ; centre < Constants.nc ; centre++)
		{
			for(int hora1 = 0 ; hora1 < Constants.ht+1 ; hora1++)
			{
				LinkedList<Peticio> pp = estado.getPeticiones(centre, hora1);

				for(it = pp.iterator(); it.hasNext();)
				{
					Peticio p = it.next();

					for(int hora2 = 0 ; hora2 < Constants.ht+1 ; hora2++)
					{						
						if(hora2 != hora1)
						{
							if(estado.desplazamientoPosible(p, centre, hora2))
							{
								op1_p.add(p);
								op1_c.add(centre);
								op1_hOri.add(hora1);
								op1_hDest.add(hora2);
							}
						}
					}
				}
			}
		}

		for(int i = 0 ; i < op1_p.size(); i++)
		{
			Estado successor = new Estado(estado) ;
			successor.desplazar(op1_p.get(i), op1_c.get(i), op1_hOri.get(i), op1_hDest.get(i));
			s = new String("Peticion " + op1_p.get(i).getID() + " del centro " + (op1_c.get(i)+1) +
					" con hora de entrega : " + op1_p.get(i).getH() +
					"h desplazada de " + (op1_hOri.get(i)+7) + "h a " + (op1_hDest.get(i)+7) + "h.");

			s = s.concat(" El benefici passa de " + estado.getBeneficio() + "€ a " + successor.getBeneficio() + "€.");
			s = s.concat(" El retard passa de " + estado.getRetraso() + "h a " + successor.getRetraso() + "h.");

			retVal.add(new Successor(s, successor));
		}
		

		// OPERADOR : INTERCAMBIO DE CAPACIDADES DE CAMIONES

		ArrayList<Peticio> op2_p = new ArrayList<Peticio>();
		ArrayList<Integer> op2_c = new ArrayList<Integer>();
		ArrayList<Integer> op2_c2 = new ArrayList<Integer>();
		ArrayList<Integer> op2_hOri = new ArrayList<Integer>();
		ArrayList<Integer> op2_hDest = new ArrayList<Integer>();
		ArrayList<Integer> op2_xcen = new ArrayList<Integer>();
		ArrayList<Integer> op2_xhor = new ArrayList<Integer>();
		ArrayList<Integer> op2_xhord = new ArrayList<Integer>();

		int ci, hi, j = 0;
		boolean a ;
		LinkedList<Peticio> petinoe = new LinkedList<Peticio>();
		
		for(int centre = 0 ; centre < Constants.nc ; centre++)
		{
			for(int hora1 = 1 ; hora1 < Constants.ht + 1 ; hora1++)
			{
				for(int centre2 = 0 ; centre2 < Constants.nc ; centre2++)
				{
					for(int hora2 = 1 ; hora2 < Constants.ht+1 ; hora2++)
					{						
						if(centre != centre2 || hora1 != hora2)
						{
							if(estado.getCap(centre, hora1) != estado.getCap(centre2, hora2))
							{
								if(estado.getCapO(centre, hora1) <= estado.getCap(centre2, hora2)
										&&
										estado.getCapO(centre2, hora2)<=estado.getCap(centre, hora1))
								{
									a = true;

									if(estado.getCap(centre, hora1) > estado.getCap(centre2, hora2))
									{
										ci = centre2;
										hi = hora2;
									} else {
										ci = centre;
										hi = hora1;
									}

									petinoe = estado.getPeticiones(ci, 0);
									Peticio pe = null;

									if(petinoe.size() >= 1)
									{
										j = -1;
										pe = petinoe.getFirst();
										a = false;
									} else {
										while(a)
										{
											for(j = Constants.ht ; a && j >= 1 ; j--)
											{
												if(j != hi)
												{
													petinoe = estado.getPeticiones(ci, j);

													if(petinoe.size() >= 1)
													{
														pe = petinoe.getFirst();
														a = false;
													}
												}
											}
										}
									}
									if(!a)
									{
										j++;
										op2_p.add(pe);
										op2_c.add(centre);
										op2_c2.add(centre2);
										op2_hOri.add(hora1);
										op2_hDest.add(hora2);
										op2_xcen.add(ci);
										op2_xhor.add(j);
										op2_xhord.add(hi);
									}
								}
							}
						}
					}
				}
			}
		}
		
		for(int i = 0 ; i < op2_c.size(); i++)
		{
			Estado successor = new Estado(estado) ;
			
			Peticio peti = successor.removePeticion(op2_p.get(i), op2_xcen.get(i), op2_xhor.get(i));
			successor.intercambioCamiones(op2_c.get(i), op2_hOri.get(i), estado.getCap(op2_c2.get(i), op2_hDest.get(i)), op2_c2.get(i), op2_hDest.get(i), estado.getCap(op2_c.get(i), op2_hOri.get(i)));				
			s = new String("Capacitats intercanviades entre l'hora " + (op2_hOri.get(i)+7) + " del centre " + (op2_c.get(i)+1) + " (" + estado.getCap(op2_c.get(i), op2_hOri.get(i)) + "kgs) i l'hora " + (op2_hDest.get(i)+7) + " del centre " + (op2_c2.get(i)+1) + " (" + estado.getCap(op2_c2.get(i), op2_hDest.get(i)) + "kgs).");
			
			successor.addPeticion(peti, op2_xcen.get(i), op2_xhord.get(i));
			s = s.concat("Peticion " + op2_p.get(i).getID() + " del centro " + (op2_xcen.get(i)+1) + " con hora de entrega : " + op2_p.get(i).getH() + "h desplazada de " + (op2_xhor.get(i)+7) + "h a " + (op2_xhord.get(i)+7) + "h.");			

			s = s.concat(" El benefici passa de " + estado.getBeneficio() + "€ a " + successor.getBeneficio() + "€.");
			s = s.concat(" El retard passa de " + estado.getRetraso() + "h a " + successor.getRetraso() + "h.");

			retVal.add(new Successor(s, successor));
		}
		

/*		// OPERADOR : INTERCAMBIO DE PETICIONES
		ArrayList<Integer> op3_c = new ArrayList<Integer>();
		ArrayList<Integer> op3_h1 = new ArrayList<Integer>();
		ArrayList<Integer> op3_h2 = new ArrayList<Integer>();
		ArrayList<Peticio> op3_p1 = new ArrayList<Peticio>();
		ArrayList<Peticio> op3_p2 = new ArrayList<Peticio>();
		
		for(int centre = 0 ; centre < Constants.nc ; centre++)
		{
			for(int hora1 = 0 ; hora1 < Constants.ht + 1 ; hora1++)
			{
				LinkedList<Peticio> pp1 = estado.getPeticiones(centre, hora1);

				for(it = pp1.iterator(); it.hasNext();)
				{
					Peticio p1 = it.next();

					for(int hora2 = 0 ; hora2 < Constants.ht+1 ; hora2++)
					{						
						if(hora2 != hora1)
						{
							LinkedList<Peticio> pp2 = estado.getPeticiones(centre, hora2);

							for(it = pp2.iterator(); it.hasNext();)
							{
								Peticio p2 = it.next();
								if(estado.intercambioPosible(centre, hora1, hora2, p1, p2))
								{
									op3_c.add(centre);
									op3_h1.add(hora1);
									op3_h2.add(hora2);
									op3_p1.add(p1);
									op3_p2.add(p2);
								}
							}
						}
					}
				}
			}
		}

		for(int i = 0 ; i < op3_c.size(); i++)
		{
			Estado successor = new Estado(estado) ;
			
			successor.intercambioPeticiones(op3_c.get(i), op3_h1.get(i), op3_h2.get(i), op3_p1.get(i), op3_p2.get(i));
			s = new String("Se han intercambiado las peticiones " + op3_p1.get(i).getID() + 
					" (" + (op3_h1.get(i)+7) + "h) y " + op3_p2.get(i).getID() + " (" + (op3_h2.get(i)+7) + ") del centro " +
					(op3_c.get(i)+1));
			
			s = s.concat(" El benefici passa de " + estado.getBeneficio() + "€ a " + successor.getBeneficio() + "€.");
			s = s.concat(" El retard passa de " + estado.getRetraso() + "h a " + successor.getRetraso() + "h.");


			if(estado.getBeneficio() < successor.getBeneficio()) successor.getBeneficioVerbose();
			retVal.add(new Successor(s, successor));
		}*/
//		System.out.println(retVal.size() + " successores generados.");
		return retVal;
	}
}