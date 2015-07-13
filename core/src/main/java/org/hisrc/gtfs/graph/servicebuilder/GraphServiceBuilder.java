package org.hisrc.gtfs.graph.servicebuilder;

import org.hisrc.gtfs.graph.model.edge.TransitionEdge;
import org.hisrc.gtfs.graph.model.vertex.TemporalVertex;
import org.hisrc.gtfs.graph.service.GraphService;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.Trip;

public interface GraphServiceBuilder {

	public TemporalVertex addTripStopTimeVertex(Trip trip, Stop stop, int time);

	public TemporalVertex addStopTimeVertex(Stop stop, int time);

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

	public TransitionEdge addTransferEdge(TemporalVertex sourceVertex,
			TemporalVertex targetVertex, int transferTime);

	public TransitionEdge addWaitEdge(TemporalVertex sourceVertex,
			TemporalVertex targetVertex, int waitTime);

	public GraphService build();

}
