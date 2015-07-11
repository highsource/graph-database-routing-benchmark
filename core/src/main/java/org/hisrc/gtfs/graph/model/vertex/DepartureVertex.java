package org.hisrc.gtfs.graph.model.vertex;

import org.onebusaway.gtfs.model.Stop;

public class DepartureVertex extends TemporalVertex {

	public DepartureVertex(Stop stop, int time) {
		super(stop, time);
	}

}
