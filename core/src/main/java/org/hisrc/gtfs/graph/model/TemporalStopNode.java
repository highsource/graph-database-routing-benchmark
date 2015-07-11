package org.hisrc.gtfs.graph.model;

import org.onebusaway.gtfs.model.Stop;

public abstract class TemporalStopNode {

	// Reference to the stop
	private final Stop stop;
	// Time point
	private final int time;

	public TemporalStopNode(Stop stop, int time) {
		super();
		this.stop = stop;
		this.time = time;
	}

	public Stop getStop() {
		return stop;
	}

	public int getTime() {
		return time;
	}

	@Override
	public String toString() {
		return "(Arrival" + stop + "@" + time + ")";
	}
}
