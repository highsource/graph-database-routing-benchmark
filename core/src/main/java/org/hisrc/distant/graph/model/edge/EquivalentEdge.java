package org.hisrc.distant.graph.model.edge;

public class EquivalentEdge extends TransitEdge {

	@Override
	public Transit after(int timepoint) {
		return Transit.instant(timepoint);
	}

	@Override
	public int length() {
		return 0;
	}
}
