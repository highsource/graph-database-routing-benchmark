package org.hisrc.gtfs.graph.model.vertex;

import org.apache.commons.lang3.Validate;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.Trip;

public class TripStopArrivalVertex extends TemporalVertex {

	private final Trip trip;

	public TripStopArrivalVertex(Trip trip, Stop stop, int time) {
		super(stop, time);
		this.trip = trip;
	}

}
