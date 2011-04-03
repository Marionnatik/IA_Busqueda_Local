package transports;

public class Peticio implements Comparable<Peticio>{
	
	private int id;
	private int cantitat;
	private int preu;
	private int hora;

	public Peticio(int i, int c, int h)
	{
		id = i;
		cantitat = c;
		hora = h;
		
		if(c <= 200)
		{
			preu = c;
		} else if (c < 500) {
			preu = c*3/2;
		} else {
			preu = c*2;
		}
	}
	
	public int getID() { return id ; }
	public int getCan() { return cantitat ; }
	public int getPre() { return preu ; }
	public int getH() { return hora ; }



	@Override
	public int compareTo(Peticio o)
	{
		switch(Estat.tipord){
			case 'a':
				if(hora!=o.getH())return hora - o.getH();
				else if(preu!=o.getPre()) return o.getPre() - preu;
			break;
			case 'b':
				if(preu!=o.getPre()) return o.getPre() - preu;
				else if(hora!=o.getH())return hora - o.getH();
			break;
			case 'c':
				if(hora!=o.getH())return hora - o.getH();
				else if(preu!=o.getPre()) return preu - o.getPre();
			break;
			case 'd':
				if(preu!=o.getPre()) return preu - o.getPre();
				else if(hora!=o.getH())return hora - o.getH();
			break;
		}
		if(id!=o.getID()) return id - o.getID();
		else return 0;
	}

	@Override
	public String toString()
	{
		return "Peticio no." + id + " : Entrega a " + hora + "h, cantitat = " + cantitat + ", preu = " + preu + ".";
	}

	public String toFile() {
		String output;
		output = " " + Integer.toString(id) + " " + Integer.toString(cantitat) + " " + Integer.toString(hora);
		return output;
	}
		
}
