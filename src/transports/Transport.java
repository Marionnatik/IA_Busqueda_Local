package transports;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class Transport {
	
	private int capacidad ;	
	private LinkedList<Peticio> peti ;
	private int capacidad_ocupada;
	private int capacidad_residual;
	
	public Transport()
	{
		capacidad = 0 ;
		peti = new LinkedList<Peticio>() ;
		capacidad_residual = 0;
		capacidad_ocupada = 0;
	}
	
	public int getCap(){
		return capacidad ; 
	}
	public boolean setCap(int c){
		if(capacidad_ocupada <= c){
		  capacidad = c;
		  capacidad_residual = c - capacidad_ocupada;
		  return true;
		}
		else return false;
	}

	public int getBenefici(int i) {
		
		int b = 0 ;
		Peticio p;
		if(i==0){
			for(Iterator<Peticio> it = peti.iterator(); it.hasNext();){
				p = it.next();
				b -= p.getPre() + (Constants.h_max-p.getH())/5;
			}
			
		}
		else{
			for(Iterator<Peticio> it = peti.iterator(); it.hasNext();){
				p = it.next();
				if(p.getH()>=i)b += p.getPre();
				else b += p.getPre() * (i-p.getH())/5;
			}
		}
		return b ;
	}

	public void ordenar(){
		Collections.sort(peti);
	}
	public boolean add_peticio(Peticio p){
		int cnp;
		cnp = p.getCan();
		if(cnp > capacidad - capacidad_ocupada)return false;
		peti.add(p);
		capacidad_ocupada += cnp;
		capacidad_residual -= cnp;
		return true;
	}
	public void remove_peticio(Peticio p){
		int cnp;
		cnp = p.getCan();
		if(peti.remove(p)){
		  capacidad_ocupada -= cnp;
		  capacidad_residual += cnp;
		}	
	}
}
