package org.hisrc.gtfs.graph.model.vertex;

import org.onebusaway.gtfs.model.Stop;

public class ParentStationVertex extends TemporalVertex {

	public ParentStationVertex(Stop stop, int time) {
		super(stop, time);
	}
}
