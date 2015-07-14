package org.hisrc.gtfs.tdgraph.model.edge;

public class TransferEdge extends TransitEdge {

	private final int transferDuration;

	public TransferEdge(int transferDuration) {
		this.transferDuration = transferDuration;
	}

	@Override
	public Transit after(int timepoint) {
		return new Transit(timepoint, timepoint + transferDuration);
	}
}
