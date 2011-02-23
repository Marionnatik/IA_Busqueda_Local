package transports;

public class Peticio implements Comparable{
	
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
	public int compareTo(Object o) {

		return 0;
	}
		
}
