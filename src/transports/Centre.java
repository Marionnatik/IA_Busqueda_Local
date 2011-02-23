package transports;

public class Centre {
	
	private Transport[] hores = new Transport[Constants.ht+1];

	
	public Centre()
	{
		for(int i = 0 ; i < Constants.ht+1 ; i++)
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
	
	public boolean set_camion(int h, int c){
		return hores[h].setCap(c);
	}
	
	public void ordena_noassign(){
		hores[0].ordenar();
	}

	public void greedy() {
		
		
	}
	
	public boolean desplazar_peticio(Peticio p, int h1, int h2){
//		int ba = hores[h1].getBenefici(h1) + hores[h2].getBenefici(h2);
		boolean be;
		be = hores[h2].add_peticio(p);
		if(be)hores[h1].remove_peticio(p);
//		return hores[h1].getBenefici(h1) + hores[h2].getBenefici(h2) - ba;
		return be;
	}



	
	
	
	
	
	
	
}
