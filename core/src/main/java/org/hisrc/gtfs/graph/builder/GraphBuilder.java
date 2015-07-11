package org.hisrc.gtfs.graph.builder;

import org.hisrc.gtfs.graph.model.edge.TransitionEdge;
import org.hisrc.gtfs.graph.model.vertex.TemporalVertex;
import org.onebusaway.gtfs.model.Stop;

public interface GraphBuilder {

	public TemporalVertex addParentStationVertex(Stop stop, int time);

	public TemporalVertex addArrivalVertex(final Stop stop,
			final int arrivalTime);

	public TemporalVertex addDepartureVertex(final Stop stop,
			final int departureTime);

	public TransitionEdge addArrivalDepartureEdge(TemporalVertex arrivalVertex,
			TemporalVertex departureVertex, int cost);

	public TransitionEdge addDepartureArrivalEdge(TemporalVertex arrivalVertex,
			TemporalVertex departureVertex, int cost);

	public TransitionEdge addParentChildEdge(TemporalVertex parentVertex,
			TemporalVertex childVertex);

	public TransitionEdge addChildParentEdge(TemporalVertex childVertex,
			TemporalVertex parentVertex);

}
