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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.hisrc.distant.jgrapht.event.TransitionOverEdgeTraversalEvent;
import org.hisrc.distant.jgrapht.event.TransitionToVertexTraversalEvent;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.traverse.AbstractGraphIterator;

/**
 * Provides a cross-connected-component traversal functionality for iterator
 * subclasses.
 *
 * @param <V>
 *            vertex type
 * @param <E>
 *            edge type
 * @param <D>
 *            type of data associated to seen vertices *
 * @author Barak Naveh
 * @since Jan 31, 2004
 */
public abstract class TimeDependentCrossComponentIterator<V, E, D, T> extends
		AbstractGraphIterator<V, E> {

	private static final int CCS_BEFORE_COMPONENT = 1;
	private static final int CCS_WITHIN_COMPONENT = 2;
	private static final int CCS_AFTER_COMPONENT = 3;

	/**
	 * Standard vertex visit state enumeration.
	 */
	protected static enum VisitColor {
		/**
		 * Vertex has not been returned via iterator yet.
		 */
		WHITE,

		/**
		 * Vertex has been returned via iterator, but we're not done with all of
		 * its out-edges yet.
		 */
		GRAY,

		/**
		 * Vertex has been returned via iterator, and we're done with all of its
		 * out-edges.
		 */
		BLACK
	}

	//
	private final ConnectedComponentTraversalEvent ccFinishedEvent = new ConnectedComponentTraversalEvent(
			this, ConnectedComponentTraversalEvent.CONNECTED_COMPONENT_FINISHED);
	private final ConnectedComponentTraversalEvent ccStartedEvent = new ConnectedComponentTraversalEvent(
			this, ConnectedComponentTraversalEvent.CONNECTED_COMPONENT_STARTED);

	// TODO: support ConcurrentModificationException if graph modified
	// during iteration.
	private FlyweightTransitionOverEdgeEvent<V, E, T> reusableEdgeEvent;
	private FlyweightTransitionToVertexEvent<V, T> reusableVertexEvent;
	private Iterator<V> vertexIterator = null;

	/**
	 * Stores the vertices that have been seen during iteration and (optionally)
	 * some additional traversal info regarding each vertex.
	 */
	private Map<V, D> seen = new HashMap<V, D>();
	private V startVertex;
	private Specifics<V, E> specifics;

	protected final Graph<V, E> graph;

	protected final EdgeTransitionFunction<E, T> edgeTransitionFunction;
	protected final ArrivalTimeFunction<T> transitionArrivalTimeFunction;
	protected int initialArrivalTime;

	/**
	 * The connected component state
	 */
	private int state = CCS_BEFORE_COMPONENT;

	/**
	 * Creates a new iterator for the specified graph. Iteration will start at
	 * the specified start vertex. If the specified start vertex is <code>
	 * null</code>, Iteration will start at an arbitrary graph vertex.
	 *
	 * @param g
	 *            the graph to be iterated.
	 * @param startVertex
	 *            the vertex iteration to be started.
	 *
	 * @throws IllegalArgumentException
	 *             if <code>g==null</code> or does not contain
	 *             <code>startVertex</code>
	 */
	public TimeDependentCrossComponentIterator(Graph<V, E> g, V startVertex,
			EdgeTransitionFunction<E, T> etf, ArrivalTimeFunction<T> tatf,
			int initialArrivalTime) {
		super();
		this.initialArrivalTime = initialArrivalTime;

		if (g == null) {
			throw new IllegalArgumentException("graph must not be null");
		}
		graph = g;
		if (etf == null) {
			throw new IllegalArgumentException(
					"edge transition function must not be null");
		}
		edgeTransitionFunction = etf;
		if (tatf == null) {
			throw new IllegalArgumentException(
					"transition arrival time function must not be null");
		}
		transitionArrivalTimeFunction = tatf;

		specifics = createGraphSpecifics(g);
		vertexIterator = g.vertexSet().iterator();
		setCrossComponentTraversal(startVertex == null);

		reusableEdgeEvent = new FlyweightTransitionOverEdgeEvent<V, E, T>(this,
				null, null);
		reusableVertexEvent = new FlyweightTransitionToVertexEvent<V, T>(this,
				null, null);

		if (startVertex == null) {
			// pick a start vertex if graph not empty
			if (vertexIterator.hasNext()) {
				this.startVertex = vertexIterator.next();
			} else {
				this.startVertex = null;
			}
		} else if (g.containsVertex(startVertex)) {
			this.startVertex = startVertex;
		} else {
			throw new IllegalArgumentException(
					"graph must contain the start vertex");
		}
	}

	/**
	 * @return the graph being traversed
	 */
	public Graph<V, E> getGraph() {
		return graph;
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (startVertex != null) {
			encounterStartVertex();
		}

		if (isConnectedComponentExhausted()) {
			if (state == CCS_WITHIN_COMPONENT) {
				state = CCS_AFTER_COMPONENT;
				if (nListeners != 0) {
					fireConnectedComponentFinished(ccFinishedEvent);
				}
			}

			if (isCrossComponentTraversal()) {
				while (vertexIterator.hasNext()) {
					V v = vertexIterator.next();

					if (!isSeenVertex(v)) {
						encounterVertex(v, null, null);
						state = CCS_BEFORE_COMPONENT;

						return true;
					}
				}

				return false;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	@Override
	public V next() {
		if (startVertex != null) {
			encounterStartVertex();
		}

		if (hasNext()) {
			if (state == CCS_BEFORE_COMPONENT) {
				state = CCS_WITHIN_COMPONENT;
				if (nListeners != 0) {
					fireConnectedComponentStarted(ccStartedEvent);
				}
			}

			TransitionToVertexViaEdge<V, E, T> nextVertexTransition = provideNextVertexTransition();
			final V nextVertex = nextVertexTransition.getVertex();
			final T transitionToNextVertex = nextVertexTransition
					.getTransitionToVertex();
			final int arrivalTime = nextVertexTransition.getArrivalTime();
			if (nListeners != 0) {
				fireVertexTraversed(createVertexArrivalTraversalEvent(
						nextVertex, transitionToNextVertex));
			}

			addUnseenChildrenOf(nextVertex, transitionToNextVertex, arrivalTime);

			return nextVertex;
		} else {
			throw new NoSuchElementException();
		}
	}

	/**
	 * Returns <tt>true</tt> if there are no more uniterated vertices in the
	 * currently iterated connected component; <tt>false</tt> otherwise.
	 *
	 * @return <tt>true</tt> if there are no more uniterated vertices in the
	 *         currently iterated connected component; <tt>false</tt> otherwise.
	 */
	protected abstract boolean isConnectedComponentExhausted();

	/**
	 * Update data structures the first time we see a vertex.
	 *
	 * @param vertex
	 *            the vertex encountered
	 * @param edge
	 *            the edge via which the vertex was encountered, or null if the
	 *            vertex is a starting point
	 */
	protected abstract void encounterVertex(V vertex, E edge,
			T transitionToVertex);

	/**
	 * Returns the vertex to be returned in the following call to the iterator
	 * <code>next</code> method.
	 *
	 * @return the next vertex to be returned by this iterator.
	 */
	protected abstract TransitionToVertexViaEdge<V, E, T> provideNextVertexTransition();

	/**
	 * Access the data stored for a seen vertex.
	 *
	 * @param vertex
	 *            a vertex which has already been seen.
	 *
	 * @return data associated with the seen vertex or <code>null</code> if no
	 *         data was associated with the vertex. A <code>null</code> return
	 *         can also indicate that the vertex was explicitly associated with
	 *         <code>
	 * null</code>.
	 */
	protected D getSeenData(V vertex) {
		return seen.get(vertex);
	}

	/**
	 * Determines whether a vertex has been seen yet by this traversal.
	 *
	 * @param vertex
	 *            vertex in question
	 *
	 * @return <tt>true</tt> if vertex has already been seen
	 */
	protected boolean isSeenVertex(Object vertex) {
		return seen.containsKey(vertex);
	}

	/**
	 * Called whenever we re-encounter a vertex. The default implementation does
	 * nothing.
	 *
	 * @param vertex
	 *            the vertex re-encountered
	 * @param edge
	 *            the edge via which the vertex was re-encountered
	 */
	protected abstract void encounterVertexAgain(V vertex, E edge,
			T transitionToVertex);

	/**
	 * Stores iterator-dependent data for a vertex that has been seen.
	 *
	 * @param vertex
	 *            a vertex which has been seen.
	 * @param data
	 *            data to be associated with the seen vertex.
	 *
	 * @return previous value associated with specified vertex or <code>
	 * null</code> if no data was associated with the vertex. A <code>
	 * null</code> return can also indicate that the vertex was explicitly
	 *         associated with <code>null</code>.
	 */
	protected D putSeenData(V vertex, D data) {
		D d = seen.put(vertex, data);
		System.out.println("Seen [" + seen.size() + "].");
		return d;
	}

	/**
	 * Called when a vertex has been finished (meaning is dependent on traversal
	 * represented by subclass).
	 *
	 * @param vertex
	 *            vertex which has been finished
	 */
	protected void finishVertex(V vertex, T transition) {
		if (nListeners != 0) {
			fireVertexFinished(createVertexArrivalTraversalEvent(vertex,
					transition));
		}
	}

	// -------------------------------------------------------------------------
	/**
	 * @param <V>
	 * @param <E>
	 * @param g
	 *
	 * @return TODO Document me
	 */
	static <V, E> Specifics<V, E> createGraphSpecifics(Graph<V, E> g) {
		if (g instanceof DirectedGraph<?, ?>) {
			return new DirectedSpecifics<V, E>((DirectedGraph<V, E>) g);
		} else {
			return new UndirectedSpecifics<V, E>(g);
		}
	}

	private void addUnseenChildrenOf(V vertex, T transitionToVertex,
			int arrivalTime) {
		for (E edge : specifics.edgesOf(vertex)) {
			final T transitionToOppositeVertex = edgeTransitionFunction.apply(
					edge, arrivalTime);
			if (transitionToOppositeVertex == null)
			{
				continue;
			}
			if (nListeners != 0) {
				fireEdgeTraversed(createEdgeTraversalEvent(edge,
						transitionToOppositeVertex));
			}

			V oppositeVertex = Graphs.getOppositeVertex(graph, edge, vertex);
			if (isSeenVertex(oppositeVertex)) {
				encounterVertexAgain(oppositeVertex, edge,
						transitionToOppositeVertex);
			} else {
				encounterVertex(oppositeVertex, edge,
						transitionToOppositeVertex);
			}
		}
	}

	private EdgeTraversalEvent<V, E> createEdgeTraversalEvent(E edge,
			T transition) {
		if (isReuseEvents()) {
			reusableEdgeEvent.setEdge(edge);
			reusableEdgeEvent.setTransition(transition);

			return reusableEdgeEvent;
		} else {
			return new TransitionOverEdgeTraversalEvent<V, E, T>(this, edge,
					transition);
		}
	}

	private VertexTraversalEvent<V> createVertexArrivalTraversalEvent(V vertex,
			T transition) {
		if (isReuseEvents()) {
			reusableVertexEvent.setVertex(vertex);
			reusableVertexEvent.setTransition(transition);

			return reusableVertexEvent;
		} else {
			return new TransitionToVertexTraversalEvent<V, T>(this, vertex,
					transition);
		}
	}

	private void encounterStartVertex() {
		encounterVertex(startVertex, null, null);
		startVertex = null;
	}

	static interface SimpleContainer<T> {
		/**
		 * Tests if this container is empty.
		 *
		 * @return <code>true</code> if empty, otherwise <code>false</code>.
		 */
		public boolean isEmpty();

		/**
		 * Adds the specified object to this container.
		 *
		 * @param o
		 *            the object to be added.
		 */
		public void add(T o);

		/**
		 * Remove an object from this container and return it.
		 *
		 * @return the object removed from this container.
		 */
		public T remove();
	}

	/**
	 * Provides unified interface for operations that are different in directed
	 * graphs and in undirected graphs.
	 */
	abstract static class Specifics<VV, EE> {
		/**
		 * Returns the edges outgoing from the specified vertex in case of
		 * directed graph, and the edge touching the specified vertex in case of
		 * undirected graph.
		 *
		 * @param vertex
		 *            the vertex whose outgoing edges are to be returned.
		 *
		 * @return the edges outgoing from the specified vertex in case of
		 *         directed graph, and the edge touching the specified vertex in
		 *         case of undirected graph.
		 */
		public abstract Set<? extends EE> edgesOf(VV vertex);
	}

	/**
	 * A reusable edge event.
	 *
	 * @author Barak Naveh
	 * @since Aug 11, 2003
	 */
	static class FlyweightTransitionOverEdgeEvent<VV, localE, localT> extends
			TransitionOverEdgeTraversalEvent<VV, localE, localT> {
		private static final long serialVersionUID = 4051327833765000755L;

		/**
		 * @see EdgeTraversalEvent#EdgeTraversalEvent(Object, Edge)
		 */
		public FlyweightTransitionOverEdgeEvent(Object eventSource,
				localE edge, localT transition) {
			super(eventSource, edge, transition);
		}

		/**
		 * Sets the edge of this event.
		 *
		 * @param edge
		 *            the edge to be set.
		 */
		protected void setEdge(localE edge) {
			this.edge = edge;
		}

		protected void setTransition(localT transition) {
			this.transition = transition;
		}
	}

	/**
	 * A reusable vertex event.
	 *
	 * @author Barak Naveh
	 * @since Aug 11, 2003
	 */
	static class FlyweightTransitionToVertexEvent<VV, localT> extends
			TransitionToVertexTraversalEvent<VV, localT> {

		private static final long serialVersionUID = -5874391335009939375L;

		/**
		 * @see VertexTraversalEvent#VertexTraversalEvent(Object, Object)
		 */
		public FlyweightTransitionToVertexEvent(Object eventSource, VV vertex,
				localT transition) {
			super(eventSource, vertex, transition);
		}

		/**
		 * Sets the vertex of this event.
		 *
		 * @param vertex
		 *            the vertex to be set.
		 */
		protected void setVertex(VV vertex) {
			this.vertex = vertex;
		}

		protected void setTransition(localT transition) {
			this.transition = transition;
		}
	}

	/**
	 * An implementation of {@link Specifics} for a directed graph.
	 */
	private static class DirectedSpecifics<VV, EE> extends Specifics<VV, EE> {
		private DirectedGraph<VV, EE> graph;

		/**
		 * Creates a new DirectedSpecifics object.
		 *
		 * @param g
		 *            the graph for which this specifics object to be created.
		 */
		public DirectedSpecifics(DirectedGraph<VV, EE> g) {
			graph = g;
		}

		/**
		 * @see TimeDependentCrossComponentIterator.Specifics#edgesOf(Object)
		 */
		@Override
		public Set<? extends EE> edgesOf(VV vertex) {
			return graph.outgoingEdgesOf(vertex);
		}
	}

	/**
	 * An implementation of {@link Specifics} in which edge direction (if any)
	 * is ignored.
	 */
	private static class UndirectedSpecifics<VV, EE> extends Specifics<VV, EE> {
		private Graph<VV, EE> graph;

		/**
		 * Creates a new UndirectedSpecifics object.
		 *
		 * @param g
		 *            the graph for which this specifics object to be created.
		 */
		public UndirectedSpecifics(Graph<VV, EE> g) {
			graph = g;
		}

		/**
		 * @see TimeDependentCrossComponentIterator.Specifics#edgesOf(Object)
		 */
		@Override
		public Set<EE> edgesOf(VV vertex) {
			return graph.edgesOf(vertex);
		}
	}

	public interface TransitionToVertexViaEdge<VV, EE, TT> {

		public VV getVertex();

		public EE getEdge();

		public TT getTransitionToVertex();

		public int getArrivalTime();
	}
}

// End CrossComponentIterator.java
