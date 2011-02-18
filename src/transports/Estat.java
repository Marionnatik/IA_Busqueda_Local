package transports;

public class Estat {
	
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
