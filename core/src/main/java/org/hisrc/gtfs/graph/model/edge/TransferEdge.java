package org.hisrc.gtfs.graph.model.edge;

public class TransferEdge extends TransitionEdge {

	// TODO: transfer cost is in seconds, all other times are in minutes
	public TransferEdge(int cost) {
		super(cost);
	}
}
