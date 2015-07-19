/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.hisrc.distant.jgrapht.alg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisrc.distant.jgrapht.TimeDependentGraphPath;
import org.hisrc.distant.jgrapht.graph.TimeDependentGraphPathImpl;
import org.hisrc.distant.jgrapht.traverse.ArrivalTimeFunction;
import org.hisrc.distant.jgrapht.traverse.EdgeTransitionFunction;
import org.hisrc.distant.jgrapht.traverse.TimeDependentCrossComponentIterator.TransitionToVertexViaEdge;
import org.hisrc.distant.jgrapht.traverse.TimeDependentEarliestFirstIterator;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;

/**
 * An implementation of <a
 * href="http://mathworld.wolfram.com/DijkstrasAlgorithm.html">Dijkstra's
 * shortest path algorithm</a> using <code>ClosestFirstIterator</code>.
 *
 * @author John V. Sichi
 * @since Sep 2, 2003
 */
public final class TimeDependentDijkstraShortestPath<V, E, T> {

	private TimeDependentGraphPath<V, E, T> path;

	/**
	 * Creates and executes a new DijkstraShortestPath algorithm instance. An
	 * instance is only good for a single search; after construction, it can be
	 * accessed to retrieve information about the path found.
	 *
	 * @param graph
	 *            the graph to be searched
	 * @param startVertex
	 *            the vertex at which the path should start
	 * @param endVertex
	 *            the vertex at which the path should end
	 */
	public TimeDependentDijkstraShortestPath(Graph<V, E> graph, V startVertex,
			V endVertex, EdgeTransitionFunction<E, T> edgeTransitionFunction,
			ArrivalTimeFunction<T> transitionArrivalTimeFunction,
			int initialArrivalTime) {
		this(graph, startVertex, endVertex, Double.POSITIVE_INFINITY,
				edgeTransitionFunction, transitionArrivalTimeFunction,
				initialArrivalTime);
	}

	/**
	 * Creates and executes a new DijkstraShortestPath algorithm instance. An
	 * instance is only good for a single search; after construction, it can be
	 * accessed to retrieve information about the path found.
	 *
	 * @param graph
	 *            the graph to be searched
	 * @param startVertex
	 *            the vertex at which the path should start
	 * @param endVertex
	 *            the vertex at which the path should end
	 * @param radius
	 *            limit on weighted path length, or Double.POSITIVE_INFINITY for
	 *            unbounded search
	 */
	public TimeDependentDijkstraShortestPath(Graph<V, E> graph, V startVertex,
			V endVertex, double radius,
			EdgeTransitionFunction<E, T> edgeTransitionFunction,
			ArrivalTimeFunction<T> transitionArrivalTimeFunction,
			int initialArrivalTime) {
		if (!graph.containsVertex(endVertex)) {
			throw new IllegalArgumentException(
					"graph must contain the end vertex");
		}

		TimeDependentEarliestFirstIterator<V, E, T> iter = new TimeDependentEarliestFirstIterator<V, E, T>(
				graph, startVertex, radius, edgeTransitionFunction,
				transitionArrivalTimeFunction, initialArrivalTime);

		while (iter.hasNext()) {
			V vertex = iter.next();

			if (vertex.equals(endVertex)) {
				createEdgeList(graph, iter, startVertex, endVertex);
				return;
			}
		}

		path = null;
	}

	/**
	 * Return the edges making up the path found.
	 *
	 * @return List of Edges, or null if no path exists
	 */
	public List<E> getPathEdgeList() {
		if (path == null) {
			return null;
		} else {
			return path.getEdgeList();
		}
	}

	/**
	 * Return the path found.
	 *
	 * @return path representation, or null if no path exists
	 */
	public TimeDependentGraphPath<V, E, T> getPath() {
		return path;
	}

	/**
	 * Return the weighted length of the path found.
	 *
	 * @return path length, or Double.POSITIVE_INFINITY if no path exists
	 */
	public double getPathLength() {
		if (path == null) {
			return Double.POSITIVE_INFINITY;
		} else {
			return path.getWeight();
		}
	}

	private void createEdgeList(Graph<V, E> graph,
			TimeDependentEarliestFirstIterator<V, E, T> iter, V startVertex,
			V endVertex) {
		List<E> edgeList = new ArrayList<E>();
		List<T> transitionList = new ArrayList<T>();

		V v = endVertex;

		while (true) {
			TransitionToVertexViaEdge<V, E, T> vet = iter
					.getSpanningTreeEdge(v);

			if (vet == null || vet.getEdge() == null) {
				break;
			}

			final E edge = vet.getEdge();
			final T transition = vet.getTransitionToVertex();
			edgeList.add(edge);
			transitionList.add(transition);
			v = Graphs.getOppositeVertex(graph, edge, v);
		}

		Collections.reverse(edgeList);
		double pathLength = iter.getShortestPathLength(endVertex);
		path = new TimeDependentGraphPathImpl<V, E, T>(graph, startVertex,
				endVertex, edgeList, transitionList, pathLength);
	}
}

// End DijkstraShortestPath.java
