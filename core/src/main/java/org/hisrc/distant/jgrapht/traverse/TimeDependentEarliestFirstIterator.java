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
package org.hisrc.distant.jgrapht.traverse;

import java.text.MessageFormat;

import org.hisrc.distant.jgrapht.traverse.TimeDependentCrossComponentIterator.TransitionToVertexViaEdge;
import org.hisrc.distant.jgrapht.traverse.TimeDependentEarliestFirstIterator.QueueEntry;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.traverse.CrossComponentIterator;
import org.jgrapht.util.FibonacciHeap;
import org.jgrapht.util.FibonacciHeapNode;

/**
 * A closest-first iterator for a directed or undirected graph. For this
 * iterator to work correctly the graph must not be modified during iteration.
 * Currently there are no means to ensure that, nor to fail-fast. The results of
 * such modifications are undefined.
 *
 * <p>
 * The metric for <i>closest</i> here is the weighted path length from a start
 * vertex, i.e. Graph.getEdgeWeight(Edge) is summed to calculate path length.
 * Negative edge weights will result in an IllegalArgumentException. Optionally,
 * path length may be bounded by a finite radius.
 * </p>
 *
 * @author John V. Sichi
 * @since Sep 2, 2003
 */
public class TimeDependentEarliestFirstIterator<V, E, T>
		extends
		TimeDependentCrossComponentIterator<V, E, FibonacciHeapNode<QueueEntry<V, E, T>>, T> {

	/**
	 * Priority queue of fringe vertices.
	 */
	private FibonacciHeap<QueueEntry<V, E, T>> heap = new FibonacciHeap<QueueEntry<V, E, T>>();

	/**
	 * Maximum distance to search.
	 */
	private double radius = Double.POSITIVE_INFINITY;

	private boolean initialized = false;

	/**
	 * Creates a new closest-first iterator for the specified graph.
	 *
	 * @param g
	 *            the graph to be iterated.
	 */
	public TimeDependentEarliestFirstIterator(Graph<V, E> g,
			EdgeTransitionFunction<E, T> etf, ArrivalTimeFunction<T> tatf,
			int initialArrivalTime) {
		this(g, null, etf, tatf, initialArrivalTime);
	}

	/**
	 * Creates a new closest-first iterator for the specified graph. Iteration
	 * will start at the specified start vertex and will be limited to the
	 * connected component that includes that vertex. If the specified start
	 * vertex is <code>null</code>, iteration will start at an arbitrary vertex
	 * and will not be limited, that is, will be able to traverse all the graph.
	 *
	 * @param g
	 *            the graph to be iterated.
	 * @param startVertex
	 *            the vertex iteration to be started.
	 */
	public TimeDependentEarliestFirstIterator(Graph<V, E> g, V startVertex,
			EdgeTransitionFunction<E, T> etf, ArrivalTimeFunction<T> tatf,
			int initialArrivalTime) {
		this(g, startVertex, Double.POSITIVE_INFINITY, etf, tatf,
				initialArrivalTime);
	}

	/**
	 * Creates a new radius-bounded closest-first iterator for the specified
	 * graph. Iteration will start at the specified start vertex and will be
	 * limited to the subset of the connected component which includes that
	 * vertex and is reachable via paths of weighted length less than or equal
	 * to the specified radius. The specified start vertex may not be <code>
	 * null</code>.
	 *
	 * @param g
	 *            the graph to be iterated.
	 * @param startVertex
	 *            the vertex iteration to be started.
	 * @param radius
	 *            limit on weighted path length, or Double.POSITIVE_INFINITY for
	 *            unbounded search.
	 */
	public TimeDependentEarliestFirstIterator(Graph<V, E> g, V startVertex,
			double radius, EdgeTransitionFunction<E, T> etf,
			ArrivalTimeFunction<T> tatf, int initialArrivalTime) {
		super(g, startVertex, etf, tatf, initialArrivalTime);
		this.radius = radius;
		checkRadiusTraversal(isCrossComponentTraversal());
		initialized = true;
	}

	// override AbstractGraphIterator
	@Override
	public void setCrossComponentTraversal(boolean crossComponentTraversal) {
		if (initialized) {
			checkRadiusTraversal(crossComponentTraversal);
		}
		super.setCrossComponentTraversal(crossComponentTraversal);
	}

	/**
	 * Get the weighted length of the shortest path known to the given vertex.
	 * If the vertex has already been visited, then it is truly the shortest
	 * path length; otherwise, it is the best known upper bound.
	 *
	 * @param vertex
	 *            vertex being sought from start vertex
	 *
	 * @return weighted length of shortest path known, or
	 *         Double.POSITIVE_INFINITY if no path found yet
	 */
	public double getShortestPathLength(V vertex) {
		FibonacciHeapNode<QueueEntry<V, E, T>> node = getSeenData(vertex);

		if (node == null) {
			return Double.POSITIVE_INFINITY;
		}

		return node.getKey();
	}

	/**
	 * Get the spanning tree edge reaching a vertex which has been seen already
	 * in this traversal. This edge is the last link in the shortest known path
	 * between the start vertex and the requested vertex. If the vertex has
	 * already been visited, then it is truly the minimum spanning tree edge;
	 * otherwise, it is the best candidate seen so far.
	 *
	 * @param vertex
	 *            the spanned vertex.
	 *
	 * @return the spanning tree edge, or null if the vertex either has not been
	 *         seen yet or is the start vertex.
	 */
	public TransitionToVertexViaEdge<V, E, T> getSpanningTreeEdge(V vertex) {
		FibonacciHeapNode<QueueEntry<V, E, T>> node = getSeenData(vertex);

		if (node == null) {
			return null;
		}

		return node.getData();
	}

	/**
	 * @see CrossComponentIterator#isConnectedComponentExhausted()
	 */
	@Override
	protected boolean isConnectedComponentExhausted() {
		if (heap.size() == 0) {
			return true;
		} else {
			if (heap.min().getKey() > radius) {
				heap.clear();

				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * @see CrossComponentIterator#encounterVertex(Object, Object)
	 */
	@Override
	protected void encounterVertex(V vertex, E edge, T transitionToVertex) {
		int arrivalTime;
		final V oppositeVertex;
		if (edge == null || transitionToVertex == null) {
			oppositeVertex = null;
			arrivalTime = initialArrivalTime;
		} else {
			oppositeVertex = Graphs.getOppositeVertex(graph, edge, vertex);
			arrivalTime = transitionArrivalTimeFunction
					.apply(transitionToVertex);
		}
		
		
		FibonacciHeapNode<QueueEntry<V, E, T>> node = createSeenData(vertex,
				edge, transitionToVertex, arrivalTime);
		putSeenData(vertex, node);
		heap.insert(node, arrivalTime);
	}
	
	private int replacements = 0;

	/**
	 * Override superclass. When we see a vertex again, we need to see if the
	 * new edge provides a shorter path than the old edge.
	 *
	 * @param vertex
	 *            the vertex re-encountered
	 * @param edge
	 *            the edge via which the vertex was re-encountered
	 */
	@Override
	protected void encounterVertexAgain(V vertex, E edge, T transitionToVertex) {
		final FibonacciHeapNode<QueueEntry<V, E, T>> node = getSeenData(vertex);

		final QueueEntry<V, E, T> data = node.getData();
		if (data.frozen) {
			// no improvement for this vertex possible
			return;
		}
		final V oppositeVertex;
		if (edge == null ) {
			oppositeVertex = null;
		} else {
			oppositeVertex = Graphs.getOppositeVertex(graph, edge, vertex);
		}
		

		int candidateArrivalTime = transitionToVertex == null ? initialArrivalTime
				: transitionArrivalTimeFunction.apply(transitionToVertex);

		final int previousArrivalTime = node.getData().getArrivalTime();
		System.out
				.println(MessageFormat
						.format("Encountered [{0}->{1}] again at [{2}], previous arrival time [{3}].",
								oppositeVertex, vertex, candidateArrivalTime,
								previousArrivalTime));
		if (candidateArrivalTime < previousArrivalTime) {
			data.spanningTreeEdge = edge;
			data.transitionToVertex = transitionToVertex;
			data.arrivalTime = candidateArrivalTime;
			heap.decreaseKey(node, candidateArrivalTime);
			replacements++;
			System.out.println("Replacements [" + replacements + "]");
		}
	}

	/**
	 * @see CrossComponentIterator#provideNextVertex()
	 */
	@Override
	protected TransitionToVertexViaEdge<V, E, T> provideNextVertexTransition() {
		FibonacciHeapNode<QueueEntry<V, E, T>> node = heap.removeMin();
		node.getData().frozen = true;
		return node.getData();
	}

	private void checkRadiusTraversal(boolean crossComponentTraversal) {
		if (crossComponentTraversal && (radius != Double.POSITIVE_INFINITY)) {
			throw new IllegalArgumentException(
					"radius may not be specified for cross-component traversal");
		}
	}

	/**
	 * The first time we see a vertex, make up a new heap node for it.
	 *
	 * @param vertex
	 *            a vertex which has just been encountered.
	 * @param edge
	 *            the edge via which the vertex was encountered.
	 *
	 * @return the new heap node.
	 */
	private FibonacciHeapNode<QueueEntry<V, E, T>> createSeenData(V vertex,
			E edge, T transitionToVertex, int arrivalTime) {
		QueueEntry<V, E, T> entry = new QueueEntry<V, E, T>();
		entry.vertex = vertex;
		entry.spanningTreeEdge = edge;
		entry.transitionToVertex = transitionToVertex;
		entry.arrivalTime = arrivalTime;

		return new FibonacciHeapNode<QueueEntry<V, E, T>>(entry);
	}

	/**
	 * Private data to associate with each entry in the priority queue.
	 */
	static class QueueEntry<V, E, T> implements
			TransitionToVertexViaEdge<V, E, T> {
		/**
		 * Best spanning tree edge to vertex seen so far.
		 */
		E spanningTreeEdge;

		/**
		 * The vertex reached.
		 */
		V vertex;

		T transitionToVertex;

		int arrivalTime;

		/**
		 * True once spanningTreeEdge is guaranteed to be the true minimum.
		 */
		boolean frozen;

		public V getVertex() {
			return vertex;
		}

		public E getEdge() {
			return spanningTreeEdge;
		}

		public T getTransitionToVertex() {
			return transitionToVertex;
		}

		public int getArrivalTime() {
			return arrivalTime;
		}

		QueueEntry() {
		}
	}
}
