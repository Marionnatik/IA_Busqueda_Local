package transports;

public class Centre {
	
	private Transport[] hores ;

	
	public Centre()
	{
		for(int i = 1 ; i < Constants.ht+1 ; i++)
		{
			hores[i] = new Transport() ;
		}
		hores[0].setCap(Integer.MAX_VALUE);
	}

	public int getBenefici()
	{
		int b = 0 ;
		// CASO 0!!!
		for(int i = 1 ; i < Constants.ht+1 ; i++)
		{
			
			b += hores[i].getBenefici(i+7);
		}
		
		return b ;
	}
	
	public void set_camion(int h, int c){
		
		hores[h].setCap(c);
	}

}
