package org.hisrc.gtfs.graph.service;

import java.util.Collection;

import org.hisrc.gtfs.graph.model.path.Path;
import org.hisrc.gtfs.graph.model.vertex.TemporalVertex;

public interface GraphService {

	public Path findShortestPathStartingAfter(String fromStopId,
			String toStopId, int time);

	public Path findShortestPathEndingBefore(String fromStopId,
			String toStopId, int time);

	public Path findShortestPath(Collection<TemporalVertex> startVertices,
			Collection<TemporalVertex> endVertices);

	public TemporalVertex findLatestTemporalVertexByStopIdBefore(String stopId,
			int time);

	public Collection<TemporalVertex> findTemporalVerticesByStopIdBefore(
			String stopId, int time);

	public TemporalVertex findEarliestTemporalVertexByStopIdBefore(
			String stopId, int time);

	public Collection<TemporalVertex> findTemporalVerticesByStopIdAfter(
			String stopId, int time);
}
