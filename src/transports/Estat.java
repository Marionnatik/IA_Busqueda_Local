package transports;

public class Estat {
	
	// distribucion de capacidades de los transportes
	// distriCap[0] = numero de transportes de 500kgs
	// distriCap[1] = numero de transportes de 1000kgs
	// distriCap[2] = numero de transportes de 2000kgs
	public int[] distriCap = new int[Constants.cap.length];
	
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
				  this.mes_aviat_posible();
			break;
		} 
	}
		  
	private void mes_aviat_posible() {
		// TODO Auto-generated method stub
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
	
	
}
