package org.hisrc.gtfs.graph.builder.jgrapht;

import org.hisrc.gtfs.graph.builder.GraphBuilder;
import org.hisrc.gtfs.graph.model.edge.ArrivalDepartureEdge;
import org.hisrc.gtfs.graph.model.edge.ChildParentEdge;
import org.hisrc.gtfs.graph.model.edge.DepartureArrivalEdge;
import org.hisrc.gtfs.graph.model.edge.ParentChildEdge;
import org.hisrc.gtfs.graph.model.edge.TransitionEdge;
import org.hisrc.gtfs.graph.model.vertex.ArrivalVertex;
import org.hisrc.gtfs.graph.model.vertex.DepartureVertex;
import org.hisrc.gtfs.graph.model.vertex.ParentStationVertex;
import org.hisrc.gtfs.graph.model.vertex.TemporalVertex;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;
import org.onebusaway.gtfs.model.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JGraphTGraphBuilder implements GraphBuilder {

	private DirectedGraph<TemporalVertex, TransitionEdge> graph = new DirectedMultigraph<TemporalVertex, TransitionEdge>(
			new EdgeFactory<TemporalVertex, TransitionEdge>() {
				@Override
				public TransitionEdge createEdge(TemporalVertex start,
						TemporalVertex stop) {
					throw new UnsupportedOperationException();
				}
			});

	private Logger logger = LoggerFactory.getLogger(JGraphTGraphBuilder.class);

	public TemporalVertex addParentStationVertex(Stop stop, int time) {
		final ParentStationVertex vertex = new ParentStationVertex(stop, time);
		graph.addVertex(vertex);
		return vertex;
	}

	public TemporalVertex addArrivalVertex(final Stop stop,
			final int arrivalTime) {
		final TemporalVertex arrivalNode = new ArrivalVertex(stop, arrivalTime);
		graph.addVertex(arrivalNode);
		return arrivalNode;
	}

	public TemporalVertex addDepartureVertex(final Stop stop,
			final int departureTime) {
		final TemporalVertex departureNode = new DepartureVertex(stop,
				departureTime);
		graph.addVertex(departureNode);
		return departureNode;
	}

	public TransitionEdge addParentChildEdge(final TemporalVertex childVertex,
			final TemporalVertex parentVertex) {
		// logger.info("Adding [" + parentVertex + "-pc->" + childVertex + "]");
		final ParentChildEdge edge = new ParentChildEdge();
		graph.addEdge(parentVertex, childVertex, edge);

		return edge;
	}

	public TransitionEdge addChildParentEdge(final TemporalVertex childVertex,
			final TemporalVertex parentVertex) {
		// logger.info("Adding [" + childVertex + "-cp->" + parentVertex + "]");
		ChildParentEdge edge = new ChildParentEdge();
		graph.addEdge(childVertex, parentVertex, edge);
		return edge;
	}

	public TransitionEdge addArrivalDepartureEdge(
			final TemporalVertex arrivalVertex,
			final TemporalVertex departureVertex, int cost) {
		// logger.info("Adding [" + arrivalNode + "-" + departureNode + "]");
		final TransitionEdge edge = new ArrivalDepartureEdge(cost);
		graph.addEdge(arrivalVertex, departureVertex, edge);
		return edge;
	}

	public TransitionEdge addDepartureArrivalEdge(
			final TemporalVertex departureVertex,
			final TemporalVertex arrivalVertex, final int cost) {
		final TransitionEdge edge = new DepartureArrivalEdge(cost);
		graph.addEdge(departureVertex, arrivalVertex, edge);
		return edge;
	}
}
