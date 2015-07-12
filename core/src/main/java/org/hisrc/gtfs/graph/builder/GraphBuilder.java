package org.hisrc.gtfs.graph.builder;

import org.hisrc.gtfs.graph.model.edge.TransitionEdge;
import org.hisrc.gtfs.graph.model.vertex.TemporalVertex;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;

public interface GraphBuilder {
	
	public TemporalVertex findPreviousTripStopDepartureVertex(StopTime stopTime);

	public TemporalVertex addTripStopArrivalVertex(StopTime stopTime);

	public TemporalVertex addTripStopDepartureVertex(StopTime stopTime);

	public TemporalVertex addStopTimeVertex(Stop stop, int time);

	public TemporalVertex addParentStationVertex(Stop stop, int time);

	public TransitionEdge addStayEdge(TemporalVertex arrivalVertex,
			TemporalVertex departureVertex, int cost);

	public TransitionEdge addRideEdge(TemporalVertex arrivalVertex,
			TemporalVertex departureVertex, int cost);

	public TransitionEdge addParentChildEdge(TemporalVertex parentVertex,
			TemporalVertex childVertex);

	public TransitionEdge addChildParentEdge(TemporalVertex childVertex,
			TemporalVertex parentVertex);

	public TransitionEdge addUnboardEdge(TemporalVertex arrivalVertex,
			TemporalVertex arrivalStopTimeVertex);

	public TransitionEdge addBoardEdge(TemporalVertex departureStopTimeVertex,
			TemporalVertex departureVertex);
	
}
