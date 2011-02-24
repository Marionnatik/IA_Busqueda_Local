package transports;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Principal {	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Constants();
		Estat inicial = generadorProblema();
		//inicial.estat_inicial("t");
	}
	
	private static Estat generadorProblema()
	{
		Scanner sc = new Scanner(System.in);
		
		int nbPeticions = 0 ;
		int[] capTransports = new int[Constants.n_cap];
		float[] probaHoras = new float[Constants.ht];
		float[] probaPesos = new float[Constants.n_cap];
		
		nbPeticions = getIntegerInput(sc, "Introduïu el nombre de peticions : ");
		
		int i = chooseOption(sc, 2,
				"Tria la repartició de les capacitats de transport que desitgi :\n  1-Equiprobale\n  2-Manual");
		switch(i)
		{
		case 1 :
			capTransports[0] = 20 ;
			capTransports[1] = 20 ;
			capTransports[2] = 20 ;
			break;
		case 2 :
			System.out.println("Introduïu successivament el nombre de transports que desitja per las capacitats 500, 1000 i 2000kgs.");
			intRepartition(sc, capTransports, 60);
			break;			
		default :
			System.out.println("Error : Wrong option number.");
			System.exit(1);	
		}
		
		i = chooseOption(sc, 2,
				"Tria la repartició de les peticions entre las hores que desitgi :\n  1-Equiprobale\n  2-Manual");
		switch(i)
		{
		case 1 :
			for(int j = 0 ; j < probaHoras.length ; j++)
			{
				probaHoras[j] = 1/probaHoras.length ;
			}
			break;
		case 2 :
			System.out.println("Introduïu successivament la probabilitat de repartició de las peticions que desitja per cada hora.");
			floatRepartition(sc, probaHoras, 1);
			break;			
		default :
			System.out.println("Error : Wrong option number.");
			System.exit(1);			
		}
		
		return new Estat(capTransports);
	}
	
	private static int chooseOption(Scanner sc, int numOp, String s)
	{
		int i = getIntegerInput(sc, s);
		
		if(i < 0 || i > numOp)
		{
			System.out.println("Entreu el nombre de la opció desitjada (entre 1 i " + numOp + ").");
			i = chooseOption(sc, numOp, s);
		}		
		return i;
	}
	
	private static void intRepartition(Scanner sc, int[] rep, int limit)
	{
		int total = 0 ;
		
		System.out.println("Recordeu que el total ha de ser exactament " + limit + ".");
		
		for(int i = 0 ; i < rep.length ; i++)
		{
			rep[i] = getIntegerInput(sc, i + " : ");
			total += rep[i];
		}
		
		if(total != limit)
		{
			System.out.println("El total no es igual a " + limit + ". Si us plau, recomenceu.");
			intRepartition(sc, rep, limit);
		}		
	}
	
	private static void floatRepartition(Scanner sc, float[] rep, float limit)
	{
		float total = 0 ;
		
		System.out.println("Recordeu que el total ha de ser exactament " + limit + ".");
		
		for(int i = 0 ; i < rep.length ; i++)
		{
			rep[i] = getFloatInput(sc, i + " : ");
			total += rep[i];
		}
		
		if(total != limit)
		{
			System.out.println("El total no es igual a " + limit + ". Si us plau, recomenceu.");
			floatRepartition(sc, rep, limit);
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
			sc.close();
			System.out.println("S'ha d'introduir un nombre enter.");
			Scanner scan = new Scanner(System.in);
			i = getIntegerInput(scan, s);
		}
		return i;
	}

	private static float getFloatInput(Scanner sc, String s)
	{
		float f ;
		try
		{
			System.out.print(s);
			f = sc.nextFloat();
		} catch (InputMismatchException e)
		{
			System.out.println("S'ha d'introduir un nombre float.");
			f = getFloatInput(sc, s);
		}
		return f;
	}
	
}
