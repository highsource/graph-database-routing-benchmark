package org.hisrc.gtfs.graph.builder.jgrapht;

import java.util.HashSet;
import java.util.Set;

import org.hisrc.gtfs.graph.builder.GraphBuilder;
import org.hisrc.gtfs.graph.model.edge.BoardEdge;
import org.hisrc.gtfs.graph.model.edge.ChildParentEdge;
import org.hisrc.gtfs.graph.model.edge.ParentChildEdge;
import org.hisrc.gtfs.graph.model.edge.RideEdge;
import org.hisrc.gtfs.graph.model.edge.StayEdge;
import org.hisrc.gtfs.graph.model.edge.TransitionEdge;
import org.hisrc.gtfs.graph.model.edge.UnboardEdge;
import org.hisrc.gtfs.graph.model.vertex.ParentStationVertex;
import org.hisrc.gtfs.graph.model.vertex.StopTimeVertex;
import org.hisrc.gtfs.graph.model.vertex.TemporalVertex;
import org.hisrc.gtfs.graph.model.vertex.TripStopArrivalVertex;
import org.hisrc.gtfs.graph.model.vertex.TripStopDepartureVertex;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JGraphTGraphBuilder implements GraphBuilder {

	private Logger logger = LoggerFactory.getLogger(JGraphTGraphBuilder.class);

	private DirectedGraph<TemporalVertex, TransitionEdge> graph = new DirectedMultigraph<TemporalVertex, TransitionEdge>(
			new EdgeFactory<TemporalVertex, TransitionEdge>() {
				@Override
				public TransitionEdge createEdge(TemporalVertex start,
						TemporalVertex stop) {
					throw new UnsupportedOperationException();
				}
			});

	public TemporalVertex addParentStationVertex(Stop stop, int time) {
		final ParentStationVertex vertex = new ParentStationVertex(stop, time);
		graph.addVertex(vertex);
		return vertex;
	}

	@Override
	public TemporalVertex addTripStopArrivalVertex(StopTime stopTime) {
		final TemporalVertex vertex = new TripStopArrivalVertex(
				stopTime.getTrip(), stopTime.getStop(),
				stopTime.getArrivalTime());
		graph.addVertex(vertex);
		return vertex;
	}

	private Trip lastTrip = null;
	private TemporalVertex lastTripStopDepartureVertex = null;
	private int lastStopSequence = -1;

	@Override
	public TemporalVertex addTripStopDepartureVertex(StopTime stopTime) {
		final TemporalVertex vertex = new TripStopDepartureVertex(
				stopTime.getTrip(), stopTime.getStop(),
				stopTime.getDepartureTime());
		graph.addVertex(vertex);
		
		lastTrip = stopTime.getTrip();
		lastTripStopDepartureVertex = vertex;
		lastStopSequence = stopTime.getStopSequence();
		return vertex;
	}
	
	private Set<Trip> processedTrips = new HashSet<Trip>();
	
	@Override
	public TemporalVertex findPreviousTripStopDepartureVertex(StopTime stopTime) {
		final Trip trip = stopTime.getTrip();
		final int stopSequence = stopTime.getStopSequence();
		final TemporalVertex previousDepartureNode;
		if (trip == lastTrip) {
			if (stopSequence <= lastStopSequence) {
				throw new IllegalStateException(
						"Stop sequence must be greater than the last stop sequence.");
			} else {
				previousDepartureNode = lastTripStopDepartureVertex;
			}
		} else {
			if (processedTrips.contains(trip)) {
				throw new IllegalStateException(
						"Trip was already processed and now appears again.");
			} else {
				processedTrips.add(lastTrip);
				previousDepartureNode = null;
			}
		}
		return previousDepartureNode;
	}

	@Override
	public TemporalVertex addStopTimeVertex(Stop stop, int time) {
		final TemporalVertex vertex = new StopTimeVertex(stop, time);
		graph.addVertex(vertex);
		return vertex;
	}

	public TransitionEdge addParentChildEdge(final TemporalVertex childVertex,
			final TemporalVertex parentVertex) {
		final ParentChildEdge edge = new ParentChildEdge();
		graph.addEdge(parentVertex, childVertex, edge);

		return edge;
	}

	public TransitionEdge addChildParentEdge(final TemporalVertex childVertex,
			final TemporalVertex parentVertex) {
		ChildParentEdge edge = new ChildParentEdge();
		graph.addEdge(childVertex, parentVertex, edge);
		return edge;
	}

	public TransitionEdge addStayEdge(final TemporalVertex arrivalVertex,
			final TemporalVertex departureVertex, int cost) {
		final TransitionEdge edge = new StayEdge(cost);
		graph.addEdge(arrivalVertex, departureVertex, edge);
		return edge;
	}

	public TransitionEdge addRideEdge(final TemporalVertex departureVertex,
			final TemporalVertex arrivalVertex, final int cost) {
		final TransitionEdge edge = new RideEdge(cost);
		graph.addEdge(departureVertex, arrivalVertex, edge);
		return edge;
	}

	@Override
	public TransitionEdge addBoardEdge(TemporalVertex departureStopTimeVertex,
			TemporalVertex departureVertex) {
		final TransitionEdge edge = new BoardEdge();
		graph.addEdge(departureStopTimeVertex, departureVertex, edge);
		return edge;
	}

	@Override
	public TransitionEdge addUnboardEdge(TemporalVertex arrivalVertex,
			TemporalVertex arrivalStopTimeVertex) {
		final TransitionEdge edge = new UnboardEdge();
		graph.addEdge(arrivalVertex, arrivalStopTimeVertex, edge);
		return edge;
	}

}
