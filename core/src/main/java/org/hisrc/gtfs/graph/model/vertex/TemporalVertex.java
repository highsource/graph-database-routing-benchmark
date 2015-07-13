package org.hisrc.gtfs.graph.model.vertex;

import org.hisrc.gtfs.graph.model.TimeAware;
import org.onebusaway.gtfs.model.Stop;

public abstract class TemporalVertex implements TimeAware {

	// Reference to the stop
	private final Stop stop;
	// Time point
	private final int time;

	public TemporalVertex(Stop stop, int time) {
		super();
		this.stop = stop;
		this.time = time;
	}

	public Stop getStop() {
		return stop;
	}

	public int getTime() {
		return time;
	}
	
	public abstract boolean isEntryVertex();
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stop == null) ? 0 : stop.hashCode());
		result = prime * result + time;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TemporalVertex other = (TemporalVertex) obj;
		if (stop == null) {
			if (other.stop != null) {
				return false;
			}
		} else if (!stop.equals(other.stop)) {
			return false;
		}
		if (time != other.time) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "(" + stop + "@" + time + ")";
	}
}
