package transports;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;

public class Estado 
{
	public int[] distriCap = new int[Constants.cap.length];
	public static char tipord;
	private Centre[] centres = new Centre[Constants.nc];

	
	// CONSTRUCTORES
	public Estado(int[] capacitats)
	{
		distriCap = capacitats ;
		for(int i = 0 ; i < Constants.nc ; i++) centres[i] = new Centre(i+1) ;
	}

	public Estado(Estado e)
	{
		distriCap = e.distriCap ;
		for(int i = 0 ; i < Constants.nc ; i++) centres[i] = new Centre(e.centres[i]);
	}

	
	// ESTADOS INICIALES
	public void estadoInicial(char tipo)
	{
		switch(tipo)
		{
		case 'a': 
			tipord = 'a';
			this.mesAviatPossible();
			break;
		case 'b': 
			tipord = 'b';
			this.perHora();
			break;
		case 'f':
			this.firstFit();
			break;
		case 'v': 
			this.buit();
			break;
		}
	}

	private void mesAviatPossible()
	{
		int i;
		this.camionsGreedy();
		for(i = 0 ; i < Constants.nc ; i++)
		{
			centres[i].ordena_noassign();
			centres[i].greedy();
		}
	}
	
	private void perHora()
	{
		int i, j, a, k ;
		boolean c, r[], tr ;
		Peticio p ;
		Transport ts[];
		Iterator<Peticio>[] it = new Iterator[Constants.nc];
		r = new boolean[Constants.nc];
		int[] cl = distriCap.clone();
		
		for(i = 0 ; i < Constants.nc ; i++)
		{
			centres[i].ordena_noassign();
			r[i] = true;
			it[i] = centres[i].h_setup();
		}
		
		c = true;
		
		while(c)
		{
			c = false;
			
			for(i = 0 ; i < Constants.nc ; i++)
			{
				if(r[i]){
					p = it[i].next();
					if(!it[i].hasNext())r[i] = false;
					cl = centres[i].h_step(p, cl, p.getH()+1-Constants.h_min);
				}
				c = c || r[i];
			}
		}
		
		for(i = 0 ; i < Constants.nc ; i++) centres[i].neteja();
		
		a = Constants.cap.length;
		
		for(i = 0 ; i < Constants.nc ; i++)
		{
			ts = centres[i].get_transports();
			
			for(j = 0 ; j < Constants.ht+1 ; j++)
			{
				tr = true;
				if(ts[j].getCap() == 0)
				{
					for(k = a-1 ; tr ; k--)
					{
						if(cl[k] > 0)
						{
							centres[i].set_camion(j, Constants.cap[k]);
							tr = false;
						}
					}
				}

			}
		}
	}

	private void firstFit()
	{
		int i;
		camionsGreedy();
		for(i = 0 ; i < Constants.nc ; i++)
		{
			centres[i].firstFitPorHora();
		}
	}

	private void buit()
	{
		this.camionsGreedy();
	}

	private void camionsGreedy()
	{
		int i, j, k, cont[], v, tot[], cam[];
		tot = new int[Constants.nc];
		cam = new int[Constants.nc];
		cont = new int[Constants.cap.length];
		k = Constants.cap.length-1;
		for(i = 0; i<=k; i++)cont[i] = distriCap[k];
		v = Constants.ht * Constants.cap[0];
		
		for(i = 0 ; i < Constants.nc ; i++)
		{
			tot[i] = centres[i].getCapNa();
			cam[i] = v;
		}
		
		for(j = 1 ; j < Constants.ht+1 ; j++)
		{
			for(i = 0 ; i < Constants.nc ; i++)
			{
				if(cont[k] == 0)
				{
					/*do*/	k--; /*while(cont[k]==0);*/
				}
				
				if(cam[i] < tot[i])
				{
					centres[i].set_camion(j, Constants.cap[k]);
					cont[k]--;
					cam[i] += Constants.cap[k]-Constants.cap[0];
				} else {
					if(k > 0 && cont[0] > 0)
					{
						centres[i].set_camion(j, Constants.cap[0]);
						cont[0]--;
					}
					else if(k > 1 && cont[1] > 0)
					{
						centres[i].set_camion(j, Constants.cap[1]);
						cont[1]--;
						cam[i] += Constants.cap[1]-Constants.cap[0];
					} else {
						centres[i].set_camion(j, Constants.cap[k]);
						cont[k]--;
						cam[i] += Constants.cap[k]-Constants.cap[0];
					}
				}
			}
		}
	}
	

