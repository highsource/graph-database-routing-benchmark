package org.hisrc.distant.graph.model.edge;

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

	/**
	 * Returns the estimated length of the edge in meters, -1 for unknown, 0 for
	 * virtual edges like equivalent edge.
	 * 
	 * @return Length of the edge in meters, -1 for unknown or 0 for virtual.
	 */
	public abstract int length();
}
