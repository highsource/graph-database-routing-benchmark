package org.hisrc.gtfs.graph.model.vertex;

import org.onebusaway.gtfs.model.Stop;

public abstract class TemporalVertex {

	// Reference to the stop
	private final Stop stop;
	// Time point
	private final int time;

	public TemporalVertex(Stop stop, int time) {
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
