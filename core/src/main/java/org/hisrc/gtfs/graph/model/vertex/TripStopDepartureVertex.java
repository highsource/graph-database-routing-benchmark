package org.hisrc.gtfs.graph.model.vertex;

import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.Trip;

public class TripStopDepartureVertex extends TemporalVertex {
	private final Trip trip;

	public TripStopDepartureVertex(Trip trip, Stop stop, int time) {
		super(stop, time);
		this.trip = trip;
	}

}
