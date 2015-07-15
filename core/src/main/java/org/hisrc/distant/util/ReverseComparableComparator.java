package org.hisrc.distant.util;

import java.util.Comparator;

public class ReverseComparableComparator<C extends Comparable<C>> implements
		Comparator<C> {

	@Override
	public int compare(C o1, C o2) {
		if (o1 == o2) {
			return 0;
		} else if (o1 == null) {
			return 1;
		} else if (o2 == null) {
			return -1;
		} else {
			return o2.compareTo(o1);
		}
	}

	@SuppressWarnings("rawtypes")
	private static Comparator INSTANCE = new ReverseComparableComparator();

	public static <T extends Comparable<T>> Comparator<T> create() {
		@SuppressWarnings("unchecked")
		final Comparator<T> comparator = (Comparator<T>) INSTANCE;
		return comparator;
	}

}
