package transports;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

public class Transport {
	
	private int capacidad ;	
	private LinkedList<Peticio> peti ;

	public Transport()
	{
		capacidad = 0 ;
		peti = new LinkedList<Peticio>() ;
	}
	
	public int getCap(){ return capacidad ; }
	public void setCap(int c) { capacidad = c ; }

	public int getBenefici(int i) {
		
		int b = 0 ;
		Peticio p;
		for(Iterator<Peticio> it = peti.iterator(); it.hasNext();){
			p = it.next();
			if(p.getH()>=i)b += p.getPre();
			else b += p.getPre() * (i-p.getH())/5;
		}
		
		return b ;
	}
	public void ordenar(){
		Comparador<Peticio> horaPrecio = new Comparador<Peticio>();
		Collections.sort(peti, horaPrecio);
	}
}
