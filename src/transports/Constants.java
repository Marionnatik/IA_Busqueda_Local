package transports;

public class Constants {
	public static int cap[];
	public static int h_min;
	public static int h_max;
	public static int nc;
	public static int ht;
	public Constants(){
		cap[0] = 500;
		cap[1] = 1000;
		cap[2] = 2000;
		h_min = 8;
		h_max = 17;
		nc = 6;
		ht = 1 + h_max - h_min;
	}
}
