package org.hisrc.gtfs.onebusaway.model.util;

import java.util.Comparator;

import org.onebusaway.gtfs.model.Stop;

public class StopComparator implements Comparator<Stop> {

	@Override
	public int compare(Stop o1, Stop o2) {
		if (o1 == o2) {
			return 0;
		} else if (o1 == null) {
			return -1;
		} else if (o2 == null) {
			return 1;
		} else {
			return o1.getId().compareTo(o2.getId());
		}
	}

	private static final Comparator<Stop> INSTANCE = new StopComparator();

	public static Comparator<Stop> create() {
		return INSTANCE;
	}

}
