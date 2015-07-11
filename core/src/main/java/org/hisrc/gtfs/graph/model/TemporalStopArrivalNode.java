package org.hisrc.gtfs.graph.model;

import org.onebusaway.gtfs.model.Stop;

public class TemporalStopArrivalNode extends TemporalStopNode {

	public TemporalStopArrivalNode(Stop stop, int time) {
		super(stop, time);
	}

}
