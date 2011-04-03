package transports;

import java.io.*;
import java.util.*;

import aima.search.framework.*;
import aima.search.informed.*;


public class Principal
{	
	static int nbTests;

	static int nbPeticiones;
	static float[] probaPesos = new float[Constants.cant.length];
	static float[] probaHoras = new float[Constants.ht];
	static int[] capTransportes = new int[Constants.cap.length];
	
	static char estrategiaInicial;
	static String algoritmo;
	static String heuristica;

	public static void main(String[] args)
	{
		int i, bi, bo, ri, ro;
		float mb, mr;
		double elapsedTimeInmSec = -1;
		String intest;
		
		// Genera las constantes
		new Constants();

		// Prepara els fitxers d'input (test1 per defecte) i output
		String file_in = "tests/test1/test1.txt";
		if(args.length != 0) file_in = args[0];

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

		intest = file_in.substring(12, file_in.length()-4);
		
		try {
			String nt;
			String file_outs = file_in.substring(0, 17) + "_estadisticas.csv";
			BufferedWriter out = new BufferedWriter(new FileWriter(file_outs, true));
			out.write(intest);
			out.newLine();
			// Genera el estat inicial amb l'estratègia desitjada
			for(i=0;i<nbTests;i++){
				nt = Integer.toString(i+1);
				String file_out1 = file_in.replace(".txt", "." + nt + "_output1.txt");
				String file_out2 = file_in.replace(".txt", "." + nt + "_output2.txt");
				long start = System.nanoTime();
				Estado e = generadorProblema();
				e.estadoInicial(estrategiaInicial);
				//t				elapsedTimeInmSec = (System.nanoTime() - initemp) * 1.0e-6;
				//t				out.write(elapsedTimeInmSec + ";");
				//t				long start = System.nanoTime();
				bi = e.getBeneficio();
				ri = e.getRetraso();
				
				String s1 = "Benefici : " + bi;
				e.writeFile(file_out1+"", "ESTAT INICIAL", s1);
				// Resolució del problema
				try {
					Problem problem = null;

					if(algoritmo.equals("hc"))
					{
						// Algorisme Hill Climbing
						if (heuristica.equals("gan"))
						{
							// Heuristic 1
							problem = new Problem(e,
									new Successor_HC(),
									new Goal_Test(),
									new Heuristica_Ganancia());
						} else if (heuristica.equals("ret")) {
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
						e = (Estado) search.getLastSearchState();			
						elapsedTimeInmSec = (System.nanoTime() - start) * 1.0e-6;
						printActions(agent.getActions());
						printInstrumentation(agent.getInstrumentation(), out);

					} else if (algoritmo.equals("sa"))
					{
						// Algorisme Simulated Annealing
						if (heuristica.equals("gan"))
						{
							// Heuristic 1
							problem = new Problem(e,
									new Successor_SA(),
									new Goal_Test(),
									new Heuristica_Ganancia());
						} else if (heuristica.equals("ret")) {
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
						e = (Estado) search.getLastSearchState();			
						elapsedTimeInmSec = (System.nanoTime() - start) * 1.0e-6;
						//c						printActions(agent.getActions());
						printInstrumentation(agent.getInstrumentation(), out);

					} else {
						System.out.println("Error en l'arxiu de prova " + file_in + " : Nom d'algorisme incorrecta.");
						System.exit(1);				
					}

				} catch (Exception e1) {
					e1.printStackTrace();
				}
				bo = e.getBeneficio();
				ro = e.getRetraso();
				mb = (float)100*(bo-bi)/Math.abs(bi);
				String s2 = "Benefici : " + bo;
				e.writeFile(file_out2, "ESTAT FINAL", s2);
				mr = (float)100*(ri-ro)/ri;
				out.write(bo + ";" + mb + ";" + ro + ";" + mr + ";" + elapsedTimeInmSec);
				out.newLine();
			}
			out.newLine();
			out.close();
		} 
		catch (IOException e) {
			System.out.println("Error : escritura fichero estadisticas");
			System.exit(1);
		} 

	}

	private static void readFile(String file) throws FileNotFoundException, InputMismatchException, UnsupportedEncodingException
	{
		Scanner sc = new Scanner(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		sc.useLocale(Locale.US);
		nbTests = sc.nextInt();
		// Llegeix el nombre de peticions
		nbPeticiones = sc.nextInt() ;

		// Llegeix y comproba el nombre de transports per cada capacitat
		for(int i = 0 ; i < capTransportes.length ; i++) capTransportes[i] = sc.nextInt();
		if(capTransportes[0] + capTransportes[1] + capTransportes[2] != 60)
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
		for(int i = 0 ; i < probaHoras.length ; i++) probaHoras[i] = Math.round(sc.nextFloat()*100)*(float)0.01;
		if(probaHoras[0] + probaHoras[1] + probaHoras[2] + probaHoras[3] + probaHoras[4] + 
				probaHoras[5] + probaHoras[6] + probaHoras[7] + probaHoras[8] + probaHoras[9] != 1)
		{
			System.out.println("Error en l'arxiu de prova " + file + " : La suma de las probabilitats de las hores és igual a " + 
					(probaHoras[0] + probaHoras[1] + probaHoras[2] + probaHoras[3] + probaHoras[4] + 
							probaHoras[5] + probaHoras[6] + probaHoras[7] + probaHoras[8] + probaHoras[9]) +
			".");
			System.exit(1);
		}

		// Llegeix l'estratègia de generació del estat inicial, l'algorisme i l'heuristic desitjats
		String eI = sc.next();
		estrategiaInicial = eI.charAt(0);		
		algoritmo = sc.next();
		heuristica = sc.next();
	}


	private static Estado generadorProblema()
	{
		Estado inicial =  new Estado(capTransportes);

		int numCentre = 0 ;


		for(int i = 0 ; i < nbPeticiones ; i++)
		{
			// Genera una hora d'entrega en conformitat amb la distribució de probabilitat
			float probaH = (float) Math.random();			
			int horaEntrega = 0;
			float totalProbaH = probaHoras[horaEntrega];
			while(probaH > totalProbaH)
			{
				horaEntrega++;
				totalProbaH += probaHoras[horaEntrega];
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
			if( i > Math.ceil((float)(numCentre+1)*(float)nbPeticiones/(float)Constants.nc)) numCentre++;

			// Creació de la petició
			Peticio p = new Peticio(i, Constants.cant[peso], horaEntrega + Constants.h_min);

			// Asignació de la petició al centre, com "no entregada"
			inicial.addPeticion(p, numCentre, 0);
		}

		return inicial ;
	}

	private static void printInstrumentation(Properties properties, BufferedWriter out) throws IOException {
		Iterator<?> keys = properties.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			String property = properties.getProperty(key);
			//c			System.out.println(key + " : " + property);
			out.write(property + ";");
		}

	}

	private static void printActions(List<?> actions) {
		for (int i = 0; i < actions.size(); i++) {
			//c			String action = (String) actions.get(i);
			//c			System.out.println(action);
		}
	}
}