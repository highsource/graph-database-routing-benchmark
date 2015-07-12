package org.hisrc.gtfs.graph.model.vertex;

import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.Trip;

public class TripStopTimeVertex extends TemporalVertex {

	private final Trip trip;

	public TripStopTimeVertex(Trip trip, Stop stop, int time) {
		super(stop, time);
		this.trip = trip;
	}

	public Trip getTrip() {
		return trip;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((trip == null) ? 0 : trip.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TripStopTimeVertex other = (TripStopTimeVertex) obj;
		if (trip == null) {
			if (other.trip != null) {
				return false;
			}
		} else if (!trip.equals(other.trip)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "(" + getTrip() + " A " + getStop() + "@" + getTime() + ")";
	}

}
