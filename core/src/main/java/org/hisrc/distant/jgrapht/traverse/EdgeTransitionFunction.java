package org.hisrc.distant.jgrapht.traverse;


public interface EdgeTransitionFunction<E, T> {

	public T apply(E edge, int timepoint);

}
