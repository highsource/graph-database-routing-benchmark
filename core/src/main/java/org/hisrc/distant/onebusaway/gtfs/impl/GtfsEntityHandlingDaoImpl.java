package org.hisrc.distant.onebusaway.gtfs.impl;

import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.FareAttribute;
import org.onebusaway.gtfs.model.FareRule;
import org.onebusaway.gtfs.model.FeedInfo;
import org.onebusaway.gtfs.model.Frequency;
import org.onebusaway.gtfs.model.Pathway;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.ShapePoint;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Transfer;
import org.onebusaway.gtfs.model.Trip;

public class GtfsEntityHandlingDaoImpl extends GtfsDaoImpl {

	private final GtfsEntityHandler handler;

	public GtfsEntityHandlingDaoImpl(GtfsEntityHandler handler) {
		super();
		this.handler = handler;
	}

	public void saveEntity(Object entity) {
		super.saveEntity(entity);
		if (entity instanceof Agency) {
			this.handler.handleAgency((Agency) entity);
		} else if (entity instanceof ShapePoint) {
			this.handler.handleShapePoint((ShapePoint) entity);
		} else if (entity instanceof Route) {
			this.handler.handleRoute((Route) entity);
		} else if (entity instanceof Stop) {
			this.handler.handleStop((Stop) entity);
		} else if (entity instanceof Trip) {
			this.handler.handleTrip((Trip) entity);
		} else if (entity instanceof StopTime) {
			this.handler.handleStopTime((StopTime) entity);
		} else if (entity instanceof ServiceCalendar) {
			this.handler.handleServiceCalendar((ServiceCalendar) entity);
		} else if (entity instanceof ServiceCalendarDate) {
			this.handler
					.handleServiceCalendarDate((ServiceCalendarDate) entity);
		} else if (entity instanceof FareAttribute) {
			this.handler.handleFareAttribute((FareAttribute) entity);
		} else if (entity instanceof FareRule) {
			this.handler.handleFareRule((FareRule) entity);
		} else if (entity instanceof Frequency) {
			this.handler.handleFrequency((Frequency) entity);
		} else if (entity instanceof Pathway) {
			this.handler.handlePathway((Pathway) entity);
		} else if (entity instanceof Transfer) {
			this.handler.handleTransfer((Transfer) entity);
		} else if (entity instanceof FeedInfo) {
			this.handler.handleFeedInfo((FeedInfo) entity);
		}
	}
}