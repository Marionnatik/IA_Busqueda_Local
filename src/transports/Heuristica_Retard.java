package transports;

import aima.search.framework.HeuristicFunction;

public class Heuristica_Retard implements HeuristicFunction {

	@Override
	public double getHeuristicValue(Object state) {
		Estat estado = (Estat) state;
		return estado.getRetards();
	}
}
