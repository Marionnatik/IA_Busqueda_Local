package transports;

import java.util.Iterator;
import java.util.LinkedList;

public class Centre {

	private Transport[] hores = new Transport[Constants.ht+1];

	public Centre()
	{
		hores[0] = new Transport(0);
		hores[0].setCap(Integer.MAX_VALUE);

		for(int i = 1 ; i < Constants.ht+1 ; i++)
		{
			hores[i] = new Transport(i+Constants.h_min-1) ;
		}
	}

	public Transport[] get_transports()
	{
		return hores; 
	}

	public int getBenefici()
	{
		int b;
		b = hores[0].getBenefici();
		for(int i = 1 ; i < Constants.ht+1 ; i++)
		{
			b += hores[i].getBenefici();
		}

		return b ;
	}

	public void initPeticio(Peticio p)
	{
		hores[0].add_peticio(p);
	}

	public boolean set_camion(int h, int c){
		return hores[h].setCap(c);
	}

	public void ordena_noassign(){
		hores[0].ordenar();
	}

	public void greedy() {
		Peticio p;
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
				//Si no funciona, utiliza lo de alto
				if(hores[i].add_peticio(p)){
					hores[0].remove_peticio(p);
					ep = true;
					if(i==ini && hores[i].getCapR()==0)ini++;
				}
			}

		}

	}

	public Iterator<Peticio> h_setup(){
		return hores[0].get_peticiones().iterator();
	}

	public int[] h_step(Peticio p, int[] cl, int h){
		//Esperant el aviò estic surtint bastant boig per escriure aquest metod...
		int i, j;
		boolean r, r2;
		if(hores[h].getCap()==0){
			r = true;
			for(i = 0;r;i++){
				if(Constants.cap[i]>p.getCan() && cl[i]>0){
					r = false;
					cl[i]--;
					hores[h].setCap(Constants.cap[i]);
					if(!hores[h].add_peticio(p))System.out.println("Ostres! Tenim un problema!");
				}
			}
		}
		else if(!hores[h].add_peticio(p)){
			if(hores[h].getCap()!=Constants.cap[cl.length-1]){
				r = true;
				r2 = true;
				for(i = 1;r && i < cl.length;i++){	
					if(Constants.cap[i]>p.getCan()+hores[h].getCapO() && cl[i]>0){
						r = false;
						cl[i]--;
						for(j = 0;r2;j++){
							if(hores[h].getCap() == Constants.cap[j]){
								r2 = false;
								cl[j]++;
							}
						}	
						hores[h].setCap(Constants.cap[i]);
						if(!hores[h].add_peticio(p))System.out.println("Ostres! Tenim un problema!");
					}
				}
			}
			else{
				if(h == 1)this.h_step(p, cl, p.getH()-Constants.h_min+1+1);
				else if(h <= p.getH()-Constants.h_min+1)this.h_step(p, cl, h-1);
				else if(h != Constants.ht)this.h_step(p, cl, h+1);
			}
		}
		return cl;
	}

	public boolean desplazar_peticio(int c, Peticio p, int h1, int h2){
		//		int ba = hores[h1].getBenefici(h1) + hores[h2].getBenefici(h2);
		boolean be;
		be = hores[h2].add_peticio(p);
		if(be)hores[h1].remove_peticio(p);
		//		return hores[h1].getBenefici(h1) + hores[h2].getBenefici(h2) - ba;
		return be;
	}

	public void ff_2r() {
		// TODO Auto-generated method stub
		Peticio p;
		boolean f;
		int i;
		LinkedList<Peticio> noass = hores[0].get_peticiones();
		for(Iterator<Peticio> it = noass.iterator(); it.hasNext();){
			p = it.next();

			if(hores[p.getH()-Constants.h_min+1].add_peticio(p)){
				noass.remove(p);
				hores[0].remove_peticio(p);
			}
		}
		for(Iterator<Peticio> it = noass.iterator(); it.hasNext();){
			p = it.next();
			f = true;
			for(i = p.getH()-Constants.h_min; i>0 && f; i--){
				if(hores[i].add_peticio(p)){
					hores[0].remove_peticio(p);
					f = false;
				}
			}
			for(i = p.getH()+Constants.h_min+1+1; i<Constants.ht && f; i++){
				if(hores[i].add_peticio(p)){
					hores[0].remove_peticio(p);
					f = false;
				}
			}
		}
	}

	public int getRetard() {
		int r = 0;
		for(int i = 1 ; i < Constants.ht+1 ; i++)
		{
			r += hores[i].getRetards();
		}
		return r;
	}

	@Override
	public String toString()
	{
		String s = "";

		for(int i = 0 ; i < Constants.ht+1 ; i++)
		{
			s = s.concat(hores[i].toString());
		}

		return s;
	}
}