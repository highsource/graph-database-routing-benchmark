package org.hisrc.distant.graph.model.edge;

import org.apache.commons.lang3.Validate;

public class Transition {

	private final int departureTime;
	private final int arrivalTime;
	private final int duration;

	public Transition(int departureTime, int arrivalTime) {
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

	public static Transition instant(int timepoint) {
		return new Transition(timepoint, timepoint);
	}

	@Override
	public String toString() {
		return "" + departureTime + "->" + arrivalTime;
	}
}
