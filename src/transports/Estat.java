package transports;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;

public class Estat {

	// distribucion de capacidades de los transportes
	// distriCap[0] = numero de transportes de 500kgs
	// distriCap[1] = numero de transportes de 1000kgs
	// distriCap[2] = numero de transportes de 2000kgs
	public int[] distriCap = new int[Constants.cap.length];
	public static char tipord;
	private Centre[] centres = new Centre[Constants.nc];

	public Estat(int[] capacitats)
	{
		distriCap = capacitats ;
		for(int i = 0 ; i < Constants.nc ; i++) centres[i] = new Centre(i+1) ;

	}
	
	public Estat(Estat e)
	{
		distriCap = e.distriCap ;
		for(int i = 0 ; i < Constants.nc ; i++) centres[i] = new Centre(e.centres[i]);
	}

	public void initPeticio(int numCentre, Peticio p)
	{
		centres[numCentre].initPeticio(p);
	}


	public void estat_inicial(char t){
		// Hi han diferentes tipologies
		switch(t) {
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
		case 'm':
			//no té gaire sentit
			tipord = 'c';
			this.mes_aviat_possible();
			break;
		case 'r': 
			this.random();
			break;
		case 'v': 
			this.buit();
			break;
		case 'w':
			//no tiene mucho sentido
			tipord = 'd';
			this.per_hora();
			break;
		}
	}

	private void first_fit() {
		// TODO Auto-generated method stub
		int i;
		camions_greedy();
		for(i = 0 ; i < Constants.nc ; i++){
			centres[i].ff_2r();
		}
	}

	private void per_hora() {
		// TODO Auto-generated method stub
		int i, i2, j, a, k;
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
		int i, j, k, cont, v, tot[], cam[];
		tot = new int[Constants.nc];
		cam = new int[Constants.nc];
		k = Constants.cap.length-1;
		cont = distriCap[k];
		v = Constants.ht * Constants.cap[0];
		for(i = 0 ; i < Constants.nc ; i++){
			tot[i] = centres[i].getCapNa();
			cam[i] = v;
		}
		  //Més aviat pondrem els camions amb més capacitat
		  for(j = 1 ; j< Constants.ht+1; j++){
			 for(i = 0 ; i < Constants.nc ; i++){
				if(cont==0){
				  do{
					k--;
				    cont = distriCap[k];
				  }while(cont==0);
				}
				if(cam[i]<tot[i]){
					centres[i].set_camion(j, Constants.cap[k]);
					cont--;
					cam[i] += Constants.cap[k]-Constants.cap[0];
				}
				else{
					if(k > 0 && distriCap[0]>0){
						centres[i].set_camion(j, Constants.cap[0]);
						distriCap[0]--;
					}
					else if(k>1 && distriCap[1]>0){
						centres[i].set_camion(j, Constants.cap[1]);
						distriCap[1]--;
						cam[i] += Constants.cap[1]-Constants.cap[0];
					}
						else{
							centres[i].set_camion(j, Constants.cap[k]);
							cont--;
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

	public int getBenefici()
	{
		int b = 0 ;

		for(int i = 0 ; i < Constants.nc ; i++)b += centres[i].getBenefici();

		return b;
	}


	public double getRetards() {
		// TODO Auto-generated method stub
		int b = 0 ;
		double r;
		for(int i = 0 ; i < Constants.nc ; i++)b += centres[i].getRetard();
		r = (double)b;
		return r;	
	}


	public LinkedList<Peticio> getPeticions(int i, int j) {
		return centres[i].get_transports()[j].get_peticiones();
	}


	@Override
	public String toString()
	{
		String s = "";
		s = s.concat("Retard: " + this.getRetards() + ", benefici: " + this.getBenefici() +"\n");
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
		System.out.println(s1 + " escribit en " + file);
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

	public void canvi_camion(int i, int h1, int c1, int i2, int h2, int c2) {
		centres[i].set_camion(h1, c1);
		centres[i2].set_camion(h2, c2);

	}

	public boolean canvi_peticiones(int i, int h1, int h2, Peticio p, Peticio p2){
		centres[i].get_transports()[h1].remove_peticio(p);
		centres[i].get_transports()[h2].remove_peticio(p2);
		if(centres[i].get_transports()[h1].add_peticio(p2)){
			if(centres[i].get_transports()[h2].add_peticio(p))return true;
			else{
				centres[i].get_transports()[h1].remove_peticio(p2);
				return false;
			}
		}
		return false;
	}
}
