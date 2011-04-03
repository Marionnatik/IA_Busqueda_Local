package transports;

import aima.search.framework.HeuristicFunction;

public class Heuristica_Ganancia implements HeuristicFunction {

	@Override
	public double getHeuristicValue(Object state) {
		Estado estado = (Estado) state;
		return -estado.getBeneficio();
	}
}
