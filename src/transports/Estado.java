package transports;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class Estado 
{
	public int[] distriCap = new int[Constants.cap.length];
	public static char tipord;
	private Centre[] centres = new Centre[Constants.nc];

	
	// CONSTRUCTORES
	public Estado(int[] capacitats)
	{
		distriCap = capacitats ;
		for(int i = 0 ; i < Constants.nc ; i++) centres[i] = new Centre() ;
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
			this.cuantoAntes();
			break;
		case 'b': 
			tipord = 'b';
			this.perHora();
			break;
		case 'f':
			this.firstFit();
			break;
		case 'v': 
			this.vacio();
			break;
		}
	}

	private void cuantoAntes()
	{
		int i;
		capacidadesGreedy();
		for(i = 0 ; i < Constants.nc ; i++)
		{
			centres[i].ordenarNoEntregadas();
			centres[i].peticionesGreedy();
		}
	}
	
	private void perHora()
	{
		int i, j, a, k ;
		boolean c, r[], tr ;
		Peticion p ;
		Transport ts[];
		Iterator<Peticion>[] it = new Iterator[Constants.nc];
		r = new boolean[Constants.nc];
		int[] cl = distriCap.clone();
		
		for(i = 0 ; i < Constants.nc ; i++)
		{
			centres[i].ordenarNoEntregadas();
			r[i] = true;
			it[i] = centres[i].getTransports()[0].getPeticiones().iterator();
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
					cl = centres[i].perHoraStep(p, cl, p.getH()+1-Constants.h_min);
				}
				c = c || r[i];
			}
		}
		
		for(i = 0 ; i < Constants.nc ; i++) centres[i].cleanUp();
		
		a = Constants.cap.length;
		
		for(i = 0 ; i < Constants.nc ; i++)
		{
			ts = centres[i].getTransports();
			
			for(j = 0 ; j < Constants.ht+1 ; j++)
			{
				tr = true;
				if(ts[j].getCap() == 0)
				{
					for(k = a-1 ; tr ; k--)
					{
						if(cl[k] > 0)
						{
							centres[i].setCapacidad(j, Constants.cap[k]);
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
		capacidadesGreedy();
		for(i = 0 ; i < Constants.nc ; i++)
		{
			centres[i].firstFit();
		}
	}

	private void vacio()
	{
		this.capacidadesGreedy();
	}

	private void capacidadesGreedy()
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
			tot[i] = centres[i].getTransports()[0].getCapO();
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
					centres[i].setCapacidad(j, Constants.cap[k]);
					cont[k]--;
					cam[i] += Constants.cap[k]-Constants.cap[0];
				} else {
					if(k > 0 && cont[0] > 0)
					{
						centres[i].setCapacidad(j, Constants.cap[0]);
						cont[0]--;
					}
					else if(k > 1 && cont[1] > 0)
					{
						centres[i].setCapacidad(j, Constants.cap[1]);
						cont[1]--;
						cam[i] += Constants.cap[1]-Constants.cap[0];
					} else {
						centres[i].setCapacidad(j, Constants.cap[k]);
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
	public LinkedList<Peticion> getPeticiones(int centre, int hora)
	{
		return centres[centre].getTransports()[hora].getPeticiones();
	}

	// Desplazamiento de peticion
	public boolean desplazamientoPosible(Peticion p, int centre, int hDest)
	{
		return centres[centre].getTransports()[hDest].getCapR() >= p.getCan();
	}

	public boolean desplazar(Peticion p, int centre, int hOri, int hDest)
	{
		return centres[centre].desplazarPeticion(p, hOri, hDest);
	}

	// Intercambio de camiones
	public void intercambioCamiones(int centre1, int hora1, int capacitat1, int centre2, int hora2, int capacitat2)
	{
		centres[centre1].setCapacidad(hora1, capacitat1);
		centres[centre2].setCapacidad(hora2, capacitat2);
	}

	public Peticion removePeticion(Peticion p, int centre, int hora)
	{
		return centres[centre].removePeticio(p, hora);
	}

	public boolean addPeticion(Peticion p, Integer c, Integer h)
	{
		return centres[c.intValue()].addPeticio(p, h);
	}

	public int getCap(int c, int h)
	{
		return centres[c].getTransports()[h].getCap();
	}

	public int getCapO(int c, int h) {
		return centres[c].getTransports()[h].getCapO();
	}

	// Intercambio de peticiones
	public boolean intercambioPosible(int c, int h1, int h2, Peticion p1, Peticion p2)
	{
		return (centres[c].getTransports()[h1].getCapR()+p1.getCan() >= p2.getCan()
				&&
				centres[c].getTransports()[h2].getCapR()+p2.getCan() >= p1.getCan());
	}

	public boolean intercambioPeticiones(int c, int h1, int h2, Peticion p1, Peticion p2)
	{
		centres[c].getTransports()[h1].removePeticio(p1);
		centres[c].getTransports()[h2].removePeticio(p2);
		return (centres[c].getTransports()[h2].addPeticio(p1)
				&&
				centres[c].getTransports()[h1].addPeticio(p2));
	}

	
	// OUTPUT
	public boolean toFile(String fileOut) {
		PrintWriter out = null;
		try {
			int i;
			out = new PrintWriter(new FileWriter(fileOut));
			for(i = 0; i<Constants.cap.length; i++)out.write(distriCap[i] + " ");
			out.println();
			out.println(Principal.nbPeticions);
			for(i = 0; i<Constants.nc; i++){
				for(ListIterator<Peticion> it = centres[i].getTransports()[0].getPeticiones().listIterator();it.hasNext();){
					out.print(i);
					Peticion p = it.next();
					out.println(p.toFile());
				}
			}
			out.close();
			return true;		
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
	}

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
