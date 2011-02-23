package transports;

public class Estat {
	
	// distribucion de capacidades de los transportes
	// distriCap[0] = numero de transportes de 500kgs
	// distriCap[1] = numero de transportes de 1000kgs
	// distriCap[2] = numero de transportes de 2000kgs
	public int[] distriCap ;
	
	private Centre[] centres ;
	
	public Estat(int[] capacitats)
	{
		distriCap = capacitats ;
		
		for(int i = 0 ; i < 6 ; i++)
		{
			centres[i] = new Centre() ;
		}		
	}
	
	public Estat(int[] dc, char t){
		// V és per generar una tipologia de estat inicial
		int i, j, cont;
		if(t=='v'){
			for(i = 0 ; i < 6 ; i++)
			{
				centres[i] = new Centre() ;
			}
		//Més aviat pondrem els camions amb més capacitat
		for(j = 0; j < distriCap.length; j++)
			for(cont = dc[0]; cont > 0; cont--)
				for(i = 1 ; i< 11; i++){
				//for
			}
		}
		
	}
	public int getBenefici()
	{
		int b = 0 ;
		
		for(int i = 0 ; i < 6 ; i++)
		{
			b += centres[i].getBenefici();
		}
		
		return b;
	}
}
