package org.hisrc.distant.graph.model.edge;

public class TransferEdge extends TransitionEdge {

	private final int transferDuration;

	public TransferEdge(int transferDuration) {
		this.transferDuration = transferDuration;
	}

	@Override
	public Transition after(int timepoint) {
		return new Transition(timepoint, timepoint + transferDuration);
	}
	
	@Override
	public int length() {
		return -1;
	}
	
	@Override
	public String toString() {
		return "->-";
	}
}
