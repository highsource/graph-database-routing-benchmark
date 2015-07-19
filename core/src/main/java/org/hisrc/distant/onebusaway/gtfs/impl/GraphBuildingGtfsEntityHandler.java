package org.hisrc.distant.onebusaway.gtfs.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hisrc.distant.graph.model.edge.EquivalentEdge;
import org.hisrc.distant.graph.model.edge.RideEdge;
import org.hisrc.distant.graph.model.edge.TransferEdge;
import org.hisrc.distant.graph.model.edge.Transition;
import org.hisrc.distant.graph.model.edge.TransitionEdge;
import org.hisrc.distant.graph.model.vertex.StopVertex;
import org.jgrapht.DirectedGraph;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Transfer;
import org.onebusaway.gtfs.model.Trip;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class GraphBuildingGtfsEntityHandler extends DefaultGtfsEntityHandler {

	private final DirectedGraph<StopVertex, TransitionEdge> graph;

	public GraphBuildingGtfsEntityHandler(
			DirectedGraph<StopVertex, TransitionEdge> graph) {
		this.graph = graph;
	}

	private Map<String, StopVertex> stopVerticesById = new HashMap<String, StopVertex>();
	private Multimap<String, String> childStopIdsByParentStopId = HashMultimap
			.create();

	@Override
	public void handleStop(Stop stop) {
		final String parentStation = stop.getParentStation();
		final String parentStopId;
		if (!(parentStation == null || parentStation.isEmpty())) {
			parentStopId = new AgencyAndId(stop.getId().getAgencyId(),
					parentStation).toString();
		} else {
			parentStopId = null;
		}
		addStopVertex(stop.getId().toString(), stop.getName(), stop.getLat(),
				stop.getLon(), parentStopId);
	}

	private Trip lastTrip = null;
	private String lastStopId = null;
	private int lastDepartureTime = -1;
	private int lastStopSequence = -1;
	private Set<Trip> processedTrips = new HashSet<Trip>();

	@Override
	public void handleStopTime(StopTime stopTime) {
		final String toStopId = stopTime.getStop().getId().toString();
		final Trip trip = stopTime.getTrip();
		final int stopSequence = stopTime.getStopSequence();
		final String fromStopId;
		final int fromDepartureTime;
		if (trip == lastTrip) {
			if (stopSequence <= lastStopSequence) {
				throw new IllegalStateException(
						"Stop sequence must be greater than the last stop sequence.");
			} else {
				fromStopId = lastStopId;
				fromDepartureTime = lastDepartureTime;
			}
		} else {
			if (processedTrips.contains(trip)) {
				throw new IllegalStateException(
						"Trip was already processed and now appears again.");
			} else {
				processedTrips.add(lastTrip);
				fromStopId = null;
				fromDepartureTime = -1;
			}
		}
		final int toArrivalTime = stopTime.getArrivalTime();
		if (fromStopId != null) {
			addRideEdge(fromStopId, toStopId, fromDepartureTime, toArrivalTime);
		}

		lastTrip = trip;
		lastStopId = toStopId;
		lastStopSequence = stopTime.getStopSequence();
		lastDepartureTime = stopTime.getDepartureTime();
	}

	@Override
	public void handleTransfer(Transfer transfer) {
		final String fromStopId = transfer.getFromStop().getId().toString();
		final String toStopId = transfer.getToStop().getId().toString();
		final int minTransferTime = transfer.getMinTransferTime();
		addTransferEdge(fromStopId, toStopId, minTransferTime);
	}

	private void addStopVertex(final String stopId, String stopName,
			double stopLat, double stopLon, String parentStopId) {
		final StopVertex stopVertex = new StopVertex(stopId, stopName, stopLat,
				stopLon);
		this.graph.addVertex(stopVertex);
		this.stopVerticesById.put(stopId, stopVertex);

		final Collection<String> childStopIds = this.childStopIdsByParentStopId
				.get(stopId);
		if (childStopIds != null && !childStopIds.isEmpty()) {
			for (String childStopId : childStopIds) {
				addEquivalentEdges(stopId, childStopId);
			}
		}

		if (parentStopId != null && !parentStopId.isEmpty()) {
			if (this.stopVerticesById.containsKey(parentStopId)) {
				addEquivalentEdges(parentStopId, stopId);
			} else {
				this.childStopIdsByParentStopId.put(parentStopId, stopId);
			}
		}
	}

	private void addEquivalentEdges(String stopId1, String stopId2) {
		final StopVertex v1 = this.stopVerticesById.get(stopId1);
		final StopVertex v2 = this.stopVerticesById.get(stopId2);
		this.graph.addEdge(v1, v2, new EquivalentEdge());
		this.graph.addEdge(v2, v1, new EquivalentEdge());
	}

	private void addTransferEdge(String fromStopId, String toStopId,
			int minTransferTime) {
		final StopVertex v1 = this.stopVerticesById.get(fromStopId);
		final StopVertex v2 = this.stopVerticesById.get(toStopId);
		this.graph.addEdge(v1, v2, new TransferEdge(minTransferTime));

	}

	private void addRideEdge(String fromStopId, String toStopId,
			int fromDepartureTime, int toArrivalTime) {
		final StopVertex v1 = this.stopVerticesById.get(fromStopId);
		final StopVertex v2 = this.stopVerticesById.get(toStopId);
		final Set<TransitionEdge> edges = this.graph.getAllEdges(v1, v2);
		RideEdge edge = null;
		if (edges != null) {
			for (TransitionEdge e : edges) {
				if (e instanceof RideEdge) {
					edge = (RideEdge) e;
					break;
				}
			}
			if (edge == null) {
				edge = new RideEdge();
				this.graph.addEdge(v1, v2, edge);
			}
		}
		edge.addTransit(new Transition(fromDepartureTime, toArrivalTime));
	}
}
