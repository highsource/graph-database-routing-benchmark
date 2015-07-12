package org.hisrc.gtfs.graph.model.vertex;

import org.onebusaway.gtfs.model.Stop;

public class StopTimeVertex extends TemporalVertex {

	public StopTimeVertex(Stop stop, int time) {
		super(stop, time);
	}
}
