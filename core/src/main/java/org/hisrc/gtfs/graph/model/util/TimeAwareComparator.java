package org.hisrc.gtfs.graph.model.util;

import java.util.Comparator;

import org.hisrc.gtfs.graph.model.TimeAware;

public class TimeAwareComparator<TA extends TimeAware> implements
		Comparator<TA> {

	@Override
	public int compare(TA o1, TA o2) {
		if (o1 == o2) {
			return 0;
		} else if (o1 == null) {
			return -1;
		} else if (o2 == null) {
			return 1;
		} else {
			return o1.getTime() - o2.getTime();
		}
	}

	@SuppressWarnings("rawtypes")
	private final static TimeAwareComparator<?> INSTANCE = new TimeAwareComparator();

	public static <T extends TimeAware> Comparator<T> create() {
		@SuppressWarnings("unchecked")
		final TimeAwareComparator<T> comparator = (TimeAwareComparator<T>) INSTANCE;
		return comparator;
	}

}
