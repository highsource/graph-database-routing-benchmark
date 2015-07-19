package org.hisrc.distant.jgrapht;

import java.util.List;

import org.jgrapht.GraphPath;

public interface TimeDependentGraphPath<V, E, T> extends GraphPath<V, E> {

	public List<T> getTransitionList();

}
