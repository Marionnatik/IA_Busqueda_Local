package transports;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class Centre {

	private Transport[] hores = new Transport[Constants.ht+1];
	private ArrayList<Peticio> entregades = new ArrayList<Peticio>();
	private int n;

	public Centre(int num)
	{
		hores[0] = new Transport(0);
		hores[0].setCap(Integer.MAX_VALUE);
		n = num;
		for(int i = 1 ; i < Constants.ht+1 ; i++)
		{
			hores[i] = new Transport(i+Constants.h_min-1) ;
		}
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

	public Transport[] get_transports()
	{
		return hores; 
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
		LinkedList<Peticio> petis;
		LinkedList<Peticio> puestas;
		Iterator<Peticio> it;
		petis = hores[0].get_peticiones();
		ini = 1;
		puestas = new LinkedList<Peticio>();
		for(it = petis.iterator(); it.hasNext();){
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
					puestas.add(p);
					ep = true;
					if(i==ini && hores[i].getCapR()==0)ini++;
				}
			}

		}
		for(it = puestas.iterator(); it.hasNext();){
			p = it.next();
			hores[0].remove_peticio(p);
		}
	}

	public Iterator<Peticio> h_setup(){
		return hores[0].get_peticiones().iterator();
	}

	public int[] h_step(Peticio p, int[] cl, int h){
		//Esperant el aviò estic surtint bastant boig per escriure aquest metod...
		int i, j, num;
		boolean r, r2, b;
		if(hores[h].getCap()==0){
			r = true;
			for(i = 0;i<Constants.cap.length && r;i++){
				if(Constants.cap[i]>=p.getCan() && cl[i]>0){
					r = false;
					cl[i]--;
					hores[h].setCap(Constants.cap[i]);
					if(!hores[h].add_peticio(p))System.out.println("Ostres! Tenim un problema!");
					else entregades.add(p); 
				}
			}
		}
		else if(!hores[h].add_peticio(p)){
			num = hores[h].getCap();
			b = false;
			for(i = cl.length-1; i>0; i--)if(num<Constants.cap[i] && cl[i]>0)b = true;	                                     
			if(b){
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
						else entregades.add(p);
					}
				}
			}
			else{
				
//				System.out.println("Sono qui, centre n° " + n + " - " + p.toString());
				if(h == 1){
					if(p.getH()!=17){
//						System.out.println(h + " = " + 1 + " ");
						this.h_step(p, cl, p.getH()-Constants.h_min+1+1);
					}
				}
				else if(h <= p.getH()-Constants.h_min+1){
					num = p.getH()-Constants.h_min+1;
//					System.out.println(h + " <= " + num + " ");
					this.h_step(p, cl, h-1);
				}
				else if(h != Constants.ht){
//					System.out.println(h + "!=" + Constants.ht + " ");
					this.h_step(p, cl, h+1);
				}
			}
		}
		else entregades.add(p);
		return cl;
	}
	
	public boolean desplazar_peticio(Peticio p, int h1, int h2){
		boolean be;
		be = hores[h2].add_peticio(p);
		if(be) hores[h1].remove_peticio(p);
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
				entregades.add(p);
			}
		}
		for(Iterator <Peticio>it2 = entregades.iterator(); it2.hasNext();){
			p = it2.next();
			noass.remove(p);
		}
		for(Iterator<Peticio> it = noass.iterator(); it.hasNext();){
			p = it.next();
			f = true;
			for(i = p.getH()-Constants.h_min; i>0 && f; i--){
				if(hores[i].add_peticio(p)){
					entregades.add(p);
					f = false;
				}
			}
			for(i = p.getH()+Constants.h_min+1+1; i<Constants.ht && f; i++){
				if(hores[i].add_peticio(p)){
					entregades.add(p);
					f = false;
				}
			}
		}
		neteja();
	}

	public int getRetard() {
		int r = 0;
		for(int i = 0 ; i < Constants.ht+1 ; i++)
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

	public void neteja() {
		Peticio p;
		for(Iterator<Peticio> it = entregades.iterator(); it.hasNext();){
			p = it.next();
			hores[0].remove_peticio(p);		
		}
		hores[0].capfix();
		entregades.clear();
	}
}