package org.hisrc.distant.jgrapht.traverse;

/**
 * Calculates edge length.
 * 
 * @author Alexey Valikov
 *
 * @param <E>
 *            type of the edge.
 */
public interface LengthFunction<E> {

	/**
	 * Returns an integer length of the edge (for instance in meters).
	 * 
	 * @param edge
	 *            edge to calculate the length of.
	 * @return Length of the edge, -1 for unknwon, 0 for irrelevant.
	 */
	public int apply(E edge);
}
