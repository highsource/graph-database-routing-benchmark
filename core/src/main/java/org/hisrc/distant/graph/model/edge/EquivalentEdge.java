package org.hisrc.distant.graph.model.edge;

public class EquivalentEdge extends TransitionEdge {

	@Override
	public Transition after(int timepoint) {
		return Transition.instant(timepoint);
	}

	@Override
	public int length() {
		return 0;
	}
	
	@Override
	public String toString() {
		return "===";
	}
}
