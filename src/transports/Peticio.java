package transports;

public class Peticio implements Comparable<Peticio>{
	
	private int id;
	private int cantidad;
	private int precio;
	private int hora;

	public Peticio(int i, int c, int h)
	{
		id = i;
		cantidad = c;
		hora = h;
		
		if(c <= 200)
		{
			precio = c;
		} else if (c < 500) {
			precio = c*3/2;
		} else {
			precio = c*2;
		}
	}
	
	public int getID() { return id ; }
	public int getCan() { return cantidad ; }
	public int getPre() { return precio ; }
	public int getH() { return hora ; }



	@Override
	public int compareTo(Peticio o) {
		// TODO Auto-generated method stub
		switch(Estat.tipord){
			case 'a':
				if(hora!=o.getH())return hora - o.getH();
				else if(precio!=o.getPre()) return o.getPre() - precio;
			break;
			case 'b':
				if(precio!=o.getPre()) return o.getPre() - precio;
				else if(hora!=o.getH())return hora - o.getH();
			break;
			case 'c':
				if(hora!=o.getH())return hora - o.getH();
				else if(precio!=o.getPre()) return precio - o.getPre();
			break;
			case 'd':
				if(precio!=o.getPre()) return precio - o.getPre();
				else if(hora!=o.getH())return hora - o.getH();
			break;
		}
		if(id!=o.getID()) return id - o.getID();
		else return 0;
	}
		
}
