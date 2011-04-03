package transports;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class Estat 
{
	public int[] distriCap = new int[Constants.cap.length];
	public static char tipord;
	private Centre[] centres = new Centre[Constants.nc];

	public Estat(int[] capacitats)
	{
		distriCap = capacitats ;
		for(int i = 0 ; i < Constants.nc ; i++) centres[i] = new Centre() ;
	}

	public Estat(Estat e)
	{
		distriCap = e.distriCap ;
		for(int i = 0 ; i < Constants.nc ; i++) centres[i] = new Centre(e.centres[i]);
	}

	public void estadoInicial(char tipo){
		// Hi han diferentes tipologies
		switch(tipo) {
		case 'a': 
			tipord = 'a';
			this.mes_aviat_possible();
			break;
		case 'b': 
			tipord = 'b';
			this.per_hora();
			break;
		case 'f':
			this.first_fit();
			break;
		case 'r': 
			this.random();
			break;
		case 'v': 
			this.buit();
			break;
		}
	}

	private void first_fit() {
		int i;
		camions_greedy();
		for(i = 0 ; i < Constants.nc ; i++){
			centres[i].ff_2r();
		}
	}

	private void per_hora() {
		int i, j, a, k;
		boolean c, r[], tr;
		Peticio p;
		Transport ts[];
		Iterator<Peticio>[] it = new Iterator[Constants.nc];
		r = new boolean[Constants.nc];
		int[] cl = distriCap.clone();
		for(i = 0 ; i < Constants.nc ; i++){
			centres[i].ordena_noassign();
			r[i] = true;
			it[i] = centres[i].h_setup();
		}
		c = true;
		while(c){
			c = false;
			for(i = 0 ; i < Constants.nc ; i++){
				if(r[i]){
					p = it[i].next();
					if(!it[i].hasNext())r[i] = false;
					cl = centres[i].h_step(p, cl, p.getH()+1-Constants.h_min);
				}
				c = c || r[i];
			}
		}
		for(i = 0 ; i < Constants.nc ; i++)centres[i].neteja();
		a = Constants.cap.length;
		// comprovo que hi hagi un camion cada hora
		for(i = 0; i<Constants.nc; i++){
			ts = centres[i].get_transports();
			for(j = 0; j< Constants.ht+1; j++){
				tr = true;
				if(ts[j].getCap()==0){
					for(k = a-1; tr; k--){
						if(cl[k]>0){
							centres[i].set_camion(j, Constants.cap[k]);
							tr = false;
						}
					}
				}

			}
		}
	}


	private void random() {
		// TODO Auto-generated method stub
		int i;
		camions_random();
		for(i=0; i<Constants.nc;i++){
			centres[i].ff_2r();
		}
	}

	private void camions_random() {
		// TODO Auto-generated method stub
		int i, r, j;
		int[] cd;
		cd = distriCap.clone();
		for(i=0; i<Constants.nc;i++){
			for(j=0;j<Constants.ht;j++){
				r = (int) (Constants.cap.length*Math.random());
				if(cd[r]==0)do r = r++ % Constants.cap.length; while(cd[r]>0);
				centres[i].set_camion(j, Constants.cap[r]);
				cd[r]--;
			}
		}
	}


	private void buit() {
		// TODO Auto-generated method stub
		this.camions_greedy();
	}

	private void camions_greedy(){
		int i, j, k, cont[], v, tot[], cam[];
		tot = new int[Constants.nc];
		cam = new int[Constants.nc];
		cont = new int[Constants.cap.length];
		k = Constants.cap.length-1;
		for(i = 0; i<=k; i++)cont[i] = distriCap[k];
		v = Constants.ht * Constants.cap[0];
		for(i = 0 ; i < Constants.nc ; i++){
			tot[i] = centres[i].getCapNa();
			cam[i] = v;
		}
		//Més aviat pondrem els camions amb més capacitat
		for(j = 1 ; j< Constants.ht+1; j++){
			for(i = 0 ; i < Constants.nc ; i++){
				if(cont[k]==0){
					/*do*/	k--; /*while(cont[k]==0);*/
				}
				if(cam[i]<tot[i]){
					centres[i].set_camion(j, Constants.cap[k]);
					cont[k]--;
					cam[i] += Constants.cap[k]-Constants.cap[0];
				}
				else{
					if(k > 0 && cont[0]>0){
						centres[i].set_camion(j, Constants.cap[0]);
						cont[0]--;
					}
					else if(k>1 && cont[1]>0){
						centres[i].set_camion(j, Constants.cap[1]);
						cont[1]--;
						cam[i] += Constants.cap[1]-Constants.cap[0];
					}
					else{
						centres[i].set_camion(j, Constants.cap[k]);
						cont[k]--;
						cam[i] += Constants.cap[k]-Constants.cap[0];
					}
				}
			}
		}
	}
	private void mes_aviat_possible() {
		// TODO Auto-generated method stub
		int i;
		this.camions_greedy();
		for(i = 0 ; i < Constants.nc ; i++){
			centres[i].ordena_noassign();
			centres[i].greedy();
		}
	}

	// HEURISTICAS
	// Ganancia
	public int getBenefici(){ return benef(false); }
	public int getBeneficiVerbose(){ return benef(true); }
	private int benef(boolean verb)
	{
		int b = 0 ;
		if(verb)
		{
			for(int i = 0 ; i < Constants.nc ; i++) b += centres[i].getBeneficiVerbose();
			System.out.println("Benefici total del estat : " + b);
		}
		else for(int i = 0 ; i < Constants.nc ; i++) b += centres[i].getBenefici();
		return b;
	}

	// Retraso
	public int getRetard(){ return retard(false); }	
	public int getRetardVerbose(){ return retard(true); }	
	private int retard(boolean verb)
	{
		int r = 0 ;
		if(verb)
		{
			for(int i = 0 ; i < Constants.nc ; i++) r += centres[i].getRetardVerbose();
			System.out.println("Retard total del estat : " + r);
		}
		else for(int i = 0 ; i < Constants.nc ; i++) r += centres[i].getRetard();
		return r;	
	}


	public LinkedList<Peticio> getPeticions(int i, int j) {
		return centres[i].get_transports()[j].get_peticiones();
	}


	@Override
	public String toString()
	{
		String s = "";
		s = s.concat("Retard: " + this.getRetard() + ", benefici: " + this.getBenefici() +"\n");
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

	public boolean desplazamientoPosible(Peticio p, int c, int h_dest) {
		return centres[c].get_transports()[h_dest].getCapR() >= p.getCan();
	}

	public boolean desplazar(Peticio p, int c, int h_ori, int h_dest) {
		return centres[c].desplazar_peticio(p, h_ori, h_dest);
	}

	public int getCap(int c, int h) {
		return centres[c].get_transports()[h].getCap();
	}

	public int getCapO(int c, int h) {
		return centres[c].get_transports()[h].getCapO();
	}

	// OPERADOR : INTERCAMBIO DE CAMIONES
	public void intercambioCamiones(int centre1, int hora1, int capacitat1, int centre2, int hora2, int capacitat2)
	{
		centres[centre1].set_camion(hora1, capacitat1);
		centres[centre2].set_camion(hora2, capacitat2);

	}

	public Peticio removePeticio(Peticio peticio, int centre, int hora)
	{
		return centres[centre].removePeticio(peticio, hora);
	}

	public boolean addPeticio(Peticio peti, Integer c, Integer h)
	{
		return centres[c.intValue()].addPeticio(peti, h);
	}

	// OPERADOR : INTERCAMBIO DE PETICIONES
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

	public boolean toFile(String fileOut) {
		PrintWriter out = null;
		try {
			int i;
			out = new PrintWriter(new FileWriter(fileOut));
			for(i = 0; i<Constants.cap.length; i++)out.write(distriCap[i] + " ");
			out.println();
			out.println(Principal.nbPeticions);
			for(i = 0; i<Constants.nc; i++){
				for(ListIterator<Peticio> it = centres[i].get_transports()[0].get_peticiones().listIterator();it.hasNext();){
					out.print(i);
					Peticio p = it.next();
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
}
