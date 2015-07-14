package org.hisrc.gtfs.tdgraph.model.edge;

import org.apache.commons.lang3.Validate;

public class Transit {

	private final int departureTime;
	private final int arrivalTime;
	private final int duration;

	public Transit(int departureTime, int arrivalTime) {
		Validate.isTrue(arrivalTime >= departureTime);
		this.departureTime = departureTime;
		this.arrivalTime = arrivalTime;
		this.duration = arrivalTime - departureTime;
	}

	public int getDepartureTime() {
		return departureTime;
	}

	public int getArrivalTime() {
		return arrivalTime;
	}

	public int getDuration() {
		return duration;
	}

	public static Transit instant(int timepoint) {
		return new Transit(timepoint, timepoint);
	}
}
