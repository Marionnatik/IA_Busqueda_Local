package transports;

import java.io.*;
import java.util.*;

import aima.search.framework.*;
import aima.search.informed.*;


public class Principal {

	static int nbPeticions, n, k, paso, pasos;
	static int[] capTransports = new int[Constants.cap.length];
	static float[] probaPesos = new float[Constants.cant.length];
	static float[] probaHores = new float[Constants.ht];
	static char estrategiaInicial;
	static String algorisme;
	static String heuristic;
	static double cordero;

	public static void main(String[] args)
	{
		int i, bi, bo, ri, ro;
		float mb, mr;
		double elapsedTimeInmSec = -1;
		String intest, nt;
		Estat e;
		// Genera las constantes
		new Constants();

		// Prepara els fitxers d'input (test1 per defecte) i output
		String file_in = "problemas/test1/prob1.txt";

		if(args.length != 0) file_in = args[0];

		// Llegeix el fitxer de valors
		try{
			if(file_in.contains("problemas")){
				readFileGenerador(file_in);
				intest = file_in.substring(12, file_in.length()-4);
					for(i=0;i<n;i++){
						nt = Integer.toString(i+1);
						String file_out = file_in.replace(".txt", "_" + nt + "_problema.txt");
						e = generadorProblema();
						e.toFile(file_out);
					}
			}
			else{
				readFileTest(file_in);
				intest = file_in.substring(16, file_in.length()-4);
				try {
					String file_outs = file_in.substring(0, 17) + "_estadisticas.csv";
					BufferedWriter out = new BufferedWriter(new FileWriter(file_outs, true));
					out.write(intest);
					out.newLine();
					// Genera el estat inicial amb l'estratègia desitjada
					for(i=0;i<n;i++){
						nt = Integer.toString(i+1);
/*elim*/				String file_out1 = file_in.replace(".txt", "." + nt + "_output1.txt");
/*elim*/				String file_out2 = file_in.replace(".txt", "." + nt + "_output2.txt");
						String ficherstat = "problemas/test" + intest.charAt(4) + "/" + intest + "_" + nt + "_problema.txt";
						e = readFileEstat(ficherstat);
						long start = System.nanoTime();				
						e.estadoInicial(estrategiaInicial);
						elapsedTimeInmSec = (System.nanoTime() - start) * 1.0e-6;
						out.write(elapsedTimeInmSec + ";");
						bi = e.getBenefici();
						ri = e.getRetard();
						String s1 = "Benefici : " + bi;
						e.writeFile(file_out1+"", "ESTAT INICIAL", s1);
						// Resolució del problema
						start = System.nanoTime();				
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
								e = (Estat) search.getLastSearchState();			
								elapsedTimeInmSec = (System.nanoTime() - start) * 1.0e-6;
								printActions(agent.getActions());
								//c						System.out.println("Search Outcome=" + search.getOutcome());
								printInstrumentation(agent.getInstrumentation(), out);

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

								SimulatedAnnealingSearch search = new SimulatedAnnealingSearch(pasos, paso, k, cordero);
								SearchAgent agent = new SearchAgent(problem, search);
								e = (Estat) search.getLastSearchState();			
								elapsedTimeInmSec = (System.nanoTime() - start) * 1.0e-6;
								printActions(agent.getActions());
								//c						System.out.println("Search Outcome=" + search.getOutcome());
								printInstrumentation(agent.getInstrumentation(), out);

							} else {
								System.out.println("Error en l'arxiu de prova " + file_in + " : Nom d'algorisme incorrecta.");
								System.exit(1);				
							}

						} catch (Exception e1) {
							e1.printStackTrace();
						}
						bo = e.getBenefici();
						ro = e.getRetard();
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
				catch (IOException ex) {
					System.out.println("Error : escritura fichero estadisticas");
					System.exit(1);
				}
			}
		} catch(FileNotFoundException fnf) {
			System.out.println("Error en l'arxiu " + file_in + " : Arxiu no trobat.");
			System.exit(1);
		} catch(InputMismatchException im) {
			System.out.println("Error en l'arxiu " + file_in + " : Format no vàlid.");
			System.exit(1);			
		} catch (UnsupportedEncodingException ex) {
			System.out.println("Error : Encoding no valid.");
			System.exit(1);
		}

	}

	private static void readFileGenerador(String file) throws FileNotFoundException, InputMismatchException, UnsupportedEncodingException
	{
		Scanner sc = new Scanner(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		sc.useLocale(Locale.US);		
		n = sc.nextInt();

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
		for(int i = 0 ; i < probaHores.length ; i++) probaHores[i] = (Math.round(sc.nextFloat()*100)*(float) 0.01);
		if(probaHores[0] + probaHores[1] + probaHores[2] + probaHores[3] + probaHores[4] + 
				probaHores[5] + probaHores[6] + probaHores[7] + probaHores[8] + probaHores[9] != 1.0)
		{
			System.out.println("Error en l'arxiu de prova " + file + " : La suma de las probabilitats de las hores és igual a " + 
					(probaHores[0] + probaHores[1] + probaHores[2] + probaHores[3] + probaHores[4] + 
							probaHores[5] + probaHores[6] + probaHores[7] + probaHores[8] + probaHores[9]) +
			".");
			System.exit(1);
		}

	}
	private static void readFileTest(String file) throws FileNotFoundException, InputMismatchException, UnsupportedEncodingException
	{
		Scanner sc = new Scanner(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		sc.useLocale(Locale.US);
		n = sc.nextInt();
		// Llegeix l'estratègia de generació del estat inicial, l'algorisme i l'heuristic desitjats
		String eI = sc.next();
		estrategiaInicial = eI.charAt(0);		
		algorisme = sc.next();
		heuristic = sc.next();
		if(algorisme.equals("sa")){
			pasos = sc.nextInt();
			paso = sc.nextInt();
			k = sc.nextInt();
			cordero = sc.nextFloat();
		}

	}
	private static Estat readFileEstat(String file) throws FileNotFoundException, InputMismatchException, UnsupportedEncodingException
	{
		Scanner sc = new Scanner(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		sc.useLocale(Locale.US);

		// Llegeix y comproba el nombre de transports per cada capacitat
		for(int i = 0 ; i < capTransports.length ; i++) capTransports[i] = sc.nextInt();
		if(capTransports[0] + capTransports[1] + capTransports[2] != 60)
		{
			System.out.println("Error en l'arxiu de prova " + file + " : La suma dels transports no és igual a 60.");
			System.exit(1);
		}

		// Llegeix el nombre de peticions
		nbPeticions = sc.nextInt() ;
		
		Estat est = new Estat(capTransports);
		
		// Llegeix les peticions
		for(int cont = 0; cont < nbPeticions; cont++){
			int c = sc.nextInt();
			int id = sc.nextInt();
			int cant = sc.nextInt();
			int hor = sc.nextInt();
			Peticio p = new Peticio(id, cant, hor);
			est.addPeticio(p, c, 0);
		}
		return est;
		
	}

	/*	private static Estat leerProblema(){
		Estat estado;
		estado = new Estat(null);
		return estado; 
	}

	private static Boolean escribirProblema(){

		return true;
	}
	 */
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
			inicial.addPeticio(p, numCentre, 0);
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
			String action = (String) actions.get(i);
			//c			System.out.println(action);
		}
	}
}