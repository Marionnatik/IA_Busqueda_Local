package transports;

import java.util.Iterator;

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
		for(int i = 0 ; i < Constants.nc ; i++)centres[i] = new Centre() ;

	}
	

	public void estat_inicial(char t){
		// Hi han diferentes tipologies
		switch(t) {
			case 'a': 
				  tipord = 'a';
				  this.mes_aviat_posible();
			break;
			case 'b': 
			  	tipord = 'b';
			  	this.perhora();
			break;
			case 'f':
				this.first_fit();
			break;
			case 'm':
				tipord = 'c';
				this.mes_aviat_posible();
			break;
			case 'r': 
				this.random();
			break;
			case 'v': 
				this.buit();
			break;
			case 'w':
				tipord = 'd';
				this.perhora();
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

	private void perhora() {
		// TODO Auto-generated method stub
		int i;
		boolean c, r[];
		Peticio p;
		Iterator<Peticio>[] it = new Iterator[Constants.nc];
		r = new boolean[Constants.nc];
		int[] cl = distriCap.clone();
		for(i = 0 ; i < Constants.nc ; i++){
			centres[i].ordena_noassign();
			it[i] = centres[i].h_setup();
			r[i] = true;
		}
		c = true;
		while(c){
			c = false;
			for(i = 0 ; i < Constants.nc ; i++){
				if(r[i]){
					p = it[i].next();
					cl = centres[i].h_step(p, cl, p.getH()+1-Constants.h_min);
					if(it[i].hasNext())r[i] = false;
				}
				c = c || r[i];
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
					r = (int) (Constants.n_cap*Math.random());
					if(cd[r]==0)do r = r++ % Constants.n_cap; while(cd[r]>0);
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
		int i, j, k, cont;
		cont = distriCap[0];
		  k = 0;
		  //Més aviat pondrem els camions amb més capacitat
		  for(j = 1 ; j< Constants.ht+1; j++){
			 for(i = 0 ; i < Constants.nc ; i++){
				if(cont==0){
				  do{
					k++;
				    cont = distriCap[k];
				  }while(cont!=0);
				}
				centres[i].set_camion(j, Constants.cap[k]);
				cont--;
			 }
		  }
    }
	private void mes_aviat_posible() {
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
	
	
}
