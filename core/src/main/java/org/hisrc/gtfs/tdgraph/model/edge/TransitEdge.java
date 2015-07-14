package org.hisrc.gtfs.tdgraph.model.edge;

public abstract class TransitEdge {

	/**
	 * Searches for the transit with the earliest arrival starting not earlier
	 * that the given time point.
	 * 
	 * @param timepoint
	 *            time point.
	 * @return The found transit object or null if there are not transits not
	 *         earlier than the given point.
	 */
	public abstract Transit after(int timepoint);
}
