package transports;

import java.io.*;
import java.util.Scanner;

public class Principal {

	static int nbPeticions ;
	static int[] capTransports = new int[Constants.cap.length];
	static float[] probaPesos = new float[Constants.cant.length];
	static float[] probaHoras = new float[Constants.ht];
	static char estrategiaInicial;

	static Estat e ;


	public static void main(String[] args)
	{
		// Generando las constantes
		new Constants();

		// Fichero de valores (test1.txt por defecto)
		String file = "tests/test1.txt";
		if(args.length != 0) file = args[0];

		// Recupera los valores del fichero
		try{
			readFile(file);
		} catch(FileNotFoundException e) {
			System.out.println("Error : Fichero de test " + file + " no encontrado.");
			System.exit(1);
		}

		// Generando el problema
		e = generadorProblema();
		
		System.out.println(e);

		// Generando el estado inicial
		//e.estat_inicial(estrategiaInicial);
	}

	private static void readFile(String file) throws FileNotFoundException
	{
		Scanner sc = new Scanner(new FileReader(file));

		// Lee el numero de peticiones
		nbPeticions = sc.nextInt() ;

		// Lee y comprueba el numero de transportes para cada capacidad
		for(int i = 0 ; i < capTransports.length ; i++) capTransports[i] = sc.nextInt();
		if(capTransports[0] + capTransports[1] + capTransports[2] != 60)
		{
			System.out.println("Error : La suma de los transportes no es igual a 60.");
			System.exit(1);
		}

		// Lee y comprueba la distribucio de probabilidad de los pesos de las entregas
		for(int i = 0 ; i < probaPesos.length ; i++) probaPesos[i] = sc.nextFloat();
		if(probaPesos[0] + probaPesos[1] + probaPesos[2] + probaPesos[3] + probaPesos[4] != 1.0)
		{
			System.out.println("Error : La suma de las probabilidades de pesos es igual a " + 
					(probaPesos[0] + probaPesos[1] + probaPesos[2] + probaPesos[3] + probaPesos[4]) +
			".");
			System.exit(1);
		}

		// Lee y comprueba la distribucio de probabilidad de las horas de entrega
		for(int i = 0 ; i < probaHoras.length ; i++) probaHoras[i] = sc.nextFloat();
		if(probaHoras[0] + probaHoras[1] + probaHoras[2] + probaHoras[3] + probaHoras[4] + 
				probaHoras[5] + probaHoras[6] + probaHoras[7] + probaHoras[8] + probaHoras[9] != 1)
		{
			System.out.println("Error : La suma de las probabilidades de horas es igual a " + 
					(probaHoras[0] + probaHoras[1] + probaHoras[2] + probaHoras[3] + probaHoras[4] + 
							probaHoras[5] + probaHoras[6] + probaHoras[7] + probaHoras[8] + probaHoras[9]) +
			".");
			// System.exit(1);
			// WTF ?!?!?!?! 0,1 + 0,1 + ... + 0,1 = 1,0000001 !!!!!!!!
		}
		
		// Lee la estrategia de generacion del estado inicial
		String eI = sc.next();
		estrategiaInicial = eI.charAt(0);

		// TODO : Comment that
		System.out.println(nbPeticions + ", " +
				capTransports[0] + ", " + capTransports[1] + ", " + capTransports[2] + ", " +
				probaHoras[0] + ", " + probaHoras[1] + ", " + probaHoras[2] + ", " + probaHoras[3] + ", " +	probaHoras[4] + ", " +
				probaHoras[5] + ", " + probaHoras[6] + ", " + probaHoras[7] + ", " + probaHoras[8] + ", " + probaHoras[9] + ", " +
				probaPesos[0] + ", " + probaPesos[1] + ", " + probaPesos[2] + ", " + probaPesos[3] + ", " + probaPesos[4] + ", " +
				estrategiaInicial + ".");
	}


	private static Estat generadorProblema()
	{
		Estat inicial =  new Estat(capTransports);

		int numCentre = 0 ;

		for(int i = 0 ; i < nbPeticions ; i++)
		{
			// Generando una hora de entrega conforme a la distribucion de probabilidad
			float probaH = (float) Math.random();			
			int horaEntrega = 0;
			float totalProbaH = probaHoras[horaEntrega];
			while(probaH > totalProbaH)
			{
				horaEntrega++;
				totalProbaH += probaHoras[horaEntrega];
			}

			// Generando el peso de la entrega conforme a la distribucion de probabilidad
			float probaP = (float) Math.random();			
			int peso = 0;
			float totalProbaP = probaPesos[peso];
			while(probaP > totalProbaP)
			{
				peso++;
				totalProbaP += probaPesos[peso];
			}

			// Si un centro esta "lleno", pasamos al siguiente
			if( i > Math.ceil((float)(numCentre+1)*(float)nbPeticions/(float)Constants.nc)) numCentre++;

			// Creacion de la peticion
			Peticio p = new Peticio(i, Constants.cant[peso], horaEntrega + Constants.h_min);

			// Asignacion de la peticion al centro, como "no entregada"
			inicial.initPeticio(numCentre, p);
		}
		
		return inicial ;
	}
}