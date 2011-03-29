package transports;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class Transport
{
	private LinkedList<Peticio> peti ;
	private int capacidad ;
	private int capacidad_ocupada;
	private int capacidad_residual;
	private int hora;

	public Transport(int h)
	{
		capacidad = 0;
		peti = new LinkedList<Peticio>() ;
		capacidad_residual = 0;
		capacidad_ocupada = 0;
		hora = h;
	}

	public Transport(Transport t)
	{
		capacidad = t.capacidad;
		peti = new LinkedList<Peticio>() ;
		peti.addAll(t.peti);
		capacidad_residual = t.capacidad_residual;
		capacidad_ocupada = t.capacidad_ocupada;
		hora = t.hora;
	}

	public int getCap(){ return capacidad; }	
	public int getCapO(){ return capacidad_ocupada; }	
	public int getCapR(){ return capacidad_residual; }	
	public int getHora(){ return hora; }

	public void setHora(int h){ hora = h; }
	public boolean setCap(int c)
	{
		if( capacidad_ocupada <= c )
		{
			capacidad = c ;
			capacidad_residual = c - capacidad_ocupada ;
			return true ;
		}
		else return false ;
	}

	
	public int getBenefici()
	{
		return benef(false);
	}

	public int getBeneficiVerbose()
	{
		return benef(true);		
	}
	
	private int benef(boolean verb)
	{
		int b = 0;
		Peticio p;

		if( hora == 0 )
		{
			// No entregadas
			if(verb) System.out.println("No entregadas : ");
			
			for( Iterator<Peticio> it = peti.iterator(); it.hasNext(); )
			{
				p = it.next();
				if(verb) System.out.println("La peticion de " + p.getCan() + "kgs se debia entregar a las " + p.getH() + " y no fue entregada.");
				
				// Se resta el precio de la peticion mas unos 20% para cada hora de "retraso" hasta las 17
				int sub = (int)(p.getPre() * (1 + 0.2 * (Constants.h_max - p.getH())));
				if(verb) System.out.println("Precio substraido : " + sub);
				b -= sub;
			}
			if(verb) System.out.println("Coste total no entregadas : " + b);
		} else {
			// Entregadas
			if(verb) System.out.println(hora + "h : ");
			
			for( Iterator<Peticio> it = peti.iterator(); it.hasNext(); )
			{
				p = it.next();
				if(verb) System.out.println("La peticion de " + p.getCan() + "kgs se debia entregar a las " + p.getH() + " y fue entregada a las " + hora + ".");
				
				if( p.getH() >= hora )
				{
					// Entregada a tiempo : Se anade el precio de la peticion
					if(verb) System.out.println("Entregada a tiempo, benefici : " + p.getPre());
					b += p.getPre();
				}
				else
				{
					// No entregada a tiempo : Se anade el precio menos unos 20% para cada hora de retraso
					int add = (int)(p.getPre() * (1 - 0.2 * (hora - p.getH())));
					if(verb) System.out.println("Entregada con " + (hora - p.getH()) + "h de retraso, benefici : " + add);
					b += add;
				}
			}
			if(verb) System.out.println("Benefici total de la hora : " + b);
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

	public Peticio remove_peticio(Peticio p){
		int cnp;
		cnp = p.getCan();
		if(peti.remove(p)){
			capacidad_ocupada -= cnp;
			capacidad_residual += cnp;
		}	
		return p;
	}
	/*	
	public Peticio get_peticio(int i){
		return peti.get(i);
    }
	 */	
	public LinkedList<Peticio> get_peticiones(){
		return peti;
	}

	public int getRetards() {
		int r = 0;
		Peticio p;
		if(hora!=0){
			for(Iterator<Peticio> it = peti.iterator(); it.hasNext();){
				p = it.next();
				if(p.getH()<hora)r += hora - p.getH(); 
			}
		}
		else{
			for(Iterator<Peticio> it = peti.iterator(); it.hasNext();){
				p = it.next();
				r += 24 - p.getH() + Constants.h_min; 
			}
		}
		return r;
	}

	@Override
	public String toString()
	{
		String s = "";
		if(hora == 0)
		{
			s = s.concat("  No entregadas : Capacitat ocupada= " + capacidad_ocupada + ".\n");
		} else {
			s = s.concat("  " + hora + "h : " +
					"Capacidad = " + capacidad +
					", capacidad ocupada = " + capacidad_ocupada +
					", capacidad residual="	+ capacidad_residual + "\n");			
		}
		for(int i = 0 ; i < peti.size() ; i++)
		{
			s = s.concat("    " + peti.get(i).toString() + "\n");
		}
		return s;
	}

	public void capfix() {
		Iterator<Peticio> it;
		Peticio p;
		int c = 0;
		for(it = peti.iterator(); it.hasNext();){
			p = it.next();
			c += p.getCan();
		}
		capacidad_ocupada = c;
		capacidad_residual = capacidad - c;
	}
}
