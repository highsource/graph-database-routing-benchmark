package org.hisrc.distant.graph.model.edge;

import org.hisrc.distant.jgrapht.traverse.EdgeTransitionFunction;

public class TransitionEdgeTransitionFunction implements EdgeTransitionFunction<TransitionEdge, Transition> {

	@Override
	public Transition apply(TransitionEdge edge, int timepoint) {
		return edge.after(timepoint);
	}

	private static EdgeTransitionFunction<TransitionEdge, Transition> INSTANCE = new TransitionEdgeTransitionFunction();

	public static EdgeTransitionFunction<TransitionEdge, Transition> create() {
		return INSTANCE;
	}

}
