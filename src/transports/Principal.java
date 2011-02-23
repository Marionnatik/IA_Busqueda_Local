package transports;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Principal {	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// crear el estat inicial
		new Constants();
		Estat inicial = generadorProblema();
		//inicial.estat_inicial("t");
	}
	
	private static Estat generadorProblema()
	{
		Scanner sc = new Scanner(System.in);
		
		int nbPeticions = 0 ;
		int[] capTransports = new int[3];
		float[] probaHoras = new float[10];
		float[] probaPesos = new float[3];
		
		nbPeticions = getIntegerInput(sc, "Introduïu el nombre de peticions : ");
		
		int i = getIntegerInput(sc, "Tria la distribució de les capacitats de transport que desitgi :\n1-Equiprobale\n2-Manual");
		switch(i)
		{
		case 1 :
			capTransports[0] = 20 ;
			capTransports[1] = 20 ;
			capTransports[1] = 20 ;
		case 2 :
			System.out.println("Introduïu successivament el nombre de transports que desitja per las capacitats 500, 1000 i 2000kgs.");
			getAndCheckIntRepartition(sc, capTransports, 60);
		}
		
		return new Estat(capTransports);
	}
	
	private static void getAndCheckIntRepartition(Scanner sc, int[] rep, int limit)
	{
		int total = 0 ;
		
		System.out.println("Recorda que el total no pot exedir " + limit + ".");
		
		for(int i = 0 ; i < rep.length ; i++)
		{
			rep[i] = getIntegerInput(sc, i + " : ");
			total += rep[i];
		}
		
		if(total != limit)
		{
			System.out.println("El total no es igual a " + limit + ". Si us plau, recomenceu.");
			getAndCheckIntRepartition(sc, rep, limit);
		}		
	}

	private static int getIntegerInput(Scanner sc, String s)
	{
		int i ;
		try
		{
			System.out.print(s);
			i = sc.nextInt();
		} catch (InputMismatchException e)
		{
			System.out.println("S'ha d'introduir un nombre enter.");
			i = getIntegerInput(sc, s);
		}			
		System.out.println(i);
		return i;
	}
	
}
