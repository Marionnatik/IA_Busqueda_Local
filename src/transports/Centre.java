package transports;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class Centre
{
	private Transport[] horas = new Transport[Constants.ht+1];
	private ArrayList<Peticion> entregades = new ArrayList<Peticion>();

	// CONSTRUCTORES
	public Centre()
	{
		horas[0] = new Transport(0);
		horas[0].setCap(Integer.MAX_VALUE);
		
		for(int i = 1 ; i < Constants.ht+1 ; i++)
		{
			horas[i] = new Transport(i+Constants.h_min-1) ;
		}
	}

	public Centre(Centre c)
	{
		for(int i = 0 ; i < Constants.ht+1 ; i++)
		{
			horas[i] = new Transport(c.horas[i]) ;
		}
	}

	// GETTERS / SETTERS
	public Transport[] getTransports()
	{
		return horas; 
	}

	public boolean setCapacidad(int h, int c)
	{
		return horas[h].setCap(c);
	}

	// ESTADOS INICIALES
	public void ordenarNoEntregadas()
	{
		horas[0].ordenar();
	}

	public void peticionesGreedy()
	{
		Peticion p;
		int i, ini;
		boolean ep;
		LinkedList<Peticion> petis;
		LinkedList<Peticion> puestas;
		Iterator<Peticion> it;
		petis = horas[0].getPeticiones();
		ini = 1;
		puestas = new LinkedList<Peticion>();
		
		for(it = petis.iterator(); it.hasNext();)
		{
			p = it.next();
			ep = false;
			
			for(i=ini; i<horas.length && ep == false; i++)
			{
				/*if(p.getCan() < hores[i].getCapR()){
					hores[0].remove_peticio(p);
					hores[i].add_peticio(p);
					ep = true;
				}*/
				//Si no funciona, utiliza lo de alto
				
				if(horas[i].addPeticio(p))
				{
					puestas.add(p);
					ep = true;
					
					if(i == ini && horas[i].getCapR() == 0) ini++;
				}
			}

		}
		for(it = puestas.iterator(); it.hasNext();)
		{
			p = it.next();
			horas[0].removePeticio(p);
		}
	}

	public int[] perHoraStep(Peticion p, int[] cl, int h)
	{
		int i, j, num;
		boolean r, r2, b;
		
		if(horas[h].getCap()==0)
		{
			r = true;
			
			for(i = 0 ; i < Constants.cap.length && r ; i++)
			{
				if(Constants.cap[i] >= p.getCan() && cl[i] > 0)
				{
					r = false;
					cl[i]--;
					horas[h].setCap(Constants.cap[i]);
					if(!horas[h].addPeticio(p))System.out.println("Ostres! Tenim un problema!");
					else entregades.add(p); 
				}
			}
		}
		else if(!horas[h].addPeticio(p))
		{
			num = horas[h].getCap();
			b = false;
			
			for(i = cl.length-1; i > 0; i--) if(num < Constants.cap[i] && cl[i] > 0) b = true;
			
			if(b)
			{
				r = true;
				r2 = true;
				
				for(i = 1;r && i < cl.length;i++)
				{	
					if(Constants.cap[i] > p.getCan() + horas[h].getCapO() && cl[i] > 0)
					{
						r = false;
						cl[i]--;
						for(j = 0 ; r2 ; j++)
						{
							if(horas[h].getCap() == Constants.cap[j])
							{
								r2 = false;
								cl[j]++;
							}
						}	
						horas[h].setCap(Constants.cap[i]);
						if(!horas[h].addPeticio(p))System.out.println("Ostres! Tenim un problema!");
						else entregades.add(p);
					}
				}
			}
			else
			{

				//				System.out.println("Sono qui, centre n° " + n + " - " + p.toString());
				if(h == 1)
				{
					if(p.getH()!=17)
					{
						//						System.out.println(h + " = " + 1 + " ");
						this.perHoraStep(p, cl, p.getH()-Constants.h_min+1+1);
					}
				}
				else if(h <= p.getH()-Constants.h_min+1)
				{
					num = p.getH()-Constants.h_min+1;
					//					System.out.println(h + " <= " + num + " ");
					this.perHoraStep(p, cl, h-1);
				}
				else if(h != Constants.ht)
				{
					//					System.out.println(h + "!=" + Constants.ht + " ");
					this.perHoraStep(p, cl, h+1);
				}
			}
		}
		else entregades.add(p);
		return cl;
	}

	public void firstFit()
	{
		Peticion p;
		boolean f;
		int i;
		LinkedList<Peticion> noEntregadas = horas[0].getPeticiones();
		
		for(Iterator<Peticion> it = noEntregadas.iterator(); it.hasNext();)
		{
			p = it.next();
			if(horas[p.getH()-Constants.h_min+1].addPeticio(p))
			{
				entregades.add(p);
			}
		}
		
		for(Iterator <Peticion>it2 = entregades.iterator(); it2.hasNext();)
		{
			p = it2.next();
			noEntregadas.remove(p);
		}
		
		for(Iterator<Peticion> it = noEntregadas.iterator(); it.hasNext();)
		{
			p = it.next();
			f = true;
			for(i = p.getH()-Constants.h_min; i>0 && f; i--)
			{
				if(horas[i].addPeticio(p)){
					entregades.add(p);
					f = false;
				}
			}
			for(i = p.getH()+Constants.h_min+1+1; i<Constants.ht && f; i++)
			{
				if(horas[i].addPeticio(p))
				{
					entregades.add(p);
					f = false;
				}
			}
		}
		cleanUp();
	}

	public void cleanUp()
	{
		Peticion p;
		for(Iterator<Peticion> it = entregades.iterator(); it.hasNext();)
		{
			p = it.next();
			horas[0].removePeticio(p);		
		}
		horas[0].capfix();
		entregades.clear();
	}
	

	// HEURISTICAS
	// Ganancia
	public int getBenefici(){ return benef(false); }
	public int getBeneficiVerbose(){ return benef(true); }
	private int benef(boolean verb)
	{
		int b;
		if(verb)
		{
			b = horas[0].getBeneficiVerbose();
			for(int i = 1 ; i < Constants.ht+1 ; i++) b += horas[i].getBeneficiVerbose();
			System.out.println("Benefici del centre : " + b);
		} else {
			b = horas[0].getBenefici();
			for(int i = 1 ; i < Constants.ht+1 ; i++) b += horas[i].getBenefici();
		}
		return b ;
	}

	// Retard
	public int getRetard(){ return retard(false); }	
	public int getRetardVerbose(){ return retard(true); }	
	private int retard(boolean verb)
	{
		int r = 0;
		if(verb)
		{
			for(int i = 0 ; i < Constants.ht+1 ; i++) r += horas[i].getRetardVerbose();
			System.out.println("Retard del centre : " + r);
		}
		else for(int i = 0 ; i < Constants.ht+1 ; i++) r += horas[i].getRetard();
		return r;
	}


	// OPERADORES	
	public boolean desplazarPeticion(Peticion p, int h1, int h2)
	{
		boolean be;
		be = horas[h2].addPeticio(p);
		if(be) horas[h1].removePeticio(p);
		return be;
	}

	public Peticion removePeticio(Peticion peticio, Integer h)
	{
		return horas[h].removePeticio(peticio);
	}

	public boolean addPeticio(Peticion peti, Integer h)
	{
		return horas[h].addPeticio(peti);
	}
	
	
	// OUTPUT
	@Override
	public String toString()
	{
		String s = "";

		for(int i = 0 ; i < Constants.ht+1 ; i++)
		{
			s = s.concat(horas[i].toString());
		}

		return s;
	}
}