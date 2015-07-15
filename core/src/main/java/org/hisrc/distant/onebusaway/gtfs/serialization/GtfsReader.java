package org.hisrc.distant.onebusaway.gtfs.serialization;

import org.onebusaway.gtfs.model.StopTime;

public class GtfsReader extends org.onebusaway.gtfs.serialization.GtfsReader{
	
	public GtfsReader() {
		super();
		this.getEntityClasses().remove(StopTime.class);
		this.getEntityClasses().add(StopTime.class);
	}

}
