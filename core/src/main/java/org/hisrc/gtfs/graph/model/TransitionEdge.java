package org.hisrc.gtfs.graph.model;

public class TransitionEdge {

	private final TransitionType transitionType;
	private final int cost;

	public TransitionEdge(TransitionType transitionType, int cost) {
		this.transitionType = transitionType;
		this.cost = cost;
	}
	
	

}
