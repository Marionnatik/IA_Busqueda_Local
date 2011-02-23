package transports;

public class Estat {
	
	// distribucion de capacidades de los transportes
	// distriCap[0] = numero de transportes de 500kgs
	// distriCap[1] = numero de transportes de 1000kgs
	// distriCap[2] = numero de transportes de 2000kgs
	public int[] distriCap ;
	
	private Centre[] centres ;
	
	public Estat()
	{
		for(int i = 0 ; i < Constants.nc ; i++)
		{
			centres[i] = new Centre() ;
		}		
	}
	
	public Estat(int[] dc, char t){
		// V és per generar una tipologia de estat inicial
		int i, j, k, cont;
		distriCap=dc;
		if(t=='v'){
		  for(i = 0 ; i < Constants.nc ; i++){
			 centres[i] = new Centre() ;
		  }
		//Més aviat pondrem els camions amb més capacitat
		  cont = dc[0];
		  k = 0;
		  for(j = 1 ; j< Constants.ht+1; j++){
			 for(i = 0 ; i < Constants.nc ; i++){
				if(cont==0){
				  do{
					k++;
				    cont = dc[k];
				  }while(cont!=0);
				}
				centres[i].set_camion(j, Constants.cap[k]);
				cont--;
			 }
		  }
		  
	    }
		  
	}
		
	public int getBenefici()
	{
		int b = 0 ;
		
		for(int i = 0 ; i < Constants.nc ; i++)
		{
			b += centres[i].getBenefici();
		}
		
		return b;
	}
}