	// HEURISTICAS
	// Ganancia
	public int getBeneficio(){ return beneficio(false); }
	public int getBeneficioVerbose(){ return beneficio(true); }
	private int beneficio(boolean verbose)
	{
		int b = 0 ;
		if(verbose)
		{
			for(int i = 0 ; i < Constants.nc ; i++) b += centres[i].getBeneficiVerbose();
			System.out.println("Beneficio total del estado : " + b);
		}
		else for(int i = 0 ; i < Constants.nc ; i++) b += centres[i].getBenefici();
		return b;
	}

	// Retraso
	public int getRetraso(){ return retraso(false); }	
	public int getRetrasoVerbose(){ return retraso(true); }	
	private int retraso(boolean verbose)
	{
		int r = 0 ;
		if(verbose)
		{
			for(int i = 0 ; i < Constants.nc ; i++) r += centres[i].getRetardVerbose();
			System.out.println("Retraso total del estado : " + r);
		}
		else for(int i = 0 ; i < Constants.nc ; i++) r += centres[i].getRetard();
		return r;	
	}


	// OPERADORES	
	public LinkedList<Peticio> getPeticiones(int centre, int hora)
	{
		return centres[centre].get_transports()[hora].get_peticiones();
	}

	// Desplazamiento de peticion
	public boolean desplazamientoPosible(Peticio p, int centre, int hDest)
	{
		return centres[centre].get_transports()[hDest].getCapR() >= p.getCan();
	}

	public boolean desplazar(Peticio p, int centre, int hOri, int hDest)
	{
		return centres[centre].desplazar_peticio(p, hOri, hDest);
	}

	// Intercambio de camiones
	public void intercambioCamiones(int centre1, int hora1, int capacitat1, int centre2, int hora2, int capacitat2)
	{
		centres[centre1].set_camion(hora1, capacitat1);
		centres[centre2].set_camion(hora2, capacitat2);
	}

	public Peticio removePeticion(Peticio p, int centre, int hora)
	{
		return centres[centre].removePeticio(p, hora);
	}

	public boolean addPeticion(Peticio p, Integer c, Integer h)
	{
		return centres[c.intValue()].addPeticio(p, h);
	}

	public int getCap(int c, int h)
	{
		return centres[c].get_transports()[h].getCap();
	}

	public int getCapO(int c, int h) {
		return centres[c].get_transports()[h].getCapO();
	}

	// Intercambio de peticiones
	public boolean intercambioPosible(int c, int h1, int h2, Peticio p1, Peticio p2)
	{
		return (centres[c].get_transports()[h1].getCapR()+p1.getCan() >= p2.getCan()
				&&
				centres[c].get_transports()[h2].getCapR()+p2.getCan() >= p1.getCan());
	}

	public boolean intercambioPeticiones(int c, int h1, int h2, Peticio p1, Peticio p2)
	{
		centres[c].get_transports()[h1].remove_peticio(p1);
		centres[c].get_transports()[h2].remove_peticio(p2);
		return (centres[c].get_transports()[h2].add_peticio(p1)
				&&
				centres[c].get_transports()[h1].add_peticio(p2));
	}


	// OUTPUT
	@Override
	public String toString()
	{
		String s = "";
		s = s.concat("Retard: " + this.getRetraso() + ", benefici: " + this.getBeneficio() +"\n");
		for(int i = 0 ; i < Constants.nc ; i++)
		{
			s = s.concat("Centre no." + (i+1) + " :\n");
			s = s.concat(centres[i].toString() + "\n");
		}

		return s;
	}


	public void writeFile(String file, String s1, String s2)
	{
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(file));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		out.println(s1);
		out.println(this);
		out.println(s2);
		out.close();
		//c		System.out.println(s1 + " escribit en " + file);
	}
}