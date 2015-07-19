package org.hisrc.distant.graph.model.edge;

import org.hisrc.distant.jgrapht.traverse.ArrivalTimeFunction;

public class TransitionArrivalTimeFunction implements
		ArrivalTimeFunction<Transition> {

	@Override
	public int apply(Transition transition) {
		return transition.getArrivalTime();
	}

	private static ArrivalTimeFunction<Transition> INSTANCE = new TransitionArrivalTimeFunction();

	public static ArrivalTimeFunction<Transition> create() {
		return INSTANCE;
	}
}
