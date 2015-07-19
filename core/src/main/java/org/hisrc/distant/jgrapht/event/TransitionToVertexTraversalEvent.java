package org.hisrc.distant.jgrapht.event;

import org.jgrapht.event.VertexTraversalEvent;

public class TransitionToVertexTraversalEvent<V, T> extends VertexTraversalEvent<V>{

	private static final long serialVersionUID = 8607805618940222493L;
	
	protected T transition;
	
	public TransitionToVertexTraversalEvent(Object eventSource, V vertex,
			T transition) {
		super(eventSource, vertex);
		this.transition = transition;
	}

	public T getTransition() {
		return transition;
	}

}
