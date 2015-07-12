package org.hisrc.gtfs.graph.model.path;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.hisrc.gtfs.graph.model.edge.TransitionEdge;
import org.hisrc.gtfs.graph.model.vertex.TemporalVertex;

public class Path {

	private final TemporalVertex startVertex;
	private final TemporalVertex endVertex;
	private final List<TemporalVertex> vertices;
	private final List<TransitionEdge> edges;
	private final int cost;

	public Path(List<TemporalVertex> vertices, List<TransitionEdge> edges) {
		Validate.notNull(vertices);
		Validate.notNull(edges);
		Validate.isTrue(vertices.size() == (edges.size() + 1));
		Validate.isTrue(vertices.size() > 1);
		this.vertices = Collections.unmodifiableList(vertices);
		this.edges = Collections.unmodifiableList(edges);
		this.startVertex = this.vertices.get(0);
		this.endVertex = this.vertices.get(vertices.size() - 1);

		int cost = 0;
		for (TransitionEdge edge : edges) {
			cost += edge.getCost();
		}
		this.cost = cost;
	}

	public TemporalVertex getStartVertex() {
		return startVertex;
	}

	public TemporalVertex getEndVertex() {
		return endVertex;
	}

	public List<TemporalVertex> getVertices() {
		return vertices;
	}

	public List<TransitionEdge> getEdges() {
		return edges;
	}

	public int getCost() {
		return cost;
	}
	
	

}
