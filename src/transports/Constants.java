package transports;

public class Constants {
	public static int[] cap = {500, 1000, 2000};			// capacidades de los camiones
	public static int[] cant = {100, 200, 300, 400, 500};	// cantidades de las peticiones
	public static int h_min = 8;							// hora minima de entrega
	public static int h_max = 17;							// hora maxima de entrega
	public static int ht = 1 + h_max - h_min;				// total de horas de entrega
	public static int nc = 6;								// numero de centros
}
