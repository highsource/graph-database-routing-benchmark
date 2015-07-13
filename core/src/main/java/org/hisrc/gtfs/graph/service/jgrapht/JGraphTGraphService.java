package org.hisrc.gtfs.graph.service.jgrapht;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;

import org.hisrc.gtfs.graph.model.edge.TransitionEdge;
import org.hisrc.gtfs.graph.model.path.Path;
import org.hisrc.gtfs.graph.model.util.TimeAwareComparator;
import org.hisrc.gtfs.graph.model.vertex.StopTimeVertex;
import org.hisrc.gtfs.graph.model.vertex.TemporalVertex;
import org.hisrc.gtfs.graph.service.GraphService;
import org.hisrc.util.ComparableComparator;
import org.jgrapht.DirectedGraph;
import org.onebusaway.gtfs.model.Stop;

import com.google.common.collect.TreeMultimap;

public class JGraphTGraphService implements GraphService {

	private final DirectedGraph<TemporalVertex, TransitionEdge> graph;
	private final Map<String, Stop> stopsById = new HashMap<String, Stop>();
	private final TreeMultimap<String, TemporalVertex> stopTimeVerticesByStopId = TreeMultimap
			.create(ComparableComparator.<String> create(),
					TimeAwareComparator.create());

	public JGraphTGraphService(
			DirectedGraph<TemporalVertex, TransitionEdge> graph) {
		this.graph = graph;
		for (TemporalVertex vertex : graph.vertexSet()) {
			if (vertex.isEntryVertex()) {
				final Stop stop = vertex.getStop();
				final String stopId = stop.getId().toString();
				stopTimeVerticesByStopId.put(stopId, vertex);
				stopsById.put(stopId, stop);
			}
		}
	}

	private Stop findStopById(String stopId) {
		final Stop stop = this.stopsById.get(stopId);
		return stop;
	}

	@Override
	public TemporalVertex findLatestTemporalVertexByStopIdBefore(String stopId,
			int time) {
		final Stop stop = findStopById(stopId);
		if (stop == null) {
			return null;
		} else {
			final StopTimeVertex stopTimeVertex = new StopTimeVertex(stop, time);
			final NavigableSet<TemporalVertex> stopTimeVertices = this.stopTimeVerticesByStopId
					.get(stopId);
			return stopTimeVertices.floor(stopTimeVertex);
		}
	}

	@Override
	public TemporalVertex findEarliestTemporalVertexByStopIdBefore(
			String stopId, int time) {
		final Stop stop = findStopById(stopId);
		if (stop == null) {
			return null;
		} else {
			final StopTimeVertex stopTimeVertex = new StopTimeVertex(stop, time);
			final NavigableSet<TemporalVertex> stopTimeVertices = this.stopTimeVerticesByStopId
					.get(stopId);
			return stopTimeVertices.ceiling(stopTimeVertex);
		}
	}

	@Override
	public Path findShortestPathStartingAfter(String fromStopId,
			String toStopId, int time) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Path findShortestPathEndingBefore(String fromStopId,
			String toStopId, int time) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Path findShortestPath(Collection<TemporalVertex> startVertices,
			Collection<TemporalVertex> endVertices) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<TemporalVertex> findTemporalVerticesByStopIdBefore(
			String stopId, int time) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<TemporalVertex> findTemporalVerticesByStopIdAfter(
			String stopId, int time) {
		throw new UnsupportedOperationException();
	}
}
