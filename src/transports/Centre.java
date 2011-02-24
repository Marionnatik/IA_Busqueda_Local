package transports;

import java.util.Iterator;

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
		int b;
		b = hores[0].getBenefici(0);
		for(int i = 1 ; i < Constants.ht+1 ; i++)
		{
			b += hores[i].getBenefici(i+Constants.h_min-1);
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
		Peticio p;
		Transport h;
		int i, ini;
		boolean ep;
		ini = 1;
		for(Iterator<Peticio> it = hores[0].get_peticiones().iterator(); it.hasNext();){
			p = it.next();
			ep = false;
			for(i=ini; i<hores.length && ep == false; i++){
				/*if(p.getCan() < hores[i].getCapR()){
					hores[0].remove_peticio(p);
					hores[i].add_peticio(p);
					ep = true;
				}*/
				//Si non funciona, utiliza lo de alto
				if(hores[i].add_peticio(p)){
				 hores[0].remove_peticio(p);
				 ep = true;
				 if(i==ini && hores[i].getCapR()==0)ini++;
				}
			}

		}
		
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