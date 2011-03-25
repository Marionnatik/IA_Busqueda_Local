package transports;

import java.io.*;
import java.util.*;

import aima.search.framework.*;
import aima.search.informed.*;


public class Principal {

	static int nbPeticions ;
	static int[] capTransports = new int[Constants.cap.length];
	static float[] probaPesos = new float[Constants.cant.length];
	static float[] probaHores = new float[Constants.ht];
	static char estrategiaInicial;
	static String algorisme;
	static String heuristic;

	public static void main(String[] args)
	{
		// Genera las constantes
		new Constants();

		// Prepara els fitxers d'input (test1 per defecte) i output
		String file_in = "tests/test1.txt";
		if(args.length != 0) file_in = args[0];
		String file_out1 = file_in.replace(".txt", "_output1.txt");
		String file_out2 = file_in.replace(".txt", "_output2.txt");

		// Llegeix el fitxer de valors
		try{
			readFile(file_in);
		} catch(FileNotFoundException fnf) {
			System.out.println("Error en l'arxiu de prova " + file_in + " : Arxiu no trobat.");
			System.exit(1);
		} catch(InputMismatchException im) {
			System.out.println("Error en l'arxiu de prova " + file_in + " : Format no vàlid.");
			System.exit(1);			
		} catch (UnsupportedEncodingException e) {
			System.out.println("Error : Encoding no valid.");
			System.exit(1);
		}

		// Genera el estat inicial amb l'estratègia desitjada
		Estat e = generadorProblema();
		e.estat_inicial(estrategiaInicial);
		
		String s1 = "Benefici : " + e.getBenefici();
		e.writeFile(file_out1+"", "ESTAT INICIAL", s1);

		// Resolució del problema
		try {
			Problem problem = null;

			if(algorisme.equals("hc"))
			{
				// Algorisme Hill Climbing
				if (heuristic.equals("gan"))
				{
					// Heuristic 1
					problem = new Problem(e,
							new Successor_HC(),
							new Goal_Test(),
							new Heuristica_Ganancia());
				} else if (heuristic.equals("ret")) {
					// Heuristic 2
					problem = new Problem(e,
							new Successor_HC(),
							new Goal_Test(),
							new Heuristica_Retard());
				} else {
					System.out.println("Error en l'arxiu de prova " + file_in + " : Nom d'heurística incorrecta.");
					System.exit(1);				
				}
				HillClimbingSearch search = new HillClimbingSearch();
				SearchAgent agent = new SearchAgent(problem, search);
				printActions(agent.getActions());
				System.out.println("Search Outcome=" + search.getOutcome());
				e = (Estat) search.getLastSearchState();			
				printInstrumentation(agent.getInstrumentation());

			} else if (algorisme.equals("sa"))
			{
				// Algorisme Simulated Annealing
				if (heuristic.equals("gan"))
				{
					// Heuristic 1
					problem = new Problem(e,
							new Successor_SA(),
							new Goal_Test(),
							new Heuristica_Ganancia());
				} else if (heuristic.equals("ret")) {
					// Heuristic 2
					problem = new Problem(e,
							new Successor_SA(),
							new Goal_Test(),
							new Heuristica_Retard());
				} else {
					System.out.println("Error en l'arxiu de prova " + file_in + " : Nom d'heurística incorrecta.");
					System.exit(1);				
				}				
				SimulatedAnnealingSearch search = new SimulatedAnnealingSearch();
				SearchAgent agent = new SearchAgent(problem, search);
				printActions(agent.getActions());
				System.out.println("Search Outcome=" + search.getOutcome());
				e = (Estat) search.getLastSearchState();			
				printInstrumentation(agent.getInstrumentation());
			} else {
				System.out.println("Error en l'arxiu de prova " + file_in + " : Nom d'algorisme incorrecta.");
				System.exit(1);				
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		String s2 = "Benefici : " + e.getBenefici();
		e.writeFile(file_out2, "ESTAT FINAL", s2);
	}

	private static void readFile(String file) throws FileNotFoundException, InputMismatchException, UnsupportedEncodingException
	{
		Scanner sc = new Scanner(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		sc.useLocale(Locale.US);

		// Llegeix el nombre de peticions
		nbPeticions = sc.nextInt() ;

		// Llegeix y comproba el nombre de transports per cada capacitat
		for(int i = 0 ; i < capTransports.length ; i++) capTransports[i] = sc.nextInt();
		if(capTransports[0] + capTransports[1] + capTransports[2] != 60)
		{
			System.out.println("Error en l'arxiu de prova " + file + " : La suma dels transports no és igual a 60.");
			System.exit(1);
		}

		// Llegeix y comproba la distribució de probabilitat dels pesos de las entregas
		for(int i = 0 ; i < probaPesos.length ; i++) probaPesos[i] = sc.nextFloat();
		if(probaPesos[0] + probaPesos[1] + probaPesos[2] + probaPesos[3] + probaPesos[4] != 1.0)
		{
			System.out.println("Error en l'arxiu de prova " + file + " : La suma de las probabilitats de pesos és igual a " + 
					(probaPesos[0] + probaPesos[1] + probaPesos[2] + probaPesos[3] + probaPesos[4]) +
			".");
			System.exit(1);
		}

		// Llegeix y comproba la distribució de probabilitat de las hores d'entrega
		for(int i = 0 ; i < probaHores.length ; i++) probaHores[i] = sc.nextFloat();
		if(probaHores[0] + probaHores[1] + probaHores[2] + probaHores[3] + probaHores[4] + 
				probaHores[5] + probaHores[6] + probaHores[7] + probaHores[8] + probaHores[9] != 1)
		{
			System.out.println("Error en l'arxiu de prova " + file + " : La suma de las probabilitats de las hores és igual a " + 
					(probaHores[0] + probaHores[1] + probaHores[2] + probaHores[3] + probaHores[4] + 
							probaHores[5] + probaHores[6] + probaHores[7] + probaHores[8] + probaHores[9]) +
			".");
			// System.exit(1);
			// WTF ?!?!?!?! 0,1 + 0,1 + ... + 0,1 = 1,0000001 !!!!!!!!
		}

		// Llegeix l'estratègia de generació del estat inicial, l'algorisme i l'heuristic desitjats
		String eI = sc.next();
		estrategiaInicial = eI.charAt(0);		
		algorisme = sc.next();
		heuristic = sc.next();

		// TODO : Commentar
//		System.out.println(nbPeticions + ", " +
//				capTransports[0] + ", " + capTransports[1] + ", " + capTransports[2] + ", " +
//				probaHores[0] + ", " + probaHores[1] + ", " + probaHores[2] + ", " + probaHores[3] + ", " +	probaHores[4] + ", " +
//				probaHores[5] + ", " + probaHores[6] + ", " + probaHores[7] + ", " + probaHores[8] + ", " + probaHores[9] + ", " +
//				probaPesos[0] + ", " + probaPesos[1] + ", " + probaPesos[2] + ", " + probaPesos[3] + ", " + probaPesos[4] + ", " +
//				estrategiaInicial + ", " + algorisme + ", " + heuristic + ".");
	}


	private static Estat generadorProblema()
	{
		Estat inicial =  new Estat(capTransports);

		int numCentre = 0 ;


		for(int i = 0 ; i < nbPeticions ; i++)
		{
			// Genera una hora d'entrega en conformitat amb la distribució de probabilitat
			float probaH = (float) Math.random();			
			int horaEntrega = 0;
			float totalProbaH = probaHores[horaEntrega];
			while(probaH > totalProbaH)
			{
				horaEntrega++;
				totalProbaH += probaHores[horaEntrega];
			}

			// Genera el pes de l'entrega en conformitat amb la distribució de probabilitat
			float probaP = (float) Math.random();			
			int peso = 0;
			float totalProbaP = probaPesos[peso];
			while(probaP > totalProbaP)
			{
				peso++;
				totalProbaP += probaPesos[peso];
			}

			// Si un centre està ple, es passa al següent
			if( i > Math.ceil((float)(numCentre+1)*(float)nbPeticions/(float)Constants.nc)) numCentre++;

			// Creació de la petició
			Peticio p = new Peticio(i, Constants.cant[peso], horaEntrega + Constants.h_min);

			// Asignació de la petició al centre, com "no entregada"
			inicial.initPeticio(numCentre, p);
		}

		return inicial ;
	}

	private static void printInstrumentation(Properties properties) {
		Iterator<?> keys = properties.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			String property = properties.getProperty(key);
			System.out.println(key + " : " + property);
		}

	}

	private static void printActions(List<?> actions) {
		for (int i = 0; i < actions.size(); i++) {
			String action = (String) actions.get(i);
			System.out.println(action);
		}
	}
}