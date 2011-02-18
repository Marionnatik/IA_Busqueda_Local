package transports;

public class Centre {
	
	private Transport[] hores ;
	
	public Centre()
	{
		for(int i = 0 ; i < 11 ; i++)
		{
			hores[i] = new Transport() ;
		}
	}

	public int getBenefici()
	{
		int b = 0 ;
		// CASO 0!!!
		for(int i = 1 ; i < 11 ; i++)
		{
			
			b += hores[i].getBenefici(i+7);
		}
		
		return b ;
	}

}
