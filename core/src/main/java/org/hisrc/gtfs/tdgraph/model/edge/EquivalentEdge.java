package org.hisrc.gtfs.tdgraph.model.edge;

public class EquivalentEdge extends TransitEdge {

	@Override
	public Transit after(int timepoint) {
		return Transit.instant(timepoint);
	}

}
