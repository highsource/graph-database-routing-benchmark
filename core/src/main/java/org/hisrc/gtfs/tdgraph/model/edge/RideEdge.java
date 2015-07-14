package org.hisrc.gtfs.tdgraph.model.edge;

import org.apache.commons.lang3.Validate;

public class RideEdge extends TransitEdge {

	public static final int MINUTES = 2880;

	private Transit[] transits = new Transit[MINUTES];

	/**
	 * Adds the transit to the edge.
	 * 
	 * @param transit
	 *            transit to be added.
	 */
	public void addTransit(Transit transit) {
		Validate.notNull(transit);
		final int departureTime = transit.getDepartureTime();
		final int arrivalTime = transit.getArrivalTime();
		for (int index = departureTime; index >= 0; index--) {
			final Transit bestTransit = transits[index];
			if (bestTransit == null
					|| bestTransit.getArrivalTime() > arrivalTime) {
				transits[index] = transit;
			} else {
				// We've encountered a transit which is already better than this
				// one.
				// It does not make sense to go earlier as all transits in that
				// direction will not be worse.
				break;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Transit after(int timepoint) {
		Validate.isTrue(timepoint >= 0);
		Validate.isTrue(timepoint < MINUTES);
		return transits[timepoint];
	}
}
