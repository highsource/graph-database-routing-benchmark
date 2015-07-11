package org.hisrc.gtfs.graph.model.vertex;

import org.onebusaway.gtfs.model.Stop;

public class ArrivalVertex extends TemporalVertex {

	public ArrivalVertex(Stop stop, int time) {
		super(stop, time);
	}

}
