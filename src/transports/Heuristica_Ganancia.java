package transports;

import aima.search.framework.HeuristicFunction;

public class Heuristica_Ganancia implements HeuristicFunction {

	@Override
	public double getHeuristicValue(Object state) {
		Estat estado;
		estado = (Estat) state;
		return estado.getBenefici();
	}

}
