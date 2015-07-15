package org.hisrc.distant.jgrapht;

import org.jgrapht.EdgeFactory;

public class FailingEdgeFactory<V, E> implements EdgeFactory<V, E> {

	@Override
	public E createEdge(V sourceVertex, V targetVertex) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("rawtypes")
	private final static EdgeFactory INSTANCE = new FailingEdgeFactory();

	public static <N, L> EdgeFactory<N, L> create() {
		@SuppressWarnings("unchecked")
		final EdgeFactory<N, L> instance = INSTANCE;
		return instance;
	}
}
