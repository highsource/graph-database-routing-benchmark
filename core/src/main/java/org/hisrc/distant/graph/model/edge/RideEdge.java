package org.hisrc.distant.graph.model.edge;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.lang3.Validate;
import org.hisrc.distant.util.ReverseComparableComparator;

public class RideEdge extends TransitEdge {

	// Transits by departure time, sorted in the descending order of departure
	// times. That is, later departures come first.
	private NavigableMap<Integer, Transit> transits = new TreeMap<Integer, Transit>(
			ReverseComparableComparator.<Integer> create());

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

		// Find transits which depart not later than the given departure time
		final NavigableMap<Integer, Transit> earlierTransits = transits
				.tailMap(departureTime, true);

		final Iterator<Entry<Integer, Transit>> earlierTransitsEntriesIterator = earlierTransits
				.entrySet().iterator();

		// Iterate earlier transits (in descending order, that is later ->
		// earlier)
		while (earlierTransitsEntriesIterator.hasNext()) {

			final Entry<Integer, Transit> earlierTransitEntry = earlierTransitsEntriesIterator
					.next();

			if (earlierTransitEntry.getValue().getArrivalTime() > arrivalTime) {
				// If we encounter a transit which departs earlier but arrives
				// later than the given transit, remove this earlier transit
				earlierTransitsEntriesIterator.remove();
			} else {
				// If we encounter a transit which arrives not later, stop the
				// iteration as all the following transits will not arrive
				// earlier as well by construction.
				break;
			}

		}

		final NavigableMap<Integer, Transit> laterTransits = transits.headMap(
				departureTime, true);
		boolean uselessTransit = false;
		for (Transit laterTransit : laterTransits.values()) {
			if (laterTransit.getArrivalTime() <= arrivalTime) {
				uselessTransit = true;
			}
		}

		if (!uselessTransit) {
			transits.put(transit.getDepartureTime(), transit);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Transit after(int timepoint) {
		// Since transits are reverse-ordered (later departures come first),
		// floorEntry returns the transit with earliest departure time but not
		// earlier that the timepoint
		final Entry<Integer, Transit> earliestEntry = transits
				.floorEntry(timepoint);
		return earliestEntry == null ? null : earliestEntry.getValue();
	}

	@Override
	public int length() {
		return -1;
	}
}
