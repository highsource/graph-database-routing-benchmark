package org.hisrc.gtfs.graph.servicebuilder.jgrapht;

import java.util.Collection;
import java.util.Map.Entry;

import org.hisrc.gtfs.graph.model.edge.BoardEdge;
import org.hisrc.gtfs.graph.model.edge.ChildParentEdge;
import org.hisrc.gtfs.graph.model.edge.ParentChildEdge;
import org.hisrc.gtfs.graph.model.edge.RideEdge;
import org.hisrc.gtfs.graph.model.edge.StayEdge;
import org.hisrc.gtfs.graph.model.edge.TransferEdge;
import org.hisrc.gtfs.graph.model.edge.TransitionEdge;
import org.hisrc.gtfs.graph.model.edge.UnboardEdge;
import org.hisrc.gtfs.graph.model.edge.WaitEdge;
import org.hisrc.gtfs.graph.model.util.TimeAwareComparator;
import org.hisrc.gtfs.graph.model.vertex.StopTimeVertex;
import org.hisrc.gtfs.graph.model.vertex.TemporalVertex;
import org.hisrc.gtfs.graph.model.vertex.TripStopTimeVertex;
import org.hisrc.gtfs.graph.service.GraphService;
import org.hisrc.gtfs.graph.service.jgrapht.JGraphTGraphService;
import org.hisrc.gtfs.graph.servicebuilder.GraphServiceBuilder;
import org.hisrc.gtfs.onebusaway.model.util.StopComparator;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

public class JGraphTGraphServiceBuilder implements GraphServiceBuilder {

	private Logger logger = LoggerFactory
			.getLogger(JGraphTGraphServiceBuilder.class);

	private DirectedGraph<TemporalVertex, TransitionEdge> graph = new DirectedMultigraph<TemporalVertex, TransitionEdge>(
			new EdgeFactory<TemporalVertex, TransitionEdge>() {
				@Override
				public TransitionEdge createEdge(TemporalVertex start,
						TemporalVertex stop) {
					throw new UnsupportedOperationException();
				}
			});

	@Override
	public TemporalVertex addTripStopTimeVertex(Trip trip, Stop stop, int time) {
		final TemporalVertex vertex = new TripStopTimeVertex(trip, stop, time);
		graph.addVertex(vertex);
		return vertex;
	}

	private Multimap<Stop, TemporalVertex> stopTimeVerticesByStop = TreeMultimap
			.create(StopComparator.create(), TimeAwareComparator.create());

	@Override
	public TemporalVertex addStopTimeVertex(Stop stop, int time) {
		final TemporalVertex vertex = new StopTimeVertex(stop, time);
		graph.addVertex(vertex);
		stopTimeVerticesByStop.put(stop, vertex);
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

	@Override
	public TransitionEdge addTransferEdge(TemporalVertex sourceVertex,
			TemporalVertex targetVertex, int cost) {
		final TransitionEdge edge = new TransferEdge(cost);
		graph.addEdge(sourceVertex, targetVertex, edge);
		return edge;
	}

	@Override
	public TransitionEdge addWaitEdge(TemporalVertex sourceVertex,
			TemporalVertex targetVertex, int waitTime) {
		final TransitionEdge edge = new WaitEdge(waitTime);
		graph.addEdge(sourceVertex, targetVertex, edge);
		return edge;
	}

	private void addWaitEdges() {
		for (Entry<Stop, Collection<TemporalVertex>> entry : stopTimeVerticesByStop
				.asMap().entrySet()) {

			final Stop stop = entry.getKey();

			final Collection<TemporalVertex> stopTimeVertices = entry
					.getValue();

			TemporalVertex lastVertex = null;

			for (TemporalVertex currentVertex : stopTimeVertices) {
				if (lastVertex != null) {
					int waitTime = currentVertex.getTime()
							- lastVertex.getTime();
					addWaitEdge(lastVertex, currentVertex, waitTime);
				}
				lastVertex = currentVertex;
			}
		}
	}

	@Override
	public GraphService build() {
		addWaitEdges();
		return new JGraphTGraphService(this.graph);
	}
}
