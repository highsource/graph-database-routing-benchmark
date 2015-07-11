package org.hisrc.gtfs.graph.model;

import org.onebusaway.gtfs.model.Stop;

public class TemporalStopNode {

	// Reference to the stop
	private final Stop stop;
	// Time point
	private final int time;
	// Whether this is a starting point for the routing
	private final boolean departure;

	public TemporalStopNode(Stop stop, int time, boolean departure) {
		super();
		this.stop = stop;
		this.time = time;
		this.departure = departure;
	}

	public Stop getStop() {
		return stop;
	}

	public int getTime() {
		return time;
	}

	@Override
	public String toString() {
		return stop + "@" + time;
	}
}
