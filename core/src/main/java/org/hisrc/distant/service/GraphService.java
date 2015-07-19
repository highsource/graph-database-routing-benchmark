package org.hisrc.distant.service;

import org.hisrc.distant.graph.model.edge.Transition;
import org.hisrc.distant.graph.model.edge.TransitionEdge;
import org.hisrc.distant.graph.model.vertex.StopVertex;
import org.hisrc.distant.jgrapht.TimeDependentGraphPath;
import org.jgrapht.DirectedGraph;

public interface GraphService {

	public DirectedGraph<StopVertex, TransitionEdge> getGraph();
	
	public StopVertex findVertexById(String id);
	
	public TimeDependentGraphPath<StopVertex, TransitionEdge, Transition> findEarliestArrivalPath(String startStopId, String endStopId, int departureTime);
}
