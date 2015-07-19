package org.hisrc.distant.service.impl;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.hisrc.distant.graph.model.edge.Transition;
import org.hisrc.distant.graph.model.edge.TransitionArrivalTimeFunction;
import org.hisrc.distant.graph.model.edge.TransitionEdge;
import org.hisrc.distant.graph.model.edge.TransitionEdgeTransitionFunction;
import org.hisrc.distant.graph.model.vertex.StopVertex;
import org.hisrc.distant.jgrapht.FailingEdgeFactory;
import org.hisrc.distant.jgrapht.TimeDependentGraphPath;
import org.hisrc.distant.jgrapht.alg.TimeDependentDijkstraShortestPath;
import org.hisrc.distant.service.GraphService;
import org.jgrapht.DirectedGraph;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.ListenableDirectedGraph;

public class GraphServiceImpl implements GraphService {

	private final DirectedPseudograph<StopVertex, TransitionEdge> originalGraph = new DirectedPseudograph<StopVertex, TransitionEdge>(
			FailingEdgeFactory.<StopVertex, TransitionEdge> create());

	private final Map<String, StopVertex> verticesByIds = new HashMap<String, StopVertex>();

	private final GraphListener<StopVertex, TransitionEdge> graphListener = new GraphListener<StopVertex, TransitionEdge>() {

		@Override
		public void vertexAdded(GraphVertexChangeEvent<StopVertex> e) {
			final StopVertex vertex = e.getVertex();
			verticesByIds.put(vertex.getStopId(), vertex);
		}

		@Override
		public void vertexRemoved(GraphVertexChangeEvent<StopVertex> e) {
			final StopVertex vertex = e.getVertex();
			verticesByIds.remove(vertex.getStopId());
		}

		@Override
		public void edgeAdded(GraphEdgeChangeEvent<StopVertex, TransitionEdge> e) {
		}

		@Override
		public void edgeRemoved(
				GraphEdgeChangeEvent<StopVertex, TransitionEdge> e) {
		}
	};

	private final ListenableDirectedGraph<StopVertex, TransitionEdge> listenableGraph = new ListenableDirectedGraph<StopVertex, TransitionEdge>(
			this.originalGraph);
	{
		this.listenableGraph.addGraphListener(this.graphListener);
	}

	@Override
	public DirectedGraph<StopVertex, TransitionEdge> getGraph() {
		return listenableGraph;
	}

	@Override
	public StopVertex findVertexById(String id) {
		return this.verticesByIds.get(id);
	}

	@Override
	public TimeDependentGraphPath<StopVertex, TransitionEdge, Transition> findEarliestArrivalPath(
			String startStopId, String endStopId, int departureTime) {

		final StopVertex start = this.findVertexById(startStopId);
		Validate.notNull(start, MessageFormat.format(
				"Could not find start vertex by id [{0}].", startStopId));
		final StopVertex end = this.findVertexById(endStopId);
		Validate.notNull(end, MessageFormat.format(
				"Could not find end vertex by id [{0}].", endStopId));

		return new TimeDependentDijkstraShortestPath<StopVertex, TransitionEdge, Transition>(
				getGraph(), start, end,
				TransitionEdgeTransitionFunction.create(),
				TransitionArrivalTimeFunction.create(), departureTime).getPath();
	}

}
