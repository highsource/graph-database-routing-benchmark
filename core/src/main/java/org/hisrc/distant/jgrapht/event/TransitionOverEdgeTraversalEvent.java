package org.hisrc.distant.jgrapht.event;

import org.jgrapht.event.EdgeTraversalEvent;

public class TransitionOverEdgeTraversalEvent<V, E, T> extends EdgeTraversalEvent<V, E>{

	private static final long serialVersionUID = 5601416021832092600L;
	
	protected T transition;

	public TransitionOverEdgeTraversalEvent(Object eventSource, E edge,
			T transition) {
		super(eventSource, edge);
		this.transition = transition;
	}
	
	public T getTransition() {
		return transition;
	}
}
