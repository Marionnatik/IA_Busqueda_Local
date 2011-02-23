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
		for(int i = 0 ; i < 6 ; i++)
		{
			centres[i] = new Centre() ;
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
