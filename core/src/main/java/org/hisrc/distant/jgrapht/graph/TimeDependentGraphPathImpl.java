package org.hisrc.distant.jgrapht.graph;

import java.util.List;

import org.hisrc.distant.jgrapht.TimeDependentGraphPath;
import org.jgrapht.Graph;
import org.jgrapht.graph.GraphPathImpl;

public class TimeDependentGraphPathImpl<V, E, T> extends GraphPathImpl<V, E>
		implements TimeDependentGraphPath<V, E, T> {

	private List<T> transitionList;

	public TimeDependentGraphPathImpl(Graph<V, E> graph, V startVertex,
			V endVertex, List<E> edgeList, List<T> transitionList, double weight) {
		super(graph, startVertex, endVertex, edgeList, weight);
		this.transitionList = transitionList;
	}
	
	@Override
	public List<T> getTransitionList() {
		return transitionList;
	}

}
